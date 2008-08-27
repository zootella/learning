package com.limegroup.gnutella.gui.notify;

/**
 * Interface the outlines the basic functionality of any native desktop
 * notification mechanism, such as the "system tray" on Windows.
 */
public interface NotifyUser {

    /**
     * Adds the notification gui object to the desktop.
     */
    public void addNotify();

    /**
     * Removes the notification gui object from the desktop.
     */
    public void removeNotify();

    /**
     * Updates the user notification image file and description,
     *
     * @param imageFileName the name of the image file to update to, relative
     *  to the current directory, as in "LimeWire.ico"
     * @param desc the description to use
     */
    public void updateNotify(final String imageFileName, final String desc);

    /**
     * Updates the user notification description,
     *
     * @param desc the description to use
     */
    public void updateDesc(final String desc);

    /**
     * Updates the user notification image file
     *
     * @param imageFileName the name of the image file to update to, relative
     *  to the current directory, as in "LimeWire.ico"
     */
    public void updateImage(final String imageFileName);

    /**
     * Hides the user notification mechanism.
     */
    public void hideNotify();

    /**
     * Installs the object that will handle callbacks from the
     * native desktop object.
     *
     * @param callback to the object that will handle callbacks
     *  from the native desktop object.
     */
    //public void installNotifyCallback(NotifyCallback callback);

}
