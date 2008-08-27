package com.limegroup.gnutella.gui.options;

import java.awt.CardLayout;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JPanel;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * Manages the main options window that displays the various options 
 * windows.<p>
 *
 * This class also stores all of the main options panels to access
 * all of them regardless of how many there are or what their
 * specific type is.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class OptionsPaneManager {

	/**
	 * Constant for the main panel of the options window.
	 */
	private final JPanel MAIN_PANEL = new JPanel();

	/**
	 * Constant for the <tt>CardLayout</tt> used in the main panel.
	 */
	private final CardLayout CARD_LAYOUT = new CardLayout();

	/**
	 * Constant for the <tt>ArrayList</tt> containing all of the visible
	 * <tt>OptionsPane</tt> instances.
	 */
	private final ArrayList OPTIONS_PANE_LIST = new ArrayList();
	
	/**
	 * Stores the already created option panes by key.
	 */
	private final Map panesByKey = new HashMap();
	
	/**
	 * The factory which option panes are created from.
	 */
	private final OptionsPaneFactory FACTORY = new OptionsPaneFactory();
	
	/**
	 * The sharing settings at the time this was loaded or the last time we reloaded
	 * the FileManager.
	 */
	private Set sharedDirs;
	
	/**
	 * The constructor sets the layout and adds all of the <tt>OptionPane</tt>
	 * instances.
	 */
	public OptionsPaneManager() {
		MAIN_PANEL.setLayout(CARD_LAYOUT);		
		sharedDirs = new HashSet(SharingSettings.DIRECTORIES_TO_SHARE.getValue());
    }

	/**
	 * Shows the options pane speficied by its title.
	 * <p>
	 * Lazily creates the options pane if it was not shown before. Its options
	 * are initialized before it is shown. 
	 * 
	 * @param name the name of the <code>Component</code> to show
	 */
	public final void show(final String name) {
		if (!panesByKey.containsKey(name)) {
			OptionsPane pane = FACTORY.createOptionsPane(name);
			pane.initOptions();
			addPane(pane);
			panesByKey.put(name, pane);
			
			// If this was the 'SAVED' key, then also load shared,
			// since setting save stuff requires that sharing be updated also.
			if(name.equals(OptionsConstructor.SAVE_KEY) && !panesByKey.containsKey(OptionsConstructor.SHARED_KEY)) {
			    OptionsPane shared = FACTORY.createOptionsPane(OptionsConstructor.SHARED_KEY);
			    shared.initOptions();
			    addPane(shared);
			    panesByKey.put(name, shared);
			}
		}
		CARD_LAYOUT.show(MAIN_PANEL, name);
	}

	/**
	 * Sets the options for each <tt>OptionPane</tt> instance in the 
	 * <tt>ArrayList</tt> of <tt>OptionPane</tt>s when the window is shown.
	 */
	public void initOptions() {
		for (int i = 0, size = OPTIONS_PANE_LIST.size(); i < size; i++) {
			OptionsPane op = (OptionsPane)OPTIONS_PANE_LIST.get(i);
			op.initOptions();
		}
	}

	/**
	 * Applies the current settings in the options windows, storing them
	 * to disk.  This method delegates to the <tt>OptionsPaneManager</tt>.
	 *
	 * @throws IOException if the options could not be fully applied
	 */
	public final void applyOptions() throws IOException {
        boolean restartRequired = false;
        
		for (int i = 0, size = OPTIONS_PANE_LIST.size(); i < size; i++) {
			OptionsPane op = (OptionsPane)OPTIONS_PANE_LIST.get(i);
            restartRequired |= op.applyOptions();
		}
		
		// Apply the share directories after everything else has been applied.
		Set shared = FACTORY.getSharedPane().getDirectoriesToShare();
		if(!sharedDirs.equals(shared)) {
    	    sharedDirs = shared;
		    RouterService.getFileManager().loadWithNewDirectories(shared);
        }
	
        if(restartRequired)
            GUIMediator.showMessage("OPTIONS_RESTART_REQUIRED");
	}
	
	/**
	 * Determines if any of the panes are dirty.
	 */
    public final boolean isDirty() {
        for (int i = 0, size = OPTIONS_PANE_LIST.size(); i < size; i++) {
            OptionsPane op = (OptionsPane)OPTIONS_PANE_LIST.get(i);
            if (op.isDirty())
                return true;
        }
        return false;
    }
	
	/**
	 * Returns the main <code>Component</code> for this class.
	 *
	 * @return a <code>Component</code> instance that is the main component
	 *         for this class.
	 */
	public final Component getComponent() {
		return MAIN_PANEL;
	}

	/**
	 * Adds the speficied window to the CardLayout based on its title.
	 *
	 * @param window the <code>OptionsPane</code> to add
	 */
	public final void addPane(final OptionsPane pane) {
		MAIN_PANEL.add(pane.getContainer(), pane.getName());
		OPTIONS_PANE_LIST.add(pane);
	}
}
