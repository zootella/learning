package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.DownloadStat;

/**
 * This class is a <tt>PaneItem</tt> for download transfers.
 */
public final class DownloadTransfers extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays download transfers
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public DownloadTransfers(final String key) {
		super(key);
		registerStatistic(DownloadStat.SUCCESSFUL_HTTP11,
						  GUIMediator.getStringResource("DOWNLOAD_SUCCESFULL_HTTP11"));
		registerStatistic(DownloadStat.SUCCESSFUL_HTTP10,
						  GUIMediator.getStringResource("DOWNLOAD_SUCCESFULL_HTTP10"));
		registerStatistic(DownloadStat.FAILED_HTTP11,
						  GUIMediator.getStringResource("DOWNLOAD_FAILED_HTTP11"));
		registerStatistic(DownloadStat.FAILED_HTTP10,
						  GUIMediator.getStringResource("DOWNLOAD_FAILED_HTTP10"));
        registerStatistic(DownloadStat.RETRIED_SUCCESS,
                          GUIMediator.getStringResource("DOWNLOAD_RETRIED_SUCCESS"));
    }
}
