package com.limegroup.gnutella.gui.download;

import java.awt.Component;
import java.awt.Dialog;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.IncompleteFileDesc;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.browser.MagnetOptions;
import com.limegroup.gnutella.gui.FileChooserHandler;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.settings.QuestionsHandler;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Static helper class that kicks of downloads handling all the necessary
 * consistency check and showing appropriate error/warning dialogs.
 */
public class DownloaderUtils {

	/**
	 * Tries to create a downloader for a factory performing the following
	 * consistency checks beforehand:
	 * <ul>
	 * <li>{@link #isAlreadyDownloading(DownloaderFactory)}
	 * <li>{@link #isSaveLocationTaken(DownloaderFactory)}
	 * <li>if the proposed save location is not taken
	 * {@link #continueWithOrWithoutHashConflict(DownloaderFactory)} is
	 * performed
	 * </ul>
	 * 
	 * @param factory
	 * @return <code>null</code> if there is another download for the same
	 *         hash, or incomplete file, or the user cancelled the download at
	 *         some point.
	 */
	public static Downloader createDownloader(DownloaderFactory factory) {

		// check for already downloading conflicts
		if (isAlreadyDownloading(factory)) {
			return null;
		}

		// check for file name conflicts
		if (!isSaveLocationTaken(factory)) {

			// check for hash conflicts
			if (!continueWithOrWithoutHashConflict(factory)) {
				return null;
			}
		}

		// try to start download
		return createDownloader(factory, false);
	}

	/**
	 * Tries to create a downloader for a factory asking the user for a save
	 * location for the download.
	 * <p>
	 * Performs the following consistency checks:
	 * <ul>
	 * <li>{@link #isAlreadyDownloading(DownloaderFactory)}
	 * <li>{@link #continueWithOrWithoutHashConflict(DownloaderFactory)}
	 * </ul>
	 * 
	 * @param factory
	 * @return <code>null</code> if there is another download for the same
	 *         hash or incomplete file, or the user cancelled somewhere along
	 *         the way.
	 */
	public static Downloader createDownloaderAs(DownloaderFactory factory) {

		// check for already downloading conflicts
		if (isAlreadyDownloading(factory)) {
			return null;
		}

		// check for hash conflicts
		if (!continueWithOrWithoutHashConflict(factory)) {
			return null;
		}

		File file = showFileChooser(factory, MessageService
				.getParentComponent());
		if (file == null) {
			return null;
		}
		factory.setSaveFile(file);

		// OSX's FileDialog box already prompts the user that they're
		// going to be overwriting a file, so we don't need to do that
		// particular check again.
		return createDownloader(factory, CommonUtils.isAnyMac());
	}
	
	/**
	 * Tries to create a downloader for a magnet.
	 * <p>
	 * The magnet may also be {@link MagnetOptions#isDownloadable() not valid 
	 * for downloading}, then a warning is displayed.
	 * @param magnet
	 * @return <code>null</code> if the magnet is is invalid or the user 
	 * cancelled the process
	 */
	public static Downloader createDownloader(MagnetOptions magnet) {
		String msg = magnet.getErrorMessage();
		if (!magnet.isDownloadable()) {
			if (msg == null) {
				msg = magnet.toString();
			}
			// show warning and return
			GUIMediator.showError("ERROR_BAD_MAGNET_LINK", msg);
			return null;
		}
		if (msg != null) {
			// show warning but proceed
			GUIMediator.showWarning("ERROR_INVALID_URLS_IN_MAGNET");
		}
		MagnetDownloaderFactory factory = new MagnetDownloaderFactory(magnet);
		if (magnet.getDisplayName() == null) {
			Downloader dl = createDownloaderAs(factory);
			if (dl != null && magnet.isHashOnly()) {
				GUIMediator.showError("DOWNLOAD_HASH_ONLY_MAGNET");
			}
			return dl;
		}
		else {
			return createDownloader(factory);
		}
	}

