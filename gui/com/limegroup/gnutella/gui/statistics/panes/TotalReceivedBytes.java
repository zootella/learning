package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.ReceivedMessageStatBytes;

/**
 * This class is a <tt>PaneItem</tt> for the total number of bytes 
 * received for all Gnutella messages.
 */
public final class TotalReceivedBytes extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public TotalReceivedBytes(final String key) {
		super(key, GraphAxisData.createKilobyteGraphData());
		registerStatistic(ReceivedMessageStatBytes.ALL_MESSAGES,
						  GUIMediator.getStringResource("ALL_RECEIVED_BYTES"));   		
	}
}
