package com.limegroup.gnutella.gui.statistics;

import java.awt.Component;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.gui.statistics.panes.PaneItem;

/**
 * An object that defines the basic functionality of an <i>StatisticsPane</i>,
 * or one panel specifying a set of statistics in the statistics window.<p>
 * 
 * Each <tt>StatisticsPane</tt> has a unique identifying name that allows it
 * to be displayed in the <tt>CardLayout</tt>.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public interface StatisticsPane extends RefreshListener {
	
	/**
	 * Returns the name of this <tt>StatisticsPane</tt>.
	 *
	 * @return the name of this <tt>StatisticsPane</tt>
	 */
	String getName();

	/**
	 * Returns the <tt>Container</tt> instance that holds the different 
	 * elements of this <tt>StatisticsPane</tt>.
	 *
	 * @return the <tt>Container</tt> associated with this 
	 *  <tt>StatisticsPane</tt>
	 */
	JComponent getComponent();

	/**
	 * Adds a new option item to this pane.
	 *
	 * @param item the <tt>PaneItem</tt> instance to add to this 
	 *             <tt>StatisticsPane</tt>
	 */
	void add(PaneItem item);

	/**
	 * Returns whether or not to display this pane.
	 *
	 * @return <tt>true</tt> if the pane should be displayed, otherwise
	 *  <tt>false</tt>
	 */
	boolean display();

	/**
	 * Returns the first <tt>PaneItem</tt> in this pane.
	 *
	 * @return the first <tt>PaneItem</tt> in this pane
	 */
    PaneItem getFirstPaneItem();

	/**
	 * Notifies the <tt>StatisticsPane</tt> that the component has been 
	 * resized.
	 *
	 * @param e the <tt>ComponentEvent</tt> that generated the resize
	 * @param comp the pane <tt>Component</tt>
	 */
    void componentResized(ComponentEvent e, Component comp);
}
