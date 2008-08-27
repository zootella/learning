package com.limegroup.gnutella.bugs;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.http.HttpClientManager;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class handles accessing the servlet, sending it data about the client
 * configuration, and obtaining information about the next time this or any
 * bug can be sent.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class ServletAccessor {
    
    private static final Log LOG = LogFactory.getLog(ServletAccessor.class);
    
    /**
	 * Constant number of milliseconds to wait before timing out the
	 * connection to the servlet.
	 */
	private static final int CONNECT_TIMEOUT = 10 * 1000; // 10 seconds.
    
	/**
	 * Constant for the servlet url.
	 */
	private static final String SERVLET_URL =
		"http://bugreports.limewire.com/bugs/servlet/BugHandler";

	/**
	 * Variable for the object that contains information retrieved from
	 * the servlet.  
	 */
	private volatile RemoteClientInfo _remoteInfo = null;

	/**
	 * Package-access constructor
	 */
	ServletAccessor() {}

	/**
	 * Contacts the application servlet and sends it the information 
	 * contained in the <tt>LocalClientInfo</tt> object.  This method 
	 * also builds a <tt>RemoteClientInfo</tt> object from the information 
	 * obtained from the servlet.
	 *
	 * @return a <tt>RemoteClientInfo</tt> object that encapsulates the 
	 *         data about when to next send a bug.
	 * @param localInfo is an object encapsulating information about the
	 *                  local machine to send to the remote server
	 */
	synchronized RemoteClientInfo getRemoteBugInfo(LocalClientInfo localInfo) {
		NameValuePair[] params = localInfo.getPostRequestParams();
		RemoteClientInfo remoteInfo = null;
        PostMethod post = new PostMethod(SERVLET_URL);
        post.addRequestHeader("Cache-Control", "no-cache");
        post.addRequestHeader("User-Agent", CommonUtils.getHttpServer());
        post.addRequestHeader("Content-Type", 
                              "application/x-www-form-urlencoded; charset=UTF-8");
        post.setFollowRedirects(false);
        post.addParameters(params);
        HttpClient client = HttpClientManager.getNewClient();
        client.setConnectionTimeout(CONNECT_TIMEOUT);

        // create the object to record the info        
        _remoteInfo = new RemoteClientInfo();
        try {
            //Execute the remote info.
            client.executeMethod(post);
            String response = post.getResponseBodyAsString();
            if(LOG.isDebugEnabled())
                LOG.debug("Got response: " + response);
            // process results if valid status code
            if(post.getStatusCode() == HttpStatus.SC_OK)
    			_remoteInfo.addRemoteInfo(response);
            // otherwise mark as server down.
            else {
                if(LOG.isWarnEnabled())
                    LOG.warn("Servlet connect failed, code: " + 
                             post.getStatusCode());
                _remoteInfo.connectFailed();
            }
		} catch(IOException ioe) {
		    LOG.error("Error connecting to bug servlet", ioe);
            _remoteInfo.connectFailed();
		} finally {
			if(post != null)
			    post.releaseConnection();
        }
        return _remoteInfo;
	}
}

