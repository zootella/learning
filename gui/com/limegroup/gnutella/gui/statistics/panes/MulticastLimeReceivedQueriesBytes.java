package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.LimeReceivedMessageStatBytes;
import com.limegroup.gnutella.statistics.ReceivedMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of queries 
 * passed for LimeWire vs. other Gnutella clients.
 */
public final class MulticastLimeReceivedQueriesBytes extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of queries passed for all clients vs. the number for
	 * only LimeWire.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public MulticastLimeReceivedQueriesBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatBytes.MULTICAST_QUERY_REQUESTS,
						  GUIMediator.getStringResource("STATS_ALL_CLIENTS"));  
		registerStatistic(LimeReceivedMessageStatBytes.MULTICAST_QUERY_REQUESTS,
		    GUIMediator.getStringResource("STATS_LIMEWIRE"));  
	}
}
