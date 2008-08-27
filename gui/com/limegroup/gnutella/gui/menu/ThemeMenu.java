package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * The menu to be used for themes.
 */
final class ThemeMenu extends AbstractMenu {
    
    /**
     * The client property to use for theme changing items.
     */
    private static final String THEME_PROPERTY = "THEME_NAME";
    
    /**
     * The client property to use for theme changing when using 'other' L&Fs.
     */
    private static final String THEME_CLASSNAME = "THEME_CLASSNAME";
    
    /**
     * The listener for changing the theme.
     */
    private static final ActionListener THEME_CHANGER =
        new ThemeChangeListener();
    
    /**
     * The ButtonGroup to store the theme options in.
     */
    private static final ButtonGroup GROUP = new ButtonGroup();
    
    /**
     * Constructs the menu.
     */
    ThemeMenu(String key) {
        super(key);
        
        addMenuItem("VIEW_THEMES_GET_MORE", new GetThemesListener());
        addMenuItem("VIEW_THEMES_REFRESH", new RefreshThemesListener());
        
        JMenuItem def = addMenuItem("VIEW_THEMES_USE_DEFAULT", THEME_CHANGER);            
        final Object defaultVal = ThemeSettings.THEME_DEFAULT.getAbsolutePath();
        def.putClientProperty(THEME_PROPERTY, defaultVal);
        
        // Add a listener to set the new theme as selected.
        def.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                setSelection(defaultVal);
            }
        });
        addSeparator();
        addThemeItems();
    }
    
    /**
     * Sets the default theme.
     */
    private static void setSelection(Object value) {
        Enumeration items = GROUP.getElements();
        while(items.hasMoreElements()) {
            JMenuItem item = (JMenuItem)items.nextElement();
            if(value.equals(item.getClientProperty(THEME_PROPERTY))) {
                item.setSelected(true);
                break;
            }
        }
    }        
    
    /**
     * Scans through the theme directory for .lwtp files & adds them
     * as menu items to the menu.
     */ 
    private void addThemeItems() {
        File themeDir = ThemeSettings.THEME_DIR_FILE;
        if(!themeDir.exists()) return;
       
        List allThemes = new LinkedList(Arrays.asList(themeDir.list(new ThemeFileFilter())));
        addInstalledLFs(allThemes);
        Collections.sort(allThemes, new ThemeComparator());
        
        if(allThemes.isEmpty())
            return;
            
        String otherClassName = ThemeSettings.getOtherLF();
        
        for(Iterator i = allThemes.iterator(); i.hasNext(); ) {
            Object next = i.next();
            File themeFile;
            JMenuItem theme;
            
            if(next instanceof String) {
                themeFile = new File(themeDir, (String)next);
                theme = new JRadioButtonMenuItem(ThemeSettings.formatName(themeFile.getName()));
                if( themeFile.equals(ThemeSettings.THEME_FILE.getValue()) )
                    theme.setSelected(true);
            } else {
                themeFile = new File(themeDir, ThemeSettings.OTHER_THEME_NAME);
                UIManager.LookAndFeelInfo lfi = (UIManager.LookAndFeelInfo)next;
                theme = new JRadioButtonMenuItem(lfi.getName());
                if( themeFile.equals(ThemeSettings.THEME_FILE.getValue()) &&
                    otherClassName != null && lfi.getClassName().equals(otherClassName) )
                    theme.setSelected(true);
                theme.putClientProperty(THEME_CLASSNAME, lfi.getClassName());
            }
                
            theme.setFont(AbstractMenu.FONT);
            GROUP.add(theme);
            theme.addActionListener(THEME_CHANGER);
            theme.putClientProperty(THEME_PROPERTY, themeFile.getAbsolutePath());
            MENU.add(theme);
        }
    }
    
    /**
     * Removes all items in the group from the menu.  Used for refreshing.
     */
    private void removeThemeItems() {
        Enumeration items = GROUP.getElements();
        List removed = new LinkedList();
        while(items.hasMoreElements()) {
            JMenuItem item = (JMenuItem)items.nextElement();
            MENU.remove(item);
            removed.add(item);
        }
        
        for(Iterator itr = removed.iterator(); itr.hasNext();)
            GROUP.remove((JMenuItem)itr.next());
    }
    
    /**
     * Opens the themes page in the default browser, displaying
     * an error message if the browser could not be launched 
     * successfully.
     */
    private static class GetThemesListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		String url = "http://www.limewire.com/skins2";
    	    GUIMediator.openURL(url);
    	}
    }
    
    /**
     * Refreshes the theme menu options to those on the disk.
     */
    private class RefreshThemesListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
            removeThemeItems();
            addThemeItems();
    	}
    }    
    
    /**
     * ActionListener to change the theme based on the client property.
     */
    protected static class ThemeChangeListener implements ActionListener {    
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            String themePath = (String)item.getClientProperty(THEME_PROPERTY);
            String className = (String)item.getClientProperty(THEME_CLASSNAME);
    	    ThemeMediator.changeTheme(new File(themePath), className);
        }
    }
    
    /**
     * Simple class to sort the theme lists.
     */
    private static class ThemeComparator implements Comparator {
        public int compare(Object a, Object b) {
            String name1, name2;
            if(a instanceof String)
                name1 = ThemeSettings.formatName((String)a);
            else
                name1 = ((UIManager.LookAndFeelInfo)a).getName();
                
            if(b instanceof String)
                name2 = ThemeSettings.formatName((String)b);
            else
                name2 = ((UIManager.LookAndFeelInfo)b).getName();

            return name1.compareTo(name2);
        }
    }
    
    /**
     * Adds installed LFs to the list.
     */
    private static void addInstalledLFs(List themes) {
        UIManager.LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
        if(lfs == null)
            return;
            
        for(int i = 0; i < lfs.length; i++) {
            UIManager.LookAndFeelInfo l = lfs[i];
            if(l.getClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"))
                continue;
            if(l.getClassName().startsWith("apple"))
                continue;
            if(l.getClassName().equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel") &&
               CommonUtils.isLinux() && CommonUtils.isJava15OrLater())
                continue;
            if(l.getClassName().equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel"))
                continue;
                
            themes.add(l);
        }
    }
    
    /**
     * <tt>FileNameFilter</tt> class for only displaying theme file types.
     */
    public static class ThemeFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            // don't allow anything that isn't a theme file
            if(!name.endsWith(ThemeSettings.EXTENSION))
                return false;
                
            // if this is one of the old 'default_X' themes
            // we used to ship with, ignore it.
            if(name.startsWith("default_"))
                return false;
                
            // don't allow the 'other' theme to show.
            if(name.equals(ThemeSettings.OTHER_THEME_NAME))
                return false;
                
            // only allow the osx theme if we're on osx.
            if(!CommonUtils.isMacOSX() && 
              name.equals(ThemeSettings.PINSTRIPES_OSX_THEME_NAME))
                return false;
                
            // only allow the brushed metal theme if we're on
            // osx with 10.3 or above w/ java 1.4.2+.
            if(name.equals(ThemeSettings.BRUSHED_METAL_OSX_THEME_NAME) &&
               (!CommonUtils.isPantherOrAbove() ||
                !CommonUtils.isJava14OrLater()))
                return false;
                
            // only allow the windows theme if we're on windows.
            if(!CommonUtils.isWindows() &&
              name.equals(ThemeSettings.WINDOWS_LAF_THEME_NAME))
                return false;
            
            // only show pro theme if we're on pro.
            if(!CommonUtils.isPro() &&
               name.equals(ThemeSettings.PRO_THEME_NAME))
                return false;
                
            // only show GTK theme on linux with 1.5  
            if(name.equals(ThemeSettings.GTK_LAF_THEME_NAME) &&   
              (!CommonUtils.isLinux() ||  
               !CommonUtils.isJava15OrLater()))  
                return false;  

            // everything's okay -- allow it.                
            return true;
        }
    }
    
}
