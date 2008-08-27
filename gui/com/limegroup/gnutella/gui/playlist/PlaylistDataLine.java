package com.limegroup.gnutella.gui.playlist;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.limegroup.gnutella.gui.mp3.MediaPlayerComponent;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.ColoredCell;
import com.limegroup.gnutella.gui.tables.ColoredCellImpl;
import com.limegroup.gnutella.gui.tables.FileTransfer;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.TimeRemainingHolder;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.metadata.AudioMetaData;


public final class PlaylistDataLine extends AbstractDataLine
                                    implements FileTransfer {

    /**
     * Name column
     */
    static final int NAME_IDX = 0;
    private static final LimeTableColumn NAME_COLUMN =
        new LimeTableColumn(NAME_IDX, "PLAYLIST_TABLE_NAME",
                            450, true, ColoredCell.class);

    /**
     * Length column info
     */
    static final int LENGTH_IDX = 1;
    private static final LimeTableColumn LENGTH_COLUMN =
        new LimeTableColumn(LENGTH_IDX, "PLAYLIST_TABLE_LENGTH",
                        20, true, String.class);

    /**
     * Bitrate column info
     */
    static final int BITRATE_IDX = 2;
    private static final LimeTableColumn BITRATE_COLUMN =
        new LimeTableColumn(BITRATE_IDX, "PLAYLIST_TABLE_BITRATE",
                        20, true, Integer.class);

    /**
     * Total number of columns
     */
    static final int NUMBER_OF_COLUMNS = 3;
    
    /**
     * Number of columns
     */
    public int getColumnCount() { return NUMBER_OF_COLUMNS; }

    /**
     * The file this is based on.
     */
    private File FILE;
    
    /**
     * The length of this file.
     */
    private int length;
    
    /**
     * The name of this file.
     */
    private String name;
    
    /**
     * The bitrate of this file.
     */
    private int bitrate;
    
	/**
	 * The colors for cells.
	 */
	private Color _cellColor;
	private Color _othercellColor;    


    /**
     * Sets up the dataline for use with the playlist.
     */
    public void initialize(Object file) {
        super.initialize(file);
        FILE = (File)file;
        
        try {
            AudioMetaData amd = AudioMetaData.parseAudioFile(FILE);
            if(amd != null) {
                length = amd.getLength();
                bitrate = amd.getBitrate();
            }
        } catch(IOException ignored) {}

        name = FILE.getName();
        updateTheme();
    }

    /**
     * Returns the value for the specified index.
     */
    public Object getValueAt(int idx) {
        switch(idx) {
            case NAME_IDX:
                Color color = getColor(MediaPlayerComponent.isPlaying(FILE));
                return new ColoredCellImpl(name, color);
            case LENGTH_IDX:
                return new TimeRemainingHolder(length);
            case BITRATE_IDX:
                return new Integer(bitrate);
        }
        return null;
    }
    
    /**
     * Gets the color for whether or not the row is playing.
     */
    private Color getColor(boolean playing) {
        return playing ? _othercellColor : _cellColor;
    }
    
	// inherit doc comment
	public void updateTheme() {
		_cellColor = ThemeFileHandler.WINDOW8_COLOR.getValue();
		_othercellColor = ThemeFileHandler.SEARCH_RESULT_SPEED_COLOR.getValue();
	}    

	/**
	 * Return the table column for this index.
	 */
	public LimeTableColumn getColumn(int idx) {
        switch(idx) {
            case NAME_IDX:      return NAME_COLUMN;
            case LENGTH_IDX:    return LENGTH_COLUMN;
            case BITRATE_IDX:   return BITRATE_COLUMN;
        }
        return null;
    }
    
    public boolean isClippable(int idx) {
        return idx == NAME_IDX;
    }
    
    public int getTypeAheadColumn() {
        return NAME_IDX;
    }

	public boolean isDynamic(int idx) {
	    return false;
	}
	
	public File getFile() {
	    return FILE;
	}
}
