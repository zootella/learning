package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.RoutedQueryStat;

/**
 * This clas displays the statistics for routed Ultrapeer queries.
 */
public final class RoutedOutgoingUltrapeerQueries extends AbstractMessageGraphPaneItem {

	public RoutedOutgoingUltrapeerQueries(final String key) {
		super(key);
		registerStatistic(RoutedQueryStat.ULTRAPEER_SEND,
		    GUIMediator.getStringResource("STATS_QRP_ULTRAPEER_ROUTED_QUERIES_SENT"));

		registerStatistic(RoutedQueryStat.ULTRAPEER_DROP,
		    GUIMediator.getStringResource("STATS_QRP_ULTRAPEER_ROUTED_QUERIES_DROP"));
						  
	}
}
