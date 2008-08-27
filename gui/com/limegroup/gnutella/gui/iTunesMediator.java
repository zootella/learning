package com.limegroup.gnutella.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.ErrorService;
import com.limegroup.gnutella.settings.iTunesSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;
import com.limegroup.gnutella.util.ProcessingQueue;

import de.kapsi.util.OSAException;
import de.kapsi.util.OSAScript;

/**
 * Handles sending completed downloads into iTunes.
 */
public final class iTunesMediator {
    
	private static final Log LOG = LogFactory.getLog(iTunesMediator.class);
        
	private static final iTunesMediator INSTANCE = new iTunesMediator();
    
	/**
     * Various error numbers for things that could go wrong.
     * See:
     * /System/Library/Frameworks/CoreServices.frameworks/
     *   Frameworks/CarbonCore.framework/Headers/MacErrors.h
     */
    private static final int AE_TIMEOUT_ERROR = -1712;
    private static final int AE_REPLY_NOT_ARRIVED_ERROR = -1718;

    /**
     * The data for the script, as read from disk.
     */
    private byte[] theScript = null;
    
    /**
     * The number of errors we've gotten while adding to iTunes.
     */
    private volatile int iTunesErrors = 0;
    
    /**
     * The queue that will process the tunes to add.
     */
    private final ProcessingQueue QUEUE =
        new ProcessingQueue("iTunesAdderThread");
    
    /**
     * The thread that adds tunes.
     */
    private Thread iTunesThread;

    /**
     * Returns the sole instance of this class.
     */
	public static iTunesMediator instance() {
		return INSTANCE;
	}

    /**
     * Initializes iTunesMediator with the script file.
     */
    private iTunesMediator() {
        // we are only dealing with OSX.
        if(CommonUtils.isMacOSX())
            theScript = loadScript();
    }
    
    /**
     * Returns a byte array of the iTunes.scpt file.
     */
    private byte[] loadScript() {
        if(!iTunesSettings.ITUNES_SUPPORT_ENABLED.getValue())
            return null;
    
        BufferedInputStream in = null;        
        try {                
            File file = new File(getRoot(), "iTunes.scpt");
            if(!file.exists() || !file.isFile())
                throw new IOException("iTunes.scpt does not exist");
            byte[] data = new byte[(int)file.length()];
            in = new BufferedInputStream(new FileInputStream(file));            
            if (in.read(data, 0, data.length) != data.length)
                throw new IOException("Couldn't read whole script");
            return data;
        } catch (IOException err) {
            // Notify the user that there was a problem with reading the file.
            // Inform them on how to enable iTunes support again, should
            // they want it.
            GUIMediator.showError("ERROR_ITUNES_SCRIPT");
            iTunesSettings.ITUNES_SUPPORT_ENABLED.setValue(false);
            return null;
        } finally {
            if (in != null) { 
                try {
                    in.close(); 
                    in = null;
                } catch (IOException err) {}
            }
        }
    }
    
    /**
     * Returns the root of where iTunes.scpt will exist.
     */
    private static String getRoot() {
        File test = new File("gpl.txt");
        if(test.exists() && test.isFile())
            return "../lib/native/osx";
        else
            return ".";
    }

    /**
     * If running on OSX, iTunes integration is enabled and the downloaded file
     * is a supported type, send it to iTunes.
     */
    public void handleCompleteDownload(Downloader mgr) {
        // If not on OSX or iTunes support is turned off, don't do anything.
        if (!CommonUtils.isMacOSX() ||
          !iTunesSettings.ITUNES_SUPPORT_ENABLED.getValue())
            return;
            
        // If they just recently turned iTunes support on, make sure
        // the script is loaded.
        if( theScript == null ) {
            theScript = loadScript();
            // If theScript is still null, it couldn't be loaded, so exit.
            // The user has already been notified of how to turn iTunes
            // integration back on and iTunes integration has been turned off.
            if(theScript == null)
                return;
        }

        //File saveDir = SharingSettings.DIRECTORY_FOR_SAVING_FILES.getValue();
        File file = mgr.getSaveFile();
		
        // Verify that we're adding a real file.
		if (!file.exists()) {
            if (LOG.isDebugEnabled())
                LOG.debug("File: '" + file + "' does not exist");
			return;			
		} else if (!file.isFile()) {
            if (LOG.isDebugEnabled())
                LOG.debug("File: '" + file + "' is a directory");
			return;
		}
		
        // Verify that we support this file.
        String name = file.getName().toLowerCase(Locale.US);        
        if (isSupported(name)) {			
			if(LOG.isTraceEnabled())
			    LOG.trace("Will add '" + file + "' to Playlist");
            QUEUE.add(new iTunesAdder(file));
        }
    }
    
    
    /**
     * Returns true if the extension of name is a supported file type.
     */
    private static boolean isSupported(String name) {
        String[] types = iTunesSettings.ITUNES_SUPPORTED_FILE_TYPES.getValue();        
        for(int i = 0; i < types.length; i++)
            if (name.endsWith(types[i]))
                return true;
        return false;
     }
    
    /**
     * Executes the native code to add a file to iTunes.
     */
    private class iTunesAdder implements Runnable {
        /**
         * The file to add.
         */
        private final File file;
        
        /**
         * Constructs a new iTunesAdder for the specified file.
         */
        public iTunesAdder(File f) {
            // Make sure we convert any uppercase to lowercase or vice versa.
            try {
                f = FileUtils.getCanonicalFile(f);
            } catch(IOException ignored) {}
            file = f;
        }
        
        /**
         * Runs the native code to add to iTunes.
         */
        public void run() {
            // iTunes support could have been turned off while we were waiting
            // in the event queue, so if it's off then exit.
            if(!iTunesSettings.ITUNES_SUPPORT_ENABLED.getValue())
                return;
        
            OSAScript script = null;                    
            try {
                script = new OSAScript(theScript);                        
                String[] params = new String[] { file.getAbsolutePath() };
                script.execute("add_to_itunes_library", params);                        
            } catch (OSAException err) {
                if (LOG.isDebugEnabled())
                    LOG.debug("An error occured while adding '" + file +
                              "' to Playlist", err);            
            
                // Act differently depending on the type of error.
                switch(err.getErrorNum()) {
                case AE_TIMEOUT_ERROR:
                case AE_REPLY_NOT_ARRIVED_ERROR:
                    break; // ignore, will go away?
                default:
                    if(iTunesErrors++ <= 5)
                        break; // wait until it happens a lot before erroring.
                        
                    // Turn iTunes integration off and notify about the error.
                    // Do this in two steps so we don't shove two errors at
                    // the user very quickly.
                    if(iTunesErrors == 6) {
                        ErrorService.error(err);
                    } else {
                        GUIMediator.showError("ERROR_ITUNES_INTEGRATION");
                        iTunesSettings.ITUNES_SUPPORT_ENABLED.setValue(false);
                    }
                }                                      
            } catch (UnsatisfiedLinkError err) {
                // Notify the user that something went wrong and how to
                // activate iTunes integration again.
                GUIMediator.showError("ERROR_ITUNES_LINK");
                iTunesSettings.ITUNES_SUPPORT_ENABLED.setValue(false);
            } catch (NoClassDefFoundError err) {
                // Notify the user that something went wrong and how to
                // activate iTunes integration again.
                GUIMediator.showError("ERROR_ITUNES_LINK");
                iTunesSettings.ITUNES_SUPPORT_ENABLED.setValue(false);
            } finally {
                if (script != null) {
                    script.close();
                    script = null;
                }
            }
        }
    }
}
