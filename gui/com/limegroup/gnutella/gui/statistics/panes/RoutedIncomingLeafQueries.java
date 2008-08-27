package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.RoutedQueryStat;

/**
 * This clas displays the statistics for routed leaf queries.
 */
public final class RoutedIncomingLeafQueries extends AbstractMessageGraphPaneItem {

	public RoutedIncomingLeafQueries(final String key) {
		super(key);
		registerStatistic(RoutedQueryStat.LEAF_HIT,
		    GUIMediator.getStringResource("STATS_QRP_LEAF_ROUTED_QUERIES_HIT"));

		registerStatistic(RoutedQueryStat.LEAF_FALSE_POSITIVE,
	        GUIMediator.getStringResource("STATS_QRP_LEAF_ROUTED_QUERIES_MISS"));
						  
	}
}
