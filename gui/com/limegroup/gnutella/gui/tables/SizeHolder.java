package com.limegroup.gnutella.gui.tables;

import com.limegroup.gnutella.gui.GUIUtils;


/**
 * Wrapper class that holds on to the size integer for a file so that 
 * we don't have to read it from disk every time while sorting.
 */
public final class SizeHolder implements Comparable {
	
	/**
	 * Variable for the string representation of the file size.
	 */
	private String _string;

	/**
	 * Variable for the size of the file in kilobytes.
	 */
	private int _size;

	/**
	 * The constructor sets the size and string variables, creating a
	 * formatted string in kilobytes from the size value.
	 *
	 * @param size the size of the file in kilobytes
	 */
	public SizeHolder(int size) {
		_string = GUIUtils.toUnitbytes(size);
		_size = size;
	}
	
	public int compareTo(Object o) {
	    int otherSize = ((SizeHolder)o).getSize();
	    return _size - otherSize;
	}

	/**
	 * Returns the string value of this size, formatted with commas and
	 * "KB" appended to the end.
	 *
	 * @return the formatted string representing the size
	 */
	public String toString() {
		return _string;
	}

	/**
	 * Sets the size held.
	 * 
	 * @param size the new size to hold
	 */
	public void setSize(int size) {
		if(size == _size) return;
		_string = GUIUtils.toUnitbytes(size);
		_size = size;
	}

	/**
	 * Returns the size of the file in kilobytes.
	 *
	 * @return the size of the file in kilobytes
	 */
	public int getSize() {
		return _size;
	}
}
