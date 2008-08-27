package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.limegroup.gnutella.gui.DaapManager;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedPasswordField;
import com.limegroup.gnutella.settings.DaapSettings;

public final class DaapPasswordPaneItem extends AbstractPaneItem {

    
    private final String CHECK_BOX_LABEL = 
            "OPTIONS_ITUNES_DAAP_PASSWORD_CHECKBOX_LABEL";

    private final String TEXTFIELD_BOX_LABEL = 
            "OPTIONS_ITUNES_DAAP_PASSWORD_TEXTFIELD_LABEL";

    /**
     * Constant for the check box that specifies whether or not downloads 
     * should be automatically cleared.
     */
    private final JCheckBox CHECK_BOX = new JCheckBox();

    private final JTextField TEXT_FIELD = new SizedPasswordField();

    /**
     * The constructor constructs all of the elements of this 
     * <tt>AbstractPaneItem</tt>.
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public DaapPasswordPaneItem(final String key) {
            super(key);
            LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
                                         CHECK_BOX,
                                         LabeledComponent.LEFT_GLUE);
            add(comp.getComponent());

            comp = new LabeledComponent(TEXTFIELD_BOX_LABEL,
                                         TEXT_FIELD,
                                         LabeledComponent.RIGHT_GLUE);

            add(comp.getComponent());
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Sets the options for the fields in this <tt>PaneItem</tt> when the 
     * window is shown.
     */
    public void initOptions() {
        CHECK_BOX.setSelected(DaapSettings.DAAP_REQUIRES_PASSWORD.getValue());

        if (DaapSettings.DAAP_REQUIRES_PASSWORD.getValue())
            TEXT_FIELD.setText(DaapSettings.DAAP_PASSWORD.getValue());
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

        final boolean prevRequiresPassword = DaapSettings.DAAP_REQUIRES_PASSWORD.getValue();
        final String prevPassword = DaapSettings.DAAP_PASSWORD.getValue();
        
        final boolean requiresPassword = CHECK_BOX.isSelected();
        final String password = TEXT_FIELD.getText().trim();
        
        if (password.equals("") && requiresPassword) { 
            throw new IOException(); 
        }

        if ( ! password.equals(prevPassword))
            DaapSettings.DAAP_PASSWORD.setValue(password);

        if (requiresPassword != prevRequiresPassword || 
                (requiresPassword && !password.equals(prevPassword))) {

            DaapSettings.DAAP_REQUIRES_PASSWORD.setValue(requiresPassword);

            try {

                // A password is required now or password has changed, 
                // disconnect all users...
                if (requiresPassword) { 
                    DaapManager.instance().disconnectAll();
                }
                
                DaapManager.instance().updateService();

            } catch (IOException err) {

                DaapSettings.DAAP_REQUIRES_PASSWORD.setValue(prevRequiresPassword);
                DaapSettings.DAAP_PASSWORD.setValue(prevPassword);

                DaapManager.instance().stop();

                initOptions();

                throw err;
            }
        }

        return false;
    }

    public boolean isDirty() {
        return DaapSettings.DAAP_REQUIRES_PASSWORD.getValue() != CHECK_BOX.isSelected() ||
               !DaapSettings.DAAP_PASSWORD.getValue().equals(TEXT_FIELD.getText().trim());
                
    }    
}
