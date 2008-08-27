package com.limegroup.gnutella.gui.options.panes;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.gui.SizedWholeNumberField;
import com.limegroup.gnutella.gui.WholeNumberField;
import com.limegroup.gnutella.settings.ConnectionSettings;

/**
 * This class defines the panel in the options window that allows the user to
 * select a proxy to use.
 */
public final class ProxyPaneItem extends AbstractPaneItem {
    /**
     * Constant for the key of the locale-specific <tt>String</tt> for the
     * label on the RadioButtons.
     */
    private final String NO_PROXY_LABEL_KEY =
        "OPTIONS_PROXY_NO_PROXY_BUTTON_LABEL";
    private final String SOCKS4_PROXY_LABEL_KEY =
        "OPTIONS_PROXY_SOCKS4_BUTTON_LABEL";
    private final String SOCKS5_PROXY_LABEL_KEY =
        "OPTIONS_PROXY_SOCKS5_BUTTON_LABEL";
    private final String HTTP_PROXY_LABEL_KEY =
        "OPTIONS_PROXY_HTTP_BUTTON_LABEL";

    /**
     * Constant for the key of the locale-specific <tt>String</tt> for the
     * label on the proxy host field.
     */
    private final String PROXY_HOST_LABEL_KEY = "OPTIONS_PROXY_HOST_LABEL";

    /**
     * Constant for the key of the locale-specific <tt>String</tt> for the
     * label on the port text field.
     */
    private final String PROXY_PORT_LABEL_KEY = "OPTIONS_PROXY_PORT_LABEL";

    /**
     * Constant handle to the check box that enables or disables this feature.
     */
    private final ButtonGroup BUTTONS = new ButtonGroup();
    private final JRadioButton NO_PROXY_BUTTON =
        new JRadioButton(GUIMediator.getStringResource(NO_PROXY_LABEL_KEY));
    private final JRadioButton SOCKS4_PROXY_BUTTON =
        new JRadioButton(GUIMediator.getStringResource(SOCKS4_PROXY_LABEL_KEY));
    private final JRadioButton SOCKS5_PROXY_BUTTON =
        new JRadioButton(GUIMediator.getStringResource(SOCKS5_PROXY_LABEL_KEY));
    private final JRadioButton HTTP_PROXY_BUTTON =
        new JRadioButton(GUIMediator.getStringResource(HTTP_PROXY_LABEL_KEY));

    /**
     * Constant <tt>JTextField</tt> instance that holds the ip address to use
     * as a proxy.
     */
    private final JTextField PROXY_HOST_FIELD =
        new SizedTextField(12, new Dimension(60, 20));

    /**
     * Constant <tt>WholeNumberField</tt> instance that holds the port of the
     * proxy.
     */
    private final WholeNumberField PROXY_PORT_FIELD =
        new SizedWholeNumberField();

