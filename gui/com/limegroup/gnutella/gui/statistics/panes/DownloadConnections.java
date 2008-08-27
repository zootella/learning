package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.DownloadStat;

/**
 * This class is a <tt>PaneItem</tt> for download connections.
 */
public final class DownloadConnections extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays download connections.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public DownloadConnections(final String key) {
		super(key);
		registerStatistic(DownloadStat.CONNECTION_ATTEMPTS,
		                  GUIMediator.getStringResource("DOWNLOAD_CONNECTION_ATTEMPTS"));
		registerStatistic(DownloadStat.CONNECT_DIRECT_SUCCESS,
						  GUIMediator.getStringResource("DOWNLOAD_CONNECT_DIRECT_SUCCESS"));
		registerStatistic(DownloadStat.CONNECT_DIRECT_FAILURES,
						  GUIMediator.getStringResource("DOWNLOAD_CONNECT_DIRECT_FAILURES"));
		registerStatistic(DownloadStat.CONNECT_PUSH_SUCCESS,
						  GUIMediator.getStringResource("DOWNLOAD_CONNECT_PUSH_SUCCESS"));
		registerStatistic(DownloadStat.PUSH_FAILURE_INTERRUPTED,
						  GUIMediator.getStringResource("DOWNLOAD_PUSH_FAILURE_INTERRUPTED"));
		registerStatistic(DownloadStat.PUSH_FAILURE_NO_RESPONSE,
						  GUIMediator.getStringResource("DOWNLOAD_PUSH_FAILURE_NO_RESPONSE"));
        registerStatistic(DownloadStat.PUSH_FAILURE_LOST,
                          GUIMediator.getStringResource("DOWNLOAD_PUSH_FAILURE_LOST"));
        registerStatistic(DownloadStat.FW_FW_SUCCESS,
                          GUIMediator.getStringResource("DOWNLOAD_FW_FW_SUCCESS"));
        registerStatistic(DownloadStat.FW_FW_FAILURE,
                          GUIMediator.getStringResource("DOWNLOAD_FW_FW_FAILURE"));
	}
}
