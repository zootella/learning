package com.limegroup.gnutella.gui.search;

import java.util.HashMap;

import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.settings.UISettings;
import com.limegroup.gnutella.gui.tables.BasicDataLineModel;
import com.limegroup.gnutella.gui.tables.DataLine;
import com.limegroup.gnutella.gui.tables.LimeJTable;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;

/** 
 * Model for search results.
 *
 * Ensures that if new lines are added and they are similiar to old lines,
 * that the new lines are added as extra information to the existing lines,
 * instead of as brand new lines.
 */
class ResultPanelModel extends BasicDataLineModel {
    
    /**
     * The model storing metadata information.
     */
    protected final MetadataModel METADATA;
    
    /**
     * The table this is the model of.
     * This is necessary to fix selection problems caused
     * by insertion / removal of rows.
     */
    private LimeJTable TABLE;
    
    /**
     * The columns.
     */
    protected final SearchTableColumns COLUMNS = new SearchTableColumns();
    
    /**
     * Whether or not metadata is being tallied.
     */
    protected boolean _useMetadata = true;
    
    /**
     * HashMap for quick access to indexes based on SHA1 info.
     */
    private final HashMap _indexes = new HashMap();
    
    /**
     * The TableLineGrouper to use for slow matching.
     *
     * Allocated when needed.
     */
    private TableLineGrouper _grouper;

    /**
     * The number of sources for this search.
     */
    private int _numSources;
    
    /**
     * Constructs a new ResultPanelModel with a new MetadataModel.
     */
    ResultPanelModel() {
        this(new MetadataModel());
    }
    
    /**
     * Constructs a new ResultPanelModel with the given MetadataModel.
     */
    ResultPanelModel(MetadataModel mm) {
        super(TableLine.class);
        METADATA = mm;
    }
    
    /**
     * Whether or not the line should add to the metadata.
     */
    void setUseMetadata(boolean use) {
        _useMetadata = use;
    }
    
    /**
     * Sets the LimeJTable that this ResultPanelModel is for.
     *
     * Necessary to fix the selection after moving rows.
     */
    void setTable(LimeJTable table) {
        TABLE = table;
    }
    
    /**
     * Gets the columns used by this model.
     */
    SearchTableColumns getColumns() {
        return COLUMNS;
    }
    
    /**
     * Creates a new TableLine.
     */
    public DataLine createDataLine() {
        return new TableLine(COLUMNS);
    }
    
    /**
     * Gets the column at the specified index.
     */
    public LimeTableColumn getTableColumn(int idx) {
        return COLUMNS.getColumn(idx);
    }
    
    /**
     * Overrides default compare to change the value if COUNT is sorted
     * and the line is ethernet.
     */
    public int compare(Object a, Object b) {
        if(!isSorted() || _activeColumn != SearchTableColumns.COUNT_IDX)
            return super.compare(a, b);
        else
            return compareCount((TableLine)a, (TableLine)b);
    }
    
    /**
     * Returns the metadata model storing information about each result
     * for easy filtering.
     */
    MetadataModel getMetadataModel() {
        return METADATA;
    }
    
    /** 
     * Overrides the default remove to remove the index from the hashmap.
     *
     * @param row  the index of the row to remove.
     */
    public void remove(int row) {
        URN sha1 = getSHA1(row);
        if(sha1 != null)
            _indexes.remove(sha1);
        super.remove(row);
        remapIndexes(row);
    }
    
    /**
     * Override default so new ones get added to the end
     */
    public int add(Object o) {
        return add(o, getRowCount());
    }
    
    /**
     * Override to fix compile error on OSX.
     */
    public int add(DataLine dl) {
        return super.add(dl);
	}
	
	/**
	 * Override to not iterate through each result.
	 */
	public Object refresh() {
        fireTableRowsUpdated(0, getRowCount());
        return null;
    }
    
