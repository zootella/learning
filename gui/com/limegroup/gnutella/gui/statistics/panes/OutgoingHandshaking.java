package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HandshakingStat;

/**
 * Displays statistics for outgoing handshakes.
 */
public class OutgoingHandshaking extends AbstractMessageGraphPaneItem {

    /**
     * Creates a new statistics pane for outgoing handshakes.
     * 
     * @param key the key for obtaining locale-specific strings for this
     *  set of statistics.
     */
    public OutgoingHandshaking(String key) {
        super(key);
        registerStatistic(HandshakingStat.OUTGOING_BAD_CONNECT,
            GUIMediator.getStringResource("OUTGOING_BAD_CONNECT"));
        registerStatistic(HandshakingStat.OUTGOING_SERVER_REJECT,
            GUIMediator.getStringResource("OUTGOING_SERVER_REJECT"));
        registerStatistic(HandshakingStat.OUTGOING_SERVER_UNKNOWN,
            GUIMediator.getStringResource("OUTGOING_SERVER_UNKNOWN"));
        registerStatistic(HandshakingStat.OUTGOING_CLIENT_REJECT,
            GUIMediator.getStringResource("OUTGOING_CLIENT_REJECT"));
        registerStatistic(HandshakingStat.OUTGOING_CLIENT_UNKNOWN,
            GUIMediator.getStringResource("OUTGOING_CLIENT_UNKNOWN"));
        registerStatistic(HandshakingStat.SUCCESSFUL_OUTGOING,
            GUIMediator.getStringResource("SUCCESSFUL_OUTGOING"));
    }
}
