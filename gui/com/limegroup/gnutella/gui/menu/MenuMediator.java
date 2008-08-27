package com.limegroup.gnutella.gui.menu;

import javax.swing.JMenuBar;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This class acts as a mediator among all of the various items of the 
 * application's menus.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class MenuMediator {

	/**
	 * Constant handle to the instance of this class for following 
	 * the singleton pattern.
	 */
	private static final MenuMediator INSTANCE = new MenuMediator();

	/**
	 * Constant handle to the <tt>JMenuBar</tt> instance that holds all
	 * of the <tt>JMenu</tt> instances.
	 */
	private final JMenuBar MENU_BAR = new JMenuBar();

	/**
	 * Constant handle to the single <tt>FileMenu</tt> instance for
	 * the application.
	 */
	private final FileMenu FILE_MENU = new FileMenu("FILE");

	/**
	 * Constant handle to the single <tt>NavMenu</tt> instance for
	 * the application.
	 */
	private final NavMenu NAV_MENU = new NavMenu("NAV");

	/**
	 * Constant handle to the single <tt>ResourcesMenu</tt> instance for
	 * the application.
	 */
	private final Menu RESOURCES_MENU = new ResourcesMenu("RESOURCES");

	/**
	 * Constant handle to the single <tt>ToolsMenu</tt> instance for
	 * the application.
	 */
	private final Menu TOOLS_MENU = new ToolsMenu("TOOLS");

	/**
	 * Constant handle to the single <tt>HelpMenu</tt> instance for
	 * the application.
	 */
	private final Menu HELP_MENU = new HelpMenu("HELP");

	/**
	 * Constant handle to the single <tt>ViewMenu</tt> instance for
	 * the application.
	 */
	private final Menu VIEW_MENU = new ViewMenu("VIEW");
	
	/**
	 * Singleton accessor method for obtaining the <tt>MenuMediator</tt>
	 * instance.
	 *
	 * @return the <tt>MenuMediator</tt> instance
	 */
	public static final MenuMediator instance() {
		return INSTANCE;
	}

	/**
	 * Private constructor that ensures that a <tt>MenuMediator</tt> 
	 * cannot be constructed from outside this class.  It adds all of 
	 * the menus.
	 */
	private MenuMediator() {
		GUIMediator.setSplashScreenString(
		    GUIMediator.getStringResource("SPLASH_STATUS_MENUS"));
        
        MENU_BAR.setFont(AbstractMenu.FONT);

		addMenu(FILE_MENU);
		addMenu(VIEW_MENU);
		addMenu(NAV_MENU);
		addMenu(RESOURCES_MENU);
		addMenu(TOOLS_MENU);
		addMenu(HELP_MENU);
	}

	/**
	 * Returns the <tt>JMenuBar</tt> for the application.
	 *
	 * @return the application's <tt>JMenuBar</tt> instance
	 */
	public JMenuBar getMenuBar() {
		return MENU_BAR;
	}

	/**
	 * Sets whether or not we are currently connected or disconnected 
	 * from the network, enabling or disabling the correct
	 * connect/disconnect menu items in the file menu.
	 * 
	 * @param connected specifies our connection status
	 */
	public void setConnected(boolean connected) {
		FILE_MENU.setConnected(connected);
	}


	/**
	 * Adds a <tt>Menu</tt> to the next position on the menu bar.
	 *
	 * @param menu to the <tt>Menu</tt> instance that allows access to 
	 *             its wrapped <tt>JMenu</tt> instance
	 */
	private void addMenu(Menu menu) {
		MENU_BAR.add(menu.getMenu());
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
		NAV_MENU.setNavMenuItemEnabled(TAB_INDEX, ENABLED);
	}	

	/**
	 * Returns the height of the main menu bar.
	 *
	 * @return the height of the main menu bar
	 */
	public int getMenuBarHeight() {
		return MENU_BAR.getHeight();
	}
}















