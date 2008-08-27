package com.limegroup.gnutella.gui.tables;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

/**
 * A proxy for MouseListener & MouseMotionListener.
 * This will discard all events PRESSED or DRAGGED where the
 *  MouseEvent is already consumed,
 * prior to actually calling the event in the proxied class.
 *
 * This is useful/necessary for D&D, where JComponenets do not
 * correctly discard processed events (ultimately changing the
 * active selection in tables).
 */
public final class MouseEventConsumptionChecker {
    // No construction allowed of this class.
    private MouseEventConsumptionChecker() {}
    
    /**
     * Construct a proxy for the given MouseMotionListener.
     */ 
    public static MouseMotionListener proxy(MouseMotionListener mml) {
        return new MouseMotionProxy(mml);
    }
    
    /**
     * Construct a proxy for the given MouseListener.
     */
    public static MouseListener proxy(MouseListener ml) {
        return new MouseProxy(ml);
    }
    
    /**
     * Proxy for MouseMotionListener.
     */
    private static class MouseMotionProxy implements MouseMotionListener,
                                                     EventListenerProxy {
        private final MouseMotionListener delegate;
        MouseMotionProxy(MouseMotionListener mml) {
            delegate = mml;
        }
        
        public boolean isProxyFor(EventListener el) {
            return delegate == el;
        }
        
        public void mouseDragged(MouseEvent e) {
            if(e.isConsumed()) return;
            delegate.mouseDragged(e);
        }
        public void mouseMoved(MouseEvent e) {
            delegate.mouseMoved(e);
        }
    }
    
    /**
     * Proxy for MouseListener.
     */
    private static class MouseProxy implements MouseListener,
                                               EventListenerProxy {
        private final MouseListener delegate;
        MouseProxy(MouseListener ml) {
            delegate = ml;
        }
        
        public boolean isProxyFor(EventListener el) {
            return delegate == el;
        }
    
        public void mouseClicked(MouseEvent e) { 
            delegate.mouseClicked(e);
        }
        
        public void mouseEntered(MouseEvent e) {
            delegate.mouseEntered(e);
        }
        
        public void mouseExited(MouseEvent e) {
            delegate.mouseExited(e);
        }
        
        public void mousePressed(MouseEvent e) {
            if(e.isConsumed()) return;
            delegate.mousePressed(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            delegate.mouseReleased(e);
        }
    }
}
