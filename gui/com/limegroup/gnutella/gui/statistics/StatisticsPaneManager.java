package com.limegroup.gnutella.gui.statistics;

import java.awt.CardLayout;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.limegroup.gnutella.gui.RefreshListener;
import com.limegroup.gnutella.gui.statistics.panes.PaneItem;

/**
 * Manages the main statistics window that displays the various statistics 
 * windows.<p>
 *
 * This class also stores all of the main statistics panels to access
 * all of them regardless of how many there are or what their
 * specific type is.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class StatisticsPaneManager implements RefreshListener {

	/**
	 * Constant for the main panel of the statistics window.
	 */
	private final JPanel MAIN_PANEL = new JPanel();

	/**
	 * Constant for the <tt>CardLayout</tt> used in the main panel.
	 */
	private final CardLayout CARD_LAYOUT = new CardLayout();


	/**
	 * Constant for the <tt>ArrayList</tt> containing all of the visible
	 * <tt>StatisticsPane</tt> instances.
	 */
	private final Map STATISTICS_PANE_MAP = new HashMap();


    private String _selectedPaneKey;
	
	/**
	 * The constructor sets the layout and adds all of the 
	 * <tt>StatisticsPane</tt> instances.
	 */
	public StatisticsPaneManager() {
		MAIN_PANEL.setLayout(CARD_LAYOUT);		
    }

	/**
	 * Shows the window speficied by its title.
	 * 
	 * @param name the name of the <code>Component</code> to show
	 */
	public void show(final String name) {
		CARD_LAYOUT.show(MAIN_PANEL, name);
        _selectedPaneKey = name;
	}
	
	/**
	 * Returns the main <code>JComponent</code> for this class.
	 *
	 * @return a <code>JComponent</code> instance that is the main component
	 *         for this class.
	 */
	public JComponent getComponent() {
		return MAIN_PANEL;
	}

    public PaneItem getFirstDisplayedPaneItem() {
        StatisticsPane pane = 
            (StatisticsPane)STATISTICS_PANE_MAP.get(_selectedPaneKey);
        return pane.getFirstPaneItem();
    }

	/**
	 * Adds the speficied window to the CardLayout based on its title.
	 *
	 * @param window the <code>StatisticsPane</code> to add
	 */
	public void addPane(final StatisticsPane pane) {
		MAIN_PANEL.add(pane.getComponent(), pane.getName());
        STATISTICS_PANE_MAP.put(pane.getName(), pane);
	}

	/**
	 * Refreshes all statistics panes.
	 */
	public void refresh() {
        Iterator iter = STATISTICS_PANE_MAP.values().iterator();
        while(iter.hasNext()) {
            ((StatisticsPane)iter.next()).refresh();            
        }
	}

    public void componentResized(ComponentEvent e) {
        Iterator iter = STATISTICS_PANE_MAP.values().iterator();
        while(iter.hasNext()) {
            // notify all panes, as they all need to layout their
            // labels again
            ((StatisticsPane)iter.next()).componentResized(e, MAIN_PANEL);            
        }
    }

}
