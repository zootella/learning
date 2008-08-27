package com.limegroup.gnutella.gui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;

import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.gui.xml.XMLUtils;
import com.limegroup.gnutella.util.NameValue;
import com.limegroup.gnutella.xml.LimeXMLDocument;
import com.limegroup.gnutella.xml.LimeXMLSchema;
import com.limegroup.gnutella.xml.SchemaFieldInfo;

/**
 * Static action helper class.
 *
 */
public class ActionUtils {

	/**
	 * Creates an array of {@link SearchXMLFieldAction SearchXMLFieldActions}
	 * for an xml doc.
	 * <p>
	 * It takes all fields and creates an action for each field whose value is
	 * not <code>null</code> and longer than 2 characters.
	 * @param doc
	 * @return
	 */
	public static Action[] createSearchActions(LimeXMLDocument doc) {

		LimeXMLSchema schema = doc.getSchema();
		ArrayList actions = new ArrayList();
		NamedMediaType nm = NamedMediaType.getFromDescription(doc.getSchemaDescription());
		
		for (Iterator i = schema.getCanonicalizedFields().iterator(); i.hasNext();) {
			SchemaFieldInfo field = (SchemaFieldInfo) i.next();
			String name = field.getCanonicalizedFieldName();
			String value = doc.getValue(name);
			if (value == null || value.length() < 3 || field.isHidden())
				continue;
			NameValue displayPair = XMLUtils.getDisplayPair(field, value, schema);
			actions.add(new SearchXMLFieldAction(displayPair, name, value, nm));
		}
		
		return (Action[])actions.toArray(new Action[0]);
	}
}
