package com.limegroup.gnutella.gui.upload;

import javax.swing.Icon;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.InsufficientDataException;
import com.limegroup.gnutella.Uploader;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.ChatHolder;
import com.limegroup.gnutella.gui.tables.IconAndNameHolder;
import com.limegroup.gnutella.gui.tables.IconAndNameHolderImpl;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.ProgressBarHolder;
import com.limegroup.gnutella.gui.tables.SizeHolder;
import com.limegroup.gnutella.gui.tables.SpeedRenderer;
import com.limegroup.gnutella.gui.tables.TimeRemainingHolder;
import com.limegroup.gnutella.http.HTTPRequestMethod;

/**
 * This class handles all of the data for a single upload, representing
 * one "line" in the upload window.  It continually updates the
 * displayed data for the upload from the contained <tt>Uploader</tt>
 * instance.
 */
public final class UploadDataLine extends AbstractDataLine {

	/**
	 * Constant for the <tt>Uploader</tt> instance for this upload.
	 */
	private Uploader UPLOADER;

	/**
	 * Constant for the "connecting" upload state.
	 */
	private static final String CONNECTING_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_CONNECTING");

	/**
	 * Constant for the "uploading" upload state.
	 */
	private static final String UPLOADING_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_UPLOADING");

	/**
	 * Constant for the "limit reached" upload state.
	 */
	private static final String LIMIT_REACHED_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_LIMITREACHED");

	/**
	 * Constant for the "freeloader" upload state.
	 */
	private static final String FREELOADER_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_FREELOADER");

	/**
	 * Constant for the "interrupted" upload state.
	 */
	private static final String INTERRUPTED_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_INTERRUPTED");

	/**
	 * Constant for the "complete" upload state.
	 */
	private static final String COMPLETE_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_COMPLETE");

	/**
	 * Constant for the "file not found" upload state.
	 */
	private static final String FILE_NOT_FOUND_STATE =
		GUIMediator.getStringResource("UPLOAD_TABLE_STRING_FNF");

    /**
     * Constant for the "Queued" upload state.
     */
    private static final String QUEUED_STATE =
        GUIMediator.getStringResource("UPLOAD_TABLE_STRING_QUEUED");
        
    /**
     * Constant for "Unavailable Range" upload state.
     */
    private static final String UNAVAILABLE_RANGE_STATE =
        GUIMediator.getStringResource("UPLOAD_TABLE_STRING_UNAVAILABLE_RANGE");
        
    /**
     * Constant for the "Malformed Request" upload state.
     */
    private static final String MALFORMED_REQUEST_STATE =
        GUIMediator.getStringResource("UPLOAD_TABLE_STRING_MALFORMED_REQUEST");

    /**
	 * Constant for the "Banned Greedy Servent" upload state.
	 */
	private static final String BANNED_GREEDY_STATE =
        GUIMediator.getStringResource("UPLOAD_TABLE_STRING_BANNED_GREEDY");

    /**
	 * Constant for the "Uploading Hash Tree" upload state.
	 */
	private static final String HASH_TREE_STATE =
        GUIMediator.getStringResource("UPLOAD_TABLE_STRING_HASH_TREE");

    /**
     * Constant for "average bandwidth" tip
     */
    private static final String AVERAGE_BANDWIDTH =
        GUIMediator.getStringResource("GENERAL_AVERAGE_BANDWIDTH");

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
	 * Variable for the name of the file being uploaded.
	 */
	private String _fileName;

	/**
	 * Variable for the status of the upload.
	 */
	private String _status;

	/**
	 * Variable for the hostname
	 */
	private String _hostName;

	/**
	 * Variable for the userAgent
	 */
	private String _userAgent;

	/**
	 * Variable for the progress bar
	 */
	private int _progress;

	/**
	 * Variable for whether or not chat is enabled
	 */
	private boolean _chatEnabled;

	/**
	 * Variable for whether or not browse is enabled
	 */
	private boolean _browseEnabled;

	/**
	 * Variable for the speed
	 */
	private double _speed;

	/**
	 * Variable for the time left
	 */
	private int _timeLeft;

	/**
	 * Variable for whether or not cleanup should do anything
	 */
	private boolean _persistConnection;

	/**
	 * Variable for the time the upload started.
	 */
	private long _startTime;

	/**
	 * Variable for the time the upload ended.
	 */
	private long _endTime = -1;

