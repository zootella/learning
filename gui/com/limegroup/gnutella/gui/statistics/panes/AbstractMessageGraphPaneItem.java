package com.limegroup.gnutella.gui.statistics.panes;

import javax.swing.JComponent;

/**
 * This class provides an implementation of the <tt>PaneItem</tt> interface
 * for displaying a statistics graph.<p>
 *
 * It provides the basic implementation for displaying one statistic within
 * a larger window of statistics. Each <tt>AbstractMessageGraphPaneItem</tt> 
 * has a titled border and a label describing the statistic.  The label is 
 * followed by standardized spacing.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public abstract class AbstractMessageGraphPaneItem extends AbstractPaneItem {

	/**
	 * Check box for selecting to record stats to a file.
	 */
    /*
      private final JCheckBox CHECK_BOX = 
		new JCheckBox(GUIMediator.getStringResource("STATS_RECORD_STATS_LABEL"));
    */

	/**
	 * Creates a new <tt>AbstractPaneItem</tt> with the default settings
	 * for the displayed graph.
	 *
	 * @param key the key for obtaining the locale-specific values for
	 *  displayed strings
	 */
	protected AbstractMessageGraphPaneItem(final String key) {
		this(key, new GraphAxisData());
	}

	/**
	 * This sole constructor overrides the the public accessibility of the 
	 * default constructor and is usually called implicitly.
	 *
	 * @param key the key for obtaining the locale-specific values for
	 *  displayed strings
	 * @param data the <tt>GraphAxisData</tt> instance specifying the
	 *  customized display values to use for the graph
	 */
	protected AbstractMessageGraphPaneItem(final String key, GraphAxisData data) {
		super(key, new GraphPainter(data));
	}

    
    // inherit doc comment
	public JComponent getStatsComponent() {
		return (JComponent)STAT_DISPLAYER;
	}

	// overrides Object.toString
 	public String toString() {
 		return "AbstractMessageGraphPaneItem: "+CONTAINER.getTitle();
 	}
}
