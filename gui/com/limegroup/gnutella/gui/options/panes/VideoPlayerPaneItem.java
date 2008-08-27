/*
 * ShutdownPaneItem.java
 *
 * Created on March 11, 2002
 */

package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JTextField;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.settings.URLHandlerSettings;

/**
 * This class defines the panel in the options
 * window that allows the user to select the
 * default video behavior.
 */
public class VideoPlayerPaneItem extends AbstractPaneItem { 
    
	/**
	 * Constant for the key of the locale-specific <code>String</code> for the 
	 * label on the component that allows to user to change the setting for
	 * this <tt>PaneItem</tt>.
	 */
	private final String OPTION_LABEL = "OPTIONS_VIDEO_PLAYER_BOX_LABEL";
    
    /** 
     * Handle to the <tt>JTextField</tt> that displays the player name
     */    
    private JTextField _playerField;
    
    /** Creates new VideoPlayerOptionsPaneItem
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that 
     *      the superclass uses to generate locale-specific keys
     */
    public VideoPlayerPaneItem(final String key) {
        super(key);
        _playerField = new SizedTextField();
        LabeledComponent comp = 
            new LabeledComponent( OPTION_LABEL, _playerField,
                                  LabeledComponent.TOP_LEFT);
		add(comp.getComponent());
	}
    
    /**
     * Applies the options currently set in this <tt>PaneItem</tt>.
     *
     * @throws IOException if the options could not be fully applied
     */
    public boolean applyOptions() throws IOException {
        URLHandlerSettings.VIDEO_PLAYER.setValue(_playerField.getText());
        return false;
    }
    
    /**
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        _playerField.setText(URLHandlerSettings.VIDEO_PLAYER.getValue());
    }
    
    public boolean isDirty() {
        return !URLHandlerSettings.VIDEO_PLAYER.getValue().equals(_playerField.getText());
    }
}