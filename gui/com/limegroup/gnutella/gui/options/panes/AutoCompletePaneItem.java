package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.UISettings;

/**
 * Class defineing the options panel that allows the user to enable
 * or disable autocompletion of text fields.
 */
public final class AutoCompletePaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * autocompletion enabled check box label..
	 */
    private final String AUTOCOMPLETE_LABEL = 
        "OPTIONS_AUTOCOMPLETE_DISPLAY_CHECK_BOX_LABEL";
    
    /**
	 * Constant for the check box that specifies whether to enable or 
	 * disable autocompletion
	 */
    private final JCheckBox CHECK_BOX = new JCheckBox();
    
    /**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
    public AutoCompletePaneItem(final String key) {
        super(key);
        LabeledComponent c = 
            new LabeledComponent(AUTOCOMPLETE_LABEL,
                                 CHECK_BOX,
                                 LabeledComponent.LEFT_GLUE);
        
        add(c.getComponent());
    }

    /**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
    public void initOptions() {
        CHECK_BOX.setSelected(UISettings.AUTOCOMPLETE_ENABLED.getValue());
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
        UISettings.AUTOCOMPLETE_ENABLED.setValue(CHECK_BOX.isSelected());
        return false;
    }
    
    public boolean isDirty() {
        return UISettings.AUTOCOMPLETE_ENABLED.getValue() != CHECK_BOX.isSelected();   
    }    
}


