package com.limegroup.gnutella.gui.library;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileManager;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.downloader.IncompleteFileManager;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.ColoredCell;
import com.limegroup.gnutella.gui.tables.ColoredCellImpl;
import com.limegroup.gnutella.gui.tables.FileTransfer;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.SizeHolder;
import com.limegroup.gnutella.gui.tables.UploadCountHolder;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.gui.xml.XMLUtils;
import com.limegroup.gnutella.licenses.License;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.MetaFileManager;
import com.limegroup.gnutella.util.NameValue;

/**
 * This class acts as a single line containing all
 * the necessary Library info.
 * @author Sam Berlin
 */

//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|

public final class LibraryTableDataLine extends AbstractDataLine
	implements ThemeObserver, FileTransfer {

    /**
     * 0 final constant to preserve memory & allocations
     */
    private static final Integer ZERO_INTEGER = new Integer(0);

    /**
     * 0 / 0 final constant UploadCountHolder to preserve memory & allocations
     */
    private static final UploadCountHolder ZERO_UPLOAD_COUNT_HOLDER
                                            = new UploadCountHolder(0, 0);

    /**
     * Whether or not tooltips will display XML info.
     */
    private static boolean _allowXML;

    /**
     * The schemas available
     */
    private static String[] _schemas;

    /**
     * The meta file manager
     */
    private static MetaFileManager _mfm;
    
    /**
     * Constant for the column with the icon of the file.
     */
    static final int ICON_IDX = 0;
    private static final LimeTableColumn ICON_COLUMN =
        new LimeTableColumn(ICON_IDX, "LIBRARY_TABLE_ICON",
		    GUIMediator.getThemeImage("question_mark"),
                    18, true, Icon.class);

	/**
	 * Constant for the column with the name of the file.
	 */
	static final int NAME_IDX = 1;
	private static final LimeTableColumn NAME_COLUMN =
	    new LimeTableColumn(NAME_IDX, "LIBRARY_TABLE_NAME",
	                239, true, ColoredCell.class);

	/**
	 * Constant for the column storing the size of the file.
	 */
	static final int SIZE_IDX = 2;
	private static final LimeTableColumn SIZE_COLUMN =
	    new LimeTableColumn(SIZE_IDX, "LIBRARY_TABLE_SIZE",
	                62, true, ColoredCell.class);

	/**
	 * Constant for the column storing the file type (extension or more
	 * more general type) of the file.
	 */
	static final int TYPE_IDX = 3;
	private static final LimeTableColumn TYPE_COLUMN =
	    new LimeTableColumn(TYPE_IDX, "LIBRARY_TABLE_TYPE",
	                48, true, ColoredCell.class);

	/**
	 * Constant for the column storing the file's path
	 */
	static final int PATH_IDX = 4;
	private static final LimeTableColumn PATH_COLUMN =
	    new LimeTableColumn(PATH_IDX, "LIBRARY_TABLE_PATH",
	                108, true, ColoredCell.class);

	/**
	 * Constant for the column storing the number of upload count info
	 *
	 */
	static final int UPLOADS_IDX = 5;
	private static final LimeTableColumn UPLOADS_COLUMN =
	    new LimeTableColumn(UPLOADS_IDX, "LIBRARY_TABLE_UPLOAD_COUNT",
                    62, true, UploadCountHolder.class);

	/**
	 * Constant for the column storing the numbe of hits
	 * of the file.
	 */
	static final int HITS_IDX = 6;
	private static final LimeTableColumn HITS_COLUMN =
	    new LimeTableColumn(HITS_IDX, "LIBRARY_TABLE_HITCOUNT",
	                39, true, Integer.class);

	/**
	 * Constant for the column storing the number of alt locations
	 *
	 */
	static final int ALT_LOC_IDX = 7;
	private static final LimeTableColumn ALT_LOC_COLUMN =
	    new LimeTableColumn(ALT_LOC_IDX, "LIBRARY_TABLE_NUMALTLOC",
	                72, true, Integer.class);
	                
    /**
     * Constant for the license index.
     */
    static final int LICENSE_IDX = 8;
    private static final LimeTableColumn LICENSE_COLUMN =
        new LimeTableColumn(LICENSE_IDX, "LIBRARY_TABLE_LICENSE",
                    20, true, License.class);

	/**
	 * Number of columns
	 */
	static final int NUMBER_OF_COLUMNS = 9;

	/** Variable for the file */
	private File _file;

	/** Variable for the name */
	private String _name;

	/** Variable for the type */
	private String _type;

	/** Variable for the size */
	private int _size;

	/** Variable to hold the file descriptor */
	private FileDesc _fileDesc;

	/** Variable for the path */
	private String _path;

	/**
	 * The colors for cells.
	 */
	private Color _sharedCellColor;
	private Color _unsharedCellColor;
	
	/**
	 * The model this is being displayed on
	 */
	private final LibraryTableModel _model;
	
	/**
	 * Whether or not the icon has been loaded.
	 */
	private boolean _iconLoaded = false;
	
	/**
	 * Whether or not the icon has been scheduled to load.
	 */
	private boolean _iconScheduledForLoad = false;

	public LibraryTableDataLine(LibraryTableModel ltm) {
		super();
		_model = ltm;
		updateTheme();
		ThemeMediator.addThemeObserver(this);
	}
	
	/**
	 * This must be removed from the theme observer list in
	 * order to be garbage-collected.
	 */
	public void cleanup() {
	    ThemeMediator.removeThemeObserver(this);
	}

	// inherit doc comment
	public void updateTheme() {
		_sharedCellColor = ThemeFileHandler.WINDOW8_COLOR.getValue();
		_unsharedCellColor = ThemeFileHandler.NOT_SHARING_LABEL_COLOR.getValue();
	}

	public FileDesc getFileDesc() { return _fileDesc; }

	public int getColumnCount() { return NUMBER_OF_COLUMNS; }

	/**
	 * Initialize the object.
	 * It will fail if not given a FileDesc or a File
	 * (File is retained for compatability with the Incomplete folder)
	 */
    public void initialize(Object o) {
        File file;
        if (o instanceof FileDesc) {
            file = ((FileDesc)o).getFile();
            _fileDesc = (FileDesc)o;
        } else {
            file = (File)o;
            _fileDesc = RouterService.getFileManager().getFileDescForFile(file);
        }

        super.initialize(file);

        String fullPath = file.getPath();
        try {
            fullPath = file.getCanonicalPath();
        } catch(IOException ioe) {}

	    _file = file;
		_name = _file.getName();
		_type = "";
        if (!file.isDirectory()) {
            int index = _name.lastIndexOf(".");
            int index2 = fullPath.lastIndexOf(File.separator);
            _path = fullPath.substring(0,index2);
            if (index != -1 && index != 0) {
                _type = _name.substring(index+1);
                _name = _name.substring(0, index);
            }
        }
		_size = (int)file.length();
    }
    
    /**
     * Returns the file of this data line.
     */
    public File getFile() {
        return _file;
    }

	/**
	 * Returns the object stored in the specified cell in the table.
	 *
	 * @param idx  The column of the cell to access
	 *
	 * @return  The <code>Object</code> stored at the specified "cell" in
	 *          the list
	 */
	public Object getValueAt(int idx) {
	    switch (idx) {
	    case ICON_IDX:
	        if(!_iconScheduledForLoad) {
	            _iconScheduledForLoad = true;
                GUIMediator.instance().schedule(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeAndWait(new Runnable() {
                            public void run() {
                                IconManager.instance().getIconForFile(_file);
                                _iconLoaded = true;
                                _model.refresh();
                            }
                        });
                    }
                });
	            return null;
            } else if(_iconLoaded) {
	            return IconManager.instance().getIconForFile(_file);
            } else {
                return null;
            }
	    case NAME_IDX:
	        String nm = _name;
	        // note: this fits better in the data line because
	        // sorting and whatnot will work correctly.
	        if (LibraryMediator.incompleteDirectoryIsSelected()) {
	            try {
                //Ideally we'd eliminate the dependency on IFM, but this seems
                //better than adding yet another method to RouterService.
                    nm = IncompleteFileManager.getCompletedName(_file);
                } catch (IllegalArgumentException e) {
                    //Not an incomplete file?  Just return untranslated value.
                }
            }
	        return new ColoredCellImpl(nm, getColor());	                    
	    case SIZE_IDX:
	        return new ColoredCellImpl(new SizeHolder(_size), getColor());
	    case TYPE_IDX:
	        return new ColoredCellImpl(_type, getColor());
	    case HITS_IDX:
	        if ( _fileDesc == null ) return null;
	        int hits = _fileDesc.getHitCount();
	        // don't allocate if we don't have to
	        return hits == 0 ? ZERO_INTEGER : new Integer(hits);
	        //note: we use Integer here because its compareTo is
	        //      smarter than String's, and it has a toString anyway.
	    case ALT_LOC_IDX:
	        if ( _fileDesc == null ) return null;
	        int locs = RouterService.getAltlocManager().getNumLocs(_fileDesc.getSHA1Urn()) - 1;
	        return locs <= 0 ? ZERO_INTEGER : new Integer(locs);
	    case UPLOADS_IDX:
	        if ( _fileDesc == null ) return null;
	        int a = _fileDesc.getAttemptedUploads();
	        int c = _fileDesc.getCompletedUploads();
	        return a == 0 && c == 0 ? ZERO_UPLOAD_COUNT_HOLDER :
	                                  new UploadCountHolder(a, c);
	    case PATH_IDX:
	        return new ColoredCellImpl(_path, getColor());
        case LICENSE_IDX:
            License lc = getLicense();
            if(lc != null) {
                if(lc.isValid(_fileDesc.getSHA1Urn()))
                    return new NameValue(lc.getLicenseName(), new Integer(License.VERIFIED));
                else
                    return new NameValue(lc.getLicenseName(), new Integer(License.UNVERIFIED));
            } else {
                return null;
            }
	    }
	    return null;
	}

	public LimeTableColumn getColumn(int idx) {
	    switch(idx) {
	        case ICON_IDX:          return ICON_COLUMN;
	        case NAME_IDX:          return NAME_COLUMN;
	        case SIZE_IDX:          return SIZE_COLUMN;
	        case TYPE_IDX:          return TYPE_COLUMN;
	        case PATH_IDX:          return PATH_COLUMN;
	        case HITS_IDX:          return HITS_COLUMN;
	        case ALT_LOC_IDX:       return ALT_LOC_COLUMN;
	        case UPLOADS_IDX:       return UPLOADS_COLUMN;
	        case LICENSE_IDX:       return LICENSE_COLUMN;
	    }
	    return null;
	}
	
	public boolean isClippable(int idx) {
	    switch(idx) {
        case ICON_IDX:
            return false;
        default:
            return true;
        }
    }
    
    public int getTypeAheadColumn() {
        return NAME_IDX;
    }

	public boolean isDynamic(int idx) {
	    switch(idx) {
	        case HITS_IDX:
	        case ALT_LOC_IDX:
	        case UPLOADS_IDX:
	            return true;
	    }
	    return false;
	}

	/**
	 * Initialize things we only need to do once
	 */
	static void setXMLEnabled(boolean en) {
	    _allowXML = en;
	    if ( _allowXML ) {
	        _schemas =
	            LimeXMLSchemaRepository.instance().getAvailableSchemaURIs();
    	    FileManager fm = RouterService.getFileManager();
    	    if ( fm instanceof MetaFileManager ) _mfm = (MetaFileManager)fm;
	    } else {
	        _schemas = null;
	        _mfm = null;
	    }
	}
	
	/**
	 * Determines if this FileDesc has a license.
	 */
	boolean isLicensed() {
	    return _fileDesc != null && _fileDesc.isLicensed();
	}
	
	/**
	 * Gets the license string for this FileDesc.
	 */
	License getLicense() {
	    return _fileDesc != null ? _fileDesc.getLicense() : null;
    }
    
    /** Gets the first XML doc associated with the FileDesc, if one exists. */
    LimeXMLDocument getXMLDocument() {
        if(_fileDesc != null) {
            List l = _fileDesc.getLimeXMLDocuments();
            if(!l.isEmpty())
                return (LimeXMLDocument)l.get(0);
        }
        
        return null;
    }

	public String[] getToolTipArray(int col) {

	    // if XML isn't finished loading, no schemas exist,
	    // we don't have a meta file manager, or we don't
	    // have a FileDesc, get out of here.
	    if ( !_allowXML
	         || _schemas == null || _schemas.length == 0
	         || _mfm == null || _fileDesc == null
	        ) return null;

        // Dynamically add the information.
        List allData = new LinkedList();        
        List docs = _fileDesc.getLimeXMLDocuments();
        for(Iterator i = docs.iterator(); i.hasNext(); ) {
            LimeXMLDocument doc = (LimeXMLDocument)i.next();
            allData.addAll(XMLUtils.getDisplayList(doc));
        }

        if ( !allData.isEmpty() ) {
            // if it had meta-data, display the filename in the tooltip also.
            allData.add(0, _name);
            return (String[])allData.toArray(new String[allData.size()]);
	    } else {
	        return null;
	        //return new String[] { "No meta-data exists.", "Click 'annotate' to add some." };
	    }
	}
	
	private Color getColor() {
		if (_fileDesc != null)
			return _sharedCellColor;
		if (RouterService.getFileManager().isCompletelySharedDirectory(_file))
			return _sharedCellColor;
		return _unsharedCellColor;
	}
}
