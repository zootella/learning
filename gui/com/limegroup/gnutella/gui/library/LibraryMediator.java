package com.limegroup.gnutella.gui.library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIConstants;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.actions.ConfigureOptionsAction;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.gui.util.DividerLocationSettingUpdater;
import com.limegroup.gnutella.settings.UISettings;
import com.limegroup.gnutella.util.CommonUtils;


/**
 * This class functions as an initializer for all of the elements
 * of the library and as a mediator between library objects.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class LibraryMediator implements ThemeObserver {

	/**
	 * The primary panel that contains all of the library elements.
	 */
	private static final JPanel MAIN_PANEL = new JPanel(new GridBagLayout());
	private static final CardLayout viewLayout = new CardLayout();
	private static final JPanel viewPanel = new JPanel(viewLayout);

	/**
     * Constant handle to the <tt>LibraryTree</tt> library controller.
     */
    private static final LibraryTree LIBRARY_TREE = LibraryTree.instance();
	static {
		LIBRARY_TREE.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
	}
	private static final JScrollPane TREE_SCROLL_PANE = new JScrollPane(LIBRARY_TREE);
    
    /**
     * Constant handle to the <tt>LibraryTable</tt> that displays the files
     * in a given directory.
     */
    private static final LibraryTableMediator LIBRARY_TABLE =
        LibraryTableMediator.instance();

    private static final String TABLE_KEY = "LIBRARY_TABLE";
    private static final String SHARED_KEY = "SHARED";

    /**
     * Constant handle to the file update handler.
     */
    private final HandleFileUpdate FILE_UPDATER = new HandleFileUpdate();
   
	/** Panel for the Shared Files node. */
	private static JPanel jpShared = null;

	
	///////////////////////////////////////////////////////////////////////////
	//  Singleton Pattern
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Singleton instance of this class.
	 */
	private static final LibraryMediator INSTANCE = new LibraryMediator();
    
	/**
	 * @return the <tt>LibraryMediator</tt> instance
	 */
	public static LibraryMediator instance() { return INSTANCE; }

    /** 
	 * Constructs a new <tt>LibraryMediator</tt> instance to manage calls
	 * between library components.
	 */
    private LibraryMediator() {		
		GUIMediator.setSplashScreenString(
		    GUIMediator.getStringResource("SPLASH_STATUS_LIBRARY_WINDOW"));
		ThemeMediator.addThemeObserver(this);

		addView(LIBRARY_TABLE.getScrolledTablePane(), TABLE_KEY);
		
		//  Create split pane
		JSplitPane splitPane = 
		    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, TREE_SCROLL_PANE, viewPanel);
        splitPane.setOneTouchExpandable(true);
		new DividerLocationSettingUpdater(splitPane,
				UISettings.UI_LIBRARY_TREE_DIVIDER_LOCATION);

		//  Create refresh and explore buttons
        String[] refreshKey = { "LIBRARY_REFRESH_BUTTON_LABEL" };
        String[] refreshTip = { "LIBRARY_REFRESH_BUTTON_TIP" };
        ActionListener[] refreshListener = { new RefreshListener() };
        String[] refreshName  = { "LIBRARY_REFRESH"};
		ButtonRow refreshButton = new ButtonRow(refreshKey, refreshTip, 
												refreshListener, refreshName);
        String[] exploreKey = { "LIBRARY_EXPLORE_BUTTON_LABEL" };
        String[] exploreTip = { "LIBRARY_EXPLORE_BUTTON_TIP" };
        ActionListener[] exploreListener = { new ExploreListener() };
        String[] exploreName = { "LIBRARY_EXPLORE" };
		ButtonRow exploreButton = new ButtonRow(exploreKey, exploreTip, 
												exploreListener, exploreName);

		//  Add refresh button and, if Win or Mac, explore button 
  		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		buttonPanel.add(refreshButton, gbc);
		gbc.gridx = GridBagConstraints.RELATIVE;
	    if(CommonUtils.isWindows() || CommonUtils.isMacOSX()) 	
		    buttonPanel.add(exploreButton, gbc);
		
		//  Add table's buttons
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		buttonPanel.add(LIBRARY_TABLE.getButtonRow(), gbc);

		//  Layout main panel
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(GUIConstants.SEPARATOR, GUIConstants.SEPARATOR, GUIConstants.SEPARATOR, GUIConstants.SEPARATOR);
		MAIN_PANEL.add(splitPane, gbc);
		gbc.gridy = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, GUIConstants.SEPARATOR, GUIConstants.SEPARATOR, GUIConstants.SEPARATOR);
		MAIN_PANEL.add(buttonPanel, gbc);
		updateTheme();		
		
		//  Set the initial selection in the LibraryTree
		LIBRARY_TREE.setInitialSelection();
	}

	// inherit doc comment
	public void updateTheme() {
		LIBRARY_TREE.updateTheme();
		Color tableColor = ThemeFileHandler.TABLE_BACKGROUND_COLOR.getValue();
		TREE_SCROLL_PANE.getViewport().setBackground(tableColor);
	}

	/**
	 * Returns the <tt>JComponent</tt> that contains all of the elements of
	 * the library.
	 *
	 * @return the <tt>JComponent</tt> that contains all of the elements of
	 * the library.
	 */
	public JComponent getComponent() {
		return MAIN_PANEL;
	}
	
    /**
	 * Tells the library to launch the application associated with the 
	 * selected row in the library. 
	 */
    public void launchLibraryFile() {
		LIBRARY_TABLE.launch();
    }
    
    /**
	 * Deletes the currently selected rows in the table. 
	 */
    public void deleteLibraryFile() {
        LIBRARY_TABLE.removeSelection();
    }
        
	/**
	 * Removes the gui elements of the library tree and table.
	 */
	public void clearLibrary() {
		LIBRARY_TREE.clear();
		//LIBRARY_TABLE.clearTable();
	}
    
    /**
     * Quickly refreshes the library.
     *
     * This is only done if a saved or incomplete folder is selected,
     * incase an incomplete file was deleted or a new file (not shared)
     * was added to a save directory.
     */
    public void quickRefresh() {
	    DirectoryHolder dh = LIBRARY_TREE.getSelectedDirectoryHolder();
		if(dh instanceof SavedFilesDirectoryHolder || dh instanceof IncompleteDirectoryHolder)
            updateTableFiles(dh);
    }

	/** 
	 * Launches explorer on PC in selected Shared directory
	 */
	public static void launchExplorer() {
		File exploreDir = LIBRARY_TREE.getSelectedDirectory();
		
		if (exploreDir == null)
			return;
		
		String explorePath = exploreDir.getPath();
		try {
		    explorePath = exploreDir.getCanonicalPath();
		} catch(IOException ioe) { }

		try {
            String cmdStr = "";
			if (CommonUtils.isWindows())
			    cmdStr = "explorer"; 
			else if (CommonUtils.isMacOSX()) 	
			    cmdStr = "open"; 
			Runtime.getRuntime().exec(new String[] { cmdStr, explorePath });
		} catch(SecurityException se) {
		} catch(IOException ieo) { }
	}

	/**
	 * Handles events created by the FileManager.  Passes these events on to
	 * the LibraryTableMediator or LibraryTree as necessary.
	 */
    public void handleFileManagerEvent(final FileManagerEvent evt) {
		LIBRARY_TREE.handleFileManagerEvent(evt);
		LIBRARY_TABLE.handleFileManagerEvent(evt, LIBRARY_TREE.getSelectedDirectoryHolder());		
    }
		
    /** 
	 * Displays a file chooser for selecting a new folder to share and 
	 * adds that new folder to the settings and FileManager.
	 */
    public void addSharedLibraryFolder() {
		File dir = FileChooserHandler.getInputDirectory();
		if (dir == null)
			return;
		addSharedLibraryFolder(dir);
    }
	
	public void addSharedLibraryFolder(final File dir) {
		if(dir == null || !dir.isDirectory() || !dir.canRead()) {
			GUIMediator.showError("ERROR_INVALID_SHARED_DIRECTORY");
			return;
		}
		
		GUIMediator.instance().schedule(new Runnable() {
		    public void run() {
		        RouterService.getFileManager().addSharedFolder(dir);
            }
        });
	}
	
	/**
	 * Update the this file's statistic
	 */
	public void updateSharedFile(final File file) {
	    // if the library table is visible, and
	    // if the selected directory is null
	    // or if we the file exists in a directory
	    // other than the one we selected, then there
	    // is no need to update.
	    // the user will see the newest stats when he/she 
	    // selects the directory.
	    DirectoryHolder dh = LIBRARY_TREE.getSelectedDirectoryHolder();
		if(LIBRARY_TABLE.getTable().isShowing() && dh != null && dh.accept(file)) {
		    // pass the update off to the file updater
		    // this way, only one Runnable is ever created,
		    // instead of allocating a new one every single time
		    // a query is hit.
		    // Very useful for large libraries and generic searches (ala: mp3)
		    FILE_UPDATER.addFileUpdate(file);
	    }
	}
	
	public void setAnnotateEnabled(boolean enabled) {
	    LIBRARY_TABLE.setAnnotateEnabled(enabled);
	}

    /** 
	 * Removes the selected folder from the shared folder group.. 
	 */
    public void unshareLibraryFolder() {
        LIBRARY_TREE.unshareLibraryFolder();
    }

    /**
     * Adds a file to the playlist.
     */
    void addFileToPlayList(File toAdd) {
        GUIMediator.getPlayList().addFileToPlaylist(toAdd);
    }

    /** 
	 * Obtains the shared files for the given directory and updates the 
	 * table accordingly.
	 *
	 * @param selectedDir the currently selected directory in
	 *        the library
	 */
    static void updateTableFiles(DirectoryHolder dirHolder) {
		LIBRARY_TABLE.updateTableFiles(dirHolder);
		showView(TABLE_KEY);
    }
    
	/** Returns true if this is showing the special incomplete directory,
     *  false if showing normal files. */
    public static boolean incompleteDirectoryIsSelected() {
        return LIBRARY_TREE.incompleteDirectoryIsSelected();        
    }
    
    /**
     *  Class to handle updates to shared file stats
     *  without creating tons of runnables.
     *  Idea taken from HandleQueryString in VisualConnectionCallback
     */
    private static final class HandleFileUpdate implements Runnable {
        private Vector  list;
        private boolean active;
    
        public HandleFileUpdate( ) {
            list   = new Vector();
            active = false;
        }
    
        public void addFileUpdate(File f) {
            list.addElement(f);
            if(active == false) {
                active = true;
                SwingUtilities.invokeLater(this);
            }
        }
    
        public void run() {
            try {
                File f;
                while (list.size() > 0) {
                    f = (File) list.firstElement();
                    list.removeElementAt(0);
    			    LIBRARY_TABLE.update(f);
                }
			} catch (IndexOutOfBoundsException e) {
        	    //this really should never happen, but
        	    //who really cares if we're not sharing it?
			} catch (Exception e) {
                //no other errors could happen, so if one does, something's wrong
                GUIMediator.showInternalError(e);
			}
			active = false;
        }
    }    
	
	/**
	 * Shows the Shared Files view in response to selection of the Shared Files
	 * in the LibraryTree.
	 */
	public static void showSharedFiles() {
		if (jpShared == null) {
			jpShared = new JPanel(new BorderLayout());
			JPanel jpInternal = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, ButtonRow.BUTTON_SEP, 0);
			jpInternal.add(new JLabel(GUIMediator.getStringResource("LIBRARY_SHARED_FILES_CONFIGURE_EXPLAIN")), gbc);
			gbc.gridy = 1;
			jpInternal.add(new JButton(new ConfigureOptionsAction(
					"OPTIONS_SHARED_MAIN_TITLE",
					"LIBRARY_SHARED_FILES_CONFIGURE",
					"LIBRARY_SHARED_FILES_CONFIGURE_EXPLAIN")), gbc);
			jpShared.add(jpInternal, BorderLayout.CENTER);
			jpShared.setBorder(BorderFactory.createEtchedBorder());
            addView(jpShared, SHARED_KEY);
        }
        showView(SHARED_KEY);
    }

	public static void showView(String key) {
		viewLayout.show(viewPanel, key);
	}
	
	public static void addView(Component c, String key) {
		viewPanel.add(c, key);
	}

	/**
	 * Sets the selected directory in the LibraryTree.
	 * 
	 * @return true if the directory exists in the tree and could be selected
	 */
	public static boolean setSelectedDirectory(File dir) {
		return LIBRARY_TREE.setSelectedDirectory(dir);		
	}

	/**
	 * Updates the Library GUI based on whether the player is enabled. 
	 */
	public void setPlayerEnabled(boolean value) {
		LIBRARY_TABLE.setPlayerEnabled(value);
		LIBRARY_TREE.setPlayerEnabled(value);
	}
}
