package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for query hops.
 */
public final class SpecialQuery extends AbstractMessageGraphPaneItem {
   
	/**
	 * Constructs a new statistics window that displays query hops.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public SpecialQuery(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.WHAT_IS_NEW_QUERY_MESSAGES,
						  GUIMediator.getStringResource("WHAT_IS_NEW_QUERIES"));
	}
}
