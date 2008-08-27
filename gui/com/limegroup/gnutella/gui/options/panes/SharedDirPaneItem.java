package com.limegroup.gnutella.gui.options.panes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import com.limegroup.gnutella.FileManager;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.StandardListEditor;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change the directory that are shared.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class SharedDirPaneItem extends AbstractPaneItem {

	/**
	 * Constant handle to the <tt>StandardListEditor</tt> that adds and removes
	 * remove hosts to aumotically connect to.
	 */
	private final StandardListEditor DIR_LIST = 
		new StandardListEditor(new SelectSharedDirectoryListener());

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate strings
	 */
	public SharedDirPaneItem(final String key) {
		super(key);
		add(DIR_LIST.getComponent());
	}

    /** 
	 * Adds a directory to the string of shared directories,
     * checking to make sure that the directory is not already 
	 * contained in the shared directory String. 
	 *
	 * @param dir a <tt>File</tt> instance denoting the abstract pathname 
     *  of the new shared directory
	 */
    private void addDirectory(File dir) {
		if (!dir.isDirectory())
			return;
		
		if(!isGoingToBeShared(dir))
		    DIR_LIST.addFile(dir);
    }
    
    /**
     * Adds a directory to the internal list, resetting 'dirty' only if it wasn't
     * dirty already.
     */
    void addAndKeepDirtyStatus(File dir) {
        boolean wasDirty = DIR_LIST.getListChanged();
        addDirectory(dir);
        if(!wasDirty)
            DIR_LIST.resetList();
    }
    
    /**
     * Determines if this is already in the list of things to share.
     */
    boolean isGoingToBeShared(File dir) {
		File[] dirs = DIR_LIST.getDataAsFileArray();
        for(int i = 0; i < dirs.length; i++) {
            if (dirs[i].equals(dir))
                return true;
        }
        return false;
    }
        
	/** 
	 * This class shows the <tt>JFileChooser</tt> when the user presses 
	 * the button to add a new directory to the shared directories.  It
	 * adds the directory only if does not already exist in the list.
	 */
  	private class SelectSharedDirectoryListener implements ActionListener {	   
  		public void actionPerformed(ActionEvent ae) {
			File dir = FileChooserHandler.getInputDirectory(MEDIATOR.getMainOptionsComponent());
			if (dir == null)
				return;
			if (!dir.isDirectory() || !dir.canRead()) {
				GUIMediator.showError("ERROR_INVALID_SHARED_DIRECTORY");
				return;
			}
			//  share directory only if not sensitive 
			if (FileManager.isSensitiveDirectory(dir))
				if (!RouterService.getCallback().warnAboutSharingSensitiveDirectory(dir))
					return;
			SharedDirPaneItem.this.addDirectory(dir);
  		}
  	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
		File[] dirs = SharingSettings.DIRECTORIES_TO_SHARE.getValueAsArray();
		DIR_LIST.setListData(dirs);
		DIR_LIST.resetList();
	}
	
	/**
	 * Gets all folders to share.
	 */
	public Set getDirectoriesToShare() {
	    return new HashSet(Arrays.asList(DIR_LIST.getDataAsFileArray()));
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
     * This makes sure that the shared directories have, in fact, changed to
     * make sure that we don't load the <tt>FileManager</tt> twice.  This is
     * particularly relevant to the case where the save directory has changed,
     * in which case we only want to reload the <tt>FileManager</tt> once for 
     * any changes.<p>
     * 
	 * Applies the options currently set in this window, displaying an
	 * error message to the user if a setting could not be applied.
	 *
	 * @throws <tt>IOException</tt> if the options could not be applied 
     *  for some reason
	 */
	public boolean applyOptions() throws IOException {
        // Handle a change to the shared directories or list of extensions.
        if(DIR_LIST.getListChanged())
			DIR_LIST.resetList();
			// the actual applying of shared folders is done in OptionsPaneManager,
			// since it needs to be _after_ everything else is done.
        
        return false;
	}
	
	public boolean isDirty() {
	    return DIR_LIST.getListChanged();
    }
}





