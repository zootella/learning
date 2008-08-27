package com.limegroup.gnutella.gui.options;

import com.limegroup.gnutella.gui.options.panes.*;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Static factory class that creates the option panes based on their keys.
 * <p>
 * This class constructs all of the elements of the options window.  To add
 * a new option, this class should be used.  This class allows for options
 * to be added to already existing panes as well as for options to be added
 * to new panes that you can also add here.  To add a new top-level pane,
 * create a new <tt>OptionsPaneImpl</tt> and call the addOption method.
 * To add option items to that pane, add subclasses of
 * <tt>AbstractPaneItem</tt>.
 * <p> 
 * TODO Find a nicer way than the huge if else construct, maybe a hashtable
 * storing string, class pairs using reflection to instantiate the panes.
 */
class OptionsPaneFactory {
    
    /**
     * The cached SaveDirPaneItem.
     */
    private SaveDirPaneItem SAVE_ITEM;
    
    /**
     * The cached SharedDirPaneItem.
     */
    private SharedDirPaneItem SHARED_ITEM;
    
    /**
     * Constructs a new OptionsPaneFactory.
     *
     * Due to intermixing within Saved & Shared pane items, these two need special
     * setups.
     */
    OptionsPaneFactory() {
        SHARED_ITEM = new SharedDirPaneItem("SHARED_DIRS");
        SAVE_ITEM = new SaveDirPaneItem("SAVE_DIR", SHARED_ITEM);
    }
    
    /**
     * Gets the shared item, for use with setting shared directories.
     */
    SharedDirPaneItem getSharedPane() {
        return SHARED_ITEM;
    }
	
