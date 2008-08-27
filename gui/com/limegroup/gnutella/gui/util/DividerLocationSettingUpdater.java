package com.limegroup.gnutella.gui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

import com.limegroup.gnutella.settings.IntSetting;

/**
 * Keeps track of the divder location changes of a {@link JSplitPane} and updates
 * an {@link IntSetting}.
 */
public class DividerLocationSettingUpdater implements PropertyChangeListener {

	private IntSetting setting;

	/**
	 * Creates a DividerLocationSettingUpdater which adds itself as a property
	 * change listener to the split pane and updates the int setting when 
	 * the divider location changes.
	 * <p>
	 * The constructor also sets the divider location to the value of the setting.
	 * @param pane
	 * @param setting
	 */
	public DividerLocationSettingUpdater(JSplitPane pane, IntSetting setting) {
		this.setting = setting;
		pane.setDividerLocation(setting.getValue());
		pane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		setting.setValue(((Integer)evt.getNewValue()).intValue());
	}

}
