package com.limegroup.gnutella.gui.mp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.metadata.MP3Info;

/** 
 * Ecapsulates an mp3 playlist (.m3u).  Thread-safe.
 */
public class PlayList {
    
    private static final Log LOG = LogFactory.getLog(PlayList.class);

    /**
     * Used when reading/writing playlist to backing store.
     */
    private File _file;

    /**
     * Contains the File Objects of the song in the playlist.
     */
    private Vector _songs;
    
    /**
     * Whether or not a a song has changed in the list after the last save.
     */
    private boolean _dirty;

    /**
     * Creates a PlayList accessible from the given filename.
     * If the File 'filename' exists, the playlist is loaded from that file.
     */
    public PlayList(String filename) {
        LOG.trace("PlayList(): entered.");

        _file = new File(filename);
        if (_file.isDirectory())
            throw new IllegalArgumentException(filename + " is a directory");

        _songs = new Vector();
        if (_file.exists()) {
            try {
                loadM3UFile(); // load the playlist entries....
            } catch(IOException ignored) {}
        }
        
        if(LOG.isTraceEnabled()) {
            LOG.trace("songs = " + _songs);
            LOG.trace("returning.  size is now " + getNumSongs());
        }
    }

    private static final String M3U_HEADER = "#EXTM3U";
    private static final String SONG_DELIM = "#EXTINF";
    private static final String SEC_DELIM  = ":";

    /**
     * @exception IOException Thrown if load failed.<p>
     *
     * Format of playlist (.m3u) files is:<br>
     * ----------------------<br>
     * #EXTM3U<br>
     * #EXTINF:numSeconds<br>
     * /path/of/file/1<br>
     * #EXTINF:numSeconds<br>
     * /path/of/file/2<br>
     * ----------------------<br>
     */
    private void loadM3UFile() throws IOException {
        BufferedReader m3uFile = null;
        try {
            m3uFile = new BufferedReader(new FileReader(_file));
            String currLine = null;
            currLine = m3uFile.readLine();
            if (currLine == null || !currLine.startsWith(M3U_HEADER))
                throw new IOException();
            for (currLine = m3uFile.readLine(); currLine != null;
                 currLine = m3uFile.readLine()) {
                if (currLine.startsWith(SONG_DELIM)) {
                    currLine = m3uFile.readLine();
                    if(currLine == null)
                        break;
                    File toAdd = new File(currLine);
                    if (toAdd.exists() && !toAdd.isDirectory())
                        _songs.add(toAdd);
                }
            }
        } finally {
            if(m3uFile != null) {
                try {
                    m3uFile.close();
                } catch(IOException ioe) {}
            }
        }
    }

    /**
     * Call this when you want to save the contents of the playlist.
     * @exception IOException Throw when save failed.
     */
    public synchronized void save() throws IOException {
        if(!_dirty)
            return;
        
        // if all songs are new, just get rid of the old file.  this may
        // happen if a delete was done....
        if (_songs.size() == 0) {
            if (_file.exists())
                _file.delete();
            return;
        }

        boolean fileExists = _file.exists();
        PrintWriter m3uFile = null;
        try {
            m3uFile = new PrintWriter(
                        new FileWriter(_file.getCanonicalPath(), false)
                      );

            if (!fileExists) {
                m3uFile.write(M3U_HEADER);
                m3uFile.println();
            }
            
            for(Iterator i = _songs.iterator(); i.hasNext(); ) {
                File currFile = (File)i.next();
                // first line of song description...
                m3uFile.write(SONG_DELIM);
                m3uFile.write(SEC_DELIM);
                // try to write out seconds info....
                try {
                    MP3Info currMP3 = new MP3Info(currFile.getCanonicalPath());
                    m3uFile.write("" + currMP3.getLengthInSeconds() + ",");
                } catch (IOException ignored) {
                    // didn't work, just write a placeholder
                    m3uFile.write("-1,");
                }
                m3uFile.write(currFile.getName());
                m3uFile.println();
                // canonical path follows...
                m3uFile.write(currFile.getCanonicalPath());
                m3uFile.println();
            }
        } finally {
            _dirty = false;
            if(m3uFile != null) {
                m3uFile.close();
            }
        }
    }

    /**
     * Get the total number of songs in current playlist, including those
     * that were recently added.
     */
    public int getNumSongs() {
        return _songs.size();
    }

    /**
     * Deletes a song from the playlist.
     */
    public void deleteSong(int index) {
        _dirty = true;
        _songs.remove(index);
    }

    /**
     * Adds a song to the playlist.
     */
    public void addSong(File newEntry, int idx) {
        _dirty = true;
        _songs.add(idx, newEntry);
    }
    
    /**
     * Gets all songs in the list.
     */
    public synchronized List getSongs() {
        return new LinkedList(_songs);
    }
    
    /**
     * Sets all the active songs.
     */
    public synchronized void setSongs(List l) {
        _dirty = true;
        _songs.clear();
        _songs.addAll(l);
    }

    /**
     * Get a reference to the File at the indicated index in the playlist.
     */
    public File getSong(int index) {
        return (File)_songs.get(index);
    }
}