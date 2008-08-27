package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.SentMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of bytes 
 * sent over TCP , UDP & Multicast.
 */
public final class AllBytesSent extends AbstractMessageGraphPaneItem {

	/**
	 * Constructs a new statistics window that displays the total
	 * number of TCP, UDP & Multicast bytes passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public AllBytesSent(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(SentMessageStatBytes.TCP_ALL_MESSAGES,
		    GUIMediator.getStringResource("SENT_ALL_TCP_MESSAGES_BYTES"));		
		registerStatistic(SentMessageStatBytes.UDP_ALL_MESSAGES,
			GUIMediator.getStringResource("SENT_ALL_UDP_MESSAGES_BYTES"));
		registerStatistic(SentMessageStatBytes.MULTICAST_ALL_MESSAGES,
			GUIMediator.getStringResource("SENT_ALL_MULTICAST_MESSAGES_BYTES"));			
	}
}
