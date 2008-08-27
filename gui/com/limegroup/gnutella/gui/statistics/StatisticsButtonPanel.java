package com.limegroup.gnutella.gui.statistics;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.ButtonRow;

/**
 * This class contains the <tt>ButtonRow</tt> instance for the statistics 
 * window.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class StatisticsButtonPanel {

	/**
	 * Handle to the enclosed <tt>ButtonRow</tt> instance.
	 */
	private ButtonRow _buttonRow;

	/**
	 * The constructor creates the <tt>ButtonRow</tt>.
	 */
	StatisticsButtonPanel() {
        String[] buttonLabelKeys = {
			"GENERAL_CLOSE_BUTTON_LABEL",
		};

        String[] toolTipKeys = {
			"GENERAL_CLOSE_BUTTON_TIP",
		};
        ActionListener[] listeners = {
			new OKListener()
		};
		_buttonRow= new ButtonRow(buttonLabelKeys,toolTipKeys,listeners,
								  ButtonRow.X_AXIS,ButtonRow.LEFT_GLUE);
			
	}

	/**
	 * Returns the <tt>Component</tt> that contains the <tt>ButtonRow</tt>.
	 */
	Component getComponent() {
		return _buttonRow;
	}

    /** 
	 * The listener for the ok button.  Applies the current statistics and 
	 * makes the window not visible.
	 */
    private class OKListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            StatisticsMediator.instance().setStatisticsVisible(false);
        }
    }
}
