package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * Contains all of the menu items for the resources menu.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class ResourcesMenu extends AbstractMenu {

	/**
	 * Creates a new <tt>ResourcesMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	ResourcesMenu(final String key) {
		super(key);
		ActionListener proListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.openURL("http://www.limewire.com/index.jsp/pro");
			}
		};

		addMenuItem("RESOURCES_PRO", proListener);
	}
}
