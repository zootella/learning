package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.RouteErrorStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of routing errors 
 * encountered.
 */
public final class RouteErrorsPaneItem extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays the total
	 * number of route errors.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public RouteErrorsPaneItem(final String key) {
		super(key);
		//registerStatistic(SentMessageStat.ALL_ROUTE_ERRORS,
		//			  GUIMediator.getStringResource("ALL_ROUTE_ERRORS"));		
		registerStatistic(RouteErrorStat.PING_REPLY_ROUTE_ERRORS,
						  GUIMediator.getStringResource("PING_REPLY_ROUTE_ERRORS"));		
		registerStatistic(RouteErrorStat.QUERY_REPLY_ROUTE_ERRORS,
						  GUIMediator.getStringResource("QUERY_REPLY_ROUTE_ERRORS"));		
		registerStatistic(RouteErrorStat.PUSH_REQUEST_ROUTE_ERRORS,
						  GUIMediator.getStringResource("PUSH_REQUEST_ROUTE_ERRORS")); 
	}
}
