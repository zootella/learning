package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.UploadStat;

/**
 * This class is a <tt>PaneItem</tt> for general upload stats.
 */
public final class UploadGeneral extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays general upload items
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UploadGeneral(final String key) {
		super(key);
		registerStatistic(UploadStat.ATTEMPTED,
						  GUIMediator.getStringResource("UPLOAD_STAT_ATTEMPTED"));
		registerStatistic(UploadStat.COMPLETED,
						  GUIMediator.getStringResource("UPLOAD_STAT_COMPLETED"));
		registerStatistic(UploadStat.INTERRUPTED,
						  GUIMediator.getStringResource("UPLOAD_STAT_INTERRUPTED"));
        registerStatistic(UploadStat.STALLED,
                          GUIMediator.getStringResource("UPLOAD_STAT_STALLED"));
        registerStatistic(UploadStat.COMPLETED_FILE,
                          GUIMediator.getStringResource("UPLOAD_STAT_COMPLETED_FILE"));
        registerStatistic(UploadStat.FW_FW_SUCCESS,
                          GUIMediator.getStringResource("UPLOAD_FW_FW_SUCCESS"));
        registerStatistic(UploadStat.FW_FW_FAILURE,
                          GUIMediator.getStringResource("UPLOAD_FW_FW_FAILURE"));                          
    }
}
