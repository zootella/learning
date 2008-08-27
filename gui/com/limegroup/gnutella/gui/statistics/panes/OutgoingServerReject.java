package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HandshakingStat;

/**
 * Displays statistics for outgoing server rejections.
 */
public class OutgoingServerReject extends AbstractMessageGraphPaneItem {

    /**
     * Creates a new statistics pane for outgoing server rejections.
     * 
     * @param key the key for obtaining locale-specific strings for this
     *  set of statistics.
     */
    public OutgoingServerReject(String key) {
        super(key);
        registerStatistic(HandshakingStat.OUTGOING_LIMEWIRE_ULTRAPEER_REJECT,
            GUIMediator.getStringResource("OUTGOING_LIMEWIRE_ULTRAPEER_REJECT"));
        registerStatistic(HandshakingStat.OUTGOING_LIMEWIRE_LEAF_REJECT,
            GUIMediator.getStringResource("OUTGOING_LIMEWIRE_LEAF_REJECT"));
        registerStatistic(HandshakingStat.OUTGOING_OTHER_ULTRAPEER_REJECT,
            GUIMediator.getStringResource("OUTGOING_OTHER_ULTRAPEER_REJECT"));
        registerStatistic(HandshakingStat.OUTGOING_OTHER_LEAF_REJECT,
            GUIMediator.getStringResource("OUTGOING_OTHER_LEAF_REJECT"));
    }
}
