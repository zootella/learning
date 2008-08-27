package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.UploadStat;

/**
 * This class is a <tt>PaneItem</tt> for upload requests.
 */
public final class UploadRequests extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays upload requests
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UploadRequests(final String key) {
		super(key);
		registerStatistic(UploadStat.BROWSE_HOST,
						  GUIMediator.getStringResource("UPLOAD_STAT_BROWSE_HOST"));
		registerStatistic(UploadStat.PUSH_PROXY,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSH_PROXY"));
		registerStatistic(UploadStat.UPDATE_FILE,
						  GUIMediator.getStringResource("UPLOAD_STAT_UPDATE_FILE"));
		registerStatistic(UploadStat.TRADITIONAL_GET,
						  GUIMediator.getStringResource("UPLOAD_STAT_TRADITIONAL_GET"));
		registerStatistic(UploadStat.UNKNOWN_URN_GET,
						  GUIMediator.getStringResource("UPLOAD_STAT_UNKNOWN_URN_GET"));
		registerStatistic(UploadStat.URN_GET,
						  GUIMediator.getStringResource("UPLOAD_STAT_URN_GET"));
		registerStatistic(UploadStat.MALFORMED_REQUEST,
						  GUIMediator.getStringResource("UPLOAD_STAT_MALFORMED_REQUEST"));
    }
}

