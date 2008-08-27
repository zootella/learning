package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.BandwidthStat;

/**
 * This class handles the display of all upstream bandwidth, in 
 * kilobits per second.
 */
public final class TotalUpstreamBandwidth extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays total upstream bandwidth.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public TotalUpstreamBandwidth(final String key) {
		super(key, GraphAxisData.createBandwidthGraphData());
		registerStatistic(BandwidthStat.UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("UPSTREAM_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_BODY_UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_BODY_BANDWIDTH"));
        registerStatistic(BandwidthStat.HTTP_HEADER_UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("HTTP_HEADER_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("GNUTELLA_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_MESSAGE_UPSTREAM_BANDWIDTH,
            GUIMediator.getStringResource("GNUTELLA_MESSAGE_BANDWIDTH"));
        registerStatistic(BandwidthStat.GNUTELLA_HEADER_UPSTREAM_BANDWIDTH,
          GUIMediator.getStringResource("GNUTELLA_HEADER_BANDWIDTH"));
	}
}
