package com.limegroup.gnutella.gui.options.panes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import com.limegroup.gnutella.gui.MultiLineLabel;

/**
 * This class uses the decorator pattern around a <tt>MultiLineLabel</tt>
 * that is customized for use in options panes.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class PaneItemMainLabel {
	
	/**
	 * The wrapped <tt>MultiLineLabel</tt> label instance.
	 */
	private final MultiLineLabel LABEL;

	/**
	 * Constant for the width of labels.
	 */
	private final int LABEL_WIDTH = 415;

	/**
	 * This constructor creates the label object with the standard width
	 * and maximum size.
	 *
	 * @param str the string for the label
	 */
	PaneItemMainLabel(final String str) {
		LABEL = new MultiLineLabel(str, LABEL_WIDTH);
		FontMetrics fm = LABEL.getFontMetrics(LABEL.getFont());
		int height = fm.getHeight();
		height *= LABEL.getLineCount();
		
		// add separator pixels to the height
		height += 10;
		Dimension dim = new Dimension(500, height);
		LABEL.setPreferredSize(dim);
		LABEL.setMaximumSize(dim);
	}

	/**
	 * Returns the <tt>Component</tt> containing the underlying label.
	 *
	 * @return the <tt>Component</tt> containing the underlying label
	 */
	final Component getLabel() {
		return LABEL;
	}
}
