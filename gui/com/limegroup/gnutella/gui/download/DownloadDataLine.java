package com.limegroup.gnutella.gui.download;

import java.io.File;
import java.util.Iterator;

import javax.swing.Icon;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.Endpoint;
import com.limegroup.gnutella.InsufficientDataException;
import com.limegroup.gnutella.downloader.VerifyingFile;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.CenteredHolder;
import com.limegroup.gnutella.gui.tables.ChatHolder;
import com.limegroup.gnutella.gui.tables.FileTransfer;
import com.limegroup.gnutella.gui.tables.IconAndNameHolder;
import com.limegroup.gnutella.gui.tables.IconAndNameHolderImpl;
import com.limegroup.gnutella.gui.tables.LazyFileTransfer;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.ProgressBarHolder;
import com.limegroup.gnutella.gui.tables.SizeHolder;
import com.limegroup.gnutella.gui.tables.SpeedRenderer;
import com.limegroup.gnutella.gui.tables.TimeRemainingHolder;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class handles all of the data for a single download, representing
 * one "line" in the download window.  It continually updates the
 * displayed data for the download from the contained <tt>Downloader</tt>
 * instance.
 */
public final class DownloadDataLine extends AbstractDataLine
                                    implements LazyFileTransfer {

	/**
	 * Constant for the <tt>Downloader</tt> instance for this download.
	 */
	private Downloader DOWNLOADER;

	/**
	 * Constant for the "queued" download state.
	 */
	private static final String QUEUED_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_QUEUED");

	/**
	 * Constant for the "connecting" download state.
	 */
	private static final String CONNECTING_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_CONNECTING");

	/**
	 * Constant for the "waiting" download state.
	 */
	private static final String WAITING_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_WAITING");

	/**
	 * Constant for the "complete" download state.
	 */
	private static final String COMPLETE_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_COMPLETE");

	/**
	 * Constant for the "aborted" download state.
	 */
	private static final String ABORTED_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_ABORTED");

	/**
	 * Constant for the "failed" download state.
	 */
	private static final String FAILED_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_FAILED");

	/**
	 * Constant for the "downloading" download state.
	 */
	private static final String DOWNLOADING_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_DOWNLOADING");

  	/**
	 * Constant for the "Could Not Move to Library" download state.
	 * TODO: change this to a more generic disk problem message
	 */
	private static final String LIBRARY_MOVE_FAILED_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_LIBRARY_MOVE_FAILED");

  	/**
	 * Constant for the "Corrupt File" download state.
	 */
	private static final String CORRUPT_FILE_STATE =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_CORRUPT_FILE");

  	/**
	 * Constant for the "Waiting for Results" download state.
	 */
	private static final String REQUERY_WAITING_STATE_START =
	    GUIMediator.getStringResource("DOWNLOAD_STATUS_WAITING_FOR_REQUERY_START");

  	/**
	 * Constant for the "Waiting for Results" download state.
	 */
	private static final String REQUERY_WAITING_STATE_END =
		GUIMediator.getStringResource("DOWNLOAD_STATUS_WAITING_FOR_REQUERY_END");
    
  	/**
	 * Constant for the "Waiting for Results" download state.
	 */
	private static final String REQUERY_WAITING_FOR_USER = 
		GUIMediator.getStringResource("DOWNLOAD_STATUS_WAITING_FOR_USER");
    
  	/**
	 * Constant for the "Waiting for Connections" download state.
	 */
	private static final String WAITING_FOR_CONNECTIONS_STATE = 
		GUIMediator.getStringResource("DOWNLOAD_STATUS_WAITING_FOR_CONN");
    
    /**
     * Constant for the "Remote Queued" download state
     */
    private static final String REMOTE_QUEUED_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_REMOTE_QUEUED");

    /**
     * Constant for the "Hashing" download state
     */
    private static final String HASHING_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_HASHING");

    /**
     * Constant for the "Saving" download state
     */
    private static final String SAVING_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_SAVING");
        
    /**
     * Constant for the "Identifying Corruption" download state
     */
    private static final String IDENTIFY_CORRUPTION_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_IDENTIFY_CORRUPTION");
        
    /**
     * Constant for the "Recovery Failed" download state
     */
    private static final String RECOVERY_FAILED_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_RECOVERY_FAILED");
        
    /**
     * Constant for the "Pausing" download state.
     */
    private static final String PAUSING_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_PAUSING");
    
    /**
     * Constant for the "Paused" download state.
     */
    private static final String PAUSED_STATE =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_PAUSED");

    /**
     * Constant for "average bandwidth" tip
     */
    private static final String AVERAGE_BANDWIDTH =
        GUIMediator.getStringResource("GENERAL_AVERAGE_BANDWIDTH");

    /**
     * Constant for the "alternate locations" tip
     */
    private static final String ALTERNATE_LOCATIONS =
        GUIMediator.getStringResource("GENERAL_ALTERNATE_LOCATIONS");

    /**
     * Constant for the "invalid alternate locations" tip
     */
    private static final String INVALID_ALTERNATE_LOCATIONS =
        GUIMediator.getStringResource("GENERAL_INVALID_ALTERNATE_LOCATIONS");

    /**
     * Constant for "Started on" tip
     */
    private static final String STARTED_ON =
        GUIMediator.getStringResource("GENERAL_STARTED_ON");

    /**
     * Constant for "Finished on" tip
     */
    private static final String FINISHED_ON =
        GUIMediator.getStringResource("GENERAL_FINISHED_ON");

    /**
     * Constant for "Time Spent" tip
     */
    private static final String TIME_SPENT =
        GUIMediator.getStringResource("GENERAL_TIME_SPENT");
	
	/**
     * Constant for "Chunks" tip
     */
	private static final String CHUNKS =
		GUIMediator.getStringResource("DOWNLOAD_CHUNKS");
	
	/**
     * Constant for "Lost By Corruption" tip
     */
	private static final String LOST =
		GUIMediator.getStringResource("DOWNLOAD_LOST");
	
	/**
     * Constant for "KB" tip word
     */
	private static final String KB =
		GUIMediator.getStringResource("GENERAL_UNIT_KILOBYTES");

    /**
     * Constant for the " hosts" message.
     */
    private static final String HOSTS_LABEL =
        GUIMediator.getStringResource("DOWNLOAD_HOSTS_LABEL");

    private static final String HOST_LABEL = 
        GUIMediator.getStringResource("DOWNLOAD_HOST_LABEL");
        
    /**
     * Constant for the 'gave up' tooltip message.
     */
    private static final String[] GAVE_UP_MESSAGE =
        { GUIMediator.getStringResource("DOWNLOAD_AWAITING_SOURCES") };
        
    /**
     * Constant for the number of possible hosts this downloader has
     */
    private static final String POSSIBLE_HOSTS = 
        GUIMediator.getStringResource("DOWNLOAD_POSSIBLE_HOSTS");

    private static final String BUSY_HOSTS = 
        GUIMediator.getStringResource("DOWNLOAD_BUSY_HOSTS");

    private static final String QUEUED_HOSTS = 
        GUIMediator.getStringResource("DOWNLOAD_QUEUED_HOSTS");

    private static final String DOWNLOAD_SEARCHING =
        GUIMediator.getStringResource("DOWNLOAD_SEARCHING");

	/**
	 * Variable for the name of the file being downloaded.
	 */
	private String _fileName;

	/**
	 * Variable for the status of the download.
	 */
	private String _status;

    /**
	 * Variable for the amount of the file that has been read.
	 */
	private int _amountRead = 0;

	/**
	 * Variable for the progress made in the progressbar.
	 */
	private int _progress;

	/**
	 * Variable for whether or not chat is enabled.
	 */
	private boolean _chatEnabled;

	/**
	 * Variable for whether or not browse is enabled.
	 */
	private boolean _browseEnabled;

	/**
	 * Variable for the size of the download.
	 */
	private int _size = -1;

	/**
	 * Variable for the speed of the download.
	 */
	private double _speed;

	/**
	 * Variable for how much time is left.
	 */
	private int _timeLeft;

	/**
	 * Variable for the time this download started
	 */
	private long _startTime;

	/**
	 * Variable for the time this download ended.
	 */
	private long _endTime;
    
    /**
     * The current vendor we are downloading from.
     */
    private String _vendor;
	
	/**
	 * Stores the current state of this download, as of the last update.
	 * This is the state the everything should work off of to avoid the
	 * <tt>Downloader</tt> instance being in a different state than
	 * this data line.
	 */
	private int _state;
	
	/**
	 * Whether or not we've cleaned up this line.
	 */
	private boolean _cleaned = false;
	
	/**
	 * Column index for priority.
	 */
	static final int PRIORITY_INDEX = 0;
	private static final LimeTableColumn PRIORITY_COLUMN =
	    new LimeTableColumn(PRIORITY_INDEX, "DOWNLOAD_PRIORITY_COLUMN",
	                40, false, CenteredHolder.class);

	/**
	 * Column index for the file name.
	 */
	static final int FILE_INDEX = 1;
	private static final LimeTableColumn FILE_COLUMN =
	    new LimeTableColumn(FILE_INDEX, "DOWNLOAD_NAME_COLUMN",
	                201, true, IconAndNameHolder.class);

	/**
	 * Column index for the file size.
	 */
	static final int SIZE_INDEX = 2;
	private static final LimeTableColumn SIZE_COLUMN =
	    new LimeTableColumn(SIZE_INDEX, "DOWNLOAD_SIZE_COLUMN",
	                65, true, SizeHolder.class);

	/**
	 * Column index for the file download status.
	 */
	static final int STATUS_INDEX = 3;
	private static final LimeTableColumn STATUS_COLUMN =
	    new LimeTableColumn(STATUS_INDEX, "DOWNLOAD_STATUS_COLUMN",
	                152, true, String.class);

	/**
	 * Column index for whether or not the uploader is chat-enabled.
	 */
	static final int CHAT_INDEX = 4;
	private static final LimeTableColumn CHAT_COLUMN =
	    new LimeTableColumn(CHAT_INDEX, "DOWNLOAD_CHAT_COLUMN",
	                10, false, ChatHolder.class);

	/**
	 * Column index for the progress of the download.
	 */
	static final int PROGRESS_INDEX = 5;
	private static final LimeTableColumn PROGRESS_COLUMN =
	    new LimeTableColumn(PROGRESS_INDEX, "DOWNLOAD_PROGRESS_COLUMN",
	                71, true, ProgressBarHolder.class);

	/**
	 * Column index for the download speed.
	 */
	static final int SPEED_INDEX = 6;
	private static final LimeTableColumn SPEED_COLUMN =
	    new LimeTableColumn(SPEED_INDEX, "DOWNLOAD_SPEED_COLUMN",
	                58, true, SpeedRenderer.class);

	/**
	 * Column index for the download time remaining.
	 */
	static final int TIME_INDEX = 7;
	private static final LimeTableColumn TIME_COLUMN =
	    new LimeTableColumn(TIME_INDEX, "DOWNLOAD_TIME_REMAINING_COLUMN",
	                49, true, TimeRemainingHolder.class);
	    
    /**
     * Column index for the vendor of the downloader.
     */
    static final int VENDOR_INDEX = 8;
    private static final LimeTableColumn VENDOR_COLUMN =
        new LimeTableColumn(VENDOR_INDEX, "DOWNLOAD_SERVER_COLUMN",
                    20, false, String.class);
	
	/**
	 * Number of columns to display
	 */
	static final int NUMBER_OF_COLUMNS = 9;
	
	// Implements DataLine interface
	public int getColumnCount() { return NUMBER_OF_COLUMNS; }

	/**
	 * Must initialize data.
	 *
	 * @param downloader the <tt>Downloader</tt>
	 *  that provides access to
	 *  information about the download
	 */
	public void initialize(Object downloader) {
	    super.initialize(downloader);
		DOWNLOADER = (Downloader)downloader;
		_startTime = System.currentTimeMillis();
		_endTime = -1;
		_size = DOWNLOADER.getContentLength();
		// don't cache filename anymore, since we allow renames henceforth
		_fileName  = DOWNLOADER.getSaveFile().getName();
		if (_fileName==null) //TODO: does this ever happen with an downloader?
			_fileName="";
		_status = "";
		_chatEnabled = false;
		_browseEnabled = false;
		update();
	}

	/**
	 * Tell the downloader to close its sockets.
	 */
	public void cleanup() {
	    GUIMediator.instance().schedule(new Runnable() {
	        public void run() {
	            DOWNLOADER.stop();
            }
        });
	    _cleaned = true;
    }
    
    /**
     * Determines if this was cleaned up.
     */
    public boolean isCleaned() {
        return _cleaned;
    }
    
    /**
     * Gets the file if the download was completed.
     */
    public File getFile() {
        if(!CommonUtils.isWindows())
            return DOWNLOADER.getFile();
        else {
            if(DOWNLOADER.isCompleted())
                return DOWNLOADER.getFile();
            else
                return null;
        }
    }
    
    /**
     * Lazily gets the file -- constructs it only if necessary.
     */
    public FileTransfer getLazyFile() {
        return new FileTransfer() {
            public File getFile() {
                return DOWNLOADER.getDownloadFragment();
            }
        };
    }
    
	/**
	 * Returns the <tt>Object</tt> stored at the specified column in this
	 * line of data.
	 *
	 * @param index the index of the column to retrieve data from
	 * @return the <tt>Object</tt> stored at that index
	 * @implements DataLine interface
	 */
	public Object getValueAt(int index) {
		switch(index) {
		case PRIORITY_INDEX:
		    if(DOWNLOADER.isPaused())
		        return PriorityHolder.PAUSED_P;
		    if(DOWNLOADER.isInactive())
		        return new PriorityHolder(DOWNLOADER.getInactivePriority());
            else if(DOWNLOADER.isCompleted())
                return PriorityHolder.COMPLETE_P;
            else
                return PriorityHolder.ACTIVE_P;
		case FILE_INDEX:
		    Icon icon = IconManager.instance().getIconForFile(DOWNLOADER.getFile());
		    return new IconAndNameHolderImpl(icon, _fileName);
		case SIZE_INDEX:
			return new SizeHolder(_size);
		case STATUS_INDEX:
			return _status;
		case CHAT_INDEX:
			return _chatEnabled ? Boolean.TRUE : Boolean.FALSE;
		case PROGRESS_INDEX:
			return new Integer(_progress);
		case SPEED_INDEX:
			return new Double(_speed);
        case TIME_INDEX:
            return new TimeRemainingHolder(_timeLeft);
        case VENDOR_INDEX:
            return _vendor;
        }
		return null;
	}

	/**
	 * @implements DataLine interface
	 */
	public LimeTableColumn getColumn(int idx) {
	    switch(idx) {
	        case PRIORITY_INDEX: return PRIORITY_COLUMN;
    	    case FILE_INDEX:     return FILE_COLUMN;
    	    case SIZE_INDEX:     return SIZE_COLUMN;
    	    case STATUS_INDEX:   return STATUS_COLUMN;
    	    case CHAT_INDEX:     return CHAT_COLUMN;
    	    case PROGRESS_INDEX: return PROGRESS_COLUMN;
    	    case SPEED_INDEX:    return SPEED_COLUMN;
    	    case TIME_INDEX:     return TIME_COLUMN;
    	    case VENDOR_INDEX:   return VENDOR_COLUMN;
	    }
	    return null;
	}
	
	public boolean isClippable(int idx) {
	    switch(idx) {
	    case CHAT_INDEX:
	    case PROGRESS_INDEX:
	        return false;
	    default:
	        return true;
        }
	}
	
	public int getTypeAheadColumn() {
	    return FILE_INDEX;
    }
	
	public String[] getToolTipArray(int col) {
	    // give a new message if we gave up
	    if( _state == Downloader.GAVE_UP )
	        return GAVE_UP_MESSAGE;
	    	    
	    String[] info = new String[11];
	    String bandwidth = AVERAGE_BANDWIDTH + ": " + GUIUtils.rate2speed(
	        DOWNLOADER.getAverageBandwidth()
	    );
	    String numHosts = POSSIBLE_HOSTS + ": " + 
	                     DOWNLOADER.getPossibleHostCount();
        String busyHosts = BUSY_HOSTS + ": " +DOWNLOADER.getBusyHostCount();
        String queuedHosts=QUEUED_HOSTS + ": "+DOWNLOADER.getQueuedHostCount();
	    String numLocs = ALTERNATE_LOCATIONS + ": " +
	                     DOWNLOADER.getNumberOfAlternateLocations();
        String numInvalidLocs = INVALID_ALTERNATE_LOCATIONS + ": " +
                         DOWNLOADER.getNumberOfInvalidAlternateLocations();
		int chunkSize = DOWNLOADER.getChunkSize();
		String numChunks,lost;
        int totalPending = VerifyingFile.getNumPendingItems();
		synchronized(DOWNLOADER) {
			numChunks = CHUNKS + ": "+DOWNLOADER.getAmountVerified() / chunkSize +"/"+
				DOWNLOADER.getAmountRead() / chunkSize+ "["+ 
                DOWNLOADER.getAmountPending()+"|"+totalPending+"]"+ 
                "/"+
				DOWNLOADER.getContentLength() / chunkSize+
				", "+chunkSize/1024+KB;
		
		
		 	lost = LOST+": "+DOWNLOADER.getAmountLost()/1024+KB;
		}

        info[0] = STARTED_ON + " " + GUIUtils.msec2DateTime( _startTime );
	    if( _endTime != -1 ) {
	        info[1] = FINISHED_ON + " " + GUIUtils.msec2DateTime( _endTime );
	        info[2] = TIME_SPENT + ": " + GUIUtils.seconds2time(
	            (int)((_endTime - _startTime) / 1000 ) );
	        info[3] = "";
	        info[4] = bandwidth;
	        info[5] = numHosts;
            info[6] = busyHosts;
            info[7] = queuedHosts;
	        info[8] = numLocs;
            info[9] = numInvalidLocs;
			info[10] = lost;
	    } else {
	        info[1] = TIME_SPENT + ": " + GUIUtils.seconds2time(
	            (int) ((System.currentTimeMillis() - _startTime) / 1000 ) );
	        info[2] = "";
	        info[3] = bandwidth;
	        info[4] = numHosts;
            info[5] = busyHosts;
            info[6] = queuedHosts;
	        info[7] = numLocs;
            info[8] = numInvalidLocs;
			info[9] = numChunks;
			info[10] = lost;}

	    return info;
	}

	public boolean isDynamic(int idx) {
	    switch(idx) {
	        case PRIORITY_INDEX:
	        case STATUS_INDEX:
	        case PROGRESS_INDEX:
	        case SPEED_INDEX:
	        case TIME_INDEX:
	        case VENDOR_INDEX:
	            return true;
	    }
	    return false;
	}

	/**
	 * Returns the total size in bytes of the file being downloaded.
	 *
	 * @return the total size in bytes of the file being downloaded
	 */
	int getLength() {
		return _size;
	}

    /**
     * Returns name of the file being downloaded.
     * @return name of the downloaded file.
     */
    public String getFileName() {
        return _fileName;
    }
    
	/**
	 * Returns whether or not the <tt>Downloader</tt> for this download
	 * is equal to the one passed in.
	 *
	 * @return <tt>true</tt> if the passed-in downloader is equal to the
	 *  <tt>Downloader</tt> for this download, <tt>false</tt> otherwise
	 */
	boolean containsDownloader(Downloader downloader) {
		return DOWNLOADER.equals(downloader);
	}

	/**
	 * Returns the <tt>Downloader</tt> associated with this download.
	 *
	 * @return the <tt>Downloader</tt> associated with this download
	 */
	Downloader getDownloader() {
		return DOWNLOADER;
	}

	/**
	 * Return the state of the Downloader
	 *
	 * @return the state of the downloader
	 */
	int getState() {
	    return _state;
	}

	/**
	 * Returns whether or not the download has completed.
	 *
	 * @return <tt>true</tt> if the download is complete, <tt>false</tt> otherwise
	 */
	boolean isCompleted() {
		return _state == Downloader.COMPLETE;
	}

	/**
	 * Returns whether or not chat is enabled for this download.
	 *
	 * @return <tt>true</tt> if the host we're downloading from is chattable,
	 *  <tt>false</tt> otherwise
	 */
	boolean getChatEnabled() {
		return _chatEnabled;
	}

	/**
	 * Returns whether or not browse is enabled for this download.
	 *
	 * @return <tt>true</tt> if the host we're downloading from is browsable,
	 *  <tt>false</tt> otherwise
	 */
	boolean getBrowseEnabled() {
		return _browseEnabled;
	}

	/**
	 * Updates all of the data for this download, obtaining fresh information
	 * from the contained <tt>Downloader</tt> instance.
	 *
	 * @implements DataLine interface
	 */
	public void update() {
		synchronized(DOWNLOADER) {
		// always get new file name it might have changed
		_fileName = DOWNLOADER.getSaveFile().getName();
	    _speed = -1;
	    if ( _size == -1 )
		    _size = DOWNLOADER.getContentLength();
		_amountRead = DOWNLOADER.getAmountRead();
		_chatEnabled = DOWNLOADER.hasChatEnabledHost();
        _browseEnabled = DOWNLOADER.hasBrowseEnabledHost();
        _timeLeft = 0;
        //note: we *always* want to update progress
        // specifically for when the user has downloaded stuff,
        // closed the app, and then re-opened the app.
        //previously, because progress was only set while downloading
        //or corrupted, the GUI would display 0 progress, even
        //though it actually had progress.
		double d = (double)_amountRead/(double)_size;
		_progress = (int)(d*100);
		this.updateStatus();
		// downloads can go from inactive to active through resuming.
		if ( !this.isInactive() ) _endTime = -1;
	}
	}


	/**
	 * Updates the status of the download based on the state stored in the
	 * <tt>Downloader</tt> instance for this <tt>DownloadDataLine</tt>.
	 */
	private void updateStatus() {
	    final String lastVendor = _vendor;
	    _vendor = "";
		_state = DOWNLOADER.getState();
		boolean paused = DOWNLOADER.isPaused();
		if(paused && _state != Downloader.PAUSED && !DOWNLOADER.isCompleted()) {
		    _status = PAUSING_STATE;
		    return;
		}
		
		switch (_state) {
		case Downloader.QUEUED:
			_status = QUEUED_STATE;
			break;
		case Downloader.CONNECTING:
			_status = CONNECTING_STATE;
			break;
		case Downloader.BUSY:
			_status = WAITING_STATE;
			break;
	    case Downloader.HASHING:
	        _status = HASHING_STATE;
	        break;
	    case Downloader.SAVING:
	        _status = SAVING_STATE;
	        break;
		case Downloader.COMPLETE:
            _status = COMPLETE_STATE;
			_progress = 100;
			break;
		case Downloader.ABORTED:
			_status = ABORTED_STATE;
			break;
		case Downloader.GAVE_UP:
			_status = FAILED_STATE;
			break;
        case Downloader.IDENTIFY_CORRUPTION:
			_status = IDENTIFY_CORRUPTION_STATE;
 			break;
        case Downloader.RECOVERY_FAILED:
            _status = "Recovery Failed";
            break;
		case Downloader.DOWNLOADING:
		    _vendor = lastVendor;
		    updateHostCount(DOWNLOADER);
            try {
                _speed = (double)DOWNLOADER.getMeasuredBandwidth();
            } catch(InsufficientDataException ide) {
                _speed = 0;
            }
            // If we have a valid rate (can't compute if rate is 0),
            // then determine how much time (in seconds) is remaining.
            if ( _speed > 0) {
                double kbLeft = (((double)_size/1024.0) -
								 ((double)_amountRead/1024.0));
                _timeLeft = (int)(kbLeft / _speed);
            }
			break;
		case Downloader.DISK_PROBLEM:
			_status = LIBRARY_MOVE_FAILED_STATE;
			_progress = 100;
			break;
        case Downloader.CORRUPT_FILE:
            _status = CORRUPT_FILE_STATE;
            break;
        case Downloader.WAITING_FOR_RESULTS:
			int stateTime=DOWNLOADER.getRemainingStateTime();
			_status = (REQUERY_WAITING_STATE_START+" "+stateTime
					   +REQUERY_WAITING_STATE_END);
            break;
        case Downloader.ITERATIVE_GUESSING:
            _status = DOWNLOAD_SEARCHING;
            break;
        case Downloader.WAITING_FOR_USER:
            _status = REQUERY_WAITING_FOR_USER;
            break;
        case Downloader.WAITING_FOR_CONNECTIONS:
            _status = WAITING_FOR_CONNECTIONS_STATE;
            break;
        case Downloader.REMOTE_QUEUED:
            _status = REMOTE_QUEUED_STATE+" "+DOWNLOADER.getQueuePosition();
            _vendor = DOWNLOADER.getVendor();            
            break;
        case Downloader.PAUSED:
            _status = PAUSED_STATE;
            break;
		default:
			Assert.that(false,
			    "Unknown status "+DOWNLOADER.getState()+" of downloader");
		}
	}

    /**
     * Returns a human-readable description of the address(es) from
     * which d is downloading.
     */
    private void updateHostCount(Downloader d) {
        int count = d.getNumHosts();

        // we are in between chunks with this host,
        // use the previous count so-as not to confuse
        // the user.
        if (count == 0) {
            // don't change anything.
            return;
        }
        
        if (count==1) {
            _status = DOWNLOADING_STATE + " " + count + " "+ HOST_LABEL; 
            _vendor = d.getVendor();
        } else {
            _status = DOWNLOADING_STATE + " " +  count + " " + HOSTS_LABEL;
            _vendor = d.getVendor();
        }
    }

	/**
	 * Returns whether or not this download is in what
	 * is considered an "inactive"
	 * state, such as completeed, aborted, failed, etc.
	 *
	 * @return <tt>true</tt> if this download is in an inactive state,
	 *  <tt>false</tt> otherwise
	 */
	boolean isInactive() {
		return (_state == Downloader.COMPLETE ||
				_state == Downloader.ABORTED ||
				_state == Downloader.GAVE_UP ||
				_state == Downloader.DISK_PROBLEM ||
                _state == Downloader.CORRUPT_FILE);
	}
	
	/**
	 * Determines if the downloader is in what it considers an inactive state.
	 */
	boolean isDownloaderInactive() {
	    return DOWNLOADER.isInactive();
	}

	/**
	 * Returns whether or not the
	 * download for this line is currently downloading
	 *
	 * @return <tt>true</tt> if this download is currently downloading,
	 *  <tt>false</tt> otherwise
	 */
	boolean isDownloading() {
		return _state == Downloader.DOWNLOADING;
	}

	/**
	 * Sets the time this download ended.
	 */
	void setEndTime(long time) {
	    _endTime = time;
	}
}
