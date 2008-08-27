package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of messages 
 * passed over TCP, UDP & Multicast.
 */
public final class AllMessagesReceived extends AbstractMessageGraphPaneItem {

	/**
	 * Constructs a new statistics window that displays the total
	 * number of TCP, UDP & Multicast messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public AllMessagesReceived(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.TCP_ALL_MESSAGES,
		    GUIMediator.getStringResource("RECEIVED_ALL_TCP_MESSAGES"));		
		registerStatistic(ReceivedMessageStat.UDP_ALL_MESSAGES,
			GUIMediator.getStringResource("RECEIVED_ALL_UDP_MESSAGES"));
		registerStatistic(ReceivedMessageStat.MULTICAST_ALL_MESSAGES,
			GUIMediator.getStringResource("RECEIVED_ALL_MULTICAST_MESSAGES"));			
	}
}
