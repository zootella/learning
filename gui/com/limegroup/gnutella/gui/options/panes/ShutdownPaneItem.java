/*
 * ShutdownPaneItem.java
 *
 * Created on November 3, 2001, 8:42 AM
 */

package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * This class defines the panel in the options
 * window that allows the user to select the
 * default shutdown behavior.
 */
public class ShutdownPaneItem extends AbstractPaneItem { 
    
    /** RadioButton for selecting immediate shutdown
     */    
    private JRadioButton shutdownImmediately;
    
    /** RadioButton for selecting the shutdown after transfer
     * completion option.
     */    
    private JRadioButton shutdownAfterTransfers;
    
    /** RadioButton for selecting the minimize to tray option.  This
     * option is only displayed on systems that support the tray.
     */    
    private JRadioButton minimizeToTray;

    /** Creates new ShutdownOptionsPaneItem
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that 
     *      the superclass uses to generate locale-specific keys
     */
    public ShutdownPaneItem(final String key) {
        super(key);
        
        BoxPanel buttonPanel = new BoxPanel();
        
        String immediateLabel = "OPTIONS_SHUTDOWN_IMMEDIATELY_LABEL";
        String whenReadyLabel = "OPTIONS_SHUTDOWN_AFTER_TRANSFERS_LABEL";
        String minimizeLabel  = "OPTIONS_SHUTDOWN_TO_TRAY_LABEL";
        shutdownImmediately = new JRadioButton(GUIMediator.getStringResource(immediateLabel));
        shutdownAfterTransfers = new JRadioButton(GUIMediator.getStringResource(whenReadyLabel));
        minimizeToTray = new JRadioButton(GUIMediator.getStringResource(minimizeLabel));
        
        ButtonGroup bg = new ButtonGroup();
        buttonPanel.add(shutdownImmediately);
        buttonPanel.add(shutdownAfterTransfers);
        bg.add(shutdownImmediately);
        bg.add(shutdownAfterTransfers);
        if (CommonUtils.supportsTray()) {
            buttonPanel.add(minimizeToTray);
            bg.add(minimizeToTray);
        }
        
        BoxPanel mainPanel = new BoxPanel(BoxPanel.X_AXIS);
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createHorizontalGlue());
        
        add(mainPanel);
    }

    /**
     * Applies the options currently set in this <tt>PaneItem</tt>.
     *
     * @throws IOException if the options could not be fully applied
     */
    public boolean applyOptions() throws IOException {
        
        if (minimizeToTray.isSelected()) {
            ApplicationSettings.MINIMIZE_TO_TRAY.setValue(true);
            ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.setValue(false);
        } else if (shutdownAfterTransfers.isSelected()) {
            ApplicationSettings.MINIMIZE_TO_TRAY.setValue(false);
            ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.setValue(true);
        } else {
            ApplicationSettings.MINIMIZE_TO_TRAY.setValue(false);
            ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.setValue(false);
        }
        return false;
    }
    
    /**
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        if (ApplicationSettings.MINIMIZE_TO_TRAY.getValue())
            minimizeToTray.setSelected(true);
        else if (ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.getValue())
             shutdownAfterTransfers.setSelected(true);
        else shutdownImmediately.setSelected(true);
    }
    
    public boolean isDirty() {
        boolean minimized =
            ApplicationSettings.MINIMIZE_TO_TRAY.getValue() &&
            !ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.getValue();
        boolean afterTransfers =
            !ApplicationSettings.MINIMIZE_TO_TRAY.getValue() &&
            ApplicationSettings.SHUTDOWN_AFTER_TRANSFERS.getValue();
        return minimizeToTray.isSelected() != minimized ||
               shutdownAfterTransfers.isSelected() != afterTransfers;
    }
}
