package com.limegroup.gnutella.gui.menu;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Locale;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Provides a skeletal implementation of the <tt>Menu</tt> interface to 
 * minimize the necessary work in classes that extend <tt>AbstractMenu</tt>.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
abstract class AbstractMenu implements Menu {

	/**
	 * The font menus should use.
	 */
	static final Font FONT = new Font("Dialog", Font.PLAIN, 11);    

	/**
	 * Constant handle to the <tt>JMenu</tt> instance for this 
	 * <tt>AbstractMenu</tt>.
	 */
	protected final JMenu MENU = new JMenu();

	/**
	 * Cache the <tt>KeyEvent</tt> class to avoid looking it up every time.
	 */
	private static Class _keyEventClass = null;

	/**
	 * Creates a new <tt>AbstractMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	protected AbstractMenu(final String key) {
		String titleKey = "MENU_" + key + "_TITLE";
		String titleMnemonicKey = titleKey + "_MNEMONIC";
		MENU.setText(GUIMediator.getStringResource(titleKey));
		if(_keyEventClass == null) {
			try {
				_keyEventClass = 
				    Class.forName("java.awt.event.KeyEvent");
			} catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		MENU.setMnemonic(getCodeForCharKey(titleMnemonicKey));
		MENU.setFont(FONT);
	}

	/**
	 * Returns the <tt>JMenu</tt> instance for this <tt>AbstractMenu</tt>.
	 * 
	 * @return the <tt>JMenu</tt> instance for this <tt>AbstractMenu</tt>	
	 */
	public JMenu getMenu() {
		return MENU;
	}

	/**
	 * Adds a menu item to this menu, using the <tt>key</tt> argument
	 * to obtain locale-specific resources.
	 *
	 * @param key the key to use for obtaining locale-specific resources
	 * @param listener the <tt>ActionListener</tt> to add to this menu 
	 *                 item
	 */
	protected JMenuItem addMenuItem(final String key,
							   final ActionListener listener) {
		String labelKey    = "MENU_" + key;
		String accessKey   = labelKey + "_ACCESSIBLE";
		String mnemonicKey = labelKey + "_MNEMONIC";
		String label  = GUIMediator.getStringResource(labelKey);
		String access = GUIMediator.getStringResource(accessKey);
		int mnemonic  = getCodeForCharKey(mnemonicKey);
		JMenuItem menuItem = new JMenuItem(label, mnemonic);
		menuItem.getAccessibleContext().setAccessibleDescription(access);
		menuItem.addActionListener(listener);
        menuItem.setFont(FONT);
		MENU.add(menuItem);
		return menuItem;
	}
	
    /**
     * Adds a menu item to this menu.  If the current platform is OSX,
     * the menu will be of the type JRadioButtonMenuItem, otherwise
     * it will be of the type JCheckboxMenuItem.
     * This method is used to get around the JCheckBoxMenuItem bug when 
     * using the Mac screen menu bar.
	 *
	 * @param key the key to use for obtaining locale-specific resources
	 * @param listener the <tt>ActionListener</tt> to add to this menu 
	 *                 item
	 * @param enabled whether or not the toggle is enabled by default
	 */
	protected JMenuItem addToggleMenuItem(final String key, 
								  final ActionListener listener,
								  boolean enabled) {
		String labelKey    = "MENU_" + key;
		String accessKey   = "MENU_" + key + "_ACCESSIBLE";
		String mnemonicKey = "MENU_" + key + "_MNEMONIC";
		String label  = GUIMediator.getStringResource(labelKey);
        String access = GUIMediator.getStringResource(accessKey);
        int mnemonic  = getCodeForCharKey(mnemonicKey);
		JMenuItem menuItem;
		if( CommonUtils.isMacOSX() )
		    menuItem = new JRadioButtonMenuItem(label, enabled);
        else
            menuItem = new JCheckBoxMenuItem(label, enabled);
        menuItem.setMnemonic(mnemonic);
	    menuItem.getAccessibleContext().setAccessibleDescription(access);
		menuItem.addActionListener(listener);
        menuItem.setFont(FONT);
		MENU.add(menuItem);
        return menuItem;
	}

	/**
	 * Adds a separator to the <tt>JMenu</tt> instance.
	 */
	protected void addSeparator() {
		MENU.addSeparator();
	}

	/**
	 * Using a little reflection here for a lack of any better way 
	 * to access locale-specific char codes for menu mnemonics.
	 * We could at least defer this in the future.
	 *
	 * @param str the key for the locale-specific char resource to
	 *  look up -- the key as it appears in the locale-specific
	 *  properties file
	 * @return the code for the passed-in key as defined in 
	 *  <tt>java.awt.event.KeyEvent</tt>, or -1 if no key code
	 *  could be found
	 */
	protected int getCodeForCharKey(String str) {
		int charCode = -1;
		String charStr = 
		    GUIMediator.getStringResource(str).toUpperCase(Locale.US);
		if(charStr.length()>1) return -1;
		try {
			Field charField = _keyEventClass.getField("VK_"+charStr);
			charCode = charField.getInt(_keyEventClass);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return charCode;
	}
}
