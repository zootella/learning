package com.limegroup.gnutella.gui.xml;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.xml.sax.SAXException;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.gui.LimeTextField;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLReplyCollection;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLSchemaRepository;
import com.limegroup.gnutella.xml.LimeXMLUtils;
import com.limegroup.gnutella.xml.SchemaNotFoundException;
import com.limegroup.gnutella.xml.SchemaReplyCollectionMapper;

/**
 * This Frame is popped up when the user indicated that she is interested in
 * Editing/viewing meta-data about a particular file. This frame has a one 
 * to one realtionship with files in the library.
 *<p> 
 * Constructed on the fly when the user selects a file asks to annotate it
 * The changes made to the meta-data about that file, are committed to disk
 * as soon as the changes are confirmed.
 *
 * @author Sumeet Thadani
 */


public class MetaEditorFrame extends JDialog implements ActionListener{
    
    private static final String FILE_LABEL=GUIMediator.getStringResource
                                               ("META_EDITOR_ANNOTATING_FILE");
    private static final int DIALOG_WIDTH = 500;
    private static final int DIALOG_HEIGHT = 500;
    private static final int INIT_X = 100;
    private static final int INIT_Y = 100;
    private JPanel filePanel;
    private JLabel fileLabel;
    private JTextField fileField;
    
    private JPanel buttonPanel;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JSplitPane splitPane;
    private JPanel upperLeftPanel;
    private JPanel lowerLeftPanel;
    private JList docSchemaList;
    private JLabel editLabel;
    private JLabel addLabel;
    private JList unDocSchemaList;
    private InputPanel innerEditPanel;
    private OuterEditingPanel outerEditPanel;
    private JScrollPane upperLeftScroller;
    private JScrollPane lowerLeftScroller;
    private List noDocSchemas;//a list of schemaURIs w/o any docs for this file
    private List docsOfFile;//stores a list of LimeXMLDocuments
    private String fileName;
    private String selectedSchemaURI;
    private LimeXMLDocument editedDoc;
    private JSplitPane splitter;
    private JPanel grandPanel;

    private URN editFileHashValue;
    private FileDesc fd;

    //constructor
    public MetaEditorFrame(FileDesc fd, String fullName, Frame owner){
        super(owner,GUIMediator.getStringResource
              ("META_EDITOR_ANNOTATING_FILE")+ "\""+fullName+"\"",true);
        fileName = fullName;
        this.fd = fd;
        editFileHashValue = fd.getSHA1Urn();
        noDocSchemas = new ArrayList();
        docsOfFile = getDocs();
        
        //North panel stuff 
        filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileLabel= new JLabel(FILE_LABEL);
        fileField = new LimeTextField();
        fileField.setText(fullName);
        fileField.setEditable(false);
        filePanel.add(fileLabel);
        filePanel.add(fileField);
        
        //South Panel stuff
        buttonPanel = new JPanel(); //no layout
        saveButton = new JButton(GUIMediator.getStringResource
                                                   ("META_EDITOR_SAVE_LABEL"));
        cancelButton = new JButton(GUIMediator.getStringResource
                                                ("META_EDITOR_CANCEL_LABEL"));
        deleteButton = new JButton(GUIMediator.getStringResource
                                                 ("META_EDITOR_DELETE_LABEL"));
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        deleteButton.addActionListener(this);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        //West stuff
        upperLeftPanel = new JPanel();
        upperLeftPanel.setLayout(new BorderLayout());
        lowerLeftPanel = new JPanel();
        lowerLeftPanel.setLayout(new BorderLayout());
        
        editLabel = new JLabel(GUIMediator.getStringResource(
                                                 "META_EDITOR_EDITING_LABEL"));
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //p1.setBackground(Color.white);
        p1.add(editLabel);
        upperLeftPanel.add(p1,BorderLayout.NORTH);
        int used = docsOfFile.size();
        String[] usedSchemas = new String[used];
        for(int i=0; i<used; i++){
            LimeXMLDocument d = (LimeXMLDocument)docsOfFile.get(i);
            usedSchemas[i] = XMLUtils.getTitleForSchemaURI(d.getSchemaURI());
        }
        docSchemaList = new JList(usedSchemas);
        docSchemaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        docSchemaList.addListSelectionListener(new DocListListener());
        upperLeftScroller = new JScrollPane(docSchemaList);
        upperLeftPanel.add(upperLeftScroller,BorderLayout.CENTER);
        
        addLabel = new JLabel(GUIMediator.getStringResource
                                                 ("META_EDITOR_ADDING_LABEL"));
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //p2.setBackground(Color.white);
        p2.add(addLabel);
        lowerLeftPanel.add(p2,BorderLayout.NORTH);
        int unused = noDocSchemas.size();
        String[] unusedSchemas = new String[unused];
        for(int j=0;j<unused;j++){
            String uri = (String)noDocSchemas.get(j);
            unusedSchemas[j] =  XMLUtils.getTitleForSchemaURI(uri);
        }
        unDocSchemaList = new JList(unusedSchemas);
        unDocSchemaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unDocSchemaList.addListSelectionListener(new UnDocListListener());
        lowerLeftScroller = new JScrollPane(unDocSchemaList);
        lowerLeftPanel.add(lowerLeftScroller,BorderLayout.CENTER);
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,upperLeftPanel,
                                   lowerLeftPanel);
        //splitPane.setDividerLocation(0.5);
        splitPane.setDividerLocation(splitPane.getMinimumDividerLocation()+99);
        //splitPane.setBackground(Color.white);
        
