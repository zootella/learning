package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Contains all of the menu items for the tools menu.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class ToolsMenu extends AbstractMenu {

	/**
	 * Creates a new <tt>ToolsMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	ToolsMenu(final String key) {
		super(key);
		ActionListener optionsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    GUIMediator.instance().setOptionsVisible(true);
			}
		};

		ActionListener statsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    GUIMediator.instance().setStatisticsVisible(true);
			}
		};

        if(!CommonUtils.isMacOSX()) {
		    addMenuItem("TOOLS_OPTIONS", optionsListener);
		}
		addMenuItem("TOOLS_STATISTICS", statsListener);
	}
}
