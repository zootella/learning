package com.limegroup.gnutella.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;

import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.settings.UISettings;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;
import com.limegroup.gnutella.util.ProcessingQueue;

/**
 * Manages finding native icons for files and file types.
 */
public class IconManager {
    /**
     * The sole instance of this IconManager class.
     */
    private static IconManager INSTANCE = new IconManager();
    
    /**
     * The view that retrieves the icon from the filesystem.
     */
    private final FileView VIEW;
    
    /**
     * A mapping from String (extension) to Icon.
     */
    private final Map EXTENSIONS = new HashMap();
    
    /**
     * A marker null icon so we don't create a file everytime
     * if the icon can't be found.
     */
    private final Icon NULL = new ImageIcon();
    
    /**
     * Boolean for whether or not the file is required to be on disk
     * to retrieve the icon for the given FileView.
     */
    private final boolean REQUIRES_FILE;
    
    /**
     * A mapping of user-friendly names to the file name
     * of the icon.
     */
    private final Properties /* String -> String */ BUTTON_NAMES =
        loadButtonNameMap();
    
    /**
     * A mapping of the file name of the icon to the icon itself,
     * so we don't load the resource multiple times.
     */
    private final Map /* String -> Icon */ BUTTON_CACHE = new HashMap();

    /**
     * Returns the sole instance of this IconManager class.
     */
    public static IconManager instance() { return INSTANCE; }
    
    /**
     * Constructs a new IconManager.
     */
    private IconManager() {
        FileView view = null;
        boolean requiresFile = false;
        
        // Set the FileView appropriately.
        // Currently, Windows w/ Java 1.4+ & all OSX (?) javas
        // can get the correct native icon.
        // All others cannot.
        // TODO:  Find a way to look this up dynamically.
        if(CommonUtils.isMacOSX() ||
           (CommonUtils.isWindows() && CommonUtils.isJava14OrLater())) {
            requiresFile = true;
            view = getNativeFileView();
        }
        
        if(view == null) {
            requiresFile = false;
            view = new MediaFileView();
        } else {
            requiresFile = true;
            view = new DelegateFileView(view);
        }
       
        VIEW = view;
        REQUIRES_FILE = requiresFile;
        
        // native view requires pre-loading, MediaFileView doesn't
        if(!(view instanceof MediaFileView))
            preload();
    }
    
    /**
     * Retrieves the native FileView.
     */
    private FileView getNativeFileView() {
        // This roundabout way of getting the FileView is necessary for the
        // following reasons:
        // 1) We need the native UI's FileView to get the correct icons,
        //    because the Metal UI's icons are terrible.
        // 2) We cannot just call getFileView(chooser) once retrieving the
        //    native UI, because FileChooserUI tends to delegate calls
        //    to the JFileChooser, and it seems to require that it
        //    the UI be set on the chooser.
        // 3) setUI is a protected method of JFileChooser (of JComponent),
        //    so we need to have the anonymous class with an extended
        //    constructor.
        // 4) Even after constructing the JFileChooser, using getIcon on it
        //    doesn't work well, so we need to do it directly on the FileView.
        // 5) In order to get the correct file view, it needs to be explicitly
        //    set, otherwise it reverts to the UI's FileView, using UIManager,
        //    which may actually be a different UI.
        // 6) The NullPointerException must be caught because sometimes
        //    the Windows JFileChooser throws an NPE while constructing.
        JFileChooser chooser = null;
        for(int i = 0; i < 10; i++) {
            try {
                chooser = new JFileChooser() {
                    {
                        FileChooserUI ui =
                            (FileChooserUI)ResourceManager.getNativeUI(this);
                        setUI(ui);
                        setFileView(ui.getFileView(this));
                    }
                };
                break;
            } catch(NullPointerException ignored) {}
        }
        
        // If after 10 times we still can't set the damn thing,
        // just error and give up.
        if(chooser == null) {
            return null;
        } else {
            return chooser.getFileView();
        }
    }
    
    /**
     * Returns the icon associated with this file.
     * If the file does not exist, or no icon can be found, returns
     * the icon associated with the extension.
     */
    public Icon getIconForFile(File f) {
        if(f == null)
            return null;
        
        // We must check f.exists first, otherwise there will be spurious
        // exceptions when getting the icon from the view.
        if(REQUIRES_FILE && f.exists()) {
            return VIEW.getIcon(f);
        } else {
            String extension = FileUtils.getFileExtension(f);
            if(extension != null)
                return getIconForExtension(extension);
        }
        return null;
    }
    
    /**
     * Returns the icon assocated with the extension.
     * TODO: Implement better.
     */
    public Icon getIconForExtension(String ext) {
        if(!REQUIRES_FILE)
            return VIEW.getIcon(new File("a." + ext));
        
        ext = ext.toLowerCase();
        Icon icon = (Icon)EXTENSIONS.get(ext);
        if(icon != null) {
            if(icon != NULL)
                return icon;
            else 
                return null;
        }

        // If we don't know the icon for this extension yet,
        // then create a temporary file, get icon, cache it,
        // and return it.
        File dir = SharingSettings.INCOMPLETE_DIRECTORY.getValue();
        File tmp = new File(dir, ".LimeWireIconFinder." + ext);

        if(tmp.exists()) {
            icon = VIEW.getIcon(tmp);
        } else {
            try {
                FileUtils.touch(tmp);
                icon = VIEW.getIcon(tmp);
                if(icon == null)
                    icon = NULL;
            } catch(IOException fnfe) {
                icon = NULL;
            }
        }

        tmp.delete();
        EXTENSIONS.put(ext, icon);
        return icon;
    }
    
