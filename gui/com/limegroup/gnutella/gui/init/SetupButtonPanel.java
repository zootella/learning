package com.limegroup.gnutella.gui.init;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.Border;

import com.limegroup.gnutella.gui.BoxPanel;

/**
 * this class creates a generic setup button panel with a standardized
 * margin for the border and using BoxLayout oriented along the X axis.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class SetupButtonPanel extends BoxPanel {
	
	/**
	 * Constant for the margin to avoid too much typing.
	 */
	private final int MARGIN = SetupWindow.MARGIN;

	/**
	 * Creates a standard panel with buttons and a standard margin.
	 */
	SetupButtonPanel() {
		super(BoxLayout.X_AXIS);
		Border border = 
		    BorderFactory.createEmptyBorder(MARGIN, MARGIN,
											MARGIN,	MARGIN);
		setBorder(border);
	}
}
