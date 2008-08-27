package com.limegroup.gnutella.gui.statistics.panes;

import javax.swing.JComponent;

import com.limegroup.gnutella.statistics.Statistic;

/**
 * Interface for a class that visually displays a statistic or group of
 * statistics, whether a graph, a table of stats, etc.
 */
public interface StatisticDisplayer {

	/**
	 * Registered the specified <tt>Statistic</tt> for display,
	 * with the specified display name.
	 *
	 * @param stat the <tt>Statistic</tt> to register
	 * @param displayName the name for the statistic to display
	 */
	void registerStatistic(Statistic stat, String displayName);

	/**
	 * Registered the specified two <tt>Statistic</tt>s to be displayed.
	 * This can be useful, for example, for a custom stat display that
	 * is calculated from two other statistics.
	 *
	 * @param stat0 the first <tt>Statistic</tt> to register
	 * @param stat1 the second <tt>Statistic</tt> to register
	 * @param displayName the name for the statistic to display
	 */
	void registerDualStatistic(Statistic stat0, Statistic stat1, 
							   String displayName);

	/**
	 * Refresh the stat data.
	 */
	void refresh();

	/**
	 * Sets whether or not the stats for this pane should be written
	 * out to files.
	 *
	 * @param write specifies whether or not the stats should be written
	 */
	void setWriteStatsToFile(boolean write);

	/**
	 * Returns the component that displays the statistic.
	 *
	 * @return the component that displays the statistic
	 */
	JComponent getComponent();
}
