package com.limegroup.gnutella.gui;

/**
 * This interface defines the method for installing an Apple
 * event handler for a particular event. (Mac only).
 */
public interface AEEventHandlerInterface {

    /**
     * Registers the event handler callback for specific AppleEvents.
     */
    short AEEventHandler(int event, int reply, int refcon);
}
