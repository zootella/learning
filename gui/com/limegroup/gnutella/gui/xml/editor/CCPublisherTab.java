package com.limegroup.gnutella.gui.xml.editor;

import java.awt.Color;
import java.awt.Cursor;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileEventListener;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.SizedTextField;
import com.limegroup.gnutella.gui.URLLabel;
import com.limegroup.gnutella.licenses.CCConstants;
import com.limegroup.gnutella.licenses.License;
import com.limegroup.gnutella.licenses.PublishedCCLicense;
import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;

/**
 * This class provides the ability to publish an audio file with a the 
 * Creative Commons license. 
 */
public class CCPublisherTab extends AbstractMetaEditorPanel {
	
	private final JTextField COPYRIGHT_HOLDER = new SizedTextField(24);
	
	private final JLabel COPYRIGHT_HOLDER_LABEL = 
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_COPYRIGHT_HOLDER_LABEL"));
	
	private final JTextField WORK_TITLE = new SizedTextField(24);
	
	private final JLabel WORK_TITLE_LABEL= 
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_WORK_TITLE_LABEL"));
	
	private final JTextField COPYRIGHT_YEAR = new SizedTextField(6);
	
	private final JLabel COPYRIGHT_YEAR_LABEL= 
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_COPYRIGHT_YEAR_LABEL"));
	
	private final JTextField DESCRIPTION = new SizedTextField(24);
	
	private final JLabel DESCRIPTION_LABEL= 
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_DESCRIPTION_LABEL"));
	
	private final JLabel REMOVE_LICENSE_LABEL=
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_REMOVE_LICENSE_LABEL"));
	
	private final MouseListener REMOVE_LICENSE_LISTENER = new RemoveLabelMouseListener();
	
	/**
	 * The Verification URL field
	 */
	private final JTextField URL_FIELD = new SizedTextField(24);
	
	private final JLabel URL_LABEL = new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_LICENSE_URL"));
	
	private final String WARNING_MESSAGE_CREATE = GUIMediator.getStringResource("CC_PUBLISHER_WARNING_CREATE");
	
	private final String WARNING_MESSAGE_MODIFY = GUIMediator.getStringResource("CC_PUBLISHER_WARNING_MODIFY");
		
	private final JCheckBox WARNING_CHECKBOX = new JCheckBox("<html>"+WARNING_MESSAGE_CREATE+"</html>");
	
	private final JLabel INTRO_LABEL = new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_INTRO"));
	
	private final JCheckBox LICENSE_ALLOWCOM = new JCheckBox(GUIMediator.getStringResource("CC_PUBLISHER_LICENSE_ALLOWCOM_LABEL"));
	
	private final JLabel LICENSE_ALLOWMOD_LABEL = 
		new JLabel(GUIMediator.getStringResource("CC_PUBLISHER_LICENSE_ALLOWMOD_LABEL"));
	
	private final String ALLOWMOD_SHAREALIKE = GUIMediator.getStringResource("CC_PUBLISHER_LICENSE_ALLOWMOD_SHAREALIKE");
	
	private final String ALLOWMOD_YES = GUIMediator.getStringResource("YES");
	
	private final String ALLOWMOD_NO = GUIMediator.getStringResource("NO");
	
	private final JComboBox LICENSE_ALLOWMOD_BOX = new JComboBox(new String[] {
			ALLOWMOD_YES,
			ALLOWMOD_SHAREALIKE,
			ALLOWMOD_NO
	});
	
	private final JLabel CC_INTRO_URL_LABEL = 
		new URLLabel(SharingSettings.CREATIVE_COMMONS_INTRO_URL.getValue(),
				GUIMediator.getStringResource("CC_PUBLISHER_CC_INTRO_URL_LABEL"));
	
	private final JLabel CC_VERIFICATION_URL_LABEL =
		new URLLabel(SharingSettings.CREATIVE_COMMONS_VERIFICATION_URL.getValue(),
				GUIMediator.getStringResource("CC_PUBLISHER_CC_VERIFICATION_URL_LABEL"));
	
