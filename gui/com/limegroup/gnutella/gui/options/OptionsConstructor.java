package com.limegroup.gnutella.gui.options;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.settings.SettingsHandler;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class constructs the options tree on the left side of the options dialog.
 * <p>
 * The panes that show up when a leaf in the tree is selected are created
 * lazily in {@link OptionsPaneFactory}.
 * <p>
 * If you want to add a new {@link OptionsPane}, 
 * add a call to {@link #addOption(String, String)} in the constructor here
 * and add the construction of the pane to 
 * {@link OptionsPaneFactory#createOptionsPane(String)}.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class OptionsConstructor {
	/**
	 * Handle to the top-level <tt>JDialog</tt window that contains all
	 * of the other GUI components.
	 */
	private final JDialog DIALOG;

	/**
	 * Constant for the default width of the options window.
	 */
	private final int OPTIONS_WIDTH = 600;

	/**
	 * Constant for the default height of the options window.
	 */
	private final int OPTIONS_HEIGHT = 460;

	/**
	 * Stored for convenience to allow using this in helper methods
	 * during construction.
	 */
	private final OptionsTreeManager TREE_MANAGER;
	
	/**
	 * Stored for convenience to allow using this in helper methods
	 * during construction.
	 */
	private final OptionsPaneManager PANE_MANAGER;

	static final String SAVE_KEY           = "OPTIONS_SAVE_MAIN_TITLE";
	static final String SHARED_KEY         = "OPTIONS_SHARED_MAIN_TITLE";
	static final String SPEED_KEY          = "OPTIONS_SPEED_MAIN_TITLE";
	static final String DOWNLOAD_KEY       = "OPTIONS_DOWNLOAD_MAIN_TITLE";
	static final String UPLOAD_KEY         = "OPTIONS_UPLOAD_MAIN_TITLE";
	static final String UPLOAD_BASIC_KEY   = "OPTIONS_UPLOAD_BASIC_MAIN_TITLE";
	static final String UPLOAD_SLOTS_KEY   = "OPTIONS_UPLOAD_SLOTS_MAIN_TITLE";
	static final String CONNECTIONS_KEY    = "OPTIONS_CONNECTIONS_MAIN_TITLE";
	static final String SHUTDOWN_KEY       = "OPTIONS_SHUTDOWN_MAIN_TITLE";
	static final String UPDATE_KEY         = "OPTIONS_UPDATE_MAIN_TITLE";
	static final String CHAT_KEY           = "OPTIONS_CHAT_MAIN_TITLE";
	static final String PLAYER_KEY         = "OPTIONS_PLAYER_MAIN_TITLE";
    static final String STATUS_BAR_KEY     = "OPTIONS_STATUS_BAR_MAIN_TITLE";
    static final String ITUNES_KEY		   = "OPTIONS_ITUNES_MAIN_TITLE";
    static final String ITUNES_IMPORT_KEY  = "OPTIONS_ITUNES_PREFERENCE_MAIN_TITLE";
    static final String ITUNES_DAAP_KEY    = "OPTIONS_ITUNES_DAAP_MAIN_TITLE";
	static final String POPUPS_KEY         = "OPTIONS_POPUPS_MAIN_TITLE";
	static final String BUGS_KEY           = "OPTIONS_BUGS_MAIN_TITLE";
	static final String APPS_KEY           = "OPTIONS_APPS_MAIN_TITLE";
	static final String SEARCH_KEY         = "OPTIONS_SEARCH_MAIN_TITLE";
	static final String SEARCH_LIMIT_KEY   = "OPTIONS_SEARCH_LIMIT_MAIN_TITLE";
	static final String SEARCH_QUALITY_KEY = "OPTIONS_SEARCH_QUALITY_MAIN_TITLE";
	static final String SEARCH_SPEED_KEY   = "OPTIONS_SEARCH_SPEED_MAIN_TITLE";
	static final String FILTERS_KEY        = "OPTIONS_FILTERS_MAIN_TITLE";
	static final String RESULTS_KEY        = "OPTIONS_RESULTS_MAIN_TITLE";
	static final String MESSAGES_KEY       = "OPTIONS_MESSAGES_MAIN_TITLE";
	static final String ADVANCED_KEY       = "OPTIONS_ADVANCED_MAIN_TITLE";
	static final String PREFERENCING_KEY   = "OPTIONS_PREFERENCING_MAIN_TITLE";
	static final String FIREWALL_KEY       = "OPTIONS_FIREWALL_MAIN_TITLE";
    static final String GUI_KEY            = "OPTIONS_GUI_MAIN_TITLE";
    static final String AUTOCOMPLETE_KEY   = "OPTIONS_AUTOCOMPLETE_MAIN_TITLE"; 
    static final String STARTUP_KEY        = "OPTIONS_STARTUP_MAIN_TITLE";   
    static final String PROXY_KEY          = "OPTIONS_PROXY_MAIN_TITLE";
	
	/**
	 * The constructor create all of the options windows and their
	 * components.
	 *
	 * @param treeManager the <tt>OptionsTreeManager</tt> instance to
	 *                    use for constructing the main panels and
	 *                    adding elements
	 * @param paneManager the <tt>OptionsPaneManager</tt> instance to
	 *                    use for constructing the main panels and
	 *                    adding elements
	 */
	public OptionsConstructor(final OptionsTreeManager treeManager, 
			final OptionsPaneManager paneManager) {
		TREE_MANAGER = treeManager;
		PANE_MANAGER = paneManager;
		final String title = GUIMediator.getStringResource("OPTIONS_TITLE");
        final boolean shouldBeModal = !(CommonUtils.isMacOSX() && 
                                        CommonUtils.isJava14OrLater());

		DIALOG = new JDialog(GUIMediator.getAppFrame(), title, shouldBeModal);
		DIALOG.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		GUIUtils.addHideAction((JComponent)DIALOG.getContentPane());

		// make the window non-resizable only for operating systems
		// where we know this will not cause a problem
		if((CommonUtils.isWindows() && CommonUtils.isJava14OrLater()) ||
		   CommonUtils.isMacOSX()) {
			DIALOG.setResizable(false);
		}
		DIALOG.setSize(OPTIONS_WIDTH, OPTIONS_HEIGHT);

		// most Mac users expect changes to be saved when the window
		// is closed, so save them
		DIALOG.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
				    int answer = -1;
				    if(OptionsMediator.instance().isDirty()) {
				        answer = GUIMediator.showYesNoCancelMessage("OPTIONS_SAVE_ON_CLOSE");
				        if(answer == GUIMediator.YES_OPTION) {
				            OptionsMediator.instance().applyOptions();
					        SettingsHandler.save();
					    }
                    }
                    if(answer != GUIMediator.CANCEL_OPTION) {
                        DIALOG.dispose();
						OptionsMediator.instance().disposeOptions();
                    }
				} catch(IOException ioe) {
					// nothing we should do here.  a message should
					// have been displayed to the user with more
					// information
				}
			}
        });

		PaddedPanel mainPanel = new PaddedPanel();

		Box splitBox = new Box(BoxLayout.X_AXIS);

		Component treeComponent = TREE_MANAGER.getComponent();
		Component paneComponent = PANE_MANAGER.getComponent();

		splitBox.add(treeComponent);
		splitBox.add(paneComponent);
		mainPanel.add(splitBox);

		mainPanel.add(Box.createVerticalStrut(17));
		mainPanel.add(new OptionsButtonPanel().getComponent());

		DIALOG.getContentPane().add(mainPanel);

		addOption(OptionsMediator.ROOT_NODE_KEY, SAVE_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, SHARED_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, SPEED_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, DOWNLOAD_KEY);

		// add the upload options group
		addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, UPLOAD_KEY);
		addOption(UPLOAD_KEY, UPLOAD_BASIC_KEY);
		addOption(UPLOAD_KEY, UPLOAD_SLOTS_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, CONNECTIONS_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, SHUTDOWN_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, UPDATE_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, CHAT_KEY);
		addOption(OptionsMediator.ROOT_NODE_KEY, PLAYER_KEY);	
        addOption(OptionsMediator.ROOT_NODE_KEY, STATUS_BAR_KEY);

		if (CommonUtils.isJava14OrLater() || CommonUtils.isMacOSX()) {
			addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, ITUNES_KEY);
			if (CommonUtils.isMacOSX()) {
				addOption(ITUNES_KEY, ITUNES_IMPORT_KEY);
			}
			if (CommonUtils.isJava14OrLater()) {
				addOption(ITUNES_KEY, ITUNES_DAAP_KEY);
			}
		}

		if (!CommonUtils.isWindows() && !CommonUtils.isAnyMac()) {
			addOption(OptionsMediator.ROOT_NODE_KEY, APPS_KEY);
		}

		addOption(OptionsMediator.ROOT_NODE_KEY, BUGS_KEY);

		addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, GUI_KEY);
		addOption(GUI_KEY, POPUPS_KEY);
        addOption(GUI_KEY, AUTOCOMPLETE_KEY);

		// add the search options group
		addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, SEARCH_KEY);

		addOption(SEARCH_KEY, SEARCH_LIMIT_KEY);
		addOption(SEARCH_KEY, SEARCH_QUALITY_KEY);
		addOption(SEARCH_KEY, SEARCH_SPEED_KEY);


		// add the filters options group
		addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, FILTERS_KEY);
		addOption(FILTERS_KEY, RESULTS_KEY);
		addOption(FILTERS_KEY, MESSAGES_KEY);

		// add the advanced options group
		addGroupTreeNode(OptionsMediator.ROOT_NODE_KEY, ADVANCED_KEY);

		addOption(ADVANCED_KEY, PREFERENCING_KEY);
		addOption(ADVANCED_KEY, FIREWALL_KEY);
		addOption(ADVANCED_KEY, PROXY_KEY);
        if (GUIUtils.shouldShowStartOnStartupWindow()) {
            addOption(ADVANCED_KEY, STARTUP_KEY);
        }
		
		PANE_MANAGER.show(SAVE_KEY);
	}
	
	
	/**
	 * Adds a parent node to the tree.  This node serves navigational
	 * purposes only, and so has no corresponding <tt>OptionsPane</tt>.
	 * This method allows for multiple tiers of parent nodes, not only
	 * top-level parents.
	 *
	 * @param parentKey the key of the parent node to add this parent
	 *                  node to
	 * @param childKey the key of the new parent node that is a child of
	 *                 the <tt>parentKey</tt> argument
	 */
	private final void addGroupTreeNode(final String parentKey,
			final String childKey) {
		TREE_MANAGER.addNode(parentKey, childKey,
			GUIMediator.getStringResource(childKey));
	}

	/**
	 * Adds the specified key and <tt>OptionsPane</tt> to current
	 * set of options.  This adds this <tt>OptionsPane</tt> to the set of
	 * <tt>OptionsPane</tt>s the user can select.
	 *
	 * @param parentKey the key of the parent node to add the new node to
	 * @param pane the new pane that also supplies the name of the node
	 *             in the tree
	 */
	private final void addOption(final String parentKey,
			final String childKey) {
		TREE_MANAGER.addNode(parentKey, childKey,
			GUIMediator.getStringResource(childKey));
	}


	/**
	 * Makes the options window either visible or not visible depending on the
	 * boolean argument.
	 *
	 * @param visible <tt>boolean</tt> value specifying whether the options
	 *				window should be made visible or not visible
	 * @param key the unique identifying key of the panel to show
	 */
	final void setOptionsVisible(boolean visible, final String key) {
	    if(!visible) {
	        DIALOG.dispose();
			OptionsMediator.instance().disposeOptions();
        } else {
            if(GUIMediator.isAppVisible())
    			DIALOG.setLocationRelativeTo(GUIMediator.getAppFrame());
    		else {
    			Dimension screenSize =
    			    Toolkit.getDefaultToolkit().getScreenSize();
        		Dimension dialogSize = DIALOG.getSize();
        		DIALOG.setLocation((screenSize.width - dialogSize.width)/2,
        						   (screenSize.height - dialogSize.height)/2);
    		}
			//  initial tree selection
			if (key == null)
				TREE_MANAGER.setDefaultSelection();
			else
				TREE_MANAGER.setSelection(key);
    		DIALOG.setVisible(true);
        }
	}	
	
	/** Returns if the Options Box is visible.
	 *  @return true if the Options Box is visible.
	 */
	public final boolean isOptionsVisible() {
		return DIALOG.isVisible();
	}

	/**
	 * Returns the main <tt>JDialog</tt> instance for the options window,
	 * allowing other components to position themselves accordingly.
	 *
	 * @return the main options <tt>JDialog</tt> window
	 */
	JDialog getMainOptionsComponent() {
		return DIALOG;
	}
}
