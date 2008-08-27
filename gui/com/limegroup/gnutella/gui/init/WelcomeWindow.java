package com.limegroup.gnutella.gui.init;

/**
 * this class displays information welcoming the user to the
 * setup wizard.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class WelcomeWindow extends SetupWindow {
    
	/**
	 * Creates the window and its components
	 */
	WelcomeWindow(SetupManager manager, boolean partial) {
		super(manager, "SETUP_WELCOME_TITLE", partial ?
		    "SETUP_WELCOME_PARTIAL_LABEL" : "SETUP_WELCOME_LABEL");
	}
}
