package com.limegroup.gnutella.gui.connection;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import com.limegroup.gnutella.ManagedConnection;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.TimeRemainingHolder;


public final class ConnectionDataLine extends AbstractDataLine {

    /**
     * Host column info
     */
    static final int HOST_IDX = 0;
    private static final LimeTableColumn HOST_COLUMN =
        new LimeTableColumn(HOST_IDX, "CV_COLUMN_HOST",
                            218, true, String.class);

    /**
     * Status column info
     */
    static final int STATUS_IDX = 1;
    private static final LimeTableColumn STATUS_COLUMN =
        new LimeTableColumn(STATUS_IDX, "CV_COLUMN_STATUS",
                        70, true, String.class);

    /**
     * Messages column info
     */
    static final int MESSAGES_IDX = 2;
    private static final LimeTableColumn MESSAGES_COLUMN =
        new LimeTableColumn(MESSAGES_IDX, "CV_COLUMN_MESSAGE",
                        97, true, MessagesHolder.class);

    /**
     * Bandwidth column info
     */
    static final int BANDWIDTH_IDX = 3;
    private static final LimeTableColumn BANDWIDTH_COLUMN =
        new LimeTableColumn(BANDWIDTH_IDX, "CV_COLUMN_BANDWIDTH",
                        115, true, BandwidthHolder.class);

    /**
     * Dropped column info
     */
    static final int DROPPED_IDX = 4;
    private static final LimeTableColumn DROPPED_COLUMN =
        new LimeTableColumn(DROPPED_IDX, "CV_COLUMN_DROPPED",
                        92, true, DroppedHolder.class);
    /**
     * Protocol column info
     */
    static final int PROTOCOL_IDX = 5;
    private static final LimeTableColumn PROTOCOL_COLUMN =
        new LimeTableColumn(PROTOCOL_IDX, "CV_COLUMN_PROTOCOL",
                        60, true, ProtocolHolder.class);

    /**
     * Vendor column info
     */
    static final int VENDOR_IDX = 6;
    private static final LimeTableColumn VENDOR_COLUMN =
        new LimeTableColumn(VENDOR_IDX, "CV_COLUMN_VENDOR",
                        116, true, String.class);

    /**
     * Time connected info
     */
    static final int TIME_IDX = 7;
    private static final LimeTableColumn TIME_COLUMN =
        new LimeTableColumn(TIME_IDX, "CV_COLUMN_TIME",
                        44, true, TimeRemainingHolder.class);
                        
    /**
     * The compression saved statistics.
     */
    static final int COMPRESSION_IDX = 8;
    private static final LimeTableColumn COMPRESS_COLUMN =
        new LimeTableColumn(COMPRESSION_IDX, "CV_COLUMN_COMPRESSION",
                        114, false, DroppedHolder.class);
                        
	/**
     * The value for the percent full in the QRP table.
     */
    static final int QRP_FULL_IDX = 9;
    private static final LimeTableColumn QRP_FULL_COLUMN =
        new LimeTableColumn(QRP_FULL_IDX, "CV_COLUMN_QRP_FULL",
                        70, false, QRPHolder.class);
                        
    static final int QRP_USED_IDX = 10;
    private static final LimeTableColumn QRP_USED_COLUMN =
        new LimeTableColumn(QRP_USED_IDX, "CV_COLUMN_QRP_USED",
                        70, false, String.class);

    /**
     * Total number of columns
     */
    static final int NUMBER_OF_COLUMNS = 11;

    /**
     * The main connection this dataline is based on
     */
    private ManagedConnection MCONNECTION;

    /**
     * String for connecting status
     */
    private static final String CONNECTING_STRING =
        GUIMediator.getStringResource("CV_TABLE_STRING_CONNECTING");

    /**
     * String for outgoing status
     */
    private static final String OUTGOING_STRING =
        GUIMediator.getStringResource("CV_TABLE_STRING_OUTGOING");

    /**
     * String for incoming status
     */
    private static final String INCOMING_STRING =
        GUIMediator.getStringResource("CV_TABLE_STRING_INCOMING");

    /**
     * String for 'Connected on' tooltip
     */
    private static final String CONNECTED_ON =
        GUIMediator.getStringResource("CV_TABLE_STRING_CONNECTED_ON");

    /**
     * Cached host
     */
    private volatile String _host;

