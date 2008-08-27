package com.limegroup.gnutella.gui.tables;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 * Combines two drag source listeners.
 */
public final class CompositeDragSourceListener implements DragSourceListener {
    
    private final DragSourceListener a;
    private final DragSourceListener b;
    
    public CompositeDragSourceListener(DragSourceListener a, DragSourceListener b) {
        this.a = a;
        this.b = b;
    }
    
    public void dragDropEnd(DragSourceDropEvent dsde) {
        a.dragDropEnd(dsde);
        b.dragDropEnd(dsde);
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {
        a.dragEnter(dsde);
        b.dragEnter(dsde);
    }
    
    public void dragExit(DragSourceEvent dse){
        a.dragExit(dse);
        b.dragExit(dse);
    }
    
    public void dragOver(DragSourceDragEvent dsde) {
        a.dragOver(dsde);
        b.dragOver(dsde);
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        a.dropActionChanged(dsde);
        b.dropActionChanged(dsde);
    }
}