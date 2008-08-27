package com.limegroup.gnutella.gui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.limegroup.gnutella.gui.actions.LimeAction;
import com.limegroup.gnutella.settings.UISettings;

/**
 * A JButton that uses an Icon.
 */
public class IconButton extends JButton {
    
    private String message;
    private String iconName;
	/**
	 * The super constructors of JButton call {@link #updateUI()} before we
	 * had a chance to set our values. So ignore these calls in 
	 * {@link #updateButton()}.
	 */
    private boolean initialized;
	
	private PropertyChangeListener listener = null;
    
    /**
     * Constructs a new IconButton with the given text & icon name.
     */
    public IconButton(String text, String iconName) {
	    setRolloverEnabled(true);        
        this.iconName = iconName;
        this.message = text;
        initialized = true;
        updateButton();
    }
    
	/**
	 * Constructs an IconButton for an action.
	 * <p>
	 * Actions must provide a value for the key {@link LimeAction#ICON_NAME}
	 * and can provide a short name which is shown below the icon with
	 * {@link LimeAction#SHORT_NAME}. If the short name is not provided it'll
	 * fall back on {@link Action#NAME}.
	 * @param action
	 */
	public IconButton(Action action) {
		super(action);
		setRolloverEnabled(true);
		initialized = true;
	}
	
	/**
	 * Overriden for internal reasons, no api changes.
	 */
	public void setAction(Action a) {
		Action oldAction = getAction();
		if (oldAction != null) {
			oldAction.removePropertyChangeListener(getListener());
		}
		super.setAction(a);
		setButtonFromAction(a);
		a.addPropertyChangeListener(getListener());
	}
	
    private void setButtonFromAction(Action action) {
		iconName = (String)action.getValue(LimeAction.ICON_NAME);
		message = (String)action.getValue(LimeAction.SHORT_NAME);
		// fall back on Action.NAME
		if (message == null) {
			message = (String)action.getValue(Action.NAME);
		}
		updateButton();
	}
	
	private PropertyChangeListener getListener() {
		if (listener == null) {
			listener = new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					setButtonFromAction((Action)evt.getSource());
				}
				
			};
		}
		return listener;
	}

	/**
     * Updates the UI, possibly changing the icons or text.
     */
    public void updateUI() {
        super.updateUI();
        updateButton();
    }
    
    /**
     * Updates the text of the icon.
     */
    public void setText(String text) {
        message = text;
        updateButton();
    }
    
    /**
     * Updates the button.
     */
    private void updateButton() {
       if (!initialized) 
		   return;
        
        Icon icon = IconManager.instance().getIconForButton(iconName);
        if(icon == null) {
            super.setText(message);
            setVerticalTextPosition(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setContentAreaFilled(true);
            setBorderPainted(true);
            setOpaque(true);
        } else {
            super.setIcon(icon);
            Icon rollover =
                IconManager.instance().getRolloverIconForButton(iconName);
            super.setRolloverIcon(rollover);
            if(UISettings.TEXT_WITH_ICONS.getValue() &&
               message != null && !message.equals("")) {
                super.setText(message);
                setPreferredSize(null);
            } else {
                super.setText(null);
                int height = icon.getIconHeight() + 15;
                int width = icon.getIconWidth() + 15;
                if(message != null && message.equals("")) {
                    height = icon.getIconHeight();
                    width = icon.getIconWidth();
                }
                setPreferredSize(new Dimension(height, width));
            }
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
        }
    }
}
