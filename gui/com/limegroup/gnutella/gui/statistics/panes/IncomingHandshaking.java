package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HandshakingStat;

/**
 * Displays statistics for incoming handshakes.
 */
public class IncomingHandshaking extends AbstractMessageGraphPaneItem {

    /**
     * Creates a new statistics pane for incoming handshakes.
     * 
     * @param key the key for obtaining locale-specific strings for this
     *  set of statistics.
     */
    public IncomingHandshaking(String key) {
        super(key);
        registerStatistic(HandshakingStat.INCOMING_BAD_CONNECT,
            GUIMediator.getStringResource("INCOMING_BAD_CONNECT"));
        registerStatistic(HandshakingStat.INCOMING_SERVER_UNKNOWN ,
            GUIMediator.getStringResource("INCOMING_SERVER_UNKNOWN"));
        registerStatistic(HandshakingStat.INCOMING_CLIENT_REJECT,
            GUIMediator.getStringResource("INCOMING_CLIENT_REJECT"));
        registerStatistic(HandshakingStat.INCOMING_CLIENT_UNKNOWN,
            GUIMediator.getStringResource("INCOMING_CLIENT_UNKNOWN"));
        registerStatistic(HandshakingStat.SUCCESSFUL_INCOMING,
            GUIMediator.getStringResource("SUCCESSFUL_INCOMING"));
        registerStatistic(HandshakingStat.INCOMING_NO_CONCLUSION,
            GUIMediator.getStringResource("INCOMING_NO_CONCLUSION"));
		registerStatistic(HandshakingStat.INCOMING_CRAWLER,
            GUIMediator.getStringResource("CRAWLER_CONNECTION"));            
    }
}