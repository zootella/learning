package com.limegroup.gnutella.gui.tables;

/**
 * An interface representing a file that can be transfered
 * lazily -- not retrieving the actual filename until
 * necessary.
 */
public interface LazyFileTransfer extends FileTransfer {
    
    /**
     * Retrieve an object which can construct the file when necessary.
     */
    public FileTransfer getLazyFile();
}