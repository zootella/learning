package com.limegroup.gnutella.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.limegroup.gnutella.gui.GUIMediator;

public class ShareNewFolderAction extends AbstractAction {

	public ShareNewFolderAction() {
		putValue(Action.NAME, GUIMediator.getStringResource
				("SHARE_NEW_FOLDER_ACTION_NAME"));
		putValue(Action.SHORT_DESCRIPTION, GUIMediator.getStringResource("SHARE_NEW_FOLDER_ACTION_DESCRIPTION"));
		String mnemonic = GUIMediator.getStringResource("SHARE_NEW_FOLDER_ACTION_MNEMONIC");
		if (mnemonic.length() > 0) {
			putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charAt(0)));
		}
	}

	/**
	 * Prompts for adding a shared library folder.
	 */
	public void actionPerformed(ActionEvent e) {
		GUIMediator.instance().addSharedLibraryFolder();
	}
}
