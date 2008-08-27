package com.limegroup.gnutella.gui.tabs;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;

/**
 * This class provided a rudimentary implementation of key functions for
 * any tab in the primary window.  
 */
abstract class AbstractTab implements Tab, ThemeObserver {
	
	/**
	 * Constant for the title of this tab.
	 */
	private final String TITLE;
	
	/**
	 * Constant for the tool tip for this tab.
	 */
	private final String TOOL_TIP;

	/**
	 * <tt>Icon</tt> instance to use for this tab.
	 */
	private Icon _icon;

	/**
	 * Constant for the unique key for the specific tab instance.
	 */
	private final String ICON_FILE;

	/**
	 * Constant for the index of this tab.
	 */
	protected final int INDEX;

	/**
	 * Constructs the elements of the tab.
	 */
	AbstractTab(final String KEY, final int INDEX, final String ICON) {
		this.INDEX     = INDEX;
		this.TITLE     = GUIMediator.getStringResource(KEY+"_TITLE");
		this.TOOL_TIP  = GUIMediator.getStringResource(KEY+"_TIP");
		this.ICON_FILE = ICON;
		this._icon     = GUIMediator.getThemeImage(ICON_FILE);
		ThemeMediator.addThemeObserver(this);
	}

	// inherit doc comment
	public void updateTheme() {
		_icon = GUIMediator.getThemeImage(ICON_FILE);
		GUIMediator.instance().updateTabIcon(INDEX);
	}

	public abstract void storeState(boolean state);
	
	public abstract JComponent getComponent();

	public String getTitle() {
		return TITLE;
	}

	public String getToolTip() {
		return TOOL_TIP;
	}

	public Icon getIcon() {
		return _icon;
	}

	public int getIndex() {
		return INDEX;
	}

	public String toString() {
		return TITLE + " tab";
	}
	
	public void mouseClicked() {}
	    
}
