package com.limegroup.gnutella.gui.library;

import javax.swing.Icon;

import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.settings.FileSetting;

public class SavedFilesDirectoryHolder extends FileSettingDirectoryHolder {

	public SavedFilesDirectoryHolder(FileSetting saveDir, String name) {
		super(saveDir, name);
	}
	
	public Icon getIcon() {
		NamedMediaType nmt = NamedMediaType.getFromDescription("*");
		return nmt.getIcon();
	}
}
