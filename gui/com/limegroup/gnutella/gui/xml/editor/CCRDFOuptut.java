package com.limegroup.gnutella.gui.xml.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.licenses.PublishedCCLicense;
import com.limegroup.gnutella.util.CommonUtils;

public class CCRDFOuptut implements FileEventListener{
	
	private final String CCPUBLISHER_TITLE = GUIMediator.getStringResource("CC_RDFOUTPUT_TITLE");
	
	private static final int DIALOG_WIDTH = 480;
	
	private static final int DIALOG_HEIGHT = 280;
	
	private final JLabel RDF_OUTPUT_LABEL = new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_RDF_OUTPUT"));
	
	private final JTextArea RDF_OUTPUT = new JTextArea();
	
	private final JScrollPane OUPUT_PANE = new JScrollPane(RDF_OUTPUT);
	
	private JDialog _dialog;
	
	private final FileDesc _fd;
	
	private final String _holder,_title,_year,_description,_url;
	
	private final int _type;
	
	private boolean _isEventHandled;
	
	public CCRDFOuptut(FileDesc fd,String holder, String title, 
            String year, String description, String url,int type) {
		_fd = fd;
		_holder = holder;
		_title = title;
		_year = year;
		_description = description;
		_url = url;
		_type = type;
	}
	
	public void handleFileEvent(FileManagerEvent evt) {
	    if(!evt.isChangeEvent() || 
                evt.getFileDescs() == null || 
                evt.getFileDescs().length == 0)
             return;
        
        if(_fd.equals(evt.getFileDescs()[0])) {
            
            synchronized(this) {
                if (_isEventHandled)
                    return;
                _isEventHandled = true;
            }
            
            init();
            FileDesc newFD = evt.getFileDescs()[1];
            String RDFString = 
                PublishedCCLicense.getRDFRepresentation(_holder,_title,_year,_description,
                    newFD.getSHA1Urn().httpStringValue(),_type);
            
            RDF_OUTPUT.setText(RDFString);
            
            _dialog.setLocationRelativeTo(MessageService.getParentComponent());
            _dialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    finish();
                }
            });
            
            _dialog.setVisible(true);
        }
	}

	public void init() {
		_dialog = new JDialog(GUIMediator.getAppFrame(),true);
		GUIUtils.addHideAction((JComponent)_dialog.getContentPane());
		_dialog.setResizable(false);
		_dialog.setTitle(CCPUBLISHER_TITLE);
		//content here
		JPanel publishPanel = new JPanel(new GridBagLayout());
		publishPanel.setOpaque(false);
		GridBagConstraints con = new GridBagConstraints();
		publishPanel.add(RDF_OUTPUT_LABEL,con);
		con.insets = new Insets(2,2,2,2);
		con.gridy=1;
		JLabel uriLabel = new JLabel(_url);
		uriLabel.setForeground(Color.BLUE);
		publishPanel.add(uriLabel,con);
		RDF_OUTPUT.setEditable(false);
		RDF_OUTPUT.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153)));
		RDF_OUTPUT.setLineWrap(true);
		OUPUT_PANE.setMinimumSize(new Dimension(DIALOG_WIDTH-30,DIALOG_HEIGHT-180));
		OUPUT_PANE.setPreferredSize(new Dimension(DIALOG_WIDTH-30,DIALOG_HEIGHT-180));
		con.gridy=2;
		con.insets = new Insets(20,2,2,2);
		publishPanel.add(OUPUT_PANE,con);
		con.gridy=3;
		con.insets = new Insets(10,2,2,2);
		JButton copyButton = new JButton(GUIMediator.getStringResource("CC_PUBLISHER_COPY_BUTTON"));
		copyButton.setPreferredSize(new Dimension(120,20));
		copyButton.addActionListener(new CopyButtonListener());
		publishPanel.add(copyButton,con);
		JButton finishButton = new JButton(GUIMediator.getStringResource("CC_RDF_FINISH_LABEL"));
		finishButton.addActionListener(new FinishButtonListener());
		con.gridy=4;
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(DIALOG_WIDTH-10,2));
		publishPanel.add(separator,con);
		con.gridy=5;
		con.anchor=GridBagConstraints.EAST;
		con.insets= new Insets(10,2,2,2);
		publishPanel.add(finishButton,con);
		publishPanel.setPreferredSize(new Dimension(DIALOG_WIDTH-10, DIALOG_HEIGHT-10));
		_dialog.getContentPane().add(publishPanel);
		_dialog.setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
	}
	
	private void finish() {
		_dialog.setVisible(false);
		_dialog.dispose();
	}

	private class CopyButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(RDF_OUTPUT.getText()),null);
			} catch (HeadlessException doNothingException) {}
		}
	}
	
	private class FinishButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			finish();
		}
	}
}
