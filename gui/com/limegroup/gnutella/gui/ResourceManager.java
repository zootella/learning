package com.limegroup.gnutella.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.lang.reflect.InvocationTargetException;


import com.limegroup.gnutella.gui.themes.LimeLookAndFeel;
import com.limegroup.gnutella.gui.themes.LimePlasticTheme;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.Expand;

/**
 * Manages application resources, including the custom <tt>LookAndFeel</tt>,
 * the locale-specific <tt>String</tt> instances, and any <tt>Icon</tt>
 * instances needed by the application.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class ResourceManager {

    /**
     * Instance of this <tt>ResourceManager</tt>, following singleton.
     */
    private static ResourceManager _instance;

    /**
     * The constant name of the native Windows library.
     */
    private static final String WINDOWS_LIBRARY_NAME = "LimeWire20";
    
    /**
     * Constant for the relative path of the gui directory.
     */
    private static final String GUI_PATH =
        "com/limegroup/gnutella/gui/";

    /**
     * Constant for the relative path of the resources directory.
     */
    private static final String RESOURCES_PATH =
        GUI_PATH + "resources/";

    /**
     * Constant for the relative path of the images directory.
     */
    private static final String IMAGES_PATH =
        GUI_PATH + "images/";

    /**
     * Boolean status that controls whever the shared <tt>Locale</tt> instance
     * needs to be loaded, and locale-specific options need to be setup.
     */
    private static boolean _localeOptionsSet;

    /**
     * Static variable for the loaded <tt>Locale</tt> instance.
     */
    private static Locale _locale;

    /**
     * The <tt>ResourceBundle</tt> instance to use for loading 
     * locale-specific resources.
     */
    private static ResourceBundle _resourceBundle;

    /**
     * Locale-specific option, set from the loaded resource bundle, and that
     * control the preferred appearence and layout of the GUI for the loaded
     * locale.  Complex scripts (such as Chinese, Korean, Japanese) should
     * not be displayed with bold characters if they are small (below 12
     * 12 points).
     */
    private static boolean _useBold;

    /**
     * Locale-specific option, set from the loaded resource bundle, and that
     * control the preferred appearence and layout of the GUI for the loaded
     * locale.  Semitic scripts (such as Hebrew, Arabic, Urdu, Farsi, Divehi)
     * and Thai should be used with a right-to-left directionality in the GUI
     * layout. Needed here to extend Swing with Java 1.1.8 (on Mac OS 8/9),
     * which does not handle java.awt.ComponentOrientation.
     */
    private static boolean _useLeftToRight;

    /**
     * Boolean for whether or not the installer has been shared.
     */
    private static boolean _installerShared = false;
    
    /**
     * Boolean for whether or not the font-size has been reduced.
     */
    private static boolean _fontReduced = false;
    
    /**
     * The default MetalTheme.
     */
    private static MetalTheme _defaultTheme = null;
    
    /**
     * Whether or not LimeWire was started in the 'brushed metal' 
     * look.
     */
    private final boolean BRUSHED_METAL;
    
    /**
     * Whether or not the WINDOWS_LIBRARY was able to load.
     */
    private final boolean LOADED_TRAY_LIBRARY;

    /** Cache of theme images (name as String -> image as ImageIcon) */
    private static final HashMap THEME_IMAGES = new HashMap();

    /**
     * Statically initialize necessary resources.
     */
    static {
        resetLocaleOptions();
    }

    static void resetLocaleOptions() {
        _localeOptionsSet = false;
        setLocaleOptions();
    }

    static void setLocaleOptions() {
        if (!_localeOptionsSet) {
            if(ApplicationSettings.LANGUAGE.getValue().equals(""))
                ApplicationSettings.LANGUAGE.setValue("en");
            _locale = new Locale(
                ApplicationSettings.LANGUAGE.getValue(),
                ApplicationSettings.COUNTRY.getValue(),
                ApplicationSettings.LOCALE_VARIANT.getValue());
            _resourceBundle = ResourceBundle.getBundle(
                "MessagesBundle", _locale);
                
            _useBold = !
                Boolean.valueOf(getStringResource("DISABLE_BOLD_CHARACTERS")).
                booleanValue();
            _useLeftToRight = !
                Boolean.valueOf(getStringResource("LAYOUT_RIGHT_TO_LEFT")).
                booleanValue();
            _localeOptionsSet = true;
        }
    }

    /**
     * Returns the <tt>Locale</tt> instance currently in use.
     *
     * @return the <tt>Locale</tt> instance currently in use
     */
    static Locale getLocale() {
        return _locale;
    }
    
    /**
     * Determines whether or not the current locale language is English.
     * Note that the user setting may be empty, defaulting to the running
     * system locale which may be other than English. Here we check the
     * effective locale seen in the MessagesBundle.
     */
    static boolean hasLocalizedTipsOfTheDay() {
        return Boolean.valueOf(getStringResource("HAS_TIPS_OF_THE_DAY")).booleanValue();
    }
    
    /**
     * Returns the TOTD resource bundle.
     */
    static ResourceBundle getTOTDResourceBundle() {
        return ResourceBundle.getBundle("totd/TOTD", _locale);
    }

    
    /**
     * Returns the XML resource bundle for the given schema.
     * @param String schema name 
     *        (not the URI but name returned by LimeXMLSchema.getDisplayString)
     * @return ResourceBundle
     */
    static ResourceBundle getXMLResourceBundle(String name) {
        return ResourceBundle.getBundle("xml.display." + name, _locale);
    }
    

    /**
     * Determines whever or not bold style can safely be used in the current
     * locale for displaying text with a size lower than 1 pica (12 points).
     *
     * @return <tt>true</tt> if bold style can safely be used in this
     *  locale (simple scripts), <tt>false</tt> otherwise (complex scripts)
     */
    static final boolean useBold() {
        setLocaleOptions();
        return _useBold;
    }

    /**
     * Determines whever the standard left-to-right orientation should be used
     * in the current locale.
     *
     * @return <tt>true</tt> if bold characters should be used in this
     *  locale, <tt>false</tt> otherwise (Semitic scripts).
     */
    static final boolean isLeftToRight() {
        setLocaleOptions();
        return _useLeftToRight;
    }

    /**
     * Returns the locale-specific String from the resource manager.
     *
     * @return an internationalized <tt>String</tt> instance
     *  corresponding with the <tt>resourceKey</tt>
     */
    static final String getStringResource(final String resourceKey) {
        return _resourceBundle.getString(resourceKey);
    }

    /**
     * Serves as a single point of access for any icons that should be accessed
     * directly from the file system for themes.
     *
     * @param name The name of the image (excluding the extension) to locate.
     * @return a new <tt>ImageIcon</tt> instance for the specified file,
     *  or <tt>null</tt> if the resource could not be loaded
     */
    static final ImageIcon getThemeImage(final String name) {
        if(name == null)
            throw new NullPointerException("null image name");
        
        ImageIcon icon = null;

    	// First try to get theme image from cache
    	icon = (ImageIcon)THEME_IMAGES.get(name);
    	if(icon != null)
    	    return icon;

        File themeDir = ThemeSettings.THEME_DIR.getValue();
        
        // Next try to get from themes.
        icon = getImageFromURL(new File(themeDir, name).getPath(), true);
        if(icon != null && icon.getImage() != null) {
    	    THEME_IMAGES.put(name, icon);
            return icon;
    	}

        // Then try to get from com/limegroup/gnutella/images resources
        icon = getImageFromURL(IMAGES_PATH + name, false);
        if(icon != null && icon.getImage() != null) {
    	    THEME_IMAGES.put(name, icon);
            return icon;
    	}
        
        // no resource?  error.
        throw new MissingResourceException(
                "image: " + name + " doesn't exist.", null, null);
        
    }
    
    /**
     * Retrieves an icon from the specified path in the filesystem.
     */
    static final ImageIcon getImageFromPath(String loc) {
        return getImageFromURL(loc, true);
    }
    
    /**
     * Retrieves an icon from a URL-style path.
     *
     * If 'file' is true, location is treated as a file, otherwise
     * it is treated as a resource.
     *
     * This tries, in order, the exact location, the location as a png,
     * and the location as a gif.
     */
    private static final ImageIcon getImageFromURL(String location, boolean file) {
        // no theme, try backup image.
        URL img = toURL(location, file);
        if(img != null)
            return new ImageIcon(img);
            
        // try with png
        img = toURL(location + ".png", file);
        if(img != null)
            return new ImageIcon(img);
            
        // try with gif
        img = toURL(location + ".gif", file);
        if(img != null)
            return new ImageIcon(img);
            
        return null;
    }
    
    /**
     * Makes a URL out of a location, as either a file or a resource.
     */
    private static final URL toURL(String location, boolean file) {
        if(file) {
            File f = new File(location);
            if(f.exists()) {
                try {
                    return f.toURL();
                } catch(MalformedURLException murl) {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return getURL(location);
        }
    }


    /**
     * Returns a new <tt>URL</tt> instance for the specified file in the
     * "resources" directory.
     *
     * @param FILE_NAME the name of the resource file
     * @return a new <tt>URL</tt> instance for the desired file, or
     *  <tt>null</tt> if the <tt>URL</tt> could not be loaded
     */
    static URL getURLResource(final String FILE_NAME) {
        return ResourceManager.getURL(RESOURCES_PATH + FILE_NAME);
    }    

    /**
     * Returns a new <tt>URL</tt> instance for the resource at the
     * specified local path.  The path should be the full path within
     * the jar file, such as: <p>
     * 
     * com/limegroup/gnutella/gui/images/searching.gif<p>
     *
     * @param PATH the path to the resource file within the jar
     * @return a new <tt>URL</tt> instance for the desired file, or
     *  <tt>null</tt> if the <tt>URL</tt> could not be loaded
     */
    private static URL getURL(final String PATH) {
        ClassLoader cl = ResourceManager.class.getClassLoader();
        if (cl ==  null) {
            return ClassLoader.getSystemResource(PATH);
        }
        URL url = cl.getResource(PATH);
        if (url == null) {
            return ClassLoader.getSystemResource(PATH);
        }
        return url;
    }

    /**
     * Instance accessor following singleton.
     */
    public static final ResourceManager instance() {
        if (_instance == null)
            _instance = new ResourceManager();
        return _instance;
    }


    /**
     * Private constructor to ensure that a <tt>ResourceManager</tt> 
     * cannot be constructed from outside this class.
     */
    private ResourceManager() {
        GUIMediator.setSplashScreenString(
            GUIMediator.getStringResource("SPLASH_STATUS_RESOURCE_MANAGER"));
            
        if(!ThemeFileHandler.isCurrent() || !ThemeSettings.isValid()) {
            ThemeSettings.THEME_FILE.revertToDefault();
            ThemeSettings.THEME_DIR.revertToDefault();
            ThemeFileHandler.reload();
        }
        
		String bMetal = System.getProperty("apple.awt.brushMetalLook");
		BRUSHED_METAL = bMetal != null && bMetal.equalsIgnoreCase("true");

        themeChanged();
        try {
            validateLocaleAndFonts();
        } catch(NullPointerException npe) {
            // ignore, can't do much about it -- internal ignorable error.
        }

        // The Windows library is not stored in any jar, and is
        // already in the appropriate location, so copying the resource
        // file is pointless (and doesn't work also).
        if (CommonUtils.isWindows()) {
            boolean loaded = false;
            try {
                System.loadLibrary(WINDOWS_LIBRARY_NAME);
                loaded = true;
            } catch(UnsatisfiedLinkError ule) {}
            LOADED_TRAY_LIBRARY = loaded;
        } else if (CommonUtils.isLinux()){
        	boolean loaded = false;
        	try {
        		System.loadLibrary("tray");
        		loaded = true;
        	} catch (UnsatisfiedLinkError ule){}
        	LOADED_TRAY_LIBRARY = loaded;
        }else {
            LOADED_TRAY_LIBRARY = false;
        }
            
        try {
            unpackWarFiles();
            unpackVersionFile();
        } catch(IOException e) {
            GUIMediator.showInternalError(e);
        }
    }
    
    /**
     * Validates the locale, determining if the current locale's resources
     * can be displayed using the current fonts.  If not, then the locale
     * is reset to English.
     *
     * This prevents the UI from appearing as all boxes.
     */
    public void validateLocaleAndFonts() {
        // OSX can always display everything, and if it can't,
        // we have no way of correcting things 'cause canDisplayUpTo
        // is broken on it.
        if(CommonUtils.isMacOSX())
            return;

        String s = getStringResource("LOCALE_LANGUAGE_NAME");
        if(!checkUIFonts("dialog", s)) {
            // if it couldn't display, revert the locale to english.
            ApplicationSettings.LANGUAGE.setValue("en");
            ApplicationSettings.COUNTRY.setValue("");
            ApplicationSettings.LOCALE_VARIANT.setValue("");
            resetLocaleOptions();
        }
        
        // Ensure that the Table.font can always display intl characters
        // since we can always get i18n stuff there, but only if we'd actually
        // be capable of displaying an intl character with the font...
        // unicode string == country name of simplified chinese
        String i18n = "\u4e2d\u56fd";
        checkFont("TextField.font", "dialog", i18n, true);
        checkFont("Table.font", "dialog", i18n, true);
        checkFont("ProgressBar.font", "dialog", i18n, true);
        checkFont("TabbedPane.font", "dialog", i18n, true);
    }
    
    /**
     * Alters all Fonts in UIManager to use Dialog, to correctly display
     * foreign strings.
     */
    private boolean  checkUIFonts(String newFont, String testString) {
        String[] comps = new String[] {
            "TextField.font",
            "PasswordField.font",
            "TextArea.font",
            "TextPane.font", 
            "EditorPane.font", 
            "FormattedTextField.font", 
            "Button.font", 
            "CheckBox.font", 
            "RadioButton.font", 
            "ToggleButton.font", 
            "ProgressBar.font", 
            "ComboBox.font", 
            "InternalFrame.titleFont", 
            "DesktopIcon.font", 
            "TitledBorder.font", 
            "Label.font",
            "List.font", 
            "TabbedPane.font",
            "Table.font",
            "TableHeader.font", 
            "MenuBar.font", 
            "Menu.font", 
            "Menu.acceleratorFont", 
            "MenuItem.font", 
            "MenuItem.acceleratorFont", 
            "PopupMenu.font", 
            "CheckBoxMenuItem.font", 
            "CheckBoxMenuItem.acceleratorFont", 
            "RadioButtonMenuItem.font", 
            "RadioButtonMenuItem.acceleratorFont", 
            "Spinner.font", 
            "Tree.font", 
            "ToolBar.font", 
            "OptionPane.messageFont", 
            "OptionPane.buttonFont",
            "ToolTip.font", 
        };
        
        boolean displayable = false;
        for(int i = 0; i < comps.length; i++)
            displayable |= checkFont(comps[i], newFont, testString, false);
        
        // Then do it the automagic way.
        // note that this could work all the time (without requiring the above)
        // if Java 1.4 didn't introduce Locales, and it could even still work
        // if they offered a way to get all the keys of possible resources.
        for(Iterator i = UIManager.getDefaults().entrySet().iterator(); i.hasNext(); ) {
            Map.Entry next = (Map.Entry)i.next();
            if(next.getValue() instanceof Font) {
                Font f = (Font)next.getValue();
                if(f != null && !newFont.equalsIgnoreCase(f.getName())) {
                    if(!GUIUtils.canDisplay(f, testString)) {
                        f = new Font(newFont, f.getStyle(), f.getSize());
                        if(GUIUtils.canDisplay(f, testString)) {
                            next.setValue(f);
                            displayable = true;
                        }
                    }
                }
            }
        }
        
        return displayable;
    }
    
    /**
     * Updates the font of a given fontName to be newName.
     */
    private boolean checkFont(String fontName, String newName, String testString, boolean force) {
        boolean displayable = true;
        Font f = UIManager.getFont(fontName);
        if(f != null && !newName.equalsIgnoreCase(f.getName())) {
            if(!GUIUtils.canDisplay(f, testString) || force) {
                f = new Font(newName, f.getStyle(), f.getSize());
                if(GUIUtils.canDisplay(f, testString))
                    UIManager.put(fontName, f);
                else
                    displayable = false;
            }
        } else if (f != null) {
            displayable = GUIUtils.canDisplay(f, testString);
        } else {
            displayable = false;
        }
        return displayable;
    }
    
    /**
     * Determines if the tray library has loaded.
     */
    public boolean isTrayLibraryLoaded() {
        return LOADED_TRAY_LIBRARY;
    }
    
    /**
     * Determines if the brushed metal property is set.
     */
    public boolean isBrushedMetalSet() {
        return BRUSHED_METAL;
    }

    /**
     * Updates to the current theme.
     */
    public void themeChanged() {
	    THEME_IMAGES.clear();
        try {
            if(ThemeSettings.isOtherTheme()) {
                // in case this is using metal ...
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                if(_defaultTheme != null)
                    MetalLookAndFeel.setCurrentTheme(_defaultTheme);

                String other = ThemeSettings.getOtherLF();
                UIManager.setLookAndFeel(other);
            } else if(ThemeSettings.isNativeTheme()) {
                if(CommonUtils.isWindows() && isPlasticWindowsAvailable()) {
                    try {
                        UIManager.setLookAndFeel("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");
                    } catch (NullPointerException npe) {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());   
                    }
                } else
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                if(CommonUtils.isMacOSX()) {
                    if(!_fontReduced) {
                        _fontReduced = true;
                        reduceFont("Label.font");
                        reduceFont("Table.font");
                    }
                    
                    if(CommonUtils.isJava14OrLater() && !CommonUtils.isJava15OrLater()) {
                        UIManager.put("List.focusCellHighlightBorder",  BorderFactory.createEmptyBorder(1, 1, 1, 1));
                        UIManager.put("ScrollPane.border", BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
                    }
                    
                }
            } else {
                if(isPlasticAvailable()) {
                    if(_defaultTheme == null)
                        _defaultTheme = getDefaultTheme();
                        
                    LimePlasticTheme.installThisTheme();
                    UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
                    LimeLookAndFeel.installUIManagerDefaults();
                } else {
                    UIManager.setLookAndFeel(new LimeLookAndFeel());
                }
            }

            UIManager.put("Tree.leafIcon", UIManager.getIcon("Tree.closedIcon"));
                
            // remove split pane borders
            UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());
            
            if(!CommonUtils.isMacOSX())
                UIManager.put("Table.focusRowHighlightBorder", UIManager.get("Table.focusCellHighlightBorder"));

            UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder(1, 1, 1, 1));            
            
            // Add a bolded text version of simple text.
            Font normal = UIManager.getFont("Table.font");
            FontUIResource bold = new FontUIResource(
                normal.getName(), Font.BOLD, normal.getSize());
            UIManager.put("Table.font.bold", bold);
            
        } catch (UnsupportedLookAndFeelException e) {
            throw new ExceptionInInitializerError(e);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        } catch (InstantiationException e) {
            throw new ExceptionInInitializerError(e);
        } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }        
    }
    
    /**
     * Gets the current theme (or defaults to MetalTheme) if possible.
     */
    private MetalTheme getDefaultTheme() {
        MetalTheme theme = null;
        if(CommonUtils.isJava15OrLater()) {
            try {
                theme = (MetalTheme)MetalLookAndFeel.class.getMethod("getCurrentTheme", null).invoke(null, null);
            } catch(IllegalAccessException iae) {
            } catch(InvocationTargetException ite) {
            } catch(NoSuchMethodException nsme) {
            }
        }
        
        if(theme == null)
            theme = new DefaultMetalTheme();
            
        return theme;
    }
    
    /**
     * Determines if the PlasticXP Theme is available.
     */
    private boolean isPlasticAvailable() {
        if(!CommonUtils.isJava14OrLater())
            return false;

        try {
            Class plastic = Class.forName("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
            return plastic != null;
        } catch(ClassNotFoundException cnfe) {
            return false;
        }
    }
    
    /**
     * Determines if the Plastic Windows Theme is available.
     */
    private boolean isPlasticWindowsAvailable() {
        if(!CommonUtils.isJava14OrLater())
            return false;
        
        try {
            Class plastic = Class.forName("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");
            return plastic != null;
        } catch(ClassNotFoundException cnfe) {
            return false;
        }
    }
    
    /**
     * Updates the component to use the native UI resource.
     */
    static ComponentUI getNativeUI(JComponent c) {
        ComponentUI ret = null;
        String name = UIManager.getSystemLookAndFeelClassName();
        if(name != null) {
            try {
                Class clazz = Class.forName(name);
                LookAndFeel lf = (LookAndFeel)clazz.newInstance();
                lf.initialize();
                UIDefaults def = lf.getDefaults();
                ret = def.getUI(c);
            } catch(ExceptionInInitializerError e) {
            } catch(ClassNotFoundException e) {
            } catch(LinkageError e) {
            } catch(IllegalAccessException e) {
            } catch(InstantiationException e) {
            } catch(SecurityException e) {
            } catch(ClassCastException e) {
            }
        }

        // if any of those failed, default to the current UI.
        if(ret == null)
            ret = UIManager.getUI(c);

        return ret;
    }
    
    /**
     * Reduces the size of a font in UIManager.
     */
    private static void reduceFont(String name) {
        Font oldFont = UIManager.getFont(name);
        FontUIResource newFont =
          new FontUIResource(oldFont.getName(), oldFont.getStyle(),
                            oldFont.getSize() - 2);
        UIManager.put(name, newFont);
    }

    /**
     * Unpacks any war files in the current directory.
     */
    private static void unpackWarFiles() throws IOException {
        //Unpack any .war files, and put into appropriate dirs.
        File currDir = CommonUtils.getCurrentDirectory();
        String[] warFiles = (currDir).list(
            new FilenameFilter() {
                //the files to be accepted to be returned
                public boolean accept(File dir, String name) {
                    return name.endsWith(".war");
                }
            });
        if (warFiles == null) {
            // no war files to expand -- don't worry about it
            return;
        }
        File destDir = CommonUtils.getUserSettingsDir();
        if(!destDir.isDirectory()) {
            throw new IOException("settings dir not a directory: "+destDir);
        }
        if(!destDir.canWrite()) {
            throw new IOException("cannot write to the settings dir: "+destDir);
        }
        
        for (int i = 0; i < warFiles.length; i++) {
            if(warFiles[i].equals("xml.war")) {
                // force schemas to always be recopied.
                Expand.expandFile(new File(warFiles[i]),
                                  destDir, false,
                                  new String[] { "xml/schemas/" } );
            } else {
                Expand.expandFile(new File(warFiles[i]), destDir);
            }
        }
    }

    /**
     * Unpacks the update.ver file.
     */
    private void unpackVersionFile() throws IOException {
        File userHome = CommonUtils.getUserSettingsDir();
        File verFile = new File("update.ver");
        Expand.expandFile(verFile, userHome);
    }
}
