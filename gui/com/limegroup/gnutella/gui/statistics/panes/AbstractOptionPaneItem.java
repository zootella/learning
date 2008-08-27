package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.TitledPaddedPanel;

/**
 * This class provides a skeletal implementation of the <tt>PaneItem</tt>
 * interface.<p>
 *
 * It provides the basic implementation for displaying one statistic within
 * a larger window of statistics. Each <tt>AbstractPaneItem</tt> has a titled
 * border and a label describing the statistic.  The label is followed by
 * standardized spacing.<p>
 * 
 * It includes several convenience methods that subclasses may us to 
 * simplify panel construction.<p>
 *
 * Subclasses only need to override the applyStatistics() method for storing
 * statistics to disk.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public abstract class AbstractOptionPaneItem implements PaneItem {

	/**
	 * The container that elements in the pane are added to.
	 */
	private final TitledPaddedPanel CONTAINER = new TitledPaddedPanel();

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	private static final Component HORIZONTAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(6,0));

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	private static final Component VERTICAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(0,6));


    private final PaneItemMainLabel LABEL;
	
	
	/**
	 * This sole constructor overrides the the public accessibility of the 
	 * default constructor and is usually called implicitly.
	 *
	 * @param key the key for obtaining the locale-specific values for
	 *  displayed strings
	 */
	protected AbstractOptionPaneItem(final String key) {
		String title = "STATS_"+key+"_TITLE";
		String label = "STATS_"+key+"_LABEL";
		CONTAINER.setTitle(GUIMediator.getStringResource(title));
        LABEL = new PaneItemMainLabel(GUIMediator.getStringResource(label));
        add(LABEL.getLabel());
	}

    // inherit doc comment
    public JComponent getStatsComponent() {
        return null;
    }

	// inherit doc comment
	public Container getContainer() {
		return CONTAINER;
	}

	/**
	 * Adds the specified <tt>Component</tt> to the enclosed <tt>Container</tt> 
	 * instance.
	 *
	 * @param comp the <tt>Component</tt> to add
	 */
	protected final void add(Component comp) {
		CONTAINER.add(comp);
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for horizontal separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard horizontal
	 *         separator
	 */
	protected final Component getHorizontalSeparator() {
		return HORIZONTAL_SEPARATOR;
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for vertical separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard vertical
	 *         separator
	 */
	protected final Component getVerticalSeparator() {
		return VERTICAL_SEPARATOR;
	}

	// inherit doc comment
	public void refresh() {}

	// inherit doc comment
    public void componentResized(ComponentEvent e, Component comp) {
        LABEL.componentResized(e, comp);
    }
}
