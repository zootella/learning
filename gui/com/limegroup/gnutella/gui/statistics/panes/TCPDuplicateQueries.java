package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * This class is a <tt>PaneItem</tt> for the number of queries versus duplicate
 * queries in received messages.
 */
public final class TCPDuplicateQueries extends AbstractMessageGraphPaneItem {

	// inherit doc comment
	public TCPDuplicateQueries(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.TCP_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStat.TCP_DUPLICATE_QUERIES,
		    GUIMediator.getStringResource("RECEIVED_TCP_DUPLICATE_QUERIES"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS1.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS1"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS2.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS2"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS3.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS3"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS4.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS4"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS5.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS5"));
		registerStatistic(ReceivedMessageStatHandler.TCP_DUPLICATE_QUERIES.TTL_HOPS.HOPS6.NUMBER_STAT,
						  GUIMediator.getStringResource("HOPS6"));    
	}
}
