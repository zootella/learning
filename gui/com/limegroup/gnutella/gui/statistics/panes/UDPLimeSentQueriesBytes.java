package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.LimeSentMessageStatBytes;
import com.limegroup.gnutella.statistics.SentMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of queries 
 * passed for LimeWire vs. other Gnutella clients.
 */
public final class UDPLimeSentQueriesBytes extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of queries passed for all clients vs. the number for
	 * only LimeWire.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UDPLimeSentQueriesBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(SentMessageStatBytes.UDP_QUERY_REQUESTS,
						  GUIMediator.getStringResource("STATS_ALL_CLIENTS"));  
		registerStatistic(LimeSentMessageStatBytes.UDP_QUERY_REQUESTS,
						  GUIMediator.getStringResource("STATS_LIMEWIRE"));  
	}
}
