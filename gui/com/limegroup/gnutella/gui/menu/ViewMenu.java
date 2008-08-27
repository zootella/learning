package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.BooleanSetting;
import com.limegroup.gnutella.settings.UISettings;


/**
 * This class manages the "view" menu that allows the user to dynamically select
 * which tabs should be viewable at runtime & themes to use.
 */
final class ViewMenu extends AbstractMenu {
    
    private static final String SETTING_PROPERTY = "limewire.setting";
    
    ViewMenu(final String key) {
        super(key);
        MENU.add( new ShowHideMenu("VIEW_SHOW_HIDE").getMenu() );
        addSeparator();

        MENU.add( new ThemeMenu("VIEW_THEMES").getMenu() );
        
        MENU.addSeparator();
        ActionListener listener = new IconListener();
        JMenuItem item = addToggleMenuItem("VIEW_SMALL_ICONS", listener,
                                           UISettings.SMALL_ICONS.getValue());
        item.putClientProperty(SETTING_PROPERTY, UISettings.SMALL_ICONS);
        item = addToggleMenuItem("VIEW_TEXT_WITH_ICONS", listener,
                                 UISettings.TEXT_WITH_ICONS.getValue());
        item.putClientProperty(SETTING_PROPERTY, UISettings.TEXT_WITH_ICONS);
        MENU.addSeparator();
        
        MENU.add( new LanguageMenu("VIEW_LANGS").getMenu() );
    }
    
    private static class IconListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AbstractButton item = (AbstractButton)e.getSource();
            BooleanSetting setting = (BooleanSetting)item.getClientProperty(SETTING_PROPERTY);
            setting.setValue(item.isSelected());
            GUIMediator.instance().buttonViewChanged();
        }
   }
}







