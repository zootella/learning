package com.limegroup.gnutella.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.mp3.MediaPlayerComponent;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.PlayerSettings;
import com.limegroup.gnutella.settings.StatusBarSettings;
import com.limegroup.gnutella.statistics.BandwidthStat;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.version.UpdateInformation;

/**
 * The component for the space at the bottom of the main application
 * window, including the connected status and the media player.
 */
public final class StatusLine implements ThemeObserver {

	/**
     * The different connection status possibilities.
     */
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_POOR = 2;
    public static final int STATUS_FAIR = 3;
    public static final int STATUS_GOOD = 4;
    public static final int STATUS_EXCELLENT = 5;
    public static final int STATUS_TURBOCHARGED = 6;
    public static final int STATUS_IDLE = 7;
    public static final int STATUS_WAKING_UP = 8;

    /**
     * The main container for the status line component.
     */
    private final JPanel BAR = new JPanel(new GridBagLayout());
    
    /**
     * The left most panel containing the connection quality.
     * The switcher changes the actual ImageIcons on this panel.
     */
    private final JLabel _connectionQualityMeter = new JLabel();
    private final ImageIcon[] _connectionQualityMeterIcons = new ImageIcon[9];

    /**
     * The label with the firewall status.
     */
    private final JLabel _firewallStatus = new JLabel();
	
    /**
     * The custom component for displaying the number of shared files.
     */
    private final SharedFilesLabel _sharedFiles = new SharedFilesLabel();
    
	/**
	 * The labels for displaying the bandwidth usage.
	 */
	private final JLabel _bandwidthUsageDown = new JLabel(GUIMediator.getThemeImage("downloading_small")); 
	private final JLabel _bandwidthUsageUp = new JLabel(GUIMediator.getThemeImage("uploading_small")); 
    
    /**
     * Variables for the center portion of the status bar, which can display
     * the StatusComponent (progress bar during program load), the UpdatePanel
     * (notification that a new version of LimeWire is available), and the
     * StatusLinkHandler (ads for going PRO).
     */
    private final StatusComponent STATUS_COMPONENT = new StatusComponent(StatusComponent.CENTER);
    private final UpdatePanel _updatePanel = new UpdatePanel();
	private final StatusLinkHandler _statusLinkHandler = new StatusLinkHandler();
	private final JPanel _centerPanel = new JPanel(new GridBagLayout());
	private Component _centerComponent = _updatePanel;

    /**
     * The media player.
     */
    private MediaPlayerComponent _mediaPlayer;

    
    ///////////////////////////////////////////////////////////////////////////
    //  Construction
    ///////////////////////////////////////////////////////////////////////////
        
