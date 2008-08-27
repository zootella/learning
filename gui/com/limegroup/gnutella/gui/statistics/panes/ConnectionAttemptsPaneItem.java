package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ConnectionStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of Gnutella
 * connection attempts made.
 */
public final class ConnectionAttemptsPaneItem extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public ConnectionAttemptsPaneItem(final String key) {
		super(key);
		registerStatistic(ConnectionStat.ALL_CONNECTION_ATTEMPTS,
						  GUIMediator.getStringResource("ALL_CONNECTION_ATTEMPTS"));

		registerStatistic(ConnectionStat.INCOMING_CONNECTION_ATTEMPTS,
						  GUIMediator.getStringResource("INCOMING_CONNECTION_ATTEMPTS"));
		registerStatistic(ConnectionStat.OUTGOING_CONNECTION_ATTEMPTS,
						  GUIMediator.getStringResource("OUTGOING_CONNECTION_ATTEMPTS"));
	}
}
