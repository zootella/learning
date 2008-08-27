package com.limegroup.gnutella.gui.connection;




/**
 * Wrapper class that acts as a comparable for the dropped i/o info.
 * @author sam berlin
 */
public final class DroppedHolder implements Comparable {
	
	/**
	 * Variable for the string representation
	 */
	private String _string;

	/**
	 * Variable for the info.
	 */
	private int _in, _out;

	/**
	 * The constructor sets i / o
	 *
	 */
	public DroppedHolder(float i, float o) {	    
	    _in = Math.min(100, (int)(i*100));
	    _out = Math.min(100, (int)(o*100));
		_string = Integer.toString(_in) + "% / " + Integer.toString(_out) + "%";
	}
	
	/**
	 * Add up the two things and see which is larger.
	 */
	public int compareTo(Object o) {
	    DroppedHolder other = (DroppedHolder)o;
	    return ( 
	      ( _in + _out) -
	      (other._in + other._out)
	    );	    
	}

	/**
	 *
	 * @return the formatted string
	 */
	public String toString() {
		return _string;
	}
}
