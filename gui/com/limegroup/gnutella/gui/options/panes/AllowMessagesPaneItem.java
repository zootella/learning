package com.limegroup.gnutella.gui.options.panes;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.filters.IP;
import com.limegroup.gnutella.gui.ListEditor;
import com.limegroup.gnutella.settings.FilterSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to add and remove ip addresses from a list of ip addresses to block.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class AllowMessagesPaneItem extends AbstractPaneItem {

	/**
	 * Constant handle to the <tt>ListEditor</tt> that adds and removes
	 * ips to ban.
	 */
	private final ListEditor MESSAGES_LIST = new ListEditor();

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate strings
	 */
	public AllowMessagesPaneItem(final String key) {
		super(key);
		add(MESSAGES_LIST);
        MESSAGES_LIST.addListDataListener( new IPEnforcer() );		
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
		String[] allowedIps = FilterSettings.WHITE_LISTED_IP_ADDRESSES.getValue();
		MESSAGES_LIST.setModel(new Vector(Arrays.asList(allowedIps)));
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
		Vector model = MESSAGES_LIST.getModel();
		String[] allowedIps = new String[model.size()];
		model.copyInto(allowedIps);
		
        FilterSettings.WHITE_LISTED_IP_ADDRESSES.setValue(allowedIps);
		RouterService.adjustSpamFilters();
        return false;
	}
	
    public boolean isDirty() {
        List model = Arrays.asList(FilterSettings.WHITE_LISTED_IP_ADDRESSES.getValue());
        return !model.equals(MESSAGES_LIST.getModel());
    }	
	
	private class IPEnforcer implements ListDataListener {
	    public void intervalAdded(ListDataEvent lde) {
	        Vector model = (Vector)lde.getSource();
	        String ipString = (String)model.get(lde.getIndex0());
	        // Ensure that this ip can be constructed.
	        try {
	            new IP(ipString);
	        } catch(IllegalArgumentException e) {
	            // if it can't, remove it & beep.
	            MESSAGES_LIST.removeItem(lde.getIndex0());
	            Toolkit.getDefaultToolkit().beep();
	        }
	    }
	    
	    public void intervalRemoved(ListDataEvent lde) { }
        
        public void contentsChanged(ListDataEvent lde) {
            Vector model = (Vector)lde.getSource();
            String ipString = (String)model.get(lde.getIndex0());
            try {
                new IP(ipString);
            } catch(IllegalArgumentException e) {
                // if the new one can't be created, revert
                // back to old model & beep.
                String[] allowedIps =
                    FilterSettings.WHITE_LISTED_IP_ADDRESSES.getValue();
		        MESSAGES_LIST.setModel(new Vector(Arrays.asList(allowedIps)));
		        Toolkit.getDefaultToolkit().beep();
            }
        }
    }	
}
