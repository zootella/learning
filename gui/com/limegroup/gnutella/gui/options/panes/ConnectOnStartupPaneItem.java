package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.ConnectionSettings;

/**
 * This class gives the user the option of whether or not to automatically
 * connect to the network when the program first starts.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class ConnectOnStartupPaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * check box that allows the user to connect automatically or not
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_CONNECT_ON_STARTUP_CHECK_BOX_LABEL";

	/**
	 * Constant for the check box that determines whether or not 
	 * to connect automatically on startup
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	public ConnectOnStartupPaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	public void initOptions() {
        CHECK_BOX.setSelected(ConnectionSettings.CONNECT_ON_STARTUP.getValue());
	}

	public boolean applyOptions() throws IOException {
		ConnectionSettings.CONNECT_ON_STARTUP.setValue(CHECK_BOX.isSelected());
        return false;
	}
	
	public boolean isDirty() {
	    return ConnectionSettings.CONNECT_ON_STARTUP.getValue() != CHECK_BOX.isSelected();
    }
}
