package com.limegroup.gnutella.gui.download;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.Endpoint;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileDetails;
import com.limegroup.gnutella.FileManager;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.FileDetailsProvider;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.actions.BitziLookupAction;
import com.limegroup.gnutella.gui.actions.CopyMagnetLinkToClipboardAction;
import com.limegroup.gnutella.gui.actions.LimeAction;
import com.limegroup.gnutella.gui.actions.SearchAction;
import com.limegroup.gnutella.gui.options.OptionsMediator;
import com.limegroup.gnutella.gui.playlist.PlaylistMediator;
import com.limegroup.gnutella.gui.search.SearchInformation;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.gui.tables.AbstractTableMediator;
import com.limegroup.gnutella.gui.tables.ColumnPreferenceHandler;
import com.limegroup.gnutella.gui.tables.DataLine;
import com.limegroup.gnutella.gui.tables.DragManager;
import com.limegroup.gnutella.gui.tables.LimeJTable;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.SimpleColumnListener;
import com.limegroup.gnutella.gui.tables.TableSettings;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.gui.util.CoreExceptionHandler;
import com.limegroup.gnutella.settings.QuestionsHandler;
import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.settings.SearchSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;
import com.limegroup.gnutella.util.ProcessingQueue;
import com.limegroup.gnutella.util.StringUtils;

/**
 * This class acts as a mediator between all of the components of the
 * download window.  It also constructs all of the download window
 * components.
 */
