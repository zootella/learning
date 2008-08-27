package com.limegroup.gnutella.gui.connection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.limegroup.gnutella.ManagedConnection;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.AutoCompleteTextField;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.WholeNumberField;
import com.limegroup.gnutella.gui.tables.AbstractTableMediator;
import com.limegroup.gnutella.gui.tables.LimeJTable;
import com.limegroup.gnutella.gui.tables.TableSettings;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.util.NetworkUtils;

/**
 * This class acts as a mediator between all of the components of the
 * connection window.
 */
public final class ConnectionMediator extends AbstractTableMediator {

    public static ConnectionMediator instance() { return INSTANCE; }

    /**
     * Listeners so buttons and possibly future right-click menu share.
     */
    ActionListener ADD_LISTENER;

    /**
     * Listeners so buttons and possibly future right-click menu share.
     */
    ActionListener BROWSE_HOST_LISTENER;

    private static final String IS_ULTRAPEER =
        GUIMediator.getStringResource("CV_STRING_ULTRAPEER");

    private static final String IS_LEAF =
        GUIMediator.getStringResource("CV_STRING_CLIENT");
        
    private static final String CONNECTING = 
        GUIMediator.getStringResource("CV_TABLE_STRING_CONNECTINGS");
        
	private static final String LEAVES =
        GUIMediator.getStringResource("CV_TABLE_STRING_LEAVES");
        
    private static final String ULTRAPEERS =
        GUIMediator.getStringResource("CV_TABLE_STRING_ULTRAPEERS");
        
    private static final String PEERS =
        GUIMediator.getStringResource("CV_TABLE_STRING_PEERS");

    private static final String STANDARD =
        GUIMediator.getStringResource("CV_TABLE_STRING_STANDARDS");

    /**
     * Instance of singleton access
     */
    private static final ConnectionMediator INSTANCE =
        new ConnectionMediator();

    /**
     * Extra component constants
     */
    private JLabel SERVENT_STATUS;
    
    /**
     * The label displaying the number of ultrapeers, peers & leaves.
     */
    private JLabel NEIGHBORS;

