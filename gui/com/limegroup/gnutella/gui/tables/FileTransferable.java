package com.limegroup.gnutella.gui.tables;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FileTransferable implements Transferable {
    private final List files;
    private final List lazyFiles;
    
    public FileTransferable(List l) {
        this(l, null);
    }
    
    public FileTransferable(List real, List lazy) {
        files = real;
        lazyFiles = lazy;
    }

    public synchronized Object getTransferData(DataFlavor flavor) 
      throws UnsupportedFlavorException, IOException {
        if(!isDataFlavorSupported(flavor))
            throw new UnsupportedFlavorException(flavor);
            
        if(lazyFiles != null && lazyFiles.size() > 0) {
            for(Iterator i = lazyFiles.iterator(); i.hasNext(); ) {
                File f = ((FileTransfer)i.next()).getFile();
                if(f != null)
                    files.add(f);
            }
            lazyFiles.clear();
        }   
            
        return files;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.javaFileListFlavor };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.javaFileListFlavor);
    }
}