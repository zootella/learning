package com.limegroup.gnutella.gui.xml.editor;

import java.awt.Font;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.gui.MultiLineLabel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLReplyCollection;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.SchemaFieldInfo;
import com.limegroup.gnutella.xml.SchemaReplyCollectionMapper;


public class DetailsPanel extends JPanel {
    
    private int maxRows = 7;
    private Font boldFont = null;
    
    private ArrayList list = new ArrayList();
    
    public DetailsPanel() {
    }
    
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    
    private void add(String name, String value) {
        list.add(new NameValuePair(name, value));
    }
    
    public void initWithFileDesc(FileDesc fd, String schemaUri) {
        
        String kind = MetaEditorUtil.getKind(fd.getFile());
        
        if (kind != null) {
           add(GUIMediator.getStringResource("META_EDITOR_KIND_LABEL"), kind);
        }
        
        SchemaReplyCollectionMapper map = SchemaReplyCollectionMapper.instance();
        LimeXMLReplyCollection collection = map.getReplyCollection(schemaUri);
        LimeXMLDocument doc = collection.getDocForHash(fd.getSHA1Urn());
        
        LimeXMLSchemaRepository rep = LimeXMLSchemaRepository.instance();
        LimeXMLSchema schema = rep.getSchema(schemaUri);
        
        if (doc != null) {
            java.util.List fields = schema.getCanonicalizedFields();
            java.util.Iterator it = fields.iterator();
            while(it.hasNext()) {
                SchemaFieldInfo infoField = (SchemaFieldInfo)it.next();
                String field = infoField.getCanonicalizedFieldName();
                
                if (skipField(field))
                    continue;
                
                String value = doc.getValue(field);

                if (value != null && !value.equals("")) {
                    String name = MetaEditorUtil.getStringResource(field);
                    add(name, value);
                }
            }
        }
        
        String name = GUIMediator.getStringResource("META_EDITOR_SIZE_LABEL");
        String value = GUIUtils.toUnitbytes(fd.getFileSize());
        add(name, value);
        
        name = GUIMediator.getStringResource("META_EDITOR_DATE_MODIFIED_LABEL");
        value = GUIUtils.msec2DateTime(fd.lastModified());
        list.add(new NameValuePair(name, value));
        
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        
        for(Iterator it = list.iterator(); it.hasNext(); )
            addLabel((NameValuePair)it.next(), layout, c);
    }
    
    protected void addLabel(NameValuePair pair, GridBagLayout bag, GridBagConstraints c) {
        JLabel name = new JLabel(pair.name, SwingConstants.TRAILING);
        
        if (boldFont == null) {
            Font currentFont = name.getFont();
            boldFont = new Font(currentFont.getName(), Font.BOLD, currentFont.getSize());
        }
        name.setFont(boldFont);
        
        c.anchor = GridBagConstraints.NORTHEAST;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 0, 2, 3);
        bag.setConstraints(name, c);
        add(name);
        
        MultiLineLabel value = new MultiLineLabel(pair.value, 300);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 0, 0, 0);
        bag.setConstraints(value, c);
        add(value);
    }
    
    private static boolean skipField(String field) {
        if (field.equals(MetaEditorUtil.AUDIO_TITLE))
            return true;
        else if (field.equals(MetaEditorUtil.AUDIO_ARTIST))
            return true;
        else if (field.equals(MetaEditorUtil.AUDIO_ALBUM))
            return true;
        else if (field.equals(MetaEditorUtil.AUDIO_SECONDS))
            return true;
        else if (field.equals(MetaEditorUtil.AUDIO_COMMENTS))
            return true;
        else if (!MetaEditorUtil.contains(field))
            return true;
        else
            return false;
    }
    
    private static final class NameValuePair {
        
        private final String name;
        private final String value;
        
        private NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
