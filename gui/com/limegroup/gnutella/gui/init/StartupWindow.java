package com.limegroup.gnutella.gui.init;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.WindowsUtils;
import com.limegroup.gnutella.settings.StartupSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.MacOSXUtils;

/**
 * This class displays a window to the user allowing them to specify
 * whether or not LimeWire should be run on system startup.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class StartupWindow extends SetupWindow {

	/**
	 * The checkbox that determines whether or not to start on startup.
	 */
	private JCheckBox _startup;

	/**
	 * Creates the window and its components.
	 */
	StartupWindow(SetupManager manager) {
		super(manager, "SETUP_STARTUP_TITLE", "SETUP_STARTUP_LABEL");
    }
    
    protected void createWindow() {
        super.createWindow();

		JPanel mainPanel = new BoxPanel(BoxLayout.X_AXIS);
		_startup = new JCheckBox(
            GUIMediator.getStringResource("SETUP_STARTUP_CHECKBOX"));
        _startup.setSelected(true);
		mainPanel.add(_startup);
		mainPanel.add(Box.createHorizontalGlue());
		addSetupComponent(mainPanel);
	}

	/**
	 * Overrides applySettings in SetupWindow superclass.
	 * Applies the settings handled in this window.
	 */
	public void applySettings() throws ApplySettingsException {
	    boolean allow = _startup.isSelected();
	    
	    if(CommonUtils.isMacOSX()) {
            MacOSXUtils.setLoginStatus(allow);
	    } else if(WindowsUtils.isLoginStatusAvailable()) {
	        WindowsUtils.setLoginStatus(allow);
	    }
	    
	    StartupSettings.RUN_ON_STARTUP.setValue(allow);
	}
}



