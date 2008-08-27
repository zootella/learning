package com.limegroup.gnutella.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Random;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.limegroup.gnutella.ErrorService;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.bugs.BugManager;
import com.limegroup.gnutella.bugs.FatalBugManager;
import com.limegroup.gnutella.gui.connection.ConnectionMediator;
import com.limegroup.gnutella.gui.download.DownloadMediator;
import com.limegroup.gnutella.gui.library.LibraryMediator;
import com.limegroup.gnutella.gui.menu.MenuMediator;
import com.limegroup.gnutella.gui.mp3.MediaPlayerComponent;
import com.limegroup.gnutella.gui.notify.NotifyUserProxy;
import com.limegroup.gnutella.gui.options.OptionsMediator;
import com.limegroup.gnutella.gui.playlist.PlaylistMediator;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.gui.statistics.StatisticsMediator;
import com.limegroup.gnutella.gui.tabs.LibraryPlayListTab;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.gui.upload.UploadMediator;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.BooleanSetting;
import com.limegroup.gnutella.settings.IntSetting;
import com.limegroup.gnutella.settings.PlayerSettings;
import com.limegroup.gnutella.settings.QuestionsHandler;
import com.limegroup.gnutella.settings.StartupSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.Launcher;
import com.limegroup.gnutella.util.ManagedThread;
import com.limegroup.gnutella.util.ProcessingQueue;
import com.limegroup.gnutella.version.UpdateInformation;

