package com.limegroup.gnutella.gui.search;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.SavedFileManager;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.gui.tables.AbstractDataLine;
import com.limegroup.gnutella.gui.tables.IconAndNameHolder;
import com.limegroup.gnutella.gui.tables.IconAndNameHolderImpl;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.SizeHolder;
import com.limegroup.gnutella.licenses.License;
import com.limegroup.gnutella.licenses.LicenseFactory;
import com.limegroup.gnutella.search.HostData;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.gui.xml.XMLUtils;

/** 
 * A single line of a search result.
 */
public final class TableLine extends AbstractDataLine {

    /**
     * The SearchTableColumns.
     */
    private final SearchTableColumns COLUMNS;
    
    /**
     * The SearchResult that created this particular line.
     */
    private SearchResult RESULT;
    
    /**
     * The list of other SearchResults that match this line.
     */
    private List _otherResults;
    
    /**
     * The SHA1 of this line.
     */
    private URN _sha1;
    
    /**
     * The media type of this document.
     */
    private NamedMediaType _mediaType;
    
    /**
     * The set of other locations that have this result.
     */
    private Set _alts;

    /**
     * Whether or not this file is saved in the library.
     */
    private boolean _savedFile;
    
    /**
     * Whether or not this file is incomplete.
     */
    private boolean _incompleteFile;
    
    /**
     * Whether or not this file was downloading the last time we checked.
     */
    private boolean _downloading;

    /**
     * The speed of this line.
     */
    private ResultSpeed _speed = null;
    
    /**
     * The quality of this line.
     */
    private int _quality;
    
    /**
     * A chat enabled host if there is one.
     */
    private RemoteFileDesc _chatHost;
    
    /**
     * A browse enabled host if there is one.
     */
    private RemoteFileDesc _browseHost;
    
	/**
	 * A non firewalled host if there is one.
	 */
	private RemoteFileDesc _nonFirewalledHost;
	
    /**
     * The LimeXMLDocument for this line.
     */
    private LimeXMLDocument _doc;
    
    /**
     * The location of this line.
     */
    private EndpointHolder _location = null;
    
    /**
     * The date this was added to the network.
     */
    private long _addedOn;
    
    /** License info. */
    private int _licenseState = License.NO_LICENSE;
    private String _licenseName = null;
    
    public TableLine(SearchTableColumns stc) {
        COLUMNS = stc;
    }
    
    /**
     * Initializes this line with the specified search result.
     */
    public void initialize(Object init) {
        super.initialize(init);
        
        SearchResult sr = (SearchResult)init;
        RemoteFileDesc rfd = sr.getRemoteFileDesc();
        HostData data = sr.getHostData();
        Set alts = sr.getAlts();

        RESULT = sr;
        _doc = rfd.getXMLDocument();
        _sha1 = rfd.getSHA1Urn();
        if(_doc != null)
            _mediaType = NamedMediaType.getFromDescription(
                                _doc.getSchemaDescription());
        else
            _mediaType = NamedMediaType.getFromExtension(getExtension());
        _speed = new ResultSpeed(rfd.getSpeed(), data.isMeasuredSpeed());
        _quality = rfd.getQuality();
		if (rfd.chatEnabled()) {
			_chatHost = rfd;
		}
		if (rfd.browseHostEnabled()) {
			_browseHost = rfd;
		}
		if (!rfd.isFirewalled()) {
			_nonFirewalledHost = rfd;
		}
        _location = new EndpointHolder(
            rfd.getHost(), rfd.getPort(),
            rfd.isReplyToMulticast());
        _addedOn = rfd.getCreationTime();

        if(alts != null && !alts.isEmpty()) {
            if(_alts == null)
                _alts = new HashSet();
            _alts.addAll(alts);
            sr.clearAlts();
            _location.addHosts(alts);
        }
        
        updateLicense();        
        updateFileStatus();        
    }
    
