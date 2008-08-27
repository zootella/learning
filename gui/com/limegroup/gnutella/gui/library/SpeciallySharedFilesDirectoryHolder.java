package com.limegroup.gnutella.gui.library;

import java.io.File;

import javax.swing.Icon;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;

public class SpeciallySharedFilesDirectoryHolder extends AbstractDirectoryHolder {

	public String getName() {
		return GUIMediator.getStringResource
			("LIBRARY_TREE_INDIVIDUALLY_SHARED_FILES_DIRECTORY");
	}

	public String getDescription() {
		return GUIMediator.getStringResource
			("LIBRARY_TREE_INDIVIDUALLY_SHARED_FILES_DIRECTORY_TOOLTIP");
	}

	public File getDirectory() {
		return null;
	}
	
	public boolean isEmpty() {
	    return !RouterService.getFileManager().hasIndividualFiles();
	}
	
	public File[] getFiles() {
	    return RouterService.getFileManager().getIndividualFiles();
	}
	
	public boolean accept(File file) {
		return RouterService.getFileManager().isIndividualShare(file); 
	}
	
	public Icon getIcon() {
		return GUIMediator.getThemeImage("multifile_small");
	}
}
