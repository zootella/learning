package com.limegroup.gnutella.gui.mp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class outlines the basic functionality of an audio
 * player component.  It supplies the ability for subclasses to 
 * register listeners and fire events for audio playback.
 */
abstract class AbstractAudioPlayer implements AudioPlayer {

	/**
     * <tt>List</tt> of listeners for audio player events.
     */
	private List _listeners;
	
	public abstract int getStatus();
	
	public abstract void unpause();
	
	public abstract void pause();
	
	public abstract void stop();
	
	public abstract boolean play(File file) throws IOException;
	
	public abstract int getFrameSeek();
	
	public abstract void refresh();
	
	/**
	 * Adds a new <tt>AudioPlayerListener</tt> to the list of registered
	 * listeners for AudioPlayer events.
	 *
	 * @param listener the new <tt>AudioPlayerListener</tt>
	 */
	public void addAudioPlayerListener(AudioPlayerListener listener) {
		if(_listeners == null) _listeners = new ArrayList();
		_listeners.add(listener);
	}
	
	/**
	 * Notifies all registered <tt>AudioPlayerListener</tt> classes that
	 * the position of the audio playback has been updated.
	 *
	 * @param pos the new position of the audio player playback
	 */
	protected void fireAudioPositionUpdated(int pos) {
		for(int i=0; i<_listeners.size(); i++) {
			((AudioPlayerListener)_listeners.get(i)).updateAudioPosition(pos);
		}
	}
	
	/**
	 * Notifies all registered <tt>AudioPlayerListener</tt> classes that
	 * the number of frames should be set for playback.
	 *
	 * @param numberOfFrames the number of frames in the new audio file about
	 *  to be played
	 */
	protected void fireSeekSetupRequired(int numberOfFrames) {
		for(int i=0; i<_listeners.size(); i++) {
			((AudioPlayerListener)_listeners.get(i)).setUpSeek(numberOfFrames);
		}
	}
	
	/**
	 * Notifies all registered <tt>AudioPlayerListener</tt> classes that
	 * the playback of the current audio file has completed.
	 */
	protected void firePlayComplete() {
		for(int i=0; i<_listeners.size(); i++) {
			((AudioPlayerListener)_listeners.get(i)).playComplete();
		}
	}
}
