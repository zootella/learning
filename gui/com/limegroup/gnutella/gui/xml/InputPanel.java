package com.limegroup.gnutella.gui.xml;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import com.limegroup.gnutella.gui.AutoCompleteTextField;
import com.limegroup.gnutella.util.I18NConvert;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.SchemaFieldInfo;

/**
 * A panel that is used to gather information from the user about
 * what the search criterion based on a particular schema.
 * <p>
 * This Panel is popped up everytime the user want to enter a query and has
 * specified the schema that she would like to base her search on
 *
 * @author Sumeet Thadani
 */
public class InputPanel extends IndentingPanel {
   
    private final LimeXMLSchema SCHEMA;

    public InputPanel(LimeXMLSchema schema, ActionListener listener,
                      Document document, UndoManager undoer) {
        this(schema, listener, document, undoer, false, false, true);
    }
    
    public InputPanel(LimeXMLSchema schema, ActionListener listener,
                      Document document, UndoManager undoer,
                      boolean expand, boolean indent, boolean search) {
        super(schema, listener, document, undoer, expand, indent, search);
        SCHEMA = schema;
    }

    /**
     * @return The Schema URI associated with this InputPanel
     */
    public String getSchemaURI() {
        return SCHEMA.getSchemaURI();
    }

    public String getInput() {
        return getInput(false);
    }

    /**
     * Looks at the textFields and creates a string that can be converted 
     * into LimeXMLDocument, so that the client that receives the search 
     * sting is can compare it with documents in its repository.
     * 
     * @param normalize true if the returned string should be normalized, thisis
     * the case when the user is doing a rich query. Otherwise if annotating,
     * metadata the string need not be normalized.
     */
    public String getInput(boolean normalize) {
        List namValList = new LinkedList();
        List list = SCHEMA.getCanonicalizedFields();
        for(int i = 0; i < list.size(); i++) {
            SchemaFieldInfo field = (SchemaFieldInfo)list.get(i);
            String key = field.getCanonicalizedFieldName();
            JComponent comp = getField(key);
            String value = "";
            if (comp instanceof JTextField) {
                JTextField theField = (JTextField)comp;
                value = theField.getText();
            } else if (comp instanceof JComboBox) {
                JComboBox theBox = (JComboBox)comp;
                value = ((ComboBoxValue)theBox.getSelectedItem()).getValue();
            }
            if (value != null && !value.equals("")) {
                NameValue namValue = 
                    new NameValue(key,
                                  normalize?
                                  I18NConvert.instance().getNorm(value): value);
                namValList.add(namValue);
            }
        }
        String schemaURI = SCHEMA.getSchemaURI();
        String str = constructXML(namValList, schemaURI);
        return str;
    }

    /**
     * Scan through all the AutoTextField components
     * and store the input into their dictionaries.
     */
    public void storeInput() {
        List list = SCHEMA.getCanonicalizedFields();
        for(int i = 0; i < list.size(); i++) {
            SchemaFieldInfo field = (SchemaFieldInfo)list.get(i);
            String key = field.getCanonicalizedFieldName();
            JComponent comp = getField(key);
            if (comp instanceof AutoCompleteTextField) {
                AutoCompleteTextField theField = (AutoCompleteTextField)comp;
                if (!theField.getText().equals(""))
                    theField.addToDictionary();
            }
        }
    }
    
    /**
     * @return A string the represents a standard query (as opposed to a rich
     * query).
     * <p>
     * The order in which it checks for fields is schema specific.
     */
    public String getStandardQuery() {
        List list = SCHEMA.getCanonicalizedFields();
        StringBuffer retString = new StringBuffer();
        int numWords = 0;
        for (int i = 0; i < list.size() && numWords < 3; i++) {
            SchemaFieldInfo field = (SchemaFieldInfo)list.get(i);
            String key = field.getCanonicalizedFieldName();
            JComponent comp = getField(key);
            String value = "";
            if (comp instanceof JTextField) {
                JTextField theField = (JTextField)comp;
                value = theField.getText();
            } else if (comp instanceof JComboBox) {
                JComboBox theBox = (JComboBox)comp;
                value = ((ComboBoxValue)theBox.getSelectedItem()).toString();
            }
            if (value != null && value.trim().length() > 1) {
                retString.append(value + " ");
            }
        }
        return retString.toString();
    }

    /**
     * Deligates to the the static method in LimeXMLDocument
     */
    public String constructXML(List namValList, String uri) {
        if(namValList == null || namValList.isEmpty())
            return null;
        else
            return new LimeXMLDocument(namValList, uri).getXMLString();
    }
}
