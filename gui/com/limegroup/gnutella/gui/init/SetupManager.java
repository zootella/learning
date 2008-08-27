package com.limegroup.gnutella.gui.init;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.SplashWindow;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.InstallSettings;
import com.limegroup.gnutella.settings.SettingsHandler;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class manages the setup wizard.  It constructs all of the primary
 * classes and acts as the mediator between the various objects in the
 * setup windows.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|

public class SetupManager {	

	/**
	 * the dialog window that holds all other gui elements for the setup.
	 */
	private JDialog _dialog;

	/**
	 * the class that handles the buttons for the setup panels.
	 */
	private SetupButtons _setupButtons;

	/** 
	 * the holder for the setup windows 
	 */
	private SetupWindowHolder _setupWindowHolder;

	/**
	 * holder for the current setup window.
	 */
	private SetupWindow _currentWindow;

	/**
	 * The array of scanned directory path names.
	 */
	private String[] _scannedPaths;

	/**
	 * Flag for whether or not a scan has already been completed.
	 */
	private boolean _scanned = false;

	/**
	 * Construts SetupManager, which can be used to determine which portions
	 * of setup need to be run, etc.
	 */
	public SetupManager() {
	    // Must set legacy values to true.
        if( ApplicationSettings.INSTALLED.getValue() ) {
            InstallSettings.SAVE_DIRECTORY.setValue(true);
            InstallSettings.SPEED.setValue(true);
            InstallSettings.SCAN_FILES.setValue(true);
        }
    }
    
    /**
     * Determines if the 'start on startup' window should be shown.
     */
    private boolean shouldShowStartOnStartupWindow() {
        if(InstallSettings.START_STARTUP.getValue())
            return false;
            
        return GUIUtils.shouldShowStartOnStartupWindow();
    }
    
    /**
     * Determines if the 'a firewall warning may be displayed' window should be shown.
     */
    public boolean shouldShowFirewallWindow() {
        if(InstallSettings.FIREWALL_WARNING.getValue())
            return false;
        
        return CommonUtils.isWindows();
    }
    
    /**
     * Constructs the appropriate setup windows if needed.
     */
    public void createIfNeeded() {
        List windows = new LinkedList();
        
        if( !InstallSettings.LANGUAGE_CHOICE.getValue())
            windows.add(new LanguageWindow(this));

        if( !InstallSettings.SAVE_DIRECTORY.getValue() )
            windows.add(new SaveWindow(this));
            
        if( !InstallSettings.SPEED.getValue() )
            windows.add(new SpeedWindow(this));
        
        if( shouldShowStartOnStartupWindow() )
            windows.add(new StartupWindow(this));

        if( shouldShowFirewallWindow() ) {
            windows.add(new FirewallWindowOne(this));
            windows.add(new FirewallWindowTwo(this));
        }
        
        // SCAN WINDOW MUST BE LAST BECAUSE ITS FORWARD-BACK
        // MOVING IS DEPENDENT ON USER OPTIONS.
        if( !InstallSettings.SCAN_FILES.getValue() ) {
            windows.add(new ScanWindow(this));
            windows.add(new WaitWindow(this));
            windows.add(new ScanConfirmWindow(this));
        }            
            
        // Nothing to install?.. Begone.
        if( windows.size() == 0 )
            return;
            
        // If the INSTALLED value is set, that means that a previous
        // installer has already been run.
        boolean partial = ApplicationSettings.INSTALLED.getValue();
        
        // We need to ask the user's language very very first,
        // so make sure that if the LanguageWindow is the first item,
        // that the WelcomeWindow is inserted second.
        // It's a little more tricky than that, though, because
        // it could be possible that the LanguageWindow was the only
        // item to be installed -- if that's the case, don't even
        // insert the WelcomeWindow & FinishWindow at all.
        if(windows.get(0) instanceof LanguageWindow) {
            if(windows.size() > 1) {
                windows.add(1, new WelcomeWindow(this, partial));
                windows.add(new FinishWindow(this));
            }
        } else {
            windows.add(0, new WelcomeWindow(this, partial));
            windows.add(new FinishWindow(this));
        }
        
        // Create the setup window holder, which switches
        // between the panels using a card layout.
        _setupWindowHolder = new SetupWindowHolder();
        
        // Iterate through each displayed window and set them up
        // correctly.  Note the special handling with the ScanWindow,
        // which requires different windows for the 'Yes' and 'No'
        // responses.
        SetupWindow prior = null;
        SetupWindow current = null;
        ScanWindow scanner = null;
        for(Iterator i = windows.iterator(); i.hasNext(); ) {
            current = (SetupWindow)i.next();
            _setupWindowHolder.add(current);
            
            if(prior == null)
                current.setPrevious(current);
            else
                current.setPrevious(prior);
            
            // ScanWindow must be treated specially,
            // with the 'Yes' going to the 'wait' window and the
            // 'No' going to the next available window.
            if(current instanceof ScanWindow)
                scanner = (ScanWindow)current;
            else if(current instanceof WaitWindow)
                scanner.setYesWindow(current);
            else if(current instanceof ScanConfirmWindow)
                current.setPrevious(scanner);
                
            if(prior != null)
                prior.setNext(current);
            prior = current;
        }
        if(scanner != null)
            scanner.setNoWindow((SetupWindow)windows.get(windows.size()-1));
        current.setNext(current);        
		
		// Actually display the setup dialog.
		createDialog((SetupWindow)windows.get(0));
    }
    
