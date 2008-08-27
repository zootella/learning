package com.limegroup.gnutella.gui.menu;

import javax.swing.SwingUtilities;

import com.apple.mrj.MRJOSType;
import com.apple.mrj.jdirect.Linker;
import com.apple.mrj.macos.carbon.CarbonLock;
import com.apple.mrj.macos.frameworks.ApplicationServices;
import com.apple.mrj.macos.frameworks.Carbon;
import com.limegroup.gnutella.gui.AEEventHandlerClosureUPP;
import com.limegroup.gnutella.gui.AEEventHandlerInterface;
import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This class handles the Mac OS X-specific code for handling the "Preferences"
 * menu item.  This includes acquiring and releasing the Carbon locks, registering
 * the event handler for the preferences event, and defining a handler for that
 * event.  On OS X, this class is responsible for the "Preferences..." menu item
 * listed in the application menu on the screen menu bar.
 */
final class PreferencesItem implements ApplicationServices, Carbon, 
	AEEventHandlerInterface {
	
	/**
	 * Code for the Apple event <tt>MRJOSType</tt> for registering the
	 * preferences event.
	 */
	private static final int kAppleEventClass = new MRJOSType("aevt").toInt();
	
	/**
	 * Code for the preferences event <tt>MRJOSType</tt> for registering the
	 * preferences event.
	 */
	 private static final int kAEPreferencesID = new MRJOSType("pref").toInt();
    
    /**
     * Linker for tying everything together.
     */
	private static final Object linker = new Linker(PreferencesItem.class);

    
    /**
     * Temporarily acquires the native Carbon lock to install the event handler
     * for the preferences event.
     */
	public void register() {
		try {
			CarbonLock.acquire();
			int err = 
				AEInstallEventHandler(kAppleEventClass, 
				                      kAEPreferencesID,
				                      new AEEventHandlerClosureUPP(this).getProc(), 
				                      0, 
				                      false);
			if(err != 0) {
				java.lang.System.err.println("Preferences handled stup failed: "+err);
			}
		} finally {
			CarbonLock.release();
		}
	}
	
	/**
	 * Sets the enabled state of the preferences menu item.
	 * 
	 * @param enabled the state to set the preferences item to
	 */
	public void setEnabled(boolean enabled) {
		try {
			CarbonLock.acquire();
			if(enabled) {
				EnableMenuCommand(0, kAEPreferencesID);
			} else {
				DisableMenuCommand(0, kAEPreferencesID);
			}
		} finally {
			CarbonLock.release();
		}
		
	}
	
	public short AEEventHandler(int event, int reply, int refcon) {
		Runnable prefRunnable = new Runnable() {
			public void run() {
				GUIMediator.instance().setOptionsVisible(true);
			} 
		};
		SwingUtilities.invokeLater(prefRunnable);
		return 0;
	}
	
	/**
	 * Native method that installs the event handler.
	 */
	private static native short AEInstallEventHandler(int eventClass, int id,
		int handler, int refcon, boolean sysHandler);
	
	/**
	 * Enables the menu command specified by the menu index and the command id 
	 *
	 * @param menu the index of the menu to affect
	 * @param commandID the id of the menu item to enable
	 */
	private static native void EnableMenuCommand(int menu, int commandID);
	
	/**
	 * Disables the menu command specified by the menu index and the command id 
	 *
	 * @param menu the index of the menu to affect
	 * @param commandID the id of the menu item to disable
	 */
	private static native void DisableMenuCommand(int menu, int commandID);
}
