package com.limegroup.gnutella.gui.xml.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.gui.LimeTextField;
import com.limegroup.gnutella.gui.xml.ComboBoxValue;
import com.limegroup.gnutella.licenses.CCConstants;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.SchemaFieldInfo;

public class VideoEditor extends MetaEditorPanel {
	
    private JLabel titleLabel;
    private LimeTextField titleTextField;
    private JComboBox typeComboBox;
    private JLabel typeLabel;
    private JLabel yearLabel;
    private LimeTextField yearTextField;
    private JLabel ratingLabel;
    private JComboBox ratingComboBox;
    private JLabel directorLabel;
    private LimeTextField directorTextField;
    private JLabel studioLabel;
    private LimeTextField studioTextField;
    private JLabel commentsLabel;
    private JScrollPane commentsScrollPane;
    private JTextArea commentsTextArea;
    private JLabel languageLabel;
    private LimeTextField languageTextField;
    private JLabel starsLabel;
    private LimeTextField starsTextField;
    private JLabel producerLabel;
    private LimeTextField producerTextField;
    private JLabel subtitlesLabel;
    private LimeTextField subtitlesTextField;
    private JLabel licenseLabel;
    private LimeTextField licenseTextField;
    
    private String title = null;
    private boolean titleEdited = false;
	
	public VideoEditor(FileDesc fd, LimeXMLSchema schema, LimeXMLDocument doc) {
		super(fd,schema,doc);
		super.setName(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO));
		
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
	
	private void initLocalFields() {           
        addComponent(MetaEditorUtil.VIDEO_TITLE, titleTextField);
        addComponent(MetaEditorUtil.VIDEO_COMMENTS, commentsTextArea);
        addComponent(MetaEditorUtil.VIDEO_YEAR, yearTextField);
        addComponent(MetaEditorUtil.VIDEO_TYPE, typeComboBox);
        addComponent(MetaEditorUtil.VIDEO_DIRECTOR, directorTextField);
        addComponent(MetaEditorUtil.VIDEO_STUDIO, studioTextField);
        addComponent(MetaEditorUtil.VIDEO_RATING, ratingComboBox);
        addComponent(MetaEditorUtil.VIDEO_LICENSE, licenseTextField);
        addComponent(MetaEditorUtil.VIDEO_STARS, starsTextField);
        addComponent(MetaEditorUtil.VIDEO_PRODUCER, producerTextField);
        addComponent(MetaEditorUtil.VIDEO_LANGUAGE, languageTextField);
        addComponent(MetaEditorUtil.VIDEO_SUBTITLES, subtitlesTextField);
        
        initFields();
        //show license only if the file has a Creative Commons license
        if(licenseTextField.getText().equals("")) {
	        licenseTextField.setVisible(false);
	    	licenseLabel.setVisible(false);
        }
        titleTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                titleEdited = true;
            }
        });
        
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints mainConstr = new GridBagConstraints();
		mainConstr.insets = new Insets(2,10,0,2);
		mainConstr.anchor = GridBagConstraints.WEST;
		mainConstr.insets = new Insets(5,10,0,2);
		titleLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_TITLE));
		add(titleLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=1;
		mainConstr.gridwidth=2;
		titleTextField = new LimeTextField(40);
		add(titleTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=2;
		mainConstr.gridwidth=1;
		directorLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_DIRECTOR));
		add(directorLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		directorTextField = new LimeTextField(24);
		add(directorTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=4;
		starsLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_STARS));
		add(starsLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=5;
		starsTextField = new LimeTextField(24);
		add(starsTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=6;
		producerLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_PRODUCER));
		add(producerLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=7;
		producerTextField=new LimeTextField(24);
		add(producerTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=8;
		studioLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_STUDIO));
		add(studioLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=9;
		studioTextField = new LimeTextField(24);
		add(studioTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=10;
		commentsLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_COMMENTS));
		add(commentsLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=11;
		mainConstr.gridwidth=2;
        mainConstr.fill = java.awt.GridBagConstraints.BOTH;
        commentsTextArea = new JTextArea();
        commentsTextArea.setLineWrap(true);
        commentsTextArea.setWrapStyleWord(true);
		commentsScrollPane = new JScrollPane(commentsTextArea);
        commentsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commentsScrollPane.setPreferredSize(new java.awt.Dimension(22, 50));
        add(commentsScrollPane,mainConstr);
        mainConstr.insets = new Insets(5,10,0,2);
        mainConstr.gridy=12;
        mainConstr.fill = java.awt.GridBagConstraints.NONE;
		subtitlesLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_SUBTITLES));
		add(subtitlesLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=13;
		subtitlesTextField = new LimeTextField(24);
		add(subtitlesTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=14;
		licenseLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_LICENSE));
		add(licenseLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=15;
		licenseTextField = new LimeTextField(24);
		licenseTextField.setEnabled(false);
		add(licenseTextField,mainConstr);
        //right side
		mainConstr.fill = java.awt.GridBagConstraints.BOTH;
        mainConstr.gridwidth=1;
		mainConstr.gridx=1;
		mainConstr.gridy=2;
		mainConstr.insets = new Insets(2,10,0,5);
		yearLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_YEAR));
		add(yearLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=3;
		yearTextField = new LimeTextField(6);
		add(yearTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=4;
		ratingLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_RATING));
		add(ratingLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=5;
		ratingComboBox = new JComboBox();
		add(ratingComboBox,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=6;
		languageLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_LANGUAGE));
		add(languageLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=7;
		languageTextField = new LimeTextField(6);
		add(languageTextField,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=8;
		typeLabel = new JLabel(MetaEditorUtil.getStringResource(MetaEditorUtil.VIDEO_TYPE));
		add(typeLabel,mainConstr);
		mainConstr.insets = new Insets(0,10,2,2);
		mainConstr.gridy=9;
		typeComboBox = new JComboBox();
		add(typeComboBox,mainConstr);
		mainConstr.insets = new Insets(5,10,0,2);
		mainConstr.gridy=12;
	}

}
