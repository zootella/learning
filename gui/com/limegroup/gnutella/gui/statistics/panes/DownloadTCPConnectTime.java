package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.NumericalDownloadStat;

/**
 * This class is a <tt>PaneItem</tt> for download connections.
 */
public final class DownloadTCPConnectTime 
    extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays download connections.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public DownloadTCPConnectTime(final String key) {
		super(key);
		registerStatistic(NumericalDownloadStat.TCP_CONNECT_TIME,
		    GUIMediator.getStringResource("DOWNLOAD_TCP_CONNECT_TIME"));
	}
}
