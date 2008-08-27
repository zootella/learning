package com.limegroup.gnutella.gui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.actions.ActionUtils;
import com.limegroup.gnutella.gui.actions.BitziLookupAction;
import com.limegroup.gnutella.gui.actions.CopyMagnetLinkToClipboardAction;
import com.limegroup.gnutella.gui.actions.SearchAction;
import com.limegroup.gnutella.util.StringUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;

/**
 * The search result menu.
 */
final class SearchResultMenu {
    
    final ResultPanel PANEL;

    /**
     * Private constructor to ensure that this class can never be
     * created.
     */
    SearchResultMenu(ResultPanel rp) {
        PANEL = rp;
    }
    
    private static void add(String s, ActionListener l,
                            JPopupMenu m, boolean enable) {
        JMenuItem item = new JMenuItem(s);
        item.addActionListener(l);
        item.setEnabled(enable);
        m.add(item);
    }
    
    /**
     * Creates the JPopupMenu.
     */
    JPopupMenu createMenu(TableLine[] lines) {
        JPopupMenu menu = new JPopupMenu();
        
        add(SearchMediator.DOWNLOAD_STRING, PANEL.DOWNLOAD_LISTENER,
            menu, lines.length > 0);
		add(GUIMediator.getStringResource("SEARCH_DOWNLOAD_AS_LABEL"),
				PANEL.DOWNLOAD_AS_LISTENER, menu, lines.length == 1);
        add(GUIMediator.getStringResource("LICENSE_VIEW_LICENSE"), new LicenseListener(),
            menu, lines.length > 0 &&  lines[0].isLicenseAvailable());
        add(SearchMediator.CHAT_STRING, PANEL.CHAT_LISTENER,
            menu, lines.length > 0 && lines[0].isChatEnabled());
        add(SearchMediator.BROWSE_HOST_STRING, PANEL.BROWSE_HOST_LISTENER,
            menu, lines.length > 0 && lines[0].isBrowseHostEnabled());
        add(SearchMediator.BLOCK_STRING, new BlockListener(),
            menu, lines.length > 0);

        /////////////////////////////
        menu.addSeparator();
		
		TableLine line = lines.length > 0 ? lines[0] : null;
		menu.add(createSearchAgainMenu(line));
		menu.add(createAdvancedMenu(line));
		
		menu.addSeparator();
        
        add(SearchMediator.STOP_STRING, PANEL.STOP_LISTENER,
            menu, !PANEL.isStopped());
        add(SearchMediator.KILL_STRING, new CancelListener(),
            menu, PANEL.isKillable());

        return menu;
    }
    
    private JMenu createSearchAgainMenu(TableLine line) {
		JMenu menu = new JMenu(GUIMediator.getStringResource
				("SEARCH_RESULT_MENU_SEARCH_MORE_LABEL"));
		menu.add(new JMenuItem(new RepeatSearchAction()));
		
		if (line == null) {
			menu.setEnabled(PANEL.isRepeatSearchEnabled());
			return menu;
		}
		
		menu.addSeparator();
		String keywords = StringUtils.createQueryString(line.getFilename());
		SearchInformation info = SearchInformation.createKeywordSearch
			(keywords, null, MediaType.getAnyTypeMediaType());
		if (SearchMediator.validateInfo(info) == SearchMediator.QUERY_VALID) {
			menu.add(new JMenuItem(new SearchAction(info, "SEARCH_FOR_KEYWORDS_ACTION_NAME")));
		}
		
		LimeXMLDocument doc = line.getXMLDocument();
		if (doc != null) {
			Action[] actions = ActionUtils.createSearchActions(doc);
			for (int i = 0; i < actions.length; i++) {
				menu.add(new JMenuItem(actions[i]));
			}
		}
		
		return menu;
	}
	
	private JMenu createAdvancedMenu(TableLine line) {
		JMenu menu = new JMenu(GUIMediator.getStringResource
				("GENERAL_ADVANCED_SUB_MENU"));
		
		if (line == null) {
			menu.setEnabled(false);
			return menu;
		}
		
		BitziLookupAction bitziAction = new BitziLookupAction(PANEL);
		bitziAction.setEnabled(line.getRemoteFileDesc().getSHA1Urn() != null);
		menu.add(new JMenuItem(bitziAction));
		
		CopyMagnetLinkToClipboardAction magnet =
			new CopyMagnetLinkToClipboardAction(PANEL);
		magnet.setEnabled(line.hasNonFirewalledRFD());
		menu.add(new JMenuItem(magnet));
		
		// launch action
        if(line.isLaunchable()) {
            menu.addSeparator();
			add(SearchMediator.LAUNCH_STRING, PANEL.DOWNLOAD_LISTENER, 
					menu.getPopupMenu(), true);
        }

		
		return menu;
	}
	
	private class LicenseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PANEL.showLicense();
        }
    }
    
    private class BlockListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PANEL.blockHost();
        }
    }
    
    private class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            SearchMediator.killSearch();
        }
    }
    
    private class RepeatSearchAction extends AbstractAction {
		
		public RepeatSearchAction() {
			putValue(Action.NAME, SearchMediator.REPEAT_SEARCH_STRING);
			setEnabled(PANEL.isRepeatSearchEnabled());
		}
		
		public void actionPerformed(ActionEvent e) {
            PANEL.repeatSearch();
        }
    }
}
