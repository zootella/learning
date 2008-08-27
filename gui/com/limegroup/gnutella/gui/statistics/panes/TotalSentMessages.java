package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.SentMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of messages 
 * sent.
 */
public final class TotalSentMessages extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public TotalSentMessages(final String key) {
		super(key);
		registerStatistic(SentMessageStat.ALL_MESSAGES,
						  GUIMediator.getStringResource("ALL_SENT"));   		
	}
}
