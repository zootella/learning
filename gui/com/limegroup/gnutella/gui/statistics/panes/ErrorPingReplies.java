package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all ping reply errors.
 */
public final class ErrorPingReplies extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays ping reply errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorPingReplies(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_PING_REPLY_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_PING_REPLY_ERRORS"));
		registerStatistic(ReceivedErrorStat.PING_REPLY_INVALID_PAYLOAD,
            GUIMediator.getStringResource("ERROR_PING_REPLY_INVALID_PAYLOAD"));
		registerStatistic(ReceivedErrorStat.PING_REPLY_INVALID_PORT,
            GUIMediator.getStringResource("ERROR_PING_REPLY_INVALID_PORT"));
		registerStatistic(ReceivedErrorStat.PING_REPLY_INVALID_ADDRESS,
            GUIMediator.getStringResource("ERROR_PING_REPLY_INVALID_ADDRESS"));            
		registerStatistic(ReceivedErrorStat.PING_REPLY_INVALID_GGEP,
            GUIMediator.getStringResource("ERROR_PING_REPLY_INVALID_GGEP"));
		registerStatistic(ReceivedErrorStat.PING_REPLY_INVALID_VENDOR,
            GUIMediator.getStringResource("ERROR_PING_REPLY_INVALID_VENDOR"));            
	}
}
