package com.limegroup.gnutella.gui.xml.editor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.text.JTextComponent;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLReplyCollection;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.SchemaReplyCollectionMapper;
import com.limegroup.gnutella.gui.xml.ComboBoxValue;

public abstract class MetaEditorTabbedPane extends JTabbedPane {
    
    protected final FileDesc fd;
    protected final LimeXMLDocument document;
    protected final LimeXMLSchema schema;
    
    public MetaEditorTabbedPane(FileDesc fd, String uri) {
        super();
        
        this.fd = fd;
        
        SchemaReplyCollectionMapper map = SchemaReplyCollectionMapper.instance();
        LimeXMLReplyCollection collection = map.getReplyCollection(uri);
        LimeXMLSchemaRepository rep = LimeXMLSchemaRepository.instance();
        schema = rep.getSchema(uri);
        LimeXMLDocument storedDoc = null;
        for(Iterator i = fd.getLimeXMLDocuments().iterator(); i.hasNext(); ) {
            LimeXMLDocument doc = (LimeXMLDocument)i.next();
            if(schema.equals(doc.getSchema())) {
                storedDoc = doc;
                break;
            }
        }
        document = storedDoc;
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
    
    public String getInput() {
        java.util.ArrayList namValList = new java.util.ArrayList();

        final int count = getTabCount();
        for(int i = 0; i < count; i++) {
            
            Component tab = getComponentAt(i);
            if (tab instanceof AbstractMetaEditorPanel) {
            	AbstractMetaEditorPanel panel = (AbstractMetaEditorPanel)tab;
            	List list = panel.getInput();
            	if(list!=null)namValList.addAll(list);
            }
            
        }
        
        if (namValList.isEmpty()) {
            return null;
        } else {
            return new LimeXMLDocument(namValList, getSchema().getSchemaURI()).getXMLString();
        }
    }
    
    public boolean checkInput() {
		 final int count = getTabCount();
		 boolean inputValid = true;
	     for(int i = 0; i < count; i++) {
	    	 Component tab = getComponentAt(i);
	    	 if (tab instanceof AbstractMetaEditorPanel) {
	    		 AbstractMetaEditorPanel publisher = (AbstractMetaEditorPanel) tab;
	    		 inputValid &= publisher.checkInput();
	    	 }
	    	 //any other tab should check input here
	     }
	     return inputValid;
   }
    
   public List getFileEventListeners() {
	   ArrayList listenerList = new ArrayList();
	   final int count = getTabCount();
       for(int i = 0; i < count; i++) {
           Component tab = getComponentAt(i);
           if (tab instanceof AbstractMetaEditorPanel) {
        	   AbstractMetaEditorPanel panel = (AbstractMetaEditorPanel)tab;
        	   FileEventListener listener = panel.getFileEventListener();
        	   if(listener != null) listenerList.add(listener);
           }
       }
       return listenerList;
   }
}
