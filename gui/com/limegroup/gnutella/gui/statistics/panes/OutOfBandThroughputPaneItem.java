package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.OutOfBandThroughputStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of HTTP
 * requests.
 */
public final class OutOfBandThroughputPaneItem extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public OutOfBandThroughputPaneItem(final String key) {
		super(key);
		registerStatistic(OutOfBandThroughputStat.RESPONSES_REQUESTED,
						  GUIMediator.getStringResource("ALL_OOB_REQUESTS"));
		registerStatistic(OutOfBandThroughputStat.RESPONSES_RECEIVED,
						  GUIMediator.getStringResource("ALL_OOB_RESPONSES"));
		registerStatistic(OutOfBandThroughputStat.RESPONSES_BYPASSED,
						  GUIMediator.getStringResource("ALL_OOB_BYPASSED"));
	}
}