    /*
	 * Creates the main <tt>JDialog</tt> instance and
	 * creates all of the setup window classes, buttons, etc.
	 */
	private void createDialog(SetupWindow firstWindow) {
		_dialog = new JDialog();
		_dialog.setModal(true);
	   
		// JDialog sizing seems to work differently with some Unix
		// systems, so we'll just make it resizable.
		if(!CommonUtils.isUnix())
		    _dialog.setResizable(false);
		
        _dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cancelSetup();
            }
        });

		// set the layout of the content pane
		Container container = _dialog.getContentPane();
		BoxLayout containerLayout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(containerLayout);

		// create the main panel
		JPanel setupPanel = new JPanel();
		BoxLayout layout = new BoxLayout(setupPanel, BoxLayout.Y_AXIS);
		setupPanel.setLayout(layout);


		// compare against a little bit less than the screen size,
		// as the screen size includes the taskbar
        Dimension d = new Dimension(SetupWindow.SETUP_WIDTH, 
									SetupWindow.SETUP_HEIGHT);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        _dialog.setLocation((screenSize.width-d.width)/2,
							(screenSize.height-d.height)/2);

    	// create the setup buttons panel
		_setupButtons = new SetupButtons(this);
		setupPanel.add(_setupWindowHolder);
		setupPanel.add(Box.createVerticalStrut(17));
		setupPanel.add(_setupButtons);
		
        show(firstWindow);		

		// add the panel and make it visible		
		container.add(setupPanel);

		_dialog.pack();
		_dialog.toFront();
		SplashWindow.instance().toBack();
		_dialog.setVisible(true);

	}
   
	/**
	 * Displays the next window in the setup sequence.
	 */
	public void next() {
		SetupWindow newWindow = _currentWindow.getNext();
		try {			
			_currentWindow.applySettings();
			show(newWindow);
		} catch(ApplySettingsException ase) {
			// there was a problem applying the settings from
			// the current window, so display the error message 
			// to the user.
			GUIMediator.showError(ase.getMessage());			
		}
	}

	/**
	 * Displays the previous window in the setup sequence.
	 */
	public void previous() {
		SetupWindow newWindow = _currentWindow.getPrevious();
		show(newWindow);
	}

	/**
	 * Sets the array of scanned path names.
	 */
	public void scan() {
		if(!_scanned) {
			_scannedPaths = scanDrive();
			_scanned = true;
		}
	}

	/**
	 * Returns the array of potential directory paths to share
	 * based on the hard drive scan.
	 *
	 * @return the array of scanned directory paths containing 
	 *         potential files to share
	 */
	public String[] getScannedPaths() {
		return _scannedPaths;
	}

	
	/**
	 * Cancels the setup.
	 */
	public void cancelSetup() {
		_dialog.dispose();
		System.exit(0);
	}

	/**
	 * Completes the setup.
	 */
	public void finishSetup() {		
		_dialog.dispose();
		
		ApplicationSettings.INSTALLED.setValue(true);
		
		InstallSettings.SAVE_DIRECTORY.setValue(true);
		InstallSettings.SPEED.setValue(true);
		InstallSettings.SCAN_FILES.setValue(true);
		InstallSettings.LANGUAGE_CHOICE.setValue(true);
		
		if(GUIUtils.shouldShowStartOnStartupWindow());
            InstallSettings.START_STARTUP.setValue(true);
        if(CommonUtils.isWindows())
            InstallSettings.FIREWALL_WARNING.setValue(true);
		
		SettingsHandler.save();
	}
	
	/**
	 * Instructs the buttons to redo their text.
	 */
	public void remakeButtons() {
        _setupButtons.reconstruct(this);
	}

	/**
	 * Mediator method for changing the buttons in the setup button class
	 * to show the finish button.
	 */
	public void goToFinishButtons() {
		_setupButtons.goToFinishButtons();	
	}

	/**
	 * Mediator method for changing the buttons in the setup button class
	 * to show the cancel button.
	 */
	public void goToCancelButtons() {
		_setupButtons.goToCancelButtons();	
	}

	/**
	 * Mediator method for changing the buttons in the setup button class
	 * to show its standard buttons
	 */
	public void goToStandardButtons() {
		_setupButtons.goToStandardButtons();	
	}
	
	/**
	 * Mediator method for changing the buttons in the setup button class
	 * to show its next buttons.
	 */
	public void goToNextButtons() {
	    _setupButtons.goToNextButtons();
	}

	/**
	 * Show the specified window
	 */
	private void show(SetupWindow window) {
        window.handleWindowOpeningEvent();	    
		_setupWindowHolder.show(window.getKey());
		_dialog.setTitle(window.getName());
		_currentWindow = window;
	}

	/**
	 * Scans the user's hard drive for media files, and returns,
	 * at most, the top 5 directories as an array of Strings.
	 *
	 * @return the array of pathname <tt>String</tt>s of the directories
	 *         containing media files
	 */   
	private String[] scanDrive() {
		FileScan fs = new FileScan();
		String[] filters = {
			"Recycle","Incomplete","LimeWire","Microsoft", "bin", 
			"system","WINNT"
		};
		fs.setFilters(filters);

		// get the root directory of the current directory.
		// scan that directory for files.
		File root = getRoot(CommonUtils.getCurrentDirectory());
		try {
			fs.scan( root.getCanonicalPath() );
		} catch(IOException ioe) {
			
		}
		return fs.getListAsArray();
	}

	/**
	 * Recursively travels up the path pf the file until it finds
	 * the root directory.  
	 *
	 * @return the <tt>File</tt> instance denoting the abstract pathname
	 *         of the root directory
	 */
	private File getRoot(File f) {
		String parent;
		parent = f.getParent();
		if (parent == null)
			return f;
		else 
			return getRoot( new File(parent) );
	}

}






