package com.limegroup.gnutella.gui.init;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LabeledTextField;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class displays a setup window for allowing the user to choose
 * the directory for saving their files.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|

class SaveWindow extends SetupWindow {

	/**
	 * Constant handle to the <tt>LabeledTextField</tt> instance for the 
	 * save directory.
	 */
	private final LabeledTextField SAVE_FIELD = 
		new LabeledTextField(
		    GUIMediator.getStringResource("INIT_SAVE_DIRECTORY_FIELD_LABEL"), 
			20);   
	
	/**
	 * Variable for the default save directory to use.
	 */    
	private String _defaultSaveDir;

	/**
	 * Creates the window and its components
	 */
	SaveWindow(SetupManager manager) {
		super(manager, "SETUP_SAVE_TITLE", "SETUP_SAVE_LABEL");
    }
    
    protected void createWindow() {
        super.createWindow();

		File saveDir = SharingSettings.getSaveDirectory();
		try {
		    _defaultSaveDir = saveDir.getCanonicalPath();
		} catch(IOException e) {
		    _defaultSaveDir = saveDir.getAbsolutePath();
		}

		JPanel mainPanel = new BoxPanel(BoxLayout.Y_AXIS);

		String[] labels = {
			"INIT_SAVE_BROWSE_BUTTON_LABEL",
			"INIT_SAVE_DEFAULT_BUTTON_LABEL"
		};
		String[] toolTips = {
			"INIT_SAVE_BROWSE_BUTTON_TIP",
			"INIT_SAVE_DEFAULT_BUTTON_TIP"
		};
		
		ActionListener[] listeners = {
			new SaveListener(), 
			new DefaultListener()
		};
		ButtonRow buttons = 
		    new ButtonRow(labels, toolTips, listeners, 
						  ButtonRow.X_AXIS, ButtonRow.LEFT_GLUE);
        try {
		    SAVE_FIELD.setText(_defaultSaveDir);
        } catch(NullPointerException npe) {
            // internal swing error -- no biggie if it happens,
            // just means the user has to manually click 'Use Default'.
        }
		mainPanel.add(SAVE_FIELD);
		mainPanel.add(buttons);
		super.addSetupComponent(mainPanel);
	}
	
	/**
	 * Overrides applySettings method in SetupWindow.
	 *
	 * This method applies any settings associated with this setup window.
	 */
	public void applySettings() throws ApplySettingsException {
		try {
			String saveDirString = SAVE_FIELD.getText();
			File saveDir = new File(saveDirString);
            
            if (!saveDir.isDirectory())
                if(!saveDir.mkdirs()) throw new IOException();
            
            // updates Incomplete directory etc... 
            SharingSettings.setSaveDirectory(saveDir); 
            SharingSettings.DIRECTORIES_TO_SHARE.add(saveDir);
            
		} catch(IOException ioe) {
			String msgKey = "MESSAGE_INVALID_SAVE_DIRECTORY";
			throw new ApplySettingsException(msgKey);
		}
	}

	private class DefaultListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            SAVE_FIELD.setText(_defaultSaveDir);
        }
    }
	
	private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File saveDir = 
                    FileChooserHandler.getInputDirectory(SaveWindow.this);
			if(saveDir == null || !saveDir.isDirectory()) return;
			SAVE_FIELD.setText(saveDir.getAbsolutePath());
        }
    }
}




