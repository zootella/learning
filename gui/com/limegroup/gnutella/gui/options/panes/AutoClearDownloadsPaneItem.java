package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to specify whether or not completed or inactive downloads should be
 * automatically cleared from the download window.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class AutoClearDownloadsPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * download pane check box label in the options window.
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_DOWNLOAD_CLEAR_CHECK_BOX_LABEL";

	/**
	 * Constant for the check box that specifies whether or not downloads 
	 * should be automatically cleared.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	/**
	 * The stored value to allow rolling back changes.
	 */
	private boolean _clearDownloads;

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public AutoClearDownloadsPaneItem(final String key) {
		super(key);
		
		// add a labeled component with a glue forcing the component
		// to the right
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
        _clearDownloads = SharingSettings.CLEAR_DOWNLOAD.getValue();
        CHECK_BOX.setSelected(_clearDownloads);
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Applies the options currently set in this window, displaying an
	 * error message to the user if a setting could not be applied.
	 *
	 * @throws IOException if the options could not be applied for some reason
	 */
	public boolean applyOptions() throws IOException {
        final boolean clearDownloads = CHECK_BOX.isSelected();
        if(clearDownloads != _clearDownloads) {
            SharingSettings.CLEAR_DOWNLOAD.setValue(clearDownloads);
            _clearDownloads = clearDownloads;
        }
        return false;
	}
	
    public boolean isDirty() {
        return SharingSettings.CLEAR_DOWNLOAD.getValue() != CHECK_BOX.isSelected();
    }	
}
