package com.limegroup.gnutella.gui;

/**
 * Constants used by gui classes.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class GUIConstants {

	/**
	 * Constant for the locale-specific resource key for the icon for the plug.
	 */
	public static final String LIMEWIRE_ICON = "limeicon";

	/**
	 * The number of pixels in the margin of a padded panel.
	 */
	public static final int OUTER_MARGIN = 6;

	/**
	 * Standard number of pixels that should separate many 
	 * different types of gui components.
	 */
    public static final int SEPARATOR = 6;

    /**
	 * Strings for different connection speeds.
	 */
    public static final String MODEM_SPEED = 
		GUIMediator.getStringResource("MODEM_SPEED");
    public static final String CABLE_SPEED = 
		GUIMediator.getStringResource("CABLE_SPEED");
    public static final String T1_SPEED = 
		GUIMediator.getStringResource("T1_SPEED");
    public static final String T3_SPEED = 
		GUIMediator.getStringResource("T3_SPEED");
    public static final String MULTICAST_SPEED =
        GUIMediator.getStringResource("MULTICAST_SPEED");

	/** 
	 * the interval between statistics updates. 
	 */
	public static final int UPDATE_TIME = 2000;
}
