package com.limegroup.gnutella.gui.notify;

/**
 * This class handles user notification for non-Windows platform.
 * It currently does nothing in response to user notification events.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class NonWindowsNotifyUser implements NotifyUser {   

    public void addNotify() {}

    public void removeNotify() {}

    public void hideNotify() {}

    public void updateNotify(final String imageFile, final String desc) {}

    public void updateImage(final String imageFile) {}

    public void updateDesc(final String desc) {}

    /**
     * implements the NotifyUser interface.
     * currently does nothing, since we have not implemented
     * a user notification mechanism for non-Windows platforms.
     */
    //public void installNotifyCallback(NotifyCallback callback) {}

}
