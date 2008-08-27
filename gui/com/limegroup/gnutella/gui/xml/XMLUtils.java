package com.limegroup.gnutella.gui.xml;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.SchemaFieldInfo;
import com.limegroup.gnutella.xml.XMLStringUtils;
import com.limegroup.gnutella.util.NameValue;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Various GUI-related XML utilities.
 */
public class XMLUtils {
    
    private static final Log LOG = LogFactory.getLog(XMLUtils.class);
    
    /**
     * A mapping of Schema -> ResourceBundle for that schema.
     * These ResourceBundles are only used (and only created)
     * if someone attempts to retrieve a resource that does not exist
     * in the main ResourceBundle.
     */
    private static Map bundles;
    
    private XMLUtils() {}
    
    /**
     * Returns a list of name/values for this document, suitable for display.
     */
    public static List getDisplayList(LimeXMLDocument doc) {
        List data = new LinkedList();
            
        LimeXMLSchema schema = doc.getSchema();
        List fields = schema.getCanonicalizedFields();
        
        // For each name/value pair...
        for( Iterator j = fields.iterator(); j.hasNext(); ) {
            SchemaFieldInfo sfi = (SchemaFieldInfo)j.next();
            String name = sfi.getCanonicalizedFieldName();
            String value = doc.getValue(name);
            if(value == null || sfi.isHidden())
                continue;
            
			NameValue pair = getDisplayPair(sfi, value, schema);
            
            data.add(pair.getName() + ": " + pair.getValue());
        }
        
        return data;
    }
	
	/**
	 * Returns the NameValue pair of the display name for the field
	 * and a valid visual representation of the value.
	 */
	public static NameValue getDisplayPair(SchemaFieldInfo field, String value, LimeXMLSchema schema) {
		String name = getResource(field.getCanonicalizedFieldName());
	    if(field.getJavaType() == Date.class) {
            try {
                value = GUIUtils.seconds2time(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {}
        }
        
		return new NameValue(name, value);
	}
	
	/**
	 * Gets the resource for the given string.
	 */
	public static String getResource(String field) {
	    try {
	        return GUIMediator.getStringResource("XML_" + field);
	    } catch(MissingResourceException mre) {
	        if(LOG.isWarnEnabled())
	            LOG.warn("Missing main resource for: " + field + ", falling back.", mre);
            return fallbackResource(field, getBundleForField(field));
        }
    }
    
    /**
     * Gets the title of the schema based on the field.
     */
    public static String getTitleForSchemaFromField(String field) {
        // not an XML field?  ignore.
        if(!field.endsWith("__"))
            return null;
            
        // The canonicalKey is always going to be x__x__<other stuff here>
        int idx1 = field.indexOf(XMLStringUtils.DELIMITER) + 2;
        int idx2 = field.indexOf(XMLStringUtils.DELIMITER, idx1);
        return getResource(field.substring(0, idx2));
    }
        
    
    /**
     * Gets the correct display name for the given schemaURI.
     */
    public static String getTitleForSchemaURI(String schemaURI) {
        LimeXMLSchema schema = LimeXMLSchemaRepository.instance().getSchema(schemaURI);
        if(schema != null)
            return getTitleForSchema(schema);
        else
            return null;
    }
    
    /**
     * Gets the correct display name for the given schema.
     */
    public static String getTitleForSchema(LimeXMLSchema schema) {
        return getResource(schema.getRootXMLName() + XMLStringUtils.DELIMITER + schema.getInnerXMLName());
    }
    
    /**
     * Gets the resource bundle for the given field name.
     */
    private static ResourceBundle getBundleForField(String field) {
        if(bundles == null)
            loadBundles();
            
        return (ResourceBundle)bundles.get(getDescriptionFromField(field));
    }
    
    /**
     * Gets the description of the schema from a field name.
     *
     * That is, where the field is called audios__audio__field,
     * the description this returns is "audio".
     */
    private static String getDescriptionFromField(String field) {
        // The canonicalKey is always going to be x__x__<other stuff here>
        int idx1 = field.indexOf(XMLStringUtils.DELIMITER) + 2;
        int idx2 = field.indexOf(XMLStringUtils.DELIMITER, idx1);
        if(idx2 == -1)
            idx2 = field.length();
        return field.substring(idx1, idx2);
    }
    
    /**
     * Populates the bundles map with the resource bundles we know about.
     */
    private static void loadBundles() {
        bundles = new HashMap();
        Collection schemas = LimeXMLSchemaRepository.instance().getAvailableSchemas();
        for(Iterator i = schemas.iterator(); i.hasNext(); ) {
            LimeXMLSchema schema = (LimeXMLSchema)i.next();
            String key = schema.getDescription();
            try {
                bundles.put(key, GUIMediator.getXMLResourceBundle(key));
            } catch(MissingResourceException mre) {
                if(LOG.isWarnEnabled())
                    LOG.warn("Missing resource bundle for schema: " + key, mre);
            }
        }
    }   
    
    /**
     * Gets the resource from the XML resource bundles for that field.
     */
    private static String fallbackResource(String field, ResourceBundle bundle) {
        if(bundle != null) {
            try {
                return bundle.getString(field);
            } catch(MissingResourceException mre) {
                if(LOG.isWarnEnabled())
                    LOG.warn("Missing fallback resource for: " + field + ", capitalizing field name.", mre);
            }
        }
        
        return processField(field);
    }
    
    /**
     * Capitalizes the first letter of the string and converts '_' to ' '.
     *
     * That is, where the name is "field_name", this will return "Field name".
     */
    private static String formatFieldName(String name) {
        return name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1).replace('_', ' ').trim();
    }

    /**
     * Determines the field's name based on the field.
     *
     * That is, where the field is audios__audio__field,
     * this will return "Field".
     */
    private static String processField(String field) {
        int endIdx, startIdx;
        if(field.endsWith(XMLStringUtils.DELIMITER))
            endIdx = field.length() - 2; // 2 == XMLStringUtils.DELIMITER.length()
        else
            endIdx = field.length();
            
        startIdx = field.lastIndexOf(XMLStringUtils.DELIMITER, endIdx-1) + 2;
        return formatFieldName(field.substring(startIdx, endIdx));
    }
}