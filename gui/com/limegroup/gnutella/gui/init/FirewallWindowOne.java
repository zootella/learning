package com.limegroup.gnutella.gui.init;

import javax.swing.Box;
import javax.swing.JPanel;

import javax.swing.JLabel;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.BoxPanel;

/**
 * Informs the user that a firewall warning might appear.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class FirewallWindowOne extends SetupWindow {

	/**
	 * Creates the window and its components.
	 */
	FirewallWindowOne(SetupManager manager) {
		super(manager, "SETUP_FIREWALL_TITLE_ONE", "SETUP_FIREWALL_LABEL_ONE");
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

	/**
	 * No-op
	 */
	public void applySettings() throws ApplySettingsException {
	    // no settings to apply.
	}
}



