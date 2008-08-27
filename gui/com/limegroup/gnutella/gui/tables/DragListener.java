package com.limegroup.gnutella.gui.tables;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.limegroup.gnutella.util.CommonUtils;

/**
 * A listener for drag gesture events, constructing a Transferable
 * based off the table's selection.
 */
public class DragListener implements DragGestureListener {
    
    private static final DragListener INSTANCE = new DragListener();
    public static final DragListener instance() { return INSTANCE; }
    
    /**
     * Initiates a drag with the files in the selected rows.
     */
    public void dragGestureRecognized(DragGestureEvent dge) {
        List l = new LinkedList();
        List lazy = new LinkedList();
        
        Component source = dge.getComponent();
        if(source instanceof LimeJTable)
            fillFromTable(dge, l, lazy);
        else if(source instanceof JTree)
            fillFromTree(dge, l, lazy);
        else
            return;

        if(l.size() == 0 && lazy.size() == 0)
            return;

        Transferable t = new FileTransferable(l, lazy);
        DragSourceListener listener = DefaultDragSourceListener.instance();
        if(!CommonUtils.isJava14OrLater()) {
            Object recog = ((JComponent)source).getClientProperty("limewire.dragRecognizer");
            if(recog != null)
                listener = new CompositeDragSourceListener(
                    listener, (DragSourceListener)recog
                );
        }

        try {
            if(DragSource.isDragImageSupported())
                dge.startDrag(DragSource.DefaultCopyNoDrop, 
                              DragManager.createDragImage(t),
                              new Point(2, 2),
                              t, 
                              listener);
            else
                dge.startDrag(DragSource.DefaultCopyNoDrop,
                              t,
                              listener);
        } catch(InvalidDnDOperationException ignored) {}
    }
    
    /**
     * Fills up the lists 'l' and 'lazy' with files or Lazy Files.
     */
    private void fillFromTable(DragGestureEvent dge, List l, List lazy) {
        LimeJTable table = (LimeJTable)dge.getComponent();
        DataLine[] lines = table.getSelectedDataLines();
        for(int i = 0; i < lines.length; i++)
            addFileTransfer((FileTransfer)lines[i], l, lazy);
    }
    
    /**
     * Fills up the lists 'l' and 'lazy' with files or lazy files.
     */
    private void fillFromTree(DragGestureEvent dge, List l, List lazy) {
        JTree tree = (JTree)dge.getComponent();
        TreePath path = tree.getSelectionPath();
        if(path != null)
            addFileTransfer((FileTransfer)path.getLastPathComponent(), l, lazy);
    }
    
    /**
     * Adds the specified FileTransfer to the lists.
     */
    private void addFileTransfer(FileTransfer transfer, List l, List lazy) {
        File f = transfer.getFile();
        
        if(f == null) {
            if(transfer instanceof LazyFileTransfer)
                lazy.add(((LazyFileTransfer)transfer).getLazyFile());
        } else {
            addFile(l, f);
        }
    }
    
    /**
     * Adds a file to a list as the canonical file.
     */
    private void addFile(List l, File f) {
        try {
            f = f.getCanonicalFile();
        } catch(IOException ignored) {}
        l.add(f);
    }    
}