package com.limegroup.gnutella.gui.connection;

import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.ButtonRow;

/**
 * This class contains the buttons in the connection window, allowing
 * classes in this package to enable or disable buttons at specific
 * indeces in the row.
 */
final class ConnectionButtons {


	/**
	 * The row of buttons for the donwload window.
	 */
	private ButtonRow BUTTONS;

	/**
	 * The index of the add button in the button row.
	 */
	static final int ADD_BUTTON = 0;

	/**
	 * The index of the remove button in the button row.
	 */
	static final int REMOVE_BUTTON = 1;

    /**
     * The index of the browse host button in the button row.
     */
    static final int BROWSE_HOST_BUTTON = 2;

	/**
	 * The constructor creates the row of buttons with their associated
	 * listeners.
	 */
	ConnectionButtons(final ConnectionMediator cm) {
        String[] buttonLabelKeys = {
			"CV_BUTTON_ADD",
			"CV_BUTTON_REMOVE",
			"GENERAL_BROWSE_HOST_LABEL"
		};
        String[] buttonTipKeys = {
			"CV_BUTTON_TOOLTIP_ADD",
			"CV_BUTTON_TOOLTIP_REMOVE",
			"GENERAL_BROWSE_HOST_TIP"
		};

		ActionListener[] buttonListeners = {
			cm.ADD_LISTENER,
			cm.REMOVE_LISTENER,
			cm.BROWSE_HOST_LISTENER
		};
		
		String[] buttonNames = {
		    "CONNECTION_ADD",
		    "CONNECTION_REMOVE",
		    "CONNECTION_BROWSE_HOST"
		};

		BUTTONS = new ButtonRow(buttonLabelKeys, buttonTipKeys, buttonListeners,
		                        buttonNames, ButtonRow.X_AXIS, ButtonRow.NO_GLUE);
	}
	
	ButtonRow getComponent() { return BUTTONS; }
	
}
