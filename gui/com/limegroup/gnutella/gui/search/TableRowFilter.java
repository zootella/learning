package com.limegroup.gnutella.gui.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.gui.tables.DataLine;

/**
 * Filters out certain rows from the data model.
 *
 * @author Sumeet Thadani, Sam Berlin
 */
public final class TableRowFilter extends ResultPanelModel {
    
    private static final Log LOG = LogFactory.getLog(TableRowFilter.class);

    /**
     * The filter to use in this row filter.
     */
    private final TableLineFilter FILTER;

    /**
     * A list of all filtered results.
     */
    private final List HIDDEN;
    
    /**
     * The number of sources in the hidden list.
     */
    private int _numSources;

    /**
     * Constructs a TableRowFilter with the specified TableLineFilter.
     */
    public TableRowFilter(TableLineFilter f) {
        super();

        if(f == null)
            throw new NullPointerException("null filter");

        FILTER = f;
        HIDDEN = new LinkedList();
        _numSources = 0;
    }
    
    /**
     * Gets the amount of filtered sources.
     */
    public int getFilteredSources() {
        return super.getTotalSources();
    }
    
    /**
     * Gets the total amount of sources.
     */
    public int getTotalSources() {
        return getFilteredSources() + _numSources;
    }
    
    /**
     * Determines whether or not this line should be added.
     */
    public int add(DataLine line, int row) {
        TableLine tl = (TableLine)line;
        if(allow(tl)) {
            return super.add(line, row);
        } else {
            HIDDEN.add(tl);
            _numSources += tl.getLocationCount();
            if(_useMetadata)
                METADATA.addNew(tl);
            return -1;
        }
    }
    
    /**
     * Intercepts to clear the hidden map.
     */
    protected void simpleClear() {
        _numSources = 0;
        HIDDEN.clear();
        super.simpleClear();
    }
    
    /**
     * Notification that the filters have changed.
     */
    void filtersChanged() {
        rebuild();
        fireTableDataChanged();
    }
    
    /**
     * Determines whether or not the specified line is allowed by the filter.
     */
    private boolean allow(TableLine line) {
        return FILTER.allow(line);
    }
    
    /**
     * Rebuilds the internal map to denote a new filter.
     */
	private void rebuild(){
	    List existing = new ArrayList(_list);
	    List hidden = new ArrayList(HIDDEN);
	    simpleClear();
	    
	    setUseMetadata(false);
	    
	    // For stuff in _list, we can just re-add the DataLines as-is.
	    for(int i = 0; i < existing.size(); i++) {
	        if(isSorted())
	            addSorted((DataLine)existing.get(i));
	        else
	            add((DataLine)existing.get(i));
        }
        // For other stuff, we need to re-add the SearchResults
        // so the full DataLine can be properly built.
        for(int i = 0; i < hidden.size(); i++) {
            TableLine tl = (TableLine)hidden.get(i);
            SearchResult sr = (SearchResult)tl.getInitializeObject();
            sr.setAlts(tl.getAlts());
            List otherResults = tl.getOtherResults();
            if(isSorted()) {
                addSorted(sr);
                for(Iterator j = otherResults.iterator(); j.hasNext(); )
                    addSorted(j.next());
            } else {
                add(sr);
                for(Iterator j = otherResults.iterator(); j.hasNext(); )
                    add(j.next());
            }
        }
        
        setUseMetadata(true);
    }
}
