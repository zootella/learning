package com.limegroup.gnutella.gui.tables;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import com.limegroup.gnutella.util.CommonUtils;

/**
 * The default drag source listener, updating the drag icon
 * when necessary.
 */
public final class DefaultDragSourceListener implements DragSourceListener {
    
    private static final DefaultDragSourceListener INSTANCE =
        new DefaultDragSourceListener();
    public static DefaultDragSourceListener instance() { return INSTANCE; }
    private DefaultDragSourceListener() {}

    /** Does nothing. */
    public void dragDropEnd(DragSourceDropEvent dsde) {}
    
    /**
     * Changes the cursor according to the supported actions.
     */
    public void dragEnter(DragSourceDragEvent dsde) {
        changeCursor(dsde);
    }
    
    /**
     * Changes the cursor to show no drop supported.
     */
    public void dragExit(DragSourceEvent dse){
        dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
    }
    
    /** Does nothing */
    public void dragOver(DragSourceDragEvent dsde) {}
    
    /**
     * Changes the cursor to show the supported actions.
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
        changeCursor(dsde);
    }
    
    /**
     * Changes the cursor according to the actions supported.
     */
    private void changeCursor(DragSourceDragEvent dsde) {
        DragSourceContext c = dsde.getDragSourceContext();
        int actions;
        if(CommonUtils.isJava14OrLater())
            actions = dsde.getDropAction();
        else
            actions = dsde.getTargetActions(); // :(
        
        if     ((actions & DnDConstants.ACTION_COPY) == DnDConstants.ACTION_COPY)
            c.setCursor(DragSource.DefaultCopyDrop);
        else if((actions & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE)
            c.setCursor(DragSource.DefaultMoveDrop);
        else if((actions & DnDConstants.ACTION_LINK) == DnDConstants.ACTION_LINK)
            c.setCursor(DragSource.DefaultLinkDrop);
        else
            c.setCursor(DragSource.DefaultCopyNoDrop);
    }
}