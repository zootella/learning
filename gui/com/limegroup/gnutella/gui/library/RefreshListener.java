package com.limegroup.gnutella.gui.library;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.limegroup.gnutella.RouterService;

/** 
 * This class responds to a refresh event by notifying the 
 * <tt>LibraryMediator</tt> that the library elements should be reloaded.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class RefreshListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
		RouterService.getFileManager().loadSettings();
	}
}
