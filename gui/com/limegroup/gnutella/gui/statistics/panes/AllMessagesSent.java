package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.SentMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of messages 
 * sent over TCP, UDP & Multicast
 */
public final class AllMessagesSent extends AbstractMessageGraphPaneItem {

	/**
	 * Constructs a new statistics window that displays the total
	 * number of TCP , UDP & Multicast messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public AllMessagesSent(final String key) {
		super(key);
		registerStatistic(SentMessageStat.TCP_ALL_MESSAGES,
		    GUIMediator.getStringResource("SENT_ALL_TCP_MESSAGES"));		
		registerStatistic(SentMessageStat.UDP_ALL_MESSAGES,
			GUIMediator.getStringResource("SENT_ALL_UDP_MESSAGES"));	
        registerStatistic(SentMessageStat.MULTICAST_ALL_MESSAGES,
            GUIMediator.getStringResource("SENT_ALL_MULTICAST_MESSAGES"));
	}
}
