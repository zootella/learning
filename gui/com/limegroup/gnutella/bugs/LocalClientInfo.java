package com.limegroup.gnutella.bugs;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.NameValuePair;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.UDPService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.settings.LimeProps;
import com.limegroup.gnutella.settings.Setting;
import com.limegroup.gnutella.settings.SettingsFactory;
import com.limegroup.gnutella.simpp.SimppManager;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class encapsulates all of the data for an individual client machine
 * for an individual bug report.<p>
 *
 * This class collects all of the data for the local machine and provides
 * access to that data in url-encoded form.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class LocalClientInfo extends LocalAbstractInfo {
	
	/**
	 * Creates information about this bug from the bug, thread, and detail.
	 */
	public LocalClientInfo(Throwable bug, Thread running, String detail, boolean fatal) {
	    //Store the basic information ...	    
	    _limewireVersion = CommonUtils.getLimeWireVersion();
	    _javaVersion = CommonUtils.getJavaVersion();
        _javaVendor = prop("java.vendor");
	    _os = CommonUtils.getOS();
	    _osVersion = prop("os.version");
	    _architecture = prop("os.arch");
	    _freeMemory = "" + Runtime.getRuntime().freeMemory();
	    _totalMemory = "" + Runtime.getRuntime().totalMemory();
	    
	    //Store information about the bug and the current thread.
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    bug.printStackTrace(pw);
	    pw.flush();
	    _bug = sw.toString();
	    _currentThread = running.getName();
	    
	    _bugName = bug.getClass().getName();
	    
	    _fatalError = "" + fatal;
	    
	    //Store the properties.
	    sw = new StringWriter();
	    pw = new PrintWriter(sw);
		File propsFile = new File(CommonUtils.getUserSettingsDir(),
								  "limewire.props");
		Properties props = new Properties();
		// Load the properties from SettingsFactory, excluding
		// FileSettings and FileArraySettings.
		SettingsFactory sf = LimeProps.instance().getFactory();
		synchronized(sf) {
		    Iterator it = sf.iterator();
		    while(it.hasNext()) {
		        Setting set = (Setting)it.next();
		        if(!set.isPrivate() && !set.isDefault())
		            props.put(set.getKey(), set.getValueAsString());
            }
        }
        // list the properties in the PrintWriter.
        props.list(pw);
		pw.flush();
		_props = sw.toString();
		
		//Store extra debugging information.
		if( GUIMediator.isConstructed() && RouterService.isStarted() ) {
            _upTime = GUIUtils.seconds2time(
                (int)(RouterService.getCurrentUptime()/1000));
            _connected = "" + RouterService.isConnected();
            _upToUp = ""+RouterService.getNumUltrapeerToUltrapeerConnections();
            _upToLeaf = "" + RouterService.getNumUltrapeerToLeafConnections();
            _leafToUp = "" + RouterService.getNumLeafToUltrapeerConnections();
            _oldConnections = "" + RouterService.getNumOldConnections();
            _ultrapeer = "" + RouterService.isSupernode();
            _leaf = "" + RouterService.isShieldedLeaf();
            _activeUploads = "" + RouterService.getNumUploads();
            _queuedUploads = "" + RouterService.getNumQueuedUploads();
            _activeDownloads = "" + RouterService.getNumActiveDownloads();
            _httpDownloaders = "" +RouterService.getNumIndividualDownloaders();
            _waitingDownloaders = "" + RouterService.getNumWaitingDownloads();
            _acceptedIncoming = "" +RouterService.acceptedIncomingConnection();
            _sharedFiles = "" + RouterService.getNumSharedFiles();
            _guessCapable = "" + RouterService.isGUESSCapable();
            _solicitedCapable= ""+RouterService.canReceiveSolicited();
            _latestSIMPP= "" + SimppManager.instance().getVersion();
            _portStable = "" +UDPService.instance().portStable();
            _canDoFWT = "" + UDPService.instance().canDoFWT();
            _lastReportedPort = ""+UDPService.instance().lastReportedPort();
            _externalPort = ""+RouterService.getPort();
            _receivedIpPong = ""+UDPService.instance().receivedIpPong();
        }
            
        
        //Store the detail, thread counts, and other information.
        _detail = detail;

        Thread[] allThreads = new Thread[Thread.activeCount()];
        int copied = Thread.enumerate(allThreads);
        _threadCount = "" + copied;
        Map threads = new HashMap();
        for(int i = 0; i < copied; i++) {
            String name = allThreads[i].getName();
            Object val = threads.get(name);
            if(val == null)
                threads.put(name, new Integer(1));
            else {
                int num = ((Integer)val).intValue()+1;
                threads.put(name,new Integer(num));
            }
        }
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        for(Iterator i = threads.entrySet().iterator(); i.hasNext();) {
            Map.Entry info = (Map.Entry)i.next();
            pw.println( info.getKey() + ": " + info.getValue());
        }
        pw.flush();
        _otherThreads = sw.toString();
            
	}
	
    /** 
	 * Returns the System property with the given name, or
     * "?" if it is unknown. 
	 */
    private final String prop(String name) {
        String value = System.getProperty(name);
        if (value == null) return "?";
        else return value;
    }	

	/** 
	 * Returns a an array of the name/value pairs of this info.
     *
     * @return an array of the name/value pairs of this info.
	 */
	public final NameValuePair[] getPostRequestParams() {
	    List params = new LinkedList();
        append(params, LIMEWIRE_VERSION, _limewireVersion);
        append(params, JAVA_VERSION, _javaVersion);
        append(params, OS, _os);
        append(params, OS_VERSION, _osVersion);
        append(params, ARCHITECTURE, _architecture);
        append(params, FREE_MEMORY, _freeMemory);
        append(params, TOTAL_MEMORY, _totalMemory);
        append(params, BUG, _bug);
        append(params, CURRENT_THREAD, _currentThread);
        append(params, PROPS, _props);
        append(params, UPTIME, _upTime);
        append(params, CONNECTED, _connected);
        append(params, UP_TO_UP, _upToUp);
        append(params, UP_TO_LEAF, _upToLeaf);
        append(params, LEAF_TO_UP, _leafToUp);
        append(params, OLD_CONNECTIONS, _oldConnections);
        append(params, ULTRAPEER, _ultrapeer);
        append(params, LEAF, _leaf);
        append(params, ACTIVE_UPLOADS, _activeUploads);
        append(params, QUEUED_UPLOADS, _queuedUploads);
        append(params, ACTIVE_DOWNLOADS, _activeDownloads);
        append(params, HTTP_DOWNLOADERS, _httpDownloaders);
        append(params, WAITING_DOWNLOADERS, _waitingDownloaders);
        append(params, ACCEPTED_INCOMING, _acceptedIncoming);
        append(params, SHARED_FILES, _sharedFiles);
        append(params, OTHER_THREADS, _otherThreads);
        append(params, DETAIL, _detail);
        append(params, OTHER_BUG, _otherBug);
        append(params, JAVA_VENDOR, _javaVendor);
        append(params, THREAD_COUNT, _threadCount);
        append(params, BUG_NAME, _bugName);
        append(params, GUESS_CAPABLE, _guessCapable);
        append(params, SOLICITED_CAPABLE,_solicitedCapable);
        append(params, LATEST_SIMPP,_latestSIMPP);
        //append(params, IP_STABLE,_ipStable);
        append(params, PORT_STABLE, _portStable);
        append(params, CAN_DO_FWT, _canDoFWT);
        append(params, LAST_REPORTED_PORT, _lastReportedPort);
        append(params, EXTERNAL_PORT, _externalPort);
        append(params, RECEIVED_IP_PONG, _receivedIpPong);
        append(params, FATAL_ERROR, _fatalError);
        return (NameValuePair[])params.toArray(
                            new NameValuePair[params.size()]);
	}
	
	/**
	 * Appends a NameValuePair of k/v to l if v is non-null.
	 */
	private final void append(List l, final String k, final String v){
	    if( v != null )
	        l.add(new NameValuePair(k, v));
	}
}