    /**
     * Adds a new SearchResult to this TableLine.
     */
    void addNewResult(SearchResult sr, MetadataModel mm) {
        RemoteFileDesc rfd = sr.getRemoteFileDesc();
        HostData data = sr.getHostData();
        Set alts = sr.getAlts();

        URN resultSHA1 = RESULT.getRemoteFileDesc().getSHA1Urn();
        URN thisSHA1 = rfd.getSHA1Urn();
        if(resultSHA1 == null)
            Assert.that(thisSHA1 == null);
        else
            Assert.that(resultSHA1.equals(thisSHA1));

        if(_otherResults == null)
            _otherResults = new LinkedList();
        _otherResults.add(sr);
        

        if(alts != null && !alts.isEmpty()) {
            if(_alts == null)
                _alts = new HashSet();
            _alts.addAll(alts);
            sr.clearAlts();
            _location.addHosts(alts);
        }
        _location.addHost(rfd.getHost(), rfd.getPort());
        
        // Set the speed correctly.
        ResultSpeed newSpeed = new ResultSpeed(rfd.getSpeed(), data.isMeasuredSpeed());
        // if we're changing a property, update the metadata model.
        if(_speed.compareTo(newSpeed) < 0) {
            if(mm != null)
                mm.updateProperty(MetadataModel.SPEED, _speed, newSpeed, this);
            _speed = newSpeed;
        }
        
        // Set the quality correctly.
        _quality = Math.max(rfd.getQuality(), _quality);
        
        if(rfd.getCreationTime() > 0)
            _addedOn = Math.min(_addedOn, rfd.getCreationTime());
                                  
        // Set chat host correctly.
        if (_chatHost == null && rfd.chatEnabled()) {
			_chatHost = rfd;
        }
        // Set browse host correctly.
		if (_browseHost == null && rfd.browseHostEnabled()) {
			_browseHost = rfd;
		}
		if (_nonFirewalledHost == null && !rfd.isFirewalled()) {
			_nonFirewalledHost = rfd;
		}
        
        updateXMLDocument(rfd.getXMLDocument(), mm);
    }
    
    /**
     * Updates the XMLDocument and the MetadataModel.
     */
    private void updateXMLDocument(LimeXMLDocument newDoc, MetadataModel mm) {
        // If nothing new, nothing to do.
        if(newDoc == null)
            return;
        
        // If no document exists, just set it to be the new doc
        if(_doc == null) {
            _doc = newDoc;
            updateLicense();
            if(mm != null) {
                _mediaType = NamedMediaType.getFromDescription(
                                _doc.getSchemaDescription());
                mm.addNewDocument(_doc, this);
            }
            return;
        }
        
        
        // Otherwise, if a document does exist in the group, see if the line
        // has extra fields that can be added to the group.
        
        // Must have the same schema...
        if(!_doc.getSchemaURI().equals(newDoc.getSchemaURI()))
            return;
        
        Set oldKeys = _doc.getNameSet();
        Set newKeys = newDoc.getNameSet();
        // if the we already have everything in new, do nothing
        if(oldKeys.containsAll(newKeys))
            return;

        // Now we want to add the values of newKeys that weren't
        // already in oldKeys.
        newKeys = new HashSet(newKeys);
        newKeys.removeAll(oldKeys);
        // newKeys now only has brand new elements.
        Map newMap = new HashMap(oldKeys.size() + newKeys.size());
        for(Iterator i = _doc.getNameValueSet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            newMap.put(entry.getKey(), entry.getValue());
        }
        for(Iterator i = newKeys.iterator(); i.hasNext();) {
            String key = (String)i.next();
            String value = newDoc.getValue(key);
            newMap.put(key, value);
            if(mm != null)
                mm.addField(key, value, this);
        }

        _doc = new LimeXMLDocument(newMap.entrySet(), _doc.getSchemaURI());
        updateLicense();
    }

    /**
     * Updates the file status of this line.
     */
    private void updateFileStatus() {
        if(_sha1 != null) {
            _savedFile =
                RouterService.getFileManager().isUrnShared(_sha1);
            _incompleteFile =
                RouterService.getDownloadManager().isIncomplete(_sha1);
        } else {
            _savedFile = false;
            _incompleteFile = false;
        }
        if(!_savedFile) {
            _savedFile =
                SavedFileManager.instance().isSaved(_sha1, getFilename());
        }
    }
    
    /**
     * Updates cached data about this line.
     */
    public void update() {
        updateLicense();
    }
    
    /**
     * Updates the license status.
     */
    private void updateLicense() {
        if(_doc != null && _sha1 != null) {
            String licenseString = _doc.getLicenseString();
            if(licenseString != null) {
                if(LicenseFactory.isVerifiedAndValid(_sha1, licenseString))
                    _licenseState = License.VERIFIED;
                else
                    _licenseState = License.UNVERIFIED;
                _licenseName = LicenseFactory.getLicenseName(licenseString);
            }
        }
    }
    
    /**
     * Determines if a license is available.
     */
    boolean isLicenseAvailable() {
        return _licenseState != License.NO_LICENSE;
    }
    
    /**
     * Gets the license associated with this line.
     */
    License getLicense() {
        if(_doc != null && _sha1 != null)
            return _doc.getLicense();
        else
            return null;
    }
    
    /**
     * Gets the SHA1 urn of this line.
     */
    URN getSHA1Urn() { 
        return _sha1;
    }
    
