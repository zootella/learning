package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * This class is a <tt>PaneItem</tt> for query hit ttls.
 */
public final class HitTTLBytes extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays query hit ttls.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public HitTTLBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL0.BYTE_STAT,
						  GUIMediator.getStringResource("TTL0"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL1.BYTE_STAT,
						  GUIMediator.getStringResource("TTL1"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL2.BYTE_STAT,
						  GUIMediator.getStringResource("TTL2"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL3.BYTE_STAT,
						  GUIMediator.getStringResource("TTL3"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL4.BYTE_STAT,
						  GUIMediator.getStringResource("TTL4"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL5.BYTE_STAT,
						  GUIMediator.getStringResource("TTL5"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL6.BYTE_STAT,
						  GUIMediator.getStringResource("TTL6"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL7.BYTE_STAT,
						  GUIMediator.getStringResource("TTL7"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.TTL8.BYTE_STAT,
						  GUIMediator.getStringResource("TTL8"));
	}
}
