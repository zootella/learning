package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HandshakingStat;

/**
 * This class handles the display of all ultrapeer handshaking info.
 */
public final class HandshakingUltrapeer extends AbstractMessageGraphPaneItem {
	
	/**
	 * Creates a new graph that displays leaf handshake info.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public HandshakingUltrapeer(final String key) {
		super(key);
		registerStatistic(HandshakingStat.UP_OUTGOING_REJECT_FULL,
            GUIMediator.getStringResource("UP_OUTGOING_REJECT_FULL"));
        registerStatistic(HandshakingStat.UP_OUTGOING_GUIDANCE_FOLLOWED,
            GUIMediator.getStringResource("UP_OUTGOING_GUIDANCE_FOLLOWED"));
        registerStatistic(HandshakingStat.UP_OUTGOING_GUIDANCE_IGNORED,
            GUIMediator.getStringResource("UP_OUTGOING_GUIDANCE_IGNORED"));
        registerStatistic(HandshakingStat.UP_OUTGOING_ACCEPT,
            GUIMediator.getStringResource("UP_OUTGOING_ACCEPT"));
		registerStatistic(HandshakingStat.UP_INCOMING_REJECT_LEAF,
            GUIMediator.getStringResource("UP_INCOMING_REJECT_LEAF"));
        registerStatistic(HandshakingStat.UP_INCOMING_ACCEPT_LEAF,
            GUIMediator.getStringResource("UP_INCOMING_ACCEPT_LEAF"));
        registerStatistic(HandshakingStat.UP_INCOMING_GUIDED,
            GUIMediator.getStringResource("UP_INCOMING_GUIDED"));
        registerStatistic(HandshakingStat.UP_INCOMING_ACCEPT_UP,
            GUIMediator.getStringResource("UP_INCOMING_ACCEPT_UP"));
       registerStatistic(HandshakingStat.UP_INCOMING_REJECT_NO_ROOM_LEAF,
            GUIMediator.getStringResource("UP_INCOMING_REJECT_NO_ROOM_LEAF"));
        registerStatistic(HandshakingStat.UP_INCOMING_REJECT_NO_ROOM_UP,
            GUIMediator.getStringResource("UP_INCOMING_REJECT_NO_ROOM_UP"));
	}
}
