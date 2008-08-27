package com.limegroup.gnutella.gui.options.panes;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change the maximum number of dowloads to allow at any one time.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class AutoClearUploadsPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * upload pane check box label in the options window.
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_UPLOAD_CLEAR_CHECK_BOX_LABEL";

	/**
	 * Constant for the check box that specifies whether or not uploads 
	 * should be automatically cleared.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	/**
	 * The stored value to allow rolling back changes.
	 */
	private boolean _clearUploads;

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public AutoClearUploadsPaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
        _clearUploads = SharingSettings.CLEAR_UPLOAD.getValue();
		CHECK_BOX.setSelected(_clearUploads);
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Applies the options currently set in this window.
	 */
	public boolean applyOptions() {
        final boolean clearUploads = CHECK_BOX.isSelected();
        if(clearUploads != _clearUploads) {
            SharingSettings.CLEAR_UPLOAD.setValue(clearUploads);
            _clearUploads = clearUploads;
        }
        return false;
	}
	
    public boolean isDirty() {
        return SharingSettings.CLEAR_UPLOAD.getValue() != CHECK_BOX.isSelected();
    }	
}
