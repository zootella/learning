package com.limegroup.gnutella.gui.playlist;

import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.ButtonRow;

/**
 * The buttons of the playlist.
 */
final class PlaylistButtons {


	/**
	 * The row of buttons for the donwload window.
	 */
	private ButtonRow BUTTONS;

	/**
	 * Index of the load button.
	 */
	static final int LOAD_BUTTON  = 0;

	/**
	 * Index of the save button.
	 */
	static final int SAVE_BUTTON = 1;
	
	/**
	 * Index of the delete button.
	 */
	static final int REMOVE_BUTTON = 2;
	
	/**
	 * The constructor creates the row of buttons with their associated
	 * listeners.
	 */
	PlaylistButtons(final PlaylistMediator pm) {

  		String[] buttonLabelKeys = {
			"PLAYLIST_LOAD_BUTTON_LABEL",
            "PLAYLIST_SAVE_BUTTON_LABEL",
            "PLAYLIST_DELETE_BUTTON_LABEL"
		};
		String[] toolTipKeys = {
			"PLAYLIST_LOAD_BUTTON_TIP",
            "PLAYLIST_SAVE_BUTTON_TIP",
            "PLAYLIST_DELETE_BUTTON_TIP"
		};		
		ActionListener[] listeners = {
            pm.LOAD_LISTENER,
            pm.SAVE_LISTENER,
            pm.REMOVE_LISTENER
		};
		String[] iconNames = {
		    "PLAYLIST_LOAD",
		    "PLAYLIST_SAVE",
		    "PLAYLIST_DELETE"
		};

		BUTTONS = new ButtonRow(buttonLabelKeys, toolTipKeys, listeners,
		                        iconNames, ButtonRow.X_AXIS, ButtonRow.NO_GLUE);
	}
	
	ButtonRow getComponent() { return BUTTONS; }
	
}
