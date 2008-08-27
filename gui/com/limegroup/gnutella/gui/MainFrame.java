package com.limegroup.gnutella.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.TabbedPaneUI;

import com.limegroup.gnutella.gui.connection.ConnectionMediator;
import com.limegroup.gnutella.gui.download.DownloadMediator;
import com.limegroup.gnutella.gui.library.LibraryMediator;
import com.limegroup.gnutella.gui.menu.MenuMediator;
import com.limegroup.gnutella.gui.options.OptionsMediator;
import com.limegroup.gnutella.gui.playlist.PlaylistMediator;
import com.limegroup.gnutella.gui.search.MagnetClipboardListener;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.gui.statistics.StatisticsMediator;
import com.limegroup.gnutella.gui.tabs.ConnectionsTab;
import com.limegroup.gnutella.gui.tabs.LibraryPlayListTab;
import com.limegroup.gnutella.gui.tabs.MonitorUploadTab;
import com.limegroup.gnutella.gui.tabs.SearchDownloadTab;
import com.limegroup.gnutella.gui.tabs.Tab;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.gui.upload.UploadMediator;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.PlayerSettings;
import com.limegroup.gnutella.settings.SettingsHandler;
import com.limegroup.gnutella.util.CommonUtils;


/**
 * This class constructs the main <tt>JFrame</tt> for the program as well as 
 * all of the other GUI classes.  
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class MainFrame implements ComponentListener, RefreshListener,
    ThemeObserver {

    /**
     * Handle to the <tt>JTabbedPane</tt> instance.
     */
    private final JTabbedPane TABBED_PANE =
        new JTabbedPane();

    /**
     * Constant handle to the <tt>SearchMediator</tt> class that is
     * responsible for displaying search results to the user.
     */
    private final SearchMediator SEARCH_MEDIATOR =
        new SearchMediator();

     /**
     * Constant handle to the <tt>DownloadMediator</tt> class that is
     * responsible for displaying active downloads to the user.
     */
    private final DownloadMediator DOWNLOAD_MEDIATOR =
        DownloadMediator.instance();

    /**
     * Constant handle to the <tt>MonitorView</tt> class that is
     * responsible for displaying incoming search queries to the user.
     */
    private final MonitorView MONITOR_VIEW =
        new MonitorView();

    /**
     * Constant handle to the <tt>UploadMediator</tt> class that is
     * responsible for displaying active uploads to the user.
     */
    private final UploadMediator UPLOAD_MEDIATOR =
        UploadMediator.instance();

    /**
     * Constant handle to the <tt>ConnectionView</tt> class that is
     * responsible for displaying current connections to the user.
     */
    private final ConnectionMediator CONNECTION_MEDIATOR =
        ConnectionMediator.instance();

    /**
     * Constant handle to the <tt>LibraryView</tt> class that is
     * responsible for displaying files in the user's repository.
     */
    private final LibraryMediator LIBRARY_MEDIATOR =
        LibraryMediator.instance();

    /**
     * Constant handle to the <tt>StatisticsMediator</tt> class that is
     * responsible for displaying statistics to the user.
     */
    private final StatisticsMediator STATISTICS_MEDIATOR =
        StatisticsMediator.instance();

    /**
     * Constant handle to the <tt>OptionsMediator</tt> class that is
     * responsible for displaying customizable options to the user.
     */
    private final OptionsMediator OPTIONS_MEDIATOR =
        OptionsMediator.instance();

    /**
     * Constant handle to the <tt>StatusLine</tt> class that is
     * responsible for displaying the status of the network and
     * connectivity to the user.
     */
    private final StatusLine STATUS_LINE = new StatusLine();

    /**
     * Handle the <tt>MenuMediator</tt> for use in changing the menu
     * depending on the selected tab.
     */
    private final MenuMediator MENU_MEDIATOR =
        MenuMediator.instance();

    /**
     * The main <tt>JFrame</tt> for the application.
     */
    private final JFrame FRAME;

    /**
     * Is the download view currently being shown? 
     */
    private boolean isDownloadViewVisible = false;

    /**
     * Constant for the <tt>LogoPanel</tt> used for displaying the
     * lime/spinning lime search status indicator and the logo.
     */
    private final LogoPanel LOGO_PANEL = new LogoPanel();

    /**
     * The array of tabs in the main application window.
     */
    private Tab[] TABS = null;

	private int height;

	private boolean isSearching = false;
	
    /** 
     * Initializes the primary components of the main application window,
     * including the <tt>JFrame</tt> and the <tt>JTabbedPane</tt>
     * contained in that window.
     */
    MainFrame(JFrame frame) {
        FRAME = frame;

        
        // Setup the Tabs structure based on advertising mode and Windows
        buildTabs();

        TABBED_PANE.setPreferredSize(new Dimension(10000, 10000));
        ImageIcon limeIcon = GUIMediator.getThemeImage(GUIConstants.LIMEWIRE_ICON);
        FRAME.setIconImage(limeIcon.getImage());

        FRAME.addWindowListener(new WindowAdapter() {

            public void windowDeiconified(WindowEvent e) {
                // Handle reactivation on systems which do not support
                // the system tray.  Windows systems call the
                // WindowsNotifyUser.restoreApplication()
                // method to restore applications from minimize and
                // auto-shutdown modes.  Non-windows systems restore
                // the application using the following code.
                if(!CommonUtils.supportsTray()) {
                    GUIMediator.restoreView();
                }
            }

            public void windowClosing(WindowEvent e) {
                // save the screen size and location 
                Dimension dim = GUIMediator.getAppSize();
                Point loc = GUIMediator.getAppLocation();
                ApplicationSettings.APP_WIDTH.setValue(dim.width);
                ApplicationSettings.APP_HEIGHT.setValue(dim.height);
                ApplicationSettings.WINDOW_X.setValue(loc.x);
                ApplicationSettings.WINDOW_Y.setValue(loc.y);
                SettingsHandler.save();
                GUIMediator.close(true);
            }

        });

        FRAME.addComponentListener(this);

        FRAME.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setFrameDimensions();

        // add all tabs initially....
        for (int i = 0; i < TABS.length; i++) {
            this.addTab(TABS[i]);
        }

        TABBED_PANE.setRequestFocusEnabled(false);

        TABBED_PANE.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                TabbedPaneUI ui = TABBED_PANE.getUI();
                int idx = ui.tabForCoordinate(TABBED_PANE, e.getX(), e.getY());
                if(idx != -1)
                    idx = getTabIndex(idx); // get the real index.
                if(idx != -1)
                    TABS[idx].mouseClicked();
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });          

        // remove tabs according to Settings Manager...
        if (!ApplicationSettings.MONITOR_VIEW_ENABLED.getValue())
            this.setTabVisible(GUIMediator.MONITOR_INDEX, false);
        if (!ApplicationSettings.CONNECTION_VIEW_ENABLED.getValue())
            this.setTabVisible(GUIMediator.CONNECTIONS_INDEX, false);
        if (!ApplicationSettings.LIBRARY_VIEW_ENABLED.getValue())
            this.setTabVisible(GUIMediator.LIBRARY_INDEX, false);

        FRAME.setJMenuBar(MENU_MEDIATOR.getMenuBar());
        JPanel contentPane = new JPanel();
        FRAME.setContentPane(contentPane);
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        contentPane.add(TABBED_PANE, gbc);
        gbc.weighty = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(STATUS_LINE.getComponent(), gbc);
      

        JLayeredPane layeredPane =
            JLayeredPane.getLayeredPaneAbove(TABBED_PANE);
        layeredPane.add(LOGO_PANEL, JLayeredPane.PALETTE_LAYER, 0);

        ThemeMediator.addThemeObserver(this);
        GUIMediator.addRefreshListener(this);

        updateLogoHeight();
        
        //TODO: add this to the settings so that the user can disable it
        //instead, make it unix-only.  It only gets in the way on other oses.
        //Since Mac OS X is based on BSD its a good idea to check that too
        if (!CommonUtils.isWindows() && !CommonUtils.isAnyMac()) 
        	FRAME.addWindowListener(MagnetClipboardListener.getInstance());
        	
        PowerManager pm = new PowerManager();
        FRAME.addWindowListener(pm);
        GUIMediator.addRefreshListener(pm);
    }

    // inherit doc comment
    public void updateTheme() {
        FRAME.setJMenuBar(MENU_MEDIATOR.getMenuBar());
        LOGO_PANEL.updateTheme();
        setSearchIconLocation();
        updateLogoHeight();
	}
    
    private void updateLogoHeight() {
        // necessary so that the logo does not intrude on the content below
        Rectangle rect = TABBED_PANE.getUI().getTabBounds(TABBED_PANE, 0);
        Dimension ld = LOGO_PANEL.getPreferredSize();
        int height = ld.height + 4;
		this.height = Math.max(rect.height, height);
        if (rect.height < height)
            TABBED_PANE.setBorder(BorderFactory.createEmptyBorder(
                height - rect.height, 0, 0, 0));
        else
            TABBED_PANE.setBorder(null);
    }

    /**
     * Build the Tab Structure based on advertising mode and Windows
     */
    private void buildTabs() {
        TABS = new Tab[4];
        TABS[0]=new SearchDownloadTab(SEARCH_MEDIATOR, DOWNLOAD_MEDIATOR);
        TABS[1]=new MonitorUploadTab(MONITOR_VIEW, UPLOAD_MEDIATOR);
        TABS[2]=new ConnectionsTab(CONNECTION_MEDIATOR);
        TABS[3]=new LibraryPlayListTab(LIBRARY_MEDIATOR);
    }

    
    /**
     * Adds a tab to the <tt>JTabbedPane</tt> based on the data supplied
     * in the <tt>Tab</tt> instance.
     *
     * @param tab the <tt>Tab</tt> instance containing data for the tab to
     *  add
     */
    private void addTab(Tab tab) {
        TABBED_PANE.addTab(tab.getTitle(), tab.getIcon(),
                           tab.getComponent(), tab.getToolTip());
    }

    /**
     * Inserts a tab in the <tt>JTabbedPane</tt> at the specified index, 
     * based on the data supplied in the <tt>Tab</tt> instance.
     *
     * @param tab the <tt>Tab</tt> instance containing data for the tab to
     *  add
     */
    private void insertTab(Tab tab, int index) {
        TABBED_PANE.insertTab(tab.getTitle(), tab.getIcon(),
                              tab.getComponent(), tab.getToolTip(),
                              index);
        // the component tree must be updated so that the new tab
        // fits the current theme (if the theme was changed at runtime)
        SwingUtilities.updateComponentTreeUI(TABBED_PANE);
        ThemeMediator.updateThemeObservers();
    }

    /**
     * Sets the selected index in the wrapped <tt>JTabbedPane</tt>.
     *
     * @param index the tab index to select
     */
    final void setSelectedIndex(int index) {
        int i = getTabIndex(index);
        if (i == -1)
            return;
        TABBED_PANE.setSelectedIndex(i);
    }

    void updateTabIcon(int index) {
        int i = getTabIndex(index);
        if (i == -1)
            return;
        TABBED_PANE.setIconAt(i, TABS[index].getIcon());
    }

    /**
     * Sets the x,y location as well as the height and width of the main
     * application <tt>Frame</tt>.
     */
    private final void setFrameDimensions() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int appWidth  = 0;
        int appHeight = 0;
        int locX = 0;
        int locY = 0;

        // Set the location of our window based on whether or not
        // the user has run the program before, and therefore may have 
        // modified the location of the main window.
        
        appWidth = Math.min(screenSize.width, ApplicationSettings.APP_WIDTH.getValue());
        appHeight = Math.min(screenSize.height - 40, ApplicationSettings.APP_HEIGHT.getValue());
        
        if(ApplicationSettings.RUN_ONCE.getValue()) {
            locX = ApplicationSettings.WINDOW_X.getValue();
            locY = ApplicationSettings.WINDOW_Y.getValue();
        } else {
            locX = (screenSize.width - appWidth) / 2;
            locY = (screenSize.height - appHeight) / 2;
        }
        
        // normalize the locX & Y
        if(appWidth + locX > screenSize.width)
            locX = Math.max(0, screenSize.width - appWidth);
        if(appHeight + locY + 40 > screenSize.height)
            locY = Math.max(0, screenSize.height - appHeight - 40);
        
        FRAME.setLocation(locX, locY);
        FRAME.setSize(new Dimension(appWidth, appHeight));
        FRAME.getContentPane().setSize(new Dimension(appWidth, appHeight));
        ((JComponent)FRAME.getContentPane()).setPreferredSize(new Dimension(appWidth, appHeight));
    }


    /**
     * Sets the visible/invisible state of the tab associated with the
     * specified index.  The indeces correspond to the order of the
     * tabs whether or not they are visible, as specified in 
     * <tt>GUIMediator</tt>.
     *
     * @param TAB_INDEX the index of the tab to make visible or 
     *  invisible
     * @param VISIBLE the visible/invisible state to set the tab to
     */
    void setTabVisible(final int TAB_INDEX, final boolean VISIBLE) {
        if ((TAB_INDEX == 0) || (TAB_INDEX > (TABS.length - 1)))
            throw new IllegalArgumentException(
                "Invalid tab index: " + TAB_INDEX);

        Tab tab = TABS[TAB_INDEX];
        Component comp = tab.getComponent();
        int tabCount = TABBED_PANE.getTabCount();

        if (!VISIBLE) {
            // remove the tab from the tabbed pane
            for (int i = 0; i < tabCount; i++) {
                if (comp.equals(TABBED_PANE.getComponentAt(i))) {
                    TABBED_PANE.remove(i);
                    break;
                }
            }
        } else {
            // make sure the current one is invisible.
            JComponent selComp =
                (JComponent)TABBED_PANE.getSelectedComponent();
            selComp.setVisible(false);
            
            // add the tab to the tabbed pane
            for (int i = 0; i < tabCount; i++) {                
                Component comp1 = TABBED_PANE.getComponentAt(i);
                int index = getIndex(comp1);
                if (index == -1) {
                    selComp.setVisible(true);
                    return;
                }
                if (index > TAB_INDEX) {
                    insertTab(TABS[TAB_INDEX], i);
                    break;
                } else if (i == tabCount - 1) {
                    insertTab(TABS[TAB_INDEX], i + 1);
                }
            }
            
            JComponent jcomp = (JComponent)comp;
            jcomp.invalidate();
            jcomp.revalidate();
            jcomp.repaint();
        }

        MENU_MEDIATOR.setNavMenuItemEnabled(TAB_INDEX, VISIBLE);
        tab.storeState(VISIBLE);
    }

    /**
     * This method gets the fixed index for the specified <tt>Component</tt>
     * instance, or the index as specified by the constants in
     * <tt>GUIMediator</tt>.  The component passed in must be the component
     * for one of the tabs in the main applicaiton window.
     *
     * @param comp a <tt>Component</tt> instance for one of the tabs
     * @return the fixed index for the tab that contains the component
     *  argument, or -1 if the component is not contained in any of the tabs
     */
    private int getIndex(Component comp) {
        for (int i = 0; i < TABS.length; i++) {
            if (comp.equals(TABS[i].getComponent()))
                return TABS[i].getIndex();
        }
        return -1;
    }

    /**
     * Returns the index in the tabbed pane of the specified "real" index
     * argument.  The values for this argument are listed in
     * <tt>GUIMediator</tt>.
     *
     * @param index the "real" index of the tab, meaning that this index
     *  is independent of what is currently visible in the tab
     * @return the index in the tabbed pane of the specified real index,
     *  or -1 if the specified index is not found
     */
    private int getTabIndex(int index) {
        int tabCount = TABBED_PANE.getTabCount();
        Component comp = TABS[index].getComponent();
        for (int i = 0; i < tabCount; i++) {
            Component tabComp = TABBED_PANE.getComponentAt(i);
            if (tabComp.equals(comp))
                return i;
        }
        return -1;
    }

    /**
     * Should be called whenever state may have changed, so MainFrame can then
     * re-layout window (if necessary).
     */
    public void refresh() {

		if (isSearching) {
			// if we're searching make sure the search result panel
			// is visible
		    SearchDownloadTab tab = (SearchDownloadTab)TABS[GUIMediator.SEARCH_INDEX];
			if (tab.getDividerLocation() == 0) {
				tab.setDividerLocation(0.5);
				isDownloadViewVisible = true;
			}
		}
		
        // first handle the download view
        if (DOWNLOAD_MEDIATOR.getActiveDownloads() == 0 &&
                isDownloadViewVisible) {
            ((SearchDownloadTab)TABS[GUIMediator.SEARCH_INDEX]).
                setDividerLocation(1000);
            isDownloadViewVisible = false;
        } else if (DOWNLOAD_MEDIATOR.getActiveDownloads() > 0 &&
                 !isDownloadViewVisible) {
            // need to turn it on....
            final int count = DOWNLOAD_MEDIATOR.getActiveDownloads();
            // make sure stuff didn't change on me....
            if (count > 0) {
                final double prop = (count > 6) ? 0.60 : 0.70;
                ((SearchDownloadTab)TABS[GUIMediator.SEARCH_INDEX]).
                    setDividerLocation(prop);
                ((SearchDownloadTab)TABS[GUIMediator.SEARCH_INDEX]).
                    getComponent().revalidate();
                TABBED_PANE.revalidate();
                isDownloadViewVisible = true;
            }
        }
    }

    /**
     * Returns a reference to the <tt>SearchMediator</tt> instance.
     *
     * @return a reference to the <tt>SearchMediator</tt> instance
     */
    final SearchMediator getSearchMediator() {
        return SEARCH_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>DownloadMediator</tt> instance.
     *
     * @return a reference to the <tt>DownloadMediator</tt> instance
     */
    final DownloadMediator getDownloadMediator() {
        return DOWNLOAD_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>MonitorView</tt> instance.
     *
     * @return a reference to the <tt>MonitorView</tt> instance
     */
    final MonitorView getMonitorView() {
        return MONITOR_VIEW;
    }

    /**
     * Returns a reference to the <tt>UploadMediator</tt> instance.
     *
     * @return a reference to the <tt>UploadMediator</tt> instance
     */
    final UploadMediator getUploadMediator() {
        return UPLOAD_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>ConnectionMediator</tt> instance.
     *
     * @return a reference to the <tt>ConnectionMediator</tt> instance
     */
    final ConnectionMediator getConnectionMediator() {
        return CONNECTION_MEDIATOR;
    }


    /**
     * Returns a reference to the <tt>LibraryMediator</tt> instance.
     *
     * @return a reference to the <tt>LibraryMediator</tt> instance
     */
    final LibraryMediator getLibraryMediator() {
        return LIBRARY_MEDIATOR;
    }
    
    /**
     * Returns a reference to the <tt>PlaylistMediator</tt> instance.
     *
     * @return a reference to the <tt>PlaylistMediator</tt> instance or
     * <code>null</code> if the playlist is not enabled
     */
    static final PlaylistMediator getPlaylistMediator() {
        return PlayerSettings.PLAYER_ENABLED.getValue() ?
                PlaylistMediator.instance() : null;
    }    

    /**
     * Returns a reference to the <tt>StatusLine</tt> instance.
     *
     * @return a reference to the <tt>StatusLine</tt> instance
     */
    final StatusLine getStatusLine() {
        return STATUS_LINE;
    }

    /**
     * Returns a reference to the <tt>MenuMediator</tt> instance.
     *
     * @return a reference to the <tt>MenuMediator</tt> instance
     */
    final MenuMediator getMenuMediator() {
        return MENU_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>OptionsMediator</tt> instance.
     *
     * @return a reference to the <tt>OptionsMediator</tt> instance
     */
    final OptionsMediator getOptionsMediator() {
        return OPTIONS_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>StatisticsMediator</tt> instance.
     *
     * @return a reference to the <tt>StatisticsMediator</tt> instance
     */
    final StatisticsMediator getStatisticsMediator() {
        return STATISTICS_MEDIATOR;
    }

    /**
     * Returns a reference to the <tt>StatisticsView</tt> instance.
     *
     * @return a reference to the <tt>StatisticsView</tt> instance
     */
    //final StatisticsView getStatisticsView() {
    //return STATISTICS_VIEW;
    //}

    /**
     * Sets the searching or not searching status of the application.
     *
     * @param searching the searching status of the application
     */
    final void setSearching(boolean searching) {    
        LOGO_PANEL.setSearching(searching);
		isSearching = searching;
		refresh();
    }

    // implements the ComponentListener interface
    public void componentHidden(ComponentEvent e) {
    }

    // implements the ComponentListener interface
    public void componentMoved(ComponentEvent e) {
    }

    // implements the ComponentListener interface
    public void componentResized(ComponentEvent e) {
        this.setSearchIconLocation();
    }

    // implements the ComponentListener interface
    public void componentShown(ComponentEvent e) {
        this.setSearchIconLocation();
    }

    /**
     * Sets the location of the search status icon.
     */
    private void setSearchIconLocation() {
		int y = MENU_MEDIATOR.getMenuBarHeight() 
			+ (height - LOGO_PANEL.getPreferredSize().height) / 2;
        LOGO_PANEL.setLocation(
            FRAME.getSize().width - LOGO_PANEL.getSize().width - 12,
            y);
    }
}
