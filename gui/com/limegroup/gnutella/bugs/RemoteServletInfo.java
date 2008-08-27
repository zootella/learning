package com.limegroup.gnutella.bugs;

import java.io.IOException;
import java.net.URLEncoder;

import com.limegroup.gnutella.util.FixedsizeForgetfulHashMap;

/**
 * This class handles creating the bug information for client reporting
 * the bug.  It constructs the appropriate bug information based
 * on the data supplied by the client, such as the operating system, the
 * LimeWire version, etc.<p>
 *
 * This class is reconstructed on the client side by the 
 * <tt>RemoteClientInfo</tt> class.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class RemoteServletInfo extends RemoteAbstractInfo {
    
    /**
     * The instance to use for constructing response strings.
     */
    private static final RemoteServletInfo INSTANCE = new RemoteServletInfo();
	
	/**
	 * Map of recently seen stack traces.
	 * Each stack trace maps to an integer which represents
	 * the number of times we've seen this bug.
	 * A FixedsizeForgetfulHashMap is used so that only recently reported
	 * bugs are remembered.  Those reported less frequently will eventually
	 * fall out of the map. 
	 *
	 * The information is used to tell the host reporting the bug to not
	 * respond with this particular bug again for a long time period.
	 *
	 * LOCKING: Obtain this' monitor.
	 */
	private final FixedsizeForgetfulHashMap /* String -> Integer */ BUGS =
	    new FixedsizeForgetfulHashMap(TOTAL_BUGS);
	
	/**
	 * The number of bugs to keep in the above map.
	 */
	private static final int TOTAL_BUGS = 100;
	
	/**
	 * The amount of times we'll read a bug before telling them to stop
	 * reporting it.
	 */
	private static final int BUG_CUTOFF = 3;
	
	/**
	 * The time, in milliseconds (as a string), that we'll tell people
	 * to wait before sending us another bug if the count was over
	 * the cutoff. (1 day)
	 */
	private static final String WAIT_TIME = "" + (24 * 60 * 60 * 1000);
	
	/**
	 * The time, in milliseconds (as a string), that we'll tell people
	 * to wait before sending any bug after a single bug is sent.
	 * (10 minutes)
	 */
	private static final String ANY_TIME = "" + (10 * 60 * 1000);
	
	/**
	 * The 'never' time to use for older clients that we just want to say:
	 * "SHUT UP" to. (1 year)
	 */
	private static final String SHUT_UP = "" + (365 * 24 * 60 * 60 * 1000);
	
	/**
	 * The response to send back to these clients.
	 */
	private static final String OLD_RESPONSE =
	    NEXT_THIS_BUG_TIME + "=" + SHUT_UP + "&" +
	    NEXT_ANY_BUG_TIME  + "=" + SHUT_UP;
	
	/**
	 * The last version whose bug reports we'll accept without telling the
	 * client to be quiet.
	 */
	private static final int MAJOR_VERSION   = 4;
	private static final int MINOR_VERSION   = 9;
	private static final int SERVICE_VERSION = 30;
	
	/**
	 * The only instance of this class to use.
	 */
	public static RemoteServletInfo instance() {
	    return INSTANCE;
	}
    
	/**
     * Generates the appropriate response based on the information supplied
     * by the client reporting the bug.
     *
	 * Returns a string in url encoding containing the data for the remote
	 * remote update.<p>     
     *
     * @param localInfo the <tt>LocalServletInfo</tt> instance 
     *  containing data about the client reporting the bug
     *
	 * @return an url-encoded <tt>String</tt> containing all of the 
	 *         necessary fields for responding to the bug report
	 */
    public String getURLEncodedString(LocalServletInfo localInfo)
      throws IOException {
        String version     = localInfo.getLimeWireVersion();
        String os          = localInfo.getOS();	

        if(version == null || version.equals("")) 
            throw new IOException("invalid version");        
        if(os == null || os.equals(""))      
            throw new IOException("invalid operating system");
                
        if( isOldClient(localInfo) )
            return OLD_RESPONSE;
            
        String nextThisBugTime;
        String nextAnyBugTime;
        
        synchronized(this) {
            String bug = localInfo.getParsedBug();
            Integer count = (Integer)BUGS.get(bug);
            nextAnyBugTime = ANY_TIME;
            if( count == null ) { // first time, insert.
                BUGS.put(bug, new Integer(1));
                nextThisBugTime = "0";
            } else {
                int newCount = count.intValue() + 1;
                BUGS.put(bug, new Integer(newCount));
                if( newCount >= BUG_CUTOFF ) {
                    nextThisBugTime = WAIT_TIME;
                } else {
                    nextThisBugTime = "0";
                }
            }
        }        
		StringBuffer sb = new StringBuffer();
		append(sb, NEXT_THIS_BUG_TIME, nextThisBugTime);
		append(sb, NEXT_ANY_BUG_TIME, nextAnyBugTime);
		sb.setLength(sb.length() - 1);
		return sb.toString();	
    }   
    
    /**
     * Appends 'k=URLEncoder.encode(v)&' to sb if v is non-null.
     */
	private final void append(StringBuffer sb, final String k, final String v) {
	    if( v != null ) {
	        sb.append(k);
	        sb.append("=");
	        sb.append(URLEncoder.encode(v));
	        sb.append("&");
	    }
	}
	
	/**
	 * Determines whether or not the specified client is considered 'old'.
	 */
	 public static final boolean isOldClient(LocalServletInfo info) {
	    final String vers = info.getLimeWireVersion();
        final String os   = info.getOS();
	    
	    int major, minor, service;
	    int dot1, dot2;

        // too many bugs being reported under @version@.
	    if(vers.equals("@version@"))
            return true;
            
        // unsolvable.
        if(os.equals("Mac OS"))
            return true;

        dot1 = vers.indexOf(".");
	    if(dot1 == -1)
	        return true; // unknown, tell'm to go away.
	    dot2 = vers.indexOf(".", dot1 + 1);
	    if(dot2 == -1)
	        return true; // unknown, tell'm to go away too.
	        
        try {
            major = Integer.parseInt(vers.substring(0, dot1));
        } catch(NumberFormatException nfe) {
            return true; // unknown again.
        }
        
        try {
            minor = Integer.parseInt(vers.substring(dot1 + 1, dot2));
        } catch(NumberFormatException nfe) {
            return true; // unknown still.
        }
        
        try {
            int q = dot2 + 1;
            while(q < vers.length() &&  Character.isDigit(vers.charAt(q)))
                q++;
                
            service = Integer.parseInt(vers.substring(dot2 + 1, q));
        } catch(NumberFormatException nfe) {
            return true; // unknown.
        }
        
        if( major < MAJOR_VERSION )
            return true;
        
        if( major == MAJOR_VERSION ) {
            if( minor < MINOR_VERSION )
                return true;
            
            if( minor == MINOR_VERSION ) {
                if( service < SERVICE_VERSION )
                    return true;
            }
        }
        
        return false;
    }
}













