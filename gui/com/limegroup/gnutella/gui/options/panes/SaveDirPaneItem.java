package com.limegroup.gnutella.gui.options.panes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SaveDirectoryHandler;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class defines the panel in the options window that allows the user to
 * change the directory for saving files.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class SaveDirPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * label on the component that allows to user to change the setting for this
	 * <tt>PaneItem</tt>.
	 */
	private final String OPTION_LABEL = "OPTIONS_SAVE_DIR_BOX_LABEL";
	
	/**
	 * The SharedDirPaneItem, so new save directories can be shared.
	 */
	private final SharedDirPaneItem _shareData;
	
	/**
	 * Handle to the <tt>JTextField</tt> that displays the save directory.
	 */
	private JTextField _saveField;
	/**
	 * String for storing the initial save directory.
	 */
    private String _saveDirectory;
    
	/**
	 * The mediatype table mediator that handles the per mediatype download
	 * directories table.
	 */
	private MediaTypeDownloadDirMediator _mtddMediator; 
	/**
	 *  The mediator's description label id.
	 */	
	private final String MEDIA_OPTION_LABEL = "OPTIONS_SAVE_MEDIATYPE_DIR_LABEL";

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>. This includes the row of buttons that allow
	 * the user to select the save directory.
	 *
	 * @param key
	 *            the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate strings
	 */
	public SaveDirPaneItem(final String key, SharedDirPaneItem shareStuff) {
		super(key);
		_shareData = shareStuff;
		_saveField = new SizedTextField();
		LabeledComponent comp = new LabeledComponent(OPTION_LABEL, _saveField,
								 LabeledComponent.TOP_LEFT);
		String[] labelKeys = { "OPTIONS_SAVE_DIR_BROWSE_BUTTON_LABEL",
				"OPTIONS_SAVE_DIR_DEFAULT_BUTTON_LABEL" };
		String[] toolTipKeys = { "OPTIONS_SAVE_DIR_BROWSE_BUTTON_TIP",
				"OPTIONS_SAVE_DIR_DEFAULT_BUTTON_TIP" };
		ActionListener[] listeners = { new SelectSaveDirectoryListener(),
				new DefaultListener() };
		ButtonRow br = new ButtonRow(labelKeys, toolTipKeys, listeners, 
									 ButtonRow.X_AXIS, ButtonRow.LEFT_GLUE);
		add(comp.getComponent());
		add(getVerticalSeparator());		
		add(br);				
		
		add(getVerticalSeparator());
		
		_mtddMediator = new MediaTypeDownloadDirMediator(_saveField);
		
		comp = new LabeledComponent(MEDIA_OPTION_LABEL,
				_mtddMediator.getComponent(), LabeledComponent.TOP_LEFT,
				LabeledComponent.NO_GLUE);
		add(comp.getComponent());

		Action[] actions = new AbstractAction[2];
		actions[0] = _mtddMediator.getBrowseDirectoryAction();
		actions[1] = _mtddMediator.getResetDirectoryAction();
		br = new ButtonRow(actions, ButtonRow.X_AXIS, ButtonRow.LEFT_GLUE);
		add(getVerticalSeparator());		
		add(br);
	}

	

	/**
	 * This listener responds to the selection of the default save directory
	 * and sets the current save directory to be the default.
	 */
	private class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            _saveField.setText(SharingSettings.DEFAULT_SAVE_DIR.getAbsolutePath());
        }
    }

	/**
	 * This listener displays a <tt>JFileChooser</tt> to the user, allowing
	 * the user to select the save directory.
	 */
	private class SelectSaveDirectoryListener implements ActionListener {
            
		public void actionPerformed(ActionEvent e) {
			File dir = FileChooserHandler.getInputDirectory(MEDIATOR.getMainOptionsComponent());
			
            // If the user cancelled the file chooser, simply return.
			if (dir == null)
				return;
				
            // Otherwise, make sure they selected a valid directory that
            // they can really write to.
            if(!SaveDirectoryHandler.isSaveDirectoryValid(dir)) {
				GUIMediator.showError("ERROR_INVALID_SAVE_DIRECTORY_SELECTION");
                return;
            }
            
		    try {
			    String newDir = dir.getCanonicalPath();
			    if(!newDir.equals(_saveDirectory)) {
				    _saveField.setText(newDir);
			    }
			} catch (IOException ioe) {}
        }
    }

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.
	 * <p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
		try {
            File file = SharingSettings.getSaveDirectory();
            if (file == null) {
                throw (new FileNotFoundException());
            }
			_saveDirectory = file.getCanonicalPath();
		} catch (FileNotFoundException fnfe) {
			// simply use the empty string if we could not get the save
			// directory.
			_saveDirectory = "";
		} catch (IOException ioe) {
			_saveDirectory = "";
		}
		
        _saveField.setText(_saveDirectory);
		_mtddMediator.initOptions();
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.
	 * <p>
	 *
	 * Applies the options currently set in this window, displaying an error
	 * message to the user if a setting could not be applied.
	 *
	 * @throws IOException
	 *             if the options could not be applied for some reason
	 */
	public boolean applyOptions() throws IOException {
        final String save = _saveField.getText();
        Set newDirs = new HashSet();
        if(!save.equals(_saveDirectory)) {
            try {
				File saveDir = new File(save);
                if(!saveDir.isDirectory()) {
					if (!saveDir.mkdirs())
						throw new IOException();
                }
                if(!_shareData.isGoingToBeShared(saveDir))
                    newDirs.add(saveDir);
				SharingSettings.setSaveDirectory(saveDir);
                _saveDirectory = save;
            } catch(IOException ioe) {
                GUIMediator.showError("ERROR_INVALID_SAVE_DIRECTORY");
                _saveField.setText(_saveDirectory);
				throw new IOException();
            } catch(NullPointerException npe) {
                GUIMediator.showError("ERROR_INVALID_SAVE_DIRECTORY");
                _saveField.setText(_saveDirectory);
				throw new IOException();
			}
        }
        
        boolean restart = _mtddMediator.applyOptions(newDirs);
        if(!newDirs.isEmpty()) {
            String format = "";
            for(Iterator i = newDirs.iterator(); i.hasNext(); )
                format += GUIUtils.convertToNonBreakingSpaces(4, i.next().toString()) + "\n";
            int response = GUIMediator.showYesNoMessage("OPTIONS_SHARED_NEW_SAVE_FOLDERS", format);
            if(response == GUIMediator.YES_OPTION) {
                for(Iterator i = newDirs.iterator(); i.hasNext(); )
                    _shareData.addAndKeepDirtyStatus((File)i.next());
            }
        }
        return restart;
	}
	
	/**
	 * Gets the save directories.
	 */
	Collection getSaveDirectories() {
	    Set dirs = new HashSet();
	    dirs.add(new File(_saveDirectory));
	    _mtddMediator.addSaveDirs(dirs);
	    return dirs;
	}
	    

	public boolean isDirty() {
		return !SharingSettings.getSaveDirectory().equals(
				new File(_saveField.getText())) || _mtddMediator.isDirty();
	}
}
