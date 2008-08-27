package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.DownloadStat;

/**
 * This class is a <tt>PaneItem</tt> for download alternate locations.
 */
public final class DownloadAlternateLocations extends AbstractMessageGraphPaneItem {
   
    /**
     * Constructs a new statistics window that displays alternate locations
     * for downloads.
     *
     * @param key the key for obtaining display strings for this
     *  <tt>PaneItem</tt>, including the strings for the x and y
     *  axis labels, the statistic description, etc
     */
    public DownloadAlternateLocations(final String key) {
        super(key);
        registerStatistic(DownloadStat.ALTERNATE_COLLECTED,
                          GUIMediator.getStringResource("DOWNLOAD_ALTERNATE_COLLECTED"));
        registerStatistic(DownloadStat.ALTERNATE_NOT_ADDED,
                          GUIMediator.getStringResource("DOWNLOAD_ALTERNATE_NOT_ADDED"));
        registerStatistic(DownloadStat.ALTERNATE_WORKED,
                          GUIMediator.getStringResource("DOWNLOAD_ALTERNATE_WORKED"));
    }
}
