package com.limegroup.gnutella.gui.statistics;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.statistics.panes.*;

/**
 * This class constructs all of the elements of the statistics window.
 * To add a new statistic, this class should be used.
 * This class allows for statistics to be added to already existing panes as
 * well as for statistics to be added to new panes that you can also add here.
 * To add a new top-level pane, create a new <tt>StatisticsPaneImpl</tt> and
 * call the addStatisticPane method. To add statistic items to that pane, add
 * subclasses of <tt>AbstractPaneItem</tt>.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class StatisticsConstructor {
	
	/**
	 * Handle to the top-level <tt>JDialog</tt window that contains all
	 * of the other GUI components.
	 */
	private static final JDialog DIALOG =
		new JDialog(GUIMediator.getAppFrame(),
					GUIMediator.getStringResource("STATS_TITLE"),
					false);
	
	/**
	 * Constant for the default width of the statistics window.
	 */
	private static final int STATISTICS_WIDTH = 660;
	
	/**
	 * Constant for the default height of the statistics window.
	 */
	private static final int STATISTICS_HEIGHT = 460;
	
	/**
	 * Stored for convenience to allow using this in helper methods
	 * during construction.
	 */
	private static StatisticsTreeManager _treeManager;
	
	/**
	 * Stored for convenience to allow using this in helper methods
	 * during construction.
	 */
	private static StatisticsPaneManager _paneManager;
	
	private static final String ADVANCED_KEY = "STATS_ADVANCED_PANE_TITLE";

	private static final JPanel MAIN_PANEL = new PaddedPanel();
	
	private static final JSplitPane SPLIT_PANE = 
		new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	/**
	 * The constructor create all of the statistics windows and their
	 * components.
	 *
	 * @param treeManager the <tt>StatisticsTreeManager</tt> instance to
	 *					  use for constructing the main panels and
	 *					  adding elements
	 * @param paneManager the <tt>StatisticsPaneManager</tt> instance to
	 *					  use for constructing the main panels and
	 *					  adding elements
	 */
	public StatisticsConstructor(final StatisticsTreeManager treeManager,
								 final StatisticsPaneManager paneManager) {
		_treeManager = treeManager;
		_paneManager = paneManager;
		
		DIALOG.setSize(STATISTICS_WIDTH, STATISTICS_HEIGHT);
				
		final Box splitBox = new Box(BoxLayout.X_AXIS);
		final Component treeComponent = _treeManager.getComponent();
		final Component paneComponent = _paneManager.getComponent();
		SPLIT_PANE.setLeftComponent(treeComponent);
		SPLIT_PANE.setRightComponent(paneComponent);
		splitBox.add(SPLIT_PANE);
        SPLIT_PANE.setBorder(BorderFactory.createEmptyBorder());		
		
		MAIN_PANEL.add(splitBox);
		MAIN_PANEL.add(Box.createVerticalStrut(17));
		MAIN_PANEL.add(new StatisticsButtonPanel().getComponent());
		
		DIALOG.getContentPane().add(MAIN_PANEL);
		DIALOG.addComponentListener(new PaneAdapter());
		
		///////////// BANDWIDTH //////////////
		final String BANDWIDTH_KEY = "STATS_BANDWIDTH_PANE_TITLE";
		final StatisticsPane bandwidthPane = new VisibleStatsPane(BANDWIDTH_KEY);
		bandwidthPane.add(new BandwidthStats());
		addGroupTreeNode(StatisticsMediator.ROOT_NODE_KEY, bandwidthPane);
		
		// add upstream bandwidth stats
		final String UPSTREAM_BANDWIDTH_KEY =
			"TOTAL_UPSTREAM_BANDWIDTH";
		addStatisticPane(BANDWIDTH_KEY, UPSTREAM_BANDWIDTH_KEY,
						 new TotalUpstreamBandwidth(UPSTREAM_BANDWIDTH_KEY));
		
		// add downstream bandwidth stats
		final String DOWNSTREAM_BANDWIDTH_KEY =
			"TOTAL_DOWNSTREAM_BANDWIDTH";
		addStatisticPane(BANDWIDTH_KEY, DOWNSTREAM_BANDWIDTH_KEY,
						 new TotalDownstreamBandwidth(DOWNSTREAM_BANDWIDTH_KEY));
		
		///////////// ADVANCED //////////////
		final StatisticsPane advancedPane = new VisibleStatsPane(ADVANCED_KEY);
		advancedPane.add(new AdvancedStats());
		addGroupTreeNode(StatisticsMediator.ROOT_NODE_KEY, advancedPane);
		
		_treeManager.getTree().setSelectionRow(0);
	}
	
	/**
	 * Sets whether or no the advanced statistics panels are visible.
	 *
	 * @param visible the visibility state to set
	 */
	static void setAdvancedStatsVisible(boolean visible) {
		if (visible) {
			_treeManager.advancedSize();
			SPLIT_PANE.setDividerLocation(200);
			makeStatsVisible();
			DIALOG.setSize(STATISTICS_WIDTH+75, STATISTICS_HEIGHT);
		} else {
			_treeManager.defaultSize();
			SPLIT_PANE.setDividerLocation(125);
			_treeManager.removeAllChildren(ADVANCED_KEY);
			DIALOG.setSize(STATISTICS_WIDTH, STATISTICS_HEIGHT);
		}
	}
	
	/**
	 * Makes the advanced statistics visible.
	 */
	private static void makeStatsVisible() {
		// key for gnutella messages
		final String GNUTELLA_KEY = "GNUTELLA_MESSAGES";
		addGroupTreeNode(ADVANCED_KEY, GNUTELLA_KEY);
		
		addReceivedStats(GNUTELLA_KEY);
		addSentStats(GNUTELLA_KEY);
		addFlowControlStats(GNUTELLA_KEY);
		addCompressionStats(GNUTELLA_KEY);
		addHandshakingStats(GNUTELLA_KEY);
		addQRPStats(GNUTELLA_KEY);
		addErrorStats(GNUTELLA_KEY);

		// pane that displays OOB Throughput
		final String OOB_THROUGHPUT_KEY = "OOB_THROUGHPUT";
		addStatisticPane(GNUTELLA_KEY, OOB_THROUGHPUT_KEY,
			new OutOfBandThroughputPaneItem(OOB_THROUGHPUT_KEY));

		// pane that displays all route errors
		final String ROUTE_ERRORS_KEY = "ROUTE_ERRORS";
		addStatisticPane(GNUTELLA_KEY, ROUTE_ERRORS_KEY,
			new RouteErrorsPaneItem(ROUTE_ERRORS_KEY));

		// pane that displays all query reply route errors
		final String QUERY_REPLY_ROUTE_ERRORS_KEY = 
			"QUERY_REPLY_ROUTE_ERRORS";
		addStatisticPane(GNUTELLA_KEY, QUERY_REPLY_ROUTE_ERRORS_KEY,
			new QueryReplyRouteErrors(QUERY_REPLY_ROUTE_ERRORS_KEY));		

		// pane that displays all filtered messages
		final String FILTERED_MESSAGES_KEY = "FILTERED_MESSAGES";
		addStatisticPane(GNUTELLA_KEY, FILTERED_MESSAGES_KEY,
			new FilteredMessagesPaneItem(FILTERED_MESSAGES_KEY));
		
		final String CONNECTION_ATTEMPTS_KEY = "TOTAL_CONNECTION_ATTEMPTS";
		addStatisticPane(GNUTELLA_KEY, CONNECTION_ATTEMPTS_KEY,
			new ConnectionAttemptsPaneItem(CONNECTION_ATTEMPTS_KEY));
		
		//////////// END GNUTELLA MESSAGES /////////////
		
		final String HTTP_REQUESTS_KEY = "TOTAL_HTTP_REQUESTS";
		addStatisticPane(ADVANCED_KEY, HTTP_REQUESTS_KEY,
			new HTTPRequestsPaneItem(HTTP_REQUESTS_KEY));
			
		addDownloadStats(ADVANCED_KEY);	
		addUploadStats(ADVANCED_KEY);
	}
	
	/**
	 * Adds all download stats.
	 */
	private static void addDownloadStats(final String ADVANCED_KEY) {
	    // create main 'Download' group
	    final String DOWNLOAD_GROUP_KEY = "DOWNLOAD_STATISTICS";
	    addGroupTreeNode(ADVANCED_KEY, DOWNLOAD_GROUP_KEY);

	    // create subgroup Download->Connections
	    final String CONNECTIONS_KEY = "DOWNLOAD_CONNECTIONS";
        addStatisticPane(DOWNLOAD_GROUP_KEY, CONNECTIONS_KEY,
            new DownloadConnections(CONNECTIONS_KEY));

        // create subgroup Download->Responses
        final String RESPONSES_KEY = "DOWNLOAD_RESPONSES";
        addStatisticPane(DOWNLOAD_GROUP_KEY, RESPONSES_KEY,
            new DownloadResponses(RESPONSES_KEY));

        //create subgroup Download->Alternate Locations
        final String ALTERNATE_KEY = "DOWNLOAD_ALTERNATE";
        addStatisticPane(DOWNLOAD_GROUP_KEY, ALTERNATE_KEY,
            new DownloadAlternateLocations(ALTERNATE_KEY));

        // create subgroup Download->Transfers
        final String TRANFERS_KEY = "DOWNLOAD_TRANFERS";
        addStatisticPane(DOWNLOAD_GROUP_KEY, TRANFERS_KEY,
            new DownloadTransfers(TRANFERS_KEY));

        // create stat for TCP connection times on downloads
        final String TCP_CONNECT_TIME_KEY = "DOWNLOAD_TCP_CONNECT_TIME";
        addStatisticPane(DOWNLOAD_GROUP_KEY, TCP_CONNECT_TIME_KEY,
            new DownloadTCPConnectTime(TCP_CONNECT_TIME_KEY));
	}

	
	/**
	 * Adds all upload stats.
	 */
	private static void addUploadStats(final String ADVANCED_KEY) {
	    // create main 'Upload' group
	    final String UPLOAD_GROUP_KEY = "UPLOAD_STATISTICS";
	    addGroupTreeNode(ADVANCED_KEY, UPLOAD_GROUP_KEY);

	    // create subgroup Upload->Requests
	    final String REQUESTS_KEY = "UPLOAD_REQUESTS";
        addStatisticPane(UPLOAD_GROUP_KEY, REQUESTS_KEY,
            new UploadRequests(REQUESTS_KEY));

        // create subgroup Upload->Responses
        final String RESPONSES_KEY = "UPLOAD_RESPONSES";
        addStatisticPane(UPLOAD_GROUP_KEY, RESPONSES_KEY,
            new UploadResponses(RESPONSES_KEY));

        //create subgroup Upload->Alternate Locations
        //final String ALTERNATE_KEY = "UPLOAD_ALTERNATE";
        //addStatisticPane(UPLOAD_GROUP_KEY, ALTERNATE_KEY,
        //    new DownloadAlternateLocations(ALTERNATE_KEY));

        // create subgroup Upload->Request Methods
        final String METHODS_KEY = "UPLOAD_METHODS";
        addStatisticPane(UPLOAD_GROUP_KEY, METHODS_KEY,
            new UploadRequestMethods(METHODS_KEY));

        // create subgroup Upload->General
        final String GENERAL_KEY = "UPLOAD_GENERAL";
        addStatisticPane(UPLOAD_GROUP_KEY, GENERAL_KEY,
            new UploadGeneral(GENERAL_KEY));
	}
	
	/**
	 * Adds all handshaking statistics.
	 */
	private static void addHandshakingStats(final String GNUTELLA_KEY) {
	    final String GROUP_KEY = "HANDSHAKING";
	    addGroupTreeNode(GNUTELLA_KEY, GROUP_KEY);
	    
	    //pane that displays all leaf handshake stats
	    final String LEAF_KEY = "HANDSHAKING_LEAF";
	    addStatisticPane(GROUP_KEY, LEAF_KEY,
	        new HandshakingLeaf(LEAF_KEY));

	    //pane that displays all ultrapeer handshake stats
	    final String UP_KEY = "HANDSHAKING_ULTRAPEER";
	    addStatisticPane(GROUP_KEY, UP_KEY,
	        new HandshakingUltrapeer(UP_KEY));

        //pane that displays all outgoing handshake stats
        final String OUT_KEY = "HANDSHAKING_OUTGOING";
        addStatisticPane(GROUP_KEY, OUT_KEY, new OutgoingHandshaking(OUT_KEY));
        
        //pane that displays all incoming handshake stats
        final String IN_KEY = "HANDSHAKING_INCOMING";
        addStatisticPane(GROUP_KEY, IN_KEY, new IncomingHandshaking(IN_KEY));
        
        //pane that displays outgoing server rejection handshaking stats
        final String SERVER_REJECT_KEY = "HANDSHAKING_OUTGOING_SERVER_REJECT";
        addStatisticPane(GROUP_KEY, SERVER_REJECT_KEY, 
            new OutgoingServerReject(SERVER_REJECT_KEY));
    }	  	
	
	/**
	 * Adds all compression statistics.
	 */
	private static void addCompressionStats(final String GNUTELLA_KEY) {
	    final String COMPRESSION_GROUP_KEY = "COMPRESSABLE_MESSAGES";
	    addGroupTreeNode(GNUTELLA_KEY, COMPRESSION_GROUP_KEY);
	    
	    //pane that displays all upstream compressable data
	    final String COMPRESSABLE_UPSTREAM_KEY = "COMPRESSABLE_UPSTREAM";
	    addStatisticPane(COMPRESSION_GROUP_KEY, COMPRESSABLE_UPSTREAM_KEY,
	        new CompressableUpstreamBandwidth(COMPRESSABLE_UPSTREAM_KEY));

	    //pane that displays all downstream compressable data
	    final String COMPRESSABLE_DOWNSTREAM_KEY = "COMPRESSABLE_DOWNSTREAM";
	    addStatisticPane(COMPRESSION_GROUP_KEY, COMPRESSABLE_DOWNSTREAM_KEY,
	        new CompressableDownstreamBandwidth(COMPRESSABLE_DOWNSTREAM_KEY));
    }	        
	
	/**
	 * Adds all Gnutella message statistics for flow control.
	 */
	private static void addFlowControlStats(final String GNUTELLA_KEY) {
		final String FLOW_CONTROL_GROUP_KEY = "FLOW_CONTROLLED_MESSAGES";
		addGroupTreeNode(GNUTELLA_KEY, FLOW_CONTROL_GROUP_KEY);

		// pane that displays all flow control data
		final String FLOW_CONTROL_KEY = "FLOW_CONTROL"; 
		addStatisticPane(FLOW_CONTROL_GROUP_KEY, FLOW_CONTROL_KEY,
			new FlowControl(FLOW_CONTROL_KEY));

		// pane that displays all flow control data in bytes
		final String FLOW_CONTROL_BYTES_KEY = "FLOW_CONTROL_BYTES"; 
		addStatisticPane(FLOW_CONTROL_GROUP_KEY, FLOW_CONTROL_BYTES_KEY,
			new FlowControlBytes(FLOW_CONTROL_BYTES_KEY));
	}

	/**
	 * Adds all Gnutella message statistics for received messages.
	 */
	private static void addReceivedStats(final String GNUTELLA_KEY) {
		final String RECEIVED_KEY = "RECEIVED_MESSAGES";
		addGroupTreeNode(GNUTELLA_KEY, RECEIVED_KEY);
		
		addReceivedNumberStats(RECEIVED_KEY);
		addReceivedBytesStats(RECEIVED_KEY);
		addReceivedAverageMessageSizeStats(RECEIVED_KEY);
		addReceivedLimeStats(RECEIVED_KEY);
	}
	
	/**
	 * Adds all Gnutella message statistics for sent messages.
	 */
	private static void addSentStats(final String GNUTELLA_KEY) {
		final String SENT_KEY = "SENT_MESSAGES";
		addGroupTreeNode(GNUTELLA_KEY, SENT_KEY);
		
		addSentNumberStats(SENT_KEY);
		addSentBytesStats(SENT_KEY);
		addSentLimeStats(SENT_KEY);
	}
	
	/**
	 * Adds all Gnutella message statistics for QRP.
	 */
	private static void addQRPStats(final String GNUTELLA_KEY) {
	    final String QRP_GROUP_KEY = "QRP_MESSAGES";
	    addGroupTreeNode(GNUTELLA_KEY, QRP_GROUP_KEY);
	    
		addUltrapeerQRPStats(QRP_GROUP_KEY);
		addLeafQRPStats(QRP_GROUP_KEY);
    }
    
    /**
	 * Adds all Gnutella message statistics for errors.
	 */
	private static void addErrorStats(final String GNUTELLA_KEY) {
	    final String ERROR_GROUP_KEY = "ERROR_MESSAGES";
	    addGroupTreeNode(GNUTELLA_KEY, ERROR_GROUP_KEY);
	    
	    final String GENERIC_KEY = "ERROR_GENERIC";
	    addStatisticPane(ERROR_GROUP_KEY, GENERIC_KEY,
	        new ErrorGeneric(GENERIC_KEY));
	        
        final String QUERY_KEY = "ERROR_QUERIES";
        addStatisticPane(ERROR_GROUP_KEY, QUERY_KEY,
            new ErrorQueries(QUERY_KEY));
            
        final String QUERY_REPLY_KEY = "ERROR_QUERY_REPLIES";
        addStatisticPane(ERROR_GROUP_KEY, QUERY_REPLY_KEY,
            new ErrorQueryReplies(QUERY_REPLY_KEY));
            
        final String PING_REPLY_KEY = "ERROR_PING_REPLY";
        addStatisticPane(ERROR_GROUP_KEY, PING_REPLY_KEY,
            new ErrorPingReplies(PING_REPLY_KEY));
            
        final String PUSH_KEY = "ERROR_PUSHES";
        addStatisticPane(ERROR_GROUP_KEY, PUSH_KEY,
            new ErrorPushes(PUSH_KEY));
            
        final String VENDOR_KEY = "ERROR_VENDOR";
        addStatisticPane(ERROR_GROUP_KEY, VENDOR_KEY,
            new ErrorVendorMessages(VENDOR_KEY));
    }
	
	/**
	 * Adds received Gnutella messages statistics, in number of messages.
	 */
	private static void addReceivedNumberStats(final String RECEIVED_KEY) {
		final String NUMBER_KEY = "RECEIVED_MESSAGE_NUMBER";
		addGroupTreeNode(RECEIVED_KEY, NUMBER_KEY);
		
		// pane for all received messages
		final String TOTAL_RECEIVED_KEY =
			"TOTAL_RECEIVED_MESSAGES";
		addStatisticPane(NUMBER_KEY, TOTAL_RECEIVED_KEY,
			new TotalReceivedMessages(TOTAL_RECEIVED_KEY));
		
		// pane for all received TCP messages
		final String RECEIVED_TCP_KEY =
			"TOTAL_RECEIVED_TCP_MESSAGES";
		addStatisticPane(NUMBER_KEY, RECEIVED_TCP_KEY,
			new TCPMessagesReceived(RECEIVED_TCP_KEY));
		
		// pane for all received UPD messages
		final String RECEIVED_UDP_KEY =
			"TOTAL_RECEIVED_UDP_MESSAGES";
		addStatisticPane(NUMBER_KEY, RECEIVED_UDP_KEY,
			new UDPMessagesReceived(RECEIVED_UDP_KEY));
		
		// pane for all received MULTICAST messages
		final String RECEIVED_MULTICAST_KEY =
			"TOTAL_RECEIVED_MULTICAST_MESSAGES";
		addStatisticPane(NUMBER_KEY, RECEIVED_MULTICAST_KEY,
			new MulticastMessagesReceived(RECEIVED_MULTICAST_KEY));

		// pane that displays all aggregate received TCP, UDP & Multicast messages
		final String RECEIVED_ALL_KEY =
			"RECEIVED_ALL";
		addStatisticPane(NUMBER_KEY, RECEIVED_ALL_KEY,
			new AllMessagesReceived(RECEIVED_ALL_KEY));
		

		// add the query group
		final String QUERIES_KEY = "RECEIVED_QUERY_REQUESTS";
		addGroupTreeNode(NUMBER_KEY, QUERIES_KEY);
		// query hops
		final String QUERIES_HOPS_KEY = "QUERY_REQUEST_HOPS";
		addStatisticPane(QUERIES_KEY, QUERIES_HOPS_KEY,
			new QueryHops(QUERIES_HOPS_KEY));
		// query ttl
		final String QUERIES_TTL_KEY = "QUERY_REQUEST_TTL";
		addStatisticPane(QUERIES_KEY, QUERIES_TTL_KEY,
			new QueryTTL(QUERIES_TTL_KEY));

		// pane for all received duplicate TCP queries
		final String DUPLICATE_QUERIES_TCP_KEY =
			"DUPLICATE_RECEIVED_TCP_QUERIES";
		addStatisticPane(QUERIES_KEY, DUPLICATE_QUERIES_TCP_KEY,
			new TCPDuplicateQueries(DUPLICATE_QUERIES_TCP_KEY));

		// pane for all received duplicate Multicast queries
		final String DUPLICATE_QUERIES_MULTICAST_KEY =
			"DUPLICATE_RECEIVED_MULTICAST_QUERIES";
		addStatisticPane(QUERIES_KEY, DUPLICATE_QUERIES_MULTICAST_KEY,
			new MulticastDuplicateQueries(DUPLICATE_QUERIES_MULTICAST_KEY));

        // pane for all received special queries
        final String SPECIAL_QUERIES_KEY = "SPECIAL_QUERIES";
		addStatisticPane(QUERIES_KEY, SPECIAL_QUERIES_KEY,
                         new SpecialQuery(SPECIAL_QUERIES_KEY));
		

		// add the query hit group
		final String HITS_KEY = "RECEIVED_QUERY_REPLIES";
		addGroupTreeNode(NUMBER_KEY, HITS_KEY);
		// query hit hops
		final String HITS_HOPS_KEY = "QUERY_REPLY_HOPS";
		addStatisticPane(HITS_KEY, HITS_HOPS_KEY,
			new HitHops(HITS_HOPS_KEY));
		// query hit ttl
		final String HITS_TTL_KEY = "QUERY_REPLY_TTL";
		addStatisticPane(HITS_KEY, HITS_TTL_KEY,
			new HitTTL(HITS_TTL_KEY));
	}
	
	/**
	 * Adds received Gnutella messages statistics, in total bytes.
	 */
	private static void addReceivedBytesStats(final String RECEIVED_KEY) {
		final String BYTES_KEY = "RECEIVED_MESSAGE_BYTES";
		addGroupTreeNode(RECEIVED_KEY, BYTES_KEY);
		
		// pane for bytes from all received messages
		final String TOTAL_RECEIVED_BYTES_KEY =
			"TOTAL_RECEIVED_BYTES_MESSAGES";
		addStatisticPane(BYTES_KEY, TOTAL_RECEIVED_BYTES_KEY,
			new TotalReceivedBytes(TOTAL_RECEIVED_BYTES_KEY));
		
		// pane for bytes from all received TCP messages
		final String RECEIVED_BYTES_TCP_KEY =
			"TOTAL_RECEIVED_BYTES_TCP_MESSAGES";
		addStatisticPane(BYTES_KEY, RECEIVED_BYTES_TCP_KEY,
			new TCPBytesReceived(RECEIVED_BYTES_TCP_KEY));
		
		// pane for bytes from all received UPD messages
		final String RECEIVED_BYTES_UDP_KEY =
			"TOTAL_RECEIVED_BYTES_UDP_MESSAGES";
		addStatisticPane(BYTES_KEY, RECEIVED_BYTES_UDP_KEY,
			new UDPBytesReceived(RECEIVED_BYTES_UDP_KEY));

		// pane for bytes from all received Multicast messages
		final String RECEIVED_BYTES_MULTICAST_KEY =
			"TOTAL_RECEIVED_BYTES_MULTICAST_MESSAGES";
		addStatisticPane(BYTES_KEY, RECEIVED_BYTES_MULTICAST_KEY,
			new MulticastBytesReceived(RECEIVED_BYTES_MULTICAST_KEY));
		
		// pane that displays all aggregate bytes received from TCP and
		// UDP messages
		final String RECEIVED_BYTES_ALL_KEY =
			"RECEIVED_BYTES_ALL";
		addStatisticPane(BYTES_KEY, RECEIVED_BYTES_ALL_KEY,
			new AllBytesReceived(RECEIVED_BYTES_ALL_KEY));
		
		// add the query group
		final String QUERIES_KEY = "RECEIVED_QUERY_REQUESTS_BYTES";
		addGroupTreeNode(BYTES_KEY, QUERIES_KEY);
		// query hops
		final String QUERIES_HOPS_BYTES_KEY = "QUERY_REQUEST_HOPS_BYTES";
		addStatisticPane(QUERIES_KEY, QUERIES_HOPS_BYTES_KEY,
			new QueryHopsBytes(QUERIES_HOPS_BYTES_KEY));
		// query ttl
		final String QUERIES_TTL_BYTES_KEY = "QUERY_REQUEST_TTL_BYTES";
		addStatisticPane(QUERIES_KEY, QUERIES_TTL_BYTES_KEY,
			new QueryTTLBytes(QUERIES_TTL_BYTES_KEY));

		// pane for all received duplicate TCP queries bytes
		final String DUPLICATE_QUERIES_BYTES_TCP_KEY =
			"DUPLICATE_RECEIVED_BYTES_TCP_QUERIES";
		addStatisticPane(QUERIES_KEY, DUPLICATE_QUERIES_BYTES_TCP_KEY,
			new TCPDuplicateQueriesBytes(DUPLICATE_QUERIES_BYTES_TCP_KEY));

		// pane for all received duplicate Multicast queries bytes
		final String DUPLICATE_QUERIES_BYTES_MULTICAST_KEY =
			"DUPLICATE_RECEIVED_BYTES_MULTICAST_QUERIES";
		addStatisticPane(QUERIES_KEY, DUPLICATE_QUERIES_BYTES_MULTICAST_KEY,
			new MulticastDuplicateQueriesBytes(DUPLICATE_QUERIES_BYTES_MULTICAST_KEY));			

		// add the query hit group
		final String HITS_KEY = "RECEIVED_QUERY_REPLIES_BYTES";
		addGroupTreeNode(BYTES_KEY, HITS_KEY);
		// query hit hops
		final String HITS_HOPS_BYTES_KEY = "QUERY_REPLY_HOPS_BYTES";
		addStatisticPane(HITS_KEY, HITS_HOPS_BYTES_KEY,
			new HitHopsBytes(HITS_HOPS_BYTES_KEY));
		// query hit ttl
		final String HITS_TTL_BYTES_KEY = "QUERY_REPLY_TTL_BYTES";
		addStatisticPane(HITS_KEY, HITS_TTL_BYTES_KEY,
			new HitTTLBytes(HITS_TTL_BYTES_KEY));
	}
	
	/**
	 * Adds LimeWire-specific statistics for received messages.
	 */
	private static void addReceivedLimeStats(final String RECEIVED_KEY) {
		final String LIMEWIRE_RECEIVED_KEY = "RECEIVED_LIMEWIRE";
		addGroupTreeNode(RECEIVED_KEY, LIMEWIRE_RECEIVED_KEY);
		
		final String LIME_NUMBER_KEY = "LIME_RECEIVED_MESSAGE_NUMBER";
		addGroupTreeNode(LIMEWIRE_RECEIVED_KEY, LIME_NUMBER_KEY);
		addReceivedLimeNumberStatsTCP(LIME_NUMBER_KEY);
		addReceivedLimeNumberStatsUDP(LIME_NUMBER_KEY);
		addReceivedLimeNumberStatsMulticast(LIME_NUMBER_KEY);
		
		final String LIME_BYTES_KEY = "LIME_RECEIVED_MESSAGE_BYTES";
		addGroupTreeNode(LIMEWIRE_RECEIVED_KEY, LIME_BYTES_KEY);
		addReceivedLimeBytesStatsTCP(LIME_BYTES_KEY);
		addReceivedLimeBytesStatsUDP(LIME_BYTES_KEY);
		addReceivedLimeBytesStatsMulticast(LIME_BYTES_KEY);

		addReceivedAverageLimeMessageSizeStats(LIMEWIRE_RECEIVED_KEY);
	}
	
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of messages.
	 */
	private static void addReceivedLimeNumberStatsTCP(
			final String LIME_NUMBER_KEY) {
		final String TCP_KEY = "STATS_RECEIVED_TCP";
		addGroupTreeNode(LIME_NUMBER_KEY, TCP_KEY);
		
		final String LIME_RECEIVED_TCP_MESSAGES_KEY =
			"LIME_RECEIVED_TCP_MESSAGES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_MESSAGES_KEY,
			new TCPLimeReceivedMessages(LIME_RECEIVED_TCP_MESSAGES_KEY));
		
		final String LIME_RECEIVED_TCP_PINGS_KEY =
			"LIME_RECEIVED_TCP_PING_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PINGS_KEY,
			new TCPLimeReceivedPings(LIME_RECEIVED_TCP_PINGS_KEY));
		
		final String LIME_RECEIVED_TCP_PONGS_KEY =
			"LIME_RECEIVED_TCP_PING_REPLIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PONGS_KEY,
			new TCPLimeReceivedPongs(LIME_RECEIVED_TCP_PONGS_KEY));
		
		final String LIME_RECEIVED_TCP_QUERIES_KEY =
			"LIME_RECEIVED_TCP_QUERIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_QUERIES_KEY,
			new TCPLimeReceivedQueries(LIME_RECEIVED_TCP_QUERIES_KEY));
		
		final String LIME_RECEIVED_TCP_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_TCP_QUERY_REPLIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_QUERY_REPLIES_KEY,
			new TCPLimeReceivedQueryReplies(LIME_RECEIVED_TCP_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_TCP_PUSH_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY,
			new TCPLimeReceivedPushRequests(LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of messages.
	 */
	private static void addReceivedLimeNumberStatsUDP(
			final String LIME_NUMBER_KEY) {
		final String UDP_KEY = "STATS_RECEIVED_UDP";
		addGroupTreeNode(LIME_NUMBER_KEY, UDP_KEY);
		
		final String LIME_RECEIVED_UDP_MESSAGES_KEY =
			"LIME_RECEIVED_UDP_MESSAGES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_MESSAGES_KEY,
			new UDPLimeReceivedMessages(LIME_RECEIVED_UDP_MESSAGES_KEY));
		
		final String LIME_RECEIVED_UDP_PINGS_KEY =
			"LIME_RECEIVED_UDP_PING_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PINGS_KEY,
			new UDPLimeReceivedPings(LIME_RECEIVED_UDP_PINGS_KEY));
		
		final String LIME_RECEIVED_UDP_PONGS_KEY =
			"LIME_RECEIVED_UDP_PING_REPLIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PONGS_KEY,
			new UDPLimeReceivedPongs(LIME_RECEIVED_UDP_PONGS_KEY));
		
		final String LIME_RECEIVED_UDP_QUERIES_KEY =
			"LIME_RECEIVED_UDP_QUERIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_QUERIES_KEY,
			new UDPLimeReceivedQueries(LIME_RECEIVED_UDP_QUERIES_KEY));
		
		final String LIME_RECEIVED_UDP_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_UDP_QUERY_REPLIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_QUERY_REPLIES_KEY,
			new UDPLimeReceivedQueryReplies(LIME_RECEIVED_UDP_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_UDP_PUSH_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY,
			new UDPLimeReceivedPushRequests(LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of messages.
	 */
	private static void addReceivedLimeNumberStatsMulticast(
			final String LIME_NUMBER_KEY) {
		final String MULTICAST_KEY = "STATS_RECEIVED_MULTICAST";
		addGroupTreeNode(LIME_NUMBER_KEY, MULTICAST_KEY);
		
		final String LIME_RECEIVED_MULTICAST_MESSAGES_KEY =
			"LIME_RECEIVED_MULTICAST_MESSAGES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_MESSAGES_KEY,
			new MulticastLimeReceivedMessages(LIME_RECEIVED_MULTICAST_MESSAGES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PINGS_KEY =
			"LIME_RECEIVED_MULTICAST_PING_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PINGS_KEY,
			new MulticastLimeReceivedPings(LIME_RECEIVED_MULTICAST_PINGS_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PONGS_KEY =
			"LIME_RECEIVED_MULTICAST_PING_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PONGS_KEY,
			new MulticastLimeReceivedPongs(LIME_RECEIVED_MULTICAST_PONGS_KEY));
		
		final String LIME_RECEIVED_MULTICAST_QUERIES_KEY =
			"LIME_RECEIVED_MULTICAST_QUERIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_QUERIES_KEY,
			new MulticastLimeReceivedQueries(LIME_RECEIVED_MULTICAST_QUERIES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_MULTICAST_QUERY_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY,
			new MulticastLimeReceivedQueryReplies(LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_MULTICAST_PUSH_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY,
			new MulticastLimeReceivedPushRequests(LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY));
	}
			
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addReceivedLimeBytesStatsTCP(final String LIME_BYTES_KEY) {
		final String TCP_KEY = "STATS_RECEIVED_TCP_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, TCP_KEY);
		
		final String LIME_RECEIVED_TCP_MESSAGES_KEY =
			"LIME_RECEIVED_BYTES_TCP_MESSAGES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_MESSAGES_KEY,
			new TCPLimeReceivedBytes(LIME_RECEIVED_TCP_MESSAGES_KEY));
		
		final String LIME_RECEIVED_TCP_PINGS_KEY =
			"LIME_RECEIVED_BYTES_TCP_PING_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PINGS_KEY,
			new TCPLimeReceivedPingsBytes(LIME_RECEIVED_TCP_PINGS_KEY));
		
		final String LIME_RECEIVED_TCP_PONGS_KEY =
			"LIME_RECEIVED_BYTES_TCP_PING_REPLIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PONGS_KEY,
			new TCPLimeReceivedPongsBytes(LIME_RECEIVED_TCP_PONGS_KEY));
		
		final String LIME_RECEIVED_TCP_QUERIES_KEY =
			"LIME_RECEIVED_BYTES_TCP_QUERIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_QUERIES_KEY,
			new TCPLimeReceivedQueriesBytes(LIME_RECEIVED_TCP_QUERIES_KEY));
		
		final String LIME_RECEIVED_TCP_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_BYTES_TCP_QUERY_REPLIES";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_QUERY_REPLIES_KEY,
			new TCPLimeReceivedQueryRepliesBytes(LIME_RECEIVED_TCP_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_BYTES_TCP_PUSH_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY,
			new TCPLimeReceivedPushRequestsBytes(LIME_RECEIVED_TCP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addReceivedLimeBytesStatsUDP(
			final String LIME_BYTES_KEY) {
		final String UDP_KEY = "STATS_RECEIVED_UDP_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, UDP_KEY);
		
		final String LIME_RECEIVED_UDP_MESSAGES_KEY =
			"LIME_RECEIVED_BYTES_UDP_MESSAGES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_MESSAGES_KEY,
			new UDPLimeReceivedBytes(LIME_RECEIVED_UDP_MESSAGES_KEY));
		
		final String LIME_RECEIVED_UDP_PINGS_KEY =
			"LIME_RECEIVED_BYTES_UDP_PING_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PINGS_KEY,
			new UDPLimeReceivedPingsBytes(LIME_RECEIVED_UDP_PINGS_KEY));
		
		final String LIME_RECEIVED_UDP_PONGS_KEY =
			"LIME_RECEIVED_BYTES_UDP_PING_REPLIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PONGS_KEY,
			new UDPLimeReceivedPongsBytes(LIME_RECEIVED_UDP_PONGS_KEY));
		
		final String LIME_RECEIVED_UDP_QUERIES_KEY =
			"LIME_RECEIVED_BYTES_UDP_QUERIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_QUERIES_KEY,
			new UDPLimeReceivedQueriesBytes(LIME_RECEIVED_UDP_QUERIES_KEY));
		
		final String LIME_RECEIVED_UDP_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_BYTES_UDP_QUERY_REPLIES";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_QUERY_REPLIES_KEY,
			new UDPLimeReceivedQueryRepliesBytes(LIME_RECEIVED_UDP_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_BYTES_UDP_PUSH_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY,
			new UDPLimeReceivedPushRequestsBytes(LIME_RECEIVED_UDP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for received Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addReceivedLimeBytesStatsMulticast(
			final String LIME_BYTES_KEY) {
		final String MULTICAST_KEY = "STATS_RECEIVED_MULTICAST_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, MULTICAST_KEY);
		
		final String LIME_RECEIVED_MULTICAST_MESSAGES_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_MESSAGES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_MESSAGES_KEY,
			new MulticastLimeReceivedBytes(LIME_RECEIVED_MULTICAST_MESSAGES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PINGS_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_PING_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PINGS_KEY,
			new MulticastLimeReceivedPingsBytes(LIME_RECEIVED_MULTICAST_PINGS_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PONGS_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_PING_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PONGS_KEY,
			new MulticastLimeReceivedPongsBytes(LIME_RECEIVED_MULTICAST_PONGS_KEY));
		
		final String LIME_RECEIVED_MULTICAST_QUERIES_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_QUERIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_QUERIES_KEY,
			new MulticastLimeReceivedQueriesBytes(LIME_RECEIVED_MULTICAST_QUERIES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_QUERY_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY,
			new MulticastLimeReceivedQueryRepliesBytes(LIME_RECEIVED_MULTICAST_QUERY_REPLIES_KEY));
		
		final String LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY =
			"LIME_RECEIVED_BYTES_MULTICAST_PUSH_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY,
			new MulticastLimeReceivedPushRequestsBytes(LIME_RECEIVED_MULTICAST_PUSH_REQUESTS_KEY));
	}	

	/**
	 * Adds the average TCP received message size.
	 */
	private static void addReceivedAverageMessageSizeStats(final String receivedKey) {
		final String RECEIVED_AVERAGE_MESSAGE_SIZE =
			"RECEIVED_AVERAGE_MESSAGE_SIZE";
		addStatisticPane(receivedKey, RECEIVED_AVERAGE_MESSAGE_SIZE,
						 new TCPAverageMessageSize(RECEIVED_AVERAGE_MESSAGE_SIZE));
	}

	/**
	 * Adds the average TCP received message size for LimeWire messages.
	 */
	private static void addReceivedAverageLimeMessageSizeStats(final String receivedKey) {
		final String RECEIVED_AVERAGE_LIME_MESSAGE_SIZE =
			"RECEIVED_AVERAGE_LIME_MESSAGE_SIZE";
		addStatisticPane(receivedKey, RECEIVED_AVERAGE_LIME_MESSAGE_SIZE,
						 new TCPLimeAverageMessageSize(RECEIVED_AVERAGE_LIME_MESSAGE_SIZE));
	}
	
	/**
	 * Adds sent Gnutella message statistics, in number of messages.
	 */
	private static void addSentNumberStats(final String SENT_KEY) {
		// sent messages
		final String NUMBER_SENT_KEY = "SENT_MESSAGE_NUMBER";
		addGroupTreeNode(SENT_KEY, NUMBER_SENT_KEY);
		
		// pane for all sent messages
		final String TOTAL_SENT_KEY = "TOTAL_SENT_MESSAGES";
		addStatisticPane(NUMBER_SENT_KEY, TOTAL_SENT_KEY,
			new TotalSentMessages(TOTAL_SENT_KEY));
		
		// pane for all sent TCP messages
		final String SENT_TCP_KEY = "TOTAL_SENT_TCP_MESSAGES";
		addStatisticPane(NUMBER_SENT_KEY, SENT_TCP_KEY,
			new TCPMessagesSent(SENT_TCP_KEY));
		
		// pane for all sent UPD messages
		final String SENT_UDP_KEY = "TOTAL_SENT_UDP_MESSAGES";
		addStatisticPane(NUMBER_SENT_KEY, SENT_UDP_KEY,
			new UDPMessagesSent(SENT_UDP_KEY));
			
		// pane for all sent Multicast messages
		final String SENT_MULTICAST_KEY = "TOTAL_SENT_MULTICAST_MESSAGES";
		addStatisticPane(NUMBER_SENT_KEY, SENT_MULTICAST_KEY,
			new MulticastMessagesSent(SENT_MULTICAST_KEY));			
		
		// pane that displays all aggregate sent TCP, UDP & Multicast messages
		final String SENT_ALL_KEY = "SENT_ALL";
		addStatisticPane(NUMBER_SENT_KEY, SENT_ALL_KEY,
			new AllMessagesSent(SENT_ALL_KEY));
			
	}

	/**
	 * Adds stats for messages sent to Ultrapeers.
	 */
	private static void addUltrapeerQRPStats(final String QRP_KEY) {
		// stats for qrp-routed messages to Ultrapeers
		final String ULTRAPEER_KEY = "QRP_ULTRAPEER";
		addGroupTreeNode(QRP_KEY, ULTRAPEER_KEY);

		// stat for messages routed to other ultrapeers
		final String ROUTED_ULTRAPEER_QUERY_KEY = "QRP_ULTRAPEER_ROUTED_QUERIES_SENT";
		addStatisticPane(ULTRAPEER_KEY, ROUTED_ULTRAPEER_QUERY_KEY,
						 new RoutedOutgoingUltrapeerQueries(ROUTED_ULTRAPEER_QUERY_KEY));
	}

	/**
	 * Adds stats for messages sent to leaves.
	 */
	private static void addLeafQRPStats(final String QRP_KEY) {
		// stats for qrp-routed messages to leaves
		final String LEAF_KEY = "QRP_LEAF";
		addGroupTreeNode(QRP_KEY, LEAF_KEY);		

		// stat for messages routed to other leaves
		final String ROUTED_LEAF_SENT_QUERY_KEY = "QRP_LEAF_ROUTED_QUERIES_SENT";
		addStatisticPane(LEAF_KEY, ROUTED_LEAF_SENT_QUERY_KEY,
						 new RoutedOutgoingLeafQueries(ROUTED_LEAF_SENT_QUERY_KEY));
        // stat for messages received from routed queries
        final String ROUTED_LEAF_RECVD_QUERY_KEY = "QRP_LEAF_ROUTED_QUERIES_RECEIVED";
        addStatisticPane(LEAF_KEY, ROUTED_LEAF_RECVD_QUERY_KEY,
                        new RoutedIncomingLeafQueries(ROUTED_LEAF_RECVD_QUERY_KEY));
	}
	
	/**
	 * Adds sent Gnutella message statistics, in number of bytes.
	 */
	private static void addSentBytesStats(final String SENT_KEY) {
		// sent bytes
		final String BYTES_SENT_KEY = "SENT_MESSAGE_BYTES";
		addGroupTreeNode(SENT_KEY, BYTES_SENT_KEY);
		
		// pane for all bytes sent
		final String TOTAL_BYTES_SENT_KEY = "TOTAL_SENT_BYTES_MESSAGES";
		addStatisticPane(BYTES_SENT_KEY, TOTAL_BYTES_SENT_KEY,
			new TotalSentBytes(TOTAL_BYTES_SENT_KEY));
		
		// pane for all bytes sent through TCP messages
		final String SENT_BYTES_TCP_KEY = "TOTAL_SENT_BYTES_TCP_MESSAGES";
		addStatisticPane(BYTES_SENT_KEY, SENT_BYTES_TCP_KEY,
			new TCPBytesSent(SENT_BYTES_TCP_KEY));
		
		// pane for all bytes sent through UPD messages
		final String SENT_BYTES_UDP_KEY = "TOTAL_SENT_BYTES_UDP_MESSAGES";
		addStatisticPane(BYTES_SENT_KEY, SENT_BYTES_UDP_KEY,
			new UDPBytesSent(SENT_BYTES_UDP_KEY));
			
		// pane for all bytes sent through Multicast messages
		final String SENT_BYTES_MULTICAST_KEY = "TOTAL_SENT_BYTES_MULTICAST_MESSAGES";
		addStatisticPane(BYTES_SENT_KEY, SENT_BYTES_MULTICAST_KEY,
			new MulticastBytesSent(SENT_BYTES_MULTICAST_KEY));			
		
		// pane that displays all aggregate sent TCP and UDP message bytes
		final String SENT_BYTES_ALL_KEY = "SENT_BYTES_ALL";
		addStatisticPane(BYTES_SENT_KEY, SENT_BYTES_ALL_KEY,
			new AllBytesSent(SENT_BYTES_ALL_KEY));
	}
	
	/**
	 * Adds LimeWire-specific statistics for sent messages.
	 */
	private static void addSentLimeStats(final String SENT_KEY) {
		final String LIMEWIRE_SENT_KEY = "SENT_LIMEWIRE";
		addGroupTreeNode(SENT_KEY, LIMEWIRE_SENT_KEY);
		
		final String LIME_NUMBER_KEY = "LIME_SENT_MESSAGES_NUMBER";
		addGroupTreeNode(LIMEWIRE_SENT_KEY, LIME_NUMBER_KEY);
		addSentLimeNumberStatsTCP(LIME_NUMBER_KEY);
		addSentLimeNumberStatsUDP(LIME_NUMBER_KEY);
		addSentLimeNumberStatsMulticast(LIME_NUMBER_KEY);
		
		final String LIME_BYTES_KEY = "LIME_SENT_MESSAGES_BYTES";
		addGroupTreeNode(LIMEWIRE_SENT_KEY, LIME_BYTES_KEY);
		addSentLimeBytesStatsTCP(LIME_BYTES_KEY);
		addSentLimeBytesStatsUDP(LIME_BYTES_KEY);
		addSentLimeBytesStatsMulticast(LIME_BYTES_KEY);
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in kilobytes.
	 */
	private static void addSentLimeNumberStatsTCP(
			final String LIME_NUMBER_KEY) {
		final String TCP_KEY = "STATS_SENT_TCP";
		addGroupTreeNode(LIME_NUMBER_KEY, TCP_KEY);
		
		final String LIME_SENT_TCP_MESSAGES_KEY =
			"LIME_SENT_TCP_MESSAGES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_MESSAGES_KEY,
			new TCPLimeSentMessages(LIME_SENT_TCP_MESSAGES_KEY));
		
		final String LIME_SENT_TCP_PINGS_KEY =
			"LIME_SENT_TCP_PING_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PINGS_KEY,
			new TCPLimeSentPings(LIME_SENT_TCP_PINGS_KEY));
		
		final String LIME_SENT_TCP_PONGS_KEY =
			"LIME_SENT_TCP_PING_REPLIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PONGS_KEY,
			new TCPLimeSentPongs(LIME_SENT_TCP_PONGS_KEY));
		
		final String LIME_SENT_TCP_QUERIES_KEY =
			"LIME_SENT_TCP_QUERIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_QUERIES_KEY,
			new TCPLimeSentQueries(LIME_SENT_TCP_QUERIES_KEY));
		
		final String LIME_SENT_TCP_QUERY_REPLIES_KEY =
			"LIME_SENT_TCP_QUERY_REPLIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_QUERY_REPLIES_KEY,
			new TCPLimeSentQueryReplies(LIME_SENT_TCP_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_TCP_PUSH_REQUESTS_KEY =
			"LIME_SENT_TCP_PUSH_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PUSH_REQUESTS_KEY,
			new TCPLimeSentPushRequests(LIME_SENT_TCP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in kilobytes.
	 */
	private static void addSentLimeNumberStatsUDP(
			final String LIME_NUMBER_KEY) {
		final String UDP_KEY = "STATS_SENT_UDP";
		addGroupTreeNode(LIME_NUMBER_KEY, UDP_KEY);
		
		final String LIME_SENT_UDP_MESSAGES_KEY =
			"LIME_SENT_UDP_MESSAGES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_MESSAGES_KEY,
			new UDPLimeSentMessages(LIME_SENT_UDP_MESSAGES_KEY));
		
		final String LIME_SENT_UDP_PINGS_KEY =
			"LIME_SENT_UDP_PING_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PINGS_KEY,
			new UDPLimeSentPings(LIME_SENT_UDP_PINGS_KEY));
		
		final String LIME_SENT_UDP_PONGS_KEY =
			"LIME_SENT_UDP_PING_REPLIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PONGS_KEY,
			new UDPLimeSentPongs(LIME_SENT_UDP_PONGS_KEY));
		
		final String LIME_SENT_UDP_QUERIES_KEY =
			"LIME_SENT_UDP_QUERIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_QUERIES_KEY,
			new UDPLimeSentQueries(LIME_SENT_UDP_QUERIES_KEY));
		
		final String LIME_SENT_UDP_QUERY_REPLIES_KEY =
			"LIME_SENT_UDP_QUERY_REPLIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_QUERY_REPLIES_KEY,
			new UDPLimeSentQueryReplies(LIME_SENT_UDP_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_UDP_PUSH_REQUESTS_KEY =
			"LIME_SENT_UDP_PUSH_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PUSH_REQUESTS_KEY,
			new UDPLimeSentPushRequests(LIME_SENT_UDP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in kilobytes.
	 */
	private static void addSentLimeNumberStatsMulticast(
			final String LIME_NUMBER_KEY) {
		final String MULTICAST_KEY = "STATS_SENT_MULTICAST";
		addGroupTreeNode(LIME_NUMBER_KEY, MULTICAST_KEY);
		
		final String LIME_SENT_MULTICAST_MESSAGES_KEY =
			"LIME_SENT_MULTICAST_MESSAGES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_MESSAGES_KEY,
			new MulticastLimeSentMessages(LIME_SENT_MULTICAST_MESSAGES_KEY));
		
		final String LIME_SENT_MULTICAST_PINGS_KEY =
			"LIME_SENT_MULTICAST_PING_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PINGS_KEY,
			new MulticastLimeSentPings(LIME_SENT_MULTICAST_PINGS_KEY));
		
		final String LIME_SENT_MULTICAST_PONGS_KEY =
			"LIME_SENT_MULTICAST_PING_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PONGS_KEY,
			new MulticastLimeSentPongs(LIME_SENT_MULTICAST_PONGS_KEY));
		
		final String LIME_SENT_MULTICAST_QUERIES_KEY =
			"LIME_SENT_MULTICAST_QUERIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_QUERIES_KEY,
			new MulticastLimeSentQueries(LIME_SENT_MULTICAST_QUERIES_KEY));
		
		final String LIME_SENT_MULTICAST_QUERY_REPLIES_KEY =
			"LIME_SENT_MULTICAST_QUERY_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_QUERY_REPLIES_KEY,
			new MulticastLimeSentQueryReplies(LIME_SENT_MULTICAST_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY =
			"LIME_SENT_MULTICAST_PUSH_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY,
			new MulticastLimeSentPushRequests(LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addSentLimeBytesStatsTCP(
			final String LIME_BYTES_KEY) {
		final String TCP_KEY = "STATS_SENT_TCP_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, TCP_KEY);
		
		final String LIME_SENT_TCP_MESSAGES_KEY =
			"LIME_SENT_BYTES_TCP_MESSAGES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_MESSAGES_KEY,
			new TCPLimeSentBytes(LIME_SENT_TCP_MESSAGES_KEY));
		
		final String LIME_SENT_TCP_PINGS_KEY =
			"LIME_SENT_BYTES_TCP_PING_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PINGS_KEY,
			new TCPLimeSentPingsBytes(LIME_SENT_TCP_PINGS_KEY));
		
		final String LIME_SENT_TCP_PONGS_KEY =
			"LIME_SENT_BYTES_TCP_PING_REPLIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PONGS_KEY,
			new TCPLimeSentPongsBytes(LIME_SENT_TCP_PONGS_KEY));
		
		final String LIME_SENT_TCP_QUERIES_KEY =
			"LIME_SENT_BYTES_TCP_QUERIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_QUERIES_KEY,
			new TCPLimeSentQueriesBytes(LIME_SENT_TCP_QUERIES_KEY));
		
		final String LIME_SENT_TCP_QUERY_REPLIES_KEY =
			"LIME_SENT_BYTES_TCP_QUERY_REPLIES";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_QUERY_REPLIES_KEY,
			new TCPLimeSentQueryRepliesBytes(LIME_SENT_TCP_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_TCP_PUSH_REQUESTS_KEY =
			"LIME_SENT_BYTES_TCP_PUSH_REQUESTS";
		addStatisticPane(TCP_KEY, LIME_SENT_TCP_PUSH_REQUESTS_KEY,
			new TCPLimeSentPushRequestsBytes(LIME_SENT_TCP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addSentLimeBytesStatsUDP(
			final String LIME_BYTES_KEY) {
		final String UDP_KEY = "STATS_SENT_UDP_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, UDP_KEY);
		
		final String LIME_SENT_UDP_MESSAGES_KEY =
			"LIME_SENT_BYTES_UDP_MESSAGES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_MESSAGES_KEY,
			new UDPLimeSentBytes(LIME_SENT_UDP_MESSAGES_KEY));
		
		final String LIME_SENT_UDP_PINGS_KEY =
			"LIME_SENT_BYTES_UDP_PING_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PINGS_KEY,
			new UDPLimeSentPingsBytes(LIME_SENT_UDP_PINGS_KEY));
		
		final String LIME_SENT_UDP_PONGS_KEY =
			"LIME_SENT_BYTES_UDP_PING_REPLIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PONGS_KEY,
			new UDPLimeSentPongsBytes(LIME_SENT_UDP_PONGS_KEY));
		
		final String LIME_SENT_UDP_QUERIES_KEY =
			"LIME_SENT_BYTES_UDP_QUERIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_QUERIES_KEY,
			new UDPLimeSentQueriesBytes(LIME_SENT_UDP_QUERIES_KEY));
		
		final String LIME_SENT_UDP_QUERY_REPLIES_KEY =
			"LIME_SENT_BYTES_UDP_QUERY_REPLIES";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_QUERY_REPLIES_KEY,
			new UDPLimeSentQueryRepliesBytes(LIME_SENT_UDP_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_UDP_PUSH_REQUESTS_KEY =
			"LIME_SENT_BYTES_UDP_PUSH_REQUESTS";
		addStatisticPane(UDP_KEY, LIME_SENT_UDP_PUSH_REQUESTS_KEY,
			new UDPLimeSentPushRequestsBytes(LIME_SENT_UDP_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the statistics for sent Gnutella messages from other
	 * LimeWires versus other Gnutella clients, in number of bytes.
	 */
	private static void addSentLimeBytesStatsMulticast(
			final String LIME_BYTES_KEY) {
		final String MULTICAST_KEY = "STATS_SENT_MULTICAST_BYTES";
		addGroupTreeNode(LIME_BYTES_KEY, MULTICAST_KEY);
		
		final String LIME_SENT_MULTICAST_MESSAGES_KEY =
			"LIME_SENT_BYTES_MULTICAST_MESSAGES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_MESSAGES_KEY,
			new MulticastLimeSentBytes(LIME_SENT_MULTICAST_MESSAGES_KEY));
		
		final String LIME_SENT_MULTICAST_PINGS_KEY =
			"LIME_SENT_BYTES_MULTICAST_PING_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PINGS_KEY,
			new MulticastLimeSentPingsBytes(LIME_SENT_MULTICAST_PINGS_KEY));
		
		final String LIME_SENT_MULTICAST_PONGS_KEY =
			"LIME_SENT_BYTES_MULTICAST_PING_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PONGS_KEY,
			new MulticastLimeSentPongsBytes(LIME_SENT_MULTICAST_PONGS_KEY));
		
		final String LIME_SENT_MULTICAST_QUERIES_KEY =
			"LIME_SENT_BYTES_MULTICAST_QUERIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_QUERIES_KEY,
			new MulticastLimeSentQueriesBytes(LIME_SENT_MULTICAST_QUERIES_KEY));
		
		final String LIME_SENT_MULTICAST_QUERY_REPLIES_KEY =
			"LIME_SENT_BYTES_MULTICAST_QUERY_REPLIES";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_QUERY_REPLIES_KEY,
			new MulticastLimeSentQueryRepliesBytes(LIME_SENT_MULTICAST_QUERY_REPLIES_KEY));
		
		final String LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY =
			"LIME_SENT_BYTES_MULTICAST_PUSH_REQUESTS";
		addStatisticPane(MULTICAST_KEY, LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY,
			new MulticastLimeSentPushRequestsBytes(LIME_SENT_MULTICAST_PUSH_REQUESTS_KEY));
	}
	
	/**
	 * Adds the specified statistic to the statistics displayed, within
	 * the specified group key.
	 */
	private static void addStatisticPane(final String parentKey,
										 final String key,
										 final PaneItem item) {
		final StatisticsPane pane =
			new StatisticsPaneImpl("STATS_" + key + "_TITLE");
		pane.add(item);
		_treeManager.addNode(parentKey, pane);
		_paneManager.addPane(pane);
	}
	
	/**
	 * Adds a parent node to the tree. This node serves navigational
	 * purposes only, as the corresponding <tt>StatisticsPane</tt> does
	 * not display any data. This method allows for multiple tiers of
	 * parent nodes, not only top-level parents.
	 *
	 * @param parentKey the key of the parent node to add this parent
	 *					node to
	 * @param childKey the key of the child node being added
	 */
	private static void addGroupTreeNode(final String parentKey,
										 final String childKey) {
		final StatisticsPane pane = new StatisticsPaneParent(childKey);
		_treeManager.addNode(parentKey, pane);
		_paneManager.addPane(pane);
	}
	
	/**
	 * Adds a parent node to the tree. This parent has the visible
	 * display of the <tt>StatisticsPane</tt> parameter.
	 * This method allows for multiple tiers of parent nodes, not only
	 * top-level parents.
	 *
	 * @param parentKey the key of the parent node to add this parent
	 *					node to
	 * @param pane the <tt>StatisticsPane</tt> instance containing the
	 *			   data for visual display
	 */
	private static void addGroupTreeNode(final String parentKey,
										 final StatisticsPane pane) {
		_treeManager.addNode(parentKey, pane);
		_paneManager.addPane(pane);
	}
	
	/**
	 * Makes the statistics window either visible or not visible depending on
	 * the boolean argument.
	 *
	 * @param visible <tt>boolean</tt> value specifying whether the statistics
	 *				  window should be made visible or not visible
	 */
	public void setStatisticsVisible(boolean visible) {
		if (visible == true) {
			DIALOG.setLocationRelativeTo(GUIMediator.getAppFrame());
			DIALOG.show();
		} else {
			DIALOG.dispose();
		}
	}
	
	/**
	 * Returns if the statistics window is visible.
	 *
	 * @return <tt>true</tt> if the statistics window is visible,
	 *		   <tt>false</tt> otherwise
	 */
	public boolean isStatisticsVisible() {
		return DIALOG.isVisible();
	}
	
	/**
	 * Returns the main <tt>Component</tt> instance for the statistics window,
	 * allowing other components to position themselves accordingly.
	 *
	 * @return the main statistics <tt>Component</tt> window
	 */
	public static Component getMainComponent() {
		return DIALOG;
	}

	/**
	 * Returns the main <tt>Component</tt> instance for the statistics window,
	 * allowing other components to position themselves accordingly.
	 *
	 * @return the main statistics <tt>Component</tt> window
	 */
	public static JComponent getComponent() {
		return MAIN_PANEL;
	}	

    /**
     * Accessor for the component that contains the displayed statistics,
     * as opposed to the navigational component.
     *
     * @return the component that contains the displayed statistics,
     *  as opposed to the navigational component
     */
    public static JComponent getStatDisplayComponent() {
        return _paneManager.getFirstDisplayedPaneItem().getStatsComponent();
    }

    private class PaneAdapter extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            _paneManager.componentResized(e);
        }
    }
}

