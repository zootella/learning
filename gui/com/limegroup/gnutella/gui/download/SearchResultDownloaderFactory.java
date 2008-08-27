package com.limegroup.gnutella.gui.download;

import java.io.File;
import java.util.List;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * Implements the DownloaderFactory interface to start downloads from
 * incoming search results.
 */
public class SearchResultDownloaderFactory implements DownloaderFactory {

	private RemoteFileDesc[] rfds;
	private List alts;
	private GUID queryGUID;
	private File saveDir;
	private String fileName;
	
	public SearchResultDownloaderFactory(RemoteFileDesc[] rfds,
			List alts, GUID queryGUID,
			File saveDir, String fileName) {
		this.rfds = rfds;
		this.alts = alts;
		this.queryGUID = queryGUID;
		this.saveDir = saveDir;
		this.fileName = fileName != null ? fileName : rfds[0].getFileName();
	}
			
	
	public URN getURN() {
		return rfds[0].getSHA1Urn();
	}

	public Downloader createDownloader(boolean overwrite)
		throws SaveLocationException {
		return RouterService.download(rfds, queryGUID, overwrite, saveDir, fileName);
	}

	public File getSaveFile() {
		return new File(saveDir != null ? saveDir : SharingSettings.getSaveDirectory(),
				fileName);
	}

	public void setSaveFile(File saveFile) {
		File parentDir = saveFile.getParentFile(); 
		if (!parentDir.equals(SharingSettings.getSaveDirectory())) {
			saveDir = parentDir;
		}
		fileName = saveFile.getName();
	}


	public int getFileSize() {
		return rfds[0].getSize();
	}
}
