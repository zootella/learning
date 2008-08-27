package com.limegroup.gnutella.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MessageService;

/**
 * Opens a file chooser dialog centerened on {@link
 * MessageService#getParentComponent()} and adds the selected file to the
 * specially shared files if it is not being shared already.
 */
public class ShareFileSpeciallyAction extends AbstractAction {

	public ShareFileSpeciallyAction() {
		putValue(Action.NAME, GUIMediator.getStringResource
				("SHARE_FILE_ACTION_NAME"));
		putValue(Action.SHORT_DESCRIPTION, "Opens a Dialog and Lets You Choose a File to Share");
		String mnemonic = GUIMediator.getStringResource("SHARE_FILE_ACTION_MNEMONIC");
		if (mnemonic.length() > 0) {
			putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charAt(0)));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		final File toShare = FileChooserHandler.getInputFile(MessageService.getParentComponent(), 
				"SHARE_FILE_ACTION_NAME",
				"SHARE_FILE_ACTION_APPROVE_LABEL",
				null);
		if (toShare != null) {
		    GUIMediator.instance().schedule(new Runnable() {
		        public void run() {
			        RouterService.getFileManager().addFileAlways(toShare, new Listener());
                }
            });
        }
	}
	
	private static class Listener implements FileEventListener {
	    public void handleFileEvent(final FileManagerEvent fev) {
	    	GUIMediator.safeInvokeLater(new Runnable() {
	    		public void run() {
	    			if (fev.isAlreadySharedEvent())
	    				GUIMediator.showFormattedError("SHARE_FILE_ALREADY_SHARED", new Object[] { fev.getFiles()[0] });
	    			else if (!fev.isAddEvent()) // like FailedEvent, but potentially others too.
	    				GUIMediator.showFormattedError("SHARE_FILE_FAILED", new Object[] { fev.getFiles()[0] });
	    		}
	    	});
        }
    }
	            
}
