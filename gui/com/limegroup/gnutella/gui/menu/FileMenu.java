package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Handles all of the contents of the file menu in the menu bar.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class FileMenu extends AbstractMenu {
	
	/**
	 * Creates a new <tt>FileMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	FileMenu(final String key) {
		super(key);
		MENU.add(getMenuItem("MENU_FILE_CONNECT", 
				new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.instance().connect();
			}
		}));				
		MENU.add(getMenuItem("MENU_FILE_DISCONNECT",	
				new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.instance().disconnect();
			}
		}));	
		if(!CommonUtils.isMacOSX()) {
			MENU.addSeparator(); 
			MENU.add(getMenuItem("MENU_FILE_CLOSE", 
					new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIMediator.close(false);
				}
			}));
		}
		setConnected(RouterService.isConnected() || RouterService.isConnecting());
	}

	/**
	 * Returns a new <tt>JMenuItem</tt> instance with all of the characteristics 
	 * specified in the arguments.
	 *
	 * @param key the key for obtaining locale-specific strings for both the
	 *  label and the accessible description of the menu item -- the key for the
	 *  accessible description is obtained by appending "_ACCESSIBLE" to the
	 *  end of the key for the label
	 * @param listener the <tt>ActionListener</tt> to use to respond to clicks 
	 *  on the menu item
	 * @return the new <tt>JMenuItem</tt> with the customized characteristics
	 *  specified in the arguments
	 */
	private JMenuItem getMenuItem(final String key, ActionListener listener) {
		String label = GUIMediator.getStringResource(key);
		String accessibleLabel = 
            GUIMediator.getStringResource(key + "_ACCESSIBLE");
		int mnemonic = getCodeForCharKey(key+"_MNEMONIC");
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.getAccessibleContext().setAccessibleDescription(accessibleLabel);
		menuItem.addActionListener(listener);
		menuItem.setFont(AbstractMenu.FONT);
		return menuItem;
	}
	
	/**
	 * Sets whether or not we are currently connected or disconnected 
	 * from the network, enabling or disabling the correct menu items.
	 *
	 * @param connected specifies our connection status
	 */
	void setConnected(boolean connected) {
        MENU.getItem(0).setEnabled(!connected);
		MENU.getItem(1).setEnabled(connected);
	}
}
















