package com.limegroup.gnutella.bugs;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class maintains protected constants and variables for 
 * <tt>LocalServletInfo</tt> and <tt>LocalClientInfo</tt>,
 * the classes that contain the data for the client machine 
 * reporting the bug.  This class simply ensures that they are
 * using the same values.  It also handles generating a bug
 * report string, so that both classes will create bug reports
 * that are exactly alike.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
abstract class LocalAbstractInfo {
    
    /**
     * Constant for the LimeWire version.
     */
    protected static final String LIMEWIRE_VERSION = "1";
    protected String _limewireVersion;
    
    /**
     * Constant for the Java version.
     */
    protected static final String JAVA_VERSION = "2";
    protected String _javaVersion;
    
    /**
     * Constant for the OS.
     */
    protected static final String OS = "3";
    protected String _os;
    
    /**
     * Constant for the OS version.
     */
    protected static final String OS_VERSION = "4";
    protected String _osVersion;
    
    /**
     * Constant for the architecture.
     */
    protected static final String ARCHITECTURE = "5";
    protected String _architecture;
    
    /**
     * Constant for the free memory.
     */
    protected static final String FREE_MEMORY = "6";
    protected String _freeMemory;
    
    /**
     * Constant for the total memory.
     */
    protected static final String TOTAL_MEMORY = "7";
    protected String _totalMemory;
    
    /**
     * Constant for the exception to report.
     */
    protected static final String BUG = "8";
    protected String _bug;
    
    /**
     * Constant for the current thread name.
     */
    protected static final String CURRENT_THREAD = "9";
    protected String _currentThread;
    
    /**
     * Constant for the Properties object.
     */
    protected static final String PROPS = "10";
    protected String _props;
    
    /**
     * Constant for the uptime.
     */
    protected static final String UPTIME = "11";
    protected String _upTime;
    
    /**
     * Constant for the connection status.
     */
    protected static final String CONNECTED = "12";
    protected String _connected;
    
    /**
     * Constant for the number of ultrapeer -> ultrapeer connections.
     */
    protected static final String UP_TO_UP = "13";
    protected String _upToUp;
    
    /**
     * Constant for the number of up -> leaf connections.
     */
    protected static final String UP_TO_LEAF = "14";
    protected String _upToLeaf;
    
    /**
     * Constant for the number of leaf -> up connections.
     */
    protected static final String LEAF_TO_UP = "15";
    protected String _leafToUp;
    
    /**
     * Constant for the number of old connections.
     */
    protected static final String OLD_CONNECTIONS = "16";
    protected String _oldConnections;
    
    /**
     * Constant for ultrapeer status.
     */
    protected static final String ULTRAPEER = "17";
    protected String _ultrapeer;
    
    /**
     * Constant for leaf status.
     */
    protected static final String LEAF = "18";
    protected String _leaf;
    
    /**
     * Constant for the number of active uploads.
     */
    protected static final String ACTIVE_UPLOADS = "19";
    protected String _activeUploads;
    
    /**
     * Constant for the number of queued uploads.
     */
    protected static final String QUEUED_UPLOADS = "20";
    protected String _queuedUploads;
    
    /**
     * Constant for the number of active downloads.
     */
    protected static final String ACTIVE_DOWNLOADS = "21";
    protected String _activeDownloads;
    
    /**
     * Constant for the number of http downloaders.
     */
    protected static final String HTTP_DOWNLOADERS = "22";
    protected String _httpDownloaders;
    
    /**
     * Constant for the number of waiting downloaders.
     */
    protected static final String WAITING_DOWNLOADERS = "23";
    protected String _waitingDownloaders;
    
    /**
     * Constant for whether or not incoming has been accepted.
     */
    protected static final String ACCEPTED_INCOMING = "24";
    protected String _acceptedIncoming;
    
    /**
     * Constant for the number of shared files.
     */
    protected static final String SHARED_FILES = "25";
    protected String _sharedFiles;
    
    /**
     * Constant for the other active threads.
     */
    protected static final String OTHER_THREADS = "26";
    protected String _otherThreads;
    
    /**
     * Constant for the detail message.
     */
    protected static final String DETAIL = "27";
    protected String _detail;
    
    /**
     * Constant for an underlying bug, if any.
     */
    protected static final String OTHER_BUG = "28";
    protected String _otherBug;
    
    /**
     * Constant for the java vendor.
     */
    protected static final String JAVA_VENDOR = "29";
    protected String _javaVendor;
    
    /**
     * Constant for the total amount of active threads.
     */
    protected static final String THREAD_COUNT = "30";
    protected String _threadCount;
    
    /**
     * Constant for the exception's name.
     */
    protected static final String BUG_NAME = "31";
    protected String _bugName;
    
    /**
     * Constant for guess capability.
     */
    protected static final String GUESS_CAPABLE = "32";
    protected String _guessCapable;
    
