package com.limegroup.gnutella.gui.search;

import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.ButtonRow;

/**
 * This class contains the buttons in the download window, allowing
 * classes in this package to enable or disable buttons at specific
 * indeces in the row.
 */
final class SearchButtons {
		
	/**
	 * The row of buttons for the donwload window.
	 */
	private final ButtonRow BUTTONS;

	/**
	 * The index of the WishList / Download Button.
	 */
	static final int DOWNLOAD_BUTTON_INDEX = 0;

	/**
	 * The index of the browse host button in the button row.
	 */
	static final int BROWSE_BUTTON_INDEX  = 1;
	
	/**
	 * The index of the stop button in the button row.
	 */
	static final int STOP_BUTTON_INDEX = 2;

	/**
	 * The constructor creates the row of buttons with their associated
	 * listeners.
	 */
    SearchButtons(ResultPanel rp) {
        String[] buttonLabelKeys = {
			"SEARCH_DOWNLOAD_BUTTON_LABEL",
            "SEARCH_BROWSE_HOST_BUTTON_LABEL",
            "SEARCH_STOP_BUTTON_LABEL"
		};
        String[] buttonTipKeys = {
			"SEARCH_DOWNLOAD_BUTTON_TIP",
            "SEARCH_BROWSE_HOST_BUTTON_TIP",
            "SEARCH_STOP_BUTTON_TIP"
		};

		ActionListener[] buttonListeners = {
		    rp.DOWNLOAD_LISTENER,
		    rp.BROWSE_HOST_LISTENER,
		    rp.STOP_LISTENER
		};
		
		String[] iconNames =  {
		    "SEARCH_DOWNLOAD",
		    "SEARCH_BROWSE_HOST",
		    "SEARCH_STOP"
		};

		BUTTONS = new ButtonRow(buttonLabelKeys,buttonTipKeys,buttonListeners, iconNames);
	}

	/**
	 * Returns the <tt>Component</tt> instance containing all of the buttons.
	 *
	 * @return the <tt>Component</tt> instance containing all of the buttons
	 */
	ButtonRow getComponent() {
		return BUTTONS;
	}
}
