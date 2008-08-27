package com.limegroup.gnutella.gui.init;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class displays for the user the directories that were found
 * in the hard drive scan, giving them the option to share them or not.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class ScanConfirmWindow extends SetupWindow {

	/**
	 * Constant for the number of directories to display after the
	 * file system scan.
	 */
	private final int SHARED_DIRECTORY_LIMIT = 5;

	/**
	 * The array of check boxes.
	 */
	private JCheckBox[] _dirCheckBoxes = 
		new JCheckBox[SHARED_DIRECTORY_LIMIT];

	/**
	 * a panel that holds the directory check boxes.
	 */
	private JPanel CHECK_BOX_PANEL = new BoxPanel(BoxLayout.Y_AXIS);
	
	/**
	 * flag for whether or not this window has ever been opened
	 * to avoid redrawing all of the check boxes.
	 */
	private boolean _windowOpened = false;

	/**
	 * Creates the window and its components
	 *
	 * @param manager the setup mediator class
	 */
	ScanConfirmWindow(SetupManager manager) {
		super(manager, "SETUP_CONFIRM_TITLE", "SETUP_CONFIRM_LABEL");
    }
    
	/**
	 * Overrides the createWindow in <tt>SetupWindow</tt> to mutate
	 * the label & check box list depending on whether or not we 
	 * found any directories in the scan.
	 */
    protected void createWindow() {
		if(!_windowOpened) {
			String[] dirNames = _manager.getScannedPaths();
			boolean hasDirectories = setDirectories(dirNames);
			_windowOpened = true;
			if(!hasDirectories)
			    setLabelKey("SETUP_CONFIRM_LABEL_NO_DIRECTORIES");
		}
		
        super.createWindow();
		JPanel mainPanel = new BoxPanel(BoxLayout.X_AXIS);

		mainPanel.add(CHECK_BOX_PANEL);
		mainPanel.add(Box.createHorizontalGlue());
		addSetupComponent(mainPanel);        
	}
	/**
	 * Overrides applySettings in <tt>SetupWindow</tt> superclass.
	 * 
	 * Applies the settings associated with this window.
	 */
	public void applySettings() throws ApplySettingsException {

		// note that this does not filter duplicates, but the call
		// to setDirectories will handle that for us
		File[] dirs = getSelectedDirectories();
		
		boolean gotSaveDir = false;
		int finalLength = dirs.length;
		File saveDir = null;	   
		try {
			saveDir = SharingSettings.getSaveDirectory();
            if (saveDir==null || !saveDir.exists()) {
                throw (new FileNotFoundException());
            }
            
            finalLength++;
            gotSaveDir = true;
		} catch(FileNotFoundException fnfe) {
			// this simply won't get added to the shared 
			// directories in this case
		}

        for(int i = 0; i < dirs.length; i++)
            SharingSettings.DIRECTORIES_TO_SHARE.add(dirs[i]);
        if(gotSaveDir)
            SharingSettings.DIRECTORIES_TO_SHARE.add(saveDir);
	}


	/**
	 * Returns an array of <tt>File</tt> instances where each <tt>File</tt>
	 * instance denotes the abstract pathname of a directory that the
	 * user has selected to share.
	 *
	 * @return an array of <tt>File</tt> instances denoting the abstract
	 *  pathnames of directories the user would like to share
	 */
	private File[] getSelectedDirectories() {
		ArrayList fileList = new ArrayList();
		
		int numDirsSelected = 0;
		for (int i=0; i<_dirCheckBoxes.length; i++) {
  			if(_dirCheckBoxes[i] != null &&
  			   _dirCheckBoxes[i].isSelected() == true) {
				numDirsSelected++;
				fileList.add(new File(_dirCheckBoxes[i].getText()));
            }
  		}
		
		File[] files = new File[numDirsSelected];
		for(int r=0; r<numDirsSelected; r++) {
			files[r] = (File)fileList.get(r);
		}

		return files;		
	}

	/**
	 * Sets the directories to display to the user.
	 *
	 * @param dirPaths the pathname string of the directories to display to
	 *  the user
	 */
	private boolean setDirectories(String[] dirPaths) {
	    if(dirPaths == null)
	        return false;

	    boolean exists = false;
		for (int i = 0; i < dirPaths.length; i++) {
			// for each result, create a check box, and add it
			// to the panel
			if (dirPaths[i] != null) {
				_dirCheckBoxes[i] = new JCheckBox("", false);
				_dirCheckBoxes[i].setText(dirPaths[i]);
				CHECK_BOX_PANEL.add(_dirCheckBoxes[i]);
				exists = true;
			}
		}	
		CHECK_BOX_PANEL.revalidate();
		return exists;
	} 
}













