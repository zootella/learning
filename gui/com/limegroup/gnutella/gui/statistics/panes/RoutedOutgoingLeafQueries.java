package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.RoutedQueryStat;

/**
 * This clas displays the statistics for routed leaf queries.
 */
public final class RoutedOutgoingLeafQueries extends AbstractMessageGraphPaneItem {

	public RoutedOutgoingLeafQueries(final String key) {
		super(key);
		registerStatistic(RoutedQueryStat.LEAF_SEND,
		    GUIMediator.getStringResource("STATS_QRP_LEAF_ROUTED_QUERIES_SENT"));

		registerStatistic(RoutedQueryStat.LEAF_DROP,
	        GUIMediator.getStringResource("STATS_QRP_LEAF_ROUTED_QUERIES_DROP"));
						  
	}
}