    /**
     * Cached status
     */
    private String _status;

    /**
     * Time this connected or initialized
     */
    private long _time;

    /**
     * Whether or not this dataline is in the 'connecting' state
     */
    private boolean _isConnecting = true;

    /**
     * Variable for whether or not the host name has been resolved for
     * this connection.
     */
    private boolean _hasResolvedAddress = false;

    /**
     * Boolean for whether or not the 'host' of a line has changed.
     */
    private static volatile boolean _hostChanged = false;

    /**
     * Boolean for whether a line has updated from connecting to connected
     */
    private static boolean _updated = false;

    /**
     * Number of columns
     */
    public int getColumnCount() { return NUMBER_OF_COLUMNS; }

    /**
     * Sets up the dataline for use with the connection
     */
    public void initialize(Object conn) {
        super.initialize(conn);
        MCONNECTION = (ManagedConnection)conn;

        _host = MCONNECTION.getAddress();

        _status = CONNECTING_STRING;
        _time = System.currentTimeMillis();
    }

    /**
     * Returns the value for the specified index.
     */
    public Object getValueAt(int idx) {
        switch(idx) {
            case HOST_IDX:
                if(!_hasResolvedAddress // hasn't yet resolved address
                   && !_isConnecting // must be connected
                   && (System.currentTimeMillis() - _time) > 10000)
					assignHostName();
                return _host;
            case STATUS_IDX: return _status;
            case MESSAGES_IDX:
                if (_isConnecting) return null;
                return new MessagesHolder(
                    MCONNECTION.getNumMessagesReceived(),
                    MCONNECTION.getNumMessagesSent()
                );
            case BANDWIDTH_IDX:
                if (_isConnecting) return null;
                return new BandwidthHolder(
                    MCONNECTION.getMeasuredDownstreamBandwidth(),
                    MCONNECTION.getMeasuredUpstreamBandwidth()
                );
            case DROPPED_IDX:
                if (_isConnecting) return null;
                // NOTE: this use to be getPercent[Sent|Received]Dropped
                // However that had the side-effect of altering the
                // connection's stats.
                // This provides more accurate statistics anyway,
                // rather than a snapshot-erase-style number.
                return new DroppedHolder(
                     (float)MCONNECTION.getNumReceivedMessagesDropped() /
                       ( (float)MCONNECTION.getNumMessagesReceived() + 1.0f ),
                    (float)MCONNECTION.getNumSentMessagesDropped() /
                       ( (float)MCONNECTION.getNumMessagesSent() + 1.0f )
                );
            case PROTOCOL_IDX:  return new ProtocolHolder( MCONNECTION );
            case VENDOR_IDX:
                if (_isConnecting) return null;
                String vendor = MCONNECTION.getUserAgent();
                return vendor == null ? "" : vendor;
            case TIME_IDX:
                return new TimeRemainingHolder( (int)(
                    (System.currentTimeMillis() - _time) / 1000) );
            case COMPRESSION_IDX:
                if (_isConnecting) return null;
                return new DroppedHolder(
                    MCONNECTION.getReadSavedFromCompression(),
                    MCONNECTION.getSentSavedFromCompression() );
            case QRP_FULL_IDX:
                if(_isConnecting) return null;
                return new QRPHolder(
                    MCONNECTION.getQueryRouteTablePercentFull(),
                    MCONNECTION.getQueryRouteTableSize());
            case QRP_USED_IDX:
                if(_isConnecting) return null;  
                int empty = MCONNECTION.getQueryRouteTableEmptyUnits();
                int inuse = MCONNECTION.getQueryRouteTableUnitsInUse();
                if(empty == -1 || inuse == -1)
                    return null;
                else
                    return empty + " / " + inuse;
        }
        return null;
    }

	/**
	 * Helper method that launches a separate thread to look up the host name
	 * of the given connection.  The thread is necessary because the lookup
	 * can take considerable time.
	 */
	private void assignHostName() {
		// put this outside of the runnable so multiple attempts aren't done.
        _hasResolvedAddress = true;

	    GUIMediator.instance().schedule(new HostAssigner(this));
	}