    /**
     * Does a slow refresh, forcing the underlying results to have
     * 'update' called on them.
     */
    public void slowRefresh() {
        super.refresh();
    }
    
    /**
     * Overriden to not get a new dataline if something already exists.
     */
    public DataLine getNewDataLine(Object o) {
	    SearchResult sr = (SearchResult)o;
	    URN sha1 = sr.getRemoteFileDesc().getSHA1Urn();
	    int idx = -1;
	    
	    if(UISettings.UI_GROUP_RESULTS.getValue()) {
    	    if(sha1 == null)
    	        idx = slowMatch(sr);
            else
                idx = fastMatch(sha1);
        }
        
        if(idx != -1) {
            TableLine line = (TableLine)get(idx);            
            int added = addNewResult(line, sr);
            if(added != 0 && isSorted() && 
               TABLE.getTableSettings().REAL_TIME_SORT.getValue() &&
               getSortColumn() == SearchTableColumns.COUNT_IDX)
                move(line, idx);
            else if(added != 0)
                fireTableRowsUpdated(idx, idx);
            return null;
        }
        
        return super.getNewDataLine(o);
    }
    
    /**
     * Adds sr to line as a new source.
     */
    protected int addNewResult(TableLine line, SearchResult sr) {
        int oldCount = line.getLocationCount();
        line.addNewResult(sr, METADATA);
        int newCount = line.getLocationCount();
        int added = newCount - oldCount;
        _numSources += added;
        return added;
    }

    /**
     * Maintains the indexes HashMap & MetadataModel.
     */    
    public int add(DataLine dl, int row) {
        TableLine tl = (TableLine)dl;
        _numSources += tl.getLocationCount();
        URN sha1 = tl.getSHA1Urn();
        if(sha1 != null)
            _indexes.put(sha1, new Integer(row));
        int addedAt = super.add(dl, row);
        remapIndexes(addedAt + 1);
        if(_useMetadata)
            METADATA.addNew(tl); // MUST be after add, else callbacks whack out
        return addedAt;
    }
    
    /**
     * Gets the row this DataLine is at.
     */
    public int getRow(DataLine dl) {
        TableLine tl = (TableLine)dl;
        URN sha1 = tl.getSHA1Urn();
        if(sha1 != null)
            return fastMatch(sha1);
        else
            return super.getRow(dl);
    }
    
    /**
     * Gets the row this initialize object is at.
     */
    public int getRow(Object o) {
        SearchResult sr = (SearchResult)o;
        URN sha1 = sr.getRemoteFileDesc().getSHA1Urn();
        if(sha1 != null)
            return fastMatch(sha1);
        else
            return super.getRow(o);
    }
    
    /**
     * Returns the number of sources found for this search.
     */
    int getTotalSources() {
        return _numSources;
    }
    
    /** 
     * Overrides the default sort to maintain the indexes HashMap,
     * according to the current sort column and order.
     */
    protected void doResort() {
        super.doResort();
        _indexes.clear(); // it's easier & quicker to just clear & re-input
        remapIndexes(0);
    }
    
    /**
     * Overrides the default clear to erase the indexes HashMap,
     * Metadata and Grouper.
     */
    public void clear() {
        if(METADATA != null)
            METADATA.clear();
        if(_grouper != null)
            _grouper.clear();
        simpleClear();
    }
    
    /**
     * Does nothing -- lines need no cleanup.
     */
    protected void cleanup() {}
    
    /**
     * Simple clear -- clears the number of sources & cached SHA1 indexes.
     * Calls super.clear to erase the stored lines.
     */
    protected void simpleClear() {
        _numSources = 0;
        _indexes.clear();
        super.clear();
    }
    
