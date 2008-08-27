package com.limegroup.gnutella.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.IncompleteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.filters.IPFilter;
import com.limegroup.gnutella.settings.DaapSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;
import com.limegroup.gnutella.util.ManagedThread;
import com.limegroup.gnutella.util.NetworkUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLReplyCollection;
import com.limegroup.gnutella.xml.SchemaReplyCollectionMapper;

import de.kapsi.net.daap.DaapAuthenticator;
import de.kapsi.net.daap.DaapConfig;
import de.kapsi.net.daap.DaapFilter;
import de.kapsi.net.daap.DaapServer;
import de.kapsi.net.daap.DaapServerFactory;
import de.kapsi.net.daap.DaapStreamSource;
import de.kapsi.net.daap.DaapThreadFactory;
import de.kapsi.net.daap.DaapUtil;
import de.kapsi.net.daap.Database;
import de.kapsi.net.daap.Library;
import de.kapsi.net.daap.Playlist;
import de.kapsi.net.daap.Song;
import de.kapsi.net.daap.Transaction;
import de.kapsi.net.daap.TransactionListener;

/**
 * This class handles the mDNS registration and acts as an
 * interface between LimeWire and DAAP.
 */
public final class DaapManager implements FinalizeListener {
    
    private static final Log LOG = LogFactory.getLog(DaapManager.class);
    private static final DaapManager INSTANCE = new DaapManager();
    
    private static final String AUDIO_SCHEMA = "http://www.limewire.com/schemas/audio.xsd";
    
    public static DaapManager instance() {
        return INSTANCE;
    }

    private SongURNMap map;
    
    private Library library;
    private Database database;
    private Playlist whatsNew;
    private Playlist creativecommons;
    private DaapServer server;
    private RendezvousService rendezvous;
    
    private boolean enabled = false;
    private int maxPlaylistSize;
    
    private DaapManager() {
        if (CommonUtils.isJava14OrLater() == false)
            throw new RuntimeException("Cannot instance DaapManager");
        
        GUIMediator.addFinalizeListener(this);
    }
    
    /**
     * Initializes the Library
     */
    public synchronized void init() {
        
        if (isServerRunning()) {
            setEnabled(enabled);
        }
    }
    
