package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatBytes;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * This class is a <tt>PaneItem</tt> for the number of queries versus duplicate
 * queries in received messages.
 */
public final class TCPDuplicateQueriesBytes extends AbstractMessageGraphPaneItem {

	// inherit doc comment
	public TCPDuplicateQueriesBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatBytes.TCP_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStatBytes.TCP_DUPLICATE_QUERIES,
		    GUIMediator.getStringResource("RECEIVED_TCP_DUPLICATE_QUERIES_BYTES"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS1.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS1"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS2.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS2"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS3.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS3"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS4.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS4"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS5.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS5"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS6.BYTE_STAT,
						  GUIMediator.getStringResource("HOPS6"));		    
	}
}
