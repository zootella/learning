package com.limegroup.gnutella.statistics;

import com.limegroup.gnutella.util.IntBuffer;

/**
 * Interface for generalized access to a <tt>Statistic</tt>.
 */
public interface Statistic {

	/**
	 * Constant for the number of records to hold for each statistic.
	 */
	public static final int HISTORY_LENGTH = 200;

	/**
	 * Accessor for the total number of this statistic recorded.
	 *
	 * @return the total of this statistic recorded, regardless of any
	 *  time increments
	 */
	double getTotal();

	/**
	 * Accessor for the average number of this statistic type received 
	 * per recording time period.
	 *
	 * @return the average number of this statistic type received 
	 *  per recording time period
	 */
	double getAverage();

	/**
	 * Accessor for the maximum recorded stat value over all recorded
	 * time periods.
	 *
	 * @return the maximum recorded stat value over all recorded
	 *  time periods
	 */
	double getMax();
	
	/**
	 * Accessor for the current recorded stat value over the most recent
	 * time period.
	 * 
	 * @return the stat value current being added to
	 */
	int getCurrent();
	
	/**
	 * Accessor for the most recently recorded stat value.
	 * 
	 * @return the most recently recorded stat value
	 */
	int getLastStored();
	
	/**
	 * Increments this statistic by one.
	 */
	void incrementStat();

	/**
	 * Add the specified number to the current recording for this statistic.
	 * This is the equivalent of calling incrementStat <tt>data</tt> 
	 * times.
	 *
	 * @param data the number to increment the current statistic
	 */
	void addData(int data);

	/**
	 * Accessor for the <tt>Integer</tt> array of all statistics recorded
	 * over a discrete interval.  Note that this has a finite size, so only
	 * a fixed size array will be returned.
	 *
	 * @return the <tt>Integer</tt> array for all statistics recorded for
	 *  this statistic
	 */
	IntBuffer getStatHistory();	
	
	/**
	 * Clears the current data stored in this statistic.
	 * Useful for statistics that want to be analyzed repeatedly
	 * in a single session, starting from scratch each time.
	 */
	void clearData();

	/**
	 * Stores the current set of gathered statistics into the history set,
	 * setting the currently recorded data back to zero.
	 */
	void storeCurrentStat();

	/**
	 * Sets whether or not to write this <tt>Statistic</tt> out to a file.
	 * If it does write to a file, the file name is automatically generated
	 * from the name of the class, which should easily label the data.
	 * All data is written in comma-delimited format.
	 *
	 * @param write whether or not to write the data to a file
	 */
	void setWriteStatToFile(boolean write);
}
