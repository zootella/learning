package com.limegroup.gnutella.gui.xml;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;

public class EditingPanel extends InputPanel {
    
    private List uneditedFields = new LinkedList();

    public EditingPanel(LimeXMLSchema sch, LimeXMLDocument doc){
        super(sch, null, null, null, true, true, false);
        Set nameValues = doc.getNameValueSet();
        for(Iterator i = nameValues.iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            String name = ((String)entry.getKey()).toLowerCase(Locale.US);
            String value = (String)entry.getValue();
            JComponent comp = getField(name);
            if(comp == null) {
                uneditedFields.add(new NameValue(name, value));
            } else if (comp instanceof JTextField) {
                JTextField theField = (JTextField)comp;
                theField.setText(value);
            } else if (comp instanceof JComboBox) {
                JComboBox theComboBox = (JComboBox)comp;
                theComboBox.setSelectedItem(new ComboBoxValue(value));
            }
        }
    }
    
    public String constructXML(List nameValueList, String uri) {
        nameValueList.addAll(uneditedFields);
        return super.constructXML(nameValueList, uri);
    }
}
