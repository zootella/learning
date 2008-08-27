package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.ApplicationSettings;

/**
 * The menu item that actually displays the options for dynamically
 * showing or hiding tabs.
 */
final class ShowHideMenu extends AbstractMenu {
    

    /**
     * Constructs all of the elements of the <tt>ViewMenu</tt>, in particular
     * the check box menu items and listeners for the various tabs displayed
     * in the main window.
     *
     * @param key the key allowing the <tt>AbstractMenu</tt> superclass to
     *  access the appropriate locale-specific string resources
     */
    ShowHideMenu(final String key) {
        super(key);

        addToggleMenuItem("VIEW_MONITOR",  
                    new ViewListener(GUIMediator.MONITOR_INDEX), 
                    ApplicationSettings.MONITOR_VIEW_ENABLED.getValue());

        addToggleMenuItem("VIEW_CONNECTIONS", 
                    new ViewListener(GUIMediator.CONNECTIONS_INDEX), 
                    ApplicationSettings.CONNECTION_VIEW_ENABLED.getValue());

        addToggleMenuItem("VIEW_LIBRARY", 
                    new ViewListener(GUIMediator.LIBRARY_INDEX), 
                    ApplicationSettings.LIBRARY_VIEW_ENABLED.getValue());
                    
        MENU.add(new SearchMenu("VIEW_SEARCH").getMenu());
    }

	/**
	 * Listener for the checking/unchecking of the menu buttons, making
	 * the associated tabs visible or invisible.
	 */
    private static class ViewListener implements ActionListener {
		
		/**
		 * The fixed index for the associated tab (see GUIMediator tab 
		 * indeces).
		 */
		private final int INDEX;

		/**
		 * Constructs a new <tt>ViewListener</tt> with the specified fixed
		 * index.
		 *
		 * @param INDEX the fixed index for the associated tab
		 */
		private ViewListener(final int INDEX) {
			this.INDEX = INDEX;			
		}

        public void actionPerformed(ActionEvent ae) {
		    AbstractButton button = (AbstractButton)ae.getSource();
			GUIMediator.instance().setTabVisible(INDEX, button.isSelected());
		    GUIMediator.instance().setWindow(INDEX);
        }
    }
}