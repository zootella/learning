package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of Multicast messages 
 * passed.
 */
public final class MulticastMessagesReceived extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public MulticastMessagesReceived(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.MULTICAST_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_QUERY_REPLIES,
			GUIMediator.getStringResource("GENERAL_QUERY_REPLY_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_PING_REQUESTS,
			GUIMediator.getStringResource("GENERAL_PING_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_PING_REPLIES,
			GUIMediator.getStringResource("GENERAL_PING_REPLY_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_PUSH_REQUESTS,
			GUIMediator.getStringResource("GENERAL_PUSH_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_ROUTE_TABLE_MESSAGES,
			GUIMediator.getStringResource("GENERAL_ROUTE_TABLE_LABEL"));
	}
}
