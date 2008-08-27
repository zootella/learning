package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.TitledPaddedPanel;
import com.limegroup.gnutella.statistics.Statistic;

/**
 * This class provides a skeletal implementation of the <tt>PaneItem</tt>
 * interface.<p>
 *
 * It provides the basic implementation for displaying one statistic within
 * a larger window of statistics. Each <tt>AbstractPaneItem</tt> has a titled
 * border and a label describing the statistic.  The label is followed by
 * standardized spacing.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public abstract class AbstractPaneItem implements PaneItem {
	
	/**
	 * The component that displays the statistic.
	 */
	protected final StatisticDisplayer STAT_DISPLAYER;

	/**
	 * The container that elements in the pane are added to.
	 */
	protected final TitledPaddedPanel CONTAINER = new TitledPaddedPanel();

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	protected static final Component HORIZONTAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(6,0));

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	protected static final Component VERTICAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(0,6));

	
	/**
	 * Label for the statistic.
	 */
    private final PaneItemMainLabel LABEL;


	/**
	 * This sole constructor overrides the the public accessibility of the 
	 * default constructor and is usually called implicitly.
	 *
	 * @param key the key for obtaining the locale-specific values for
	 *  displayed strings
	 */
	protected AbstractPaneItem(final String key, final StatisticDisplayer statDisplayer) {
		STAT_DISPLAYER = statDisplayer;
		String title = "STATS_"+key+"_TITLE";
		String label = "STATS_"+key+"_LABEL";
		CONTAINER.setTitle(GUIMediator.getStringResource(title));
        LABEL = new PaneItemMainLabel(GUIMediator.getStringResource(label));
        add(LABEL.getLabel());
		add(statDisplayer.getComponent());
	}

    
    // inherit doc comment
    public JComponent getStatsComponent() {
		return STAT_DISPLAYER.getComponent();
    }

    // inherit doc oomment
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

	/**
	 * Registers the specified <tt>Statistic</tt> for display.
	 *
	 * @param stat the <tt>Statistic</tt> instance to register
	 */
	protected void registerStatistic(Statistic stat, String displayName) {
		STAT_DISPLAYER.registerStatistic(stat, displayName);
	}

	/**
	 * Registers the set of two <tt>Statistic</tt>s for display.
	 *
	 * @param stat the <tt>Statistic</tt> instance to register
	 */
	protected void registerDualStatistic(Statistic stat0, Statistic stat1, 
										 String displayName) {
		STAT_DISPLAYER.registerDualStatistic(stat0, stat1, displayName);
	}

	/**
	 * Refreshes the statistics for this <tt>PaneItem</tt>.
	 */
	public void refresh() {
		STAT_DISPLAYER.refresh();				
	}

	// overrides Object.toString
	public String toString() {
		return "AbstractPaneItem: "+CONTAINER.getTitle();
	}

	// inherit doc comment
    public void componentResized(ComponentEvent e, Component comp) {
        LABEL.componentResized(e, comp);
    }
}