	private LimeXMLDocument _xmlDoc;
	
	private FileDesc _fd;
	
	private boolean _licenseRemoved = false;
	
	private JPanel _warningPanel = new JPanel(new GridBagLayout());
	
	/**
	 * Creates a new instance of CCPublisherTab.
	 * 
	 * @param fd The file descriptor
	 * @param doc The meta data of the file to publish
	 */
	public CCPublisherTab(FileDesc fd, LimeXMLDocument doc) {
		_xmlDoc = doc;
		_fd = fd;
		init();
		initInfo();
		updateDisplay();
	}
	
	private void init() {
		setName(GUIMediator.getStringResource("CC_PUBLISHER_TITLE"));
		setLayout(new GridBagLayout());
		GridBagConstraints mainConstraints = new GridBagConstraints();
		//Warning panel
		_warningPanel.setOpaque(false);
		GridBagConstraints warnConstraints = new GridBagConstraints();
		warnConstraints.anchor = GridBagConstraints.WEST;
		_warningPanel.add(INTRO_LABEL,warnConstraints);
		warnConstraints.gridx = 1;
		_warningPanel.add(CC_INTRO_URL_LABEL,warnConstraints);
		warnConstraints.gridx=0;
		warnConstraints.gridy = 1;
		warnConstraints.gridwidth=2;
		WARNING_CHECKBOX.addItemListener(new WarningCheckBoxListener());
		WARNING_CHECKBOX.setOpaque(false);
		WARNING_CHECKBOX.setPreferredSize(new Dimension(600,50));
		_warningPanel.add(WARNING_CHECKBOX,warnConstraints);
		warnConstraints.anchor = GridBagConstraints.CENTER;
		warnConstraints.gridy = 2;
		REMOVE_LICENSE_LABEL.setForeground(Color.BLUE);
		REMOVE_LICENSE_LABEL.addMouseListener(REMOVE_LICENSE_LISTENER);
		REMOVE_LICENSE_LABEL.setVisible(false);
		_warningPanel.add(REMOVE_LICENSE_LABEL,warnConstraints);
		mainConstraints.anchor = GridBagConstraints.WEST;
		add(_warningPanel,mainConstraints);
		mainConstraints.gridy=1;
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(600,2));
		add(separator,mainConstraints);
		//license details
		JPanel licenseDetailsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints licenseConstraints = new GridBagConstraints();
		licenseDetailsPanel.setOpaque(false);
		licenseConstraints.anchor = GridBagConstraints.WEST;
		licenseConstraints.insets = new Insets(2,2,2,2);
		licenseDetailsPanel.add(COPYRIGHT_HOLDER_LABEL,licenseConstraints);
		licenseConstraints.gridy=1;
		licenseDetailsPanel.add(COPYRIGHT_HOLDER,licenseConstraints);
		licenseConstraints.gridy=2;
		licenseDetailsPanel.add(WORK_TITLE_LABEL,licenseConstraints);
		licenseConstraints.gridy=3;
		licenseDetailsPanel.add(WORK_TITLE,licenseConstraints);
		licenseConstraints.gridx=1;
		licenseConstraints.gridy=0;
		licenseConstraints.insets = new Insets(2,40,2,2);
		licenseDetailsPanel.add(COPYRIGHT_YEAR_LABEL,licenseConstraints);
		licenseConstraints.gridy=1;
		licenseDetailsPanel.add(COPYRIGHT_YEAR,licenseConstraints);
		licenseConstraints.gridy=2;
		licenseDetailsPanel.add(DESCRIPTION_LABEL,licenseConstraints);
		licenseConstraints.gridy=3;
		licenseDetailsPanel.add(DESCRIPTION,licenseConstraints);
		licenseConstraints.insets = new Insets(20,2,2,2);
		licenseConstraints.gridx=0;
		licenseConstraints.gridy=4;
		LICENSE_ALLOWCOM.setOpaque(false);
		LICENSE_ALLOWCOM.setHorizontalTextPosition(SwingConstants.LEFT);
		licenseDetailsPanel.add(LICENSE_ALLOWCOM,licenseConstraints);
		licenseConstraints.gridy=5;
		//licensing options
		JPanel licenseAdvancedPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		licenseAdvancedPanel.setOpaque(false);
		constr.anchor = GridBagConstraints.EAST;
		licenseAdvancedPanel.setOpaque(false);
		licenseAdvancedPanel.add(LICENSE_ALLOWMOD_LABEL,constr);
		constr.gridx=1;
		constr.insets = new Insets(2,19,2,2);
		constr.anchor = GridBagConstraints.EAST;
		LICENSE_ALLOWMOD_BOX.setOpaque(false);
		licenseAdvancedPanel.add(LICENSE_ALLOWMOD_BOX,constr);
		licenseConstraints.gridx=0;
		licenseConstraints.gridy=5;
		licenseConstraints.insets = new Insets(2,2,2,2);
		licenseDetailsPanel.add(licenseAdvancedPanel,licenseConstraints);
		licenseConstraints.gridheight=1;
		licenseConstraints.insets = new Insets(20,40,2,2);
		licenseConstraints.gridx=1;
		licenseConstraints.gridy=4;
		JPanel panel = new JPanel(new GridBagLayout());
		constr = new GridBagConstraints();
		panel.add(URL_LABEL,constr);
		constr.gridx=1;
		constr.insets=new Insets(0,5,0,0);
		panel.add(CC_VERIFICATION_URL_LABEL,constr);
		panel.setOpaque(false);
		licenseDetailsPanel.add(panel,licenseConstraints);
		licenseConstraints.gridy=5;
		licenseConstraints.anchor = GridBagConstraints.EAST;
		licenseConstraints.insets = new Insets(2,40,2,2);
		URL_FIELD.setText("http://");
		licenseDetailsPanel.add(URL_FIELD,licenseConstraints);
		mainConstraints.gridy=2;
		mainConstraints.anchor=GridBagConstraints.CENTER;
		add(licenseDetailsPanel,mainConstraints);
		setOpaque(false);
	}
	
	/**
	 * Initializes the fieds with the file's Meta Data 
	 * only if a license does not exist.If a license exists, it populates 
	 * the verification URL field and the license distribution details.
	 */
	private void initInfo() {
		License license = _fd.getLicense();
		if(license != null) {
			WARNING_CHECKBOX.setText("<html>"+WARNING_MESSAGE_MODIFY+"</html>");
			REMOVE_LICENSE_LABEL.setVisible(true);
			_warningPanel.setPreferredSize(new Dimension(600,100));
			if(license.getLicenseURI()!=null)URL_FIELD.setText(license.getLicenseURI().toString());
			String licenseDeed = license.getLicenseDeed(_fd.getSHA1Urn()).toString();
			if(licenseDeed!=null) {
				if(licenseDeed.equals(CCConstants.ATTRIBUTION_NON_COMMERCIAL_NO_DERIVS_URI)) {
					LICENSE_ALLOWCOM.setSelected(false);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_NO);
				}
				else if(licenseDeed.equals(CCConstants.ATTRIBUTION_NO_DERIVS_URI)) {
					LICENSE_ALLOWCOM.setSelected(true);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_NO);
				}
				else if(licenseDeed.equals(CCConstants.ATTRIBUTION_NON_COMMERCIAL_URI)) {
					LICENSE_ALLOWCOM.setSelected(false);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_YES);
				}
				else if(licenseDeed.equals(CCConstants.ATTRIBUTION_SHARE_NON_COMMERCIAL_URI)) {
					LICENSE_ALLOWCOM.setSelected(false);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_SHAREALIKE);
				}
				else if(licenseDeed.equals(CCConstants.ATTRIBUTION_SHARE_URI)) {
					LICENSE_ALLOWCOM.setSelected(true);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_SHAREALIKE);
				}
				else {
					LICENSE_ALLOWCOM.setSelected(true);
					LICENSE_ALLOWMOD_BOX.setSelectedItem(ALLOWMOD_YES);
				}
			}
		}
		//license does not exist and file has XML doc
		else if(_xmlDoc != null) {
			COPYRIGHT_HOLDER.setText(_xmlDoc.getValue(MetaEditorUtil.AUDIO_ARTIST));
			COPYRIGHT_YEAR.setText(_xmlDoc.getValue(MetaEditorUtil.AUDIO_YEAR));
			WORK_TITLE.setText(_xmlDoc.getValue(MetaEditorUtil.AUDIO_TITLE));
		}	
	}
	
	private void updateDisplay() {
		WORK_TITLE.setEnabled(WARNING_CHECKBOX.isSelected());
		WORK_TITLE_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		COPYRIGHT_HOLDER.setEnabled(WARNING_CHECKBOX.isSelected());
		COPYRIGHT_HOLDER_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		COPYRIGHT_YEAR.setEnabled(WARNING_CHECKBOX.isSelected());
		COPYRIGHT_YEAR_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		DESCRIPTION.setEnabled(WARNING_CHECKBOX.isSelected());
		DESCRIPTION_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		LICENSE_ALLOWMOD_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		URL_FIELD.setEnabled(WARNING_CHECKBOX.isSelected());
		URL_LABEL.setEnabled(WARNING_CHECKBOX.isSelected());
		CC_VERIFICATION_URL_LABEL.setVisible(WARNING_CHECKBOX.isSelected());
		LICENSE_ALLOWCOM.setEnabled(WARNING_CHECKBOX.isSelected());
		LICENSE_ALLOWMOD_BOX.setEnabled(WARNING_CHECKBOX.isSelected());
	}
	
	/**
	 * Checks the validity of the input fields. 
	 * 
	 * @return true if the input is valid
	 */
	private boolean inputValid() {
		String holder = COPYRIGHT_HOLDER.getText();
		String year = COPYRIGHT_YEAR.getText();
		String title = WORK_TITLE.getText();
		String url = URL_FIELD.getText();
		if(holder.equals("")) {
			GUIMediator.showError("ERROR_CCPUBLISHER_MISSING_HOLDER");
			return false;
		}
		else if(year.equals("")) {
			GUIMediator.showError("ERROR_CCPUBLISHER_MISSING_YEAR");
			return false;
		}
		else if(title.equals("")) {
			GUIMediator.showError("ERROR_CCPUBLISHER_MISSING_TITLE");
			return false;
		}
		else if(url.equals("") || !url.startsWith("http://") || url.length()<8) {
			GUIMediator.showError("ERROR_CCPUBLISHER_MISSING_URL");
			URL_FIELD.setText("http://");
			return false;
		}
		try {
			new URL(url);
		}catch(MalformedURLException invalidURL) {
			GUIMediator.showError("ERROR_CCPUBLISHER_ERROR_URL");
			return false;
		}
		try {
			Integer.parseInt(year);
		}catch(NumberFormatException badDate) {
			GUIMediator.showError("ERROR_CCPUBLISHER_ERROR_DATE");
			return false;
		}
		return true;
	}
	
	/**
	 * Checks the validity of the input fields and if the license RDF has
	 * allready bean generated and is consistent.
	 * 
	 * @return true if input is valid
	 */
	public boolean checkInput() {
		if(WARNING_CHECKBOX.isSelected() && !_licenseRemoved && !inputValid())return false;
		else return true;
	}
	
	public void removeLicense() {
		int answer = GUIMediator.showYesNoMessage("CC_PUBLISHER_REMOVELICENSE_LABEL");
		if(answer == GUIMediator.YES_OPTION) {
			this.setVisible(false);
			_licenseRemoved=true;
			if(WARNING_CHECKBOX.isSelected())WARNING_CHECKBOX.doClick();
			INTRO_LABEL.setVisible(false);
			CC_INTRO_URL_LABEL.setVisible(false);
			WARNING_CHECKBOX.setVisible(false);
			REMOVE_LICENSE_LABEL.setText(GUIMediator.getStringResource("CC_PUBLISHER_LICENSEREMOVED_LABEL"));
			REMOVE_LICENSE_LABEL.setForeground(Color.BLACK);
			REMOVE_LICENSE_LABEL.removeMouseListener(REMOVE_LICENSE_LISTENER);
			REMOVE_LICENSE_LABEL.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setCursor(Cursor.getDefaultCursor());
				}
			});
		}
	}
	
	public FileEventListener getFileEventListener() {
		if(!checkInput() || _licenseRemoved) return null;
		return new CCRDFOuptut(_fd,COPYRIGHT_HOLDER.getText(),
				WORK_TITLE.getText(),
				COPYRIGHT_YEAR.getText(),
				DESCRIPTION.getText(),
				URL_FIELD.getText(),
				getLicenseType());
	}
	
	
	private int getLicenseType(){
		int type = CCConstants.ATTRIBUTION;
		if(!LICENSE_ALLOWCOM.isSelected()) {
			type|=CCConstants.ATTRIBUTION_NON_COMMERCIAL;
		}
		String mod = (String)LICENSE_ALLOWMOD_BOX.getSelectedItem();
		if(mod.equals(ALLOWMOD_SHAREALIKE)) {
			type|=CCConstants.ATTRIBUTION_SHARE;
		}
		else if(mod.equals(ALLOWMOD_NO)) {
			type|=CCConstants.ATTRIBUTION_NO_DERIVS;
		}
		return type;
	}
	
	/**
	 * Validates the input and returns an ArrayList with the 
	 * <name,value> MetaData of the license.
	 * 
	 * @return an ArrayList with the <name,value> tuples for the license and licensetype.
	 */
	public List getInput() {
		String holder = COPYRIGHT_HOLDER.getText();
		String year = COPYRIGHT_YEAR.getText();
		String title = WORK_TITLE.getText();
		String url = URL_FIELD.getText();
		String description = DESCRIPTION.getText();
		int type = getLicenseType();
		ArrayList valList = new ArrayList();
		if(_licenseRemoved) {
			valList.addAll(getPreviousValList());
			NameValue nameVal = new NameValue(MetaEditorUtil.AUDIO_LICENSE,"no license");
			valList.add(nameVal);
			nameVal = new NameValue(MetaEditorUtil.AUDIO_LICENSETYPE,"");
			valList.add(nameVal);
		}
		else if(WARNING_CHECKBOX.isSelected() && inputValid()) {
			valList.addAll(getPreviousValList());
			String embeddedLicense = PublishedCCLicense.getEmbeddableString(holder,title,year,url,description,type);
			if(embeddedLicense!=null) {
				NameValue nameVal = new NameValue(MetaEditorUtil.AUDIO_LICENSE,embeddedLicense);
				valList.add(nameVal);
				nameVal = new NameValue(MetaEditorUtil.AUDIO_LICENSETYPE,CCConstants.CC_URI_PREFIX);
				valList.add(nameVal);
			}
		}
		return valList;
	}
	
	private List getPreviousValList() {
		ArrayList valList = new ArrayList();
		for (Iterator iter = _xmlDoc.getNameValueSet().iterator(); iter.hasNext();) {
			Map.Entry oldNameVal = (Map.Entry) iter.next();
			String key = (String)oldNameVal.getKey();
			if(!key.equals(MetaEditorUtil.AUDIO_LICENSE)&&!key.equals(MetaEditorUtil.AUDIO_LICENSETYPE))
			valList.add(new NameValue((String)oldNameVal.getKey(),(String)oldNameVal.getValue()));
		}
		return valList;
	}
	
	private class WarningCheckBoxListener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			updateDisplay();
		}
	}
	
	private class RemoveLabelMouseListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			removeLicense();
		}

		public void mouseEntered(MouseEvent e) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		public void mouseExited(MouseEvent e) {
			 e.getComponent().setCursor(Cursor.getDefaultCursor());
		}
		
	}
}
