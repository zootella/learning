package com.limegroup.gnutella.gui;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.limegroup.gnutella.util.CommonUtils;

/**
 * This is a utility class that displays a file chooser dialog to the user,
 * automatically selecting the appropriate dialog based on the operating
 * system, the current theme, etc.  For example, if the user is on OS X
 * and is not using the default theme, this displays the standard
 * <tt>MetalLookAndFeel</tt> file chooser, as that is the only one that
 * will appear with themes.
 */
public final class FileChooserHandler {

	/**
	 * Displays a directory chooser to the user and returns the selected
	 * <tt>File</tt>.  This uses the main application frame as the parent
	 * component.
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a directory was not selected correctly
	 */
	public static File getInputDirectory() {
		return getInputDirectory(GUIMediator.getAppFrame());
	}

	/**
	 * Same as <tt>getInputDirectory</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent) {
		return getInputDirectory(parent, 
								 "FILE_CHOOSER_DIRECTORY_TITLE", 
								 CommonUtils.getCurrentDirectory());
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param directory the directory to open the dialog to
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, 
										 File directory) {
		return getInputDirectory(parent, 
								 "FILE_CHOOSER_DIRECTORY_TITLE", 
								 "FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
								 directory);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param directory the directory to open the dialog to
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, 
										 File directory,
										 FileFilter filter) {
		return getInputDirectory(parent, 
								 "FILE_CHOOSER_DIRECTORY_TITLE",  
								 "FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
								 directory,
								 filter);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param directory the directory to open the dialog to
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, String titleKey,
										 File directory) {
		return getInputDirectory(parent, 
								 titleKey, 
								 "FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
								 directory);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param directory the directory to open the dialog to
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, String titleKey,
										 File directory, FileFilter filter) {
		return getInputDirectory(parent, 
								 titleKey, 
								 "FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
								 directory,
								 filter);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, String titleKey,
										 String approveKey, File directory) {
		return getInputDirectory(parent, 
								 titleKey, 
								 approveKey,
								 directory,
								 null);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser as well as other options.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputDirectory(Component parent, String titleKey,
										 String approveKey, File directory,
										 FileFilter filter) {
		return getInput(parent, 
						titleKey, 
						approveKey,
						directory,
						JFileChooser.DIRECTORIES_ONLY,
						JFileChooser.APPROVE_OPTION,
						filter);
	}



	/**
	 * Displays a file chooser to the user and returns the selected
	 * <tt>File</tt>.  This uses the main application frame as the parent
	 * component.
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile() {
		return getInputFile(GUIMediator.getAppFrame());
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent) {
		return getInputFile(parent, 
							"FILE_CHOOSER_DIRECTORY_TITLE", 
							"FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
							CommonUtils.getCurrentDirectory());
	}


	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent, FileFilter filter) {
		return getInputFile(parent, 
							"FILE_CHOOSER_DIRECTORY_TITLE", 
							"FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
							CommonUtils.getCurrentDirectory(),
							filter);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param directory the directory to open the dialog to
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent, String titleKey,
									File directory) {
		return getInputFile(parent, 
							titleKey, 
							"FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
							directory);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param directory the directory to open the dialog to
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent, String titleKey,
									File directory, FileFilter filter) {
		return getInputFile(parent, 
							titleKey, 
							"FILE_CHOOSER_DIRECTORY_BUTTON_LABEL",
							directory,
							filter);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent, String titleKey,
									String approveKey, File directory) {
		return getInput(parent, 
						titleKey, 
						approveKey,
						directory,
						JFileChooser.FILES_ONLY,
						JFileChooser.APPROVE_OPTION);
	}

	/**
	 * Same as <tt>getInputFile</tt> that takes no arguments,
	 * except this allows the caller to specify the parent component of
	 * the chooser.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 *
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInputFile(Component parent, String titleKey,
									String approveKey, File directory,
									FileFilter filter) {
		return getInput(parent, 
						titleKey, 
						approveKey,
						directory,
						JFileChooser.FILES_ONLY,
						JFileChooser.APPROVE_OPTION,
						filter);
	}


	/**
	 * The implementation that the other methods delegate to.  This
	 * provides the caller with all available options for customizing
	 * the <tt>JFileChooser</tt> instance.  If a <tt>FileDialog</tt>
	 * is displayed instead of a <tt>JFileChooser</tt> (on OS X, for
	 * example), most or all of these options have no effect.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 * @param mode the "mode" to open the <tt>JFileChooser</tt> in from 
	 *  the <tt>JFileChooser</tt> class, such as 
	 *  <tt>JFileChooser.DIRECTORIES_ONLY</tt>
	 * @param option the option to look for in the return code, such as
	 *  <tt>JFileChooser.APPROVE_OPTION</tt>
	 * 
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInput(Component parent, String titleKey,
								String approveKey,
								File directory, int mode,
								int option) {
		return getInput(parent, titleKey, approveKey, directory, mode,
						option, null);
	}
	
	/**
	 * Opens a dialog asking the user to choose a file which is is used for saving
	 * to. 
	 * @param parent the parent component the dialog is centered on
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param suggestedFile the suggested file for saving
	 * @return the file or <code>null</code> when the user cancelled the dialog
	 */
	public static File getSaveAsFile(Component parent, String titleKey, File suggestedFile) {
	    return getSaveAsFile(parent, titleKey, suggestedFile, null);
    }
	