    /**
     * Creates a new status line in the disconnected state.
     */
    public StatusLine() {
        GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_STATUS_WINDOW"));

		GUIMediator.addRefreshListener(REFRESH_LISTENER);
		BAR.addMouseListener(STATUS_BAR_LISTENER);
		GUIMediator.getAppFrame().addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent arg0) { refresh(); }
			public void componentMoved(ComponentEvent arg0) { }
			public void componentShown(ComponentEvent arg0) { }
			public void componentHidden(ComponentEvent arg0) { }
		});
        
		//  make icons and panels for connection quality
        createConnectionQualityPanel();

        //  make the 'Firewall Status' label
        createFirewallLabel();
        
        //  make the 'Sharing X Files' component
		createSharingFilesLabel();

		//  make the 'Bandwidth Usage' label
		createBandwidthLabel();
		
		//  make the center panel
		createCenterPanel();
		
        // Set the bars to not be connected.
        setConnectionQuality(0);

	    ThemeMediator.addThemeObserver(this);

		refresh();
    }

	/**
	 * Redraws the status bar based on changes to StatusBarSettings,
	 * and makes sure it has room to add an indicator before adding it.
	 */
	public void refresh() {
		BAR.removeAll();
        
		//  figure out remaining width, and do not add indicators if no room
		int sepWidth = Math.max(2, createSeparator().getWidth());
		int remainingWidth = BAR.getWidth();
		if (remainingWidth <= 0)
			remainingWidth = ApplicationSettings.APP_WIDTH.getValue();
		
		//  subtract player as needed
		if (GUIMediator.isPlaylistVisible()) {
			if (_mediaPlayer == null)
				_mediaPlayer = MediaPlayerComponent.instance();
			remainingWidth -= sepWidth;
			remainingWidth -= GUIConstants.SEPARATOR / 2;
			remainingWidth -= Math.max(216, _mediaPlayer.getMediaPanel().getWidth());
			remainingWidth -= GUIConstants.SEPARATOR;
		}
		
		//  subtract center component
		int indicatorWidth = _centerComponent.getWidth();
		if (indicatorWidth <= 0)
            if (_updatePanel.shouldBeShown()) {
                indicatorWidth = 190;
			    if (!GUIMediator.hasDonated()) 
                    indicatorWidth = 280;
            }
		remainingWidth -= indicatorWidth;

        //  add components to panel, if room
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0,0,0,0);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = GridBagConstraints.RELATIVE;

        //  add connection quality indicator if there's room
        indicatorWidth = GUIConstants.SEPARATOR +
            Math.max((int)_connectionQualityMeter.getMinimumSize().getWidth(),
                    _connectionQualityMeter.getWidth()) + sepWidth;
        if (StatusBarSettings.CONNECTION_QUALITY_DISPLAY_ENABLED.getValue() &&
                remainingWidth > indicatorWidth) {
            BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
            BAR.add(_connectionQualityMeter, gbc);
            BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
            BAR.add(createSeparator(), gbc);
            remainingWidth -= indicatorWidth;
        }
        
        //  then add firewall display if there's room
        indicatorWidth = GUIConstants.SEPARATOR +
            Math.max((int)_firewallStatus.getMinimumSize().getWidth(),
                    _firewallStatus.getWidth()) + sepWidth;
        if (StatusBarSettings.FIREWALL_DISPLAY_ENABLED.getValue() &&
                remainingWidth > indicatorWidth) {
            BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
            BAR.add(_firewallStatus, gbc);
            BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
            BAR.add(createSeparator(), gbc);
            remainingWidth -= indicatorWidth;
        }
        
		//  add shared files indicator if there's room
		indicatorWidth = GUIConstants.SEPARATOR +
            Math.max((int)_sharedFiles.getMinimumSize().getWidth(),
                    _sharedFiles.getWidth()) + sepWidth;
        if (StatusBarSettings.SHARED_FILES_DISPLAY_ENABLED.getValue() &&
				remainingWidth > indicatorWidth) {
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
			BAR.add(_sharedFiles, gbc);
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
			BAR.add(createSeparator(), gbc);
			remainingWidth -= indicatorWidth;
        }

		//  add bandwidth display if there's room
		indicatorWidth = GUIConstants.SEPARATOR + GUIConstants.SEPARATOR / 2 + sepWidth +
			Math.max((int)_bandwidthUsageDown.getMinimumSize().getWidth(), _bandwidthUsageDown.getWidth()) +
            Math.max((int)_bandwidthUsageUp.getMinimumSize().getWidth(), _bandwidthUsageUp.getWidth());
        if (StatusBarSettings.BANDWIDTH_DISPLAY_ENABLED.getValue() &&
				remainingWidth > indicatorWidth) {
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
			BAR.add(_bandwidthUsageDown, gbc);
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR), gbc);
			BAR.add(_bandwidthUsageUp, gbc);
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
			BAR.add(createSeparator(), gbc);
			remainingWidth -= indicatorWidth;
        }

		BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
        //  make center panel stretchy
        gbc.weightx = 1;
		BAR.add(_centerPanel, gbc);
        gbc.weightx = 0;
		BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);

        //  media player
        if (GUIMediator.isPlaylistVisible()) {
			JPanel jp = _mediaPlayer.getMediaPanel();
			jp.setOpaque(false);
			BAR.add(createSeparator(), gbc);
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR / 2), gbc);
			BAR.add(jp, gbc);
			BAR.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR), gbc);
        }

		BAR.validate();
		BAR.repaint();
	}

	/**
     * Creates a vertical separator for visually separating status bar elements 
     */
    private Component createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        //  separators need preferred size in GridBagLayout
        sep.setPreferredSize(new Dimension(2, 20));
        sep.setMinimumSize(new Dimension(2, 20));
        return sep;
    }

    /**
     * Sets up _connectionQualityMeter's icons.
     */
    private void createConnectionQualityPanel() {
		updateTheme();  // loads images
		_connectionQualityMeter.setOpaque(false);
        _connectionQualityMeter.setMinimumSize(new Dimension(34, 20));
        _connectionQualityMeter.setMaximumSize(new Dimension(90, 30));
		//   add right-click listener
		_connectionQualityMeter.addMouseListener(STATUS_BAR_LISTENER);
	}

	/**
	 * Sets up the 'Sharing X Files' label.
	 */
	private void createSharingFilesLabel() {
        _sharedFiles.setHorizontalAlignment(SwingConstants.LEFT);
	    // don't allow easy clipping
		_sharedFiles.setMinimumSize(new Dimension(24, 20));
		// add right-click listener
		_sharedFiles.addMouseListener(STATUS_BAR_LISTENER);
        //  initialize tool tip
        _sharedFiles.setToolTipText(GUIMediator.getStringResource("STATISTICS_SHARING_TOOLTIP") +
                " " + 0 + " " + GUIMediator.getStringResource("STATISTICS_FILES_TOOLTIP_PENDING"));
	}

	/**
	 * Sets up the 'Firewall Status' label.
	 */
	private void createFirewallLabel() {
		updateFirewall();
		// don't allow easy clipping
		_firewallStatus.setMinimumSize(new Dimension(20, 20));
		// add right-click listener
		_firewallStatus.addMouseListener(STATUS_BAR_LISTENER);
	}

	/**
	 * Sets up the 'Bandwidth Usage' label.
	 */
	private void createBandwidthLabel() {
		updateBandwidth();
		// don't allow easy clipping
		_bandwidthUsageDown.setMinimumSize(new Dimension(60, 20));
		_bandwidthUsageUp.setMinimumSize(new Dimension(60, 20));
		// add right-click listeners
		_bandwidthUsageDown.addMouseListener(STATUS_BAR_LISTENER);
		_bandwidthUsageUp.addMouseListener(STATUS_BAR_LISTENER);
	}

	/**
	 * Sets up the center panel.
	 */
	private void createCenterPanel() {
		_centerPanel.setOpaque(false);
        _updatePanel.setOpaque(false);
		((JComponent)_statusLinkHandler.getComponent()).setOpaque(false);
        STATUS_COMPONENT.setProgressPreferredSize(new Dimension(250, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
		_centerPanel.add(STATUS_COMPONENT, gbc);

		//  add right-click listeners
		_statusLinkHandler.getComponent().addMouseListener(STATUS_BAR_LISTENER);
		_centerPanel.addMouseListener(STATUS_BAR_LISTENER);
		_updatePanel.addMouseListener(STATUS_BAR_LISTENER);
		STATUS_COMPONENT.addMouseListener(STATUS_BAR_LISTENER);
	}

	/**
	 * Updates the center panel if non-PRO.  Periodically rotates between
	 * the update panel and the status link handler. 
	 */
	private void updateCenterPanel() {
		long now = System.currentTimeMillis();
		if (_nextUpdateTime > now)
			return;

		_nextUpdateTime = now + 1000 * 5; // update every minute
		_centerPanel.removeAll();
		if (GUIMediator.hasDonated()) {
			if (_updatePanel.shouldBeShown())
				_centerComponent = _updatePanel;
			else
				_centerComponent = new JLabel();
		} else {
			if ((_centerComponent == _statusLinkHandler.getComponent()) && _updatePanel.shouldBeShown())
				_centerComponent = _updatePanel;
			else
				_centerComponent = _statusLinkHandler.getComponent();
		}
		
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        _centerPanel.add(_centerComponent, gbc);
		
		refresh();
	}
	private long _nextUpdateTime = System.currentTimeMillis();

    /**
     * Tells the status linke that the update panel should be shown with
     * the given update information.
     */
    public void showUpdatePanel(boolean popup, UpdateInformation info) {
        _updatePanel.makeVisible(popup, info);
    }
    
    /**
     * Updates the status text.
     */
    void setStatusText(final String text) {
        GUIMediator.safeInvokeAndWait(new Runnable() {
            public void run() {
                STATUS_COMPONENT.setText(text);
            }
        });
    }

	/**
	 * Updates the firewall text. 
	 */
	public void updateFirewallLabel(boolean notFirewalled) {
		if (notFirewalled) {
			_firewallStatus.setIcon(GUIMediator.getThemeImage("firewall_no"));
			_firewallStatus.setToolTipText(GUIMediator.getStringResource("STATUS_BAR_FIREWALL_NOT_FOUND_TOOLTIP"));
		} else {
			_firewallStatus.setIcon(GUIMediator.getThemeImage("firewall"));
			_firewallStatus.setToolTipText(GUIMediator.getStringResource("STATUS_BAR_FIREWALL_FOUND_TOOLTIP"));
		}
	}
	
	/**
	 * Updates the firewall text. 
	 */
	public void updateFirewall() {
		updateFirewallLabel(RouterService.acceptedIncomingConnection());
	}
	

    //  variables for time averaging the download and upload bandwidths
    private int   _numTimeSlices = 3;
    private int[] _pastDownloads = new int[_numTimeSlices];
    private int[] _pastUploads   = new int[_numTimeSlices];
    private int   _pastBandwidthIndex = 0;
    
	/**
	 * Updates the bandwidth statistics.
	 */
	public void updateBandwidth() {
		//  calculate time-averaged stats
        _pastDownloads[_pastBandwidthIndex] = BandwidthStat.HTTP_DOWNSTREAM_BANDWIDTH.getLastStored();
        _pastUploads[_pastBandwidthIndex] = BandwidthStat.HTTP_UPSTREAM_BANDWIDTH.getLastStored();
        _pastBandwidthIndex = (_pastBandwidthIndex + 1) % _numTimeSlices;
        int downBW = 0;
        int upBW = 0; 
        for (int i = 0; i < _numTimeSlices; i++)
            downBW += _pastDownloads[i];
        downBW /= _numTimeSlices;
        for (int i = 0; i < _numTimeSlices; i++)
            upBW += _pastUploads[i];
        upBW /= _numTimeSlices;

        //  format strings
        String sDown = GUIUtils.rate2speed(downBW/1000f);
		String sUp = GUIUtils.rate2speed(upBW/1000f);
        int downloads = RouterService.getNumActiveDownloads();
        int uploads = RouterService.getNumUploads();
		_bandwidthUsageDown.setText(downloads + " @ " + sDown);
		_bandwidthUsageUp.setText(uploads +   " @ " + sUp);
        
		//  create good-looking table tooltip
		String tooltip = "<html><table>" +
			"<tr><td>" + GUIMediator.getStringResource("OPTIONS_STATUS_BAR_BANDWIDTH_DOWNLOADS") + "</td><td>" + downloads +
			"</td><td>@</td><td align=right>" + sDown + "</td></tr>" +
			"<tr><td>" + GUIMediator.getStringResource("OPTIONS_STATUS_BAR_BANDWIDTH_UPLOADS") + "</td><td>" + uploads +
			"</td><td>@</td><td align=right>" + sUp + "</td></tr></table></html>";
		_bandwidthUsageDown.setToolTipText(tooltip);
		_bandwidthUsageUp.setToolTipText(tooltip);
	}
	
    /**
     * Notification that loading has finished.
     *
     * The loading label is removed and the update notification
     * component is added.  If necessary, the center panel will
     * rotate back and forth between displaying the update
     * notification and displaying the StatusLinkHandler.
     */
    void loadFinished() {
		updateCenterPanel();
		_centerPanel.revalidate();
        _centerPanel.repaint();
		refresh();
    }

	/**
     * Load connection quality theme icons
	 */
	public void updateTheme() {
        for (int i = 0; i < _connectionQualityMeterIcons.length; i++)
            _connectionQualityMeterIcons[i] = GUIMediator.getThemeImage("connect_small_" + i);
        
		if (_mediaPlayer != null)
			_mediaPlayer.updateTheme();
	}

    /**
     * Alters the displayed connection quality.
     *
     * @modifies this
     */
    public void setConnectionQuality(int quality) {
        // make sure we don't go over our bounds.
        if (quality >= _connectionQualityMeterIcons.length)
            quality = _connectionQualityMeterIcons.length - 1;

        _connectionQualityMeter.setIcon(_connectionQualityMeterIcons[quality]);

        String status = null;
        String tip = null;
        String connection = GUIMediator.getStringResource("STATISTICS_CONNECTION_QUALITY");
        switch(quality) {
            case STATUS_DISCONNECTED:
                	status = GUIMediator.getStringResource("STATISTICS_CONNECTION_DISCONNECTED");
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_DISCONNECTED_TIP");
                    break;
            case STATUS_CONNECTING:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_CONNECTING") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_CONNECTING_TIP");
                    break;
            case STATUS_POOR:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_POOR") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_POOR_TIP");
                    break;
            case STATUS_FAIR:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_FAIR") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_FAIR_TIP");
                    break;
            case STATUS_GOOD:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_GOOD") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_GOOD_TIP");
                    break;
            case STATUS_IDLE:
            case STATUS_EXCELLENT:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_EXCELLENT") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_EXCELLENT_TIP");
                    break;
            case STATUS_TURBOCHARGED:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_TURBO_CHARGED") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_TURBO_CHARGED_TIP_" +
				            (CommonUtils.isPro() ? "PRO" : "FREE"));
                    break;
            //case STATUS_IDLE:
                    //status = STATISTICS_CONNECTION_IDLE;
                    //tip = null; // impossible to see this
                    //break;
            case STATUS_WAKING_UP:
                    status = GUIMediator.getStringResource("STATISTICS_CONNECTION_WAKING_UP") + " " + connection;
                    tip = GUIMediator.getStringResource("STATISTICS_CONNECTION_WAKING_UP_TIP");
                    break;
        }
        _connectionQualityMeter.setToolTipText(tip);
        if (GUIMediator.hasDonated())
            _connectionQualityMeter.setText(status);
    }

    /**
     * Sets the horizon statistics for this.
     * @modifies this
     * @return A displayable Horizon string.
     */
    public void setStatistics(int share, int pending) {
		_sharedFiles.update(share, pending);
    }

    /**
      * Accessor for the <tt>JComponent</tt> instance that contains all
      * of the panels for the status line.
      *
      * @return the <tt>JComponent</tt> instance that contains all
      *  of the panels for the status line
      */
    public JComponent getComponent() {
        return BAR;
    }
	
    /**
     * The refresh listener for updating the bandwidth usage every second.
     */
    private final RefreshListener REFRESH_LISTENER = new RefreshListener() {
        public void refresh() {
            if (StatusBarSettings.BANDWIDTH_DISPLAY_ENABLED.getValue())
                updateBandwidth();
            updateCenterPanel();
        }
    };
    
    /**
     * The right-click listener for the status bar.
     */
	private final MouseAdapter STATUS_BAR_LISTENER = new MouseAdapter() {
		public void mousePressed(MouseEvent me) { processMouseEvent(me); }
		public void mouseReleased(MouseEvent me) { processMouseEvent(me); }
		public void mouseClicked(MouseEvent me) { processMouseEvent(me); }
		
		public void processMouseEvent(MouseEvent me) {
			if (me.isPopupTrigger()) {
                JPopupMenu jpm = new JPopupMenu();
                
                //  add 'Show Connection Quality' menu item
                JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem(new ShowConnectionQualityAction());
                jcbmi.setState(StatusBarSettings.CONNECTION_QUALITY_DISPLAY_ENABLED.getValue());
                jpm.add(jcbmi);
                
                //  add 'Show Firewall Status' menu item
                jcbmi = new JCheckBoxMenuItem(new ShowFirewallStatusAction());
                jcbmi.setState(StatusBarSettings.FIREWALL_DISPLAY_ENABLED.getValue());
                jpm.add(jcbmi);
                
                //  add 'Show Shared Files Count' menu item 
                jcbmi = new JCheckBoxMenuItem(new ShowSharedFilesCountAction());
                jcbmi.setState(StatusBarSettings.SHARED_FILES_DISPLAY_ENABLED.getValue());
                jpm.add(jcbmi);
                
                //  add 'Show Bandwidth Consumption' menu item
                jcbmi = new JCheckBoxMenuItem(new ShowBandwidthConsumptionAction());
                jcbmi.setState(StatusBarSettings.BANDWIDTH_DISPLAY_ENABLED.getValue());
                jpm.add(jcbmi);
                
                jpm.addSeparator();
                
                //  add 'Show Media Player' menu item
                jcbmi = new JCheckBoxMenuItem(new ShowMediaPlayerAction());
                jcbmi.setState(PlayerSettings.PLAYER_ENABLED.getValue());
                jpm.add(jcbmi);
                
                jpm.show(me.getComponent(), me.getX(), me.getY());
            }
		}
	};

	/**
	 * Action for the 'Show Connection Quality' menu item. 
	 */
	private class ShowConnectionQualityAction extends AbstractAction {
		
		public ShowConnectionQualityAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("STATUS_BAR_SHOW_CONNECTION_QUALITY"));
		}
		
		public void actionPerformed(ActionEvent e) {
			StatusBarSettings.CONNECTION_QUALITY_DISPLAY_ENABLED.invert();
			refresh();
		}
	}
	
	/**
	 * Action for the 'Show Shared Files Count' menu item. 
	 */
	private class ShowSharedFilesCountAction extends AbstractAction {
		
		public ShowSharedFilesCountAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("STATUS_BAR_SHOW_SHARED_FILES_COUNT"));
		}
		
		public void actionPerformed(ActionEvent e) {
			StatusBarSettings.SHARED_FILES_DISPLAY_ENABLED.invert();
			refresh();
		}
	}

	/**
	 * Action for the 'Show Firewall Status' menu item. 
	 */
	private class ShowFirewallStatusAction extends AbstractAction {
		
		public ShowFirewallStatusAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("STATUS_BAR_SHOW_FIREWALL_STATUS"));
		}
		
		public void actionPerformed(ActionEvent e) {
			StatusBarSettings.FIREWALL_DISPLAY_ENABLED.invert();
			refresh();
		}
	}
	
	/**
	 * Action for the 'Show Bandwidth Consumption' menu item. 
	 */
	private class ShowBandwidthConsumptionAction extends AbstractAction {
		
		public ShowBandwidthConsumptionAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("STATUS_BAR_SHOW_BANDWIDTH_CONSUMPTION"));
		}
		
		public void actionPerformed(ActionEvent e) {
			StatusBarSettings.BANDWIDTH_DISPLAY_ENABLED.invert();
			refresh();
		}
	}
	
	/**
	 * Action for the 'Show Media Player' menu item. 
	 */
	private class ShowMediaPlayerAction extends AbstractAction {
		
		public ShowMediaPlayerAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("STATUS_BAR_SHOW_MEDIA_PLAYER"));
		}
		
		public void actionPerformed(ActionEvent e) {
			GUIMediator.instance().setPlayerEnabled(!PlayerSettings.PLAYER_ENABLED.getValue());
		}
	}
	
	/**
	 * Custom component for displaying the number of shared files. 
	 */
	private class SharedFilesLabel extends JLabel {

		/**
		 * The height of this icon.
		 */
		private static final int _height = 20;
		
		/**
		 * The width of this icon.
		 */
		private int _width = 26;
		
		private FontMetrics fm = null;

		private String _string = "0...";
		
		private int _share;
		private int _pending;

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(_width, _height);
		}
		
		/**
		 * Updates the component with information about the sharing state. 
		 */
		public void update(int share, int pending) {
			boolean shareChanged = share != _share;
			boolean pendingChanged = pending != _pending;
			
			_share = share;
			_pending = pending;

			//  if no changes, return
			if (!(shareChanged || pendingChanged))
				return;
			
			_string = GUIUtils.toLocalizedInteger(_share);
			if (!RouterService.getFileManager().isLoadFinished() ||
                    RouterService.getFileManager().isUpdating())
				_string += "...";
			
			if (fm != null)
				_width = fm.stringWidth(_string) + _height;
			
			revalidate();
			repaint();

			//  update tooltip
			if (RouterService.getFileManager().isLoadFinished())
				setToolTipText(GUIMediator.getStringResource("STATISTICS_SHARING_TOOLTIP") +
						" " + share + " " + GUIMediator.getStringResource("STATISTICS_FILES_TOOLTIP"));
			else
				//otherwise display as  'shared / total'
				setToolTipText(GUIMediator.getStringResource("STATISTICS_SHARING_TOOLTIP") +
						" " + share + " " + GUIMediator.getStringResource("STATISTICS_FILES_TOOLTIP_PENDING"));
		}
		
		/**
		 * Paints the icon, and then paints the number of shared files on top of it.
		 */
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			RenderingHints rh = g2.getRenderingHints();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			if (fm == null)
				fm = g2.getFontMetrics();
			
            //  create string, set background color
			if (!RouterService.getFileManager().isLoadFinished() ||
                    RouterService.getFileManager().isUpdating()) {
                g2.setPaint(new Color(165, 165, 2));
                if (!_string.endsWith("..."))
                    _string += "...";
            } else {
                g2.setPaint(new Color(2, 137, 2));
                if (_string.endsWith("..."))
                    _string = _string.substring(0, _string.length() - 3);
            }

            //  figure out size
            int width = fm.stringWidth(_string) + _height; 
            if (width != _width) {
                _width = width;
                revalidate();
            }

			//  draw the round rectangle
			RoundRectangle2D.Float rect
				= new RoundRectangle2D.Float(0, 0, _width-2, _height-2, _height, _height);
			g2.fill(rect);
			
			//  stroke the rectangle
			g2.setColor(Color.black);
			g2.draw(rect);
			
			//  then draw string
			g2.setColor(Color.white);
			g2.drawString(_string, (rect.width - fm.stringWidth(_string)) / 2f,
					(rect.height + fm.getAscent() - fm.getDescent()) / 2f);
			
			g2.setRenderingHints(rh);
		}
	}
}
