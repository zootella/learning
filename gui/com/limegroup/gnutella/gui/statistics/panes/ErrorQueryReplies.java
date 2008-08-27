package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all queryhit errors.
 */
public final class ErrorQueryReplies extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays reply errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorQueryReplies(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_QUERY_REPLY_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_QUERY_REPLY_ERRORS"));
		registerStatistic(ReceivedErrorStat.REPLY_INVALID_PORT,
            GUIMediator.getStringResource("ERROR_REPLY_INVALID_PORT"));
		registerStatistic(ReceivedErrorStat.REPLY_INVALID_ADDRESS,
            GUIMediator.getStringResource("ERROR_REPLY_INVALID_ADDRESS"));
		registerStatistic(ReceivedErrorStat.REPLY_INVALID_SPEED,
            GUIMediator.getStringResource("ERROR_REPLY_INVALID_SPEED"));
	}
}
