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

public class ApplicationEditor extends MetaEditorPanel{

	private JLabel nameLabel;
	private LimeTextField nameTextField;
	private JLabel publisherLabel;
	private LimeTextField publisherTextField;
	private JLabel platformLabel;
	private JComboBox platformComboBox;
	private JLabel licenseTypeLabel;
	private JComboBox licenseTypeComboBox;
	private JLabel licenseLabel;
	private LimeTextField licenseTextField;
	
	private String name = null;
    private boolean nameEdited = false;
	
	public ApplicationEditor(FileDesc fd, LimeXMLSchema schema, LimeXMLDocument doc) {
		super(fd,schema,doc);
		super.setName(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION));
		
		init();
        initLocalFields();
	}
	
	public boolean hasChanged() {
        return (nameEdited || super.hasChanged());
    }
	
	public void prepareSave() {
        if (name != null) {
            String text = nameTextField.getText().trim();
            if (text.equals("")) {
                nameTextField.setText(name);
            }
        }
    }
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints mainConstr = new GridBagConstraints();
		mainConstr.insets = new Insets(2,10,0,2);
		mainConstr.anchor = GridBagConstraints.WEST;
		mainConstr.insets = new Insets(5,10,0,2);
		nameLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION_NAME));
		add(nameLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=1;
		mainConstr.gridwidth=2;
		nameTextField = new LimeTextField(40);
		add(nameTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=2;
		mainConstr.gridwidth=1;
		publisherLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION_PUBLISHER));
		add(publisherLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		publisherTextField = new LimeTextField(24);
		add(publisherTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=4;
		licenseLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION_LICENSE));
		add(licenseLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=5;
		licenseTextField = new LimeTextField(24);
		licenseTextField.setEnabled(false);
		add(licenseTextField,mainConstr);
		mainConstr.gridy=6;
		mainConstr.insets = new Insets(5,10,0,2);
		licenseTypeLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION_LICENSETYPE));
		add(licenseTypeLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=7;
		licenseTypeComboBox = new JComboBox();
		add(licenseTypeComboBox,mainConstr);
		//right side
		mainConstr.gridx=1;
		mainConstr.gridy=2;
		mainConstr.insets = new Insets(5,10,0,2);
		platformLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.APPLICATION_PLATFORM));
		add(platformLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		platformComboBox = new JComboBox();
		add(platformComboBox,mainConstr);
	}
	
	private void initLocalFields() {
		addComponent(MetaEditorUtil.APPLICATION_NAME, nameTextField);
        addComponent(MetaEditorUtil.APPLICATION_PUBLISHER, publisherTextField);
        addComponent(MetaEditorUtil.APPLICATION_PLATFORM, platformComboBox);
        addComponent(MetaEditorUtil.APPLICATION_LICENSE, licenseTextField);
        addComponent(MetaEditorUtil.APPLICATION_LICENSETYPE, licenseTypeComboBox);
		
        initFields();
        //show license only if the file has a Creative Commons license
        ComboBoxValue val = (ComboBoxValue)licenseTypeComboBox.getSelectedItem();
        if(val==null || !(val.equals(new ComboBoxValue(CCConstants.CC_URI_PREFIX)))){
        	licenseTextField.setVisible(false);
        	licenseLabel.setVisible(false);
        }
        
        nameTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                nameEdited = true;
            }
        });
	}
}