/**
 * This class acts as a central point of access for all gui components, a sort
 * of "hub" for the frontend.  This should be the only common class that all
 * frontend components have access to, reducing the overall dependencies and
 * therefore increasing the modularity of the code.
 *
 * <p>Any functions or services that should be accessible to multiple classes
 * should be added to this class.  These currently include such functions as
 * easily displaying standardly-formatted messages to the user, obtaining
 * locale-specific strings, and obtaining image resources, among others.
 *
 * <p>All of the methods in this class should be called from the event-
 * dispatch (Swing) thread.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class GUIMediator {

    /**
     * The number of messages a connection must have sent before we consider
     * it stable for the UI.
     */
    private static final int STABLE_THRESHOLD = 5;

    /**
     * Flag for whether or not a message has been displayed to the user --
     * useful in deciding whether or not to display other dialogues.
     */
	private static boolean _displayedMessage;

    /**
	 * Singleton for easy access to the mediator.
	 */
	private static GUIMediator _instance = null;

	/**
	 * Constant for the index of the search tab in the main application
	 * window.
	 */
	public static final int SEARCH_INDEX = 0;

	/**
	 * Constant for the index of the monitor tab in the main application
	 * window.
	 */
	public static final int MONITOR_INDEX = 1;

	/**
	 * Constant for the index of the connections tab in the main application
	 * window.
	 */
	public static final int CONNECTIONS_INDEX = 2;

	/**
	 * Constant for the index of the library tab in the main application
	 * window.
	 */
	public static final int LIBRARY_INDEX = 3;

	/**
	 * Constant specifying whether or not the user has donated to the LimeWire
	 * project.
	 */
	private static boolean HAS_DONATED = true;

	/**
	 * The main <tt>JFrame</tt> for the application.
	 */
	private static final JFrame FRAME = new JFrame();

	/**
	 * <tt>List</tt> of <tt>RefreshListener</tt> classes to notify of UI
	 * refresh events.
	 */
	private static final List REFRESH_LIST = new ArrayList();
    
	/**
	 * String to be displayed in title bar of LW client.
	 */
	private final String APP_TITLE =
		GUIMediator.getStringResource("APP_TITLE");

	/**
	 * Constant for when the user selects the yes button
	 * in a message giving the user a yes and a no option.
	 */
	public static final int YES_OPTION = MessageService.YES_OPTION;

	/**
	 * Constant for when the user selects the no button
	 * in a message giving the user a yes and a no option.
	 */
	public static final int NO_OPTION = MessageService.NO_OPTION;

	/**
	 * Constant for when the user selects the cancel button
	 * in a message giving the user a cancel option
	 */
	public static final int CANCEL_OPTION = MessageService.CANCEL_OPTION;

	/**
	 * Handle to the <tt>OptionsMediator</tt> class that is responsible for
	 * displaying customizable options to the user.
	 */
	private static OptionsMediator _optionsMediator;

	/**
	 * Constant handle to the <tt>MainFrame</tt> instance that handles
	 * constructing all of the primary gui components.
	 */
	private final MainFrame MAIN_FRAME = new MainFrame(FRAME);

	/**
	 * Constant handle to the <tt>DownloadMediator</tt> class that is
	 * responsible for displaying active downloads to the user.
	 */
	private final DownloadMediator DOWNLOAD_MEDIATOR =
		MAIN_FRAME.getDownloadMediator();

	/**
	 * Constant handle to the <tt>UploadMediator</tt> class that is
	 * responsible for displaying active uploads to the user.
	 */
	private final UploadMediator UPLOAD_MEDIATOR =
		MAIN_FRAME.getUploadMediator();

	/**
	 * Constant handle to the <tt>ConnectionMediator</tt> class that is
	 * responsible for displaying current connections to the user.
	 */
	private final ConnectionMediator CONNECTION_MEDIATOR =
		MAIN_FRAME.getConnectionMediator();

	/**
	 * Constant handle to the <tt>LibraryMediator</tt> class that is
	 * responsible for displaying files in the user's repository.
	 */
	private final LibraryMediator LIBRARY_MEDIATOR =
		MAIN_FRAME.getLibraryMediator();

	/**
	 * Constant handle to the <tt>MenuMediator</tt> class that is responsible
	 * for displaying and controlling the main menu bar of the program.
	 */
	private final MenuMediator MENU_MEDIATOR =
		MAIN_FRAME.getMenuMediator();

	/**
	 * Constant handle to the <tt>DownloadView</tt> class that is responsible
	 * for displaying the status of the network and connectivity to the user.
	 */
	private final StatusLine STATUS_LINE =
		MAIN_FRAME.getStatusLine();

	/**
	 * Constant handle to the <tt>StatisticsMediator</tt> class that is
	 * responsible for displaying statistics to the user.
	 */
	private final StatisticsMediator STATISTICS_MEDIATOR =
		MAIN_FRAME.getStatisticsMediator();

	/**
	 * Handle to <tt>RouterService</tt> to give frontend classes access to the
	 * backend.
	 */
	private RouterService _routerService;

    /**
     * Flag for whether or not the app has ever been made visible during this
     * session.
     */
    private static boolean _visibleOnce = false;

    /**
     * Flag for whether or not the app is allowed to become visible.
     */
    private static boolean _allowVisible = false;

    /**
     * Queue for items to be run in the background.
     */
    private final ProcessingQueue QUEUE = new ProcessingQueue("DelayedGUI");
    
    /**
     * The last recorded idle time.
     */
    private long lastIdleTime = 0;
    
    /**
     * The number of times that we'll allow a java check to go unheeded.
     */
    private int JAVA_CHECK = new Random().nextInt(10) + 5;

	/**
	 * Private constructor to ensure that this class cannot be constructed
	 * from another class.
	 */
	private GUIMediator() {
		FRAME.setTitle(APP_TITLE);
		_optionsMediator = MAIN_FRAME.getOptionsMediator();
		addRefreshListener(STATISTICS_MEDIATOR);
	}

	/**
	 * Singleton accessor for this class.
	 *
	 * @return the <tt>GUIMediator</tt> instance
	 */
	public static synchronized GUIMediator instance() {
		if (_instance == null)
			_instance = new GUIMediator();
		return _instance;
	}

	/**
	 * Accessor for whether or not the GUIMediator has been constructed yet.
	 */
	public static boolean isConstructed() {
	    return _instance != null;
	}

	/**
	 * Runs the specified runnable in a different thread when it can.
	 */
	public void schedule(Runnable r) {
	    QUEUE.add(r);
    }

	/**
	 * The host catcher and the statistics view need the backend to be
	 * initialized for these methods to be called.
	 */
	public final void startTimer() {
		RefreshTimer timer = new RefreshTimer();
		timer.startTimer();
	}

	/**
	 * Returns a boolean specifying whether or not the wrapped
	 * <tt>JFrame</tt> is visible or not.
	 *
	 * @return <tt>true</tt> if the <tt>JFrame</tt> is visible,
	 *  <tt>false</tt> otherwise
	 */
	public static final boolean isAppVisible() {
		return FRAME.isShowing();
	}

	/**
	 * Specifies whether or not the main application window should be visible
	 * or not.
	 *
	 * @param visible specifies whether or not the application should be
	 *                made visible or not
	 */
	public static final void setAppVisible(final boolean visible) {
        safeInvokeLater(new Runnable() {
            public void run() {
                try {
                    if (visible)
                        FRAME.toFront();
                    FRAME.setVisible(visible);
                } catch (NullPointerException npe) {
                    //  NPE being thrown on WinXP sometimes.  First try
                    //  reverting to the limewire theme.  If NPE still
                    //  thrown, tell user to change LimeWire's Windows
                    //  compatibility mode to Win2k.
                    //  null pointer found
                    if (CommonUtils.isWindowsXP()) {
                        try {
                            if (ThemeSettings.isWindowsTheme()) {
                                ThemeMediator.changeTheme(ThemeSettings.LIMEWIRE_THEME_FILE);
                                try {
                                    if (visible)
                                        FRAME.toFront();
                                    FRAME.setVisible(visible);
                                } catch (NullPointerException npe2) {
                                    GUIMediator.showError("ERROR_STARTUP_WINDOWS_COMPATIBILITY");
                                    System.exit(0);
                                }                                
                            } else {
                                GUIMediator.showError("ERROR_STARTUP_WINDOWS_COMPATIBILITY");
                                System.exit(0);
                            }
                        } catch (Throwable t) {
                            if (visible)
                                FatalBugManager.handleFatalBug(npe);
                            else
                                showInternalError(npe);
                        }
                    } else {
                        if (visible)
                            FatalBugManager.handleFatalBug(npe);
                        else
                            showInternalError(npe);
                    }
                } catch(Throwable t) {
                    if (visible)
                        FatalBugManager.handleFatalBug(t);
                    else
                        showInternalError(t);
                }
                if (visible) {
                    SearchMediator.requestSearchFocus();
                    // forcibily revalidate the FRAME
                    // after making it visible.
                    // on Java 1.5, it does not validate correctly.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            FRAME.getContentPane().invalidate();
                            FRAME.getContentPane().validate();
                        }
                    });
                }
                // If the app has already been made visible, don't display extra
                // dialogs.  We could display the pro dialog here, but it causes
                // some odd issues when LimeWire is brought back up from the tray
                if (visible && !_visibleOnce) {
                    // Show the startup dialogs in the swing thread.
                    showDialogsForFirstVisibility();
                    _visibleOnce = true;
                }
            }
        });
	}

	/**
	 * Displays various dialog boxes that should only be shown the first
	 * time the application is made visible.
	 */
	private static final void showDialogsForFirstVisibility() {
		if (!hasDonated())
			UpgradeWindow.showProDialog();
			
		if (!_displayedMessage && 
		  ResourceManager.hasLocalizedTipsOfTheDay() && StartupSettings.SHOW_TOTD.getValue()) {
			new ManagedThread("TOTD") {
                public void managedRun() {
                    try {
                        Thread.sleep(500);
                    } catch (Throwable t) { }
                    TipOfTheDayMediator.instance().displayTipWindow();
                }
            }.start();
        }
	}

	/**
	 * Displays a dialog the first time a user performs a download. 
	 * Returns true iff the user selects 'Yes'; returns false otherwise.
	 */
	public static boolean showFirstDownloadDialog() {
		if (MessageService.YES_OPTION ==
			showYesNoCancelMessage("DOWNLOAD_SHOW_FIRST_DOWNLOAD_WARNING",
                    QuestionsHandler.SKIP_FIRST_DOWNLOAD_WARNING))
			return true;
		return false;
	}
	
    /**
     * Closes any dialogues that are displayed at startup and sets the flag to
     * indicate that we've displayed a message.
     */
    private static void closeStartupDialogs() {
        if(SplashWindow.instance().isShowing())
            SplashWindow.instance().toBack();
        
        _displayedMessage = true;
        TipOfTheDayMediator.instance().hide();
    }
    
    /**
     * Checks to see if we should notify the user that they're running an
     * older version of java.
     */
    public void checkForJavaVersion() {
        if(CommonUtils.isJavaOutOfDate() && JAVA_CHECK-- == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UpgradeWindow.showJavaDialog();
                }
            });
        }   
    }

	/**
	 * Returns a <tt>Dimension</tt> instance containing the dimensions of the
	 * wrapped JFrame.
	 *
	 * @return a <tt>Dimension</tt> instance containing the width and height
	 *         of the wrapped JFrame
	 */
	public static final Dimension getAppSize() {
		return FRAME.getSize();
	}

	/**
	 * Returns a <tt>Point</tt> instance containing the x, y position of the
	 * wrapped <ttJFrame</tt> on the screen.
	 *
	 * @return a <tt>Point</tt> instance containting the x, y position of the
	 *         wrapped JFrame
	 */
	public static final Point getAppLocation() {
		return FRAME.getLocation();
	}

	/**
	 * Returns the <tt>MainFrame</tt> instance.  <tt>MainFrame</tt> maintains
	 * handles to all of the major gui classes.
	 *
	 * @return the <tt>MainFrame</tt> instance
	 */
	public final MainFrame getMainFrame() {
		return MAIN_FRAME;
	}

	/**
	 * Returns the main application <tt>JFrame</tt> instance.
	 *
	 * @return the main application <tt>JFrame</tt> instance
	 */
	public static final JFrame getAppFrame() {
		return FRAME;
	}

	/**
	 * Returns the router service variable for other classes to access.
	 *
	 * @return the <tt>RouterService</tt> instance
	 */
	public final RouterService getRouter() {
		return _routerService;
	}

	/**
	 * Sets the router service variable for other classes to access.
	 *
	 * @param routerService the <tt>RouterService</tt> instance for other
	 *                      classes to access
	 */
	public final void setRouterService(RouterService routerService) {
		_routerService = routerService;
	}

	/**
	 * Returns the status line instance for other classes to access 
	 */
	public StatusLine getStatusLine() {
		return STATUS_LINE;
	}
	
	/**
	 * Refreshes the various gui components that require refreshing.
	 */
	public final void refreshGUI() {
		for (int i = 0; i < REFRESH_LIST.size(); i++) {
            try {
                ((RefreshListener)REFRESH_LIST.get(i)).refresh();
            } catch(Throwable t) {
                // Show the error for each RefreshListener individually
                // so that we continue refreshing the other items.
                GUIMediator.showInternalError(t);
            }
		}

        // update the status panel
        int sharedFiles  = RouterService.getNumSharedFiles();
        int pendingShare = RouterService.getNumPendingShared();
        int quality      = getConnectionQuality();
        STATUS_LINE.setStatistics(sharedFiles, pendingShare);

        updateConnectionUI(quality);
	}

    /**
     * Returns the connectiong quality.
     */
    public int getConnectionQuality() {
        int stable =
            RouterService.countConnectionsWithNMessages(STABLE_THRESHOLD);
            
        int status;

        if(stable == 0) {
            int initializing = CONNECTION_MEDIATOR.getConnectingCount();
            int connections = RouterService.getNumInitializedConnections();
            // No initializing or stable connections
            if(initializing == 0 && connections == 0) {
                //Not attempting to connect at all...
                if(!RouterService.isConnecting())
                    status = StatusLine.STATUS_DISCONNECTED;
                //Attempting to connect...
                else
                    status = StatusLine.STATUS_CONNECTING;
            }
            // No initialized, all initializing - connecting
            else if(connections == 0)
                status = StatusLine.STATUS_CONNECTING;
            // Some initialized - poor connection.
            else
                status = StatusLine.STATUS_POOR;
        } else if(RouterService.getConnectionManager().isConnectionIdle()) {
            lastIdleTime = System.currentTimeMillis();
            status = StatusLine.STATUS_IDLE;
        } else {
            int preferred = RouterService.getConnectionManager().
                            getPreferredConnectionCount();
            // pro will have more.
            if(CommonUtils.isPro())
                preferred -= 2;
            // ultrapeers don't need as many...
            if(RouterService.isSupernode())
                preferred -= 5;
            preferred = Math.max(1, preferred); // prevent div by 0

            double percent = (double)stable / (double)preferred;
            if(percent <= 0.25)
                status = StatusLine.STATUS_POOR;
            else if(percent <= 0.5)
                status = StatusLine.STATUS_FAIR;
            else if(percent <= 0.75)
                status = StatusLine.STATUS_GOOD;
            else if(percent <= 1)
                status = StatusLine.STATUS_EXCELLENT;
            else /* if(percent > 1) */
                status = StatusLine.STATUS_TURBOCHARGED;
        }
        
        switch(status) {
        case StatusLine.STATUS_CONNECTING:            
        case StatusLine.STATUS_POOR:
        case StatusLine.STATUS_FAIR:
        case StatusLine.STATUS_GOOD:
            // if one of these four, see if we recently woke up from
            // idle, and if so, report as 'waking up' instead.
            long now = System.currentTimeMillis();
            if(now < lastIdleTime + 15 * 1000)
                status = StatusLine.STATUS_WAKING_UP;
        }
        
        return status;
    }


	/**
	 * Sets the visibility state of the options window.
	 *
	 * @param visible the visibility state to set the window to
	 */
	public void setOptionsVisible(boolean visible) {
		if (_optionsMediator == null) return;
		_optionsMediator.setOptionsVisible(visible);
	}

	/**
	 * Sets the visibility state of the options window, and sets
	 * the selection to a option pane associated with a given key.
	 *
	 * @param visible the visibility state to set the window to
	 * @param key the unique identifying key of the panel to show
	 */
	public void setOptionsVisible(boolean visible, final String key) {
		if (_optionsMediator == null) return;
		_optionsMediator.setOptionsVisible(visible, key);
	}

	/**
	 * Returns whether or not the options window is visible
	 *
	 * @return <tt>true</tt> if the options window is visible,
	 *  <tt>false</tt> otherwise
	 */
	public static boolean isOptionsVisible() {
		if (_optionsMediator == null) return false;
		return _optionsMediator.isOptionsVisible();
	}

	/**
	 * Gets a handle to the options window main <tt>JComponent</tt> instance.
	 *
	 * @return the options window main <tt>JComponent</tt>, or <tt>null</tt>
	 *  if the options window has not yet been constructed (the window is
	 *  guaranteed to be constructed if it is visible)
	 */
	public static Component getMainOptionsComponent() {
		if (_optionsMediator == null) return null;
		return _optionsMediator.getMainOptionsComponent();
	}

	/**
	 * Sets the visibility of the statistics window.
	 *
	 * @param visible the visibility state to set the window to
	 */
	public final void setStatisticsVisible(boolean visible) {
		STATISTICS_MEDIATOR.setStatisticsVisible(visible);
	}

	/**
	 * Sets the tab pane to display the given tab.
	 *
	 * @param index the index of the tab to display
	 */
	public void setWindow(int index) {
		MAIN_FRAME.setSelectedIndex(index);
	}

	/**
	 * Updates the icon at the specified tab index.
	 *
	 * @param index the fixed index of the tab to update
	 */
	public void updateTabIcon(int index) {
		MAIN_FRAME.updateTabIcon(index);
	}

	/**
	 * Clear the connections in the connection view.
	 */
	public void clearConnections() {
		CONNECTION_MEDIATOR.clearConnections();
	}

	/**
	 * Sets the connected/disconnected visual status of the client.
	 *
	 * @param connected the connected/disconnected status of the client
	 */
	private void updateConnectionUI(int quality) {
        STATUS_LINE.setConnectionQuality(quality);

        boolean connected =
            quality != StatusLine.STATUS_DISCONNECTED;
		MENU_MEDIATOR.setConnected(connected);
		if (connected == false)
			this.setSearching(false);
	}

  	/**
  	 * Returns the total number of uploads for this session.
	 *
	 * @return the total number of uploads for this session
  	 */
  	public int getTotalUploads() {
  		return UPLOAD_MEDIATOR.getTotalUploads();
  	}

  	/**
  	 * Returns the total number of currently active uploads.
	 *
	 * @return the total number of currently active uploads
  	 */
  	public int getCurrentUploads() {
  		return UPLOAD_MEDIATOR.getCurrentUploads();
  	}

  	/**
  	 * Returns the total number of downloads for this session.
	 *
	 * @return the total number of downloads for this session
  	 */
  	public final int getTotalDownloads() {
  		return DOWNLOAD_MEDIATOR.getTotalDownloads();
  	}

  	/**
  	 * Returns the total number of currently active downloads.
	 *
	 * @return the total number of currently active downloads
  	 */
  	public final int getCurrentDownloads() {
  		return DOWNLOAD_MEDIATOR.getCurrentDownloads();
  	}

	/**
	 * Tells the library to add a new top-level (shared) folder.
	 */
	public final void addSharedLibraryFolder() {
		LIBRARY_MEDIATOR.addSharedLibraryFolder();
	}

	/**
	 * Returns the active playlist or <code>null</code> if the playlist
	 * is not enabled.
	 */
	public static PlaylistMediator getPlayList() {
	    return MainFrame.getPlaylistMediator();
    }

    /**
     * Determines whether or not the PlaylistMediator is being used this session.
     */
    public static boolean isPlaylistVisible() {
        // If we are not constructed yet, then make our best guess as
        // to visibility.  It is actually VERY VERY important that this
        // returns the same thing throughout the entire course of the program,
        // otherwise exceptions can pop up.
        if(!isConstructed())
            return PlayerSettings.PLAYER_ENABLED.getValue();
        else
            return getPlayList() != null && PlayerSettings.PLAYER_ENABLED.getValue();
    }
    
    /**
     * Runs the appropriate methods to start LimeWire up
     * hidden.
     */
    public static void startupHidden() {
        // sends us to the system tray on windows, ignored otherwise.
        GUIMediator.showTrayIcon();
        // If on OSX, we must set the framestate appropriately.
        if(CommonUtils.isMacOSX())
            GUIMediator.hideView();
    }

    /**
     * Notification that visibility is now allowed.
     */
    public static void allowVisibility() {
		if(!_allowVisible && CommonUtils.isAnyMac())
		    MacEventHandler.instance().enablePreferences();
        _allowVisible = true;
    }

    /**
     * Notification that loading is finished.  Updates the status line and 
     * bumps the AWT thread priority.
     */
    public void loadFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Thread awt = Thread.currentThread();
                awt.setPriority(awt.getPriority() + 1);
                STATUS_LINE.loadFinished();
            }
       });
    }

    /**
     * Handles a 'reopen' event appropriately.
     * Used primarily for allowing LimeWire to be made
     * visible after it was started from system startup
     * on OSX.
     */
    public static void handleReopen() {
        // Do not do anything
        // if visibility is not allowed yet, as initialization
        // is not yet finished.
        if(_allowVisible) {
            if(!_visibleOnce)
                restoreView(); // First make sure it's not minimized
            setAppVisible(true); // Then make it visible
            // Otherwise (if the above operations were reversed), a tiny
            // LimeWire icon would appear in the 'minimized' area of the dock
            // for a split second, and the Console would report strange errors
        }
    }

	/**
	 * Hides the GUI by either sending it to the System Tray or
	 * minimizing the window.  Mimimize behavior occurs on platforms
	 * which do not support the System Tray.
	 * @see restoreView
	 */
	public static void hideView() {
        FRAME.setState(Frame.ICONIFIED);

		if (CommonUtils.supportsTray())
			GUIMediator.setAppVisible(false);
	}


	/**
	 * Makes the GUI visible by either restoring it from the System Tray or
	 * the task bar.
	 * @see hideView
	 */
	public static void restoreView() {
		// Frame must be visible for setState to work.  Make visible
		// before restoring.

		if (CommonUtils.supportsTray()) {
            // below is a little hack to get around odd windowing
            // behavior with the system tray on windows.  This enables
            // us to get LimeWire to the foreground after it's run from
            // the startup folder with all the nice little animations
            // that we want

            // cache whether or not to use our little hack, since setAppVisible
            // changes the value of _visibleOnce
            boolean doHack = false;
            if (!_visibleOnce)
                doHack = true;
			GUIMediator.setAppVisible(true);
            if (ApplicationSettings.DISPLAY_TRAY_ICON.getValue())
                GUIMediator.showTrayIcon();
            else
                GUIMediator.hideTrayIcon();
            if (doHack)
                restoreView();
		}

        // If shutdown sequence was initiated, cancel it.  Auto shutdown is
		// disabled when the GUI is visible.
		Finalizer.cancelShutdown();
		
		FRAME.setState(Frame.NORMAL);
	}

	/**
	 * Determines the appropriate shutdown behavior based on user settings.
	 * This implementation decides between exiting the application immediately,
	 * or exiting after all file transfers in progress are complete.
	 */
	public static void close(boolean fromFrame) {
		if (ApplicationSettings.MINIMIZE_TO_TRAY.getValue()) {
		    // if we want to minimize to the tray, but LimeWire wasn't
		    // able to load the tray library, then shutdown after transfers.
		    if(CommonUtils.supportsTray() && !ResourceManager.instance().isTrayLibraryLoaded())
		        shutdownAfterTransfers();
		    else {
                applyWindowSettings();
                GUIMediator.showTrayIcon();
                hideView();
            }
        } else if (CommonUtils.isMacOSX() && CommonUtils.isJava14OrLater() &&
                   fromFrame) {
            //If on OSX, don't close in response to clicking on the 'X'
            //as that's not normal behaviour.  This can only be done on Java14
            //though, because we need access to the
            //com.apple.eawt.ApplicationListener.handleReOpenApplication event
            //in order to restore the GUI.
            GUIMediator.setAppVisible(false);
        } else if (ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.getValue()) {
			GUIMediator.shutdownAfterTransfers();
		} else {
		    shutdown();
        }
	}

	/**
	 * Shutdown the program cleanly.
	 */
	public static void shutdown() {
		Finalizer.shutdown();
	}
    
	/**
	 * Shutdown the program cleanly after all transfers in progress are
	 * complete.  Calling this method causes the GUI to be hidden while the
	 * application waits to shutdown.
	 * @see hideView
	 */
	public static void shutdownAfterTransfers() {
		Finalizer.shutdownAfterTransfers();
		GUIMediator.hideView();
	}
    
    public static void flagUpdate(String toExecute) {
        Finalizer.flagUpdate(toExecute);
    }

	/**
	 * Shows the "About" menu with more information about the program.
	 */
	public static final void showAboutWindow() {
		new AboutWindow().showDialog();
	}

	/**
	 * Shows the user notification area.  The user notification icon and
	 * tooltip created by the NotifyUser object are not modified.
	 */
	public static void showTrayIcon() {
        NotifyUserProxy.instance().addNotify();
	}

    /**
     * Hides the user notification area.
     */
    public static void hideTrayIcon() {
        //  Do not use hideNotify() here, since that will
        //  create multiple tray icons.
        NotifyUserProxy.instance().removeNotify();
    }

    /**
     * Sets the window height, width and location properties to remember the
     * next time the program is started.
     */
    public static void applyWindowSettings()  {
        ApplicationSettings.RUN_ONCE.setValue(true);
        if (GUIMediator.isAppVisible()) {
            // set the screen size and location for the
            // next time the application is run.
            Dimension dim = GUIMediator.getAppSize();

            // only save reasonable sizes to get around a bug on
            // OS X that could make the window permanently
            // invisible
            if((dim.height > 100) && (dim.width > 100)) {
                Point loc = GUIMediator.getAppLocation();
                ApplicationSettings.APP_WIDTH.setValue(dim.width);
                ApplicationSettings.APP_HEIGHT.setValue(dim.height);
                ApplicationSettings.WINDOW_X.setValue(loc.x);
                ApplicationSettings.WINDOW_Y.setValue(loc.y);
            }
        }
    }

	/**
	 * Serves as a single point of access for any icons used in the program.
	 *
	 * @param imageName the name of the icon to return without path
	 *                  information, as in "plug"
	 * @return the <tt>ImageIcon</tt> object specified in the param string
	 */
	public static final ImageIcon getThemeImage(final String name) {
		return ResourceManager.getThemeImage(name);
	}

	/**
	 * Returns an ImageIcon for the specified resource.
	 */
	public static final ImageIcon getImageFromPath(final String loc) {
	    return ResourceManager.getImageFromPath(loc);
    }

	/**
	 * Returns a new <tt>URL</tt> instance for the specified file name.
	 * The file must be located in the com/limegroup/gnutella/gui/resources
	 * directory, or this will return <tt>null</tt>.
	 *
	 * @param FILE_NAME the name of the file to return a url for without path
	 *  information, as in "about.html"
	 * @return the <tt>URL</tt> instance for the specified file, or
	 * <tt>null</tt> if the <tt>URL</tt> could not be loaded
	 */
	public static URL getURLResource(final String FILE_NAME) {
		return ResourceManager.getURLResource(FILE_NAME);
	}

	/**
	 * Resets locale options.
	 */
	public static void resetLocale() {
	    ResourceManager.resetLocaleOptions();
	}

	/**
	 * Returns the locale-specific String from the resource manager.
	 *
	 * @return an internationalized <tt>String</tt> instance
	 *         corresponding with the <tt>resourceKey</tt>
	 */
	public static final String getStringResource(final String resourceKey) {
		return ResourceManager.getStringResource(resourceKey);
	}

    /**
     * Return ResourceBundle for use with specific xml schema
     *
     * @param schemaname the name of schema
     *        (not the URI but name returned by LimeXMLSchema.getDisplayString)
     * @return a ResourceBundle matching the passed in param
     */
    public static final ResourceBundle getXMLResourceBundle(final String schemaname) {
        return ResourceManager.getXMLResourceBundle(schemaname);
    }

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user in the form of a yes or no
	 * question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @return an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoMessage(
			final String messageKey) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageKey));
	}
    
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user in the form of a yes or no
	 * question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param defaultValue the IntSetting to store/retrieve the default value
	 * @return an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoMessage(
			final String messageKey,
			final IntSetting defaultValue) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageKey), defaultValue);
	}
    
    public static final int showYesNoTitledMessage(
            final String messageKey,
            final String titleKey) {
        return MessageService.instance().showYesNoMessage(
                getStringResource(messageKey),
                getStringResource(titleKey));
    }

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * appended to it.  This is in the form of a yes or no question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @return  an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoMessage(
			final String messageKey,
			final Object message) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageKey) + " " +
			message);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * appended to it.  This is in the form of a yes or no question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
     * @param defaultValue the IntSetting to store/retrieve the defaultValue
	 * @return  an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoMessage(
			final String messageKey,
			final Object message,
			final IntSetting defaultValue) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageKey) + " " +
			message, defaultValue);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * in the middle of the message between two locale-specific
	 * <tt>String</tt> values.  This is in the form of a yes or no
	 * question.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 * @return  an integer indicating a yes or a no response from the user.
	 */
	public static final int showYesNoMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * in the middle of the message between two locale-specific
	 * <tt>String</tt> values.  This is in the form of a yes or no
	 * question.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 * @param defaultValue the IntSetting that stores/retrieves the defaultValue
	 * @return  an integer indicating a yes or a no response from the user.
	 */
	public static final int showYesNoMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey,
			final IntSetting defaultValue) {
		return MessageService.instance().showYesNoMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey),
			defaultValue);
	}
	
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user in the form of a yes or no or cancel
	 * question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @return an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoCancelMessage(
			final String messageKey) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user in the form of a yes or no or cancel
	 * question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param defaultValue the IntSetting to store/retrieve the default value
	 * @return an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoCancelMessage(
			final String messageKey, final IntSetting defaultValue) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageKey), defaultValue);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * appended to it.  This is in the form of a yes or no or cancel question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @return  an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoCancelMessage(
			final String messageKey,
			final Object message) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageKey) + " " +
			message);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * appended to it.  This is in the form of a yes or no or cancel question.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
     * @param defaultValue the IntSetting to store/retrieve the defaultValue
	 * @return  an integer indicating a yes or a no response from the user
	 */
	public static final int showYesNoCancelMessage(
			final String messageKey,
			final Object message,
			final IntSetting defaultValue) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageKey) + " " +
			message, defaultValue);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * in the middle of the message between two locale-specific
	 * <tt>String</tt> values.  This is in the form of a yes or no or cancel
	 * question.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 * @return  an integer indicating a yes or a no response from the user.
	 */
	public static final int showYesNoCancelMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded <tt>String</tt>
	 * in the middle of the message between two locale-specific
	 * <tt>String</tt> values.  This is in the form of a yes or no or cancel
	 * question.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 * @param defaultValue the IntSetting that stores/retrieves the defaultValue
	 * @return  an integer indicating a yes or a no response from the user.
	 */
	public static final int showYesNoCancelMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey,
			final IntSetting defaultValue) {
		return MessageService.instance().showYesNoCancelMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey),
			defaultValue);
	}	

	/**
	 * Displays the message denoted by <code>messageKey</code> replacing all 
	 * occurrences of {number} in the message string with the arguments given
	 * in <code>args</code>
	 * <p>
	 * For details on the syntax, please see 
	 * {@link MessageFormat#format(java.lang.String, java.lang.Object[])} which 
	 * is used internally.
	 */
	public static final void showFormattedMessage(final String messageKey,
			Object[] args) {
		MessageService.instance().showMessage(MessageFormat.format(getStringResource(messageKey), args));
	}
	
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 */
	public static final void showMessage(
			final String messageKey) {
		MessageService.instance().showMessage(
			getStringResource(messageKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param ignore the BooleanSetting that stores/retrieves whether or
	 *  not to display this message.
	 */
	public static final void showMessage(
			final String messageKey, final BooleanSetting ignore) {
		MessageService.instance().showMessage(
			getStringResource(messageKey), ignore);
	}


	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 */
	public static final void showMessage(
			final String messageKey,
			final Object message) {
		MessageService.instance().showMessage(
			getStringResource(messageKey) + " " +
			message);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
     * @param ignore the BooleanSetting that stores/retrieves whether or
     *  not to display this message.
	 */
	public static final void showMessage(
			final String messageKey,
			final Object message,
			final BooleanSetting ignore) {
		MessageService.instance().showMessage(
			getStringResource(messageKey) + " " +
			message, ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message
	 * <tt>String</tt>s and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 */
	public static final void showMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey) {
		MessageService.instance().showMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message
	 * <tt>String</tt>s and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey,
			final BooleanSetting ignore) {
		MessageService.instance().showMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey), ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays a
	 * confirmation message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 */
	public static final void showConfirmMessage(
			final String messageKey) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays a
	 * confirmation message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showConfirmMessage(
			final String messageKey,
			final BooleanSetting ignore) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageKey), ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a confirmation message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 */
	public static final void showConfirmMessage(
			final String messageKey,
			final Object message) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageKey) + " " +
			message);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a confirmation message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showConfirmMessage(
			final String messageKey,
			final Object message,
			final BooleanSetting ignore) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageKey) + " " +
			message, ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays a
	 * confirmation message to the user.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 */
	public static final void showConfirmMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays a
	 * confirmation message to the user.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message <tt>String</tt>s
	 * and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showConfirmMessage(
			final String messageStartKey,
			final Object message,
			final String messageEndKey,
			final BooleanSetting ignore) {
		MessageService.instance().showConfirmMessage(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey), ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display.
	 */
	public static final void showError(
			final String messageKey) {

        closeStartupDialogs();
		MessageService.instance().showError(
			getStringResource(messageKey));
        //closeStartupDialogs();
	}
	
	public static final void showTranslatedError(String error) {
		closeStartupDialogs();
		MessageService.instance().showError(error);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display.
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showError(
			final String messageKey,
			final BooleanSetting ignore) {

        closeStartupDialogs();
		MessageService.instance().showError(
			getStringResource(messageKey), ignore);
	}
	
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific warning message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display.
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showWarning(
			final String messageKey,
			final BooleanSetting ignore) {

        closeStartupDialogs();
		MessageService.instance().showWarning(
			getStringResource(messageKey), ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific warning message to the user.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 *
	 * @param messageKey the key for the locale-specific message to display.
	 */
	public static final void showWarning(final String messageKey) {
        closeStartupDialogs();
		MessageService.instance().showWarning(getStringResource(messageKey));
	}

    /**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded message
	 * appended to it at the end.<p>
	 *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 * <p>
	 * For a more flexible way of choosing the place of the hard-coded value see
	 * {@link #showFormattedError(String, Object[]).
	 * 
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 */
	public static final void showError(
	        final String messageKey,
		    final Object message) {
        closeStartupDialogs();
		MessageService.instance().showError(
			getStringResource(messageKey) + " " +
			message);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded message 
     * appended to it at the end.<p> 
     *
	 * The <tt>messageKey</tt> parameter must be the key for a locale-
	 * specific message <tt>String</tt> and not a hard-coded value.
	 * <p>
	 * For a more flexible way of choosing the place of the hard-coded value see
	 * {@link #showFormattedError(String, Object[], BooleanSetting)}.
	 * 
	 * @param messageKey the key for the locale-specific message to display
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showError(
	        final String messageKey,
		    final Object message,
		    final BooleanSetting ignore) {
        closeStartupDialogs();
		MessageService.instance().showError(
				getStringResource(messageKey) + " " + 
				message, ignore);
	}
	
	
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded names inbetween, which 
     * can appear anywhere within the localized text by making use of 
     * {@link MessageFormat#format(java.lang.String, java.lang.Object[])}.  
     * <p>
     * A call to:
     * <pre>
     * KEY="the file {0} is moved to directory {1}."
     * GUIMediator.showFormattedError(KEY,
     * new Object[] { "text.txt", "/home/user"});
     * </pre>
     * would tranlate to:
     * <pre>
     * "the file text.txt is moved to directory /home/user"
     * </pre>
     * <p>
     * For more details see {@link MessageFormat}.
     * 
	 * @param messageKey
	 * @param objs array of hardcoded object which will be merged into the 
	 * messageKey's translation.
	 * 
	 */
	public static final void showFormattedError(
			final String messageKey,
			final Object[] objs) {
		closeStartupDialogs();
		MessageService.instance().showError(
				MessageFormat.format(getStringResource(messageKey), objs));
	}
			
	
	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.  Displays a
	 * locale-specific message to the user with a hard-coded names inbetween, which 
     * can appear anywhere within the localized text by making use of 
     * {@link MessageFormat#format(java.lang.String, java.lang.Object[])}.  
     * <p>
     * A call to:
     * <pre>
     * KEY="the file {0} is moved to directory {1}."
     * GUIMediator.showFormattedError(KEY,
     * new Object[] { "text.txt", "/home/user"}, booleanSetting);
     * </pre>
     * would tranlate to:
     * <pre>
     * "the file text.txt is moved to directory /home/user"
     * </pre>
     * <p>
     * For more details see {@link MessageFormat}.
     * 
	 * @param messageKey
	 * @param objs
	 * @param ignore
	 */
	public static final void showFormattedError(
			final String messageKey,
			final Object[] objs,
			final BooleanSetting ignore) {
		closeStartupDialogs();
		MessageService.instance().showError(
				MessageFormat.format(getStringResource(messageKey), objs),
				ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message
	 * <tt>String</tt>s and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
	 */
	public static final void showError(
	 		final String messageStartKey,
			final Object message,
			final String messageEndKey) {
        closeStartupDialogs();
		MessageService.instance().showError(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey));
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class. Displays
	 * a message to the user with a locale-specific message with a
	 * hard-coded message appended to it.<p>
	 *
	 * The <tt>messageStartKey</tt> and the <tt>messageEndKey</tt>
	 * parameters must be keys for locale-specific message
	 * <tt>String</tt>s and not a hard-coded values.
	 *
	 * @param messageStartKey the key for the locale-specific message to
	 *                        display at the beginning of the message
	 * @param message a second, non-locale-specific message to display, such
	 *                as a filename
	 * @param messageEndKey the key for the locale-specific message to
	 *                      display at the end of the message
     * @param ignore the BooleanSetting for that stores/retrieves whether
     *  or not to display this message.
	 */
	public static final void showError(
	 		final String messageStartKey,
			final Object message,
			final String messageEndKey,
			final BooleanSetting ignore) {
        closeStartupDialogs();
        MessageService.instance().showError(
			getStringResource(messageStartKey) + " " +
			message + " " +
			getStringResource(messageEndKey), ignore);
	}

	/**
	 * Acts as a proxy for the <tt>MessageService</tt> class.
	 * @param t a Throwable object for displaying more information to the user
	 * @param detail A detailed message to display with the error
	 * @param curThread the thread the error occured in.
	 */
	public static final void showInternalError(Throwable t, String detail,
        Thread curThread) {
        closeStartupDialogs();
        BugManager.instance().handleBug(t, curThread, detail);
	}

	/**
	 * Stub for calling showInternalError(t, null, Thread.currentThread())
	 *
	 * @param t a Throwable object for displaying more information to the user
	 */
	public static final void showInternalError(Throwable t) {
        closeStartupDialogs();
        showInternalError(t, null, Thread.currentThread());
	}

	/**
	 * Stub for calling showInternalError(t, detail, Thread.currentThread())
	 *
	 * @param t a Throwable object for displaying more information to the user
	 * @param detail A detailed error message to display to the user.
	 */
	public static final void showInternalError(Throwable t, String detail) {
        closeStartupDialogs();
        showInternalError(t, detail, Thread.currentThread());
	}

	/**
	 * Stub for calling showInternalError(t, null, curThread)
	 *
	 * @param t a Throwable object for displaying more information to the user
	 * @param curThread the thread the error occured in
	 */
	public static final void showInternalError(Throwable t, Thread curThread) {
        closeStartupDialogs();
        showInternalError(t, null, curThread);
	}

	/**
	 * Acts as a proxy for the Launcher class so that other classes only need
	 * to know about this mediator class.
	 *
	 * <p>Opens the specified url in a browser.
	 *
	 * @param url the url to open
	 * @return an int indicating the success of the browser launch
	 */
	public static final int openURL(String url) {
	    try {
		    return Launcher.openURL(url);
        } catch(IOException ioe) {
            GUIMediator.showError("ERROR_OPEN_URL", url + ".");
            return -1;
        }
	}

	/**
	 * Acts as a proxy for the Launcher class so that other classes only need
	 * to know about this mediator class.
	 *
	 * <p>Launches the file specified in its associated application.
	 *
	 * @param file a <tt>File</tt> instance denoting the abstract pathname
	 *             of the file to launch
	 * @return an int indicating the success of the file launch
	 * @throws IOException if the file cannot be launched do to an IO problem
	 */
	public static final int launchFile(File file) throws IOException {
		try {
			return Launcher.launchFile(file);
		} catch (SecurityException se) {
			showError("MESSAGE_FILE_LAUNCHING_SECURITY_MESSAGE");
		}
		return -1;
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for horizontal separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard horizontal
	 *         separator
	 */
	public static final Component getHorizontalSeparator() {
		return Box.createRigidArea(new Dimension(6,0));
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for vertical separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard vertical
	 *         separator
	 */
	public static final Component getVerticalSeparator() {
		return Box.createRigidArea(new Dimension(0,6));
	}

	/**
	 * Connects the user from the network.
	 */
	public void connect() {
		RouterService.connect();
	}

	/**
	 * Disconnects the user to the network.
	 */
	public void disconnect() {
		RouterService.disconnect();
	}

	/**
	 * Returns a <tt>boolean</tt> specifying whether or not the user has
	 * donated to the LimeWire project.
	 *
	 * @return <tt>true</tt> if the user has donated, <tt>false</tt> otherwise
	 */
	public static boolean hasDonated() {
		return HAS_DONATED;
	}

	/**
	 * Sets the visible/invisible state of the tab associated with the
	 * specified index.  The indeces correspond to the order of the tabs
	 * whether or not they are visible, as specified by the tab indices in
	 * this class.
	 *
	 * @param TAB_INDEX the index of the tab to make visible or invisible
	 * @param VISIBLE the visible/invisible state to set the tab to
	 */
	public void setTabVisible(final int TAB_INDEX, final boolean VISIBLE) {
		MAIN_FRAME.setTabVisible(TAB_INDEX, VISIBLE);
	}

	/**
	 * Modifies the text displayed to the user in the splash screen to
	 * provide application loading information.
	 *
	 * @param text the text to display
	 */
	public static void setSplashScreenString(String text) {
	    if(!_allowVisible)
		    SplashWindow.setStatusText(text);
        else if(isConstructed())
            instance().STATUS_LINE.setStatusText(text);
	}

	/**
	 * Returns the point for the placing the specified component on the
	 * center of the screen.
	 *
	 * @param comp the <tt>Component</tt> to use for getting the relative
	 *             center point
	 * @return the <tt>Point</tt> for centering the specified
	 *         <tt>Component</tt> on the screen
	 */
	public static Point getScreenCenterPoint(Component comp) {
		final Dimension COMPONENT_DIMENSION = comp.getSize();
		Dimension screenSize =
			Toolkit.getDefaultToolkit().getScreenSize();
		int appWidth = Math.min(screenSize.width,
		                        COMPONENT_DIMENSION.width);
		// compare against a little bit less than the screen size,
		// as the screen size includes the taskbar
		int appHeight = Math.min(screenSize.height - 40,
		                         COMPONENT_DIMENSION.height);
		return new Point((screenSize.width - appWidth) / 2,
		                 (screenSize.height - appHeight) / 2);
	}

	/**
	 * Adds the <tt>FinalizeListener</tt> class to the list of classes that
	 * should be notified of finalize events.
	 *
	 * @param fin the <tt>FinalizeListener</tt> class that should be notified
	 */
	public static void addFinalizeListener(FinalizeListener fin) {
	    Finalizer.addFinalizeListener(fin);
	}

	/**
	 * Sets the searching or not searching status of the application.
	 *
	 * @param searching the searching status of the application
	 */
	public void setSearching(boolean searching) {
		MAIN_FRAME.setSearching(searching);
	}

	/**
	 * Adds the specified <tt>RefreshListener</tt> instance to the list of
	 * listeners to be notified when a UI refresh event occurs.
	 *
	 * @param the new <tt>RefreshListener</tt> to add
	 */
	public static void addRefreshListener(RefreshListener listener) {
		if (!REFRESH_LIST.contains(listener))
			REFRESH_LIST.add(listener);
	}

	/**
	 * Removes the specified <tt>RefreshListener</tt> instance from the list
	 * of listeners to be notified when a UI refresh event occurs.
	 * 
	 * @param the <tt>RefreshListener</tt> to remove
	 */
	public static void removeRefreshListener(RefreshListener listener) {
		REFRESH_LIST.remove(listener);
	}
	
	/**
	 * Returns the <tt>Locale</tt> instance currently in use.
	 *
	 * @return the <tt>Locale</tt> instance currently in use
	 */
	public static Locale getLocale() {
		return ResourceManager.getLocale();
	}

	/**
	 * Launches the specified audio file in the player.
	 *
	 * @param file the <tt>File</tt> instance to launch
	 */
	public void launchAudio(File file) {
		MediaPlayerComponent.launchAudio(file);
	}

    /**
     * Makes the update message show up in the status panel
     */
    public void showUpdateNotification(UpdateInformation info) {
        boolean popup = info.getUpdateCommand() != null;
        STATUS_LINE.showUpdatePanel(popup, info);
    }

   /**
    * Trigger a search based on a string.
    *
    * @param query the query <tt>String</tt>
    * @return the GUID of the query sent to the network.
    *         Used mainly for testing
    */
   public byte[] triggerSearch(String query) {
       MAIN_FRAME.setSelectedIndex(SEARCH_INDEX);
       return SearchMediator.triggerSearch(query);
    }
    
    /**
     * Notification that the button state has changed.
     */
    public void buttonViewChanged() {
        IconManager.instance().wipeButtonIconCache();
        updateButtonView(FRAME);
    }
    
    private void updateButtonView(Component c) {
        if (c instanceof IconButton) {
            ((IconButton) c).updateUI();
        }
        Component[] children = null;
        if (c instanceof Container) {
            children = ((Container)c).getComponents();
        }
        if (children != null) {
            for(int i = 0; i < children.length; i++) {
                updateButtonView(children[i]);
            }
        }
    }
   
    /**
     * trigger a browse host based on address and port
     */
    public void doBrowseHost(String address, int port) {
        MAIN_FRAME.setSelectedIndex(SEARCH_INDEX);
        SearchMediator.doBrowseHost(address, port, null);
    }

    /**
     * safely run code synchroneously in the event dispatching thread.
     */
    public static void safeInvokeAndWait(Runnable runnable) {
        if (EventQueue.isDispatchThread())
            runnable.run();
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getTargetException();
                if(t instanceof Error)
                    throw (Error)t;
                else if(t instanceof RuntimeException)
                    throw (RuntimeException)t;
                else
                    ErrorService.error(t);
            } catch(InterruptedException ignored) {}
        }
    }
	
	/**
	 * InvokesLater if not already in the dispatch thread.
	 */
	public static void safeInvokeLater(Runnable runnable) {
		if (EventQueue.isDispatchThread())
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	/**
	 * Changes whether the media player is enabled and updates the GUI accordingly. 
	 */
	public void setPlayerEnabled(boolean value) {
		if (value == PlayerSettings.PLAYER_ENABLED.getValue())
			return;
		PlayerSettings.PLAYER_ENABLED.setValue(value);
		getStatusLine().refresh();
		LIBRARY_MEDIATOR.setPlayerEnabled(value);
		LibraryPlayListTab.setPlayerEnabled(value);
	}
}

