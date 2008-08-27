package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.DroppedSentMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for flow control stats.
 */
public final class FlowControlBytes extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays flow control
	 * stats.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public FlowControlBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
        registerStatistic(DroppedSentMessageStatBytes.ALL_MESSAGES,
            GUIMediator.getStringResource("GENERAL_ALL_MESSAGES_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_QUERY_REQUESTS,
            GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_QUERY_REPLIES,
            GUIMediator.getStringResource("GENERAL_QUERY_REPLY_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_PING_REQUESTS,
            GUIMediator.getStringResource("GENERAL_PING_REQUEST_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_PING_REPLIES,
            GUIMediator.getStringResource("GENERAL_PING_REPLY_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_PUSH_REQUESTS,
            GUIMediator.getStringResource("GENERAL_PUSH_REQUEST_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_RESET_ROUTE_TABLE_MESSAGES,
            GUIMediator.getStringResource("GENERAL_RESET_ROUTE_TABLE_LABEL"));
        registerStatistic(DroppedSentMessageStatBytes.TCP_PATCH_ROUTE_TABLE_MESSAGES,
            GUIMediator.getStringResource("GENERAL_PATCH_ROUTE_TABLE_LABEL"));

	}
}
