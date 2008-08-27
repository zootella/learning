package com.limegroup.gnutella.gui.actions;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.gui.search.SearchInformation;
import com.limegroup.gnutella.gui.search.SearchMediator;
import com.limegroup.gnutella.util.I18NConvert;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.util.StringUtils;
import com.limegroup.gnutella.xml.LimeXMLDocument;

/**
 * A class that triggers an xml search query for a single xml field and its
 * value.
 * <p>
 * After the search request has been sent the search panel is focused.
 */
public class SearchXMLFieldAction extends AbstractAction {

	private NameValue displayPair;
	private String name;
	private String value;
	private NamedMediaType nm;

	/**
	 * Constructs an xml field search action.
	 * @param displayPair used for constructing the name of the action
	 * @param name the name of the xml field
	 * @param value the value of the xmls field
	 * @param nm the mediatype whose xml schema is used for the xml query
	 */
	public SearchXMLFieldAction(NameValue displayPair, String name, 
			String value,
			NamedMediaType nm) {
		this.displayPair = displayPair;
		this.name = name;
		this.value = value;
		this.nm = nm;
		String formatted = MessageFormat.format(
                GUIMediator.getStringResource("SEARCH_XML_FIELD_ACTION_NAME"),
				new Object[] { displayPair.getName(), displayPair.getValue() }
        );
        if(formatted.length() > 80)
            formatted = formatted.substring(0, 80) + "...";
		
		putValue(Action.NAME, formatted);
	}
	
	public void actionPerformed(ActionEvent e) {
		NameValue namValue = new NameValue(name, I18NConvert.instance().getNorm(value));
		String xml = (new LimeXMLDocument(Collections.singletonList(namValue),
				nm.getSchema().getSchemaURI())).getXMLString();
		SearchMediator.triggerSearch(SearchInformation.createTitledKeyWordSearch
				(StringUtils.createQueryString(value, true), xml, 
						nm.getMediaType(),
						displayPair.getName() + ": "
						+ displayPair.getValue()));
		GUIMediator.instance().setWindow(GUIMediator.SEARCH_INDEX);
	}
	
}