    /**
     * Gets the speed of this line.
     */
    ResultSpeed getSpeed() {
        return _speed;
    }
    
    /**
     * Gets the creation time.
     */
    Date getAddedOn() {
        if(_addedOn > 0)
            return new Date(_addedOn);
        else
            return null;
    }
    
    /**
     * Gets the quality of this line.
     */
    int getQuality() {
        RemoteFileDesc rfd = RESULT.getRemoteFileDesc();
        boolean downloading = rfd.isDownloading();
        if(downloading != _downloading)
            updateFileStatus();
        _downloading = downloading;
        
        if(_savedFile)
            return QualityRenderer.SAVED_FILE_QUALITY;
        else if(downloading)
            return QualityRenderer.DOWNLOADING_FILE_QUALITY;
        else if(_incompleteFile)
            return QualityRenderer.INCOMPLETE_FILE_QUALITY;
        else
            return _quality;
    }
    
    /**
     * Returns the NamedMediaType.
     */
    NamedMediaType getNamedMediaType() {
        return _mediaType;
    }
    
    /**
     * Gets the LimeXMLDocument for this line.
     */
    LimeXMLDocument getXMLDocument() {
        return _doc;
    }
    
    /**
     * Gets the EndpointHolder holding locations.
     */
    EndpointHolder getLocation() {
        return _location;
    }
    
    /**
     * Gets the other results for this line.
     */
    List getOtherResults() {
        return _otherResults == null ? Collections.EMPTY_LIST : _otherResults;
    }
    
    /**
     * Gets the alternate locations for this line.
     */
    Set getAlts() {
        return _alts == null ? Collections.EMPTY_SET : _alts;
    }
    
    /**
     * Gets the number of locations this line holds.
     */
    int getLocationCount() {
        return _location.numLocations();
    }
    
    /**
     * Determines whether or not chat is enabled.
     */
    boolean isChatEnabled() {
        return _chatHost != null;
    }
    
    /**
     * Determines whether or not browse host is enabled.
     */
    boolean isBrowseHostEnabled() {
        return _browseHost != null;
    }
	
	/**
	 * Determines whether there is a non firewalled host for this result.
	 */
	boolean hasNonFirewalledRFD() {
		return _nonFirewalledHost != null;
	}
    
    /**
     * Determines if this line is launchable.
     */
    boolean isLaunchable() {
        return _doc != null && _doc.getAction() != null &&
                               !"".equals(_doc.getAction());
    }
    
    /**
     * Gets the filename without the extension.
     */
    String getFilenameNoExtension() {
        return RESULT.getFilenameNoExtension();
    }
    
    /**
     * Returns the icon & extension.
     */
    IconAndNameHolder getIconAndExtension() {
        String ext = getExtension();
        return new IconAndNameHolderImpl(
                IconManager.instance().getIconForExtension(ext), ext);
    }
    
    /**
     * Returns the icon.
     */
    Icon getIcon() {
        String ext = getExtension();
        return IconManager.instance().getIconForExtension(ext);
    }

    /**
     * Returns the extension of this result.
     */
    String getExtension() {
        return RESULT.getExtension();
    }

    /**
     * Returns this filename, as passed to the constructor.  Limitation:
     * if the original filename was "a.", the returned value will be
     * "a".
     */
    String getFilename() {
        return RESULT.getRemoteFileDesc().getFileName();
    }
    
    /**
     * Gets the size of this TableLine.
     */
    int getSize() {
        return RESULT.getSize();
    }

    /**
     * Returns the vendor code of the result.
     */
    String getVendor() {
        return RESULT.getRemoteFileDesc().getVendor();
    }
    
    /**
     * Gets the LimeTableColumn for this column.
     */
    public LimeTableColumn getColumn(int idx) {
        return COLUMNS.getColumn(idx);
    }
    
    /**
     * Returns the number of columns.
     */
    public int getColumnCount() {
        return SearchTableColumns.COLUMN_COUNT;
    }    
    
    /**
     * Determines if the column is dynamic.
     */
    public boolean isDynamic(int idx) {
        return false;
    }
    
    /**
     * Determines if the column is clippable.
     */
    public boolean isClippable(int idx) {
        switch(idx) {
        case SearchTableColumns.QUALITY_IDX: 
        case SearchTableColumns.COUNT_IDX:
        case SearchTableColumns.ICON_IDX: 
        case SearchTableColumns.CHAT_IDX:
        case SearchTableColumns.LICENSE_IDX:
            return false;
        default:
            return true;
        }
    }
    
    public int getTypeAheadColumn() {
        return SearchTableColumns.NAME_IDX;
    }