    /**
     * Moves line from oldIdx to somewhere new because its sources updated.
     */
    private void move(TableLine dl, int oldIdx) {
        int newIdx = oldIdx;
        if(!isSortAscending()) {
            // if was at the beginning, update and leave.
            if(oldIdx == 0) {
                fireTableRowsUpdated(0, 0);
                return;
            }

            // traverse upwards till we find a line with more.
            for(int i = 0; newIdx > 0; newIdx--, i++) {
                TableLine current = (TableLine)get(newIdx-1);
                if(compareCount(dl, current) >= 0)
                    break;
            }
        } else {
            int end = getRowCount() - 1;
            // if it was at the end, update & leave.
            if(oldIdx == end) {
                fireTableRowsUpdated(end, end);
                return;
            }
            
            // traverse downloads till we find a line with more
            for(; newIdx < end; newIdx++) {
                TableLine current = (TableLine)get(newIdx+1);
                if(compareCount(dl, current) >= 0)
                    break;
            }
        }                
        
        // didn't move anywhere? update and leave.
        if(oldIdx == newIdx) {
            fireTableRowsUpdated(newIdx, newIdx);
            return;
        }

        // store value for later fix.        
        boolean selected = TABLE.isRowSelected(oldIdx);
        boolean inView = TABLE.isSelectionVisible();

        // we moved from oldIdx to newIdx.
        super.remove(oldIdx);
        super.add(dl, newIdx);

        // *** fix for JTable selection bugs.
        if(selected) {
            TABLE.clearSelection();
            TABLE.addRowSelectionInterval(newIdx, newIdx);
            if(inView)
                TABLE.ensureSelectionVisible();
        } else {
            TABLE.removeRowSelectionInterval(newIdx, newIdx);
            int selRow = TABLE.getSelectedRow();
            if(selRow != -1) {
                TABLE.addRowSelectionInterval(selRow, selRow);
                if(inView)
                    TABLE.ensureSelectionVisible();
            }
        }           
        // *** end fix. 

        // remap the indexes that changed.
        if(oldIdx < newIdx)
            remapIndexes(oldIdx, newIdx + 1);
        else
            remapIndexes(newIdx, oldIdx + 1);
    }    
    
    /**
     * Remaps the indexes, starting at 'start' and going to the end of
     * the list.  This is needed for when rows are added to the middle of
     * the list to maintain the correct rows per objects.
     */
    private void remapIndexes(int start) {
        remapIndexes(start, getRowCount());
    }        
    
    /**
     * Remaps the indexes, starting at 'start' and going to 'end'.
     * This is useful for when we move a row from end to start or vice versa.
     */
    private void remapIndexes(int start, int end) {
        for (int i = start; i < end; i++) {
            URN sha1 = getSHA1(i);
            if(sha1 != null)
                _indexes.put(sha1, new Integer(i));
        }
    }
    
    /**
     * Gets the SHA1 URN for a row.
     */
    private URN getSHA1(int idx) {
        if(idx >= getRowCount())
            return null;
        return ((TableLine)get(idx)).getSHA1Urn();
    }
    
    /**
     * Compares the count between two rows.
     */
    private int compareCount(TableLine a, TableLine b) {
        int c1 = a.getLocationCount();
        int c2 = b.getLocationCount();
        if(a.getQuality() == QualityRenderer.MULTICAST_QUALITY)
            c1 = Integer.MAX_VALUE;
        if(b.getQuality() == QualityRenderer.MULTICAST_QUALITY)
            c2 = Integer.MAX_VALUE;
        return (c1 - c2) * _ascending;
    }
    
    /**
     * Slow match -- file/size lookups.
     */
    private int slowMatch(SearchResult sr) {
        if(_grouper == null)
            _grouper = new TableLineGrouper();
        
        // OK we created a Line out of a response.
        // Do the grouping.  This is expensive!  May return null.
        SearchResult group = _grouper.match(sr);
        if (group == null)
            _grouper.add(sr);
            
        return super.getRow(group);
    }
    
    /**
     * Fast match -- lookup in the table.
     */
    private int fastMatch(URN sha1) {
        Integer idx = (Integer)_indexes.get(sha1);
        if(idx == null)
            return -1;
        else
            return idx.intValue();
    }        
}