    /**
     * The constructor constructs all of the elements of this <tt>AbstractPaneItem</tt>.
     * 
     * @param key
     *            the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public ProxyPaneItem(final String key) {
        super(key);

        BUTTONS.add(NO_PROXY_BUTTON);
        BUTTONS.add(SOCKS4_PROXY_BUTTON);
        BUTTONS.add(SOCKS5_PROXY_BUTTON);
        BUTTONS.add(HTTP_PROXY_BUTTON);

        NO_PROXY_BUTTON.addItemListener(new LocalProxyListener());

        // set the minimum size of the text field to control
        // it more precisely
        PROXY_HOST_FIELD.setMinimumSize(new Dimension(60, 20));

        LabeledComponent comp1 =
            new LabeledComponent(PROXY_HOST_LABEL_KEY, PROXY_HOST_FIELD);

        LabeledComponent comp2 =
            new LabeledComponent(PROXY_PORT_LABEL_KEY, PROXY_PORT_FIELD);

        BoxPanel bp0 = new BoxPanel(BoxPanel.X_AXIS);
        BoxPanel bp0a = new BoxPanel(BoxPanel.Y_AXIS);
        bp0a.add(NO_PROXY_BUTTON);
        bp0a.add(SOCKS4_PROXY_BUTTON);
        bp0a.add(SOCKS5_PROXY_BUTTON);
        bp0a.add(HTTP_PROXY_BUTTON);
        bp0.add(bp0a);
        bp0.add(Box.createHorizontalGlue());

        bp0.add(Box.createHorizontalGlue());

        BoxPanel bp1 = new BoxPanel(BoxPanel.X_AXIS);
        bp1.add(comp1.getComponent());
        bp1.add(getHorizontalSeparator());
        bp1.add(comp2.getComponent());

        add(bp0);
        add(getVerticalSeparator());
        add(bp1);
    }
    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.
     * <p>
     * 
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        String proxy = ConnectionSettings.PROXY_HOST.getValue();
        int proxyPort = ConnectionSettings.PROXY_PORT.getValue();
        int connectionMethod = ConnectionSettings.CONNECTION_METHOD.getValue();

        PROXY_PORT_FIELD.setValue(proxyPort);
        NO_PROXY_BUTTON.setSelected(
            connectionMethod == ConnectionSettings.C_NO_PROXY);
        SOCKS4_PROXY_BUTTON.setSelected(
            connectionMethod == ConnectionSettings.C_SOCKS4_PROXY);
        SOCKS5_PROXY_BUTTON.setSelected(
            connectionMethod == ConnectionSettings.C_SOCKS5_PROXY);
        HTTP_PROXY_BUTTON.setSelected(
            connectionMethod == ConnectionSettings.C_HTTP_PROXY);
        PROXY_HOST_FIELD.setText(proxy);
        updateState();
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.
     * <p>
     * 
     * Applies the options currently set in this window, displaying an error
     * message to the user if a setting could not be applied.
     * 
     * @throws IOException
     *             if the options could not be applied for some reason
     */
    public boolean applyOptions() throws IOException {
        int connectionMethod = ConnectionSettings.C_NO_PROXY;

        if (SOCKS4_PROXY_BUTTON.isSelected())
            connectionMethod = ConnectionSettings.C_SOCKS4_PROXY;
        else if (SOCKS5_PROXY_BUTTON.isSelected())
            connectionMethod = ConnectionSettings.C_SOCKS5_PROXY;
        else if (HTTP_PROXY_BUTTON.isSelected())
            connectionMethod = ConnectionSettings.C_HTTP_PROXY;

        final int proxyPort = PROXY_PORT_FIELD.getValue();
        final String proxy = PROXY_HOST_FIELD.getText();

        ConnectionSettings.PROXY_PORT.setValue(proxyPort);
        ConnectionSettings.CONNECTION_METHOD.setValue(connectionMethod);
        ConnectionSettings.PROXY_HOST.setValue(proxy);

        return false;
    }
    
    public boolean isDirty() {
        if(ConnectionSettings.PROXY_PORT.getValue() !=  PROXY_PORT_FIELD.getValue())
            return true;
        if(!ConnectionSettings.PROXY_HOST.getValue().equals(PROXY_HOST_FIELD.getText()))
            return true;
        switch(ConnectionSettings.CONNECTION_METHOD.getValue()) {
        case ConnectionSettings.C_SOCKS4_PROXY:
            return !SOCKS4_PROXY_BUTTON.isSelected();
        case ConnectionSettings.C_SOCKS5_PROXY:
            return !SOCKS5_PROXY_BUTTON.isSelected();
        case ConnectionSettings.C_HTTP_PROXY:
            return !HTTP_PROXY_BUTTON.isSelected();
        case ConnectionSettings.C_NO_PROXY:
            return !NO_PROXY_BUTTON.isSelected();
        default:
            return true;
        }
    }
    
    private void updateState() {
        PROXY_HOST_FIELD.setEditable(!NO_PROXY_BUTTON.isSelected());
        PROXY_PORT_FIELD.setEditable(!NO_PROXY_BUTTON.isSelected());
        PROXY_HOST_FIELD.setEnabled(!NO_PROXY_BUTTON.isSelected());
        PROXY_PORT_FIELD.setEnabled(!NO_PROXY_BUTTON.isSelected());
    }

    /**
     * Listener class that responds to the checking and the unchecking of the
     * RadioButton specifying whether or not to use a proxy configuration. It
     * makes the other fields editable or not editable depending on the state
     * of the JRadioButton.
     */
    private class LocalProxyListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            updateState();
        }
    }
}
