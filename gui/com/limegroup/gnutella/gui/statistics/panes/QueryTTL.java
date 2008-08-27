package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * This class is a <tt>PaneItem</tt> for query ttls.
 */
public final class QueryTTL extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays query ttls.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public QueryTTL(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL0.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL0"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL1.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL1"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL2.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL2"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL3.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL3"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL4.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL4"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL5.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL5"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL6.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL6"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL7.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL7"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.TTL_HOPS.TTL8.NUMBER_STAT,
						  GUIMediator.getStringResource("TTL8"));
	}
}
