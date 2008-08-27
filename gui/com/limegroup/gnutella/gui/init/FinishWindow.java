package com.limegroup.gnutella.gui.init;

/**
 * This class displays a window for completing the setup.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
class FinishWindow extends SetupWindow {

	/**
	 * Creates the window and its components.
	 *
	 * @param manager the setup mediator class
	 */
	FinishWindow(SetupManager manager) {
		super(manager, "SETUP_FINISH_TITLE", "SETUP_FINISH_LABEL");
	}
}
