package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.statistics.StatisticsMediator;

/**
 * This class displays information about advanced statistics, allowing
 * the user to selectively display them.
 */
public final class AdvancedStats extends AbstractOptionPaneItem {
	
	/**
	 * Constant for the key of the locale-specific <tt>String</tt> for the 
	 * PLAYER enabled check box label in the options window.
	 */
	private final String CHECK_BOX_LABEL = 
		"STATS_ADVANCED_CHECK_BOX_LABEL";

	/**
	 * Constant for the check box that specifies whether or not downloads 
	 * should be automatically cleared.
	 */
	private final JCheckBox CHECK_BOX = new JCheckBox();

	/**
	 * Creates a new graph that displays total downstream bandwidth.
	 * 
	 * @param key the key for obtaining label string resources
	 */
	public AdvancedStats() {
		super("ADVANCED_PANE");
		CHECK_BOX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    boolean sel = CHECK_BOX.isSelected();
                StatisticsMediator.instance().setAdvancedStatsVisible(sel);
			}
		});
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
													 CHECK_BOX);
		add(comp.getComponent());
	}
}
