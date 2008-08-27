package com.limegroup.gnutella.gui.xml.editor;

import com.limegroup.gnutella.FileDesc;

public class DocumentTabbedPane extends MetaEditorTabbedPane {

	public DocumentTabbedPane(FileDesc fd) {
		 super(fd, MetaEditorUtil.DOCUMENT_SCHEMA);
		 add(new DocumentEditor(fd, getSchema(), getDocument()));
	}

}
