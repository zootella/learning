package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.RouteErrorStat;

/**
 * This class is a <tt>PaneItem</tt> for the routing errors for query repies.
 */
public final class QueryReplyRouteErrors extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays the total
	 * number of query reply route errors.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public QueryReplyRouteErrors(final String key) {
		super(key);
		registerStatistic(RouteErrorStat.QUERY_REPLY_ROUTE_ERRORS,
		    GUIMediator.getStringResource("QUERY_REPLY_ROUTE_ERRORS"));		
        registerStatistic(RouteErrorStat.HARD_LIMIT_QUERY_REPLY_ROUTE_ERRORS,
            GUIMediator.getStringResource("HARD_LIMIT_QUERY_REPLY_ROUTE_ERRORS"));
        for (int i = 0; i < RouteErrorStat.HARD_LIMIT_QUERY_REPLY_TTL.length;
             i++)
        registerStatistic(RouteErrorStat.HARD_LIMIT_QUERY_REPLY_TTL[i],
                          GUIMediator.getStringResource("HARD_LIMIT_QR_TTL_"+i));
        registerStatistic(RouteErrorStat.NO_ROUTE_QUERY_REPLY_ROUTE_ERRORS,
            GUIMediator.getStringResource("NO_ROUTE_QUERY_REPLY_ROUTE_ERRORS"));
	}
}
