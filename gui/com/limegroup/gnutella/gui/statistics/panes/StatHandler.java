package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Color;

import com.limegroup.gnutella.statistics.Statistic;

/**
 * Class for keeping track of message statistics, colors, etc. for
 * a given statistic.
 */
final class StatHandler {
	
	private final Statistic STATS;
	private final int[] Y_COORDS = new int[Statistic.HISTORY_LENGTH];
	private final Color COLOR;
	private final String NAME;

    /**
     * Creates a new <tt>StatHandler</tt> instance for the specified
     * <tt>Statistic</tt>, <tt>Color</tt>, and display name.
     *
     * @param stat the <tt>Statistic</tt> this is a handler for
     * @param lineColor the color for the statistic
     * @param displayName the display name for the statistic
     */
	StatHandler(Statistic stat, Color lineColor, String displayName) {
		STATS = stat;
		COLOR = lineColor;
		NAME = displayName;
	}
	
	/**
	 * Accessor for the display name of this graph statistic.
	 *
	 * @return the display name of this graph statistic
	 */
	public String getDisplayName() {
		return NAME;
	}

	/**
	 * Accessor for the <tt>Color</tt> of this statistic.
	 *
	 * @return the <tt>Color</tt> for the graph of this statistic
	 */
	public Color getColor() {
		return COLOR;
	}

	/**
	 * Accessor for the y-axis data for this statistic.
	 *
	 * @return the y-axis data for this statistic
	 */
	public int[] getData() {
		return Y_COORDS;
	}

	/**
	 * Accessor for the <tt>Statistic</tt> data.
	 *
	 * @return the <tt>Statistic</tt> data
	 */
	public Statistic getStat() {
		return STATS;
	}
	
	public String toString() {
		return "StatHandler: "+STATS.toString();
	}
}
