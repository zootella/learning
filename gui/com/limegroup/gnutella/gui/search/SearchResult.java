package com.limegroup.gnutella.gui.search;

import java.util.Collections;
import java.util.Set;

import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.search.HostData;
import com.limegroup.gnutella.util.ApproximateMatcher;
import com.limegroup.gnutella.util.I18NConvert;
import com.limegroup.gnutella.settings.UISettings;

/**
 * A single SearchResult.
 *
 * (A collection of RemoteFileDesc, HostData, and Set of alternate locations.)
 */
final class SearchResult {
    private final RemoteFileDesc RFD;
    private final HostData DATA;
    private Set _alts;

    /** The processed version of the filename used for approximate matching.
     *  Not allocated until a match must be done.  The assumption here is that
     *  all matches will use the same ApproximateMatcher.  TODO3: when we move
     *  to Java 1.3, this should be a weak reference so the memory is reclaimed
     *  after GC. */
    private String processedFilename=null;    
    
    /**
     * Constructs a new SearchResult with the given data.
     */
    SearchResult(RemoteFileDesc rfd, HostData data, Set alts) {
        RFD = rfd;
        DATA = data;
        if(UISettings.UI_ADD_REPLY_ALT_LOCS.getValue())
            _alts = alts;
        else
            _alts = Collections.EMPTY_SET;
    }
    
    /** Gets the RemoteFileDesc */
    RemoteFileDesc getRemoteFileDesc() { return RFD; }
    
    /** Gets the HostData */
    HostData getHostData() { return DATA; }
    
    /** Gets the Alternate Locations */
    Set getAlts() { return _alts; }
    
    /**
     * Clears the alternate locations for this SearchResult.
     */
    void clearAlts() {
        _alts = null;
    }
    
    /**
     * Sets the alternate locations for this SearchResult.
     */
    void setAlts(Set alts) {
        _alts = alts;
    }
    
    /**
     * Gets the size of this SearchResult.
     */
    int getSize() {
        return RFD.getSize();
    }
    
    /**
     * Gets the filename without the extension.
     */
    String getFilenameNoExtension() {
        String fullname = RFD.getFileName();
        int i = fullname.lastIndexOf(".");
        if(i<0)
            return fullname;
        return I18NConvert.instance().compose(fullname.substring(0,i));
    }
    
    /**
     * Returns the extension of this result.
     */
    String getExtension() {
        String fullname = RFD.getFileName();
        int i = fullname.lastIndexOf(".");
        if(i<0)
            return "";
        return fullname.substring(i+1);
    }    
    
    /**
     * Gets the processed filename.
     */
    private String getProcessedFilename(ApproximateMatcher matcher) {
        if(processedFilename!=null)
            return processedFilename;
        processedFilename = matcher.process(getFilenameNoExtension());
        return processedFilename;
    }
    
    /** 
     * Compares this against o approximately:
     * <ul>
     *  <li> Returns 0 if o is similar to this. 
     *  <li> Returns 1 if they have non-similar extensions.
     *  <li> Returns 2 if they have non-similar sizes.
     *  <li> Returns 3 if they have non-similar names.
     * <ul>
     *
     * Design note: this takes an ApproximateMatcher as an argument so that many
     * comparisons may be done with the same matcher, greatly reducing the
     * number of allocations.<b>
     *
     * <b>This method is not thread-safe.</b>
     */
    int match(SearchResult o, final ApproximateMatcher matcher) {
        //Same file type?
        if (! getExtension().equals(o.getExtension()))
            return 1;

		long thisSize = getSize();
		long thatSize = o.getSize();

        // Sizes same?
        if(thisSize != thatSize)
            return 2;
            
        //Preprocess the processed fileNames
        getProcessedFilename(matcher);
        o.getProcessedFilename(matcher);
            
        //Filenames close?  This is the most expensive test, so it should go
        //last.  Allow 5% edit difference in filenames or 4 characters,
        //whichever is smaller.
        int allowedDifferences=Math.round(Math.min(
             0.10f*((float)getFilenameNoExtension().length()),
             0.10f*((float)o.getFilenameNoExtension().length())));
        allowedDifferences=Math.min(allowedDifferences, 4);
        if (! matcher.matches(getProcessedFilename(matcher), 
                              o.getProcessedFilename(matcher),
                              allowedDifferences))
            return 3;
        return 0;
    }
}