    /**
     * Build the listeners
     */
    protected void buildListeners() {
        super.buildListeners();
        ADD_LISTENER = new AddListener();
        BROWSE_HOST_LISTENER = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBrowseHost();
            }
	    };
    }
    
    /**
     * Overriden to have different default values for tooltips.
     */
    protected void buildSettings() {
        SETTINGS = new TableSettings(ID) {
            public boolean getDefaultTooltips() {
                return false;
            }
        };
    }

    /**
     * Add the listeners
     */
    protected void addListeners() {
        super.addListeners();
    }

	/**
	 * Set up the necessary constants.
	 */
	protected void setupConstants() {
	    //  Create padded panel without bottom padding so that button
	    //  rows for all the tabs line up.
		MAIN_PANEL = new PaddedPanel();
		DATA_MODEL = new ConnectionModel();
		TABLE = new LimeJTable(DATA_MODEL);
		BUTTON_ROW = (new ConnectionButtons(this)).getComponent();

		SERVENT_STATUS = new JLabel("");
		NEIGHBORS = new JLabel("");
    }

    /**
     * Overridden to set the size.
     */
    protected JComponent getScrolledTablePane() {
		JComponent pane = super.getScrolledTablePane();

        SCROLL_PANE.setPreferredSize(new Dimension(3000, 5000));

        return pane;
    }

    /**
     * Update the splash screen
     */
	protected void updateSplashScreen() {
		GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_CONNECTION_WINDOW"));
    }

    /**
     * Override the default main panel setup
     */
    protected void setupMainPanel() {
        if (MAIN_PANEL == null)
            return;

        super.setupMainPanel();

	    JPanel status = new JPanel();
	    status.setLayout(new BorderLayout());
	    status.add(SERVENT_STATUS, BorderLayout.WEST);
	    status.add(NEIGHBORS, BorderLayout.EAST);

        MAIN_PANEL.add(status, 0);
    }

	/**
	 * Constructor -- private for Singleton access
	 */
	private ConnectionMediator() {
	    super("CONNECTION_TABLE");
	    GUIMediator.addRefreshListener(this);
	    ThemeMediator.addThemeObserver(this);
	    doRefresh();
	}

    /**
     * Removes all selected rows from Router,
     * which will in turn remove it from the list.
     * Overrides default removeSelection
     */
    public void removeSelection() {
		int[] sel = TABLE.getSelectedRows();
		Arrays.sort(sel);
		ManagedConnection c;
		for( int counter = sel.length - 1; counter >= 0; counter--) {
			int i = sel[counter];
			c = (ManagedConnection)DATA_MODEL.get(i).getInitializeObject();
			RouterService.removeConnection(c);
		}
		clearSelection();
    }

    /** 
     * Returns the JPopupMenu for the connection table
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu jpm = new JPopupMenu();

        //  add
        JMenuItem jmi = new JMenuItem(GUIMediator
                .getStringResource("CV_BUTTON_ADD"));
        jmi.addActionListener(ADD_LISTENER);
        jpm.add(jmi);
        jpm.addSeparator();

        //  remove
        jmi = new JMenuItem(GUIMediator.getStringResource("CV_BUTTON_REMOVE"));
        jmi.addActionListener(REMOVE_LISTENER);
        jpm.add(jmi);
        jpm.addSeparator();

        //  browse host
        jmi = new JMenuItem(GUIMediator
                .getStringResource("GENERAL_BROWSE_HOST_LABEL"));
        jmi.addActionListener(BROWSE_HOST_LISTENER);
        jpm.add(jmi);

        return jpm;
    }

	/**
	 * Handles the selection of the specified row in the connection window,
	 * enabling or disabling buttons
	 *
	 * @param row the selected row
	 */
	public void handleSelection(int row) {
	    setButtonEnabled( ConnectionButtons.REMOVE_BUTTON, true );
	    setButtonEnabled( ConnectionButtons.BROWSE_HOST_BUTTON, true );
	}

	/**
	 * Handles the deselection of all rows in the download table,
	 * disabling all necessary buttons and menu items.
	 */
	public void handleNoSelection() {
	    setButtonEnabled( ConnectionButtons.REMOVE_BUTTON, false );
	    setButtonEnabled( ConnectionButtons.BROWSE_HOST_BUTTON, false );
	}

    public void handleActionKey() {
        doBrowseHost(); 
    }

    /**
     * get the first selected row and trigger a browse host
     */
    private void doBrowseHost() {
        int[] rows = TABLE.getSelectedRows();
        if(rows.length > 0) {
            ManagedConnection c = 
                (ManagedConnection)DATA_MODEL.get(rows[0]).getInitializeObject();
            GUIMediator.instance().doBrowseHost(c.getAddress(), c.getPort());
        }
    }

	/**
	 * Override the default doRefresh so we can update the servent status label
	 * (Uses doRefresh instead of refresh so this will only get called
	 *  when the table is showing.  Small optimization.)
	 */
	public void doRefresh() {
	    super.doRefresh();
	    SERVENT_STATUS.setText(GUIMediator.getStringResource("CV_STRING_STATUS") + "  " +
	        ( RouterService.isSupernode() ?
                IS_ULTRAPEER : IS_LEAF ) + "      "
        );
        int[] counts = ((ConnectionModel)DATA_MODEL).getConnectionInfo();
        NEIGHBORS.setText("(" +
            counts[1] + " " + ULTRAPEERS + ", " +
            counts[2] + " " + PEERS + ", " + 
            counts[3] + " " + LEAVES + ", " +
            counts[0] + " " + CONNECTING + ", " +
            counts[4] + " " + STANDARD + ")");
    }
    
    /**
     * Determines the number of connections that are in connecting state.
     */
    public int getConnectingCount() {
        return ((ConnectionModel)DATA_MODEL).getConnectingCount();
    }

    private void tryConnection(final String hostname, final int portnum) {
        GUIMediator.instance().schedule(new Runnable() {
            public void run() {
                RouterService.connectToHostAsynchronously(hostname, portnum);
            }
        });
    }

    /**
     *  Clear the connections visually
     */
    public void clearConnections() {
		DATA_MODEL.clear();
    }

    /**
     * Adds the host & port to the dictionary of the HOST_INPUT
     */
    void addKnownHost( String host, int port ) {
	    //HOST_INPUT.addToDictionary( host + ":" + port );
	}

    /**
     * First attempts to parse out the ':' from the host.
     * If one exists, it replaces the text in PORT_INPUT.
     * Otherwise, it uses the text in port input.
     */
    private final class AddListener implements ActionListener {
    
    	private JDialog dialog = null;
    
    	private AutoCompleteTextField HOST_INPUT = new AutoCompleteTextField(20);
    
    	private JTextField PORT_INPUT = new WholeNumberField(6346, 4);
    
    	private JButton OK_BUTTON = new JButton(GUIMediator.getStringResource("GENERAL_OK_BUTTON_LABEL"));
    
    	private JButton CANCEL_BUTTON = new JButton(GUIMediator.getStringResource("GENERAL_CANCEL_BUTTON_LABEL"));
    
    	private void createDialog() {
    	    if(dialog != null) return;
    	    //  1.  create modal dialog
    	    //      Host: [            ]
    	    //      Port: [            ]
    	    //         [ OK ] [ Cancel ]
    	    dialog = new JDialog(GUIMediator.getAppFrame(),
    				 GUIMediator.getStringResource("CV_ADD_DIALOG_TITLE"),
    				 true);
    	    JPanel jp = (JPanel)dialog.getContentPane();
    	    GUIUtils.addHideAction(jp);
    	    jp.setLayout(new GridBagLayout());
    	    GridBagConstraints gbc = new GridBagConstraints();
    
    	    //  space between title bar and Host line
    	    gbc.gridwidth = 5;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 0;
    	    gbc.gridy = 0;
    	    jp.add(getHorizontalSpacer(), gbc);
    
    	    //  lefthand side space
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 5;
    	    gbc.weightx = 0;
    	    gbc.weighty = 1;
    	    gbc.fill = GridBagConstraints.VERTICAL;
    	    gbc.gridx = 0;
    	    gbc.gridy = 1;
    	    jp.add(getVerticalSpacer(), gbc);
    
    	    //  righthand side space
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 5;
    	    gbc.weightx = 0;
    	    gbc.weighty = 1;
    	    gbc.fill = GridBagConstraints.VERTICAL;
    	    gbc.gridx = 4;
    	    gbc.gridy = 1;
    	    jp.add(getVerticalSpacer(), gbc);
    	    
    	    //  bottom spacer between buttons and window border
    	    gbc.gridwidth = 5;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 0;
    	    gbc.gridy = 6;
    	    jp.add(getHorizontalSpacer(), gbc);
    
    	    //  host label
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 0;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.NONE;
    	    gbc.gridx = 1;
    	    gbc.gridy = 1;
    	    jp.add(new JLabel(GUIMediator.getStringResource("CV_ADD_HOST_LABEL")), gbc);
    
    	    //  host label <-> host input field spacer
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 0;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.NONE;
    	    gbc.gridx = 2;
    	    gbc.gridy = 1;
    	    jp.add(getVerticalSpacer(), gbc);
    	    
    	    //  host input field
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 3;
    	    gbc.gridy = 1;
    	    jp.add(HOST_INPUT, gbc);
    
    	    //  host <-> port spacer
    	    gbc.gridwidth = 3;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 1;
    	    gbc.gridy = 2;
    	    jp.add(getHorizontalSpacer(), gbc);
    
    	    //  port label
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 0;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.NONE;
    	    gbc.gridx = 1;
    	    gbc.gridy = 3;
    	    jp.add(new JLabel(GUIMediator.getStringResource("CV_ADD_PORT_LABEL")), gbc);
    
    	    //  port label <-> port input field spacer
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 0;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.NONE;
    	    gbc.gridx = 2;
    	    gbc.gridy = 3;
    	    jp.add(getVerticalSpacer(), gbc);
    	    
    	    //  port input field
    	    gbc.gridwidth = 1;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 3;
    	    gbc.gridy = 3;
    	    jp.add(PORT_INPUT, gbc);
    
    	    //  port <-> buttons spacer
    	    gbc.gridwidth = 3;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    gbc.gridx = 1;
    	    gbc.gridy = 4;
    	    jp.add(getHorizontalSpacer(), gbc);
    
    	    //  buttons
    	    JPanel buttons = new JPanel();
    	    OK_BUTTON.addActionListener(new ActionListener() {
    		    public void actionPerformed(ActionEvent ae) {
    			String hostnamestr = HOST_INPUT.getText();
    			String portstr = PORT_INPUT.getText();
    
    			// look for the port in the host
    			int idx = hostnamestr.lastIndexOf(':');
    			// if it exists, rewrite the host & port
    			if ( idx != -1 ) {
    			    PORT_INPUT.setText( hostnamestr.substring(idx+1) );
    			    portstr = PORT_INPUT.getText();
    			    HOST_INPUT.setText( hostnamestr.substring(0, idx) );
    			    hostnamestr = HOST_INPUT.getText();
    			}
    
    			int portnum = -1;
    			try {
    			    portnum = Integer.parseInt(portstr);
    			} catch (NumberFormatException ee) {
    			    portnum = 6346;
    			}
    			if(!NetworkUtils.isValidPort(portnum))
    			    portnum = 6346;
                
    			PORT_INPUT.setText("" + portnum);
    
    			if ( !hostnamestr.equals("") ) {
    			    tryConnection(hostnamestr, portnum);
    			    dialog.setVisible(false);
    			    dialog.dispose();
    			} else {
    			    HOST_INPUT.requestFocus();
    			}
    		    }
    		});
    	    CANCEL_BUTTON.addActionListener(GUIUtils.getDisposeAction());
    	    buttons.add(OK_BUTTON);
    	    buttons.add(CANCEL_BUTTON);
    	    gbc.gridwidth = 3;
    	    gbc.gridheight = 1;
    	    gbc.weightx = 1;
    	    gbc.weighty = 0;
    	    gbc.fill = GridBagConstraints.NONE;
    	    gbc.anchor = GridBagConstraints.EAST;
    	    gbc.gridx = 1;
    	    gbc.gridy = 5;
    	    jp.add(buttons, gbc);
       	}

        public void actionPerformed(ActionEvent e) {
    	    if(dialog == null) createDialog();
    
    	    //  2.  display dialog centered (and modal)
    	    dialog.getRootPane().setDefaultButton(OK_BUTTON);
    	    dialog.pack();
    	    dialog.setLocation(GUIMediator.getScreenCenterPoint(dialog));
    	    dialog.setVisible(true);
    	}

    	/** Returns a vertical separator */
    	private Component getVerticalSpacer() {
    	    return Box.createRigidArea(new Dimension(6,0));
    	}
    
    	/** Returns a horizontal separator */
    	private Component getHorizontalSpacer() {
    	    return Box.createRigidArea(new Dimension(0,6));
    	}
    }
}
