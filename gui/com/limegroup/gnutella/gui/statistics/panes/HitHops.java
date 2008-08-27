package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * This class is a <tt>PaneItem</tt> for query hit hops.
 */
public final class HitHops extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays query hit hops.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public HitHops(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS1.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS1"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS2.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS2"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS3.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS3"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS4.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS4"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS5.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS5"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS6.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS6"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS7.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS7"));
		registerStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.TTL_HOPS.HOPS8.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS8"));
	}
}
