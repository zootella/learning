package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.CompressionStat;

/**
 * This class handles the display of all compressable upstream bandwidth, in 
 * kilobits per second.
 */
public final class CompressableDownstreamBandwidth
  extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays compressable upstream bandwidth.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public CompressableDownstreamBandwidth(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(CompressionStat.DOWNSTREAM_UNCOMPRESSED,
            GUIMediator.getStringResource("UNCOMPRESSED_DOWNSTREAM"));
		registerStatistic(CompressionStat.DOWNSTREAM_COMPRESSED,
            GUIMediator.getStringResource("COMPRESSED_DOWNSTREAM"));
        registerStatistic(CompressionStat.HTTP_UNCOMPRESSED_DOWNSTREAM,
            GUIMediator.getStringResource("HTTP_UNCOMPRESSED"));
        registerStatistic(CompressionStat.HTTP_COMPRESSED_DOWNSTREAM,
            GUIMediator.getStringResource("HTTP_COMPRESSED"));
        registerStatistic(CompressionStat.GNUTELLA_UNCOMPRESSED_DOWNSTREAM,
            GUIMediator.getStringResource("GNUTELLA_UNCOMPRESSED"));
        registerStatistic(CompressionStat.GNUTELLA_COMPRESSED_DOWNSTREAM,
            GUIMediator.getStringResource("GNUTELLA_COMPRESSED"));
	}
}
