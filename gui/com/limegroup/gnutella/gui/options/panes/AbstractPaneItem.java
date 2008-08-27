package com.limegroup.gnutella.gui.options.panes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.Box;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.TitledPaddedPanel;
import com.limegroup.gnutella.gui.options.OptionsMediator;

/**
 * This class provides a skeletal implementation of the <tt>PaneItem</tt>
 * interface.<p>
 *
 * It provides the basic implementation for displaying one option within
 * a larger window of options. Each <tt>AbstractPaneItem</tt> has a titled
 * border and a label describing the option.  The label is followed by
 * standardized spacing.<p>
 * 
 * It includes several convenience methods that subclasses may us to 
 * simplify panel construction.<p>
 *
 * Subclasses only need to override the applyOptions() method for storing
 * options to disk.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public abstract class AbstractPaneItem implements PaneItem {
	
	/**
	 * Handle to the mediator class for use of constants and for necessary 
	 * notifications of changes to other classes.
	 */
	protected static final OptionsMediator MEDIATOR = 
		OptionsMediator.instance();

	/**
	 * The container that elements in the pane are added to.
	 */
	private final TitledPaddedPanel CONTAINER = new TitledPaddedPanel();

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	private static final Component HORIZONTAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(6,0));

	/**
	 * Constant <tt>Component</tt> for use as a standard horizontal 
	 * separator.
	 */
	private static final Component VERTICAL_SEPARATOR = 
		Box.createRigidArea(new Dimension(0,6));
	
	
	/**
	 * This sole constructor overrides the the public accessibility of the 
	 * default constructor and is usually called implicitly.
	 *
	 * @param key the key for obtaining the locale-specific values for
	 *  displayed strings
	 */
	protected AbstractPaneItem(final String key) {
		String title = "OPTIONS_"+key+"_TITLE";
		String label = "OPTIONS_"+key+"_LABEL";
		CONTAINER.setTitle(GUIMediator.getStringResource(title));
		add(new PaneItemMainLabel(GUIMediator.getStringResource(label)).getLabel());
	}

	/**
	 * Implements the <tt>PaneItem</tt> interface. <p>
	 *
	 * Returns the <tt>Container</tt> for this set of options.
	 *
	 * @return the <tt>Container</tt> for this set of options
	 */
	public Container getContainer() {
		return CONTAINER;
	}

	/**
	 * Implements the <tt>PaneItem</tt> interface. <p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 *
	 * Subclasses must define this method to set their initial options 
	 * when the options window is shown.
	 */
	public abstract void initOptions();

	/**
	 * Implements the <tt>PaneItem</tt> interface. <p>
	 *
	 * Applies the options currently set in this <tt>PaneItem</tt>.<p>
	 *
	 * Subclasses must define this method to apply their specific options.
	 *
	 * @throws IOException if the options could not be fully applied
	 */
	public abstract boolean applyOptions() throws IOException;

	/**
	 * Adds the specified <tt>Component</tt> to the enclosed <tt>Container</tt> 
	 * instance.
	 *
	 * @param comp the <tt>Component</tt> to add
	 */
	protected final void add(Component comp) {
		CONTAINER.add(comp);
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for horizontal separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard horizontal
	 *         separator
	 */
	protected final Component getHorizontalSeparator() {
		return HORIZONTAL_SEPARATOR;
	}

	/**
	 * Returns a <tt>Component</tt> standardly sized for vertical separators.
	 *
	 * @return the constant <tt>Component</tt> used as a standard vertical
	 *         separator
	 */
	protected final Component getVerticalSeparator() {
		return VERTICAL_SEPARATOR;
	}
}
