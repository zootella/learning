package com.limegroup.gnutella.gui.notify;

import com.limegroup.gnutella.gui.ResourceManager;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class acts as a proxy for a platform-specific user notification class.
 */
 //2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public class NotifyUserProxy implements NotifyUser {

    /**
     * The NotifyUser object that this class is serving as a
     * proxy for.
     */
    private NotifyUser _notifier;

    /**
     * Flag for whether or not the application is currently in the tray.
     */
    private boolean _inTray = false;

    /**
     * Singleton.
     */
    private static final NotifyUserProxy INSTANCE =
        new NotifyUserProxy();
    
    /**
     * Instance accessor method for the single object of this class,
     * following the singleton pattern.
     *
     * @return a NotifyUserProxy instance for this object
     */
    public static NotifyUserProxy instance() {
        return INSTANCE;
    }

    /**
     * Instantiates the appropriate NotifyUser object depending on the
     * platform.  This class serves as a "proxy" for the object constructed.
     */
    private NotifyUserProxy() {
        if (CommonUtils.supportsTray() &&
           ResourceManager.instance().isTrayLibraryLoaded()) {
        	if (CommonUtils.isWindows())
        		_notifier = new WindowsNotifyUser();
        	else 
        		_notifier = new LinuxNotifyUser();
            addNotify();
        } else
            _notifier = new NonWindowsNotifyUser();
    }

    public void addNotify() {
        if (_inTray)
            return;
        _notifier.addNotify();
        _inTray = true;
    }

    public void removeNotify() {
        if (!_inTray)
            return;
        _notifier.removeNotify();
        _inTray = false;
    }

    public void updateNotify(final String imageFileName, final String desc) {
        _notifier.updateNotify(imageFileName, desc);
    }

    public void updateDesc(final String desc) {
        _notifier.updateDesc(desc);
    }    

    public void updateImage(final String imageFileName) {
        _notifier.updateImage(imageFileName);
    }

    /**
     * Hides the tray icon.  Not currently used.
     */
    public void hideNotify() {
        if (!_inTray)
            return;
        _notifier.hideNotify();
    }
}
