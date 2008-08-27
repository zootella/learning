package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.DownloadStat;

/**
 * This class is a <tt>PaneItem</tt> for responses received from downloads.
 */
public final class DownloadResponses extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays responses
	 * for downloads.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public DownloadResponses(final String key) {
		super(key);
		registerStatistic(DownloadStat.RESPONSE_OK,
		                  GUIMediator.getStringResource("DOWNLOAD_RESPONSE_OK"));
		registerStatistic(DownloadStat.TAL_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_BUSY"));
        registerStatistic(DownloadStat.RNA_EXCEPTION,
                          GUIMediator.getStringResource("DOWNLOAD_RANGE_UNAVAILABLE"));
		registerStatistic(DownloadStat.NSR_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_NO_SUCH_RANGE"));
		registerStatistic(DownloadStat.FNF_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_FILE_NOT_FOUND"));
		registerStatistic(DownloadStat.NS_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_NOT_SHARING"));
		registerStatistic(DownloadStat.Q_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_QUEUED"));
		registerStatistic(DownloadStat.PRH_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_PROBLEM_READING_HEADER"));
		registerStatistic(DownloadStat.UNKNOWN_CODE_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_UNKNOWN_CODE"));
		registerStatistic(DownloadStat.CONTENT_URN_MISMATCH_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_CONTENT_URN_MISMATCH"));
		registerStatistic(DownloadStat.IO_EXCEPTION,
						  GUIMediator.getStringResource("DOWNLOAD_ERROR"));
	}
}
