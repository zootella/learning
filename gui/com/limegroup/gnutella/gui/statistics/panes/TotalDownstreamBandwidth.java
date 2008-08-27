package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.BandwidthStat;

/**
 * This class handles the display of all downstream bandwidth, in 
 * kilobits per second.
 */
public final class TotalDownstreamBandwidth extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays total downstream bandwidth.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public TotalDownstreamBandwidth(final String key) {
		super(key, GraphAxisData.createBandwidthGraphData());
		registerStatistic(BandwidthStat.DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("DOWNSTREAM_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_BODY_DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_BODY_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_HEADER_DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_HEADER_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("GNUTELLA_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_MESSAGE_DOWNSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("GNUTELLA_MESSAGE_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_HEADER_DOWNSTREAM_BANDWIDTH,
          GUIMediator.getStringResource("GNUTELLA_HEADER_BANDWIDTH"));
	}
}