	/**
	 * Creates the options pane for a key. 
	 * @param key keys are listed in {@link OptionsConstructor}.
	 * @return
	 */
	OptionsPane createOptionsPane(String key) {
		// Create the keys for the main panes.  These are used as
		// unique identifiers for the windows as well as for keys
		// for the locale-specific string used to display them
		
		if (key.equals(OptionsConstructor.SAVE_KEY)) {
			final OptionsPane savingPane = new OptionsPaneImpl(OptionsConstructor.SAVE_KEY);
			savingPane.add(SAVE_ITEM);
			savingPane.add(new PurgeIncompletePaneItem("PURGE_INCOMPLETE_TIME"));
			return savingPane;
		}
		else if (key.equals(OptionsConstructor.SHARED_KEY)) {
			final OptionsPane sharingPane = new OptionsPaneImpl(OptionsConstructor.SHARED_KEY);
			sharingPane.add(SHARED_ITEM);
			sharingPane.add(new ExtensionsPaneItem("SHARED_EXTENSIONS"));
			sharingPane.add(new ShareSpeciallyPaneItem("SHARED_SHARE_SPECIALLY"));
			return sharingPane;
		}
		else if (key.equals(OptionsConstructor.SPEED_KEY)) {
			final OptionsPane speedPane = new OptionsPaneImpl(OptionsConstructor.SPEED_KEY);
			speedPane.add(new SpeedPaneItem("SPEED"));
			speedPane.add(new DisableSupernodeModePaneItem("DISABLE_SUPERNODE_MODE"));
			speedPane.add(new DisableOOBSearchingPaneItem("DISABLE_OOB_SEARCHING"));
			return speedPane;
		}
		else if (key.equals(OptionsConstructor.DOWNLOAD_KEY)) {
			final OptionsPane downloadPane = new OptionsPaneImpl(OptionsConstructor.DOWNLOAD_KEY);
			downloadPane.add(new MaximumDownloadsPaneItem("DOWNLOAD_MAX"));
			downloadPane.add(new AutoClearDownloadsPaneItem("DOWNLOAD_CLEAR"));
			downloadPane.add(new DownloadBandwidthPaneItem("DOWNLOAD_BANDWIDTH"));
            return downloadPane;
		}
		else if (key.equals(OptionsConstructor.UPLOAD_BASIC_KEY)) {
			final OptionsPane uploadBasicPane = new OptionsPaneImpl(OptionsConstructor.UPLOAD_BASIC_KEY);
			uploadBasicPane.add(new AutoClearUploadsPaneItem("UPLOAD_CLEAR"));
			uploadBasicPane.add(new UploadBandwidthPaneItem("UPLOAD_BANDWIDTH"));
			uploadBasicPane.add(new PartialFileSharingPaneItem("UPLOAD_ALLOW_PARTIAL_SHARING"));
			return uploadBasicPane;
		}
		else if (key.equals(OptionsConstructor.UPLOAD_SLOTS_KEY)) {
			final OptionsPane uploadSlotsPane = new OptionsPaneImpl(OptionsConstructor.UPLOAD_SLOTS_KEY);
			uploadSlotsPane.add(new PerPersonUploadsPaneItem("UPLOAD_PER_PERSON"));
			//uploadSlotsPane.add(new SoftMaximumUploadsPaneItem("UPLOAD_SOFT_MAX"));
			uploadSlotsPane.add(new MaximumUploadsPaneItem("UPLOAD_MAX"));
			return uploadSlotsPane;
		}
		else if (key.equals(OptionsConstructor.CONNECTIONS_KEY)) {
			final OptionsPane connectionsPane = new OptionsPaneImpl(OptionsConstructor.CONNECTIONS_KEY);
			connectionsPane.add(new ConnectOnStartupPaneItem("CONNECT_ON_STARTUP"));
			//connectionsPane.add(new AutoConnectPaneItem("AUTO_CONNECT"));
			//connectionsPane.add(new AutoConnectActivePaneItem("AUTO_CONNECT_ACTIVE"));
			return connectionsPane;
		}
		else if (key.equals(OptionsConstructor.SHUTDOWN_KEY)) {
			final OptionsPane shutdownPane = new OptionsPaneImpl(OptionsConstructor.SHUTDOWN_KEY);
			shutdownPane.add(new ShutdownPaneItem("SHUTDOWN"));
            if (CommonUtils.supportsTray())
                shutdownPane.add(new TrayIconDisplayPaneItem("TRAY_ICON_DISPLAY"));
			return shutdownPane;
		}
		else if (key.equals(OptionsConstructor.UPDATE_KEY)) {
			final OptionsPane updatePane = new OptionsPaneImpl(OptionsConstructor.UPDATE_KEY);
			updatePane.add(new UpdatePaneItem("UPDATE"));
			return updatePane;
		}
		else if (key.equals(OptionsConstructor.CHAT_KEY)) {
			final OptionsPane chatPane = new OptionsPaneImpl(OptionsConstructor.CHAT_KEY);
			chatPane.add(new ChatActivePaneItem("CHAT_ACTIVE"));
			return chatPane;
		}
		else if (key.equals(OptionsConstructor.PLAYER_KEY)) {
			final OptionsPane playerPane = new OptionsPaneImpl(OptionsConstructor.PLAYER_KEY);
			playerPane.add(new PlayerPreferencePaneItem("PLAYER_PREFERENCE"));
			return playerPane;
		}
        else if (key.equals(OptionsConstructor.STATUS_BAR_KEY)) {
            final OptionsPane statusBarPane = new OptionsPaneImpl(OptionsConstructor.STATUS_BAR_KEY);
            statusBarPane.add(new StatusBarConnectionQualityPaneItem("STATUS_BAR_CONNECTION_QUALITY"));
            statusBarPane.add(new StatusBarFirewallPaneItem("STATUS_BAR_FIREWALL"));
            statusBarPane.add(new StatusBarSharedFilesPaneItem("STATUS_BAR_SHARED_FILES"));
            statusBarPane.add(new StatusBarBandwidthPaneItem("STATUS_BAR_BANDWIDTH"));
            return statusBarPane;
        }
		else if (key.equals(OptionsConstructor.ITUNES_IMPORT_KEY)) {
			final OptionsPane itunesPane = new OptionsPaneImpl(OptionsConstructor.ITUNES_IMPORT_KEY);
			itunesPane.add(new iTunesPreferencePaneItem("ITUNES_PREFERENCE"));
			return itunesPane;
		}
		else if (key.equals(OptionsConstructor.ITUNES_DAAP_KEY)) {
			final OptionsPane daapPane = new OptionsPaneImpl(OptionsConstructor.ITUNES_DAAP_KEY);
			daapPane.add(new DaapSupportPaneItem("ITUNES_DAAP_PREFERENCE"));
			daapPane.add(new DaapPasswordPaneItem("ITUNES_DAAP_PASSWORD"));
			return daapPane;
		}
		else if (key.equals(OptionsConstructor.APPS_KEY)) {
			final OptionsPane browserPane = new OptionsPaneImpl(OptionsConstructor.APPS_KEY);
			browserPane.add(new BrowserPaneItem("BROWSER_PREFERENCE"));
			browserPane.add(new ImageViewerPaneItem("IMAGE_VIEWER_PREFERENCE"));
			browserPane.add(new VideoPlayerPaneItem("VIDEO_PLAYER_PREFERENCE"));
			browserPane.add(new AudioPlayerPaneItem("AUDIO_PLAYER_PREFERENCE"));
			return browserPane;
		}
		else if (key.equals(OptionsConstructor.BUGS_KEY)) {
			final OptionsPane bugsPane = new OptionsPaneImpl(OptionsConstructor.BUGS_KEY);
			bugsPane.add( new BugsPaneItem("BUGS") );
			return bugsPane;
		}
		else if (key.equals(OptionsConstructor.POPUPS_KEY)) {
			final OptionsPane popupsPane = new OptionsPaneImpl(OptionsConstructor.POPUPS_KEY);
			popupsPane.add( new PopupsPaneItem("POPUPS") );
			return popupsPane;
		}
        else if (key.equals(OptionsConstructor.AUTOCOMPLETE_KEY)) {
			final OptionsPane autocompletePane = 
				new OptionsPaneImpl(OptionsConstructor.AUTOCOMPLETE_KEY);
			autocompletePane.add(new AutoCompletePaneItem("AUTOCOMPLETE"));
			return autocompletePane;
		}
		else if (key.equals(OptionsConstructor.SEARCH_LIMIT_KEY)) {
			final OptionsPane searchLimitPane = 
				new OptionsPaneImpl(OptionsConstructor.SEARCH_LIMIT_KEY);
			searchLimitPane.add(new MaximumSearchesPaneItem("SEARCH_MAX"));
            searchLimitPane.add(new DownloadLicenseWarningPaneItem("DOWNLOAD_LICENSE_WARNING"));
            return searchLimitPane;
		}
		else if (key.equals(OptionsConstructor.SEARCH_QUALITY_KEY)) {
			final OptionsPane searchQualityPane = 
				new OptionsPaneImpl(OptionsConstructor.SEARCH_QUALITY_KEY);
			searchQualityPane.add(new SearchQualityPaneItem("SEARCH_QUALITY"));
			return searchQualityPane;
		}
		else if (key.equals(OptionsConstructor.SEARCH_SPEED_KEY)) {
			final OptionsPane searchSpeedPane = 
				new OptionsPaneImpl(OptionsConstructor.SEARCH_SPEED_KEY);
			searchSpeedPane.add(new SearchSpeedPaneItem("SEARCH_SPEED"));
			return searchSpeedPane;
		}
		else if (key.equals(OptionsConstructor.RESULTS_KEY)) {
			final OptionsPane filtersResultsPane = new OptionsPaneImpl(OptionsConstructor.RESULTS_KEY);
			filtersResultsPane.add(new IgnoreResultsPaneItem("IGNORE_RESULTS"));
			filtersResultsPane.add(new IgnoreResultTypesPaneItem("IGNORE_RESULT_TYPES"));
			return filtersResultsPane;
		}
		else if (key.equals(OptionsConstructor.MESSAGES_KEY)) {
			final OptionsPane filtersMessagesPane = 
				new OptionsPaneImpl(OptionsConstructor.MESSAGES_KEY);
			filtersMessagesPane.add(new IgnoreMessagesPaneItem("IGNORE_MESSAGES"));
			filtersMessagesPane.add(new AllowMessagesPaneItem("ALLOW_MESSAGES"));
			return filtersMessagesPane;
		}
		else if (key.equals(OptionsConstructor.PREFERENCING_KEY)) {
			final OptionsPane preferencingPane = 
				new OptionsPaneImpl(OptionsConstructor.PREFERENCING_KEY);
			preferencingPane.add(new ConnectionPreferencingPaneItem("CONNECT_PREF"));
			return preferencingPane;
		}
		else if (key.equals(OptionsConstructor.FIREWALL_KEY)) {
			final OptionsPane portPane = new OptionsPaneImpl(OptionsConstructor.FIREWALL_KEY);
			portPane.add(new PortPaneItem("PORT"));
			portPane.add(new ForceIPPaneItem("ROUTER"));
			return portPane;
		}
        else if (key.equals(OptionsConstructor.PROXY_KEY)) {
			final OptionsPane proxyPane = new OptionsPaneImpl(OptionsConstructor.PROXY_KEY);
			proxyPane.add(new ProxyPaneItem("PROXY"));
			proxyPane.add(new ProxyLoginPaneItem("PROXY_LOGIN"));
			return proxyPane;
		}
		else if (key.equals(OptionsConstructor.STARTUP_KEY)) {
            final OptionsPane startupPane = new OptionsPaneImpl(OptionsConstructor.STARTUP_KEY);
            startupPane.add(new StartupPaneItem("STARTUP"));
			return startupPane;
		}
		else {
			throw new IllegalArgumentException("no options pane for this key: " + key);
		}
	}

}
