package com.limegroup.gnutella.gui.mp3;

/**
 * This interface responds to audio player events.
 */
public interface AudioPlayerListener {

	/**
	 * play_complete
	 * - signifies when a song has finished playing
	 */
	public void playComplete();
	
	/**
	 * Notification that the time position of the audio file
	 * has been updated.
	 * 
	 * @param value the new position of the audio file in its
	 *  playback
	 */
	public void updateAudioPosition(int value);
	
    /**
	 * setUpSeek
	 *
	 * - called before playing a song
	 * - retrieves the mp3 size in frames
	 * - sets the maximum slider value
	 *
	 */
	public void setUpSeek(int lengthInFrames);
}
