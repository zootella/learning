package com.limegroup.gnutella.gui.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.util.IpPort;
import com.limegroup.gnutella.util.NetworkUtils;



/**
 * Holds one or more hosts.  Used for displaying the IP.
 */
class EndpointHolder implements Comparable {
    
    /**
     * String for "Multiple"
     */
    private static final String MULTIPLE =
        GUIMediator.getStringResource("SEARCH_MULTIPLE_HOSTS");    

    /**
     * The host this represents.
     */
    private final String _hostName;
    
    /**
     * The port of this host.
     */
    private final int _port;

    /**
     * Whether or not this IP is private.
     */
    private boolean _isPrivate;
    
    /**
     * The tag to display.
     */
    private String _tag;
    
    /**
     * The hosts this holds.
     */
    private Set _hosts;
        
    /**
     * Builds an EndpointHolder with the specified host/port.
     */
    EndpointHolder(final String host, int port, boolean replyToMCast) {
        _hostName = host;
        _port = port;
        _isPrivate = !replyToMCast && NetworkUtils.isPrivateAddress(host);
        _tag = host;
    }
    
    void addHost(final String host, int port) {
        if(_hosts == null) {
            _hosts = new HashSet();
            _hosts.add(_hostName + ":" + _port);
        }
        _hosts.add(host + ":" + port);
        _tag = MULTIPLE + " (" + _hosts.size() + ")";
        _isPrivate = false;
    }
    
    void addHosts(Set alts) {
        if(_hosts == null) {
            _hosts = new HashSet();
            _hosts.add(_hostName + ":" + _port);
        }
        for(Iterator i = alts.iterator(); i.hasNext(); ) {
            IpPort next = (IpPort)i.next();
            _hosts.add(next.getAddress() + ":" + next.getPort());
        }
        _tag = MULTIPLE + " (" + _hosts.size() + ")";
        _isPrivate = false;
    }
    
    /**
     * Gets the set of hosts.
     */
    Set getHosts() {
        return _hosts;
    }
    
    /**
     * Returns the number of locations this holder knows about.
     */
    int numLocations() {
        return _hosts == null ? 1 : _hosts.size();
    }
    
    /**
     * Whether or not this endpoint represents a private address.
     */
    boolean isPrivateAddress() {
        return _isPrivate;
    }

    /**
     * Returns the tag of this holder.
     */
    public String toString() {
        return _tag;
    }
    
    public int compareTo(Object o) {
        EndpointHolder other = (EndpointHolder)o;
        int n1 = numLocations(), n2 = other.numLocations();
        if(n1 == 1 && n2 == 1)
            return _tag.compareTo(other._tag);
        else
            return n1 - n2;
    }
}
    
