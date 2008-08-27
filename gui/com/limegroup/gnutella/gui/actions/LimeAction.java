package com.limegroup.gnutella.gui.actions;
import javax.swing.Action;

/**
 * Extends Swing's action interface to provide more specific keys.
 */
public interface LimeAction extends Action {

	/**
	 * Short name for the action which should be complimentary to an icon.
	 * See {@link IconButton}.
	 */
	final static String SHORT_NAME = "LimeShortName";
	/**
	 * Name of the icon used when displaying this action. See {@link IconButton}.
	 */
	final static String ICON_NAME = "LimeIconName";
	
}
