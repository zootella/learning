package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * Contains all of the menu items for the navigation menu.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class NavMenu extends AbstractMenu {
    /**
     * The ActionListener for navigating to a window.
     */
    private static final ActionListener NAV_LISTENER =
        new NavigationListener();
        
    /**
     * The property that indicates which index to surf to.
     */
    private static final String NAV_PROPERTY = "NAV_INDEX";

	/**
	 * Creates a new <tt>NavMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	NavMenu(final String key) {
		super(key);
		JMenuItem item;

		item = addMenuItem("NAV_SEARCH", NAV_LISTENER);
        item.putClientProperty(NAV_PROPERTY, 
            new Integer(GUIMediator.SEARCH_INDEX));
            
		item = addMenuItem("NAV_MONITOR", NAV_LISTENER);
		item.putClientProperty(NAV_PROPERTY,
		    new Integer(GUIMediator.MONITOR_INDEX));
		    
		item = addMenuItem("NAV_CONN", NAV_LISTENER);
		item.putClientProperty(NAV_PROPERTY,
		    new Integer(GUIMediator.CONNECTIONS_INDEX));
		    
		item = addMenuItem("NAV_LIB", NAV_LISTENER);
		item.putClientProperty(NAV_PROPERTY,
		    new Integer(GUIMediator.LIBRARY_INDEX));
	}

	
	/**
	 * Sets the enabled/disabled state of the navigation menu item
	 * at the specified index.
	 *
	 * @param TAB_INDEX the index of the item to set
	 * @param ENABLED the enabled or disabled state of the item
	 */
	public void setNavMenuItemEnabled(final int TAB_INDEX, 
									  final boolean ENABLED) {
		MENU.getItem(TAB_INDEX).setEnabled(ENABLED);
	}
	
	/**
	 * Listener for navigating to tabs from the menu.
	 */
	private static class NavigationListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            int idx =
                ((Integer)item.getClientProperty(NAV_PROPERTY)).intValue();
            GUIMediator.instance().setWindow(idx);
        }
    }
}


















