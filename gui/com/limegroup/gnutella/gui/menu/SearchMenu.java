package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.settings.BooleanSetting;
import com.limegroup.gnutella.settings.UISettings;

/**
 * Options for the search tab.
 */
final class SearchMenu extends AbstractMenu {
    

    /**
     * Constructs the SearchMenu options.
     *
     * @param key the key allowing the <tt>AbstractMenu</tt> superclass to
     *  access the appropriate locale-specific string resources
     */
    SearchMenu(final String key) {
        super(key);
        
        addToggleMenuItem("VIEW_SEARCH_FILTERS",
                    UISettings.SEARCH_RESULT_FILTERS);
                    
        addToggleMenuItem("VIEW_SEARCH_MAGNETMIX",
                    UISettings.MAGNETMIX_BUTTON);
    }
    
    private JMenuItem addToggleMenuItem(String key, BooleanSetting set) {
        return addToggleMenuItem(key, new Listener(set), set.getValue());
        
    }
    
    private static class Listener implements ActionListener {
        final BooleanSetting SETTING;
        
        Listener(BooleanSetting set) {
            SETTING = set;
        }
        
        public void actionPerformed(ActionEvent e) {
            AbstractButton b = (AbstractButton)e.getSource();
            if(SETTING.getValue() == b.isSelected())
                return;
                
            SETTING.setValue(b.isSelected());
            SearchMediator.rebuildInputPanel();
        }
    }
}