package com.limegroup.gnutella.gui.init;

import java.awt.Dimension;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MultiLineLabel;

/**
 * Specialized class that is a MultiLineLabel sized especially
 * for setup windows.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class SetupLabel extends MultiLineLabel {
	
	/**
	 * Constructs a new multiple line label with specialized dimensions
	 * for the setup windows.
	 *
	 * @param labelKey the key for the locale-specific label to use
	 */
	SetupLabel(final String labelKey) {
		super(GUIMediator.getStringResource(labelKey), 
			  SetupWindow.SETUP_WIDTH - (SetupWindow.MARGIN * 5));
		int totalMargin = SetupWindow.MARGIN * 5;
		Dimension size = new Dimension(SetupWindow.SETUP_WIDTH-totalMargin, 
									   80);
		setPreferredSize(size);
		setMaximumSize(size);
	}
}
