package com.limegroup.gnutella.gui.library;

import java.io.File;

import javax.swing.Icon;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.SharingSettings;

public class IncompleteDirectoryHolder extends FileSettingDirectoryHolder {

	public IncompleteDirectoryHolder() {
		super(SharingSettings.INCOMPLETE_DIRECTORY,
				GUIMediator.getStringResource("LIBRARY_TREE_INCOMPLETE_DIRECTORY"));
	}
		
	
	public boolean accept(File file) {
		String name = file.getName();
		return super.accept(file) &&
		!file.isHidden() &&
		!name.startsWith(".") &&
		file.isFile() &&
		!name.equals("downloads.dat") &&
		!name.equals("downloads.bak");
	}
	
	public Icon getIcon() {
		return GUIMediator.getThemeImage("incomplete");
	}
}