	/**
	 * Return the table column for this index.
	 */
	public LimeTableColumn getColumn(int idx) {
        switch(idx) {
            case HOST_IDX:      return HOST_COLUMN;
            case STATUS_IDX:    return STATUS_COLUMN;
            case MESSAGES_IDX:  return MESSAGES_COLUMN;
            case BANDWIDTH_IDX: return BANDWIDTH_COLUMN;
            case DROPPED_IDX:   return DROPPED_COLUMN;
            case PROTOCOL_IDX:  return PROTOCOL_COLUMN;
            case VENDOR_IDX:    return VENDOR_COLUMN;
            case TIME_IDX:      return TIME_COLUMN;
            case COMPRESSION_IDX: return COMPRESS_COLUMN;
            case QRP_FULL_IDX:	return QRP_FULL_COLUMN;
            case QRP_USED_IDX: return QRP_USED_COLUMN;
        }
        return null;
    }
    
    public boolean isClippable(int idx) {
        return true;
    }
    
    public int getTypeAheadColumn() {
        return HOST_IDX;
    }

	public boolean isDynamic(int idx) {
	    switch(idx) {
	        case MESSAGES_IDX:
	        case BANDWIDTH_IDX:
	        case DROPPED_IDX:
	        case COMPRESSION_IDX:
            case QRP_FULL_IDX:
            case QRP_USED_IDX:
	            return true;
	        case HOST_IDX:
	            // if a host changed, set it to false for the future
	            // and return true.  otherwise return false.
	            if ( _hostChanged ) {
	                _hostChanged = false;
	                return true;
	            } else {
	                return false;
	            }
	        case VENDOR_IDX:
	        case STATUS_IDX:
	        case PROTOCOL_IDX:
	            if ( _updated ) {
	                _updated = false;
	                return true;
	            } else {
	                return false;
	            }

	    }
	    return false;
	}
	
	boolean isPeer() {
	    return MCONNECTION.isSupernodeSupernodeConnection();
    }
    
    boolean isUltrapeer() {
        return MCONNECTION.isClientSupernodeConnection();
    }
    
    boolean isLeaf() {
        return MCONNECTION.isSupernodeClientConnection();
    }
    
    boolean isConnecting() {
        return _isConnecting;
    }

    /**
     * Updates this connection from a 'connecting' to a 'connected' state.
     */
    public void update() {
        _isConnecting = false;

        boolean isOutgoing = MCONNECTION.isOutgoing();

        _status = isOutgoing ? OUTGOING_STRING : INCOMING_STRING;

        _host = MCONNECTION.getInetAddress().getHostAddress();

        // once it's connected, add it to the dictionary for host entry
        if ( isOutgoing )
            ConnectionMediator.instance().addKnownHost(
                _host, MCONNECTION.getPort()
            );

        _updated = true;
        _time = MCONNECTION.getConnectionTime();
    }
    
    /**
     * Returns whether or not this line is connected.
     */
    public boolean isConnected() {
        return !_isConnecting;
    }

    /**
     * Returns the ToolTip text for this DataLine.
     * Display some of the finer connection information.
     */
    public String[] getToolTipArray(int col) {
        Properties p = MCONNECTION.headers().props();

        String[] tips = new String[p != null ? p.size() + 2  : 1];

        if ( p == null ) {
            // for the lazy .4 connections (yes, some are still there)
            tips[0] = CONNECTED_ON + " " + GUIUtils.msec2DateTime(_time);
        }
        else {
            tips[0] = CONNECTED_ON + " " + GUIUtils.msec2DateTime(_time);
            tips[1] = "";

            String k;
            Enumeration ps = p.propertyNames();
            for(int i = 2 ; ps.hasMoreElements(); i++ ) {
                k = (String)ps.nextElement();
                tips[i] = k + ": " + p.getProperty(k);
            }
        }
        return tips;
    }
    
    /**
     * Assigns the host field to the line without holding an explicit
     * reference to it.
     */
    private static class HostAssigner implements Runnable {
        private final WeakReference line;
        
        HostAssigner(ConnectionDataLine cdl) {
            line = new WeakReference(cdl);
        }
        
        public void run() {
            ConnectionDataLine cdl = (ConnectionDataLine)line.get();
            if(cdl != null) {
                try {
    				cdl._host = InetAddress.getByName(cdl._host).getHostName();
    			    ConnectionDataLine._hostChanged = true;
    		    } catch (UnknownHostException ignored) {}
            }
        }
    }
}
