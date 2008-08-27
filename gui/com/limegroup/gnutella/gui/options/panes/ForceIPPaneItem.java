
// Edited for the Learning branch

package com.limegroup.gnutella.gui.options.panes;

import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.SizedWholeNumberField;
import com.limegroup.gnutella.gui.WholeNumberField;
import com.limegroup.gnutella.settings.ConnectionSettings;
import com.limegroup.gnutella.util.NetworkUtils;

/**
 * This class defines the panel in the options window that allows the user
 * to force their ip address to the specified value.
 */
public final class ForceIPPaneItem extends AbstractPaneItem {

	/**
	 * Constant <tt>WholeNumberField</tt> instance that holds the port 
	 * to force to.
	 */
	private final WholeNumberField PORT_FIELD = new SizedWholeNumberField();
	
    /**
     * Constant handle to the check box that enables or disables this feature.
     */
    private final ButtonGroup BUTTONS = new ButtonGroup();
    private final JRadioButton UPNP =
        new JRadioButton(GUIMediator.getStringResource("OPTIONS_ROUTER_UPNP"));
    private final JRadioButton PORT =
        new JRadioButton(GUIMediator.getStringResource("OPTIONS_ROUTER_PORT"));
    private final JRadioButton NONE =
        new JRadioButton(GUIMediator.getStringResource("OPTIONS_ROUTER_NOTHING"));

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public ForceIPPaneItem(final String key) {
		super(key);
		
		BUTTONS.add(UPNP);
		BUTTONS.add(PORT);
		BUTTONS.add(NONE);
		PORT.addItemListener(new LocalPortListener());
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 0, 6);
		panel.add(UPNP, c);
		c.gridwidth = GridBagConstraints.RELATIVE;
		panel.add(PORT, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(PORT_FIELD, c);
		panel.add(NONE, c);
		
		add(GUIUtils.left(panel));
	}
	
	private void updateState() {
	    PORT_FIELD.setEnabled(PORT.isSelected());
        PORT_FIELD.setEditable(PORT.isSelected());
    }

    /** 
	 * Listener class that responds to the checking and the 
	 * unchecking of the check box specifying whether or not to 
	 * use a local ip configuration.  It makes the other fields 
	 * editable or not editable depending on the state of the
	 * check box.
	 */
    private class LocalPortListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            updateState();
        }
    }

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
	    if(ConnectionSettings.FORCE_IP_ADDRESS.getValue() && 
	      !ConnectionSettings.UPNP_IN_USE.getValue())
	        PORT.setSelected(true);
	    else if(ConnectionSettings.DISABLE_UPNP.getValue())
	        NONE.setSelected(true);
	    else
	        UPNP.setSelected(true);
	        
        PORT_FIELD.setValue(ConnectionSettings.FORCED_PORT.getValue());
        
		updateState();
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

		// Save values from settings before this method changes them
	    boolean oldUPNP  = ConnectionSettings.UPNP_IN_USE.getValue();      // The program has set up a UPnP mapping right now that it needs to remove on shutdown
        int     oldPort  = ConnectionSettings.FORCED_PORT.getValue();      // The program has chosen a port number to forward and listen on, and it's saved in settings
        boolean oldForce = ConnectionSettings.FORCE_IP_ADDRESS.getValue(); // A remote computer told us our Internet IP, and it's saved in settings

        // The user chose "Use UPnP"
	    if (UPNP.isSelected()) {

	    	// If there's no If we don't have a mapping on the NAT right now, don't use the IP address we saved in settings
	        if (!ConnectionSettings.UPNP_IN_USE.getValue()) ConnectionSettings.FORCE_IP_ADDRESS.setValue(false);

	        // Allow the program to try UPnP
	        ConnectionSettings.DISABLE_UPNP.setValue(false);

        // The user chose "Do Nothing"
        } else if (NONE.isSelected()) {
        	
        	// There's no IP address in settings, and the program can't try UPnP
            ConnectionSettings.FORCE_IP_ADDRESS.setValue(false);
            ConnectionSettings.DISABLE_UPNP.setValue(true);

        // The user chose "Manual Port Forward"
        } else { // PORT.isSelected() is true
        	
        	// Get the port number the user typed in "Manual Port Forward"
            int forcedPort = PORT_FIELD.getValue();
            if (!NetworkUtils.isValidPort(forcedPort)) {
            	
            	// The user typed 0 or higher than 65535
                GUIMediator.showError("ERROR_FORCE_IP_PORT_RANGE");
                throw new IOException("bad port: " + forcedPort);
            }

            // Manual port forwarding means no trying UPnP and our IP address and port number are saved in settings
            ConnectionSettings.DISABLE_UPNP.setValue(false);     // Let the program try UPnP
            // TODO:kfaaborg Shouldn't this be set to true? Don't we want UPnP disabled if the user selected manual port forwarding?
            ConnectionSettings.FORCE_IP_ADDRESS.setValue(true);  // Our IP address is saved in settings
            ConnectionSettings.UPNP_IN_USE.setValue(false);      // There isn't anything to clean up in the NAT right now
            ConnectionSettings.FORCED_PORT.setValue(forcedPort); // Save the "Manual Port Forward" in settings
        }

	    // See what the values in settings are now
        boolean newForce = ConnectionSettings.FORCE_IP_ADDRESS.getValue();
        int     newPort  = ConnectionSettings.FORCED_PORT.getValue();

        /*
         * Notify the router service that our address changed if:
         * (1) The forced address status changed, meaning we used to have our IP address stored in settings but should ignore that now, or we just got it there.
         * (2) Our IP address is in settings, and our port number changed.
         */

        // We've changed our record of whether or not our IP address is stored in settings, or, our IP address is in settings and we chose a different port number 
        if (oldForce != newForce || (newForce && (oldPort != newPort))) RouterService.addressChanged(); // Tell the router service our address changed
        
        return false;
    }
    
    public boolean isDirty() {
		
		if(ConnectionSettings.FORCE_IP_ADDRESS.getValue() && 
				!ConnectionSettings.UPNP_IN_USE.getValue()) {
			if (!PORT.isSelected()) {
				return true;
			}
		}
		else if(ConnectionSettings.DISABLE_UPNP.getValue()) {
			if (!NONE.isSelected()) {
				return true;
			}
		}
		else {
			if (!UPNP.isSelected()) {
				return true;
			}
		}
		return PORT.isSelected() 
			&& PORT_FIELD.getValue() != ConnectionSettings.FORCED_PORT.getValue();
    }
}
