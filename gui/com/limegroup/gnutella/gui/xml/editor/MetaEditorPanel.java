package com.limegroup.gnutella.gui.xml.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.SchemaFieldInfo;
import com.limegroup.gnutella.gui.xml.ComboBoxValue;
import com.limegroup.gnutella.licenses.CCConstants;


public abstract class MetaEditorPanel extends AbstractMetaEditorPanel {
    
    private HashMap nameToComponent;
    private ArrayList checkboxes;
    
    protected final FileDesc fd;
    protected final LimeXMLDocument document;
    protected final LimeXMLSchema schema;
    protected final List uneditedNameValues;
    
    public MetaEditorPanel(FileDesc fd, LimeXMLSchema schema, LimeXMLDocument document) {
        this.fd = fd;
        this.schema = schema;
        this.document = document;
        
        nameToComponent = new HashMap();
        checkboxes = new ArrayList();
        uneditedNameValues = new LinkedList();
        addAllFields();
    }
    
    public FileDesc getFileDesc() {
        return fd;
    }
    
    public LimeXMLDocument getDocument() {
        return document;
    }
    
    public LimeXMLSchema getSchema() {
        return schema;
    }
    
    public String getValue(String name) {
        return (document != null) ? document.getValue(name) : null;
    }
    
    public void addComponent(String name, JComponent component) {
        addComponent(name, null, component);
    }
    
    public void addComponent(String name, JCheckBox checkbox, JComponent component) {
        nameToComponent.put(name, component);
        for(Iterator i = uneditedNameValues.iterator(); i.hasNext(); ) {
            NameValue nv = (NameValue)i.next();
            if(nv.getName().equals(name)) {
                i.remove();
                break;
            }
        }
        
        if (checkbox != null)
            link(checkbox, component);
    }
    
    public JComponent getComponent(String name) {
        return (JComponent)nameToComponent.get(name);
    }
    
    public Iterator getComponentIterator() {
        return nameToComponent.keySet().iterator();
    }
    
    public Iterator getUneditedFieldsIterator() {
        return uneditedNameValues.iterator();
    }
    
    private void link(final JCheckBox checkbox, JComponent comp) {
        if (comp instanceof JTextComponent) {
            ((JTextComponent)comp).addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent evt) {
                    checkbox.setSelected(true);
                }
            });
            
            checkboxes.add(checkbox);
            
        } else if (comp instanceof JComboBox) {
            
            JComboBox comboBox = (JComboBox)comp;
            
            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent evt) {
                    checkbox.setSelected(true);
                }
            });
            
            if (comboBox.isEditable()) {
                JTextComponent editor = (JTextComponent)comboBox.getEditor().getEditorComponent();
                editor.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent evt) {
                        checkbox.setSelected(true);
                    }
                });
            }
            
            checkboxes.add(checkbox);
        }
    }
    
    public boolean hasChanged() {
        Iterator it = checkboxes.iterator();
        while(it.hasNext()) {
            if (((JCheckBox)it.next()).isSelected())
                return true;
        }
        return false;
    }
    
    public void setCheckBoxesSelected(boolean selected) {
        Iterator it = checkboxes.iterator();
        while(it.hasNext()) {
            ((JCheckBox)it.next()).setSelected(selected);
        }
    }
    
    public void setCheckBoxesVisible(boolean visible) {
        Iterator it = checkboxes.iterator();
        while(it.hasNext()) {
            ((JCheckBox)it.next()).setVisible(visible);
        }
    }
    
    public void reset() {
        Iterator it = getComponentIterator();
        while(it.hasNext()) {
            String name = (String)it.next();
            JComponent comp = getComponent(name);
            
            if (comp instanceof JTextComponent) {
                ((JTextComponent)comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox)comp).setSelectedIndex(0);
            }
        }
        
        setCheckBoxesSelected(true);
    }
    
    public void prepareSave() {
    }
    
    public void initFields() {
    	Iterator it = getComponentIterator();
    	while(it.hasNext()) {
    		String name = (String)it.next();
    		String value = getValue(name);
    		JComponent comp = getComponent(name);
    		if(comp instanceof  JTextComponent) {
    			if(value!=null) {
    				((JTextComponent)comp).setText(value);
    				if(comp instanceof JTextArea) {
    					((JTextArea)comp).setCaretPosition(0);
    				}
    			}
    		}
    		else if(comp instanceof JComboBox) {
    			JComboBox box = (JComboBox)comp;
    			LimeXMLSchema schema = getSchema();
    			List enums = schema.getEnumerationFields();
    			Iterator it2 = enums.iterator();
    			while(it2.hasNext()) {
    				SchemaFieldInfo infoField = (SchemaFieldInfo)it2.next();
    				String currField = infoField.getCanonicalizedFieldName();
    				if(currField.equals(name)) {
    					ArrayList values = new ArrayList();
    					values.add(0, new ComboBoxValue());
    					addEnums(infoField.getEnumerationList(), values);
    					
    					int index = 0;
    					
    					if (value != null && !value.equals("")) {
    						ComboBoxValue combVal = new ComboBoxValue(value);
    						if (!values.contains(combVal))
    							values.add(combVal);
    					}
    					
    					if((name.indexOf("__licensetype__")>-1) && value!=null &&
    							!value.equals(CCConstants.CC_URI_PREFIX)) {
    						values.remove(new ComboBoxValue(CCConstants.CC_URI_PREFIX));
    					}
    					
    					Object[] arr = (Object[])values.toArray(new Object[0]);
    					Arrays.sort(arr);
    					if(value != null && !value.equals(""))
    						index = Arrays.asList(arr).indexOf(new ComboBoxValue(value));
    					
    					box.setModel(new DefaultComboBoxModel(arr));
    					box.setSelectedIndex(index);
    				}
    			}
    		}
    	}
    }
    
    public List getInput() {
    	ArrayList namValList = new ArrayList();
    	
    	prepareSave();
        
        Iterator it = getComponentIterator();
        while(it.hasNext()) {
            String name = (String)it.next();
            JComponent comp = getComponent(name);
            String value = null;
            
            if (comp instanceof JTextComponent) {
                value = ((JTextComponent)comp).getText().trim();
            } else if (comp instanceof JComboBox) {
                JComboBox box = (JComboBox)comp;
                ComboBoxValue cbv = (ComboBoxValue)box.getSelectedItem();
                if(cbv != null) {
                    String cbvalue = cbv.getValue();
                    if(cbvalue != null)
                        value = cbvalue.trim();
                }
            }

            if (value != null) {
                NameValue namValue = new NameValue(name, value);
                namValList.add(namValue);
            }
        }
        
        for(Iterator j = getUneditedFieldsIterator(); j.hasNext(); )
            namValList.add((NameValue)j.next());
        
        return namValList;
    }
    
    protected void addEnums(List nameValues, List comboValues) {
        for(Iterator i = nameValues.iterator(); i.hasNext(); )
            comboValues.add(new ComboBoxValue((NameValue)i.next()));
    }    
    
    private void addAllFields() {
        if(document != null) {
            for(Iterator i = document.getNameValueSet().iterator(); i.hasNext(); ) {
                Map.Entry next = (Map.Entry)i.next(); 
                uneditedNameValues.add(new NameValue((String)next.getKey(), next.getValue()));
            }
        }
    } 
    
    public boolean checkInput() {
    	return true;
    }

	public FileEventListener getFileEventListener() {
		return null;
	}
}
