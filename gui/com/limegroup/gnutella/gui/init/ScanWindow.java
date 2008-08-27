package com.limegroup.gnutella.gui.init;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This class handles the display of the window that prompts the user
 * for whether or not they would like their hard drive to be scanned.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class ScanWindow extends SetupWindow {

	private final ButtonGroup BUTTON_GROUP = new ButtonGroup();
	private final JRadioButton YES_BUTTON = 
		new JRadioButton(GUIMediator.getStringResource("YES"));
	private final JRadioButton NO_BUTTON = 
		new JRadioButton(GUIMediator.getStringResource("NO"));

	private SetupWindow _yesWindow;
	private SetupWindow _noWindow;
	
	/**
	 * Creates the window and its components
	 *
	 * @param manager the setup mediator class
	 */
	ScanWindow(SetupManager manager) {
		super(manager, "SETUP_SCAN_TITLE", "SETUP_SCAN_LABEL");
    }
    
    protected void createWindow() {
        super.createWindow();

		JPanel mainPanel   = new BoxPanel(BoxLayout.X_AXIS);
		JPanel buttonPanel = new BoxPanel(BoxLayout.Y_AXIS);
		BUTTON_GROUP.add(YES_BUTTON);
		BUTTON_GROUP.add(NO_BUTTON);
		YES_BUTTON.setSelected(true);
		buttonPanel.add(YES_BUTTON);
		buttonPanel.add(NO_BUTTON);
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createHorizontalGlue());
		addSetupComponent(mainPanel);
	}

	/**
	 * sets the next window in the case when the user selects
	 * the "yes" option.
	 */
	public void setYesWindow(SetupWindow window) {
		_yesWindow = window;
	}

	/**
	 * sets the next window in the case when the user selects
	 * the "no" option.
	 */
	public void setNoWindow(SetupWindow window) {
		_noWindow = window;
	}

	/**
	 * overrides getNext() in SetupWindow
	 * returns the next window in the setup sequence.
	 */	
	public SetupWindow getNext() {
		if(YES_BUTTON.isSelected()) return _yesWindow;

		return _noWindow;	
	}
}
