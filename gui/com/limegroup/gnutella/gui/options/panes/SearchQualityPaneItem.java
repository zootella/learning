package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to select the quality of search results to display to the user.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class SearchQualityPaneItem extends AbstractPaneItem {

	/**
	 * Constant handle to the radio button for selecting only four star
	 * results.
	 */
	private final JRadioButton FOUR_STAR_BUTTON = new JRadioButton();

	/**
	 * Constant handle to the radio button for selecting only three and four 
	 * star results.
	 */
	private final JRadioButton THREE_AND_FOUR_STAR_BUTTON = 
		new JRadioButton();

	/**
	 * Constant handle to the radio button for selecting only two, three and 
	 * four star results.
	 */
	private final JRadioButton TWO_THREE_AND_FOUR_STAR_BUTTON = 
		new JRadioButton();

	/**
	 * Constant handle to the radio button for showing all results.
	 */
	private final JRadioButton ALL_RESULTS_BUTTON = new JRadioButton();

	/**
	 * The stored value to allow rolling back changes.
	 */

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public SearchQualityPaneItem(final String key) {
		super(key);

		String fourStarLabelKey = 
		    "OPTIONS_SEARCH_QUALITY_FOUR_STAR_LABEL";
		String threeAndFourStarLabelKey = 
		    "OPTIONS_SEARCH_QUALITY_THREE_AND_FOUR_STAR_LABEL";
		String twoThreeAndFourStarLabelKey = 
		    "OPTIONS_SEARCH_QUALITY_TWO_THREE_AND_FOUR_STAR_LABEL";
		String showAllResultsLabelKey =
		    "OPTIONS_SEARCH_QUALITY_ALL_RESULTS_LABEL";

		String fourStarLabel = 
		    GUIMediator.getStringResource(fourStarLabelKey);
		String threeAndFourStarLabel = 
		    GUIMediator.getStringResource(threeAndFourStarLabelKey);
		String twoThreeAndFourStarLabel = 
		    GUIMediator.getStringResource(twoThreeAndFourStarLabelKey);
		String showAllResultsLabel = 
		    GUIMediator.getStringResource(showAllResultsLabelKey);

		FOUR_STAR_BUTTON.setText(fourStarLabel);
		THREE_AND_FOUR_STAR_BUTTON.setText(threeAndFourStarLabel);
		TWO_THREE_AND_FOUR_STAR_BUTTON.setText(twoThreeAndFourStarLabel);
		ALL_RESULTS_BUTTON.setText(showAllResultsLabel);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(FOUR_STAR_BUTTON);
		group.add(THREE_AND_FOUR_STAR_BUTTON);
		group.add(TWO_THREE_AND_FOUR_STAR_BUTTON);
		group.add(ALL_RESULTS_BUTTON);

		BoxPanel buttonPanel = new BoxPanel();
		buttonPanel.add(FOUR_STAR_BUTTON);
		buttonPanel.add(THREE_AND_FOUR_STAR_BUTTON);
		buttonPanel.add(TWO_THREE_AND_FOUR_STAR_BUTTON);
		buttonPanel.add(ALL_RESULTS_BUTTON);

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
		int minQuality = SearchSettings.MINIMUM_SEARCH_QUALITY.getValue();
		switch(minQuality) {
		case 3:
			FOUR_STAR_BUTTON.setSelected(true);
			break;
		case 2:
			THREE_AND_FOUR_STAR_BUTTON.setSelected(true);
			break;
		case 1:
			TWO_THREE_AND_FOUR_STAR_BUTTON.setSelected(true);
			break;
		default:
			ALL_RESULTS_BUTTON.setSelected(true);
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
		int quality = 3;
		if(FOUR_STAR_BUTTON.isSelected()) {
			quality = 3;
		} else if(THREE_AND_FOUR_STAR_BUTTON.isSelected()) {
			quality = 2;
		} else if(TWO_THREE_AND_FOUR_STAR_BUTTON.isSelected()) {
			quality = 1;
		} else {
			quality = 0;
		}			
        SearchSettings.MINIMUM_SEARCH_QUALITY.setValue(quality);
        return false;
	}
	
	public boolean isDirty() {
	    switch(SearchSettings.MINIMUM_SEARCH_QUALITY.getValue()) {
	    case 3: return !FOUR_STAR_BUTTON.isSelected();
	    case 2: return !THREE_AND_FOUR_STAR_BUTTON.isSelected();
	    case 1: return !TWO_THREE_AND_FOUR_STAR_BUTTON.isSelected();
	    case 0: return !ALL_RESULTS_BUTTON.isSelected();
	    default: return true;
	    }
    }
}
