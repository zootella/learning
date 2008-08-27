package com.limegroup.gnutella.gui.tabs;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.tables.ComponentMediator;
import com.limegroup.gnutella.settings.ApplicationSettings;

/**
 * This class contains access to the connections tab properties.
 */
public final class ConnectionsTab extends AbstractTab {

	/**
	 * Constant for the <tt>Component</tt> instance containing the 
	 * elements of this tab.
	 */
	private final JComponent COMPONENT;

	/**
	 * Construcs the connections tab.
	 *
	 * @param CONNECTION_MEDIATOR the <tt>ConectionMediator</tt> instance
	 */
	public ConnectionsTab(final ComponentMediator CONNECTION_MEDIATOR) {
		super("CONNECTIONS", GUIMediator.CONNECTIONS_INDEX, "connection_tab");
		COMPONENT = CONNECTION_MEDIATOR.getComponent();
	}

	public void storeState(boolean visible) {
        ApplicationSettings.CONNECTION_VIEW_ENABLED.setValue(visible);
	}

	public JComponent getComponent() {
		return COMPONENT;
	}
}
