package com.limegroup.gnutella.gui.search;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.limegroup.gnutella.BrowseHostHandler;
import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.Endpoint;
import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.actions.SearchAction;
import com.limegroup.gnutella.gui.download.DownloaderFactory;
import com.limegroup.gnutella.gui.download.DownloaderUtils;
import com.limegroup.gnutella.gui.download.SearchResultDownloaderFactory;
import com.limegroup.gnutella.search.HostData;
import com.limegroup.gnutella.settings.FileSetting;
import com.limegroup.gnutella.settings.QuestionsHandler;
import com.limegroup.gnutella.settings.SearchSettings;
import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.util.IpPort;
import com.limegroup.gnutella.util.StringUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLProperties;

/**
 * This class acts as a mediator between the various search components --
 * the hub that all traffic passes through.  This allows the decoupling of
 * the various search packages and simplfies the responsibilities of the
 * underlying classes.
 */
public final class SearchMediator {

	/**
	 * Query text is valid.
	 */
	public static final int QUERY_VALID = 0;
	/**
	 * Query text is empty.
	 */
	public static final int QUERY_EMPTY = 1;
	/**
	 * Query text is too short.
	 */
	public static final int QUERY_TOO_SHORT = 2;
	/**
	 * Query text is too long.
	 */
	public static final int QUERY_TOO_LONG = 3;
	/**
	 * Query xml is too long.
	 */
	public static final int QUERY_XML_TOO_LONG = 4;
    /**
     * Query contains invalid characters.
     */
    public static final int QUERY_INVALID_CHARACTERS = 5;
	
	
	static final String DOWNLOAD_STRING =
        GUIMediator.getStringResource("SEARCH_DOWNLOAD_BUTTON_LABEL");
    static final String KILL_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_KILL_STRING");
    static final String STOP_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_STOP_STRING");
    static final String LAUNCH_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_LAUNCH_STRING");
    static final String BROWSE_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_BROWSE_STRING");
    static final String CHAT_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_CHAT_STRING");
    static final String REPEAT_SEARCH_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_REPEAT_SEARCH_STRING");
    static final String BROWSE_HOST_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_BROWSE_STRING");
    static final String BITZI_LOOKUP_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_BITZI_LOOKUP_STRING");
    static final String BLOCK_STRING =
        GUIMediator.getStringResource("SEARCH_PUBLIC_BLOCK_STRING");

    /** A name of attribute, which holds a query in state of downloaded file. */
    public static final String SEARCH_INFORMATION_KEY = "searchInformationMap";

    /**
     * Variable for the component that handles all search input from the user.
     */
    private static final SearchInputManager INPUT_MANAGER =
        new SearchInputManager();

    /**
     * This instance handles the display of all search results.
     */
    private static final SearchResultDisplayer RESULT_DISPLAYER =
        new SearchResultDisplayer();