	/**
	 * Stores the current state of this upload, as of the last update.
	 * This is the state the everything should work off of to avoid the
	 * <tt>Uploader</tt> instance being in a different state than
	 * this data line.
	 */
	private int _state;

	/**
	 * Column index for the file name.
	 */
	static final int FILE_INDEX = 0;
	private static final LimeTableColumn FILE_COLUMN =
	    new LimeTableColumn(FILE_INDEX, "UPLOAD_TABLE_STRING_NAME",
	                160, true, IconAndNameHolder.class);

	/**
	 * Column index for the host name.
	 */
	static final int HOST_INDEX = 1;
	private static final LimeTableColumn HOST_COLUMN =
	    new LimeTableColumn(HOST_INDEX, "UPLOAD_TABLE_STRING_HOST",
	                70, true, String.class);

	/**
	 * Column index for the file size.
	 */
	static final int SIZE_INDEX = 2;
	private static final LimeTableColumn SIZE_COLUMN =
	    new LimeTableColumn(SIZE_INDEX, "UPLOAD_TABLE_STRING_SIZE",
	                25, true, SizeHolder.class);

	/**
	 * Column index for the file upload status.
	 */
	static final int STATUS_INDEX = 3;
	private static final LimeTableColumn STATUS_COLUMN =
	    new LimeTableColumn(STATUS_INDEX, "UPLOAD_TABLE_STRING_STATUS",
	                100, true, String.class);

	/**
	 * Column index for whether or not the uploader is chat-enabled.
	 */
	static final int CHAT_INDEX = 4;
	private static final LimeTableColumn CHAT_COLUMN =
	    new LimeTableColumn(CHAT_INDEX, "UPLOAD_TABLE_STRING_CHAT",
	                10, true, ChatHolder.class);

	/**
	 * Column index for the progress of the upload.
	 */
	static final int PROGRESS_INDEX = 5;
	private static final LimeTableColumn PROGRESS_COLUMN =
	    new LimeTableColumn(PROGRESS_INDEX, "UPLOAD_TABLE_STRING_PROGRESS",
	                25, true, ProgressBarHolder.class);

	/**
	 * Column index for the upload speed.
	 */
	static final int SPEED_INDEX = 6;
	private static final LimeTableColumn SPEED_COLUMN =
	    new LimeTableColumn(SPEED_INDEX, "UPLOAD_TABLE_STRING_SPEED",
	                15, true, SpeedRenderer.class);

	/**
	 * Column index for the upload time remaining.
	 */
	static final int TIME_INDEX = 7;
	private static final LimeTableColumn TIME_COLUMN =
	    new LimeTableColumn(TIME_INDEX, "UPLOAD_TABLE_STRING_TIME_REMAINING",
	                15, true, TimeRemainingHolder.class);

	/**
	 * Column index for the user agent
	 */
	static final int USER_AGENT_INDEX = 8;
	private static final LimeTableColumn USER_AGENT_COLUMN =
	    new LimeTableColumn(USER_AGENT_INDEX, "UPLOAD_TABLE_STRING_USER_AGENT",
	                70, true, String.class);

	/** Number of columns visible
	 *
	 */
	static final int NUMBER_OF_COLUMNS = 9;

	//implements DataLine interface
	public int getColumnCount() { return NUMBER_OF_COLUMNS; }

	/**
	 * Must initialize data.
	 *
	 * @param uploader the <tt>Uploader</tt>
	 *  that provides access to
	 *  information about the upload
	 */
	public void initialize(Object uploader) {
	    super.initialize(uploader);

        if ( UPLOADER != null ) {
            UPLOADER = (Uploader)uploader;
        } else {
            UPLOADER = (Uploader)uploader;
            _startTime = System.currentTimeMillis();
	        _chatEnabled = UPLOADER.isChatEnabled();
	        _browseEnabled = UPLOADER.isBrowseHostEnabled();
            _fileName = UPLOADER.getFileName();
            _hostName = UPLOADER.getHost();
    	    _userAgent = UPLOADER.getUserAgent();
        }

		_endTime = -1;
		_status = "";
		_persistConnection = false;
		update();
	}

	// implements DataLine interface
	public void cleanup() { if ( !_persistConnection) UPLOADER.stop(); }

