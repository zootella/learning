package com.limegroup.gnutella.gui.connection;


import com.limegroup.gnutella.ManagedConnection;
import com.limegroup.gnutella.gui.GUIMediator;


/**
 * Wrapper class that acts as a comparable for the dropped i/o info.
 * @author sam berlin
 */
public final class ProtocolHolder implements Comparable {
	
	/**
	 * Variable for the string representation
	 */
	private String _string;
	
	private static final String LEAF =
        GUIMediator.getStringResource("CV_TABLE_STRING_LEAF");
        
    private static final String ULTRAPEER =
        GUIMediator.getStringResource("CV_TABLE_STRING_ULTRAPEER");
        
    private static final String PEER =
        GUIMediator.getStringResource("CV_TABLE_STRING_PEER");
        
    private static final String STANDARD =
        GUIMediator.getStringResource("CV_TABLE_STRING_STANDARD");    

	/**
	 * Variable for the info.
	 */
	private ManagedConnection _c;

	/**
	 * The constructor sets  the connection
	 */
	public ProtocolHolder(ManagedConnection c) {
	    _c = c;
        if( c.isSupernodeClientConnection() )
            _string = LEAF;
        else if( c.isClientSupernodeConnection() )
            _string = ULTRAPEER;
        else if( c.isSupernodeSupernodeConnection() )
            _string = PEER;
        else
            _string = STANDARD;
	}
	
	/**
	 * Add up the two things and see which is larger.
	 */
	public int compareTo(Object o) {
	    ProtocolHolder other = (ProtocolHolder)o;
	    return weightHostInfo(_c) - weightHostInfo(other._c);
	}
	
    private static int weightHostInfo(ManagedConnection c) {
        //Assign weight based on bandwidth:
        //4. ultrapeer->ultrapeer
        //3. old-fashioned (unrouted)
        //2. ultrapeer->leaf
        //1. leaf->ultrapeer
        if (c.isSupernodeConnection()) {
            if (c.isClientSupernodeConnection())
                return 1;
            else
                return 4;                
        } else if (c.isSupernodeClientConnection()) {
            return 2;
        }
        return 3;
    }	

	/**
	 *
	 * @return the formatted string
	 */
	public String toString() {
		return _string;
	}
}
