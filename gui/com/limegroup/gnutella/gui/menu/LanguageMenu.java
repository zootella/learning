package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LanguageInfo;


/**
 * The menu to be used for choosing a language.
 */
final class LanguageMenu extends AbstractMenu {
    
    /**
     * The listener for changing the language.
     */
    private static final ActionListener LANGUAGE_CHANGER =
        new LanguageChangeListener();
    
    /**
     * The ButtonGroup to store the language options in.
     */
    private static final ButtonGroup GROUP = new ButtonGroup();
    
    /**
     * The property that the language is stored in.
     */
    private static final String LANGUAGE_PROPERTY = "LANGUAGE";
    
    /**
     * Constructs the menu.
     */
    LanguageMenu(String key) {
        super(key);
		getMenu().addMenuListener(new SelectionListener());
    }
    
	private void populateMenu() {
		addMenuItem("VIEW_LANGS_TRANSLATE_LIMEWIRE", new TranslateListener());
		addSeparator();
        
        LanguageInfo[] langs = LanguageInfo.getLanguages(AbstractMenu.FONT);
        for(int i = 0; i < langs.length; i++) {
            JMenuItem item = new JRadioButtonMenuItem(langs[i].toString());
            item.setFont(AbstractMenu.FONT);
            item.putClientProperty(LANGUAGE_PROPERTY, langs[i]);
            item.addActionListener(LANGUAGE_CHANGER);
            if(langs[i].isCurrent())
                item.setSelected(true);
            MENU.add(item);
            GROUP.add(item);            
        }
	}
	
	/**
	 * Opens the browser to the limewire translation page.
	 */
	private static class TranslateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            String url = "http://www.limewire.org/translate.shtml";
			GUIMediator.openURL(url);
		}
	}    
    
    /**
     * ActionListener to change the theme based on the client property.
     */
    protected static class LanguageChangeListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            LanguageInfo lang = 
                (LanguageInfo)item.getClientProperty(LANGUAGE_PROPERTY);
            if(!lang.isCurrent()) {
                lang.apply();
                GUIMediator.showMessage("MENU_VIEW_LANGS_RESTART_REQUIRED");
            }
        }
    }

	/**
	 * Class that listens for the popup menu to be selected and populates it
	 * before the menu's popup menu actually becomes visible.
	 * <p>
	 * {@link PopupMenuListener} did not work on MAC OS.
	 */
	private class SelectionListener implements MenuListener {

		/**
		 * Only populate menu when it is shown for the first time.
		 */
		private boolean shown = false;
		
		public void menuSelected(MenuEvent e) {
			if (!shown) {
				shown = true;
				populateMenu();
			}
		}

		public void menuDeselected(MenuEvent e) {
		}

		public void menuCanceled(MenuEvent e) {
		}
		
	}
}
        