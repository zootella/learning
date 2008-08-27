package com.limegroup.gnutella.gui;

import com.limegroup.gnutella.MessageResourceCallback;

/** Simply delegates calls to GUIMediator.getStringResource().
 */
public class MessageResourceHandler implements MessageResourceCallback {

    public String getHTMLPageTitle() {
        return GUIMediator.getStringResource("HTML_PAGE_TITLE");
    }

    public String getHTMLPageListingHeader() {
        return GUIMediator.getStringResource("HTML_PAGE_LISTING");
    }

    public String getHTMLPageMagnetHeader() {
        return GUIMediator.getStringResource("HTML_PAGE_MAGNET");
    }

}
