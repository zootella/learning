package com.limegroup.gnutella.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.limegroup.gnutella.gui.GUIMediator;

public class ConfigureOptionsAction extends AbstractAction {

	/**
	 * The title of the 
	 */
	private String paneTitle;
	
	public ConfigureOptionsAction(String pane, String menu, String tooltip) {
		putValue(Action.NAME, GUIMediator.getStringResource(menu));
		putValue(Action.SHORT_DESCRIPTION,
				GUIMediator.getStringResource(tooltip));
		paneTitle = pane;
	}

	/**
	 * Launches LimeWire's options with the Sharing options pane selected.
	 */
	public void actionPerformed(ActionEvent e) {
		GUIMediator.instance().setOptionsVisible(true, paneTitle);
	}
}
