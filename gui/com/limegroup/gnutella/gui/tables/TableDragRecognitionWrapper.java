package com.limegroup.gnutella.gui.tables;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.limegroup.gnutella.ErrorService;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Wraps the mouse events around mouse listeners that consume
 * the event & check for pointing on a selection.
 */
public class TableDragRecognitionWrapper implements MouseInputListener,
                                                    DragSourceListener {
    /**
     * Indicates the dnd is armed.
     */
    private boolean dndArmed = false;
    
    /**
     * The table this wrapper is working on.
     *
     * Required for checking to see if the mouse was acting
     * on a selection.
     */
    private final LimeJTable table;
    
    /**
     * The delegate MouseDragGestureRecognizer.
     */
    private final MouseDragGestureRecognizer delegate;
    
    /**
     * The last press.  Required for reprocessing presses
     * that was mistaked for an initiating d&d gesture.
     */
    private MouseEvent storedPress = null;
    
    /**
     * Constructs a new drag recognizer.
     */
    TableDragRecognitionWrapper(LimeJTable table, MouseDragGestureRecognizer wrappee) {
        this.table = table;
        this.delegate = wrappee;
    }
    
    /**
     * Forwards the click to the delegate.
     *
     * If the prior press was consumed, then we unconsume
     * it and process the event.  This is necessary to allow
     * presses that were mistaken for the initiating d&d gesture
     * to be correctly processed as selections.
     */
    public void mouseClicked(MouseEvent e) {
        dndArmed = false;
        delegate.mouseClicked(e);
        // if it was a click & we consumed it improperly
        // then reprocess it, unconsumed.
        if(storedPress != null && storedPress.isConsumed()) {
            storedPress = unconsume(storedPress);
            table.processMouseEvent(storedPress);
        }
    }

    /**
     * Ignores the event if it was the 'stored press', allowing
     * a mistaken d&d recognition to be corrected.
     *
     * Arms the d&d code & consumes the event, preventing
     * selection from changing.
     */
    public void mousePressed(MouseEvent e) {
        if(storedPress == e)
            return;
        
        dndArmed = false;
        storedPress = e;

    	if (table.isPointSelected(e.getPoint()) &&
    	    SwingUtilities.isLeftMouseButton(e) ) {
            dndArmed = true;
    	    e.consume();
    	    delegate.mousePressed(e);
    	}
    }
    
    /**
     * De-arms the d&d event.
     */
    public void mouseReleased(MouseEvent e) {
        dndArmed = false;
        delegate.mouseReleased(e);
    }
    
    public void mouseEntered(MouseEvent e) {
        delegate.mouseEntered(e);
    }
    
    public void mouseExited(MouseEvent e) {
        delegate.mouseExited(e);
    }
    
    /**
     * DnD is broken on OSX with JVMs elder than 1.4.2_05.
     * 
     * @return true if OSX and JVM is not 1.4.2_05 or later
     */
    private boolean isOSXAndDnDIsBroken() {
        return CommonUtils.isMacOSX() &&
               CommonUtils.getJavaVersion().compareTo("1.4.2_05") < 0;
    }
    
    /**
     * Consumes the event if the d&d action is still armed,
     * preventing the selection code from changing the selection
     */
    public void mouseDragged(MouseEvent e) {
        if (dndArmed) {
            // If it's broken, select only 1 row, 'cause
            // more than that will fail.
            if (isOSXAndDnDIsBroken()) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1)
                    table.setSelectedRow(row);
            }
            e.consume();
        }

        delegate.mouseDragged(e);
    }
    
    public void mouseMoved(MouseEvent e) {
        delegate.mouseDragged(e);
    }
    
    /**
     * Unarms the d&d.
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        dndArmed = false;
    }
    
    public void dragEnter(DragSourceDragEvent dsde) {}
    
    public void dragExit(DragSourceEvent dse){}
    
    public void dragOver(DragSourceDragEvent dsde) {}
    
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
    /**
     * The 'consumed' field in a MouseEvent.
     */
    private final static Field consumed;
    static {
        Field f = null;
        try {
            f = java.awt.AWTEvent.class.getDeclaredField("consumed");
        } catch(NoSuchFieldException nsfe) {
            ErrorService.error(nsfe);
        }
        if(f != null)
            f.setAccessible(true);
        consumed = f;
    }

    /**
     * Uses reflection to unset the 'consumed' field in a MouseEvent.
     * This is required for fixing mistaken d&d recognition.
     */
    private static MouseEvent unconsume(MouseEvent e) {
        try {
            if(consumed != null)
                consumed.setBoolean(e, false);
        } catch(IllegalAccessException iae) {}
        return e;
    }
}