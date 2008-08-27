package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.statistics.Statistic;

/**
 * Class for keeping track of message statistics, colors, etc. for
 * a given statistic.
 */
final class AverageStatHandler {
	
	private final Statistic BYTE_STAT;
	private final Statistic NUMBER_STAT;
	private final int[] Y_COORDS = new int[Statistic.HISTORY_LENGTH];
	private final String NAME;

	AverageStatHandler(Statistic totalBytes, Statistic totalMessages,
					   String displayName) {
		BYTE_STAT = totalBytes;
		NUMBER_STAT = totalMessages;
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
	 * Accessor for the y-axis data for this statistic.
	 *
	 * @return the y-axis data for this statistic
	 */
	public int[] getData() {
		return Y_COORDS;
	}

	/**
	 * Accessor for the <tt>Statistic</tt> byte data.
	 *
	 * @return the <tt>Statistic</tt> byte data
	 */
	public Statistic getByteStat() {
		return BYTE_STAT;
	}

	/**
	 * Accessor for the <tt>Statistic</tt> byte data.
	 *
	 * @return the <tt>Statistic</tt> byte data
	 */
	public Statistic getNumberStat() {
		return NUMBER_STAT;
	}
	
	// overrides Object.toString
	public String toString() {
		return "AverageStatHandler: "+BYTE_STAT.toString()+" "+NUMBER_STAT.toString();
	}
}
