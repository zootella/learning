package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.UploadStat;

/**
 * This class is a <tt>PaneItem</tt> for upload responses.
 */
public final class UploadResponses extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays upload responses
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UploadResponses(final String key) {
		super(key);
		registerStatistic(UploadStat.LIMIT_REACHED,
						  GUIMediator.getStringResource("UPLOAD_STAT_LIMIT_REACHED"));
		registerStatistic(UploadStat.FILE_NOT_FOUND,
						  GUIMediator.getStringResource("UPLOAD_STAT_FILE_NOT_FOUND"));
        registerStatistic(UploadStat.UNAVAILABLE_RANGE,
                          GUIMediator.getStringResource("UPLOAD_STAT_UNAVAILABLE_RANGE"));
		registerStatistic(UploadStat.FREELOADER,
						  GUIMediator.getStringResource("UPLOAD_STAT_FREELOADER"));
		registerStatistic(UploadStat.QUEUED,
						  GUIMediator.getStringResource("UPLOAD_STAT_QUEUED"));
		registerStatistic(UploadStat.UPLOADING,
						  GUIMediator.getStringResource("UPLOAD_STAT_NORMAL"));
		registerStatistic(UploadStat.PUSH_PROXY_REQ_BAD,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSH_PROXY_BAD"));
		registerStatistic(UploadStat.PUSH_PROXY_REQ_SUCCESS,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSH_PROXY_SUCCESS"));
		registerStatistic(UploadStat.PUSH_PROXY_REQ_FAILED,
						  GUIMediator.getStringResource("UPLOAD_STAT_PUSH_PROXY_FAILED"));
        registerStatistic(UploadStat.LIMIT_REACHED_GREEDY,
                          GUIMediator.getStringResource("UPLOAD_STAT_LIMIT_REACHED_GREEDY"));
        registerStatistic(UploadStat.BANNED,
                          GUIMediator.getStringResource("UPLOAD_STAT_BANNED"));
        registerStatistic(UploadStat.THEX,
                          GUIMediator.getStringResource("UPLOAD_STAT_THEX"));
    }
}

