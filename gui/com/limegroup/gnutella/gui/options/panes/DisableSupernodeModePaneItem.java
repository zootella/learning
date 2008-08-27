package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.UltrapeerSettings;

/**
 * This class gives the user the option of whether or not to automatically
 * connect to the network when the program first starts.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class DisableSupernodeModePaneItem extends AbstractPaneItem {

	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * check box that allows the user to connect automatically or not
	 */
	private final String CHECK_BOX_LABEL = 
		"OPTIONS_DISABLE_SUPERNODE_MODE_BOX_LABEL";

	/**
	 * Constant for the check box that determines whether or not 
	 * to connect automatically on startup
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	public DisableSupernodeModePaneItem(final String key) {
		super(key);
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX,
													 LabeledComponent.LEFT_GLUE);
		add(comp.getComponent());
	}

	public void initOptions() {
        CHECK_BOX.setSelected(UltrapeerSettings.DISABLE_ULTRAPEER_MODE.getValue());
	}

	public boolean applyOptions() throws IOException {
	    boolean changed = 
	        UltrapeerSettings.DISABLE_ULTRAPEER_MODE.getValue() !=
	        CHECK_BOX.isSelected();
        boolean isSupernode = RouterService.isSupernode();
		UltrapeerSettings.DISABLE_ULTRAPEER_MODE.setValue(CHECK_BOX.isSelected());
        if(changed && CHECK_BOX.isSelected() && isSupernode) {
            RouterService.disconnect();
            RouterService.connect();
        }
        return false;
	}
	
    public boolean isDirty() {
        return UltrapeerSettings.DISABLE_ULTRAPEER_MODE.getValue() != CHECK_BOX.isSelected();
    }	
}