    /**
     * Gets the value for the specified idx.
     */
    public Object getValueAt(int index){
        switch (index) {
        case SearchTableColumns.QUALITY_IDX: return new Integer(getQuality());
        case SearchTableColumns.COUNT_IDX:
            int count = _location.numLocations();
            if(count == 1)
                return null;
            else
                return new Integer(count);
        case SearchTableColumns.ICON_IDX: return getIcon();
        case SearchTableColumns.NAME_IDX: return getFilenameNoExtension();
        case SearchTableColumns.TYPE_IDX: return getExtension();
        case SearchTableColumns.SIZE_IDX: return new SizeHolder(getSize());
        case SearchTableColumns.SPEED_IDX: return getSpeed();
        case SearchTableColumns.CHAT_IDX: return isChatEnabled() ? Boolean.TRUE : Boolean.FALSE;
        case SearchTableColumns.LOCATION_IDX: return getLocation();
        case SearchTableColumns.VENDOR_IDX: return RESULT.getRemoteFileDesc().getVendor();
        case SearchTableColumns.ADDED_IDX: return getAddedOn();
        case SearchTableColumns.LICENSE_IDX: return new NameValue(_licenseName, new Integer(_licenseState));
        default:
            if(_doc == null)
                return null;
            // Look up the value in the doc.
            // The id of the LimeTableColumn is the field.
            LimeTableColumn ltc = getColumn(index);
            return _doc.getValue(ltc.getId());
        }
    }
    
    /**
     * Returns the XMLDocument as a tool tip.
     */
    public String[] getToolTipArray(int col) {
        // only works on windows, which gives good toString descriptions
        // of its native file icons.
        if(col == SearchTableColumns.ICON_IDX && CommonUtils.isWindows()) {
            Icon icon = getIcon();
            if(icon != null)
                return new String[] { icon.toString() };
            else
                return null;
        }
        // if we're on the location column and we've got multiple results,
        // list them all out.
        if(col == SearchTableColumns.LOCATION_IDX && getLocationCount() > 1) {
            StringBuffer sb = new StringBuffer(3 * 23);
            List retList = new LinkedList();
            Iterator iter = _location.getHosts().iterator();
            for(int i = 0; iter.hasNext(); i++) {
                if(i == 3) {
                    i = 0;
                    retList.add(sb.toString());
                    sb = new StringBuffer(3 * 23);
                } 
                sb.append(iter.next());
                if(iter.hasNext())
                    sb.append(", ");
                else
                    retList.add(sb.toString());
            }
            return (String[])retList.toArray(new String[retList.size()]);
        }    
        
        if(_doc == null) {
            return null;
        }
        
        List data = XMLUtils.getDisplayList(_doc);
        if ( data != null && !data.isEmpty() ) {
            // if it had meta-data, display the filename in the tooltip also.
            data.add(0, getFilenameNoExtension());
            return (String[])data.toArray(new String[data.size()]);
	    } else {
	        return null;
	    }
    }
    
    /**
     * Gets the main result's host.
     */
    String getHostname() {
        return RESULT.getRemoteFileDesc().getHost();
    }
    
    /**
     * Gets all RemoteFileDescs for this line.
     */
    RemoteFileDesc[] getAllRemoteFileDescs() {
        int size = getOtherResults().size() + 1;
        RemoteFileDesc[] rfds = new RemoteFileDesc[size];
        rfds[0] = RESULT.getRemoteFileDesc();
        int j = 1;
        for(Iterator i = getOtherResults().iterator(); i.hasNext(); j++)
            rfds[j] = ((SearchResult)i.next()).getRemoteFileDesc();
        return rfds;
    }
    
    /**
     * Does a chat.
     */
    void doChat() {
		if (_chatHost != null && _chatHost.getHost() != null 
				&& _chatHost.getPort() != -1) {
			RouterService.createChat(_chatHost.getHost(), _chatHost.getPort());
		}
    }
    
	/**
	 * Returns the rfd of the search result for which this download was enabled
	 * @return
	 */
	RemoteFileDesc getRemoteFileDesc() {
		return RESULT.getRemoteFileDesc();
	}
	
    /**
     * Gets the first browse-host enabled RFD or <code>null</code>.
     */
    RemoteFileDesc getBrowseHostEnabledRFD() {
        return _browseHost;
    }
	
	/**
	 * Returns the first non-firewalled rfd for this result or <code>null</code>.
	 */
	RemoteFileDesc getNonFirewalledRFD() {
		return _nonFirewalledHost;
	}
	
	/**
	 * Returns the first chat enabled rfd for this result or <code>null</code>.
	 */
	RemoteFileDesc getChatEnabledRFD() {
		return _chatHost;
	}
}
