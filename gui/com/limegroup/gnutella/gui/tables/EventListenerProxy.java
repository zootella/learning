package com.limegroup.gnutella.gui.tables;

import java.util.EventListener;

/**
 * Indicates that this is a proxy for another EventListener
 */
public interface EventListenerProxy {
    
    /**
     * Determines if this is a proxy for the given listener.
     */
    public boolean isProxyFor(EventListener l);
}