public final class DownloadMediator extends AbstractTableMediator
	implements FileDetailsProvider {

	private static final Log LOG = LogFactory.getLog(DownloadMediator.class);
	
    /**
     * Variable for the total number of downloads that have been added in this
     * session.
     */
    private static int _totalDownloads = 0;

    /**
     * Flag for whether or not an mp3 file has been launched from the download
     * window.
     */
    private static boolean _audioLaunched = false;

    /**
     * instance, for singleton acces
     */
    private static DownloadMediator _instance = new DownloadMediator();

    /**
     * The queue that launched items are processed in.
     */
    private static final ProcessingQueue QUEUE = 
        new ProcessingQueue("DownloadLauncher");


    public static DownloadMediator instance() { return _instance; }

    /**
     * Variables so only one ActionListener needs to be created for both
     * the buttons & popup menu.
     */
	private Action removeAction;
    private Action chatAction;
    private Action clearAction;
    private Action browseAction;
    private Action launchAction;
    private Action resumeAction;
    private Action pauseAction;
    private Action priorityUpAction;
    private Action priorityDownAction;
	private Action editLocationAction;
	private Action magnetAction;
	private Action bitziAction;
	private Action exploreAction; 

    /** The actual download buttons instance.
     */
    private DownloadButtons _downloadButtons;
    
    /**
     * Overriden to have different default values for tooltips.
     */
    protected void buildSettings() {
        SETTINGS = new TableSettings(ID) {
            public boolean getDefaultTooltips() {
                return false;
            }
        };
    }

    /**
     * Sets up drag & drop for the table.
     */
    protected void setupDragAndDrop() {
        DragManager.install(TABLE);
    }

    /**
     * Build some extra listeners
     */
    protected void buildListeners() {
        super.buildListeners();

		removeAction = new RemoveAction();
		chatAction = new ChatAction();
		clearAction = new ClearAction();
		browseAction = new BrowseAction();
		launchAction = new LaunchAction();
		resumeAction = new ResumeAction();
		pauseAction = new PauseAction();
		priorityUpAction = new PriorityUpAction();
		priorityDownAction = new PriorityDownAction();
		editLocationAction = new EditLocationAction();
		magnetAction = new CopyMagnetLinkToClipboardAction(this);
		exploreAction = new ExploreAction(); 
		bitziAction = new BitziLookupAction(this);
    }

	/**
	 * Returns the most prominent actions that operate on the download table.
	 * @return
	 */
	public Action[] getActions() {
		Action[] actions;
		if(CommonUtils.isWindows()||CommonUtils.isMacOSX())
			actions = new Action[] { priorityUpAction, priorityDownAction,
				removeAction, resumeAction, pauseAction, launchAction,
				exploreAction,clearAction};
		else 
			actions = new Action[] { priorityUpAction, priorityDownAction,
			removeAction, resumeAction, pauseAction, launchAction,clearAction 
		};
		return actions;
	}
	
    /**
     * Set up the necessary constants.
     */
    protected void setupConstants() {
        MAIN_PANEL =
            new PaddedPanel(GUIMediator.getStringResource("DOWNLOAD_TITLE"));
        DATA_MODEL = new DownloadModel();
        TABLE = new LimeJTable(DATA_MODEL);
        _downloadButtons = new DownloadButtons(this);
        BUTTON_ROW = _downloadButtons.getComponent();
    }
    
    /**
     * Sets up the table headers.
     */
    protected void setupTableHeaders() {
        super.setupTableHeaders();
        
        // set the queue panel to be visible depending on whether or 
        // not the priority column is visible.
        Object pId = DATA_MODEL.getColumnId(DownloadDataLine.PRIORITY_INDEX);
        _downloadButtons.setQueuePanelVisible(TABLE.isColumnVisible(pId));
        
        // add a listener to keep the queue panel in synch with the priority column
        ColumnPreferenceHandler cph = TABLE.getColumnPreferenceHandler();
        cph.setSimpleColumnListener(new SimpleColumnListener() {
            public void columnAdded(LimeTableColumn ltc, LimeJTable table) {
                Assert.that(table == TABLE);
                if(ltc.getModelIndex() == DownloadDataLine.PRIORITY_INDEX)
                    _downloadButtons.setQueuePanelVisible(true);
            }
            
            public void columnRemoved(LimeTableColumn ltc, LimeJTable table) {
                Assert.that(table == TABLE);
                if(ltc.getModelIndex() == DownloadDataLine.PRIORITY_INDEX)
                    _downloadButtons.setQueuePanelVisible(false);
            }
        });
    }

    /**
     * Update the splash screen.
     */
    protected void updateSplashScreen() {
        GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_DOWNLOAD_WINDOW"));
    }

    /**
     * Constructs all of the elements of the download window, including
     * the table, the buttons, etc.
     */
    private DownloadMediator() {
        super("DOWNLOAD_TABLE");
        GUIMediator.addRefreshListener(this);
        ThemeMediator.addThemeObserver(this);
        
        if(SETTINGS.REAL_TIME_SORT.getValue())
            DATA_MODEL.sort(DownloadDataLine.PRIORITY_INDEX); // ascending
    }

    /**
     * Override the default refreshing so that we can
     * set the clear button appropriately.
     */
    public void doRefresh() {
        boolean inactivePresent =
            ((Boolean)DATA_MODEL.refresh()).booleanValue();
        
		clearAction.setEnabled(inactivePresent);
      
		int[] selRows = TABLE.getSelectedRows();
        
		if (selRows.length > 0) {
            DownloadDataLine dataLine = 
                (DownloadDataLine)DATA_MODEL.get(selRows[0]);
            
			if (dataLine.getState() == Downloader.WAITING_FOR_USER) {
				resumeAction.putValue(Action.NAME,
						GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_SOURCES"));
				resumeAction.putValue(LimeAction.SHORT_NAME,
						GUIMediator.getStringResource("DOWNLOAD_SOURCES_BUTTON_LABEL"));
				resumeAction.putValue(Action.SHORT_DESCRIPTION,
						GUIMediator.getStringResource("DOWNLOAD_SOURCES_BUTTON_TIP"));
			}
            else {
				resumeAction.putValue(Action.NAME,
						GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_RESUME"));
				resumeAction.putValue(LimeAction.SHORT_NAME, 
						 GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_LABEL"));
				resumeAction.putValue(Action.SHORT_DESCRIPTION,
						 GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_TIP"));
            }
			
			
            boolean inactive = dataLine.isDownloaderInactive();
            boolean pausable = !dataLine.getDownloader().isPaused() &&
                               !dataLine.getDownloader().isCompleted();                
			resumeAction.setEnabled(inactive);
			pauseAction.setEnabled(pausable);
			priorityUpAction.setEnabled(inactive && pausable);
			priorityDownAction.setEnabled(inactive && pausable);
			exploreAction.setEnabled(dataLine.getDownloader().isCompleted()); 
			
		}
	}

    /**
     * Returns the total number of Downloads that have occurred in this session.
     *
     * @return the total number of Downloads that have occurred in this session
     */
    public int getTotalDownloads() {
        return _totalDownloads;
    }

    /**
     * Returns the total number of current Downloads.
     *
     * @return the total number of current Downloads
     */
    public int getCurrentDownloads() {
        return ((DownloadModel)DATA_MODEL).getCurrentDownloads();
    }

    /**
     * Returns the total number of active Downloads.
     * This includes anything that is still viewable in the Downloads view.
     *
     * @return the total number of active Downloads
     */
    public int getActiveDownloads() {
        return ((DownloadModel)DATA_MODEL).getRowCount();
    }

    /**
     * Overrides the default add.
     *
     * Adds a new Downloads to the list of Downloads, obtaining the necessary
     * information from the supplied <tt>Downloader</tt>.
     *
     * If the download is not already in the list, then it is added.
     *  <p>
     */
    public void add(Object downloader) {
        if ( !DATA_MODEL.contains(downloader) ) {
            _totalDownloads++;
            super.add(downloader);
        }
    }

    /**
     * Overrides the default remove.
     *
     * Takes action upon downloaded theme files, asking if the user wants to
     * apply the theme.
     *
     * Removes a download from the list if the user has configured their system
     * to automatically clear completed download and if the download is
     * complete.
     *
     * @param downloader the <tt>Downloader</tt> to remove from the list if it is
     *  complete.
     */
    public void remove(Object downloader) {
        Downloader dloader = (Downloader)downloader;
        int state = dloader.getState();
        
        if (state == Downloader.COMPLETE 
        		&& isThemeFile(dloader.getSaveFile().getName())) {
        	File themeFile = dloader.getDownloadFragment();
        	themeFile = copyToThemeDir(themeFile);
        	// don't allow changing of theme while options are visible,
        	// but notify the user how to change the theme
        	if (OptionsMediator.instance().isOptionsVisible()) {
        		GUIMediator.showFormattedMessage("DOWNLOAD_EXPLAIN_HOW_TO_CHANGE_THEME",
        				new String[] { ThemeSettings.formatName(dloader.getSaveFile().getName()),
        				GUIMediator.getStringResource("MENU_VIEW_THEMES_REFRESH"),
        				GUIMediator.getStringResource("MENU_VIEW_TITLE"),
        				GUIMediator.getStringResource("MENU_VIEW_THEMES_TITLE"),
        		});
        	}
        	else {
        		int response = GUIMediator.showYesNoMessage(
        				"DOWNLOAD_APPLY_NEW_THEME_START",
        				ThemeSettings.formatName(dloader.getSaveFile().getName()),
        				"DOWNLOAD_APPLY_NEW_THEME_END",
        				QuestionsHandler.THEME_DOWNLOADED);
        		if( response == GUIMediator.YES_OPTION ) {
        			ThemeMediator.changeTheme(themeFile);
        		}
        	}
        }
        
        if(SharingSettings.CLEAR_DOWNLOAD.getValue()
           && ( state == Downloader.COMPLETE ||
                state == Downloader.ABORTED ) ) {
            super.remove(downloader);
        } else {
            DownloadDataLine ddl = (DownloadDataLine)DATA_MODEL.get(downloader);
            if (ddl != null) ddl.setEndTime(System.currentTimeMillis());
        }
    }
    
    private File copyToThemeDir(File themeFile) {
        File themeDir = ThemeSettings.THEME_DIR_FILE;
        File realLoc = new File(themeDir, themeFile.getName());
        // if they're the same, just use it.
        if( realLoc.equals(themeFile) )
            return themeFile;

        // otherwise, if the file already exists in the theme dir, remove it.
        realLoc.delete();
        
        // copy from shared to theme dir.
        CommonUtils.copy(themeFile, realLoc);
        return realLoc;
    }
    
    private boolean isThemeFile(String name) {
        return name.toLowerCase().endsWith(ThemeSettings.EXTENSION);
    }
    

    /**
     * Launches the selected files in the <tt>Launcher</tt> or in the built-in
     * media player.
     */
    void launchSelectedDownloads() {
        final DataLine[] lines = TABLE.getSelectedDataLines();
        _audioLaunched = false;
        for(int i = 0; i < lines.length; i++) {
            final Downloader dl = (Downloader)lines[i].getInitializeObject();
            QUEUE.add(new Runnable() {
                public void run() {
                    File toLaunch = dl.getDownloadFragment();
                    if (toLaunch == null) {
                        GUIMediator.showMessage("NO_PREVIEW_BEGIN", 
                                                dl.getSaveFile().getName(),
                                                "NO_PREVIEW_END",
                                                QuestionsHandler.NO_PREVIEW_REPORT);
                        return;
                    }
                    if (!_audioLaunched && PlaylistMediator.isPlayableFile(toLaunch) && GUIMediator.isPlaylistVisible()) {
                        GUIMediator.instance().launchAudio(toLaunch);
                        _audioLaunched = true;
                    } else {
                        try {
                            GUIMediator.launchFile(toLaunch);
                        } catch (IOException ignored) {}
                    }
                }
            });
        }
    }
    
    /**
     * Pauses all selected downloads.
     */
    void pauseSelectedDownloads() {
        DataLine[] lines = TABLE.getSelectedDataLines();
        for(int i = 0; i < lines.length; i++)
            ((Downloader)lines[i].getInitializeObject()).pause();
    }
    
    /**  
     * Launches explorer
     */ 
    void launchExplorer() { 
        final DataLine[] lines = TABLE.getSelectedDataLines(); 
        final Downloader dl = (Downloader)lines[lines.length-1].getInitializeObject(); 
        File toExplore = dl.getFile(); 
        if (toExplore == null) 
            return; 
         
        String explorePath = toExplore.getPath(); 
        try { 
            explorePath = toExplore.getCanonicalPath(); 
        } catch(IOException ioe) { } 
 
        try { 
            if (CommonUtils.isWindows())
            	//launches explorer and highlights the file
                Runtime.getRuntime().exec(new String[] {"explorer","/select,", explorePath }); 
            else if (CommonUtils.isMacOSX()) {
            	if(toExplore.isFile())explorePath = toExplore.getParent();
            	Runtime.getRuntime().exec(new String[] { "open", explorePath });
            }
        } catch(SecurityException se) { 
        } catch(IOException ieo) { } 
    } 

    /**
     * Changes the priority of the selected downloads by amt.
     */
    void bumpPriority(final boolean up, int amt) {
        DataLine[] lines = TABLE.getSelectedDataLines();

        // sort the lines by priority.
        // this is necessary so that they move in the correct order
        Arrays.sort(lines, new Comparator() {
            public int compare(Object a, Object b) {
                int pa = ((Downloader)((DataLine)a).getInitializeObject()).
                            getInactivePriority();
                int pb = ((Downloader)((DataLine)b).getInitializeObject()).
                            getInactivePriority();
                return (pa < pb ? -1 : pa > pb ? 1 : 0) * ( up ? 1 : -1 );
            }
        });

        for(int i = 0; i < lines.length; i++) {
            Downloader dl = (Downloader)lines[i].getInitializeObject();
            RouterService.getDownloadManager().bumpPriority(dl, up, amt);
        }
    }

    /**
     * Forces the selected downloads in the download window to resume.
     */
    void resumeSelectedDownloads() {
        DataLine[] lines = TABLE.getSelectedDataLines();
        for(int i = 0; i < lines.length; i++) {
            DownloadDataLine dd = (DownloadDataLine)lines[i];
            Downloader downloader = dd.getDownloader();
                if(!dd.isCleaned())
                    downloader.resume();
        }
    }

    /**
     * Opens up a chat session with the selected hosts in the download
     * window.
     */
    void chatWithSelectedDownloads() {
        DataLine[] lines = TABLE.getSelectedDataLines();
        for(int i = 0; i < lines.length; i++) {
            DataLine dl = lines[i];
            Downloader downloader=(Downloader)dl.getInitializeObject();
            Endpoint end = downloader.getChatEnabledHost();
            if (end!=null)
                RouterService.createChat(end.getAddress(), end.getPort());
        }
    }

	/**
	 * Shows file chooser dialog for first selected download.
	 *
	 */
	void editSelectedDownload() {
		DataLine[] lines = TABLE.getSelectedDataLines();
		Downloader dl = (Downloader)lines[0].getInitializeObject();
		File saveLocation = dl.getSaveFile();
		File saveFile = FileChooserHandler.getSaveAsFile(MessageService.getParentComponent(),
				"DOWNLOAD_LOCATION_DIALOG_TITLE", saveLocation);
		if (saveFile == null)
			return;

		try {
			// note: if the user did not change the file location
			// and you try setting the same location as is set,
			// you get an exception because the
			// filename is already taken by the same downloader
			if (!saveFile.equals(dl.getSaveFile())) {
				dl.setSaveFile(saveFile.getParentFile(), saveFile.getName(), false);
			}
		} catch (SaveLocationException sle) {
			CoreExceptionHandler.handleSaveLocationError(sle);
		}
	}

    /**
     * Opens up a browse session with the selected hosts in the download
     * window.
     */
    void browseSelectedDownloads() {
        DataLine[] lines = TABLE.getSelectedDataLines();
        for(int i = 0; i < lines.length; i++) {
            DataLine dl = lines[i];
            Downloader downloader=(Downloader)dl.getInitializeObject();
            RemoteFileDesc end = downloader.getBrowseEnabledHost();
            if (end != null)
                SearchMediator.doBrowseHost(end);
        }
    }

    /**
     * Handles a double-click event in the table.
     */
    public void handleActionKey() {
        launchSelectedDownloads();
    }

    /**
     * Clears the downloads in the download window that have completed.
     */
    void clearCompletedDownloads() {
        ((DownloadModel)DATA_MODEL).clearCompleted();
        clearSelection();
        clearAction.setEnabled(false);
    }

	/**
	 * Returns the selected {@link FileDetails}.
	 */
	public FileDetails[] getFileDetails() {
		DataLine[] lines = TABLE.getSelectedDataLines();
		FileManager fmanager = RouterService.getFileManager();
		ArrayList list = new ArrayList(lines.length);
		for (int i = 0; i < lines.length; i++) {
			URN urn = ((DownloadDataLine)lines[i]).getDownloader().getSHA1Urn();
			if (urn != null) {
				FileDesc fd = fmanager.getFileDescForUrn(urn);
				if (fd != null) {
					list.add(fd);
				}
				else if (LOG.isDebugEnabled()) {
					LOG.debug("not filedesc for urn " + urn);
				}
			}
			else if (LOG.isDebugEnabled()) {
				LOG.debug("no urn");
			}
		}
		return (FileDetails[])list.toArray(new FileDetails[0]);
	}

    // inherit doc comment
    protected JPopupMenu createPopupMenu() {
		
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem(removeAction));
		menu.add(new JMenuItem(resumeAction));
		menu.add(new JMenuItem(pauseAction));
		menu.add(new JMenuItem(launchAction));
		if(CommonUtils.isWindows()||CommonUtils.isMacOSX())
			menu.add(new JMenuItem(exploreAction)); 
		menu.addSeparator();
		menu.add(new JMenuItem(clearAction));
		menu.addSeparator();
        menu.add(createSearchMenu());
		menu.add(new JMenuItem(chatAction));
		menu.add(new JMenuItem(browseAction));
		menu.add(new JMenuItem(editLocationAction));
//		menu.addSeparator();
//		menu.add(createAdvancedSubMenu());
				
		return menu;
    }
	
    private JMenu createSearchMenu() {
        JMenu menu = new JMenu(GUIMediator.getStringResource("DOWNLOAD_SEARCH_MENU"));
        DataLine[] lines = TABLE.getSelectedDataLines();
        if ( lines.length == 0 )  { // is there any file selected ?
        	menu.setEnabled(false);
            return menu;
        }
        //-- make perform orginal query --
        // get orginal query
        Downloader downloader = ((DownloadDataLine) lines[0]).getDownloader();
        Map searchInfoMap = (Map) downloader.getAttribute(
                                        SearchMediator.SEARCH_INFORMATION_KEY );
        if ( searchInfoMap != null ) {
            SearchInformation searchInfo = SearchInformation.createFromMap( searchInfoMap );
            menu.add(new JMenuItem( new SearchAction( searchInfo ) ));
        }
        
        //-- make search for filename action --
        // get name of the file
        java.lang.String filename = ((DownloadDataLine) lines[0]).getFileName();
        // remove extension - searches searches filename not in specified format
        int dotPos = filename.lastIndexOf('.');
        if ( dotPos > 0 )
            filename = filename.substring( 0, dotPos );
        filename = StringUtils.removeIllegalChars( filename );
        // cut the file name if necessary
        if ( filename.length() > SearchSettings.MAX_QUERY_LENGTH.getValue() )
            filename = filename.substring(0, SearchSettings.MAX_QUERY_LENGTH.getValue());
        SearchInformation info = 
            SearchInformation.createKeywordSearch (filename, null, MediaType.getAnyTypeMediaType());
        if (SearchMediator.validateInfo(info) == SearchMediator.QUERY_VALID)
            menu.add(new JMenuItem( new SearchAction(info,"SEARCH_FOR_KEYWORDS_ACTION_NAME") ));
        
        return menu;
    }
    
	private JMenu createAdvancedSubMenu() {
		JMenu menu = new JMenu(GUIMediator.getStringResource
				("GENERAL_ADVANCED_SUB_MENU"));
		menu.add(new JMenuItem(bitziAction));
		menu.add(new JMenuItem(magnetAction));
		return menu;
	}
    
    /**
     * Handles the selection of the specified row in the download window,
     * enabling or disabling buttons and chat menu items depending on
     * the values in the row.
     *
     * @param row the selected row
     */
    public void handleSelection(int row) {

        DownloadDataLine dataLine = (DownloadDataLine)DATA_MODEL.get(row);

        chatAction.setEnabled(dataLine.getChatEnabled());
        browseAction.setEnabled(dataLine.getBrowseEnabled());
        
		boolean inactive = dataLine.isDownloaderInactive();
        boolean pausable = !dataLine.getDownloader().isPaused() &&
                           !dataLine.getDownloader().isCompleted();

		
		if (dataLine.getState() == Downloader.WAITING_FOR_USER) {
			resumeAction.putValue(Action.NAME,
								  GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_SOURCES"));
			resumeAction.putValue(LimeAction.SHORT_NAME,
								  GUIMediator.getStringResource("DOWNLOAD_SOURCES_BUTTON_LABEL"));
			resumeAction.putValue(Action.SHORT_DESCRIPTION,
								  GUIMediator.getStringResource("DOWNLOAD_SOURCES_BUTTON_TIP"));
		} else {
			resumeAction.putValue(Action.NAME,
								  GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_RESUME"));
			resumeAction.putValue(LimeAction.SHORT_NAME, 
								  GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_LABEL"));
			resumeAction.putValue(Action.SHORT_DESCRIPTION,
								  GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_TIP"));
		}
		
		if (dataLine.isCompleted()) {
			removeAction.putValue(Action.NAME,
					  GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_KILL_COMPLETED_LABEL"));
			removeAction.putValue(LimeAction.SHORT_NAME,
					  GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_COMPLETED_LABEL"));
			removeAction.putValue(Action.SHORT_DESCRIPTION,
					  GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_COMPLETED_TIP"));
			launchAction.putValue(Action.NAME,
					  GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_LAUNCH_COMPLETED_LABEL"));
			launchAction.putValue(LimeAction.SHORT_NAME,
					  GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_COMPLETED_LABEL"));
			launchAction.putValue(Action.SHORT_DESCRIPTION,
					  GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_COMPLETED_TIP"));
			exploreAction.setEnabled(TABLE.getSelectedRowCount() == 1); 
		} else {
			removeAction.putValue(Action.NAME, GUIMediator.getStringResource
					("DOWNLOAD_POPUP_MENU_KILL_DOWNLOAD"));
			removeAction.putValue(LimeAction.SHORT_NAME,
					 GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_LABEL"));
			removeAction.putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_TIP"));
			launchAction.putValue(Action.NAME,
					  GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_LAUNCH"));
			launchAction.putValue(LimeAction.SHORT_NAME,
					  GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_LABEL"));
			launchAction.putValue(Action.SHORT_DESCRIPTION,
					  GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_TIP"));
			exploreAction.setEnabled(false); 
		}
		
		removeAction.setEnabled(true);
        resumeAction.setEnabled(inactive);
		pauseAction.setEnabled(pausable);
        priorityDownAction.setEnabled(inactive && pausable);
        priorityUpAction.setEnabled(inactive && pausable);
		
		Downloader dl = (Downloader)dataLine.getInitializeObject();
		editLocationAction.setEnabled(TABLE.getSelectedRowCount() == 1 
									  && dl.isRelocatable());
		
		magnetAction.setEnabled(dl.getSHA1Urn() != null);
		bitziAction.setEnabled(dl.getSHA1Urn() != null);
		launchAction.setEnabled(dl.getAmountRead() > 0);
    }

    /**
     * Handles the deselection of all rows in the download table,
     * disabling all necessary buttons and menu items.
     */
    public void handleNoSelection() {
        removeAction.setEnabled(false);
		resumeAction.setEnabled(false);
		launchAction.setEnabled(false);
		pauseAction.setEnabled(false);
		chatAction.setEnabled(false);
		browseAction.setEnabled(false);
		priorityDownAction.setEnabled(false);
		priorityUpAction.setEnabled(false);
		editLocationAction.setEnabled(false);
		magnetAction.setEnabled(false);
		bitziAction.setEnabled(false);
		exploreAction.setEnabled(false); 
    }

	private class RemoveAction extends AbstractAction {
		
		public RemoveAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("DOWNLOAD_POPUP_MENU_KILL_DOWNLOAD"));
			putValue(LimeAction.SHORT_NAME,
					 GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_KILL_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_KILL");
		}
		
		public void actionPerformed(ActionEvent e) {
			removeSelection();
		}
	}
	
	private class ChatAction extends AbstractAction {
		
		public ChatAction() {
    	    putValue(Action.NAME,
					GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_CHAT"));
		}
		
		public void actionPerformed(ActionEvent e) {
            chatWithSelectedDownloads();
        }
	}
	
	private class ClearAction extends AbstractAction {
		
		public ClearAction() {
			putValue(Action.NAME,
					 GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_CLEAR"));
			putValue(LimeAction.SHORT_NAME,
					 GUIMediator.getStringResource("DOWNLOAD_CLEAR_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_CLEAR_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_CLEAR");
		}
		
	    public void actionPerformed(ActionEvent e) {
            clearCompletedDownloads();
        }
	}

	private class BrowseAction extends AbstractAction {

		public BrowseAction() {
    	    putValue(Action.NAME,
					GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_BROWSE"));
		}
		
		public void actionPerformed(ActionEvent e) {
			browseSelectedDownloads();
		}
	}

	private class LaunchAction extends AbstractAction {
		
		public LaunchAction() {
			putValue(Action.NAME,
					 GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_LAUNCH"));
			putValue(LimeAction.SHORT_NAME,
					 GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_LAUNCH_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_LAUNCH");
		}

		public void actionPerformed(ActionEvent e) {
			launchSelectedDownloads();
		}
	}

	
	private class ResumeAction extends AbstractAction {

		public ResumeAction() {
    	    putValue(Action.NAME,
					 GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_RESUME"));
			putValue(LimeAction.SHORT_NAME, 
					 GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_RESUME_BUTTON_TIP"));
 			putValue(LimeAction.ICON_NAME, "DOWNLOAD_FILE_MORE_SOURCES");
		}
		
		public void actionPerformed(ActionEvent e) {
			resumeSelectedDownloads();
		}
	}

	private class PauseAction extends AbstractAction {

		public PauseAction() {
			putValue(Action.NAME,
					 GUIMediator.getStringResource("DOWNLOAD_POPUP_MENU_PAUSE"));
			putValue(LimeAction.SHORT_NAME,
					 GUIMediator.getStringResource("DOWNLOAD_PAUSE_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_PAUSE_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_PAUSE");
		}
		
		public void actionPerformed(ActionEvent e) {
			pauseSelectedDownloads();
		}
	}

	private class ExploreAction extends AbstractAction { 
		public ExploreAction() { 
	        putValue(Action.NAME, 
	                 GUIMediator.getStringResource("LIBRARY_EXPLORE_BUTTON_LABEL")); 
	        putValue(LimeAction.SHORT_NAME, 
	                 GUIMediator.getStringResource("LIBRARY_EXPLORE_BUTTON_LABEL")); 
	        putValue(Action.SHORT_DESCRIPTION, 
	                 GUIMediator.getStringResource("DOWNLOAD_EXPLORE_BUTTON_TIP")); 
	        putValue(LimeAction.ICON_NAME, "LIBRARY_EXPLORE"); 
	    } 
	     
	    public void actionPerformed(ActionEvent e) { 
	        launchExplorer(); 
	    } 
	} 
	private class PriorityUpAction extends AbstractAction {

		public PriorityUpAction() {
			putValue(LimeAction.SHORT_NAME, "");
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_PRIORITY_UP_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_PRIORITY_UP");
		}
		
		public void actionPerformed(ActionEvent e) {
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
				bumpPriority(true, 10); //bump by 10 places
			else if ((e.getModifiers() & ActionEvent.ALT_MASK) != 0)
				bumpPriority(true, 0);  //bump to top priority
			else
				bumpPriority(true, 1);
		}
	}

	private class PriorityDownAction extends AbstractAction {

		public PriorityDownAction() {
			putValue(LimeAction.SHORT_NAME, "");
			putValue(Action.SHORT_DESCRIPTION,
					 GUIMediator.getStringResource("DOWNLOAD_PRIORITY_DOWN_BUTTON_TIP"));
			putValue(LimeAction.ICON_NAME, "DOWNLOAD_PRIORITY_DOWN");
		}
		
		public void actionPerformed(ActionEvent e) {
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
				bumpPriority(false, 10);    //bump by 10 places
			else if ((e.getModifiers() & ActionEvent.ALT_MASK) != 0)
				bumpPriority(false, 0); //bump to top priority
			else
				bumpPriority(false, 1);
		}
	}
	
	private class EditLocationAction extends AbstractAction {

		public EditLocationAction() {
			putValue(Action.NAME, 
					 GUIMediator.getStringResource
					 ("DOWNLOAD_POPUP_MENU_EDIT_LOCATION_LABEL"));
			putValue(Action.SHORT_DESCRIPTION, 
					 GUIMediator.getStringResource
					 ("DOWNLOAD_POPUP_MENU_EDIT_LOCATION_TIP"));
		}

		public void actionPerformed(ActionEvent e) {
			editSelectedDownload();
		}
	}

}
