package com.limegroup.gnutella.gui.xml.editor;

import com.limegroup.gnutella.FileDesc;

public class CCPublisherTabbedPane extends MetaEditorTabbedPane {

	public CCPublisherTabbedPane(FileDesc fd) {
		super(fd, MetaEditorUtil.AUDIO_SCHEMA);
		
		add(new CCPublisherTab(fd,getDocument()));
	}
}
