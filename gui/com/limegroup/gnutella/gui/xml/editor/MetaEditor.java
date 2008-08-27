package com.limegroup.gnutella.gui.xml.editor;


import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.xml.sax.SAXException;

import com.limegroup.gnutella.Assert;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.library.LibraryMediator;
import com.limegroup.gnutella.gui.xml.MetaEditorFrame;
import com.limegroup.gnutella.gui.xml.editor.audio.AudioTabbedPane;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.FileUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLReplyCollection;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.LimeXMLUtils;
import com.limegroup.gnutella.xml.SchemaNotFoundException;
import com.limegroup.gnutella.xml.SchemaReplyCollectionMapper;


public class MetaEditor extends javax.swing.JDialog {

    private FileDesc fd;
    private String fileName;
    private Frame parent;
	/**
	 * Used when advanced mode is shown.
	 */
	private Component relativeToComponent = null;
	
    public MetaEditor(FileDesc fd, String fileName, Frame parent,boolean publish) {
        super(parent, true);
        
        this.fd = fd;
        this.fileName = fileName;
        this.parent = parent;
        
        initComponents(publish);
        
        // The Aqua L&F draws an ugly focus indicator which disappears
        // after a while. This code hides it right from the start!
        if (CommonUtils.isMacOSX() && CommonUtils.isJava14OrLater()) {
            tabbedPane.setFocusable(false);
        }
        
        advancedButton.setText(GUIMediator.getStringResource("META_EDITOR_ADVANCED_BUTTON_LABEL"));
        okButton.setText(GUIMediator.getStringResource("META_EDITOR_OK_BUTTON_LABEL"));
        cancelButton.setText(GUIMediator.getStringResource("META_EDITOR_CANCEL_BUTTON_LABEL"));
        
        setTitle(fd.getFile().getName());
        
        getRootPane().setDefaultButton(okButton);
        okButton.requestFocus();
        
        okButton.updateUI();
        cancelButton.updateUI();
        advancedButton.updateUI();
        
        GUIUtils.addHideAction((JComponent)getContentPane());
        pack();
    }
    
    private void removeMeta(LimeXMLSchema schema) {
        
        String uri = schema.getSchemaURI();
        SchemaReplyCollectionMapper map = SchemaReplyCollectionMapper.instance();
        LimeXMLReplyCollection collection = map.getReplyCollection(uri);

        Assert.that(collection!=null,
                    "Trying to remove data from a non-existent collection");
        
        if( ! collection.removeDoc(fd) ) {//unable to remove or write to disk
            GUIMediator.showError("ERROR_DEL_META_SYSTEM");
        }
    }
    
