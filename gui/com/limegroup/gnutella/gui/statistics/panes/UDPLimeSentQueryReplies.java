package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.LimeSentMessageStat;
import com.limegroup.gnutella.statistics.SentMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of replies 
 * passed for LimeWire vs. other Gnutella clients.
 */
public final class UDPLimeSentQueryReplies extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of replies passed for all clients vs. the number for
	 * only LimeWire.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UDPLimeSentQueryReplies(final String key) {
		super(key);
		registerStatistic(SentMessageStat.UDP_QUERY_REPLIES,
						  GUIMediator.getStringResource("STATS_ALL_CLIENTS"));  
		registerStatistic(LimeSentMessageStat.UDP_QUERY_REPLIES,
						  GUIMediator.getStringResource("STATS_LIMEWIRE"));  
	}
}
