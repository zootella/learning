package com.limegroup.gnutella.gui.init;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This class displays a button in the setup window for going
 * forward to the next window.  It also handles responding to
 * mouse clicks and forwarding the appropriate message.
 */
final class NextButton extends JButton implements ActionListener {

	/**
	 * Reference to the manager to forward events to.
	 */
	private SetupManager _setupManager;

	/**
	 * The constructor set the reference to the setup mediator
	 * class and adds the action listener.
	 *
	 * @param setupManager the setup mediator class
	 */	
	NextButton(SetupManager setupManager) {
		super(GUIMediator.getStringResource("GENERAL_NEXT_BUTTON_LABEL"));
		addActionListener(this);
		_setupManager = setupManager;
	}
	
	/**
	 * Implements the <tt>ActionListener</tt> interface.
	 *
	 * Notifies the setup manager that it should take 
	 * the appropriate action.
	 */
	public void actionPerformed(ActionEvent e) {
        _setupManager.next();
	}
	
}
