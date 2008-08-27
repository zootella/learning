package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.SharingSettings;

public class ShareSpeciallyPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * check box that allows the user to connect automatically or not
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_SHARED_SPECIALLY_SHARE_BOX_LABEL";

	/**
	 * Constant for the check box that determines whether or not 
	 * to send OOB searches.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	public ShareSpeciallyPaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	public void initOptions() {
        CHECK_BOX.setSelected
        	(SharingSettings.SHARE_DOWNLOADED_FILES_IN_NON_SHARED_DIRECTORIES.getValue());
	}

	public boolean applyOptions() throws IOException {
		SharingSettings.SHARE_DOWNLOADED_FILES_IN_NON_SHARED_DIRECTORIES.
			setValue(CHECK_BOX.isSelected());
        return false;
	}

    public boolean isDirty() {
        return SharingSettings.SHARE_DOWNLOADED_FILES_IN_NON_SHARED_DIRECTORIES.getValue() 
        	!= CHECK_BOX.isSelected();
    }
	
}
