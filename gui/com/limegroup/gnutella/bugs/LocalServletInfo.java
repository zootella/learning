package com.limegroup.gnutella.bugs;


/**
 * This class encapsulates all of the data for an individual client machine
 * for an individual bug report.<p>  
 *
 * The servlet utilizes this class to reconstruct the data for the
 * client machine to determine the update information to return.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public class LocalServletInfo extends LocalAbstractInfo {	
    
    /**
     * Adds one key/value pair to the data stored in this object.  This
     * method is used on the servlet to reconstruct the 
     * <tt>LocalClientInfo</tt> object.
     *
     * @param key the key for the pair
     * @param value the value for the pair
     */
    public void addKeyValuePair(final String key, final String value) {
		if(key.equals(LIMEWIRE_VERSION))
		    _limewireVersion = value;
		else if(key.equals(JAVA_VERSION))
		    _javaVersion = value;
        else if(key.equals(OS))
		    _os = value;
        else if(key.equals(OS_VERSION))
		    _osVersion = value;
        else if(key.equals(ARCHITECTURE))
		    _architecture = value;
        else if(key.equals(FREE_MEMORY))
		    _freeMemory = value;
        else if(key.equals(TOTAL_MEMORY))
		    _totalMemory = value;
        else if(key.equals(BUG))
		    _bug = value;
        else if(key.equals(CURRENT_THREAD))
		    _currentThread = value;
		else if(key.equals(PROPS))
		    _props = value;
        else if(key.equals(UPTIME))
		    _upTime = value;
        else if(key.equals(CONNECTED))
		    _connected = value;
        else if(key.equals(UP_TO_UP))
		    _upToUp = value;
        else if(key.equals(UP_TO_LEAF))
		    _upToLeaf = value;
        else if(key.equals(LEAF_TO_UP))
		    _leafToUp = value;
        else if(key.equals(OLD_CONNECTIONS))
		    _oldConnections = value;
        else if(key.equals(ULTRAPEER))
		    _ultrapeer = value;
		else if(key.equals(LEAF))
		    _leaf = value;
        else if(key.equals(ACTIVE_UPLOADS))
		    _activeUploads = value;
        else if(key.equals(QUEUED_UPLOADS))
		    _queuedUploads = value;
        else if(key.equals(ACTIVE_DOWNLOADS))
		    _activeDownloads = value;
        else if(key.equals(HTTP_DOWNLOADERS))
		    _httpDownloaders = value;
        else if(key.equals(WAITING_DOWNLOADERS))
		    _waitingDownloaders = value;
        else if(key.equals(ACCEPTED_INCOMING))
		    _acceptedIncoming = value;
        else if(key.equals(SHARED_FILES))
		    _sharedFiles = value;
		else if(key.equals(OTHER_THREADS))
		    _otherThreads = value;
        else if(key.equals(DETAIL))
		    _detail = value;
        else if(key.equals(OTHER_BUG))
		    _otherBug = value;
        else if(key.equals(JAVA_VENDOR))
		    _javaVendor = value;
        else if(key.equals(THREAD_COUNT))
		    _threadCount = value;
        else if(key.equals(BUG_NAME))
            _bugName = value;
        else if(key.equals(GUESS_CAPABLE))
            _guessCapable = value;
        else if (key.equals(SOLICITED_CAPABLE))
        	_solicitedCapable=value;
        else if (key.equals(LATEST_SIMPP))
        	_latestSIMPP=value;
        else if (key.equals(PORT_STABLE))
            _portStable=value;
        else if (key.equals(CAN_DO_FWT))
            _canDoFWT=value;
        else if (key.equals(LAST_REPORTED_PORT))
            _lastReportedPort=value;
        else if (key.equals(EXTERNAL_PORT))
            _externalPort=value;
        else if (key.equals(RECEIVED_IP_PONG))
            _receivedIpPong=value;
        else if (key.equals(FATAL_ERROR))
            _fatalError=value;
	    // else just ignore it
    }
    
    public String getLimeWireVersion() {
        return _limewireVersion;
    }
    
    public String getJavaVersion() {
		return _javaVersion;
    }
    
    public String getOS() {    
        return _os;
    }
    
    public String getOSVersion() {
        return _osVersion;
    }
    
    public String getArchitecture() {
        return _architecture;
    }
    
    public String getFreeMemory() {
        return _freeMemory;
    }
    
    public String getTotalMemory() {
        return _totalMemory;
    }
    
    public String getBug() {
        return _bug;
    }
    
    public String getCurrentThread() {
        return _currentThread;
    }
    
    public String getProps() {
		return _props;
    }
    
    public String getUpTime() {
        return _upTime;
    }
    
    public String getConnected() {
        return _connected;
    }
    
    public String getUpToUp() {
        return _upToUp;
    }
    
    public String getUpToLeaf() {
        return _upToLeaf;
    }
    
    public String getLeafToUp() {
        return _leafToUp;
    }
    
    public String getOldConnections() {
        return _oldConnections;
    }
    
    public String getUltrapeer() {
        return _ultrapeer;
    }
    
    public String getLeaf() {
		return _leaf;
    }
    
    public String getActiveUploads() {
        return _activeUploads;
    }
    
    public String getQueuedUploads() {
        return _queuedUploads;
    }
    
    public String getActiveDownloads() {
        return _activeDownloads;
    }
    
    public String getHttpDownloaders() {
        return _httpDownloaders;
    }
    
    public String getWaitingDownloaders() {
        return _waitingDownloaders;
    }
    
    public String getAcceptedIncoming() {    
        return _acceptedIncoming;
    }
    
    public String getSharedFiles() {
        return _sharedFiles;
    }
    
    public String getOtherThreads() {
		return _otherThreads;
    }
    
    public String getDetail() {
        return _detail;
    }
    
    public String getOtherBug() {
        return _otherBug;
    }
    
    public String getJavaVendor() {
        return _javaVendor;
    }
    
    public String getThreadCount() {
        return _threadCount;
    }
    
    public String getBugName() {
        return _bugName;
    }

    public String getGuessCapable() {
        return _guessCapable;
    }
    
    public String getSolicitedCapable() {
    	return _solicitedCapable;
    }
    
    public String getLatestSIMPP() {
    	return _latestSIMPP;
    }
    
//    public String getIpStable() {
//        return _ipStable;
//    }
    
    public String getPortStable() {
        return _portStable;
    }
    
    public String getCanDoFWT() {
        return _canDoFWT;
    }
    
    public String getLastReportedPort() {
        return _lastReportedPort;
    }
    
    public String getExternalPort() {
        return _externalPort;
    }
    
    public String getReceivedIpPong() {
        return _receivedIpPong;
    }
    
    public String getFatalError() {
        return _fatalError;
    }
}









