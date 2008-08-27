package com.limegroup.gnutella.gui.init;

import javax.swing.Box;
import javax.swing.JPanel;

import javax.swing.JLabel;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.UPnPManager;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.settings.ConnectionSettings;

/**
 * Informs the user that a firewall warning might appear & makes it appear.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class FirewallWindowTwo extends SetupWindow {

	/**
	 * Creates the window and its components.
	 */
	FirewallWindowTwo(SetupManager manager) {
		super(manager, "SETUP_FIREWALL_TITLE_TWO", "SETUP_FIREWALL_LABEL_TWO");
    }
    
    protected void createWindow() {
        super.createWindow();
        
        JPanel panel = new BoxPanel(BoxPanel.X_AXIS);
        
        JLabel label = new JLabel(GUIMediator.getThemeImage("firewall_warning"));
        panel.add(Box.createHorizontalGlue());
        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        
        JPanel outer = new BoxPanel(BoxPanel.Y_AXIS);
        outer.add(Box.createVerticalGlue());
        outer.add(panel);
        
        addSetupComponent(outer);
	}
	
	public void handleWindowOpeningEvent() {
	    super.handleWindowOpeningEvent();
	    
        if (CommonUtils.isJava14OrLater() && !ConnectionSettings.DISABLE_UPNP.getValue())
            UPnPManager.instance().start();

        RouterService.asyncGuiInit();
    }

	/**
	 * No-op
	 */
	public void applySettings() throws ApplySettingsException {
	    // no settings to apply.
	}
}



