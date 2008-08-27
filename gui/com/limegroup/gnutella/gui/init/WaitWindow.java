package com.limegroup.gnutella.gui.init;

import javax.swing.SwingUtilities;

import com.limegroup.gnutella.util.ManagedThread;

/**
 * This class displays the window that is displayed when the user is
 * waiting for a hard drive scan to complete.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class WaitWindow extends SetupWindow {
	
	/**
	 * Constructs the superclass with specialized string for this window.
	 *
	 * @param manager the setup mediator class
	 */
	WaitWindow(SetupManager manager) {
		super(manager, "SETUP_WAIT_TITLE", "SETUP_WAIT_LABEL");
	} 

	/**
	 * Overrides handleWindowOpeningEvent() in SetupWindow.
	 * Changes the buttons displayed to the user.
	 */
	public void handleWindowOpeningEvent() {
	    super.handleWindowOpeningEvent();

		_manager.goToCancelButtons();
		Thread scanThread = new ManagedThread("Scanning HardDrive") {
			public void managedRun() {
				_manager.scan();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						_manager.next();
					}
				});
			}
		};
		scanThread.setDaemon(true);
		scanThread.start();
	}
}