	/*
	 * Returns the <tt>Object</tt> stored at the specified column in this
	 * line of data.
	 *
	 * @param index the index of the column to retrieve data from
	 * @return the <tt>Object</tt> stored at that index
	 * @implements DataLine interface
	 */
	public Object getValueAt(int index) {
        switch(index) {
        case FILE_INDEX:
            FileDesc fd = UPLOADER.getFileDesc();
            Icon icon = fd == null ? null : 
                IconManager.instance().getIconForFile(fd.getFile());
            return new IconAndNameHolderImpl(icon, _fileName);
	    case HOST_INDEX:
	        return _hostName;
		case SIZE_INDEX:
			return new SizeHolder(getLength());
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
        case USER_AGENT_INDEX:
            return _userAgent;
		}
		return null;
	}

	// Implements DataLine interface
	public LimeTableColumn getColumn(int idx) {
	    switch (idx) {
	        case FILE_INDEX:            return FILE_COLUMN;
	        case HOST_INDEX:            return HOST_COLUMN;
	        case SIZE_INDEX:            return SIZE_COLUMN;
	        case STATUS_INDEX:          return STATUS_COLUMN;
	        case CHAT_INDEX:            return CHAT_COLUMN;
	        case PROGRESS_INDEX:        return PROGRESS_COLUMN;
	        case SPEED_INDEX:           return SPEED_COLUMN;
	        case TIME_INDEX:            return TIME_COLUMN;
	        case USER_AGENT_INDEX:      return USER_AGENT_COLUMN;
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
	    String[] info = new String[ _endTime != -1 ? 5 : 4];
	    String tp = AVERAGE_BANDWIDTH + ": " + GUIUtils.rate2speed(
	        UPLOADER.getAverageBandwidth()
	    );
	    info[0] = STARTED_ON + " " + GUIUtils.msec2DateTime( _startTime );
	    if( _endTime != -1 ) {
	        info[1] = FINISHED_ON + " " + GUIUtils.msec2DateTime( _endTime );
	        info[2] = TIME_SPENT + ": " + GUIUtils.seconds2time(
	            (int)((_endTime - _startTime) / 1000 ) );
            info[3] = "";
	        info[4] = tp;
	    } else {
	        info[1] = TIME_SPENT + ": " + GUIUtils.seconds2time(
	            (int) ((System.currentTimeMillis() - _startTime) / 1000 ) );
            info[2] = "";
	        info[3] = tp;
	    }

	    return info;
	}

	// Implements DataLine interface
	public boolean isDynamic(int idx) {
	    switch(idx) {
	        case STATUS_INDEX:
	        case PROGRESS_INDEX:
	        case SPEED_INDEX:
	        case TIME_INDEX:
	            return true;
	    }
	    return false;
	}

	/**
	 * Returns the total size in bytes of the file being uploaded.
	 *
	 * @return the total size in bytes of the file being uploaded
	 */
	int getLength() {
		return UPLOADER == null ? 0 : UPLOADER.getFileSize();
	}

	/**
	 * Returns whether or not the <tt>Uploader</tt> for this upload
	 * is equal to the one passed in.
	 *
	 * @return <tt>true</tt> if the passed-in uploader is equal to the
	 *  <tt>Uploader</tt> for this upload, <tt>false</tt> otherwise
	 */
	boolean containsUploader(Uploader uploader) {
		return UPLOADER.equals(uploader);
	}

	/**
	 * Returns the <tt>Uploader</tt> associated with this upload.
	 *
	 * @return the <tt>Uploader</tt> associated with this upload
	 */
	Uploader getUploader() {
		return UPLOADER;
	}

	/**
	 * Returns the ip address string of the host we are uploading from.
	 *
	 * @return the ip address string of the host we are uploading from
	 */
	String getHost() {
		return UPLOADER.getHost();
	}

	/**
	 * Returns the index of the file of the uploader
	 *
	 * @return the index of the file of the uploader
	 */
	int getFileIndex() {
	    return UPLOADER.getIndex();
	}

	/**
	 * Return the state of the Uploader
	 *
	 * @return the state of the uploader
	 */
	int getState() {
	    return _state;
	}

	/**
	 * Returns whether or not the upload has completed.
	 *
	 * @return <tt>true</tt> if the upload is complete, <tt>false</tt> otherwise
	 */
	boolean isCompleted() {
		return _state == Uploader.COMPLETE;
	}

	/**
	 * Returns whether or not chat is enabled for this upload.
	 *
	 * @return <tt>true</tt> if the host we're uploading from is chattable,
	 *  <tt>false</tt> otherwise
	 */
	boolean isChatEnabled() {
		return _chatEnabled;
	}

	/**
	 * Returns whether or not browse is enabled for this upload.
	 */
	boolean isBrowseEnabled() {
	    return _browseEnabled;
	}

	/**
	 * Updates all of the data for this upload, obtaining fresh information
	 * from the contained <tt>Uploader</tt> instance.
	 * @implements DataLine interface
	 */
	public void update() {
	    // do not change the display if we are at an intermediary
	    // complete or connecting state.
	    // (meaning that this particular chunk finished, but more will come)
	    // we use _endTime to tell us when it's finished, because that is
	    // set when remove is called, which is only called when the entire
	    // upload has finished.
	    // we use getTotalAmountUploaded to know if a byte has been read
	    // (which would mean we're not connecting anymore)
	    int state = UPLOADER.getState();
	    int lastState = UPLOADER.getLastTransferState();
	    if ( (state == Uploader.COMPLETE && _endTime == -1) ||
	         (state == Uploader.CONNECTING &&
	          UPLOADER.getTotalAmountUploaded() != 0)
	       ) {
            state = lastState;
        }
        
        // Reset the current state to be the lastState if we're complete now,
        // but our last transfer wasn't uploading, queued, or thex.
        if(state == Uploader.COMPLETE && 
          lastState != Uploader.UPLOADING &&
          lastState != Uploader.QUEUED &&
          lastState != Uploader.THEX_REQUEST) {
            state = lastState;
        }
            

		_speed = -1;
		_timeLeft = 0;
		this.updateStatus(state);
	}


	/**
	 * Updates the status of the upload based on the state stored in the
	 * <tt>Uploader</tt> instance for this <tt>UploadDataLine</tt>.
	 */
	private void updateStatus(int state) {
		_state = state;
		switch (_state) {
		case Uploader.CONNECTING:
			_status = CONNECTING_STATE;
			break;
		case Uploader.FREELOADER:
		    _status = FREELOADER_STATE;
			break;
		case Uploader.COMPLETE:
		    //must set progress for the case of when
		    //an upload completes when someone isn't watching the screen
		    //when they come back to it, it would have displayed as 0%
		    //since that's the first update to the dataline.
		    if ( _status != COMPLETE_STATE ) {
		        setProgress();
		        if ( _progress == 99 )
		            _progress = 100;
		    }
		    _status = COMPLETE_STATE;
			break;
        case Uploader.UNAVAILABLE_RANGE:
            _status = UNAVAILABLE_RANGE_STATE;
            break;
        case Uploader.MALFORMED_REQUEST:
            _status = MALFORMED_REQUEST_STATE;
            break;
		case Uploader.LIMIT_REACHED:
			_status = LIMIT_REACHED_STATE;
			break;
		case Uploader.INTERRUPTED:
		    //must set progress for the case of when
		    //an upload completes when someone isn't watching the screen
		    //when they come back to it, it would have displayed as 0%
		    //since that's the first update to the dataline.
    		if ( _status != INTERRUPTED_STATE )
    		    setProgress();
			_status = INTERRUPTED_STATE;
			break;
		case Uploader.FILE_NOT_FOUND:
		    _status = FILE_NOT_FOUND_STATE;
		    break;
        case Uploader.THEX_REQUEST:
            _status = HASH_TREE_STATE;
            setProgress();
            setSpeedAndTimeLeft();
            break;
		case Uploader.UPLOADING:
			_status = UPLOADING_STATE;
			setProgress();
			setSpeedAndTimeLeft();
			break;
	    case Uploader.BROWSE_HOST:
	        Assert.that(false, "Browse Host status in GUI Upload view");
	        break;
        case Uploader.QUEUED:
            _status = QUEUED_STATE + " (" + 
                        (UPLOADER.getQueuePosition() + 1) + ")";
            setProgress();
            break;
        case Uploader.BANNED_GREEDY:
            _status = BANNED_GREEDY_STATE;
            break;
		default:
			Assert.that(false,
						"Unknown status "+UPLOADER.getState()+" of uploader");
		}
	}
	
	/**
	 * Sets the speed & time left.
	 */
    private void setSpeedAndTimeLeft() {
            try {
                _speed = (double)UPLOADER.getMeasuredBandwidth();
            } catch(InsufficientDataException ide) {
                _speed = 0;
            }
            // If we have a valid rate (can't compute if rate is 0),
            // then determine how much time (in seconds) is remaining.
            if ( _speed > 0) {
                double kbLeft = (
                                 (double)getLength() - 
                                 (double)UPLOADER.getTotalAmountUploaded()
                                ) / 1024.0;
                _timeLeft = (int)(kbLeft / _speed);
            }
    }

	/**
	 * Set the _progress variable based on the cumulative amount
	 * read, current amount uploaded & the filesize.
	 */
	private void setProgress() {
        double d = (double)(UPLOADER.getTotalAmountUploaded())/(double)getLength();
        _progress = (int)Math.round(d*100);
    }


	/**
	 * Returns whether or not this upload is in what is considered an "inactive"
	 * state, such as completeed, aborted, failed, etc.
	 *
	 * @return <tt>true</tt> if this upload is in an inactive state,
	 *  <tt>false</tt> otherwise
	 */
	boolean isInactive() {
	    //The upload is active up until 'remove' has been called on it.
	    return _endTime != -1;
	}

	/**
	 * Returns whether or not the upload for this line is currently uploading
	 *
	 * @return <tt>true</tt> if this upload is currently uploading,
	 *  <tt>false</tt> otherwise
	 */
	boolean isUploading() {
		return _state == Uploader.UPLOADING;
	}

	/**
	 * Updates the connection persistance.  Changes how cleanup() works.
	 */
	void setPersistConnection(boolean persist) {
	    _persistConnection = persist;
	}

	/**
	 * Sets the time this upload finished.
     * When this is called, we create a fake "Uploader" object so that the real
     * Uploader can have all its references garbage collected.  Otherwise, we
     * can end up holding too many things in memory.
	 */
	void setEndTime(long time) {
	    _endTime = time;
        UPLOADER = new FakeUploader(UPLOADER);
        super.initialize(UPLOADER);
	}
    
    private static class FakeUploader implements Uploader {
        private final int idx;
        private final int tUp;
        private final int gPort;
        private final float mBand;
        private final float aBand;
        private final String name;
        private final int size;
        private final String host;
        private final int state;
        private final String agent;
        private final boolean chat;
        private final boolean browse;
        private final int lastState;
        private final FileDesc fd;
                
        FakeUploader(Uploader u) {
            idx = u.getIndex();
            tUp = u.getTotalAmountUploaded();
            gPort = u.getGnutellaPort();
            float bandwidth;
            try {
                bandwidth = u.getMeasuredBandwidth();
            } catch(InsufficientDataException e) {
                bandwidth = 0;
            }
            mBand = bandwidth;
            aBand = u.getAverageBandwidth();
            name = u.getFileName();
            size = u.getFileSize();
            host = u.getHost();
            state = u.getState();
            chat = u.isChatEnabled();
            browse = u.isBrowseHostEnabled();
            agent = u.getUserAgent();
            lastState = u.getLastTransferState();
            fd = u.getFileDesc();
        }    
    
        public void stop() { }
        public String getFileName() { return name; }
        public int getFileSize() { return size; }
        public int getAmountRequested() { return 0; }
        public FileDesc getFileDesc() { return fd; }
        public int getIndex() { return idx; }
        public int amountUploaded() { return 0; }
        public int getTotalAmountUploaded() { return tUp; }
        public String getHost() { return host; }
        public int getState() { return state; }
        public int getLastTransferState() { return lastState; }
        public void setState(int s) { }
        public void writeResponse() { }
        public boolean isChatEnabled() { return chat; }
        public boolean isBrowseHostEnabled() { return browse; }
        public int getGnutellaPort() { return gPort; }
        public String getUserAgent() { return agent; }
        public boolean isHeaderParsed() { return true; }
        public boolean supportsQueueing() { return false; }
        public HTTPRequestMethod getMethod() { return HTTPRequestMethod.HEAD; }
        public int getQueuePosition() { return -1; }
        public boolean isInactive() { return true; }
        public void measureBandwidth() {  }
        public float getMeasuredBandwidth() { return mBand; }
        public float getAverageBandwidth() { return aBand; }
	}
}
