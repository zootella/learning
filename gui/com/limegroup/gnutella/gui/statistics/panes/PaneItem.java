package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.RefreshListener;

/**
 * An object that defines the basic functions of one <i>statistics item</i>, 
 * or one individual panel that displays a set of statistics to the 
 * user.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public interface PaneItem extends RefreshListener {
	
	/**
	 * Returns the <tt>Container</tt> for this set of statistics.
	 *
	 * @return the <tt>Container</tt> for this set of statistics
	 */
	Container getContainer();

    /**
     * Accessor for the component that displays the statistic -- without the
     * label or associated components.
	 *
	 * @return the <tt>JComponent</tt> instance that contains the stat 
	 *  display
     */
    JComponent getStatsComponent();

	/**
	 * Notifies the <tt>StatisticsPane</tt> that the component has been 
	 * resized.
	 *
	 * @param e the <tt>ComponentEvent</tt> that generated the resize
	 * @param comp the pane <tt>Component</tt>
	 */
    void componentResized(ComponentEvent e, Component comp);    
}
