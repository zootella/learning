package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HandshakingStat;

/**
 * This class handles the display of all leaf handshaking info.
 */
public final class HandshakingLeaf extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays leaf handshake info.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public HandshakingLeaf(final String key) {
		super(key);
		registerStatistic(HandshakingStat.LEAF_OUTGOING_REJECT_LEAF,
            GUIMediator.getStringResource("LEAF_OUTGOING_REJECT_LEAF"));
		registerStatistic(HandshakingStat.LEAF_OUTGOING_REJECT_OLD_UP,
            GUIMediator.getStringResource("LEAF_OUTGOING_REJECT_OLD_UP"));
        registerStatistic(HandshakingStat.LEAF_OUTGOING_ACCEPT,
            GUIMediator.getStringResource("LEAF_OUTGOING_ACCEPT"));
        registerStatistic(HandshakingStat.LEAF_INCOMING_REJECT,
            GUIMediator.getStringResource("LEAF_INCOMING_REJECT"));
        registerStatistic(HandshakingStat.LEAF_INCOMING_ACCEPT,
            GUIMediator.getStringResource("LEAF_INCOMING_ACCEPT"));
	}
}
