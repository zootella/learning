package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.SentMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of bytes 
 * passed.
 */
public final class TCPBytesSent extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of bytes passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public TCPBytesSent(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(SentMessageStatBytes.TCP_QUERY_REQUESTS,
		    GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_QUERY_REPLIES,
			GUIMediator.getStringResource("GENERAL_QUERY_REPLY_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_PING_REQUESTS,
			GUIMediator.getStringResource("GENERAL_PING_REQUEST_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_PING_REPLIES,
			GUIMediator.getStringResource("GENERAL_PING_REPLY_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_PUSH_REQUESTS,
			GUIMediator.getStringResource("GENERAL_PUSH_REQUEST_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_RESET_ROUTE_TABLE_MESSAGES,
			GUIMediator.getStringResource("GENERAL_RESET_ROUTE_TABLE_LABEL"));
		registerStatistic(SentMessageStatBytes.TCP_PATCH_ROUTE_TABLE_MESSAGES,
			GUIMediator.getStringResource("GENERAL_PATCH_ROUTE_TABLE_LABEL"));
	}
}
