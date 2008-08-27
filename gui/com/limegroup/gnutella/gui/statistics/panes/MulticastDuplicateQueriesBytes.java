package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the number of queries versus duplicate
 * queries in received messages.
 */
public final class MulticastDuplicateQueriesBytes extends AbstractMessageGraphPaneItem {

	// inherit doc comment
	public MulticastDuplicateQueriesBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatBytes.MULTICAST_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStatBytes.MULTICAST_DUPLICATE_QUERIES,
		    GUIMediator.getStringResource("RECEIVED_MULTICAST_DUPLICATE_QUERIES_BYTES"));
	}
}
