package com.limegroup.gnutella.gui.statistics;

import java.awt.Component;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.statistics.StatisticsManager;

/**
 * This class acts as a mediator for the different components of the statistics
 * window.  This class maintains references to the 
 * <tt>StatisticsTreeManager</tt> and <tt>StatisticsPaneManager</tt>, the two
 * primary classes that it delegates to.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class StatisticsMediator implements RefreshListener, ThemeObserver {

	/**
	 * Constant for the key for the root node in the tree.
	 */
	public final static String ROOT_NODE_KEY = "STATISTICS_ROOT_NODE";

	/**
	 * Singleton constant for easy access to the statistics mediator.
	 */
	private final static StatisticsMediator INSTANCE =
		new StatisticsMediator();
	
	/**
	 * Constant for the class that manages the tree that controls which 
	 * option window is currently selected.  It is fine to construct these 
	 * her since they do not reference this class.
	 */
	private static StatisticsTreeManager _treeManager = null;

	/**
	 * Constant for the class that manages the current statistics pane 
	 * displayed to the user.  It is fine to construct these here since 
	 * they do not reference this class.
	 */
	private static StatisticsPaneManager _paneManager = null;

	/**
	 * Constant for the class that handles constructing all of the 
	 * elements of the statistics windows.
	 */
	private static StatisticsConstructor _constructor = null;


	/** 
	 * Singleton accessor for this class. 
	 *
	 * @return the <tt>StatisticsMediator</tt> instance
	 */
	public static synchronized StatisticsMediator instance() {
		return INSTANCE;
	}

	/** 
	 * Private constructor to ensure that this class cannot be constructed 
	 * from another class.  The constructor does very little to alleviate
	 * construction conflicts with classes that may use the mediator.
	 */
	private StatisticsMediator() {
		GUIMediator.setSplashScreenString(
		    GUIMediator.getStringResource("SPLASH_STATUS_STATISTICS_WINDOW"));
	}

	/**
	 * Makes the statistics window either visible or not visible depending 
	 * on the boolean argument.
	 *
	 * @param visible <tt>boolean</tt> value specifying whether the 
	 *                statistics window should be made visible or not 
	 *                visible
	 */
	public final void setStatisticsVisible(boolean visible) {
        if(_constructor == null) {
            if(!visible)
                return;
            updateTheme();
        }
		_constructor.setStatisticsVisible(visible);
	}


    /** 
	 * Returns whether or not the statistics window is visible.
	 * 
     * @return <tt>true</tt> is the statistics window is visible,
	 *  <tt>false</tt> otherwise
     */
    public final boolean isStatisticsVisible() {
        if(_constructor == null)
            return false;
        return _constructor.isStatisticsVisible();
    }


	/**
	 * Handles the selection of a new panel as the currently visible panel.
	 *
	 * @param key the unique identifying key of the panel to show
	 */
	public final void handleSelection(final String key) {
		_paneManager.show(key);
	}
		
	/**
	 * Returns the main <tt>JComponent</tt> instance for the statistics window,
	 * allowing other components to position themselves accordingly.
	 *
	 * @return the main statistics <tt>JComponent</tt> window
	 */
	public Component getMainStatisticsComponent() {
        if(_constructor == null)
            updateTheme();
	    return StatisticsConstructor.getMainComponent();
	}

	public JComponent getComponent() {
        if(_constructor == null)
            updateTheme();
		return StatisticsConstructor.getComponent();
	}

    /**
     * Accessor for the component that contains the displayed statistics,
     * as opposed to the navigational component.
     *
     * @return the component that contains the displayed statistics,
     *  as opposed to the navigational component
     */
    public static JComponent getStatDisplayComponent() {
        if(_constructor == null)
            INSTANCE.updateTheme();
        return StatisticsConstructor.getStatDisplayComponent();
    }

	/**
	 * Implements <tt>RefreshListener</tt>.<p>
	 *
	 * Refreshes all statistics.
	 */
	public void refresh() {		
		if(this.isStatisticsVisible()) {
			_paneManager.refresh();
		}
	}

	/**
	 * Sets the visibility state of the advanced statistics.
	 *
	 * @param visible the visibility state to apply
	 */
	public void setAdvancedStatsVisible(boolean visible) {
        if(_constructor == null)
            updateTheme();
		StatisticsManager.instance().setRecordAdvancedStats(visible);
		StatisticsConstructor.setAdvancedStatsVisible(visible);
	}
    
    /**
     * Updates the theme by reconstructing the necessary components.
     */
    public void updateTheme() {
        _treeManager = new StatisticsTreeManager();
        _paneManager = new StatisticsPaneManager();
        _constructor = new StatisticsConstructor(_treeManager, _paneManager);
	}


//    	public static void main(String args[]) {
//    		StatisticsMediator mediator = StatisticsMediator.instance();
//    		mediator.setStatisticsVisible(true);
//    	}
}


