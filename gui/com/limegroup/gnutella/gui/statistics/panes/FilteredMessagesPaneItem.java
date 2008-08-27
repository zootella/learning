package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of routing errors 
 * encountered.
 */
public final class FilteredMessagesPaneItem extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of filtered messages.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public FilteredMessagesPaneItem(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.ALL_FILTERED_MESSAGES,
						  GUIMediator.getStringResource("ALL_FILTERED_MESSAGES"));		
	}
}
