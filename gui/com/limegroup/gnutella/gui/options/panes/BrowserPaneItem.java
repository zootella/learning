/*
 * ShutdownPaneItem.java
 *
 * Created on March 11, 2002
 */

package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JTextField;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.settings.URLHandlerSettings;

/**
 * This class defines the panel in the options
 * window that allows the user to select the
 * default browser behavior.
 */
public class BrowserPaneItem extends AbstractPaneItem { 

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * label on the component that allows to user to change the setting for
	 * this <tt>PaneItem</tt>.
	 */
	private final String OPTION_LABEL = "OPTIONS_BROWSER_BOX_LABEL";
    
    /** 
     * Handle to the <tt>JTextField</tt> that displays the browser name
     */    
    private JTextField _browserField;
    
    /** Creates new BrowserOptionsPaneItem
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that 
     *      the superclass uses to generate locale-specific keys
     */
    public BrowserPaneItem(final String key) {
        super(key);
        _browserField = new SizedTextField();
        LabeledComponent comp = 
            new LabeledComponent( OPTION_LABEL, _browserField,
                                  LabeledComponent.TOP_LEFT);
		add(comp.getComponent());
	}

    /**
     * Applies the options currently set in this <tt>PaneItem</tt>.
     *
     * @throws IOException if the options could not be fully applied
     */
    public boolean applyOptions() throws IOException {
        URLHandlerSettings.BROWSER.setValue(_browserField.getText());
        return false;
    }

    public boolean isDirty() {
        return !URLHandlerSettings.BROWSER.getValue().equals(_browserField.getText());
    }
    
    /**
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        _browserField.setText(URLHandlerSettings.BROWSER.getValue());
    }
    
}
