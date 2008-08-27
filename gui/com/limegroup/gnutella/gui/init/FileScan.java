package com.limegroup.gnutella.gui.init;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.util.LimitedList;
import com.limegroup.gnutella.util.Pair;
import com.limegroup.gnutella.util.StringUtils;

/**
 * At either run time or the first time the client is run, this class
 * will scan the entire hard drive searching for files with particular
 * file extensions (like .mp3).  It will keep track of the directories
 * in which those files exist, and will return the top 5 directories.
 * The top five are chosen by some combination of number of files and
 * memory.
 *
 * @author rsoule
 */
public class FileScan {

    private int MEM_WEIGHT = 1;
    private int NUM_WEIGHT = 1;
    private int MAX_DEPTH = 3;

    private String[] _extensions; 
    private String[] _filters;
    private LimitedList _list;

    public FileScan() {
        _list = new LimitedList();
        _filters = new String[0];
        String exts = SharingSettings.EXTENSIONS_TO_SHARE.getValue();
        _extensions = StringUtils.split(exts, ";");
    }

    /**
     * Sets the shared extensions to use for searching.
     */
    public void setExtensions(String[] e) {
        _extensions = e;
    }

    /**
     * Sets the words to use as filters for determining potential
     * shared directories.
     */
    public void setFilters(String[] f) {
        _filters = f;
    }

    /**
     * Returns the list of directories containing shared file 
     * types as an array of strings.
     */
    public String[] getListAsArray() {
        Object[] objs = _list.getAllElements();
        int len = objs.length;
        Pair p;
        File f;
        String[] files = new String[len];
        for (int i = 0; i < len; i++) {
            if (objs[i] != null) {
                p = (Pair)objs[i]; 
                f = (File)p.getElement();
                try {
                    files[i] = f.getCanonicalPath();
                } catch (IOException ioe) {
                    files[i] = "";
                }
            }
        }
        return files;
    }
    
    /**
     * Returns the list of directories containing shared file 
     * types as one long strings.
     */
    public String getListAsString() {
        Object[] objs = _list.getAllElements();
        int len = objs.length;
        String files = "";
        for (int i = 0; i < len; i++) {
            if (objs[i] != null) {
                Pair p = (Pair)objs[i]; 
                File f = (File)p.getElement();
                try {
                    files += f.getCanonicalPath();
                    files += ";";
                } catch (IOException ioe) {
                }
            }
        }
        return files;
    }

    /**
     * Scans the directory associated with the string parameter
     * for directories containing shared file types.
     */
    public void scan(String pathname) {
        scan(pathname, MAX_DEPTH);    
    }

    /**
     * Does the internal scanning for directories containing
     * shared file types.
     */
    private void scan(String pathname, int depth) {    
        if (depth == 0) 
            return;
        depth--;

        File file = new File(pathname);
        if (!file.isDirectory())
            return;
    
        File[] files = listFiles(file);
        int num_files = files.length;
        for (int i = 0; i < num_files; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                try {
                    String path = f.getCanonicalPath();
                    addDirectory(path);
                    scan(path, depth);
                } catch (IOException ioe) {
                }
                //addDirectory(f.getCanonicalPath());
                //scan(f.getAbsolutePath(), depth);
            }
        }
    }

    /**
     * .
     */
    public void scan(String[] pathnames) {
        scan(pathnames, MAX_DEPTH);
    }

    /**
     * .
     */
    public void scan(String[] pathnames, int depth) {
        if (depth == 0) 
            return;
        depth--;
        String pathname;
        for (int j = 0; j < pathnames.length; j++) {
            pathname = pathnames[j];
            File file = new File(pathname);
            if (file.isDirectory()) {
                File[] files = listFiles(file);
                int num_files = files.length;
                for (int i = 0; i < num_files; i++) {
                    File f = files[i];
                    if (f.isDirectory()) {
                        try {
                            String subDirPath = f.getCanonicalPath();
                            addDirectory(subDirPath);
                            scan(subDirPath);
                        } catch (IOException ioe) {
                        }
                    }        
                }       
            }
        }
    }

    /**
     * Adds a directory to the list of potential directories
     * to share.
     */
    private void addDirectory(String pathname) {
        File dir = new File(pathname);
        if (!dir.isDirectory())
            return;
        int mem = 0;
        int num = 0;
        if (!hasFilter(pathname)) {
            File[] files = listFiles(dir);
            int num_files = files.length;
            for (int i = 0; i < num_files; i++) {
                File f = files[i];
                String name = f.getName();
                if (hasExtension(name)) {
                    mem += f.length();
                    num++;
                }
            }
        }
        int key = calculateKey(num, mem);
        _list.add(new Pair(key, dir), key);    
    }

    /**
     * Calculates the "key," or weighted value, that a directory
     * should have based on the number of files and the size of
     * those files.
     */
    private int calculateKey(int num_files, int size_files) {
        int key = (num_files * NUM_WEIGHT) + (size_files * MEM_WEIGHT);
        return key;
    }

    /**
     * Determines whether or not the file denoted by this
     * pathname has a filter associated with it.
     */
    private boolean hasFilter(String pathname) {
        pathname = pathname.toLowerCase(Locale.US);
        int length = _filters.length;        
        for (int i = 0; i < length; i++) {
            String curFilter = _filters[i].toLowerCase(Locale.US);
            if (pathname.indexOf(curFilter) != -1)
                return true;
        }
        return false;
    }

    /**
     * Determines if the file denoted by the "filename" parameter
     * has a shared extension.
     */
    private boolean hasExtension(String filename) {
        int begin = filename.lastIndexOf(".") + 1;
        if (begin == 0) // file has no extension
            return false; // always not shared ?
        // Should we also share files with only extensions, i.e. those
        // starting with a dot like ".rc" which are "hidden" on Unix
        // if not, we should exclude the case where begin == 1 too...
        // (A leading dot does not denote an extension but a special name).
        int end = filename.length();
        String ext = filename.substring(begin, end);

        int length = _extensions.length;
        for (int i = 0; i < length; i++) {
            if (ext.equalsIgnoreCase(_extensions[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an array of File objects representing
     * the files and directories in the directory 
     * denoted in the "dir" parameter.
     * @requires the File parameter must be a directory.
     * @param dir a directory File object.
     */
    private File[] listFiles(File dir) {
        String [] fnames = dir.list();
        if (fnames == null)
            return new File[0];

        File[] theFiles = new File[fnames.length];
        for (int i = 0; i < fnames.length; i++) {
            theFiles[i] = new File(dir, fnames[i]);
        }
        return theFiles;
    }


//  public static void main(String argc[]) {
//      FileScan fs = new FileScan();
//      String root = "C:\\";
//      String[] filters = { "Recycle", "Incomplete", "LimeWire" };
//      fs.setFilters(filters);
//      fs.scan(root);
//      //String[] dirs_ = fs.getListAsArray();
//      String str = fs.getListAsString();
//      System.out.println("found: " + str);
//  }

}

