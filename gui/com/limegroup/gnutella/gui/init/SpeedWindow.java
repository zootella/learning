package com.limegroup.gnutella.gui.init;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.SpeedConstants;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIConstants;
import com.limegroup.gnutella.settings.ConnectionSettings;
import com.limegroup.gnutella.settings.DownloadSettings;

/**
 * This class displays a window to the user allowing them to specify
 * their connection speed.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class SpeedWindow extends SetupWindow {

	/**
	 * The four buttons that represent the speeds, and their button group.
	 */
	private ButtonGroup _speedGroup;
	private JRadioButton _modem;
	private JRadioButton _cable;
	private JRadioButton _t1;
	private JRadioButton _t3;

	/**
	 * Creates the window and its components.
	 */
	SpeedWindow(SetupManager manager) {
		super(manager, "SETUP_SPEED_TITLE", "SETUP_SPEED_LABEL");
    }
    
    protected void createWindow() {
        super.createWindow();

		JPanel mainPanel = new BoxPanel(BoxLayout.X_AXIS);
		JPanel buttonPanel = new BoxPanel(BoxLayout.Y_AXIS);
		_speedGroup = new ButtonGroup();
		_modem = new JRadioButton(GUIConstants.MODEM_SPEED);
		_cable = new JRadioButton(GUIConstants.CABLE_SPEED);
		_t1 = new JRadioButton(GUIConstants.T1_SPEED);
		_t3 = new JRadioButton(GUIConstants.T3_SPEED);
		_cable.setSelected(true);
		_speedGroup.add(_modem);
		_speedGroup.add(_cable);
		_speedGroup.add(_t1);
		_speedGroup.add(_t3);
  		buttonPanel.add(_modem);
		buttonPanel.add(_cable);
		buttonPanel.add(_t1);
		buttonPanel.add(_t3);
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createHorizontalGlue());
		addSetupComponent(mainPanel);
	}

	/**
	 * Overrides applySettings in SetupWindow superclass.
	 * Applies the settings handled in this window.
	 */
	public void applySettings() throws ApplySettingsException {
		int speed = getSpeed();
		setDownloadSlots(speed);
        
        if (speed < SpeedConstants.MIN_SPEED_INT || SpeedConstants.MAX_SPEED_INT < speed) {
            throw (new IllegalArgumentException());
        }
        
        ConnectionSettings.CONNECTION_SPEED.setValue(speed);
	}

	/**
	 * Returns the selected speed value.  If no speed was selected, 
	 * it returns the MODEM_SPEED.
	 *
	 * @return the selected speed value.  If no speed was selected, 
	 * it returns the MODEM_SPEED
	 */
	private int getSpeed() {
		if (_cable.isSelected())
			return SpeedConstants.CABLE_SPEED_INT;
		else if (_t1.isSelected())
			return SpeedConstants.T1_SPEED_INT;
		else if (_t3.isSelected())
			return SpeedConstants.T3_SPEED_INT;
		else
			return SpeedConstants.MODEM_SPEED_INT;
	}

	/**
	 * Sets the number of download slots based on the connection
	 * speed the user entered.
	 * 
	 * @param speed the speed of the connection to use for setting
	 *  the download slots
	 */
	private void setDownloadSlots(int speed) {
        
		if(speed == SpeedConstants.MODEM_SPEED_INT) {
			DownloadSettings.MAX_SIM_DOWNLOAD.setValue(3);
		}
		else if(speed == SpeedConstants.CABLE_SPEED_INT) {
			DownloadSettings.MAX_SIM_DOWNLOAD.setValue(8);			
		}
		else if(speed == SpeedConstants.T1_SPEED_INT) {
			DownloadSettings.MAX_SIM_DOWNLOAD.setValue(12);
		}
		else if(speed == SpeedConstants.T3_SPEED_INT) {
			DownloadSettings.MAX_SIM_DOWNLOAD.setValue(14);
		}

		else {
			DownloadSettings.MAX_SIM_DOWNLOAD.setValue(3);
		}
	}
}



