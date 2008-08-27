package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedWholeNumberField;
import com.limegroup.gnutella.gui.WholeNumberField;
import com.limegroup.gnutella.settings.ConnectionSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change the listening port.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class PortPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * label on the component that allows to user to change the setting for
	 * this <tt>PaneItem</tt>.
	 */
	private final String OPTION_LABEL = "OPTIONS_PORT_BOX_LABEL";


	/**
	 * Handle to the <tt>WholeNumberField</tt> where the user selects the
	 * time to live for outgoing searches.
	 */
	private WholeNumberField _portField;

	/**
	 * The stored value to allow rolling back changes.
	 */
	private int _port;

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public PortPaneItem(final String key) {
		super(key);
		_portField = new SizedWholeNumberField();
		LabeledComponent comp = new LabeledComponent(OPTION_LABEL, 
													 _portField,
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
		_port = ConnectionSettings.PORT.getValue();
		_portField.setValue(_port);
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
		int port = _portField.getValue();
		if(port == _port) return false;
		try {
            ConnectionSettings.PORT.setValue(port);
			RouterService.setListeningPort(port);
			_port = port;
			RouterService.addressChanged();
		} catch(IOException ioe) {
			GUIMediator.showError("ERROR_PORT_UNAVAILABLE");
			ConnectionSettings.PORT.setValue(_port);
			_portField.setValue(_port);
			throw new IOException("port not available");
		} catch(IllegalArgumentException iae) {
			GUIMediator.showError("ERROR_PORT_RANGE");
			_portField.setValue(_port);
			throw new IOException("invalid port");
		}
        return false;
	}
	
	public boolean isDirty() {
	    return ConnectionSettings.PORT.getValue() != _portField.getValue();
    }
}


