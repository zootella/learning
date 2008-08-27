package com.limegroup.gnutella.gui;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;

public class CocoaApplicationEventHandler implements ApplicationListener {

    // Handle to the Cocoa Application interface
    private final Application _app = new Application();
    
    private static CocoaApplicationEventHandler INSTANCE;
    
    public static CocoaApplicationEventHandler instance() {
        if(INSTANCE == null)
            INSTANCE = new CocoaApplicationEventHandler();
        return INSTANCE;
    }

    /**
     * For MacOSX, Java 1.4 access to Cocoa subsystem.  See the methods 
     * handleAbout(), handleOpenApplication(), handleOpenFile(),
     * handlePreferences, handlePrintFile(), handleQuit()
     */
    private CocoaApplicationEventHandler() { ; }

    /**
     * Sets the 'Preferences' menu to be enabled and handles when a user selects
     * it (through handlePreferences()) .
     */
    public void register() {
        _app.addApplicationListener(this);
    }
    
    /**
     * Enables the preferences menu item.
     */
    public void enablePreferences() {
        _app.setEnabledPreferencesMenu(true);
    }

    //--------------------------------------------------------------------------
    // ApplicationListener interface implementation
    //--------------------------------------------------------------------------

    
    public void handleAbout(ApplicationEvent event) {
        event.setHandled(true);
        GUIMediator.showAboutWindow();
    }

    public void handleOpenApplication(ApplicationEvent event) {
        event.setHandled(true);
    }

    public void handleReOpenApplication(ApplicationEvent event) {
        event.setHandled(true);
        GUIMediator.handleReopen();
    }

    public void handleOpenFile(ApplicationEvent event) {
        event.setHandled(true);
        if(event.getFilename().endsWith("limestart"))
            Initializer.setStartup();
        else
            PackagedMediaFileLauncher.launchFile(event.getFilename(), false);
    }

    public void handlePreferences(ApplicationEvent event) {
        event.setHandled(true);
        // turn on that badboy (options)
        GUIMediator.instance().setOptionsVisible(true);
    }

    public void handlePrintFile(ApplicationEvent event) {}

    public void handleQuit(ApplicationEvent event) {
        event.setHandled(true);
        GUIMediator.shutdown();
    }
    
    
    // required for compiling on windows, presumably because the stub
    // jar has an older interface.
    public void handlePrintDocument(ApplicationEvent ev) {
        ev.setHandled(true);
    }
    public void handleOpenDocument(ApplicationEvent ev) {
        ev.setHandled(true);
    }

}
