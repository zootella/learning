package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.UploadStat;

/**
 * This class is a <tt>PaneItem</tt> for upload request methods.
 */
public final class UploadRequestMethods extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays upload request methods
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UploadRequestMethods(final String key) {
		super(key);
		registerStatistic(UploadStat.SUBSEQUENT_GET,
						  GUIMediator.getStringResource("UPLOAD_STAT_GET"));
		registerStatistic(UploadStat.SUBSEQUENT_HEAD,
						  GUIMediator.getStringResource("UPLOAD_STAT_HEAD"));
		registerStatistic(UploadStat.SUBSEQUENT_UNKNOWN,
						  GUIMediator.getStringResource("UPLOAD_STAT_UNKNOWN"));
		registerStatistic(UploadStat.PUSHED_GET,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSHED_GET"));
		registerStatistic(UploadStat.PUSHED_HEAD,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSHED_HEAD"));
		registerStatistic(UploadStat.PUSHED_UNKNOWN,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSHED_UNKNOWN"));
		registerStatistic(UploadStat.PUSH_FAILED,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSH_FAILED"));
    }
}