    private void saveMeta(MetaEditorTabbedPane pane) {
        
        String XMLString = pane.getInput();
        
        if (XMLString == null) {
            return;
        } else if (XMLString.trim().length() == 0) {
            removeMeta(pane.getSchema());
            return;
        }
        
        LimeXMLDocument oldDoc = pane.getDocument();
        LimeXMLDocument newDoc = null;
        
        try{
            newDoc = new LimeXMLDocument(XMLString);
        } catch(SAXException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return;
        } catch(SchemaNotFoundException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return;
        } catch(IOException e) {
            GUIMediator.showError("ERROR_SAVE_META_DOC");
            return;
        }

        //OK we have the new LimeXMLDocument
        SchemaReplyCollectionMapper map = SchemaReplyCollectionMapper.instance();
        String uri = newDoc.getSchemaURI();
        LimeXMLReplyCollection collection = map.getReplyCollection(uri);

        //This is a really bad case!
        Assert.that(collection!=null,"Cant add doc to nonexistent collection");
        
        if (oldDoc == null) {
            collection.addReply(fd, newDoc);
        } else {
            oldDoc = collection.replaceDoc(fd, newDoc);
        }

        if (LimeXMLUtils.isSupportedFormat(fileName)) {
            
            int committed = collection.mediaFileToDisk(fd, fileName, newDoc, false);
            
            switch(committed) {
                case LimeXMLReplyCollection.FILE_DEFECTIVE:
                    GUIMediator.showError("ERROR_SAVE_META_FILE");
                    break;
                case LimeXMLReplyCollection.RW_ERROR:
                    GUIMediator.showError("ERROR_SAVE_META_RW");
                    break;
                case LimeXMLReplyCollection.BAD_ID3:
                    GUIMediator.showError("ERROR_SAVE_META_ID3");
                    break;
                case LimeXMLReplyCollection.FAILED_TITLE:
                    GUIMediator.showError("ERROR_SAVE_META_ID3");
                    break;
                case LimeXMLReplyCollection.FAILED_ARTIST:
                    cleanUpChanges(MetaEditorUtil.AUDIO_ARTIST,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.FAILED_ALBUM:
                    cleanUpChanges(MetaEditorUtil.AUDIO_ALBUM,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.FAILED_YEAR:
                    cleanUpChanges(MetaEditorUtil.AUDIO_YEAR,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.FAILED_COMMENT:
                    cleanUpChanges(MetaEditorUtil.AUDIO_COMMENTS,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.FAILED_TRACK:
                    cleanUpChanges(MetaEditorUtil.AUDIO_TRACK,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.FAILED_GENRE:
                    cleanUpChanges(MetaEditorUtil.AUDIO_GENRE,collection,oldDoc);
                    break;
                case LimeXMLReplyCollection.HASH_FAILED:
                    GUIMediator.showError("ERROR_SAVE_META_DISK");
                    break;
            }
            
        } else {
            
            if( ! collection.writeMapToDisk() ){
                GUIMediator.showError("ERROR_SAVE_META_DISK");
            }
        }
    }
    
    private void cleanUpChanges(String canonicalFieldName, 
                                LimeXMLReplyCollection collection, 
                                LimeXMLDocument oldDoc){
        GUIMediator.showError("ERROR_SAVE_META_BAD");
        
        if(oldDoc == null)//it was added....just remove
            collection.removeDoc(fd);
        else//older one was replaced....replace back
            collection.replaceDoc(fd, oldDoc);
    }
    
    private JTabbedPane createTabbedPane(boolean publish) {
        JTabbedPane tabbedPane = null;
        if(publish) {
        	if(LimeXMLUtils.isFilePublishable(fd.getFile()))
        		tabbedPane = new CCPublisherTabbedPane(fd);
        	return tabbedPane;
        }
        else {
	        if (LimeXMLUtils.isSupportedAudioFormat(fileName)) {
	            tabbedPane = new AudioTabbedPane(fd);
	        }
	        else if (MediaType.getVideoMediaType().matches((fileName))) {
	            tabbedPane = new VideoTabbedPane(fd);
	        }
	        else if (MediaType.getProgramMediaType().matches(fileName)) {
	        	tabbedPane = new ApplicationTabbedPane(fd);
	        }
	        else if (MediaType.getImageMediaType().matches(fileName)) {
	        	tabbedPane = new ImageTabbedPane(fd);
	        }
	        else if (MediaType.getDocumentMediaType().matches(fileName)) {
	        	tabbedPane = new DocumentTabbedPane(fd);
	        }
	        return tabbedPane;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents(boolean publish) {//GEN-BEGIN:initComponents
        javax.swing.JPanel advancedPanel;
        javax.swing.JPanel buttonPanel1;
        javax.swing.JPanel cancelOKPanel;
        java.awt.GridBagConstraints gridBagConstraints;
        javax.swing.JPanel spacer1;
        javax.swing.JPanel spacer2;

        tabbedPane = createTabbedPane(publish);
        buttonPanel1 = new javax.swing.JPanel();
        cancelOKPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        spacer1 = new javax.swing.JPanel();
        advancedPanel = new javax.swing.JPanel();
        advancedButton = new javax.swing.JButton();
        spacer2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocationRelativeTo(this);
        setName("MetaEditorDialog");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                doWindowClose(evt);
            }
        });

        tabbedPane.setMinimumSize(null);
        tabbedPane.setNextFocusableComponent(okButton);
        tabbedPane.setPreferredSize(null);
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        buttonPanel1.setLayout(new java.awt.BorderLayout());

        buttonPanel1.setMinimumSize(new java.awt.Dimension(264, 40));
        buttonPanel1.setPreferredSize(new java.awt.Dimension(0, 40));
        cancelOKPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setText("Cancel");
        cancelButton.setFocusPainted(false);
        cancelButton.setNextFocusableComponent(advancedButton);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancel(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        cancelOKPanel.add(cancelButton, gridBagConstraints);

        okButton.setText("OK");
        okButton.setFocusPainted(false);
        okButton.setNextFocusableComponent(cancelButton);
        okButton.setSelected(true);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	doOK(evt);
            }
        });

        cancelOKPanel.add(okButton, new java.awt.GridBagConstraints());

        cancelOKPanel.add(spacer1, new java.awt.GridBagConstraints());

        buttonPanel1.add(cancelOKPanel, java.awt.BorderLayout.EAST);

        advancedPanel.setLayout(new java.awt.GridBagLayout());

        advancedButton.setText("Advanced");
        advancedButton.setFocusPainted(false);
        advancedButton.setNextFocusableComponent(okButton);
        advancedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doAdvanced(evt);
            }
        });
        advancedButton.setVisible(!publish);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        advancedPanel.add(advancedButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        advancedPanel.add(spacer2, gridBagConstraints);

        buttonPanel1.add(advancedPanel, java.awt.BorderLayout.WEST);

        getContentPane().add(buttonPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

	/**
	 * Overriden to remember the component the dialog should be relative to.
	 * <p>
	 * The same component will be used for the Advanced Dialog, if it is opened.
 	 */
	public void setLocationRelativeTo(Component c) {
		relativeToComponent = c;
		super.setLocationRelativeTo(c);
	}
	
    private void doAdvanced(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doAdvanced
        MetaEditorFrame frame = new MetaEditorFrame(fd, fileName, parent);
        
        setVisible(false);
        dispose();
        
		if (relativeToComponent != null) {
			frame.setLocationRelativeTo(relativeToComponent);
		}
        frame.setVisible(true); /* blocking! */
        frame.dispose();
    }//GEN-LAST:event_doAdvanced

    private void doWindowClose(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_doWindowClose
        setVisible(false);
        dispose();
    }//GEN-LAST:event_doWindowClose

    private void doOK(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doOK
    	MetaEditorTabbedPane metaEditor = (MetaEditorTabbedPane)tabbedPane;
    	if(!metaEditor.checkInput())return;
    	for (Iterator iter = metaEditor.getFileEventListeners().iterator(); iter.hasNext();) {
			FileEventListener listener = (FileEventListener) iter.next();
			RouterService.getFileManager().registerFileManagerEventListener(listener);
		}
    	GUIMediator.instance().schedule( new Runnable(){
            public void run() {
                try {
                    GUIMediator.safeInvokeAndWait(new Runnable() {
                        public void run(){
                            LibraryMediator.instance().setAnnotateEnabled(false);
                        }
                    });                    
                    saveMeta((MetaEditorTabbedPane)tabbedPane);
                } finally{
                    GUIMediator.safeInvokeAndWait(new Runnable() {
                        public void run(){
                            LibraryMediator.instance().setAnnotateEnabled(true);
                        }
                    });
                    for (Iterator iter = ((MetaEditorTabbedPane)tabbedPane).getFileEventListeners().iterator(); iter.hasNext();) {
            			FileEventListener listener = (FileEventListener) iter.next();
            			RouterService.getFileManager().unregisterFileManagerEventListener(listener);
            		}
                }
            }
        });
        setVisible(false);
        dispose();
    }//GEN-LAST:event_doOK

    private void doCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancel
        setVisible(false);
        dispose();
    }//GEN-LAST:event_doCancel
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton advancedButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
}
