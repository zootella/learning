package com.limegroup.gnutella.gui.library;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** 
 * This class responds to a explore request by 
 * opening the selected (or first) Library folder.
 */
public final class ExploreListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
        LibraryMediator.launchExplorer();
	}
}
