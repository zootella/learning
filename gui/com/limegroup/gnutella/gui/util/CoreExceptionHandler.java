package com.limegroup.gnutella.gui.util;

import java.text.MessageFormat;

import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.gui.GUIMediator;

/**
 * Static helper class that handles exceptions from the core by creating 
 * localized error messages and presenting those to the user via
 * {@link MessageService}. 
 */
public class CoreExceptionHandler {

	/**
	 * Handles {@link SaveLocationException} by presenting an error dialog to
	 * the user.
	 * @param sle the exception to handle
	 * @throws IllegalArgumentException if the error code is not handled
	 */
	public static void handleSaveLocationError(SaveLocationException sle) {
		GUIMediator.showTranslatedError(getSaveLocationErrorString(sle));
	}

	/**
	 * Returs a localized string summing up the details of the exception
	 * depending on its error code.
	 */
	public static String getShortSaveLocationErrorString(SaveLocationException sle) {
		switch (sle.getErrorCode()) {
		case SaveLocationException.SECURITY_VIOLATION:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_SECURITY_VIOLATION_SHORT_ERROR");
		case SaveLocationException.FILE_ALREADY_SAVED:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_ALREADY_SAVED_SHORT_ERROR");
		case SaveLocationException.DIRECTORY_NOT_WRITEABLE:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_DIRECTORY_NOT_WRITEABLE_SHORT_ERROR");
		case SaveLocationException.DIRECTORY_DOES_NOT_EXIST:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_DIRECTORY_DOES_NOT_EXIST_SHORT_ERROR");
		case SaveLocationException.FILE_ALREADY_EXISTS:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_ALREADY_EXISTS_SHORT_ERROR");
		case SaveLocationException.FILE_IS_ALREADY_DOWNLOADED_TO:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_IS_ALREADY_DOWNLOADED_TO_SHORT_ERROR");
		case SaveLocationException.NOT_A_DIRECTORY:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_NOT_A_DIRECOTRY_SHORT_ERROR");
		case SaveLocationException.FILE_NOT_REGULAR:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_FILE_NOT_REGULAR_SHORT_ERROR");
		case SaveLocationException.FILESYSTEM_ERROR:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_FILESYSTEM_SHORT_ERROR");
		case SaveLocationException.FILE_ALREADY_DOWNLOADING:
			return GUIMediator.getStringResource
				("SAVE_LOCATION_ALREADY_DOWNLOADING_SHORT_ERROR");
		default:
			throw new IllegalArgumentException("Unhandled error code: " 
											   + sle.getErrorCode());
		}
	}
	
	/**
	 * Returns a localized string that explains in detail the exception
	 * depending on its error code.
	 */
	public static String getSaveLocationErrorString(SaveLocationException sle) {
		switch (sle.getErrorCode()) {
		case SaveLocationException.SECURITY_VIOLATION:
			return MessageFormat.format
				(GUIMediator.getStringResource("SAVE_LOCATION_SECURITY_VIOLATION_ERROR"),
				 new Object[] { sle.getFile() });
		case SaveLocationException.FILE_ALREADY_SAVED:
			return GUIMediator.getStringResource("SAVE_LOCATION_ALREADY_SAVED_ERROR");
		case SaveLocationException.DIRECTORY_NOT_WRITEABLE:
			return MessageFormat.format
			(GUIMediator.getStringResource("SAVE_LOCATION_DIRECTORY_NOT_WRITEABLE_ERROR"),
					new Object[] { sle.getFile() });
		case SaveLocationException.DIRECTORY_DOES_NOT_EXIST:
			return MessageFormat.format
				(GUIMediator.getStringResource("SAVE_LOCATION_DIRECTORY_DOES_NOT_EXIST_ERROR"),
				 new Object[] { sle.getFile() });
		case SaveLocationException.FILE_ALREADY_EXISTS:
			return MessageFormat.format
				(GUIMediator.getStringResource("SAVE_LOCATION_ALREADY_EXISTS_ERROR"),
				 new Object[] { sle.getFile() });
		case SaveLocationException.FILE_IS_ALREADY_DOWNLOADED_TO:
			return MessageFormat.format
				(GUIMediator.getStringResource("SAVE_LOCATION_IS_ALREADY_DOWNLOADED_TO_ERROR"),
						new Object[] { sle.getFile() });
		case SaveLocationException.NOT_A_DIRECTORY:
			return MessageFormat.format
			(GUIMediator.getStringResource("SAVE_LOCATION_NOT_A_DIRECOTRY_ERROR"),
					new Object[] { sle.getFile() });
		case SaveLocationException.FILE_NOT_REGULAR:
			return MessageFormat.format
				(GUIMediator.getStringResource("SAVE_LOCATION_FILE_NOT_REGULAR_ERROR"),
				 new Object[] { sle.getFile() });
		case SaveLocationException.FILESYSTEM_ERROR:
			return GUIMediator.getStringResource("SAVE_LOCATION_FILESYSTEM_ERROR");
		case SaveLocationException.FILE_ALREADY_DOWNLOADING:
			return GUIMediator.getStringResource("SAVE_LOCATION_ALREADY_DOWNLOADING_ERROR");
		default:
			throw new IllegalArgumentException("Unhandled error code: " 
											   + sle.getErrorCode());
		}
	}
	
}
