package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * This class gives the user the option of whether or not the user wants to use
 * OOB queries (if even possible).
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class DisableOOBSearchingPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * check box that allows the user to connect automatically or not
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_DISABLE_OOB_SEARCHING_BOX_LABEL";

	/**
	 * Constant for the check box that determines whether or not 
	 * to send OOB searches.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	public DisableOOBSearchingPaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	public void initOptions() {
        CHECK_BOX.setSelected(SearchSettings.OOB_ENABLED.getValue());
	}

	public boolean applyOptions() throws IOException {
		SearchSettings.OOB_ENABLED.setValue(CHECK_BOX.isSelected());
        return false;
	}

    public boolean isDirty() {
        return SearchSettings.OOB_ENABLED.getValue() != CHECK_BOX.isSelected();
    }
}
