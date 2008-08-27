package com.limegroup.gnutella.gui.playlist;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.limegroup.gnutella.gui.mp3.PlayList;
import com.limegroup.gnutella.gui.tables.BasicDataLineModel;
import com.limegroup.gnutella.gui.tables.DataLine;


/**
 * A model for playlists.  Keeps of track of what song is next,
 * what has/hasn't been played during shuffling, etc...
 */
public final class PlaylistModel extends BasicDataLineModel {

    /**
     * Whether or not songs are shuffling.
     */
    private boolean _shuffle = false;
    
    /**
     * Indicates that the next song to return is before
     * the current one.
     */
    private boolean _nextIsBefore = false;
    
    /**
     * The currently playing song.
     */
    private File _currentSong;
    
    /**
     * Songs already played -- used for shuffling.
     */
    private List _songsNotPlayed;
        
    /**
     * Constructs a new playlist model.
     */
    PlaylistModel() {
        super( PlaylistDataLine.class );
    }
    
    /**
     * Quickly updates all instead of iterating & calling udpate on each one.
     * @return null
     */
    public Object refresh() {
        fireTableRowsUpdated(0, getRowCount());
        return null;
    }
    
    /**
     * Creates a new ConnectionDataLine
     */
    public DataLine createDataLine() {
        return new PlaylistDataLine();
    }
    
    /**
     * Override default so new ones get added to the end
     */
    public int add(Object o) {
        return add(o, getRowCount());
    }
    
    /**
     * If shuffling, adds to the list of songs not played.
     */
    public int add(DataLine dl, int row) {
        if(_shuffle) {
            _songsNotPlayed.add(dl.getInitializeObject());
            Collections.shuffle(_songsNotPlayed);
        }
        return super.add(dl, row);
    }
    
    /**
     * If shuffling, removes the song from the songs not played.
     */
    public void remove(int i) {
        File f = (File)get(i).getInitializeObject();
        super.remove(i);
        if(_shuffle)
            _songsNotPlayed.remove(f);
    }
    
    /**
     * If shuffling, clears the songs not played.
     */
    public void clear() {    
        super.clear();
        if(_shuffle)
            _songsNotPlayed.clear();
    }
    
    /**
     * Gets the next song to play.
     */
    File getNextSong() {
        int rowCount = getRowCount();
        
        if(rowCount == 0)
            return null;
            
        boolean prior = _nextIsBefore;
        _nextIsBefore = false;

        if(_shuffle) {
            //fill up songs not played if empty
            if(_songsNotPlayed.isEmpty()) {
                for(int i = 0; i < rowCount; i++)
                    _songsNotPlayed.add(get(i).getInitializeObject());
                Collections.shuffle(_songsNotPlayed);
            }
            _currentSong = (File)_songsNotPlayed.remove(0);
        } else {
            int idx = getRow(_currentSong);
            // if we haven't played anything, alway get the first one.
            if(idx == -1)
                idx = 0;
            else if(prior)
                idx = (idx - 1 + rowCount) % rowCount;
            else
                idx = (idx + 1 + rowCount) % rowCount;
            _currentSong = (File)get(idx).getInitializeObject();
        }
        
        return _currentSong;
    }
    
    /**
     * Sets the currently playing song.
     */
    void setCurrentSong(File f) {
        if(_shuffle)
            _songsNotPlayed.remove(f);
        _currentSong = f;
    }
    
    /**
     * Gets the index of the currently playing song.
     */
    int getCurrentSongIndex() {
        return getRow(_currentSong);
    }
    
    /**
     * Returns the list of all songs.
     */
    List getSongs() {
        List l = new LinkedList();
        for(int i = 0; i < getRowCount(); i++) {
            DataLine dl = get(i);
            l.add(dl.getInitializeObject());
        }
        return l;
    }
    
    /**
     * Notification that the next song we want to play is BEFORE the current
     * song.
     */
    void setBackwardsMode() {
        _nextIsBefore = true;
    }
    
    /**
     * Adds all songs from the playlist.
     */
    void addSongs(PlayList list) {
        unsort();
        List songs = list.getSongs();
        for(Iterator i = songs.iterator(); i.hasNext(); )
            add(i.next());
    }
    
    /**
     * Sets whether or not shuffle is active.
     */
    void setShuffle(boolean shuffle) {
        if(shuffle)
            _songsNotPlayed = new LinkedList();
        else
            _songsNotPlayed = null;
        _shuffle = shuffle;
    }
}