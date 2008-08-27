package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.HTTPStat;

/**
 * This class is a <tt>PaneItem</tt> for the total number of HTTP
 * requests.
 */
public final class HTTPRequestsPaneItem extends AbstractMessageGraphPaneItem {
	
	/**
	 * Constructs a new statistics window that displays the total
	 * number of messages passed.
	 *
	 * @param key the key for obtaining display strings for this
	 *  <tt>PaneItem</tt>, including the strings for the x and y
	 *  axis labels, the statistic description, etc
	 */
	public HTTPRequestsPaneItem(final String key) {
		super(key);
		registerStatistic(HTTPStat.HTTP_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_REQUESTS"));
		registerStatistic(HTTPStat.GET_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_GET_REQUESTS"));
		registerStatistic(HTTPStat.HEAD_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_HEAD_REQUESTS"));
        registerStatistic(HTTPStat.GIV_REQUESTS,
                          GUIMediator.getStringResource("ALL_HTTP_GIV_REQUESTS"));
		registerStatistic(HTTPStat.GNUTELLA_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_GNUTELLA_REQUESTS"));
		registerStatistic(HTTPStat.GNUTELLA_LIMEWIRE_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_GNUTELLA_LIMEWIRE_REQUESTS"));
        registerStatistic(HTTPStat.CHAT_REQUESTS,
                          GUIMediator.getStringResource("ALL_HTTP_CHAT_REQUESTS"));
		registerStatistic(HTTPStat.MAGNET_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_MAGNET_REQUESTS"));
        registerStatistic(HTTPStat.UNKNOWN_REQUESTS,
                          GUIMediator.getStringResource("ALL_HTTP_UNKNOWN_REQUESTS"));
		registerStatistic(HTTPStat.BANNED_REQUESTS,
						  GUIMediator.getStringResource("ALL_HTTP_BANNED_REQUESTS"));
        registerStatistic(HTTPStat.CLOSED_REQUESTS,
                          GUIMediator.getStringResource("ALL_HTTP_CLOSED_REQUESTS"));
	}
}
