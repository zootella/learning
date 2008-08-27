package com.limegroup.gnutella.gui.upload;

import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.ButtonRow;

/**
 * This class contains the buttons in the download window, allowing
 * classes in this package to enable or disable buttons at specific
 * indeces in the row.
 */
final class UploadButtons {


	/**
	 * The row of buttons for the donwload window.
	 */
	private ButtonRow BUTTONS;

	/**
	 * The index of the kill button in the button row.
	 */
	static final int KILL_BUTTON   = 0;
	
	/**
	 * The index of the browse button in the button row.
	 */
	static final int BROWSE_BUTTON = 1;

	/**
	 * The index of the clear button in the button row.
	 */
	static final int CLEAR_BUTTON  = 2;
	
	/**
	 * The constructor creates the row of buttons with their associated
	 * listeners.
	 */
	UploadButtons(final UploadMediator um) {
        String[] buttonLabelKeys = {
			"UPLOAD_KILL_BUTTON_LABEL",
			"GENERAL_BROWSE_HOST_LABEL",
			"UPLOAD_CLEAR_BUTTON_LABEL"
		};
        String[] buttonTipKeys = {
			"UPLOAD_KILL_BUTTON_TIP",
			"GENERAL_BROWSE_HOST_TIP",
			"UPLOAD_CLEAR_BUTTON_TIP"
		};

		ActionListener[] buttonListeners = {
			um.REMOVE_LISTENER,
			um.BROWSE_LISTENER,
			um.CLEAR_LISTENER
		};
		
		String[] iconNames =  {
		    "UPLOAD_REMOVE",
		    "UPLOAD_BROWSE_HOST",
		    "UPLOAD_CLEAR"
		};				

		BUTTONS = new ButtonRow(buttonLabelKeys,buttonTipKeys,buttonListeners,
		                        iconNames, ButtonRow.X_AXIS, ButtonRow.NO_GLUE);
	}
	
	ButtonRow getComponent() { return BUTTONS; }
	
}
