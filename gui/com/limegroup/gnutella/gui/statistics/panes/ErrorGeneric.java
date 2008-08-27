package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all generic errors.
 */
public final class ErrorGeneric extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays generic errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorGeneric(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_RECEIVED_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_RECEIVED"));
		registerStatistic(ReceivedErrorStat.CONNECTION_CLOSED,
            GUIMediator.getStringResource("ERROR_CONNECTION_CLOSED"));
        registerStatistic(ReceivedErrorStat.INVALID_LENGTH,
            GUIMediator.getStringResource("ERROR_INVALID_LENGTH"));
        registerStatistic(ReceivedErrorStat.INVALID_HOPS,
            GUIMediator.getStringResource("ERROR_INVALID_HOPS"));
        registerStatistic(ReceivedErrorStat.INVALID_TTL,
            GUIMediator.getStringResource("ERROR_INVALID_TTL"));
        registerStatistic(ReceivedErrorStat.HOPS_EXCEED_SOFT_MAX,
            GUIMediator.getStringResource("ERROR_HOPS_EXCEED_SOFT_MAX"));
        registerStatistic(ReceivedErrorStat.HOPS_AND_TTL_OVER_HARD_MAX,
            GUIMediator.getStringResource("ERROR_HOPS_AND_TTL_OVER_HARD_MAX"));
        registerStatistic(ReceivedErrorStat.INVALID_CODE,
            GUIMediator.getStringResource("ERROR_INVALID_CODE"));            
	}
}
