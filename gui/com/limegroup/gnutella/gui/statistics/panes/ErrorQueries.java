package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all query errors.
 */
public final class ErrorQueries extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays query errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorQueries(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_QUERY_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_QUERY_ERRORS"));		
		registerStatistic(ReceivedErrorStat.QUERY_URN,
            GUIMediator.getStringResource("ERROR_QUERY_URN"));
		registerStatistic(ReceivedErrorStat.QUERY_TOO_LARGE,
            GUIMediator.getStringResource("ERROR_QUERY_TOO_LARGE"));
        registerStatistic(ReceivedErrorStat.QUERY_XML_TOO_LARGE,
            GUIMediator.getStringResource("ERROR_QUERY_XML_TOO_LARGE"));
        registerStatistic(ReceivedErrorStat.QUERY_EMPTY,
            GUIMediator.getStringResource("ERROR_QUERY_EMPTY"));
        registerStatistic(ReceivedErrorStat.QUERY_ILLEGAL_CHARS,
            GUIMediator.getStringResource("ERROR_QUERY_ILLEGAL_CHARS"));
	}
}
