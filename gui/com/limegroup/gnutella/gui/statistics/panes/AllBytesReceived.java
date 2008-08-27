package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of bytes
 * from Gnutella messages received over TCP, UDP & Multicast.
 */
public final class AllBytesReceived extends AbstractMessageGraphPaneItem {

	/**
	 * Constructs a new statistics window that displays the total
	 * number of TCP, UDP & Multicast messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public AllBytesReceived(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatBytes.TCP_ALL_MESSAGES,
		    GUIMediator.getStringResource("RECEIVED_ALL_TCP_MESSAGES_BYTES"));		
		registerStatistic(ReceivedMessageStatBytes.UDP_ALL_MESSAGES,
			GUIMediator.getStringResource("RECEIVED_ALL_UDP_MESSAGES_BYTES"));
		registerStatistic(ReceivedMessageStatBytes.MULTICAST_ALL_MESSAGES,
			GUIMediator.getStringResource("RECEIVED_ALL_MULTICAST_MESSAGES_BYTES"));
	}
}
