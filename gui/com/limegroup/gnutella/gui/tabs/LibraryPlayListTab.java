package com.limegroup.gnutella.gui.tabs;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.library.LibraryMediator;
import com.limegroup.gnutella.gui.util.DividerLocationSettingUpdater;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.UISettings;

/**
 * This class handles access to the tab that contains the library
 * as well as the playlist to the user.
 */
public final class LibraryPlayListTab extends AbstractTab {

	/**
	 * Constant for the <tt>Component</tt> instance containing the 
	 * elements of this tab.
	 */
	private static JComponent COMPONENT;
	private static JPanel PANEL = new JPanel(new BorderLayout());
	
	private static LibraryMediator LIBRARY_MEDIATOR;
	
	/**
	 * Constructs the elements of the tab.
	 *
	 * @param LIBRARY_MEDIATOR the <tt>LibraryMediator</tt> instance 
	 * @param PLAYLIST_MEDIATOR the <tt>PlayListMediator</tt> instance 
	 */
	public LibraryPlayListTab(final LibraryMediator lm) {
		super("LIBRARY", GUIMediator.LIBRARY_INDEX, "library_tab");
		LIBRARY_MEDIATOR = lm;
		setPlayerEnabled(GUIMediator.isPlaylistVisible());
	}

	public void storeState(boolean visible) {
        ApplicationSettings.LIBRARY_VIEW_ENABLED.setValue(visible);
	}

	public JComponent getComponent() {
		return PANEL;
	}
	
	public static void setPlayerEnabled(boolean value) {
		if (COMPONENT != null && value == COMPONENT instanceof JSplitPane)
			return;
		
		PANEL.removeAll();
		
		if (value) {
			JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
												LIBRARY_MEDIATOR.getComponent(), 
												GUIMediator.getPlayList().getComponent());
			divider.setOneTouchExpandable(true);
			new DividerLocationSettingUpdater(divider, 
					UISettings.UI_LIBRARY_PLAY_LIST_TAB_DIVIDER_LOCATION);
			COMPONENT = divider;
		} else
			COMPONENT = LIBRARY_MEDIATOR.getComponent();
		
		PANEL.add(COMPONENT, BorderLayout.CENTER);
		
		PANEL.invalidate();
		PANEL.validate();
	}
}