	/**
	 * Opens a dialog asking the user to choose a file which is is used for saving
	 * to. 
	 * @param parent the parent component the dialog is centered on
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param suggestedFile the suggested file for saving
	 * @param the filter to use for what's shown.
	 * @return the file or <code>null</code> when the user cancelled the dialog
	 */
	public static File getSaveAsFile(Component parent, String titleKey,
	                                 File suggestedFile, final FileFilter filter) {
		if(CommonUtils.isAnyMac()) {
			FileDialog dialog = new FileDialog(GUIMediator.getAppFrame(),
											   GUIMediator.getStringResource(titleKey),
											   FileDialog.SAVE);
			dialog.setDirectory(suggestedFile.getParent());
			dialog.setFile(suggestedFile.getName()); 
		    if(filter != null) {
                FilenameFilter f = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return filter.accept(new File(dir, name));
                    }
                };
                dialog.setFilenameFilter(f);
            }

			dialog.setVisible(true);
			String dir = dialog.getDirectory();
			String file = dialog.getFile();
			if(dir != null && file != null) {
			    File f = new File(dir, file);
			    if(filter != null && !filter.accept(f))
			        return null;
			    else
			        return f;
            } else {
                return null;
            }
		} else {
			JFileChooser chooser = getDirectoryChooser(titleKey, null, null, JFileChooser.FILES_ONLY, filter);
			chooser.setSelectedFile(suggestedFile);
			return chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION ? 
						null : chooser.getSelectedFile();
		}
	}

	/**
	 * The implementation that the other methods delegate to.  This
	 * provides the caller with all available options for customizing
	 * the <tt>JFileChooser</tt> instance.  If a <tt>FileDialog</tt>
	 * is displayed instead of a <tt>JFileChooser</tt> (on OS X, for
	 * example), most or all of these options have no effect.
	 *
	 * @param parent the <tt>Component</tt> that should be the dialog's
	 *  parent
	 * @param titleKey the key for the locale-specific string to use for
	 *  the file dialog title
	 * @param approveKey the key for the locale-specific string to use for
	 *  the approve button text
	 * @param directory the directory to open the dialog to
	 * @param mode the "mode" to open the <tt>JFileChooser</tt> in from 
	 *  the <tt>JFileChooser</tt> class, such as 
	 *  <tt>JFileChooser.DIRECTORIES_ONLY</tt>
	 * @param option the option to look for in the return code, such as
	 *  <tt>JFileChooser.APPROVE_OPTION</tt>
	 * @param filter the <tt>FileFilter</tt> instance for customizing 
	 *  the files that are displayed -- if this is null, no filter is used
	 * 
	 * @return the selected <tt>File</tt> instance, or <tt>null</tt> if
	 *  a file was not selected correctly
	 */
	public static File getInput(Component parent, String titleKey,
								String approveKey,
								File directory, int mode,
								int option,
								final FileFilter filter) {
            if(!CommonUtils.isAnyMac()) {
                JFileChooser fileChooser = 
                    getDirectoryChooser(titleKey, approveKey, directory, mode, filter);
                try {
                    if(fileChooser.showOpenDialog(parent) != option)
                        return null;
                } catch(NullPointerException npe) {
                    // ignore NPE.  can't do anything with it ...
                    return null;
                }

                return fileChooser.getSelectedFile();
                
            } else {
                FileDialog dialog;
                if(mode == JFileChooser.DIRECTORIES_ONLY)
                    dialog = MacUtils.getFolderDialog();
                else
                    dialog = new FileDialog(GUIMediator.getAppFrame(), "");
                
                dialog.setTitle(GUIMediator.getStringResource(titleKey));
                if(filter != null) {
                    FilenameFilter f = new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return filter.accept(new File(dir, name));
                        }
                    };
                    dialog.setFilenameFilter(f);
                }
                
                dialog.setVisible(true);
                String dirStr = dialog.getDirectory();
                String fileStr = dialog.getFile();
                  
                if((dirStr==null) || (fileStr==null))
                    return null;
                // if the filter didn't work, pretend that the person picked
                // nothing
                File f = new File(dirStr, fileStr);
                if(filter != null && !filter.accept(f))
                    return null;
                
                return f;
            }		
	}

	/**
     * Returns a new <tt>JFileChooser</tt> instance for selecting directories
     * and with internationalized strings for the caption and the selection
     * button.
     * 
     * @param approveKey can be <code>null</code>
     * @param directory can be <code>null</code>
     * @param filter can be <code>null</code>
     * @return a new <tt>JFileChooser</tt> instance for selecting directories.
     */
    private static JFileChooser getDirectoryChooser(String titleKey,
            String approveKey, File directory, int mode, FileFilter filter) {
        JFileChooser chooser = null;
        if (directory == null) {
            chooser = new JFileChooser();
        } else {
            try {
                chooser = new JFileChooser(directory);
            } catch (NullPointerException e) {
                // Workaround for JRE bug 4711700. A NullPointer is thrown
                // sometimes on the first construction under XP look and feel,
                // but construction succeeds on successive attempts.
                chooser = new JFileChooser(directory);
            }
        }
        if (filter != null) {
            chooser.setFileFilter(filter);
        } else {
			if (mode == JFileChooser.DIRECTORIES_ONLY) {
				chooser.setFileFilter(new FileFilter() {
					public boolean accept(File file) {
						return true;
					}
					public String getDescription() {
						return GUIMediator.getStringResource("DIRECTORY_CHOOSER_FILE_DESCRIPTION");
					}
				});
			}
        }
        chooser.setFileSelectionMode(mode);
        String title = GUIMediator.getStringResource(titleKey);
        chooser.setDialogTitle(title);

		if (approveKey != null) {
			String approveButtonText = GUIMediator.getStringResource(approveKey);
			chooser.setApproveButtonText(approveButtonText);
		}
        return chooser;
    }
}