    protected static final String SOLICITED_CAPABLE = "33";
    protected String _solicitedCapable;
    
    protected static final String LATEST_SIMPP = "34";
    protected String _latestSIMPP;
    
//    protected static final String IP_STABLE = "35";
//    protected String _ipStable;
    
    protected static final String PORT_STABLE = "36";
    protected String _portStable;
    
    protected static final String CAN_DO_FWT = "37";
    protected String _canDoFWT;
    
    protected static final String LAST_REPORTED_PORT = "38";
    protected String _lastReportedPort;
    
    protected static final String EXTERNAL_PORT = "39";
    protected String _externalPort;
    
    protected static final String RECEIVED_IP_PONG = "40";
    protected String _receivedIpPong;
    
    protected static final String FATAL_ERROR = "41";
    protected String _fatalError;
    
    /**
     * Returns this bug as a bug report.
     */
    public String toBugReport() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("LimeWire version " + _limewireVersion);
        pw.println("Java version " + _javaVersion + " from " + _javaVendor);
        pw.println(_os + " v. " + _osVersion + " on " + _architecture);
        pw.println("Free/total memory: " + _freeMemory + "/" + _totalMemory);
        pw.println();
        
        if(isFatalError()) {
            pw.println("FATAL ERROR!");
            pw.println();
        }
		
		pw.println(_bug);
		pw.println();
		
		if( _detail != null ) {
		    pw.println("Detail: " + _detail);
		    pw.println();
		}

        pw.println("-- listing session information --");
        pw.println("Current thread: " + _currentThread);
        pw.println("Active Threads: " + _threadCount);
        append(pw, "Uptime", _upTime);
        append(pw, "Is Connected", _connected);
        append(pw, "Number of Ultrapeer -> Ultrapeer Connections", _upToUp);
        append(pw, "Number of Ultrapeer -> Leaf Connections", _upToLeaf);
        append(pw, "Number of Leaf -> Ultrapeer Connections", _leafToUp);
        append(pw, "Number of Old Connections", _oldConnections);
        append(pw, "Acting as Ultrapeer", _ultrapeer);
        append(pw, "Acting as Shielded Leaf", _leaf);
        append(pw, "Number of Active Uploads", _activeUploads);
        append(pw, "Number of Queued Uploads", _queuedUploads);
    	append(pw, "Number of Active Managed Downloads", _activeDownloads);
    	append(pw, "Number of Active HTTP Downloaders", _httpDownloaders);
        append(pw, "Number of Waiting Downloads", _waitingDownloaders);
    	append(pw, "Received incoming this session", _acceptedIncoming);
    	append(pw, "Number of Shared Files", _sharedFiles);
    	append(pw, "Guess Capable", _guessCapable);
    	append(pw, "Received Solicited UDP",_solicitedCapable);
    	append(pw, "SIMPP version",_latestSIMPP);
    	append(pw, "Port Stable", _portStable);
    	append(pw, "FWT Capable", _canDoFWT);
    	append(pw, "Last Reported Port",_lastReportedPort);
    	append(pw, "External Port", _externalPort);
    	append(pw, "IP Pongs Received",_receivedIpPong);
        pw.println();

	    if( _otherThreads != null ) {
	        pw.println("-- listing threads --");
	        pw.println(_otherThreads);
	        pw.println();
        }
        
        pw.println(_props);
        
        pw.println();
        pw.flush();
        return sw.toString();
    }
    
    /**
     * Appends 'k: v' to pw if v is non null.
     */
    private void append(PrintWriter pw, final String k, final String v) {
        if( v != null ) {
            pw.println(k + ": " + v);
        }
    }
    
    /**
     * Returns the parsed version of the stack trace, without the message
     * between the exception and the stack trace.
     */
    public final String getParsedBug() {
        int colon = _bug.indexOf(':');
        // If no ':', just use the bug as we read it.
        if(colon == -1)
            return _bug;
            
        // If we did find a colon, ensure that it isn't within
        // the file:line part.  If so, return the normal bug.
        if( colon - 4 >= 0 && _bug.substring(colon - 4, colon).equals("java"))
            return _bug;
            
        int ntat = _bug.indexOf("\n\tat", colon);
        // If no \n\tat, just use the bug as we read it.
        if(ntat == -1)
            return _bug;
            
        //Now that we know where the message begins and where the message ends
        //put the bug back together without the message.
        final String parsedBug = _bug.substring(0, colon) + 
                                 _bug.substring(ntat);
        return parsedBug;
    }   
        
	/**
	 * Prints the bug's name. This is used primarily in generating the
	 * servlet log.
     *
     * @return a <tt>String</tt> containing the bug's name.
	 */
    public String toString() {
        return _bugName;
    }
    
    /**
     * Determines if this was a fatal error.
     */
    public boolean isFatalError() {
        return _fatalError != null && _fatalError.equalsIgnoreCase("true");
    }
}






