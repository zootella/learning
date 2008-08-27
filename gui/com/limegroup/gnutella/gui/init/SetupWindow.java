package com.limegroup.gnutella.gui.init;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This abstract class creates a <tt>JPanel</tt> that uses 
 * <tt>BoxLayout</tt> for setup windows.  It defines many of the 
 * basic accessor and mutator methods required by subclasses.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
abstract class SetupWindow extends JPanel {

	/**
	 * The width of the setup window.
	 */
	public static final int SETUP_WIDTH = 500;

	/**
	 * The height of the setup window.
	 */
	public static final int SETUP_HEIGHT = 300;

	/**
	 * Margin for the outside of the setup panel.
	 */
	public static final int MARGIN = 10;	


	/**
	 * Margin for the left side of the setup window.
	 */
	private final int SETUP_LEFT_MARGIN = 20;

	/**
	 * Constant for the inner panel of a <tt>SetupWindow</tt>.
	 */
	private final JPanel INNER_PANEL = new BoxPanel(BoxPanel.Y_AXIS);;

	/**
	 * Variable for the name of this window for use with <tt>CardLayout</tt>.
	 */
	private String _key;
	
	/**
	 * Variable for the key of the label to display.
	 */
	private String _labelKey;

	/**
	 * Variable for the caption for the window.
	 */
	private String _name;

	/**
	 * Variable for the next window in the sequence.
	 */
	private SetupWindow _next;

	/**
	 * Variable for the previous window in the sequence.
	 */
	private SetupWindow _previous;

	/**
	 * Constant handle to the setup manager mediator class.
	 */
	protected final SetupManager _manager;

	/**
	 * Creates a new setup window with the specified label.
	 *
	 * @param key the title of the window for use with <tt>CardLayout</tt>
	 *  and for use in obtaining the locale-specific caption for this
	 *  window
	 * @param labelKey the key for locale-specific label to be displayed 
	 *  in the window
	 */
	SetupWindow(final SetupManager manager, final String key, 
				final String labelKey) {

		_manager = manager;
		_key = key;
		_labelKey = labelKey;
    }
    
    protected void createWindow() {
		removeAll();
		INNER_PANEL.removeAll();

		_name = GUIMediator.getStringResource(_key);
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		Border border = 
		    BorderFactory.createEmptyBorder(MARGIN * 2,
											MARGIN,
											MARGIN,
											MARGIN);

		Border innerBorder = 
		    BorderFactory.createEmptyBorder(0,
											SETUP_LEFT_MARGIN,
											0,
											0);
		setBorder(border);
		INNER_PANEL.setBorder(innerBorder);

		int totalMargin = MARGIN * 2;
		Dimension size 
		    = new Dimension(SETUP_WIDTH-totalMargin, 
							SETUP_HEIGHT-totalMargin);
		setPreferredSize(size);

		SetupLabel setupLabel = new SetupLabel(_labelKey);
		add(setupLabel);
		add(INNER_PANEL);
	}

	/** 
	 * Accessor for the name of the panel 
	 *
	 * @return the unique identifying name for this panel
	 */
	public String getName() {return _name;}

	/**
	 * Accessor for the unique identifying key of the window
	 * in the <tt>CardLayout</tt>.
	 *
	 * @return the unique identifying key for the window.
	 */
	public String getKey() {return _key;}
	
	/**
	 * Mutator for the labelKey.
	 */
	protected void setLabelKey(String newKey) {
	    _labelKey = newKey;
    }

	/**
	 * Accessor for the next panel in the sequence.
	 *
	 * @return the next window in the sequence
	 */
	public SetupWindow getNext() {return _next;}

	/**
	 * Accessor for the previous panel in the sequence.
	 *
	 * @return the previous window in the sequence
	 */
	public SetupWindow getPrevious() {return _previous;}

	/**
	 * Sets the next SetupWindow in the sequence.
	 *
	 * @param previous the window to set as the previous window
	 */
	public void setNext(SetupWindow next) {_next = next;}

	/**
	 * Sets the previous SetupWindow in the sequence.
	 *
	 * @param previous the window to set as the previous window
	 */
	public void setPrevious(SetupWindow previous) {_previous = previous;}

	/**
	 * Called each time this window is opened.
	 */
	public void handleWindowOpeningEvent() {
	    createWindow();

	    if( _next == this )
	        _manager.goToFinishButtons();
	    else if ( _previous == this )
	        _manager.goToNextButtons();
	    else
	        _manager.goToStandardButtons();
	    
    }

	/**
	 * Applies the settings currently set in this window.
	 *
	 * @throws ApplySettingsException if there was a problem applying the
	 *         settings
	 */
	public void applySettings() throws ApplySettingsException {}

	/**
	 * Adds a component to the inner panel of the window.
	 *
	 * @param setupComponent the <tt>Component</tt> to add to this window
	 */
	protected void addSetupComponent(Component setupComponent) {
		INNER_PANEL.add(setupComponent);
	}
}
