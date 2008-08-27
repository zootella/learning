package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the number of queries versus duplicate
 * queries in received messages.
 */
public final class MulticastDuplicateQueries extends AbstractMessageGraphPaneItem {

	// inherit doc comment
	public MulticastDuplicateQueries(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.MULTICAST_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(ReceivedMessageStat.MULTICAST_DUPLICATE_QUERIES,
		    GUIMediator.getStringResource("RECEIVED_MULTICAST_DUPLICATE_QUERIES"));
	}
}
