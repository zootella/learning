package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.SpeedConstants;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to only allow search results of specific speeds.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class SearchSpeedPaneItem extends AbstractPaneItem {

	/**
	 * Constant handle to the radio button for only showing T3 and higher
	 */
	private final JRadioButton T3_BUTTON = new JRadioButton();

	/**
	 * Constant handle to the radio button for only showing T1 and higher
	 */
	private final JRadioButton T1_BUTTON = new JRadioButton();

	/**
	 * Constant handle to the radio button for only showing cable/dsl 
	 * and higher
	 */
	private final JRadioButton CABLE_BUTTON = new JRadioButton();

	/**
	 * Constant handle to the radio button for showing all results.
	 */
	private final JRadioButton ALL_BUTTON = new JRadioButton();


	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public SearchSpeedPaneItem(final String key) {
		super(key);

		String t3LabelKey    = "OPTIONS_SEARCH_SPEED_T3_LABEL";
		String t1LabelKey    = "OPTIONS_SEARCH_SPEED_T1_LABEL";
		String cableLabelKey = "OPTIONS_SEARCH_SPEED_CABLE_LABEL";
		String allLabelKey   = "OPTIONS_SEARCH_SPEED_ALL_LABEL";

		String t3Label    = GUIMediator.getStringResource(t3LabelKey);
		String t1Label    = GUIMediator.getStringResource(t1LabelKey);
		String cableLabel = GUIMediator.getStringResource(cableLabelKey);
		String allLabel   = GUIMediator.getStringResource(allLabelKey);

		T3_BUTTON.setText(t3Label);
		T1_BUTTON.setText(t1Label);
		CABLE_BUTTON.setText(cableLabel);
		ALL_BUTTON.setText(allLabel);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(T3_BUTTON);
		group.add(T1_BUTTON);
		group.add(CABLE_BUTTON);
		group.add(ALL_BUTTON);

		BoxPanel buttonPanel = new BoxPanel();
		buttonPanel.add(T3_BUTTON);
		buttonPanel.add(T1_BUTTON);
		buttonPanel.add(CABLE_BUTTON);
		buttonPanel.add(ALL_BUTTON);

		BoxPanel mainPanel = new BoxPanel(BoxPanel.X_AXIS);
		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createHorizontalGlue());

		add(mainPanel);
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
		int minSpeed = SearchSettings.MINIMUM_SEARCH_SPEED.getValue();
		switch(minSpeed) {
		case SpeedConstants.T3_SPEED_INT:
			T3_BUTTON.setSelected(true);
			break;
		case SpeedConstants.T1_SPEED_INT:
			T1_BUTTON.setSelected(true);
			break;
		case SpeedConstants.CABLE_SPEED_INT:
			CABLE_BUTTON.setSelected(true);
			break;
		default:
			ALL_BUTTON.setSelected(true);
			break;
		}
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Applies the options currently set in this window, displaying an
	 * error message to the user if a setting could not be applied.
	 *
	 * @throws IOException if the options could not be applied for some reason
	 */
	public boolean applyOptions() throws IOException {
		int speed = 3;
		if(T3_BUTTON.isSelected()) {
			speed = SpeedConstants.T3_SPEED_INT;
		} else if(T1_BUTTON.isSelected()) {
			speed = SpeedConstants.T1_SPEED_INT;
		} else if(CABLE_BUTTON.isSelected()) {
			speed = SpeedConstants.CABLE_SPEED_INT;
		} else {
			speed = 0;
		}			
		SearchSettings.MINIMUM_SEARCH_SPEED.setValue(speed);
        return false;
	}
	
    public boolean isDirty() {
        switch(SearchSettings.MINIMUM_SEARCH_SPEED.getValue()) {
        case SpeedConstants.T3_SPEED_INT:
            return !T3_BUTTON.isSelected();
        case SpeedConstants.T1_SPEED_INT:
            return !T1_BUTTON.isSelected();
        case SpeedConstants.CABLE_SPEED_INT:
            return !CABLE_BUTTON.isSelected();
        case 0:
            return !ALL_BUTTON.isSelected();
        default:
            return true;
        }
    }
}
