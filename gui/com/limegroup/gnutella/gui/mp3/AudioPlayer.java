package com.limegroup.gnutella.gui.mp3;

import java.io.File;
import java.io.IOException;

import com.limegroup.gnutella.gui.RefreshListener;

/**
 * This interface defines the required functionality of an audio player
 * component.
 */
public interface AudioPlayer extends RefreshListener {

    /**
     * Constant for the playing state.
     */
	static final int STATUS_PLAYING = 0;
	
	/**
     * Constant for the paused state.
     */
	static final int STATUS_PAUSED  = 1;
	
	/**
     * Constant for the stopped state.
     */
	static final int STATUS_STOPPED = 2;

    
    /**
     * Returns the current state of the player.
     *
     * @return the state of the player -- one of STATUS_PLAYING, STATUS_PAUSED,
     *  STATUS_STOPPED
     */
    int getStatus();

    /**
     * Unpauses the player.
     */
    void unpause();
    
    /**
     * Pauses the player.
     */
    void pause();
    
    /**
     * Stops the player.
     */
    void stop();
    
    /**
     * Plays the specified file.
     *
     * @param file the <tt>File</tt> instance denoting the abstract pathname of 
     * the file to play
     * @throws IOException Thrown if the playing encountered difficulties, ie
     * couldn't find file, etc.
     */
    boolean play(File file) throws IOException;
    
    /**
     * Returns the frame seek position.
     * 
     * @return the seek position of the seek bar
     */
    int getFrameSeek();
    
    /**
     * Adds a listener to the list of player listeners.
     */
    void addAudioPlayerListener(AudioPlayerListener listener);
}
