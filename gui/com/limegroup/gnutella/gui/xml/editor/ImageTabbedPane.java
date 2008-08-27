package com.limegroup.gnutella.gui.xml.editor;

import com.limegroup.gnutella.FileDesc;

public class ImageTabbedPane extends MetaEditorTabbedPane {

	public ImageTabbedPane(FileDesc fd) {
		 super(fd, MetaEditorUtil.IMAGE_SCHEMA);
		 add(new ImageEditor(fd, getSchema(), getDocument()));
	}

}
