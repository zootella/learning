package com.limegroup.gnutella.gui.upload;

import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPopupMenu;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.Uploader;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.gui.tables.AbstractTableMediator;
import com.limegroup.gnutella.gui.tables.DataLine;
import com.limegroup.gnutella.gui.tables.LimeJTable;
import com.limegroup.gnutella.gui.tables.TableSettings;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class acts as a mediator between all of the components of the
 * upload window.  It also constructs all of the upload window
 * components.
 */
public final class UploadMediator extends AbstractTableMediator {

	/**
	 * Variable for the total number of uploads that have been added in this
	 * session.
	 */
	private static int _totalUploads = 0;


	/**
	 * Variables so we only need one listener for both ButtonRow & PopupMenu
	 */
	ActionListener CHAT_LISTENER;
	ActionListener CLEAR_LISTENER;
	ActionListener BROWSE_LISTENER;
	
	private static final String UPLOAD_TITLE =
	    GUIMediator.getStringResource("UPLOAD_TITLE");
    private static final String ACTIVE = 
        GUIMediator.getStringResource("UPLOAD_ACTIVE");
    private static final String QUEUED =
        GUIMediator.getStringResource("UPLOAD_QUEUED");

    /**
     * instance, for singelton acces
     */
    private static UploadMediator _instance = new UploadMediator();

    public static UploadMediator instance() { return _instance; }

    /**
     * Variable for whether or not chat is enabled for the selected host.
     */
    private static boolean _chatEnabled;

    /**
     * Variable for whether or not browse host is enabled for the selected 
     * host.
     */
    private static boolean _browseEnabled;
    
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
	 * Build some extra listeners
	 */
	protected void buildListeners() {
	    super.buildListeners();
	    CHAT_LISTENER = new ChatListener(this);
	    CLEAR_LISTENER = new ClearListener(this);
	    BROWSE_LISTENER = new BrowseListener(this);
	}

	/**
	 * Set us up the constants
	 */
	protected void setupConstants() {
		MAIN_PANEL = new PaddedPanel(UPLOAD_TITLE);
		DATA_MODEL = new UploadModel();
		TABLE = new LimeJTable(DATA_MODEL);
		BUTTON_ROW = (new UploadButtons(this)).getComponent();
    }

	/**
	 * Update the splash screen
	 */
	protected void updateSplashScreen() {
		GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_UPLOAD_WINDOW"));
    }

	/**
	 * Constructs all of the elements of the upload window, including
	 * the table, the buttons, etc.
	 */
	public UploadMediator() {
	    super("UPLOAD_MEDIATOR");
	    GUIMediator.addRefreshListener(this);
	    ThemeMediator.addThemeObserver(this);
	}

	/**
	 * Override the default refresh so we can set the clear button.
	 */
	public void doRefresh() {
	    boolean inactivePresent =
	        ((Boolean)DATA_MODEL.refresh()).booleanValue();
	    setButtonEnabled(UploadButtons.CLEAR_BUTTON, inactivePresent);
	    
	    MAIN_PANEL.setTitle(UPLOAD_TITLE + " (" +
	        RouterService.getUploadManager().uploadsInProgress() + " " +
	        ACTIVE + ", " +
			RouterService.getUploadManager().getNumQueuedUploads() + " " +
	        QUEUED + ")");
	}

	/**
	 * Returns the total number of Uploads that have occurred in this session.
	 *
	 * @return the total number of Uploads that have occurred in this session
	 */
    public int getTotalUploads() {
        return _totalUploads;
    }

 	/**
	 * Returns the total number of current Uploads.
	 *
	 * @return the total number of current Uploads
	 */
    public int getCurrentUploads() {
        return ((UploadModel)DATA_MODEL).getCurrentUploads();
    }

 	/**
	 * Returns the total number of active Uploads.
     * This includes anything that is still viewable in the Uploads view.
	 *
	 * @return the total number of active Uploads
	 */
    public int getActiveUploads() {
        return ((UploadModel)DATA_MODEL).getRowCount();
    }

    /**
     * Override the default add.
     *
	 * Adds a new Uploads to the list of Uploads, obtaining the necessary
	 * information from the supplied <tt>Uploader</tt>.
	 *
	 * If the upload is not already in the list, then it is added.
     *  <p>
     * With HTTP1.1 support, swarm downloads, and chunking, it becomes
     * important that the GUI should not get updated whenever a little
     * chunk of a file is uploaded.
	 */
    public void add(Object uploader) {
        if ( !DATA_MODEL.contains(uploader) ) {
            //attempt to update an existing uploader
            int idx = DATA_MODEL.update(uploader);
            if ( idx == -1 ) {
                //if we couldn't find one to update, add it as new
                _totalUploads++;
                super.add(uploader);
    	    }
        }
    }