    /**
     * Constructs the UI components of the search result display area of the 
     * search tab.
     */
    public SearchMediator() {
        // Set the splash screen text...
        final String splashScreenString =
            GUIMediator.getStringResource("SPLASH_STATUS_SEARCH_WINDOW");
        GUIMediator.setSplashScreenString(splashScreenString);
        GUIMediator.addRefreshListener(RESULT_DISPLAYER);
        
        // Link up the tabs of results with the filters of the input screen.
        RESULT_DISPLAYER.setSearchListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ResultPanel panel = RESULT_DISPLAYER.getSelectedResultPanel();
                if(panel == null)
                    INPUT_MANAGER.clearFilters();
                else
                    INPUT_MANAGER.setFiltersFor(panel);
            }
        });
    }
    
    /**
     * Rebuilds the INPUT_MANAGER's panel.
     */
    public static void rebuildInputPanel() {
        INPUT_MANAGER.rebuild();
    }
    
    /**
     * Notification that the address has changed -- pass it along.
     */
    public static void addressChanged() {
        INPUT_MANAGER.addressChanged();
    }
    
    /**
     * Informs the INPUT_MANAGER that we want to display the searching
     * window.
     */
    public static void showSearchInput() {
        INPUT_MANAGER.goToSearch();
    }
    
    /**
     * Requests the search focus in the INPUT_MANAGER.
     */
    public static void requestSearchFocus() {
        INPUT_MANAGER.requestSearchFocus();
    }
    
    /**
     * Updates all current results.
     */
    public static void updateResults() {
        RESULT_DISPLAYER.updateResults();
    }

    /** 
     * Repeats the given search.
     */
    static byte[] repeatSearch(ResultPanel rp, SearchInformation info) {
        if(!validate(info))
            return null;

        // 1. Update panel with new GUID
        byte [] guidBytes = RouterService.newQueryGUID();
        final GUID newGuid = new GUID(guidBytes);

        RouterService.stopQuery(new GUID(rp.getGUID()));
        rp.setGUID(newGuid);
        INPUT_MANAGER.panelReset(rp);
        
        if(info.isBrowseHostSearch()) {
            IpPort ipport = info.getIpPort();
            String host = ipport.getAddress();
            int port = ipport.getPort();
            if(host != null && port != 0) {
                GUIMediator.instance().setSearching(true);
                reBrowseHost(host, port, rp);
            }
        } else {
            GUIMediator.instance().setSearching(true);
            doSearch(guidBytes, info);
        }

        return guidBytes;
    }

    /**
     * Browses the first selected host. Fails silently if couldn't browse.
     */
    static void doBrowseHost(ResultPanel rp) {
        TableLine line = rp.getSelectedLine();
        if(line == null)
            return;
            
        // Get the browse-host RFD from the line.
        RemoteFileDesc rfd = line.getBrowseHostEnabledRFD();
        if(rfd == null)
            return;
        
        // See if it is firewalled
        byte[] serventIDBytes = rfd.getClientGUID();
        // if the reply is to a multicast query, don't use any
        // push proxies so we definitely will send a UDP push request
        final Set proxies = rfd.isReplyToMulticast() ? 
            Collections.EMPTY_SET : rfd.getPushProxies();
        final GUID serventID = new GUID(serventIDBytes);
        // Get the host's address....
        final String host = rfd.getHost();
        final int port = rfd.getPort();
        
        doBrowseHost2(host, port, serventID, proxies, rfd.supportsFWTransfer());
     }

    /**
     * Allows for browsing of a host from outside of the search package.
     */
    public static void doBrowseHost(final RemoteFileDesc rfd) {
        doBrowseHost2(rfd.getHost(), rfd.getPort(),
                      new GUID(rfd.getClientGUID()), rfd.getPushProxies(),
                      rfd.supportsFWTransfer());
    }

    /**
     * Allows for browsing of a host from outside of the search package
     * without an rfd.
     */
    public static void doBrowseHost(final String host, final int port,
                                    final GUID guid) {
        if (guid == null)
            doBrowseHost2(host, port, null, null, false);
        else
            doBrowseHost2(host, port, new GUID(guid.bytes()), null, false);
    }

    /**
     * Re-browses the host.  Fails silently if browse failed...
     * TODO: WILL NOT WORK FOR RE-BROWSES THAT REQUIRES A PUSH!!!
     */
    private static void reBrowseHost(final String host, final int port,
                             ResultPanel in) {
        // Update the GUID
        final GUID guid = new GUID(GUID.makeGuid());
        in.setGUID(guid);
        BrowseHostHandler bhh =
            RouterService.doAsynchronousBrowseHost(host, port, guid, 
                                                   new GUID(GUID.makeGuid()), 
                                                   null, false);
                                         
        in.setBrowseHostHandler(bhh);
        INPUT_MANAGER.panelReset(in);
    }

    /**
     * Browses the passed host at the passed port.
     * Fails silently if couldn't browse.
     * @param host The host to browse
     * @param port The port at which to browse
     */
    static private void doBrowseHost2(String host, int port,
                                      GUID serventID, 
                                      Set proxies, boolean canDoFWTransfer) {
        // Update the GUI
        GUID guid = new GUID(GUID.makeGuid());
        ResultPanel rp = addBrowseHostTab(guid, host + ":" + port);
        // Do the actual browse host
        BrowseHostHandler bhh = RouterService.doAsynchronousBrowseHost(
                                    host, port, guid, serventID, proxies,
                                    canDoFWTransfer);
        
        rp.setBrowseHostHandler(bhh);
    }

    /**
     * Call this when a Browse Host fails.
     * @param guid The guid associated with this Browse. 
     */
    public static void browseHostFailed(GUID guid) {
        RESULT_DISPLAYER.browseHostFailed(guid);
    }
    
    /**
     * Initiates a new search with the specified SearchInformation.
     *
     * Returns the GUID of the search if a search was initiated,
     * otherwise returns null.
     */
    public static byte[] triggerSearch(SearchInformation info) {
        if(!validate(info))
            return null;
            
        // generate a guid for the search.
        byte[] guid = RouterService.newQueryGUID();
        // only add tab if this isn't a browse-host search.
        if(!info.isBrowseHostSearch()) {
            addResultTab(new GUID(guid), info);
        }
        
        if(info.isKeywordSearch())
            GUIMediator.instance().checkForJavaVersion();
        
        doSearch(guid, info);
        return guid;
    }
    
    /**
     * Triggers a search given the text in the search field.  For testing
     * purposes returns the 16-byte GUID of the search or null if the search
     * didn't happen because it was greedy, etc.  
     */
    public static byte[] triggerSearch(String query) {
        return triggerSearch(
            SearchInformation.createKeywordSearch(query, null, 
                                  MediaType.getAnyTypeMediaType())
        );
    }
    
    /**
     * Validates the given search information.
     */
    private static boolean validate(SearchInformation info) {
    
		switch (validateInfo(info)) {
		case QUERY_EMPTY:
			return false;
		case QUERY_TOO_SHORT:
			GUIMediator.showError("ERROR_THREE_CHARACTER_SEARCH");
			return false;
		case QUERY_TOO_LONG:
			String xml = info.getXML();
			if (xml == null || xml.length() == 0) {
				GUIMediator.showError("ERROR_SEARCH_TOO_LARGE");
			}
			else {
				GUIMediator.showError("ERROR_XML_SEARCH_TOO_LARGE");
			}
			return false;
		case QUERY_XML_TOO_LONG:
            GUIMediator.showError("ERROR_XML_SEARCH_TOO_LARGE");
			return false;
		case QUERY_VALID:
		default:
	        // only show search messages if not doing browse host.
	        if(!info.isBrowseHostSearch()) {
	            if(!RouterService.isConnected()) {
	                // if not connected or connecting, show one message.
	                if(!RouterService.isConnecting())
	                    GUIMediator.showMessage("SEARCH_NOT_CONNECTED", QuestionsHandler.NO_NOT_CONNECTED);
	                else // if attempting to connect, show another.
	                    GUIMediator.showMessage("SEARCH_STILL_CONNECTING", QuestionsHandler.NO_STILL_CONNECTING);
	            }
	        }
			return true;
		}
    }
	
	
	/**
	 * Validates the a search info and returns {@link #QUERY_VALID} if it is
	 * valid.
	 * @param info
	 * @return one of the static <code>QUERY*</code> fields
	 */
	public static int validateInfo(SearchInformation info) {
		
		String query = info.getQuery();
        String xml = info.getXML();
        MediaType media = info.getMediaType();
        
		if (query.length() == 0) {
			return QUERY_EMPTY;
		}
		else if (query.length() <= 2 && !(query.length() == 2 && 
				((Character.isDigit(query.charAt(0)) && 
						Character.isLetter(query.charAt(1)))   ||
						(Character.isLetter(query.charAt(0)) && 
								Character.isDigit(query.charAt(1)))))) {
			return QUERY_TOO_SHORT;
		} 
		else if (query.length() > SearchSettings.MAX_QUERY_LENGTH.getValue()) {
			return QUERY_TOO_LONG;
		} 
		else if (xml != null 
				 &&  xml.length() > SearchSettings.MAX_XML_QUERY_LENGTH.getValue()) {
			return QUERY_XML_TOO_LONG;
		} 
        
        if (StringUtils.containsCharacters(query,SearchSettings.ILLEGAL_CHARS.getValue()))
            return QUERY_INVALID_CHARACTERS;
        
		return QUERY_VALID;
	}
    
    /**
     * Does the actual search.
     */
    private static void doSearch(byte[] guid, SearchInformation info) {
        String query = info.getQuery();
        String xml = info.getXML();
        MediaType media = info.getMediaType();

        if(info.isXMLSearch()) {
            RouterService.query(guid, query, xml, media);
        } else if(info.isKeywordSearch()) {
            RouterService.query(guid, query, media);
        } else if(info.isWhatsNewSearch()) {
            RouterService.queryWhatIsNew(guid, media);
        } else if(info.isBrowseHostSearch()) {
            IpPort ipport = info.getIpPort();
            doBrowseHost(ipport.getAddress(), ipport.getPort(), null);
        }
    }
    
    /**
     * Adds a single result tab for the specified GUID, type,
     * standard query string, and XML query string.
     */
    private static ResultPanel addResultTab(GUID guid,
                                            SearchInformation info) {
        return RESULT_DISPLAYER.addResultTab(guid, info);
    }

    /**
     * Adds a browse host tab with the given description.
     */
    private static ResultPanel addBrowseHostTab(GUID guid, String desc) {
        return RESULT_DISPLAYER.addResultTab(guid, 
            SearchInformation.createBrowseHostSearch(desc)
        );
    }
    
    /**
     * If i rp is no longer the i'th panel of this, returns silently.
     * Otherwise adds line to rp under the given group.  Updates the count
     * on the tab in this and restarts the spinning lime.
     * @requires this is called from Swing thread
     * @modifies this
     */
    public static void handleQueryResult(RemoteFileDesc rfd,
                                         HostData data,
                                         Set alts) {
        byte[] replyGUID = data.getMessageGUID();
        ResultPanel rp = getResultPanelForGUID(new GUID(replyGUID));
        if(rp != null) {
            SearchResult sr = new SearchResult(rfd, data, alts);
            RESULT_DISPLAYER.addQueryResult(replyGUID, sr, rp);
        }
    }

    /**
     * Downloads the selected files in the currently displayed
     * <tt>ResultPanel</tt> if there is one.
     */
    static void doDownload(final ResultPanel rp) {
        final TableLine[] lines = rp.getAllSelectedLines();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SearchMediator.downloadAll(lines, new GUID(rp.getGUID()),
                                           rp.getSearchInformation());
                rp.refresh();
            }
        });
    }
	
	/**
	 * Opens a dialog where you can specify the download directory and final
	 * filename for the selected file.
	 * @param panel
	 * @throws IllegalStateException when there is more than one file selected
	 * for download or there is no file selected.
	 */
	static void doDownloadAs(final ResultPanel panel) {
		final TableLine[] lines = panel.getAllSelectedLines();
		if (lines.length != 1) {
			throw new IllegalStateException("There should only be one search result selected");
		}
		downloadLine(lines[0], new GUID(panel.getGUID()), null, null, true,
                     panel.getSearchInformation());
	}

    /**
     * Downloads all the selected lines.
     */
    private static void downloadAll(TableLine[] lines, GUID guid, 
                                    SearchInformation searchInfo) 
    {
        for(int i = 0; i < lines.length; i++)
            downloadLine(lines[i], guid, null, null, false, searchInfo);
    }
    
    /**
     * Downloads the given TableLine.
     * @param line
     * @param guid
     * @param saveDir optionally the directory where the final file should be
     * saved to, can be <code>null</code>
     * @param fileName the optional filename of the final file, can be
     * <code>null</code>
     * @param searchInfo The query used to find the file being downloaded.
     */
    private static void downloadLine(TableLine line, GUID guid, File saveDir,
			String fileName, boolean saveAs, SearchInformation searchInfo) 
    {
        if (line == null)
            throw new NullPointerException("Tried to download null line");
        
		//  do not download if no license and user does not acknowledge
		if (!line.isLicenseAvailable() && !GUIMediator.showFirstDownloadDialog())
			return;
		
        RemoteFileDesc[] rfds;
        Set /* of EndpointPoint */ alts = new HashSet();
        List /* of RemoteFileDesc */ otherRFDs = new LinkedList();
        
        rfds = line.getAllRemoteFileDescs();
        alts.addAll(line.getAlts());
        
        
        // Iterate through RFDs and remove matching alts.
        // Also store the first SHA1 capable RFD for collecting alts.
        RemoteFileDesc sha1RFD = null;
        for(int i = 0; i < rfds.length; i++) {
            RemoteFileDesc next = rfds[i];
			// this has been moved down until the download is actually started
            // next.setDownloading(true);
            next.setRetryAfter(0);
            if(next.getSHA1Urn() != null)
                sha1RFD = next;
            alts.remove(new Endpoint(next.getHost(), next.getPort()));
        }

        // If no SHA1 rfd, just use the first.
        if(sha1RFD == null)
            sha1RFD = rfds[0];
        
        // Now iterate through alts & add more rfds.
        for(Iterator i = alts.iterator(); i.hasNext(); ) {
            Endpoint next = (Endpoint)i.next();
            otherRFDs.add(new RemoteFileDesc(sha1RFD, next));
        }
		
		// determine per mediatype directory if saveLocation == null
		// and only pass it through if directory is different from default
		// save directory == !isDefault()
		if (saveDir == null && line.getNamedMediaType() != null) {
			FileSetting fs = SharingSettings.getFileSettingForMediaType
			(line.getNamedMediaType().getMediaType());
			if (!fs.isDefault()) {
				saveDir = fs.getValue();
			}
		}

        downloadWithOverwritePrompt(rfds, otherRFDs, guid, saveDir, fileName, 
                                    saveAs, searchInfo);
    }

    /**
     * Downloads the given files, prompting the user if the file already exists.
     * @param queryGUID the guid of the query you ar downloading rfds for.
     * @param searchInfo The query used to find the file being downloaded.
     */
    private static void downloadWithOverwritePrompt(RemoteFileDesc[] rfds,
                                                    List alts, GUID queryGUID,
                                                    File saveDir, String fileName,
                                                    boolean saveAs, 
                                                    SearchInformation searchInfo) 
    {
        if (rfds.length < 1)
            return;
        if (containsExe(rfds)) {
            if (!userWantsExeDownload())
                return;
        }

        // Before proceeding...check if there is an rfd withpure metadata
        // ie no file
        int actLine = 0;
        boolean pureFound = false;
        for (; actLine < rfds.length; actLine++) {
            if (rfds[actLine].getIndex() ==
               LimeXMLProperties.DEFAULT_NONFILE_INDEX) {
                // we have our line
                pureFound = true;
                break;
            }
        }
        
        if (pureFound) {
            LimeXMLDocument doc = rfds[actLine].getXMLDocument();
            String action = doc.getAction();
            if (action != null && !action.equals("")) { // valid action
                GUIMediator.openURL(action);
                return; // goodbye
            }
        }
        // No pure metadata lines found...continue as usual...
        DownloaderFactory factory = new SearchResultDownloaderFactory
        	(rfds, alts, queryGUID, saveDir, fileName); 
		Downloader dl = saveAs ? DownloaderUtils.createDownloaderAs(factory) 
				: DownloaderUtils.createDownloader(factory);
		if (dl != null) {
			setAsDownloading(rfds);
            if (validateInfo(searchInfo) == QUERY_VALID)
                dl.setAttribute(SEARCH_INFORMATION_KEY, searchInfo.toMap());
		}
    }

	private static void setAsDownloading(RemoteFileDesc[] rfds) {
		for (int i = 0; i < rfds.length; i++) {
			rfds[i].setDownloading(true);
		}
	}
	
    /**
     * Returns true if any of the entries of rfd contains a .exe file.
     */
    private static boolean containsExe(RemoteFileDesc[] rfd) {
        for (int i = 0; i < rfd.length; i++) {
            if (rfd[i].getFileName().toLowerCase(Locale.US).endsWith("exe"))
                return true;
        }
        return false;
    }

    /**
     * Prompts the user if they want to download an .exe file.
     * Returns true s/he said yes.
     */
    private static boolean userWantsExeDownload() {        
        String middleMsg = GUIMediator.getStringResource("SEARCH_VIRUS_MSG_TWO");        
        int response = GUIMediator.showYesNoMessage("SEARCH_VIRUS_MSG_ONE",
                                                    middleMsg,
                                                    "SEARCH_VIRUS_MSG_THREE",
                                            QuestionsHandler.PROMPT_FOR_EXE);
        return response == GUIMediator.YES_OPTION;
    }

    ////////////////////////// Other Controls ///////////////////////////

    /**
     * called by ResultPanel when the views are changed. Used to set the
     * tab to indicate the correct number of TableLines in the current
     * view.
     */
    static void setTabDisplayCount(ResultPanel rp) {
        RESULT_DISPLAYER.setTabDisplayCount(rp);
    }

    /**
     * @modifies tabbed pane, entries
     * @effects removes the currently selected result window (if any)
     *  from this
     */
    static void killSearch() {
        RESULT_DISPLAYER.killSearch();
    }
    
    /**
     * Notification that a given ResultPanel has been selected
     */
    static void panelSelected(ResultPanel panel) {
        INPUT_MANAGER.setFiltersFor(panel);
    }
    
    /**
     * Notification that a search has been killed.
     */
    static void searchKilled(ResultPanel panel) {
        INPUT_MANAGER.panelRemoved(panel);
        ResultPanel rp = RESULT_DISPLAYER.getSelectedResultPanel();
        if(rp != null)
            INPUT_MANAGER.setFiltersFor(rp);
    }
    
    /**
     * Checks to see if the spinning lime should be stopped.
     */
    static void checkToStopLime() {
        RESULT_DISPLAYER.checkToStopLime();
    }
    
    /**
     * Returns the <tt>ResultPanel</tt> for the specified GUID.
     * 
     * @param rguid the guid to search for
     * @return the <tt>ResultPanel</tt> that matches the GUID, or null
     *  if none match.
     */
    static ResultPanel getResultPanelForGUID(GUID rguid) {
        return RESULT_DISPLAYER.getResultPanelForGUID(rguid);
    }

    /** @returns true if the user is still using the query results for the input
     *  guid, else false.
     */
    public static boolean queryIsAlive(GUID guid) {
        return (getResultPanelForGUID(guid) != null);
    }

    /**
     * Returns the search input panel component.
     *
     * @return the search input panel component
     */
    public static JComponent getSearchComponent() {
        return INPUT_MANAGER.getComponent();
    }

    /**
     * Returns the <tt>JComponent</tt> instance containing all of the
     * search result UI components.
     *
     * @return the <tt>JComponent</tt> instance containing all of the
     *  search result UI components
     */
    public static JComponent getResultComponent() {
        return RESULT_DISPLAYER.getComponent();
    }

	
}