    /**
     * Wipes out the button icon cache, so we can switch from large to small
     * icons (or vice versa).
     */
    public void wipeButtonIconCache() {
        BUTTON_CACHE.clear();
    }
    
    /**
     * Retrieves the icon for the specified button name.
     */
    public Icon getIconForButton(String buttonName) {
        String fileName = (String)BUTTON_NAMES.get(buttonName);
        if(fileName == null)
            return null;
        
        ImageIcon icon = (ImageIcon)BUTTON_CACHE.get(fileName);
        if(icon == NULL)
            return null;
        if(icon != null)
            return icon;
        
        try {
            String retrieveName;
            if(UISettings.SMALL_ICONS.getValue())
                retrieveName = fileName + "_small";
            else
                retrieveName = fileName + "_large";
            
            icon = ResourceManager.getThemeImage(retrieveName);
            BUTTON_CACHE.put(fileName, icon);
        } catch(MissingResourceException mre) {
            // if neither small nor large existed, try once as exact
            try {
                icon = ResourceManager.getThemeImage(fileName);
                BUTTON_CACHE.put(fileName, icon);
            } catch(MissingResourceException mre2) {
                BUTTON_CACHE.put(fileName, NULL);
            }
        }
        return icon;
    }
    
    /**
     * Retrieves the rollover image for the specified button name.
     */
    public Icon getRolloverIconForButton(String buttonName) {
        String fileName = (String)BUTTON_NAMES.get(buttonName);
        if(fileName == null)
            return null;

        // See if we've already cached a brighter icon.
        String rolloverName = fileName + "_rollover";
        Icon rollover = (Icon)BUTTON_CACHE.get(rolloverName);
        if(rollover == NULL)
            return null;
        if(rollover != null)
            return rollover;
        
        // Retrieve the initial icon, so we can brighten it.
        Icon icon = (Icon)BUTTON_CACHE.get(fileName);
        // no icon?  no brightened icon.
        if(icon == NULL || icon == null) {
            BUTTON_CACHE.put(rolloverName, NULL);
            return null;
        }
           
        // Make a brighter version of the icon, and cache it.
        rollover = ImageManipulator.brighten(icon);
        if(rollover == null)
            BUTTON_CACHE.put(rolloverName, NULL);
        else
            BUTTON_CACHE.put(rolloverName, rollover);
        
        return rollover;
    }   
    
    private static Properties loadButtonNameMap() {
        Properties p = new Properties();
        URL url = ResourceManager.getURLResource("icon_mapping.properties");
        InputStream is = null;
        try {
            if(url != null) {
                is = new BufferedInputStream(url.openStream());
                p.load(is);
            }
        } catch(IOException ignored) {
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(IOException ignored) {}
            }
        }
        return p;
    }    
    
    /**
     * Preloads a bunch of icons.
     */
    private void preload() {
        ProcessingQueue queue = new ProcessingQueue("IconLoader");
        final MediaType[] types = MediaType.getDefaultMediaTypes();
        for(int i = 0; i < types.length; i++) {
            final Set exts = types[i].getExtensions();
            for(Iterator j = exts.iterator(); j.hasNext(); ) {
                final String next = (String)j.next();
                queue.add(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeAndWait(new Runnable() {
                            public void run() {
                                getIconForExtension(next);
                            }
                        });
                    }
                });
            }
        }
    }
    
    /**
     * A simple FileView for returning icons that match the MediaType's
     * extension.  Useful for when no native icon can be retrieved.
     */
    private static class MediaFileView extends FileView {
        public Icon getIcon(File f) {
            String ext = FileUtils.getFileExtension(f);
            NamedMediaType nmt = null;
            if (ext != null)
                nmt = NamedMediaType.getFromExtension(ext);
            if(nmt == null)
                nmt = NamedMediaType.getFromDescription("*"); // any type
            
            return nmt.getIcon();
        }
        
        public String getDescription(File f) { return null; }
        public String getName(File f) { return null; }
        public String getTypeDescription(File f) { return null; }
        public Boolean isTraversable(File f) { return Boolean.FALSE; }
    }
    
    /**
     * Delegates to another FileView, catching NPEs.
     *
     * This is required because of poorly built methods in
     * javax.swing.filechooser.FileSystemView that print true
     * exceptions to System.err and return null, instead of
     * letting the exception propogate.
     */
    private static class DelegateFileView extends FileView {
        private final FileView DELEGATE;
        DelegateFileView(FileView real) {
            DELEGATE = real;
        }
        
        public Icon getIcon(File f) {
            try {
                return DELEGATE.getIcon(f);
            } catch(NullPointerException npe) {
                return null;
            }
        }
        
        public String getDescription(File f) {
            return DELEGATE.getDescription(f);
        }
        
        public String getName(File f) {
            return DELEGATE.getName(f);
        }
        
        public String getTypeDescription(File f) {
            return DELEGATE.getTypeDescription(f);
        }
        
        public Boolean isTraversable(File f) {
            return DELEGATE.isTraversable(f);
        }
    }
    
    /**
     * A simple FileView for returning null objects, when we can't get
     * the view we want.
     */
    private static class NullFileView extends FileView {
        public String getDescription(File f) { return null; }
        public Icon getIcon(File f) { return null; }
        public String getName(File f) { return null; }
        public String getTypeDescription(File f) { return null; }
        public Boolean isTraversable(File f) { return Boolean.FALSE; }
    }
}