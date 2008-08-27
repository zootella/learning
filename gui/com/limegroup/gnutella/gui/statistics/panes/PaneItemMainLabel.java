package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.MultiLineLabel;

/**
 * This class uses the decorator pattern around a <tt>MultiLineLabel</tt>
 * that is customized for use in statistics panes.
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
	private final int LABEL_WIDTH = 480;

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
        height += 12;
		
  		// add separator pixels to the height
  		height += 10;
        Dimension dim = new Dimension(1000, height);
  		LABEL.setPreferredSize(dim);
  		LABEL.setMaximumSize(dim);
	}

	/**
	 * Returns the <tt>Component</tt> containing the underlying label.
	 *
	 * @return the <tt>Component</tt> containing the underlying label
	 */
	final JComponent getLabel() {
        return LABEL;
	}

	// inherit doc comment
	public void componentResized(ComponentEvent e, Component comp) {
        Dimension dim = comp.getSize();
        LABEL.setText(LABEL.getText(), dim.width-55);
	}
}
