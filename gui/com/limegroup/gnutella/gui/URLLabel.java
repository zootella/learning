package com.limegroup.gnutella.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.Icon;

import java.net.URL;

import org.apache.commons.httpclient.URI;


/**
 * A label that has a URL.
 */
public class URLLabel extends JLabel {
    
    public URLLabel(URL url, String display) {
        this(url.toExternalForm(), display);
    }
    
    public URLLabel(URI uri, String display) {
        this(uri.toString(), display);
    }
    
    public URLLabel(String url) {
        this(url, url);
    }
    
    public URLLabel(final String url, String display) {
        super("<html><a href=\"" + url + "\">" + display + "</a></html");
        addMouseListener(GUIUtils.getURLInputListener(url));        
        setToolTipText(url);

        // must explicitly set the preferred size 'cause otherwise
        // components will muck up the size, looking at the string-width
        // of the text (which isn't what's shown)
        setPreferredSize(buildPreferredSize(display));
    }
    
    public URLLabel(String url, Icon icon) {
        super(icon);
        addMouseListener(GUIUtils.getURLInputListener(url));
        setToolTipText(url);
    }
    
    private Dimension buildPreferredSize(String display) {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(display) + 3, fm.getHeight());
    }
}