        //East stuff
        int index = docSchemaList.getSelectedIndex();
        if (index>-1){
            LimeXMLDocument doc =(LimeXMLDocument)docsOfFile.get(index);
            String schemaURI = doc.getSchemaURI();
            LimeXMLSchemaRepository rep=LimeXMLSchemaRepository.instance();
            LimeXMLSchema schema = rep.getSchema(schemaURI);
            innerEditPanel = new EditingPanel(schema,doc);//it is editable
        }
        else{
            index = unDocSchemaList.getSelectedIndex();
            if(index > -1){
                String schemaURI = (String)noDocSchemas.get(index);
                LimeXMLSchemaRepository rep=LimeXMLSchemaRepository.instance();
                LimeXMLSchema schema = rep.getSchema(schemaURI);
                innerEditPanel=new InputPanel(schema,null,null,null,true,true, false);
            }
        }
        
        outerEditPanel= new OuterEditingPanel(innerEditPanel);      
        outerEditPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  
        
        setSize(DIALOG_WIDTH,DIALOG_HEIGHT);
        setLocation(INIT_X,INIT_Y);

        splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,splitPane,
                                  outerEditPanel);
        splitter.setDividerLocation(splitter.getMinimumDividerLocation());

        grandPanel  = new JPanel();
        grandPanel.setLayout(new BorderLayout());
        grandPanel.add(filePanel,BorderLayout.NORTH);
        grandPanel.add(splitter,BorderLayout.CENTER);
        grandPanel.add(buttonPanel,BorderLayout.SOUTH);

        JComponent contentPane = (JComponent)getContentPane();
        GUIUtils.addHideAction(contentPane);
        contentPane.add(grandPanel,BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getSource().equals(saveButton)){
            boolean close = saveMeta();
            if(close)
                MetaEditorFrame.this.dispose();
        }
        else if(event.getSource().equals(deleteButton)){
            removeMeta();
            MetaEditorFrame.this.dispose();
        }
        else if(event.getSource().equals(cancelButton)){
            MetaEditorFrame.this.dispose();
        }
    }

    private void removeMeta(){
        InputPanel removePanel= outerEditPanel.getContentPanel();
        if(!(removePanel instanceof EditingPanel) ){
            GUIMediator.showError("ERROR_DEL_META_USER");
            return;
        }
        String uri = editedDoc.getSchemaURI();
        SchemaReplyCollectionMapper map=SchemaReplyCollectionMapper.instance();
        LimeXMLReplyCollection collection = map.getReplyCollection(uri);

        Assert.that(collection!=null,
                    "Trying to remove data from a non-existent collection");
        
        boolean removed = collection.removeDoc(fd);
        if(removed)
            editedDoc = null;
        else {//unable to remove or write to disk
            GUIMediator.showError("ERROR_DEL_META_SYSTEM");
        }
    }
    
    /**
     * @return the returned boolean decided whether or not to close the dialog
     */
    private boolean saveMeta() {        
        //OK, the save button has been pressed.
        InputPanel inputPanel = outerEditPanel.getContentPanel();
        if (inputPanel == null)
            return true;//close the dialog
        String XMLString = inputPanel.getInput();//gets XML string.
        LimeXMLDocument newDoc = null;
        LimeXMLDocument oldDoc = null;
        try{
            newDoc = new LimeXMLDocument(XMLString);
        } catch(SAXException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return true;//close save window
        } catch(SchemaNotFoundException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return true;
        } catch(IOException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return true;
        }
        //OK we have the new LimeXMLDocument
        SchemaReplyCollectionMapper map=SchemaReplyCollectionMapper.instance();
        String uri = newDoc.getSchemaURI();
        LimeXMLReplyCollection collection = map.getReplyCollection(uri);
        //This is a really bad case!
        Assert.that(collection!=null,
                    "Can't add document. No Collection exists for uri:"+uri+
                    "\nDocument created with string: "+XMLString);
        if(inputPanel instanceof EditingPanel) {//we are editing data
            oldDoc = collection.replaceDoc(fd, newDoc);
        }
        else if(inputPanel instanceof InputPanel) {//we are adding new data
            collection.addReply(fd,newDoc);
        }
        else {//The save button as been pressed in error - do nothing.
            GUIMediator.showError("ERROR_SAVE_META_ILLEGAL");
        }
        
        int committed =-1;
        boolean committed2 = true;
        if (LimeXMLUtils.isSupportedFormat(fileName))
            committed = collection.mediaFileToDisk(fd, fileName, newDoc, false);
        else
            committed2 = collection.writeMapToDisk();
        if(!committed2){
            GUIMediator.showError("ERROR_SAVE_META_DISK");
            return true;
        }

        switch(committed) {
        case LimeXMLReplyCollection.FILE_DEFECTIVE:
            GUIMediator.showError("ERROR_SAVE_META_FILE");
            return true;
        case LimeXMLReplyCollection.RW_ERROR:
            GUIMediator.showError("ERROR_SAVE_META_RW");
            return true;
        case LimeXMLReplyCollection.BAD_ID3:
            GUIMediator.showError("ERROR_SAVE_META_ID3");
            return true;
        case LimeXMLReplyCollection.FAILED_TITLE:
            GUIMediator.showError("ERROR_SAVE_META_ID3");
            return false;
        case LimeXMLReplyCollection.FAILED_ARTIST:
            cleanUpChanges("audios__audio__artist__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.FAILED_ALBUM:
            cleanUpChanges("audios__audio__album__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.FAILED_YEAR:
            cleanUpChanges("audios__audio__year__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.FAILED_COMMENT:
            cleanUpChanges("audios__audio__comment__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.FAILED_TRACK:
            cleanUpChanges("audios__audio__track__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.FAILED_GENRE:
            cleanUpChanges("audios__audio__genre__",collection,oldDoc);
            return false;
        case LimeXMLReplyCollection.HASH_FAILED:
            GUIMediator.showError("ERROR_SAVE_META_DISK");
            return true;
        default:
            return true;
        }
    }
    
    private void cleanUpChanges(String canonicalFieldName, 
                                LimeXMLReplyCollection collection, 
                                LimeXMLDocument oldDoc){
        GUIMediator.showError("ERROR_SAVE_META_BAD");
        JComponent comp=innerEditPanel.getField(canonicalFieldName);
        innerEditPanel.clearField(comp);
        if(oldDoc == null)//it was added....just remove
            collection.removeDoc(fd);
        else//older one was replaced....replace back
            collection.replaceDoc(fd, oldDoc);
    }

    private List getDocs() {
        //create a list of LimeXMLDocuments that are associated with this file
        //first get all the schemas
        LimeXMLSchemaRepository rep = LimeXMLSchemaRepository.instance();
        String[] schemas = rep.getAvailableSchemaURIs();
        int len = schemas.length;
        SchemaReplyCollectionMapper map = 
                                SchemaReplyCollectionMapper.instance();
        List xmlDocs = new ArrayList();
        for(int i=0; i<len;i++){//for each schema
            LimeXMLReplyCollection coll=map.getReplyCollection(schemas[i]);
            if(coll == null || coll.getCount()< 1){//null or no data
                noDocSchemas.add(schemas[i]);
                continue;
            }
            LimeXMLDocument currDoc = coll.getDocForHash(editFileHashValue);
            if (currDoc==null)//entry not found in this collection
                noDocSchemas.add(schemas[i]);
            else
                xmlDocs.add(currDoc);
        }        
        return xmlDocs;
    }

    private class DocListListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent event){
            int sIndex = unDocSchemaList.getSelectedIndex();
            unDocSchemaList.removeSelectionInterval(sIndex,sIndex);//deselect
            int index = docSchemaList.getSelectedIndex();
            if (index>-1){
                LimeXMLDocument doc =(LimeXMLDocument)docsOfFile.get(index);
                MetaEditorFrame.this.editedDoc = doc;
                MetaEditorFrame.this.selectedSchemaURI = doc.getSchemaURI();
                LimeXMLSchemaRepository rep=LimeXMLSchemaRepository.instance();
                LimeXMLSchema schema = rep.getSchema(selectedSchemaURI);
                innerEditPanel=new EditingPanel(schema,doc);
                outerEditPanel.setContent(innerEditPanel);
                outerEditPanel.revalidate();
            }
        }
    }

    private class UnDocListListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent event){
            int sIndex = docSchemaList.getSelectedIndex();
            docSchemaList.removeSelectionInterval(sIndex,sIndex);//deselect
            int index = unDocSchemaList.getSelectedIndex();
            if(index > -1){
                String schemaURI = (String)noDocSchemas.get(index);
                MetaEditorFrame.this.selectedSchemaURI = schemaURI;
                MetaEditorFrame.this.editedDoc = null;
                LimeXMLSchemaRepository rep=LimeXMLSchemaRepository.instance();
                LimeXMLSchema schema = rep.getSchema(schemaURI);
                innerEditPanel=new InputPanel(schema,null,null,null, true,true, false);
                outerEditPanel.setContent(innerEditPanel);
                outerEditPanel.revalidate();
            }
        }
    }        

}
