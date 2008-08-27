package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatHandler;

/**
 * Class for displaying the average size of Gnutella messages received
 * from other LimeWires.
 */
public final class TCPLimeAverageMessageSize extends AbstractPaneItem {

	public TCPLimeAverageMessageSize(String key) {
		super(key, new AveragesPainter());
		registerDualStatistic(ReceivedMessageStatHandler.TCP_PING_REQUESTS.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_PING_REQUESTS.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_PING_REQUEST_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_PING_REPLIES.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_PING_REPLIES.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_PING_REPLY_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_QUERY_REQUESTS.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_QUERY_REQUEST_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_QUERY_REPLIES.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_QUERY_REPLIES.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_QUERY_REPLY_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_PUSH_REQUESTS.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_PUSH_REQUESTS.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_PUSH_REQUEST_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_RESET_ROUTE_TABLE_MESSAGES.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_RESET_ROUTE_TABLE_MESSAGES.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_RESET_ROUTE_TABLE_LABEL"));

		registerDualStatistic(ReceivedMessageStatHandler.TCP_PATCH_ROUTE_TABLE_MESSAGES.LIME_BYTE_STAT,
		    ReceivedMessageStatHandler.TCP_PATCH_ROUTE_TABLE_MESSAGES.LIME_NUMBER_STAT,
			GUIMediator.getStringResource("GENERAL_PATCH_ROUTE_TABLE_LABEL"));
							  
	}
}