    /**
     * Starts the DAAP Server
     */
    public synchronized void start() throws IOException {
        
        if (!isServerRunning()) {
            
            try {
                
                InetAddress addr = InetAddress.getLocalHost();
                
                if (addr.isLoopbackAddress() || !(addr instanceof Inet4Address)) {
                    addr = null;
                    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                    if (interfaces != null) {
                        while(addr == null && interfaces.hasMoreElements()) {
                            NetworkInterface nif = (NetworkInterface)interfaces.nextElement();
                            Enumeration addresses = nif.getInetAddresses();
                            while(addresses.hasMoreElements()) {
                                InetAddress address = (InetAddress)addresses.nextElement();
                                if (!address.isLoopbackAddress() 
                                        && address instanceof Inet4Address) {
                                    addr = address;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                if (addr == null) {
                    stop();
                    // No valid IP address -- just ignore, since
                    // it's probably the user isn't connected to the
                    // internet.  Next time they start, it might work.
                    return;
                }
                
                rendezvous = new RendezvousService(addr);
                
                map = new SongURNMap();
                
                maxPlaylistSize = DaapSettings.DAAP_MAX_LIBRARY_SIZE.getValue();
                
                String name = DaapSettings.DAAP_LIBRARY_NAME.getValue();
                int revisions = DaapSettings.DAAP_LIBRARY_REVISIONS.getValue();
                boolean useLibraryGC = DaapSettings.DAAP_LIBRARY_GC.getValue();
                library = new Library(name, revisions, useLibraryGC);
                
                database = new Database(name);
                whatsNew = new Playlist(GUIMediator.getStringResource("SEARCH_TYPE_WHATSNEW"));
                creativecommons = new Playlist(GUIMediator.getStringResource("LICENSE_CC"));
                
                Transaction txn = library.open(false);
                library.add(txn, database);
                database.add(txn, creativecommons);
                database.add(txn, whatsNew);
                creativecommons.setSmartPlaylist(txn, true);
                whatsNew.setSmartPlaylist(txn, true);
                txn.commit();
                
                LimeConfig config = new LimeConfig(addr);
                
                final boolean NIO = DaapSettings.DAAP_USE_NIO.getValue();

                server = DaapServerFactory.createServer(library, config, NIO);

                server.setAuthenticator(new LimeAuthenticator());
                server.setStreamSource(new LimeStreamSource());
                server.setFilter(new LimeFilter());
                
                if (!NIO) {
                    server.setThreadFactory(new LimeThreadFactory());
                }
                
                final int maxAttempts = 10;
                
                for(int i = 0; i < maxAttempts; i++) {
                    try {
                        server.bind();
                        break;
                    } catch (BindException bindErr) {
                        if (i < (maxAttempts-1)) {
                            // try next port...
                            config.nextPort();
                        } else {
                            throw bindErr;
                        }
                    }
                }
                
                Thread serverThread = new ManagedThread(server, "DaapServerThread") {
                    protected void managedRun() {
                        try {
                            super.managedRun();
                        } catch (Throwable t) {
                            DaapManager.this.stop();
                            if(!handleError(t)) {
                                GUIMediator.showError("ERROR_DAAP_RUN_ERROR");
                                DaapSettings.DAAP_ENABLED.setValue(false);
                                if(t instanceof RuntimeException)
                                    throw (RuntimeException)t;
								throw new RuntimeException(t);
                            }
                        }
                    }
                };
                
                serverThread.setDaemon(true);
                serverThread.start();
                
                rendezvous.registerService();
                
            } catch (IOException err) {
                stop();
                throw err;
            }
        }
    }
    
    /**
     * Stops the DAAP Server and releases all resources
     */
    public synchronized void stop() {
        
        if (rendezvous != null)
            rendezvous.close();
        
		if (server != null) {
			server.stop();
			server = null;
		}
        
        if (map != null)
            map.clear();
        
        rendezvous = null;

        map = null;
        library = null;
        whatsNew = null;
        creativecommons = null;
        database = null;
    }
    
    /**
     * Restarts the DAAP server and re-registers it via mDNS.
     * This is equivalent to:<p>
     *
     * <code>
     * stop();
     * start();
     * init();
     * </code>
     */
    public synchronized void restart() throws IOException {
        if (isServerRunning())
            stop();
    
        start();
        init();
    }
    
    /**
     * Shutdown the DAAP service properly. In this case
     * is the main focus on mDNS (Rendezvous) as in
     * some rare cases iTunes doesn't recognize that
     * LimeWire/DAAP is no longer online.
     */
    public void doFinalize() {
        stop();
    }
    
    /**
     * Updates the multicast-DNS servive info
     */
    public synchronized void updateService() throws IOException {
        
        if (isServerRunning()) {
            rendezvous.updateService();

            Transaction txn = library.open(false);
            String name = DaapSettings.DAAP_LIBRARY_NAME.getValue();
            library.setName(txn, name);
            database.setName(txn, name);
            txn.commit();
            server.update();
        }
    }
    
    /**
     * Disconnects all clients
     */
    public synchronized void disconnectAll() {
        if (isServerRunning()) {
            server.disconnectAll();
        }
    }
    
    /**
     * Returns <tt>true</tt> if server is running
     */
    public synchronized boolean isServerRunning() {
        if (server != null) {
            return server.isRunning();
        }
        return false;
    }
    
    /**
     * Attempts to handle an exception.
     * Returns true if we could handle it correctly.
     */
    private boolean handleError(Throwable t) {
        if(t == null)
            return false;
            
        String msg = t.getMessage();
        if(msg == null || msg.indexOf("Unable to establish loopback connection") == -1)
            return handleError(t.getCause());
        
        // Problem with XP SP2. -- Loopback connections are disallowed.
        // Why?  Who knows.  This patch fixes it:
        // http://support.microsoft.com/default.aspx?kbid=884020
        if(CommonUtils.isWindowsXP()) {
            int answer = GUIMediator.showYesNoCancelMessage("ERROR_DAAP_LOOPBACK_FAILED");
            switch(answer) {
            case GUIMediator.YES_OPTION:
                GUIMediator.openURL("http://support.microsoft.com/default.aspx?kbid=884020");
                break;
            case GUIMediator.NO_OPTION:
                DaapSettings.DAAP_ENABLED.setValue(false);
                break;
            }
        } else {
            // Also a problem on non XP systems with firewalls.
            int answer = GUIMediator.showYesNoMessage("ERROR_DAAP_LOOPBACK_FAILED_NONXP");
            if(answer == GUIMediator.NO_OPTION)
                DaapSettings.DAAP_ENABLED.setValue(false);
        }
        
        return true;
    }
        
    
    /**
     * Returns true if the extension of name is a supported file type.
     */
    private static boolean isSupportedFormat(String name) {
        String[] types = DaapSettings.DAAP_SUPPORTED_FILE_TYPES.getValue();
        for(int i = 0; i < types.length; i++) {
            if (name.endsWith(types[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handles a change event.
     */
    private void handleChangeEvent(FileManagerEvent evt) {
        FileDesc oldDesc = evt.getFileDescs()[0];        
        Song song = map.remove(oldDesc.getSHA1Urn());
        
        if (song != null) {            
            FileDesc newDesc = evt.getFileDescs()[1];
            map.put(song, newDesc.getSHA1Urn());
            
            // Any changes in the meta data?
            if (updateSongMeta(song, newDesc)) {
                Transaction txn = library.open(true);
                txn.addTransactionListener(new ServerUpdater(server));
                database.update(txn, song);
            }
        }
    }
    
    /**
     * Handles an add event.
     */
    private void handleAddEvent(FileManagerEvent evt) {        
        if (database.getMasterPlaylist().size() >= maxPlaylistSize)
            return;
        
        FileDesc file = evt.getFileDescs()[0];
        if (!(file instanceof IncompleteFileDesc)) {
            String name = file.getFileName().toLowerCase(Locale.US);
            if (isSupportedFormat(name)) {                
                Song song = createSong(file);
                map.put(song, file.getSHA1Urn());
                
                Transaction txn = library.open(true);
                txn.addTransactionListener(new ServerUpdater(server));
                
                database.getMasterPlaylist().add(txn, song);
                whatsNew.add(txn, song);
                
                if (file.isLicensed()) {
                    creativecommons.add(txn, song);
                }
            }
        }
    }
    
    /**
     * Handles a rename event.
     */
    private void handleRenameEvent(FileManagerEvent evt) {
        FileDesc oldDesc = evt.getFileDescs()[0];
        Song song = map.remove(oldDesc.getSHA1Urn());
        
        if (song != null) {
            FileDesc newDesc = evt.getFileDescs()[1];
            map.put(song, newDesc.getSHA1Urn());
        }
    }
    
    /**
     * Handles a remove event.
     */
    private void handleRemoveEvent(FileManagerEvent evt) {
        FileDesc file = evt.getFileDescs()[0];
        Song song = map.remove(file.getSHA1Urn());
        
        if (song != null) {
            Transaction txn = library.open(true);
            txn.addTransactionListener(new ServerUpdater(server));
            database.remove(txn, song);
        }
    }
    
    
    /**
     * Called by VisualConnectionCallback
     */
    public synchronized void handleFileManagerEvent(FileManagerEvent evt) {
        if (!enabled || !isServerRunning())
            return;
              
        if (evt.isChangeEvent())
            handleChangeEvent(evt);
        else if (evt.isAddEvent())
            handleAddEvent(evt);
        else if (evt.isRenameEvent())
            handleRenameEvent(evt);
        else if (evt.isRemoveEvent())
            handleRemoveEvent(evt);
    }
    
    /**
     * Called by VisualConnectionCallback/MetaFileManager.
     */
    public void fileManagerLoading() {
        setEnabled(false);
    }
    
    /**
     * Called by VisualConnectionCallback/MetaFileManager.
     */
    public void fileManagerLoaded() {
        setEnabled(true);
    }
    
    public synchronized boolean isEnabled() {
        return enabled;
    }
    
    private synchronized void setEnabled(boolean enabled) {
        
        this.enabled = enabled;
        //System.out.println("setEnabled: " + enabled);
        
        if (!enabled || !isServerRunning())
            return;
        
        int size = database.getMasterPlaylist().size();        
        Transaction txn = library.open(false);        
        SongURNMap tmpMap = new SongURNMap();        
        FileDesc[] files = RouterService.getFileManager().getAllSharedFileDescriptors();
        
        for(int i = 0; i < files.length; i++) {
            FileDesc file = files[i];
            if(file instanceof IncompleteFileDesc)
                continue;
            
            String name = file.getFileName().toLowerCase(Locale.US);
            if(!isSupportedFormat(name))
                continue;

            URN urn = file.getSHA1Urn();
            
            // 1)
            // _Remove_ URN from the current 'map'...
            Song song = map.remove(urn);
                
            // Check if URN is already in the tmpMap.
            // If so do nothing as we don't want add 
            // the same file multible times...
            if(tmpMap.contains(urn))
                continue;
            
            // This URN was already mapped with a Song.
            // Save the Song (again) and update the meta
            // data if necessary
            if (song != null) {
                tmpMap.put(song, urn);

                // Any changes in the meta data?
                if ( updateSongMeta(song, file) )
                    database.update(txn, song);
            } else if (size < maxPlaylistSize){
                // URN was unknown and we must create a
                // new Song for this URN...
                song = createSong(file);
                tmpMap.put(song, urn);
                database.getMasterPlaylist().add(txn, song);
                
                if (file.isLicensed()) {
                    creativecommons.add(txn, song);
                }
                
                size++;
            }
        }
        
        // See 1)
        // As all known URNs were removed from 'map' only
        // deleted FileDesc URNs can be leftover! We must 
        // remove the associated Songs from the Library now
        Iterator it = map.getSongIterator();
        while(it.hasNext()) {
            Song song = (Song)it.next();
            database.remove(txn, song);
        }
        
        map.clear();
        map = tmpMap; // tempMap is the new 'map'
        
        txn.addTransactionListener(new ServerUpdater(server));
        txn.commit();
    }
    
    /**
     * Create a Song and sets its meta data with
     * the data which is retrieved from the FileDesc
     */
    private Song createSong(FileDesc desc) {
        
        Song song = new Song(desc.getFileName());
        song.setSize((int)desc.getFileSize());
        song.setDateAdded((int)(System.currentTimeMillis()/1000));
        
        File file = desc.getFile();
        String ext = FileUtils.getFileExtension(file);
        
        if (ext != null) {
            
            // Note: This is required for formats other than MP3
            // For example AAC (.m4a) files won't play if no
            // format is set. As far as I can tell from the iTunes
            // 'Get Info' dialog are Songs assumed as MP3 until
            // a format is set explicit.
            
            song.setFormat(ext.toLowerCase(Locale.US));
            
            updateSongMeta(song, desc);
        }
        
        return song;
    }
    
    /**
     * Sets the meta data
     */
    private boolean updateSongMeta(Song song, FileDesc desc) {
        
        SchemaReplyCollectionMapper map = SchemaReplyCollectionMapper.instance();
        LimeXMLReplyCollection collection = map.getReplyCollection(AUDIO_SCHEMA);
        
        if (collection == null) {
            LOG.error("LimeXMLReplyCollection is null");
            return false;
        }
        
        LimeXMLDocument doc = collection.getDocForHash(desc.getSHA1Urn());
        
        if (doc == null)
            return false;
        
        boolean update = false;
        
        String title = doc.getValue("audios__audio__title__");
        String track = doc.getValue("audios__audio__track__");
        String artist = doc.getValue("audios__audio__artist__");
        String album = doc.getValue("audios__audio__album__");
        String genre = doc.getValue("audios__audio__genre__");
        String bitrate = doc.getValue("audios__audio__bitrate__");
        String comments = doc.getValue("audios__audio__comments__");
        String time = doc.getValue("audios__audio__seconds__");
        String year = doc.getValue("audios__audio__year__");
        
        if (title != null) {
            String currentTitle = song.getName();
            if (currentTitle == null || !title.equals(currentTitle)) {
                update = true;
                song.setName(title);
            }
        }
        
        int currentTrack = song.getTrackNumber();
        if (track != null) {
            try {
                int num = Integer.parseInt(track);
                if (num > 0 && num != currentTrack) {
                    update = true;
                    song.setTrackNumber(num);
                }
            } catch (NumberFormatException err) {}
        } else if (currentTrack != 0) {
            update = true;
            song.setTrackNumber(0);
        }
        
        String currentArtist = song.getArtist();
        if (artist != null) {
            if (currentArtist == null || !artist.equals(currentArtist)) {
                update = true;
                song.setArtist(artist);
            }
        } else if (currentArtist != null) {
            update = true;
            song.setArtist(null);
        }
        
        String currentAlbum = song.getAlbum();
        if (album != null) {
            if (currentAlbum == null || !album.equals(currentAlbum)) {
                update = true;
                song.setAlbum(album);
            }
        } else if (currentAlbum != null) {
            update = true;
            song.setAlbum(null);
        }
        
        String currentGenre = song.getGenre();
        if (genre != null) {
            if (currentGenre == null || !genre.equals(currentGenre)) {
                update = true;
                song.setGenre(genre);
            }
        } else if (currentGenre != null) {
            update = true;
            song.setGenre(null);
        }
        
        String currentComments = song.getComment();
        if (comments != null) {
            if (currentComments == null || !comments.equals(currentComments)) {
                update = true;
                song.setComment(comments);
            }
        } else if (currentComments != null) {
            update = true;
            song.setComment(null);
        }
        
        int currentBitrate = song.getBitrate();
        if (bitrate != null) {
            try {
                int num = Integer.parseInt(bitrate);
                if (num > 0 && num != currentBitrate) {
                    update = true;
                    song.setBitrate(num);
                }
            } catch (NumberFormatException err) {}
        } else if (currentBitrate != 0) {
            update = true;
            song.setBitrate(0);
        }
        
        int currentTime = song.getTime();
        if (time != null) {
            try {
                // iTunes expects the song length in milliseconds
                int num = (int)Integer.parseInt(time)*1000;
                if (num > 0 && num != currentTime) {
                    update = true;
                    song.setTime(num);
                }
            } catch (NumberFormatException err) {}
        } else if (currentTime != 0) {
            update = true;
            song.setTime(0);
        }
        
        int currentYear = song.getYear();
        if (year != null) {
            try {
                int num = Integer.parseInt(year);
                if (num > 0 && num != currentYear) {
                    update = true;
                    song.setYear(num);
                }
            } catch (NumberFormatException err) {}
        } else if (currentYear != 0) {
            update = true;
            song.setYear(0);
        }
        
        // iTunes expects the date/time in seconds
        int mod = (int)(desc.lastModified()/1000);
        if (song.getDateModified() != mod) {
            update = true;
            song.setDateModified(mod);
        }

        return update;
    }
    
    /**
     * This factory creates ManagedThreads for the DAAP server
     */
    private final class LimeThreadFactory implements DaapThreadFactory {
               
        public Thread createDaapThread(Runnable runner, String name) {
            Thread thread = new ManagedThread(runner, name);
            thread.setDaemon(true);
            return thread;
        }
    }
    
    /**
     * Handles the audio stream
     */
    private final class LimeStreamSource implements DaapStreamSource {
        
        public FileInputStream getSource(Song song) throws IOException {
            URN urn = map.get(song);
            
            if (urn != null) {
                FileDesc fileDesc = RouterService.getFileManager().getFileDescForUrn(urn);
                if(fileDesc != null)
                    return new FileInputStream(fileDesc.getFile());
            }
            
            return null;
        }
    }
    
    /**
     * Implements the DaapAuthenticator
     */
    private final class LimeAuthenticator implements DaapAuthenticator {
        
        public boolean requiresAuthentication() {
            return DaapSettings.DAAP_REQUIRES_PASSWORD.getValue();
        }
        
        /**
         * Returns true if username and password are correct.<p>
         * Note: iTunes does not support usernames (i.e. it's
         * don't care)!
         */
        public boolean authenticate(String username, String password) {
            return password.equals(DaapSettings.DAAP_PASSWORD.getValue());
        }
    }
    
    /**
     * The DAAP Library should be only accessable from the LAN
     * as we can not guarantee for the required bandwidth and it
     * could be used to bypass Gnutella etc. Note: iTunes can't
     * connect to DAAP Libraries outside of the LAN but certain
     * iTunes download tools can.
     */
    private final class LimeFilter implements DaapFilter {

        /**
         * Returns true if <tt>address</tt> is a private address
         */
        public boolean accept(InetAddress address) {
            
            byte[] addr = address.getAddress();
            try {
                // not private & not close, not allowed.
                if(!NetworkUtils.isVeryCloseIP(addr) &&
                   !NetworkUtils.isPrivateAddress(addr))
                    return false;
            } catch (IllegalArgumentException err) {
                LOG.error(err);
                return false;
            }
            
            // Is it a annoying fellow? >:-)
            return IPFilter.instance().allow(addr);
        }
    }
    
    /**
     * A LimeWire specific implementation of DaapConfig
     */
    private final class LimeConfig implements DaapConfig {
        
        private InetAddress addr;
        
        public LimeConfig(InetAddress addr) {
            this.addr = addr;
            
            // Reset PORT to default value to prevent increasing
            // it to infinity
            DaapSettings.DAAP_PORT.revertToDefault();
        }
        
        public String getServerName() {
            return CommonUtils.getHttpServer();
        }
        
        public void nextPort() {
            int port = DaapSettings.DAAP_PORT.getValue();
            DaapSettings.DAAP_PORT.setValue(port+1);
        }
        
        public int getBacklog() {
            return 0;
        }
        
        public InetSocketAddress getInetSocketAddress() {
            int port = DaapSettings.DAAP_PORT.getValue();
            return new InetSocketAddress(addr, port);
        }
        
        public int getMaxConnections() {
            return DaapSettings.DAAP_MAX_CONNECTIONS.getValue();
        }
    }
    
    /**
     * Helps us to publicize and update the DAAP Service via
     * multicast-DNS (aka Rendezvous or Zeroconf)
     */
    private final class RendezvousService {
        
        private static final String VERSION = "Version";
        private static final String MACHINE_NAME = "Machine Name";
        private static final String PASSWORD = "Password";
        
        private final JmDNS zeroConf;
        private ServiceInfo service;
        
        public RendezvousService(InetAddress addr) throws IOException {
            zeroConf = new JmDNS(addr);
        }
        
        public boolean isRegistered() {
            return (service != null);
        }
        
        private ServiceInfo createServiceInfo() {
            
            String type = DaapSettings.DAAP_TYPE_NAME.getValue();
            String name = DaapSettings.DAAP_SERVICE_NAME.getValue();
            
            int port = DaapSettings.DAAP_PORT.getValue();
            int weight = DaapSettings.DAAP_WEIGHT.getValue();
            int priority = DaapSettings.DAAP_PRIORITY.getValue();
            
            boolean password = DaapSettings.DAAP_REQUIRES_PASSWORD.getValue();
            
            java.util.Hashtable props = new java.util.Hashtable();
            
            // Greys the share and the playlist names when iTunes's
            // protocol version is different from this version. It's
            // only a nice visual effect and has no impact to the
            // ability to connect this server! Disabled because 
            // iTunes 4.2 is still widespread...
            props.put(VERSION, Integer.toString(DaapUtil.VERSION_3));
            
            // This is the inital share name
            props.put(MACHINE_NAME, name);
            
            // shows the small lock if Service is protected
            // by a password!
            props.put(PASSWORD, Boolean.toString(password)); 
            
            String qualifiedName = null;
            
            // This isn't really required but as iTunes
            // does it in this way I'm doing it too...
            if (password) {
                qualifiedName = name + "_PW." + type;
            } else {
                qualifiedName = name + "." + type;
            }
            
            ServiceInfo serviceInfo = new ServiceInfo(type, qualifiedName, port, 
                                                     weight, priority, props);
            
            return serviceInfo;
        }
        
        public void registerService() throws IOException {
            
            if (isRegistered())
                throw new IOException();
            
            ServiceInfo serviceInfo = createServiceInfo();
            zeroConf.registerService(serviceInfo);
            this.service = serviceInfo;
        }
        
        public void unregisterService() {
            if (!isRegistered())
                return;
            
            zeroConf.unregisterService(service);
            service = null;
        }
        
        public void updateService() throws IOException {
            if (!isRegistered())
                throw new IOException();
            
            if (service.getPort() != DaapSettings.DAAP_PORT.getValue())
                unregisterService();
            
            ServiceInfo serviceInfo = createServiceInfo();
            zeroConf.registerService(serviceInfo);
            
            this.service = serviceInfo;
        }
        
        public void close() {
            unregisterService();
            zeroConf.close();
        }
    }
    
    /**
     * A simple wrapper for a two way mapping as we have to
     * deal in both directions with FileManager and DaapServer
     * <p>
     * Song -> URN
     * URN -> Song
     */
    private final class SongURNMap {
        
        private HashMap /* Song -> URN */ songToUrn = new HashMap();
        private HashMap /* URN -> Song */ urnToSong = new HashMap();
        
        public SongURNMap() {
        }
        
        public void put(Song song, URN urn) {
            songToUrn.put(song, urn);
            urnToSong.put(urn, song);
        }
        
        public URN get(Song song) {
            return (URN)songToUrn.get(song);
        }
        
        public Song get(URN urn) {
            return (Song)urnToSong.get(urn);
        }
        
        public Song remove(URN urn) {
            Song song = (Song)urnToSong.remove(urn);
            if (song != null)
                songToUrn.remove(song);
            return song;
        }
        
        public URN remove(Song song) {
            URN urn = (URN)songToUrn.remove(song);
            if (urn != null)
                urnToSong.remove(urn);
            return urn;
        }
        
        public boolean contains(URN urn) {
            return urnToSong.containsKey(urn);
        }
        
        public boolean contains(Song song) {
            return songToUrn.containsKey(song);
        }
        
        public Iterator getSongIterator() {
            return songToUrn.keySet().iterator();
        }
        
        public Iterator getURNIterator() {
            return urnToSong.keySet().iterator();
        }
        
        public void clear() {
            urnToSong.clear();
            songToUrn.clear();
        }
        
        public int size() {
            // NOTE: songToUrn.size() == urnToSong.size()
            return songToUrn.size();
        }
    }
    
    private static class ServerUpdater implements TransactionListener {
        private DaapServer server;
        	
        private ServerUpdater(DaapServer server) {
            this.server = server;
        }
        
        public void commit(Transaction arg0) {
            if (server != null) {
                server.update();
            }
        }
        
        public void rollback(Transaction arg0) {
        }
    }
}
