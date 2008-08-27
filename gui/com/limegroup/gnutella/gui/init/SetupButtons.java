package com.limegroup.gnutella.gui.init;

import java.awt.CardLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class handles the panel of buttons at the bottom of the setup 
 * window.  The panel has two states -- the standard state with a "Back,"
 * a "Next," and a "Cancel" button, and a "Finish" state with a "Finish" 
 * button.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class SetupButtons extends JPanel {

	/**
	 * The number of pixels separating buttons.
	 */
    private final int BUTTON_SEP = 6;

	/**
	 * Title for the standard setup buttons panel
	 */
	private final String STANDARD_BUTTON_TITLE = "Standard Buttons";
	
	/**
	 * Title for the next setup buttons panel
	 */
	private final String NEXT_BUTTON_TITLE = "Next";

	/**
	 * Title for the setup buttons panel with only the "finish" button
	 */
	private final String FINISH_BUTTON_TITLE = "Finish Button";

	/**
	 * Title for the setup buttons panel with only the "cancel" button
	 */
	private final String CANCEL_BUTTON_TITLE = "Cancel Button";

	/**
	 * Variable for the card layout so that we can make calls on it later.
	 */
	private final CardLayout CARD_LAYOUT = new CardLayout(); 

	/**
	 * Flag for whether or not the standard buttons are currently shown.
	 */
	private boolean _standardButtonsShown = true;

	/**
	 * Creates the back, next, cancel, and finish buttons for the setup
	 * window.  It also creates a second cancel button for when the 
	 * button panel only has a cancel button.  
	 *
	 * @param manager the <tt>SetupManager</tt> instance so that the
	 *                buttons can make callbacks 
	 */
	SetupButtons(SetupManager manager) {
		setLayout(CARD_LAYOUT);
		reconstruct(manager);
    }
    
    /**
     * (Re)creates the button panels.
     */
    void reconstruct(SetupManager manager) {
		// create the button panels
		JPanel standardButtonPanel = new SetupButtonPanel();
		JPanel finishButtonPanel   = new SetupButtonPanel();
		JPanel cancelButtonPanel   = new SetupButtonPanel();
		JPanel nextButtonPanel = new SetupButtonPanel();

		// create the standard button panel
		JButton backButton   = new BackButton(manager);
		JButton nextButton   = new NextButton(manager);
		JButton cancelButton = new CancelButton(manager);
		standardButtonPanel.add(Box.createHorizontalGlue());
		standardButtonPanel.add(backButton);
		standardButtonPanel.add(Box.createHorizontalStrut(BUTTON_SEP));
		standardButtonPanel.add(nextButton);
		standardButtonPanel.add(Box.createHorizontalStrut(12));
		standardButtonPanel.add(cancelButton);
		
		// create the next button panel
		JButton nextButton1   = new NextButton(manager);
		JButton cancelButton1 = new CancelButton(manager);
		nextButtonPanel.add(Box.createHorizontalGlue());
		nextButtonPanel.add(nextButton1);
		nextButtonPanel.add(Box.createHorizontalStrut(12));
		nextButtonPanel.add(cancelButton1);		

		// create the finish button panel
		JButton finishButton = new FinishButton(manager);
		finishButtonPanel.add(Box.createHorizontalGlue());
		finishButtonPanel.add(finishButton);

		// create the cancel button panel
		JButton cancelButton2 = new CancelButton(manager);
		cancelButtonPanel.add(Box.createHorizontalGlue());
		cancelButtonPanel.add(cancelButton2);
		
		removeAll();

		// add the different button options to the card layout
		add(standardButtonPanel, STANDARD_BUTTON_TITLE);
		add(finishButtonPanel, FINISH_BUTTON_TITLE);
		add(cancelButtonPanel, CANCEL_BUTTON_TITLE);
		add(nextButtonPanel, NEXT_BUTTON_TITLE);
	}

	/**
	 * Switches the button panel displayed to be the button panel with
	 * the standard button array
	 */
	void goToStandardButtons() {
		if(!_standardButtonsShown) {
			CARD_LAYOUT.show(this, STANDARD_BUTTON_TITLE);
			_standardButtonsShown = true;
		}
	}	

	/**
	 * Switches the button panel displayed to be the button panel with
	 * only a finish button.
	 */
	void goToFinishButtons() {
		CARD_LAYOUT.show(this, FINISH_BUTTON_TITLE);
		_standardButtonsShown = false;
	}	

	/**
	 * Switches the button panel displayed to be the button panel with
	 * only a cancel button.
	 */
	void goToCancelButtons() {
		CARD_LAYOUT.show(this, CANCEL_BUTTON_TITLE);
		_standardButtonsShown = false;
	}
	
	/**
	 * Switches the button panel displayed to be the button panel with
	 * only a 'next' and 'cancel' button.
	 */
	void goToNextButtons() {
	    CARD_LAYOUT.show(this, NEXT_BUTTON_TITLE);
	    _standardButtonsShown = false;
    }
}
