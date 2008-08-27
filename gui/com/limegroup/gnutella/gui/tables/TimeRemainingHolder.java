package com.limegroup.gnutella.gui.tables;

import com.limegroup.gnutella.gui.GUIUtils;


/**
 * simple class to store the numeric value of time remaining (or ETA)
 * used so we can sort by a value, but display a human-readable time.
 * @author sberlin
 */
public final class TimeRemainingHolder implements Comparable {
	
	private int _timeRemaining;
	
	public TimeRemainingHolder(int intValue) 
	{
		_timeRemaining = intValue;
	}
	
	public int compareTo(Object o) {
	    return ((TimeRemainingHolder)o)._timeRemaining - _timeRemaining;
	}
	
    public String toString()
    {
        //TODO: maybe make an EMPTY_STRING constant 
        //instead of allocating a blank string each time?
        return _timeRemaining == 0 ? "" : GUIUtils.seconds2time(_timeRemaining);
    }
}
