package com.limegroup.gnutella.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import net.roydesign.event.ApplicationEvent;
import net.roydesign.mac.MRJAdapter;

import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.ManagedThread;

/**
 * This class handles Macintosh specific events. The handled events  
 * include the selection of the "About" option in the Mac file menu,
 * the selection of the "Quit" option from the Mac file menu, and the
 * dropping of a file on LimeWire on the Mac, which LimeWire would be
 * expected to handle in some way.
 */
public class MacEventHandler {
    
    private static final MacEventHandler instance = new MacEventHandler();
    
    public static MacEventHandler instance() {
        return instance;
    }
    
    /** Creates a new instance of MacEventHandler */
    private MacEventHandler() {
        
        MRJAdapter.addAboutListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleAbout();
            }
        });
        
        MRJAdapter.addQuitApplicationListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleQuit();
            }
        });
        
        MRJAdapter.addOpenDocumentListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                File file = ((ApplicationEvent)evt).getFile();
                handleOpenFile(file);
            }
        });
        
        MRJAdapter.addReopenApplicationListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleReopen();
            }
        });
    } 
    
    /**
     * Enable preferences.
     */
    public void enablePreferences() {
        MRJAdapter.setPreferencesEnabled(true);
        
        MRJAdapter.addPreferencesListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handlePreferences();
            }
        });
    }
    
    /**
    * This responds to the selection of the about option by displaying the
    * about window to the user.  On OSX, this runs in a new ManagedThread to handle
    * the possibility that event processing can become blocked if launched
    * in the calling thread.
    */
    private void handleAbout() {
        // Don't use a separate thread.  On Jaguar, using a separate thread
        // can result in the code not being executes, as the calling thread
        // can call System.exit before this thread is able to finish.
        if(CommonUtils.isJaguarOrAbove() || CommonUtils.isJava14OrLater()) {
                    
            GUIMediator.showAboutWindow();
            
        } else {
            new ManagedThread("Mac about menu thread") {
                public void managedRun() {
                    GUIMediator.showAboutWindow();
                }
            }.start();
        }
    }
    
    /**
    * This method responds to a quit event by closing the application in
    * the whichever method the user has configured (closing after completed
    * file transfers by default).  On OSX, this runs in a new ManagedThread to handle
    * the possibility that event processing can become blocked if launched
    * in the calling thread.
    */
    private void handleQuit() {
        // Don't use a separate thread.  On Jaguar, using a separate thread
        // can result in the code not being executes, as the calling thread
        // can call System.exit before this thread is able to finish.
        if(CommonUtils.isJaguarOrAbove() || CommonUtils.isJava14OrLater()) {
            GUIMediator.applyWindowSettings();
            GUIMediator.close(false);
        } else {
            new ManagedThread("Mac quit thread") {
                public void managedRun() {
                    GUIMediator.applyWindowSettings();
                    GUIMediator.close(false);
                }
            }.start();
        }
    }
    

    /**
     * This method handles a request to open the specified file.
     */
    private void handleOpenFile(File file) {
        
        String filename = file.toString();
        
        // Java 1.4 => Panther or above!
        if (CommonUtils.isJava14OrLater() && 
            filename.endsWith("limestart")) {
            
            Initializer.setStartup();
        
        } else if(CommonUtils.isJaguarOrAbove() || 
                    CommonUtils.isJava14OrLater()) {
                        
            PackagedMediaFileLauncher.launchFile(filename, false);  
        
        } else {
            new HandleOpenFile(file).start();
        }
    }
    
    class HandleOpenFile extends ManagedThread {
        File requestedFile;

        public HandleOpenFile(File file) {
            super("handleOpenFile");
            requestedFile = file;
        }
        public void managedRun() {
            PackagedMediaFileLauncher.launchFile(
              requestedFile.toString(), false);  
        }
    }
    
    private void handleReopen() {
        GUIMediator.handleReopen();
    }
    
    private void handlePreferences() {
        GUIMediator.instance().setOptionsVisible(true);
    }
}
