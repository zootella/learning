package com.limegroup.gnutella.gui.xml.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.gui.LimeTextField;
import com.limegroup.gnutella.gui.xml.ComboBoxValue;
import com.limegroup.gnutella.licenses.CCConstants;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;

public class ImageEditor extends MetaEditorPanel {

	private JLabel titleLabel;
    private LimeTextField titleTextField;
    private JLabel descriptionLabel;
    private LimeTextField descriptionTextField;
    private JLabel artistLabel;
    private LimeTextField artistTextField;
    private JLabel licenseTypeLabel;
	private JComboBox licenseTypeComboBox;
    private JLabel licenseLabel;
	private LimeTextField licenseTextField;
    
	private String title = null;
    private boolean titleEdited = false;
    
    
	public ImageEditor(FileDesc fd, LimeXMLSchema schema,
			LimeXMLDocument document) {
		super(fd, schema, document);
		super.setName(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE));
		
		init();
        initLocalFields();
	}
	
	public boolean hasChanged() {
        return (titleEdited || super.hasChanged());
    }
	
	public void prepareSave() {
        if (title != null) {
            String text = titleTextField.getText().trim();
            if (text.equals("")) {
                titleTextField.setText(title);
            }
        }
    }
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints mainConstr = new GridBagConstraints();
		mainConstr.insets = new Insets(2,10,0,2);
		mainConstr.anchor = GridBagConstraints.WEST;
		mainConstr.insets = new Insets(5,10,0,2);
		titleLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE_TITLE));
		add(titleLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=1;
		titleTextField = new LimeTextField(24);
		add(titleTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=2;
		descriptionLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE_DESCRIPTION));
		add(descriptionLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		descriptionTextField = new LimeTextField(24);
		add(descriptionTextField,mainConstr);
		mainConstr.gridy=4;
		licenseLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE_LICENSE));
		add(licenseLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=5;
		licenseTextField = new LimeTextField(24);
		licenseTextField.setEnabled(false);
		add(licenseTextField,mainConstr);
		//right side
		mainConstr.gridx=1;
		mainConstr.gridy=0;
		mainConstr.insets = new Insets(5,10,0,2);
		artistLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE_ARTIST));
		add(artistLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=1;
		artistTextField = new LimeTextField(24);
		add(artistTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=2;
		licenseTypeLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.IMAGE_LICENSETYPE));
		add(licenseTypeLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		licenseTypeComboBox = new JComboBox();
		add(licenseTypeComboBox,mainConstr);
	}
	
	private void initLocalFields() {
		addComponent(MetaEditorUtil.IMAGE_TITLE, titleTextField);
        addComponent(MetaEditorUtil.IMAGE_ARTIST, artistTextField);
        addComponent(MetaEditorUtil.IMAGE_DESCRIPTION, descriptionTextField);
        addComponent(MetaEditorUtil.IMAGE_LICENSETYPE, licenseTypeComboBox);
        addComponent(MetaEditorUtil.IMAGE_LICENSE, licenseTextField);
		
        initFields();
        //show license only if the file has a Creative Commons license
        ComboBoxValue val = (ComboBoxValue)licenseTypeComboBox.getSelectedItem();
        if(val==null || !(val.equals(new ComboBoxValue(CCConstants.CC_URI_PREFIX)))){
        	licenseTextField.setVisible(false);
        	licenseLabel.setVisible(false);
        }        
        titleTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                titleEdited = true;
            }
        });
	}

}