	/**
	 * Tries to create a downloader from a factory.
	 * <p>
	 * If the {@link DownloaderFactory#createDownloader(boolean)} throws an
	 * exception, {@link DownloaderDialog#handle(DownloaderFactory, 
	 * SaveLocationException)} is called to handle it.
	 * <p>
	 * If the process was successful, the final file is shared individually if
	 * it's not in a shared directory.
	 * 
	 * @param factory
	 * @param overwrite
	 * @return <code>null</null> if the user cancelled at some point
	 */
	public static Downloader createDownloader(DownloaderFactory factory,
											  boolean overwrite) {
		try {
			return factory.createDownloader(overwrite);
		} catch (SaveLocationException sle) {
			return DownloaderDialog.handle(factory, sle);
		}
	}

	/**
	 * Checks if there is a conflicting download already running with the same
	 * hash or incomplete file name, shows a notification dialog and returns
	 * true.
	 * 
	 * @param factory
	 * @return
	 */
	public static boolean isAlreadyDownloading(DownloaderFactory factory) {
		if (RouterService.getDownloadManager().conflicts(factory.getURN(),
				factory.getSaveFile().getName(), factory.getFileSize())) {
			showIsAlreadyDownloadingWarning(factory);
			return true;
		}
		return false;
	}
	
	public static void showIsAlreadyDownloadingWarning(DownloaderFactory factory) {
		GUIMediator.showFormattedError(
				"FORMATTED_ERROR_ALREADY_DOWNLOADING",
				new Object[] { factory.getSaveFile() },
				QuestionsHandler.ALREADY_DOWNLOADING);
	}

	/**
	 * Returns a non-incomplete FileDesc for the urn or null.
	 * 
	 * @param urn
	 * @return
	 */
	public static FileDesc getFromLibrary(URN urn) {
		if (urn == null) {
			return null;
		}
		FileDesc desc = RouterService.getFileManager().getFileDescForUrn(urn);
		return (desc instanceof IncompleteFileDesc) ? null : desc;
	}

	/**
	 * Checks if there is already a file in the library with the same urn and
	 * shows a dialog to user if (s)he wants to continue anyway.
	 * 
	 * @param factory
	 * @return <code>true></code> if there is no conflict or the user wants to
	 *         continue anyway
	 */
	public static boolean continueWithOrWithoutHashConflict(
			DownloaderFactory factory) {
		FileDesc desc = getFromLibrary(factory.getURN());
		if (desc != null) {
			return showHashConflict(desc);
		}
		return true;
	}

	private static boolean showHashConflict(FileDesc desc) {
		String message = MessageFormat.format(GUIMediator
				.getStringResource("DOWNLOADER_UTILS_HASH_CONFLICT_MESSAGE"),
				new Object[] { desc.getFile() });
		String question = GUIMediator
				.getStringResource("DOWNLOADER_UTILS_HASH_CONFLICT_QUESTION");
		String continueLabel = GUIMediator
				.getStringResource("DOWNLOADER_UTILS_HASH_CONFLICT_CONTINUE_LABEL");

		String[] content = new String[] { message, question };

		JOptionPane pane = new JOptionPane(
				content,
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION,
				null,
				new String[] {
						continueLabel,
						GUIMediator.getStringResource("GENERAL_CANCEL_BUTTON_LABEL") }
				);
		Dialog dialog = pane.createDialog(MessageService.getParentComponent(),
							GUIMediator.getStringResource("DOWNLOADER_UTILS_ALREADY_IN_LIBRARY"));
		dialog.setVisible(true);
		return continueLabel.equals(pane.getValue());
	}

	/**
	 * Shows a filechooser for selecting a save location for a downloader.
	 * 
	 * @param factory
	 * @param c
	 * @return
	 */
	public static File showFileChooser(DownloaderFactory factory, Component c) {
		return FileChooserHandler.getSaveAsFile(c, "DOWNLOADER_UTILS_FILECHOOSER_TITLE",
				factory.getSaveFile());
	}

	/**
	 * Checks if the final save location already exists or is taken by another
	 * download.
	 * 
	 * @param factory
	 * @return
	 */
	private static boolean isSaveLocationTaken(DownloaderFactory factory) {
		return factory.getSaveFile().exists()
				|| RouterService.getDownloadManager().isSaveLocationTaken(
						factory.getSaveFile());
	}

}
