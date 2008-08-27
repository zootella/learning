package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.ConnectionSettings;

/**
 * option pane for turning on/off locale preferencing and
 * choosing the number of connections to preference
 */
public final class LocalePreferencingPaneItem extends AbstractPaneItem {

    
    private final String LOCALE_PREF_LABEL_CHECK_BOX = 
        "OPTIONS_CONNECT_LOCALE_PREF_DISPLAY_CHECK_BOX_LABEL";

    private final JCheckBox CHECK_BOX = new JCheckBox();


    public LocalePreferencingPaneItem(final String key) {
        super(key);

        LabeledComponent c = 
            new LabeledComponent(LOCALE_PREF_LABEL_CHECK_BOX,
                                 CHECK_BOX,
                                 LabeledComponent.LEFT_GLUE);
        
        add(c.getComponent());
    }


    public void initOptions() {
        CHECK_BOX.setSelected(ConnectionSettings.USE_LOCALE_PREF.getValue());
    }

    public boolean applyOptions() throws IOException {
        ConnectionSettings.USE_LOCALE_PREF.setValue(CHECK_BOX.isSelected());
        return false;
    }
    
    public boolean isDirty() {
        return ConnectionSettings.USE_LOCALE_PREF.getValue() != CHECK_BOX.isSelected();
    }
}
