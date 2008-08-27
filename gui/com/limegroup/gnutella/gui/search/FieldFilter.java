package com.limegroup.gnutella.gui.search;

import com.limegroup.gnutella.xml.LimeXMLDocument;

/**
 * Returns true if the TableLine's field value
 * of a specific field matches.
*/
class FieldFilter implements TableLineFilter {
    private final String SCHEMA;
    private final String FIELD;
    private final String VALUE;
    
	FieldFilter(String schema, String field, String value) {
        if(schema == null)
            throw new NullPointerException("null schema");
        if(field == null)
            throw new NullPointerException("null field");
        if(value == null)
            throw new NullPointerException("null value");
        SCHEMA = schema;
        FIELD = field;
        VALUE = value;
    }
    
    public boolean allow(TableLine line) {
        LimeXMLDocument doc = line.getXMLDocument();
        return doc != null &&
               SCHEMA.equals(doc.getSchema().getDescription()) &&
               VALUE.equalsIgnoreCase(doc.getValue(FIELD));
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof FieldFilter))
            return false;
        else {
            FieldFilter other = (FieldFilter)o;
            return SCHEMA.equals(other.SCHEMA) &&
                   FIELD.equals(other.FIELD) &&
                   VALUE.equals(other.VALUE);
        }
    }       
}