	/**
	 * Override the default remove
	 *
	 * Removes a upload from the list if the user has configured their system
	 * to automatically clear completed upload and if the upload is
	 * complete.
	 *
	 * @param uploader the <tt>Uploader</tt> to remove from the list if it is
	 *  complete.
	 */
    public void remove(Object uploader) {
		if (SharingSettings.CLEAR_UPLOAD.getValue() &&
            ((Uploader)uploader).isInactive()) {
            // This is called when the upload is finished, either because
            // the user clicked 'Kill', something was interupted, etc..
            // It doesn't matter that we always setPersistConnect(true),
            // because if the upload was already killed, the sockets
            // are already closed.
            // The flow of a manually killed upload goes like this:
            //  RemoveListener.actionPerformed() ->
            //  UploadMediator.removeSelection() ->
            //  (for each row selected)...
            //    UploadDataLine.cleanup()
            //    (core notices socket closed, marks interupted)
            //    VisualConnectionCallBack.removeUploader(Uploader) ->
            //    UploadMediator.remove(uploader)
            //    (if the user has clear completed checked)...
            //      UploadDataLine.setPersistConnection(true)
            //      AbstractTableMediator.removeRow( uploader's row )
            // A remotely-terminated download follows the same path,
            // but starts at the 'core notices socket closed'.
            int i = DATA_MODEL.getRow(uploader);
            if( i != -1 ) {
                // tell the DataLine that we don't want to clean up.
                // necessary for chunked transfers.
                ((UploadDataLine)DATA_MODEL.get(i)).setPersistConnection(true);
                super.removeRow(i);
            }
		} else {
		    //if we're not removing it, note the time at which it ended.
		    UploadDataLine udl = (UploadDataLine)DATA_MODEL.get(uploader);
		    if (udl != null) udl.setEndTime( System.currentTimeMillis() );
	    }
    }

    /**
     * Override the default remove to not actually remove,
     * but instead just call 'cleanup'.
     * If the user has 'Clear Completed Uploads' checked,
     * they'll be removed.  Otherwise, they'll show as interupted.
     */
    public void removeSelection() {
		int[] sel = TABLE.getSelectedRows();
		Arrays.sort(sel);
		for( int counter = sel.length - 1; counter >= 0; counter--) {
			int i = sel[counter];
			DATA_MODEL.get(i).cleanup();
		}
    }

	/**
	 * Opens up a chat session with the selected hosts in the upload
	 * window.
	 */
	void chatWithSelectedUploads() {
		int[] sel = TABLE.getSelectedRows();
		for (int i =0; i<sel.length; i++) {
		    DataLine dl = DATA_MODEL.get(sel[i]);
			Uploader uploader=(Uploader)dl.getInitializeObject();
			if (uploader.isChatEnabled() ) {
			    String host = uploader.getHost();
				int port = uploader.getGnutellaPort();
				RouterService.createChat(host, port);
			}
		}
	}

	/**
	 * Browses all selected hosts (only once per host)
	 * Moves display to the search window if a browse host was triggered
	 */
	void browseWithSelectedUploads() {
	    boolean found = false;
	    int[] sel = TABLE.getSelectedRows();
	    Set searched = new HashSet( sel.length );
	    for( int i = 0; i < sel.length; i++ ) {
	        DataLine dl = DATA_MODEL.get(sel[i]);
	        Uploader uploader=(Uploader)dl.getInitializeObject();
	        if ( uploader.isBrowseHostEnabled() ) {
	            String host = uploader.getHost();
				int port = uploader.getGnutellaPort();
	            if ( !searched.contains( host ) ) {
	                SearchMediator.doBrowseHost( host, port, null );
	                searched.add( host );
	                found = true;
	            }
	        }
	    }
	    if ( found ) GUIMediator.instance().setWindow(GUIMediator.SEARCH_INDEX);
	}

	/**
	 * Don't do anything on a double click.
	 */
	public void handleActionKey() { }

	/**
	 * Clears the uploads in the upload window that have completed.
	 */
	void clearCompletedUploads() {
		((UploadModel)DATA_MODEL).clearCompleted();
		clearSelection();
        setButtonEnabled(UploadButtons.CLEAR_BUTTON, false);
	}

    // inherit doc comment
    protected JPopupMenu createPopupMenu() {
        JPopupMenu menu = (new UploadPopupMenu(this)).getComponent();
		menu.getComponent(UploadPopupMenu.KILL_INDEX).
            setEnabled(!TABLE.getSelectionModel().isSelectionEmpty());
		menu.getComponent(UploadPopupMenu.CHAT_INDEX).
            setEnabled(_chatEnabled);
		menu.getComponent(UploadPopupMenu.BROWSE_INDEX).
            setEnabled(_browseEnabled);   
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

		UploadDataLine dataLine = (UploadDataLine)DATA_MODEL.get(row);
		_chatEnabled = dataLine.isChatEnabled();
		_browseEnabled = dataLine.isBrowseEnabled();

		setButtonEnabled(UploadButtons.KILL_BUTTON, 
                         !TABLE.getSelectionModel().isSelectionEmpty());
		setButtonEnabled(UploadButtons.BROWSE_BUTTON, _browseEnabled);
	}

	/**
	 * Handles the deselection of all rows in the upload table,
	 * disabling all necessary buttons and menu items.
	 */
	public void handleNoSelection() {
		_chatEnabled = false;
		_browseEnabled = false;
		setButtonEnabled(UploadButtons.KILL_BUTTON, false);
		setButtonEnabled(UploadButtons.BROWSE_BUTTON, false);
	}
}
