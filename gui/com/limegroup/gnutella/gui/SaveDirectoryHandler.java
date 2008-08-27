package com.limegroup.gnutella.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.util.FileUtils;

/**
 * This class handles prompting the user to enter a valid save directory.
 */
public final class SaveDirectoryHandler {   

    /**
     * Ensure that this class cannot be constructed from outside this class.
     */
    private SaveDirectoryHandler() {}

    /**
     * Constructs a new window that prompts the user to enter a valid save
     * directory.
     *
     * This doesn't return until the user has chosen a valid directory.
     */
    private static void showSaveDirectoryWindow() {
        File dir = null;
        while(!isSaveDirectoryValid(dir)) {
            GUIMediator.showError("ERROR_INVALID_SAVE_DIRECTORY_WINDOW");
            dir = showChooser();
            if(dir == null)
                continue;
            FileUtils.setWriteable(dir);
        }
    }

    /**
     * Shows the chooser & sets the save directory setting, adding the save
     * directory as shared, also.
     *
     * @return the selected <tt>File</tt>, or <tt>null</tt> if there were
     *  any problems
     */
    private static File showChooser() {
        File dir = FileChooserHandler.getInputDirectory(null);
        if(dir != null) {
            try {
                // updates Incomplete directory etc... 
                SharingSettings.setSaveDirectory(dir); 
                SharingSettings.DIRECTORIES_TO_SHARE.add(dir);
                return dir;
            } catch(IOException ignored) {}
        }
        
        return null;
    }
    
    /**
     * Utility method for checking whether or not the save directory is valid.
     * 
     * @param saveDir the save directory to check for validity
     * @return <tt>true</tt> if the save directory is valid, otherwise 
     *  <tt>false</tt>
     */
    public static boolean isSaveDirectoryValid(File saveDir) {
        if(saveDir == null || !saveDir.exists() || !saveDir.isDirectory())
            return false;

        FileUtils.setWriteable(saveDir);
        
        RandomAccessFile testRAFile = null;
        File testFile = new File(saveDir, "test");
        try {
            testRAFile = new RandomAccessFile(testFile, "rw");
         
            // Try to write something just to make extra sure we're OK.
            testRAFile.write(7);
            testRAFile.close();
        } catch (FileNotFoundException e) {
            // If we could not open the file, then we can't write to that 
            // directory.
            return false;
        } catch(IOException e) {
            // The directory is invalid if there was an error writing to it.
            return false;
        } finally {
            // Delete our test file.
            testFile.delete();
            try {
                if(testRAFile != null)
                    testRAFile.close();
            } catch (IOException ignored) {}
        }
        
        return saveDir.canWrite();
    }

    /**
     * Makes sure that the user has a valid save directory.
     */
    public static void handleSaveDirectory() {    
        File saveDir = SharingSettings.getSaveDirectory();
        if(!isSaveDirectoryValid(saveDir))
            showSaveDirectoryWindow();
    }
}

