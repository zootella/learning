package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.LimeReceivedMessageStat;
import com.limegroup.gnutella.statistics.ReceivedMessageStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of pings 
 * passed for LimeWire vs. other Gnutella clients.
 */
public final class UDPLimeReceivedPings extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of pings passed for all clients vs. the number for
	 * only LimeWire.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public UDPLimeReceivedPings(final String key) {
		super(key);
		registerStatistic(ReceivedMessageStat.UDP_PING_REQUESTS,
						  GUIMediator.getStringResource("STATS_ALL_CLIENTS"));  
		registerStatistic(LimeReceivedMessageStat.UDP_PING_REQUESTS,
		    GUIMediator.getStringResource("STATS_LIMEWIRE"));  
	}
}
