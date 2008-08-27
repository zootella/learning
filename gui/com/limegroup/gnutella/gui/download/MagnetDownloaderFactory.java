package com.limegroup.gnutella.gui.download;

import java.io.File;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.browser.MagnetOptions;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * Creates a Downloader from a magnet
 *
 */
public class MagnetDownloaderFactory implements DownloaderFactory {

	private MagnetOptions magnet;
	private File saveFile;
	
	/**
	 * Constructs a factory for a magnet
	 * @param magnet
	 * @throws IllegalArgumentException if the magnet is not 
	 * {@link MagnetOptions#isDownloadable() valid for download}
	 */
	public MagnetDownloaderFactory(MagnetOptions magnet) {
		this.magnet = magnet;
		String fileName = magnet.getDisplayName();
		if (fileName == null) { 
			fileName = GUIMediator.getStringResource("NO_FILENAME_LABEL");
		}
		this.saveFile = new File(SharingSettings.getSaveDirectory(), fileName);
		if (!magnet.isDownloadable()) {
			throw new IllegalArgumentException("Invalid magnet");
		}
	}
	
	public File getSaveFile() {
		return saveFile;
	}

	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	public int getFileSize() {
		return 0;
	}

	public URN getURN() {
		return magnet.getSHA1Urn();
	}

	public Downloader createDownloader(boolean overwrite)
			throws SaveLocationException {
		return RouterService.download(magnet, overwrite, 
									  saveFile.getParentFile(),
									  getSaveFile().getName());
	}

}
