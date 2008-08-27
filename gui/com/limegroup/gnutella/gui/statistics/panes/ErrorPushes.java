package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all push errors.
 */
public final class ErrorPushes extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays push errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorPushes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_PUSH_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_PUSH_ERRORS"));
		registerStatistic(ReceivedErrorStat.PUSH_INVALID_PORT,
            GUIMediator.getStringResource("ERROR_PUSH_INVALID_PORT"));
		registerStatistic(ReceivedErrorStat.PUSH_INVALID_ADDRESS,
            GUIMediator.getStringResource("ERROR_PUSH_INVALID_ADDRESS"));
		registerStatistic(ReceivedErrorStat.PUSH_INVALID_PAYLOAD,
            GUIMediator.getStringResource("ERROR_PUSH_INVALID_PAYLOAD"));
	}
}
