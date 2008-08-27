package com.limegroup.gnutella.gui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import com.limegroup.gnutella.licenses.License;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.tables.ChatHolder;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.xml.XMLUtils;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.SchemaFieldInfo;

/**
 * Simple collection of table columns.
 */
final class SearchTableColumns {
    
    // It is important that all the columns be non static,
    // so that the multiple search tables all have their own
    // columns.

    static final int QUALITY_IDX = 0;
    private final LimeTableColumn QUALITY_COLUMN =
        new SearchColumn(QUALITY_IDX, "RESULT_PANEL_QUALITY",
                            55, true,  QualityHolder.class);
    
    static final int COUNT_IDX = 1;
    private final LimeTableColumn COUNT_COLUMN =
        new SearchColumn(COUNT_IDX, "RESULT_PANEL_COUNT",
                            24, true,  Integer.class);
                            
    static final int LICENSE_IDX = 2;
    private final LimeTableColumn LICENSE_COLUMN =
        new SearchColumn(LICENSE_IDX, "RESULT_PANEL_LICENSE",
                            40, true, License.class);
    
    static final int ICON_IDX = 3;
    private final LimeTableColumn ICON_COLUMN =
        new SearchColumn(ICON_IDX, "RESULT_PANEL_ICON",
		    GUIMediator.getThemeImage("question_mark"),
                    18, true, Icon.class);
    
    static final int NAME_IDX = 4;
    private final LimeTableColumn NAME_COLUMN =
        new SearchColumn(NAME_IDX, "RESULT_PANEL_NAME",
                            272, true,  String.class);
                            
    static final int TYPE_IDX = 5;
    private final LimeTableColumn TYPE_COLUMN =
        new SearchColumn(TYPE_IDX, "RESULT_PANEL_TYPE",
                            42, true, String.class);

    static final int SIZE_IDX = 6;
    private final LimeTableColumn SIZE_COLUMN =
        new SearchColumn(SIZE_IDX, "RESULT_PANEL_SIZE",
                            53, true, String.class);
                            
    static final int SPEED_IDX = 7;
    private final LimeTableColumn SPEED_COLUMN =
        new SearchColumn(SPEED_IDX, "RESULT_PANEL_SPEED",
                            61, true, String.class);
    
    static final int CHAT_IDX = 8;
    private final LimeTableColumn CHAT_COLUMN =
        new SearchColumn(CHAT_IDX, "RESULT_PANEL_CHAT",
                            40, false, ChatHolder.class);
    
    static final int LOCATION_IDX = 9;
    private final LimeTableColumn LOCATION_COLUMN =
        new SearchColumn(LOCATION_IDX, "RESULT_PANEL_LOCATION",
                           86, false, EndpointHolder.class);
    
    static final int VENDOR_IDX = 10;
    private final LimeTableColumn VENDOR_COLUMN = 
        new SearchColumn(VENDOR_IDX, "RESULT_PANEL_VENDOR",
                            55, false, String.class);
                            
    static final int ADDED_IDX = 11;
    private final LimeTableColumn ADDED_COLUMN =
        new SearchColumn(ADDED_IDX, "RESULT_PANEL_ADDED",
                            55, false, Date.class);
                            
    /**
     * The number of default columns.
     */
    static final int DEFAULT_COLUMN_COUNT = 12;
    
    /**
     * The number of extra XML columns.
     */
    static final int EXTRA_COLUMN_COUNT;
    
    /**
     * The actual XML columns.
     */
    private final List EXTRA_COLUMNS = new ArrayList();
    
    /**
     * The total number of columns.
     */
    static final int COLUMN_COUNT;
    
    // initializes EXTRA_COLUMN_COUNT & COLUMN_COUNT.
    static {
        List columns = new LinkedList();
        addColumns(columns);
        EXTRA_COLUMN_COUNT = columns.size();
        COLUMN_COUNT = EXTRA_COLUMN_COUNT + DEFAULT_COLUMN_COUNT;
    }
    
    /**
     * Constructs a new SearchTableColumns.
     */
    SearchTableColumns() {
        addColumns(EXTRA_COLUMNS);
    }
    
    /**
     * Adds all available XML columns into the list.
     */
    private static void addColumns(List columns) {
        Collection schemas = LimeXMLSchemaRepository.instance().getAvailableSchemas();
        int idx = DEFAULT_COLUMN_COUNT;
        for(Iterator i = schemas.iterator(); i.hasNext(); ) {
            LimeXMLSchema schema = (LimeXMLSchema)i.next();
            String schemaDesc = schema.getDescription();
            Iterator fields = schema.getCanonicalizedFields().iterator();
            for(; fields.hasNext();) {
                SchemaFieldInfo sfi = (SchemaFieldInfo)fields.next();
                if(sfi.isHidden())
                    continue;
                
                String rawName = sfi.getCanonicalizedFieldName();
                String dispName = XMLUtils.getResource(rawName);
                Class clazz = String.class; //sfi.getJavaType();
                boolean visible = sfi.getDefaultVisibility();
                int width = sfi.getDefaultWidth();
                LimeTableColumn ltc = new SearchColumn(idx, rawName, dispName, width, visible, clazz);
                columns.add(ltc);
                idx++;
            }
        }
    }
    
    /**
     * Gets the column for the specified index.
     */
    LimeTableColumn getColumn(int idx) {
        switch (idx) {
        case QUALITY_IDX: return QUALITY_COLUMN;
        case COUNT_IDX: return COUNT_COLUMN;
        case ICON_IDX: return ICON_COLUMN;
        case NAME_IDX: return NAME_COLUMN;
        case TYPE_IDX: return TYPE_COLUMN;
        case SIZE_IDX: return SIZE_COLUMN;
        case SPEED_IDX: return SPEED_COLUMN;
        case CHAT_IDX: return CHAT_COLUMN;
        case LOCATION_IDX: return LOCATION_COLUMN;
        case VENDOR_IDX: return VENDOR_COLUMN;
        case ADDED_IDX: return ADDED_COLUMN;
        case LICENSE_IDX: return LICENSE_COLUMN;
        default:
            if(idx < COLUMN_COUNT)
                return (LimeTableColumn)EXTRA_COLUMNS.get(idx - DEFAULT_COLUMN_COUNT);
            throw new IllegalStateException("illegal idx: " + idx);
        }
    }
}    
    
