package com.limegroup.gnutella.gui.options.panes;

import java.io.File;
import java.io.IOException;

import java.util.Set;

import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.IconAndNameHolder;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.settings.FileSetting;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * Displays the named mediatype in the first column and its download directory
 * in the second column.
 */
public class MediaTypeDownloadDirDataLine extends AbstractDataLine {

	/**
	 * The mediatype this data line represents.
	 */
	private NamedMediaType nm;

	/**
	 * Holds the new value of the download directory for this mediatype if it
	 * was set in this session.
	 */
	private String dir;

	/**
	 * Holds the corresponding file setting for the mediatype.
	 */
	private FileSetting setting;

	/**
	 * Is true when the reset action has been called on this data line.
	 */
	private boolean isReset;

	/**
	 * Handle to the current default download directory.
	 */
	private String defaultDir;

	private static final LimeTableColumn[] columns = new LimeTableColumn[] {
			new LimeTableColumn(0, "OPTIONS_SAVE_MEDIATYPE", 60, true, IconAndNameHolder.class),
			new LimeTableColumn(1, "OPTIONS_SAVE_DIRECTORY", 100, true, String.class), };

	public int getColumnCount() {
		return columns.length;
	}

	public void setDefaultDir(String text) {
		defaultDir = text;
	}

	/**
	 * Reset the download directory for this mediatype. Henceforth files of this
	 * mediatype will be saved to the default download directory again.
	 */
	public void reset() {
		dir = null;
		isReset = true;
	}

	/**
	 * Saves the new download directory for this mediatyp if it was set during
	 * this session or reverts the default value if it was reset.
	 */
	public void saveDirectory(Set newDirs) throws IOException {
	    boolean dirty = isDirty();
	    
		if (isReset)
			setting.revertToDefault();
		else if (dir != null && !setting.getValue().equals(new File(dir)))
			setting.setValue(new File(dir));

        if(dirty)
            newDirs.add(setting.getValue());
	}

	public LimeTableColumn getColumn(int col) {
		return columns[col];
	}

	public boolean isDynamic(int col) {
		return false;
	}

	public boolean isClippable(int col) {
		return true;
	}

	public void initialize(Object obj) {
		super.initialize(obj);
		nm = (NamedMediaType) obj;
		setting = SharingSettings.getFileSettingForMediaType(nm.getMediaType());
		isReset = false;
	}

	public Object getValueAt(int col) {
		switch (col) {
		case 0:
			return nm;
		case 1:
			if (dir != null)
				return dir;
			else if (isReset || setting.isDefault())
				return defaultDir;
			else
				return setting.getValue();
		}
		return null;
	}
	
	/**
	 * Determines if this has data different than the setting's data.
	 */
	boolean isDirty() {
        return dir != null &&  !setting.getValue().equals(new File(dir));
	}

	/**
	 * Sets the new download directory for this mediatype.
	 */
	public void setDirectory(String dir) {
		this.dir = dir;
		isReset = false;
	}
	
	/**
	 * Gets the current value.
	 */
	String getDirectory() {
	    return dir;
	}

	/*
	 * @see com.limegroup.gnutella.gui.tables.DataLine#getTypeAheadColumn()
	 */
	public int getTypeAheadColumn() {
		return 0;
	}
}