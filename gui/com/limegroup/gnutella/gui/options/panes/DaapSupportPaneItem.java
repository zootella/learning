package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.limegroup.gnutella.gui.DaapManager;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.settings.DaapSettings;

public final class DaapSupportPaneItem extends AbstractPaneItem {

    
    private final String DAAP_ENABLED_LABEL = 
            "OPTIONS_ITUNES_DAAP_SUPPORT_DAAP_ENABLED_LABEL";

    private final String USE_BIO_LABEL =
            "OPTIONS_ITUNES_DAAP_SUPPORT_USE_BIO_LABEL";
    
    private final String SERVICE_NAME_LABEL = 
            "OPTIONS_ITUNES_DAAP_SUPPORT_SERVICE_NAME_LABEL";


    private final JCheckBox DAAP_ENABLED = new JCheckBox();
    
    private final JCheckBox USE_BIO = new JCheckBox();

    private final JTextField SERVICE_NAME = new SizedTextField();

    /**
     * The constructor constructs all of the elements of this 
     * <tt>AbstractPaneItem</tt>.
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public DaapSupportPaneItem(final String key) {
        super(key);
        
        LabeledComponent comp = new LabeledComponent(DAAP_ENABLED_LABEL, DAAP_ENABLED,
            LabeledComponent.LEFT_GLUE);
        add(comp.getComponent());
        
        add(getVerticalSeparator());
        
        comp = new LabeledComponent(USE_BIO_LABEL, USE_BIO,
            LabeledComponent.LEFT_GLUE);
        add(comp.getComponent());

        add(getVerticalSeparator());
        add(getVerticalSeparator());
        
        comp = new LabeledComponent(SERVICE_NAME_LABEL, SERVICE_NAME,
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
        DAAP_ENABLED.setSelected(DaapSettings.DAAP_ENABLED.getValue() && 
                    DaapManager.instance().isServerRunning());
        
        USE_BIO.setSelected(!DaapSettings.DAAP_USE_NIO.getValue());
        
        SERVICE_NAME.setText(DaapSettings.DAAP_SERVICE_NAME.getValue());
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

        final boolean prevEnabled = DaapSettings.DAAP_ENABLED.getValue();
        final boolean prevUseNIO = DaapSettings.DAAP_USE_NIO.getValue();
        final String prevServiceName = DaapSettings.DAAP_SERVICE_NAME.getValue();

        String serviceName = SERVICE_NAME.getText().trim();

        if (serviceName.length()==0 && DAAP_ENABLED.isSelected()) { 
            throw new IOException(); 
        }

        DaapSettings.DAAP_ENABLED.setValue(DAAP_ENABLED.isSelected());
        DaapSettings.DAAP_USE_NIO.setValue(!USE_BIO.isSelected());
        DaapSettings.DAAP_SERVICE_NAME.setValue(serviceName);
        DaapSettings.DAAP_LIBRARY_NAME.setValue(serviceName);

        try {
            
            if (DAAP_ENABLED.isSelected()) {
                
                if (!prevEnabled || !USE_BIO.isSelected() != prevUseNIO) {
                    DaapManager.instance().restart();
                   
                } else if (!serviceName.equals(prevServiceName)) {
                    DaapManager.instance().updateService();
                }
                    
            } else if (prevEnabled) {
                
                DaapManager.instance().stop();
            }

        } catch (IOException err) {

            DaapSettings.DAAP_ENABLED.setValue(prevEnabled);
            DaapSettings.DAAP_USE_NIO.setValue(prevUseNIO);
            DaapSettings.DAAP_SERVICE_NAME.setValue(prevServiceName);
            DaapSettings.DAAP_LIBRARY_NAME.setValue(prevServiceName);

            DaapManager.instance().stop();

            initOptions();

            throw err;
        }

        return false;
    }
    
    public boolean isDirty() {
        return DaapSettings.DAAP_ENABLED.getValue() != DAAP_ENABLED.isSelected() ||
               DaapSettings.DAAP_USE_NIO.getValue() == USE_BIO.isSelected() ||
               !DaapSettings.DAAP_SERVICE_NAME.getValue().equals(SERVICE_NAME.getText()) ||
               !DaapSettings.DAAP_LIBRARY_NAME.getValue().equals(SERVICE_NAME.getText());
    }
}
