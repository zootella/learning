package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.UploadSettings;

/**
 * Allows the user to change whether or not partial files are shared.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class PartialFileSharingPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * upload pane check box label in the options window.
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_UPLOAD_ALLOW_PARTIAL_SHARING_CHECK_BOX_LABEL";

	/**
	 * Constant for the check box that specifies whether or not partial
	 * files should be shared.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public PartialFileSharingPaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	public void initOptions() {
	    CHECK_BOX.setSelected(UploadSettings.ALLOW_PARTIAL_SHARING.getValue());
	}

	public boolean applyOptions() throws IOException {
	    UploadSettings.ALLOW_PARTIAL_SHARING.setValue(CHECK_BOX.isSelected());
        return false;
	}
	
    public boolean isDirty() {
        return UploadSettings.ALLOW_PARTIAL_SHARING.getValue() != CHECK_BOX.isSelected();
    }
}

