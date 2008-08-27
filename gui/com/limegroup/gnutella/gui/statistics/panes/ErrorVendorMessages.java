package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedErrorStat;

/**
 * This class handles the display of all vendormsg errors.
 */
public final class ErrorVendorMessages extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays vendormsg errors
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public ErrorVendorMessages(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedErrorStat.ALL_VENDOR_ERRORS,
            GUIMediator.getStringResource("ERROR_ALL_VENDOR_ERRORS"));
		registerStatistic(ReceivedErrorStat.VENDOR_INVALID_ID,
            GUIMediator.getStringResource("ERROR_VENDOR_INVALID_ID"));
		registerStatistic(ReceivedErrorStat.VENDOR_INVALID_SELECTOR,
            GUIMediator.getStringResource("ERROR_VENDOR_INVALID_SELECTOR"));
		registerStatistic(ReceivedErrorStat.VENDOR_INVALID_VERSION,
            GUIMediator.getStringResource("ERROR_VENDOR_INVALID_VERSION"));
		registerStatistic(ReceivedErrorStat.VENDOR_INVALID_PAYLOAD,
            GUIMediator.getStringResource("ERROR_VENDOR_INVALID_PAYLOAD"));
		registerStatistic(ReceivedErrorStat.VENDOR_UNRECOGNIZED,
            GUIMediator.getStringResource("ERROR_VENDOR_UNRECOGNIZED"));
	}
}
