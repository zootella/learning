package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedWholeNumberField;
import com.limegroup.gnutella.gui.WholeNumberField;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change time before incomplete files are purged.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class PurgeIncompletePaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * label on the component that allows to user to change the setting for
	 * this <tt>PaneItem</tt>.
	 */
	private final String OPTION_LABEL = "OPTIONS_PURGE_INCOMPLETE_TIME_NUMBER_LABEL";


	/**
	 * Handle to the <tt>WholeNumberField</tt> where the user selects the
	 * maximum number of downloads.
	 */
	private WholeNumberField _purgeIncompleteField;

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate strings
	 */
	public PurgeIncompletePaneItem(final String key) {
		super(key);

		_purgeIncompleteField = new SizedWholeNumberField();
		LabeledComponent comp = new LabeledComponent(OPTION_LABEL, 
													 _purgeIncompleteField,
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
        _purgeIncompleteField.setValue(SharingSettings.INCOMPLETE_PURGE_TIME.getValue());
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
        SharingSettings.INCOMPLETE_PURGE_TIME.setValue(_purgeIncompleteField.getValue());
        return false;
	}
	
	public boolean isDirty() {
        return SharingSettings.INCOMPLETE_PURGE_TIME.getValue() != _purgeIncompleteField.getValue();
    }
}
