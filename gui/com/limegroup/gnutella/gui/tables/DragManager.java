package com.limegroup.gnutella.gui.tables;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import com.limegroup.gnutella.gui.IconManager;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Manages installation of the drag aspect of a LimeJTable.
 */
public final class DragManager {

    /**
     * The pane to use for drawing the Drag image.
     */
    private static final CellRendererPane PANE = new CellRendererPane();
    
    /**
     * The maximum width of the drag image.
     */
    private static final int IMAGE_WIDTH = 300;
    
    /**
     * The height of each row in the drag image.
     */
    private static final int IMAGE_ROW_HEIGHT = 16;

    /**
     * Empty constructor -- all management is static.
     */
    private DragManager() {}
    
    /**
     * Installs drag recognition for the specified JTree.
     */
    public static void install(JTree tree) {
        // Construct the drag recognizer.
        try {
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                tree,
                DnDConstants.ACTION_COPY | DnDConstants.ACTION_LINK, 
                DragListener.instance());
        } catch(Throwable ignored) {}
    }
    
    /**
     * Installs drag recognition for the specified LimeJTable.
     *
     * This does a bit of voodoo to make everything work:
     * A drag gesture recognizer uses a MouseListener & a MouseMotionListener
     * to recognize drag events.  Unfortunately, list selection also
     * uses MouseListeners & MouseMotionListeners, and these two listeners
     * can become at odds with each other -- the drag recognizer starting
     * a drag just as the list selection changes...
     * In order to resolve everything, we need to make careful use of
     * 'consume()' and 'isConsumed()' in MouseEvent.  On Windows, the
     * listeners for selection correctly check for isConsumed before
     * processing.  This is not done on OSX.  To work on all platforms,
     * we install a proxy Mouse[Motion]Listener that discards the appropriate
     * events if they were consumed.
     * Further complicating things, the DragGestureRecognizers are platform
     * dependent, using various key combinations to initiate the drag with
     * different default actions (move / copy / link).  Even worse, these
     * platform dependent D&D listeners do not consume the mouse event.
     * To work around this, we make use of the fact that the default
     * drag gesture recognizer extends MouseDragGestureRecognizer, which is
     * both a MouseListener & MouseMotionListener, and then wrap the platform
     * dependent listener around our own TableDragRecnogitionWrapper, which
     * correctly consumes mouse events, preventing the selection code from
     * changing the selection.
     *
     * A bit of estoric wierdness is added with tooltips, in that they will NPE
     * if they don't receive all events, all the time.  To work around that,
     * we first unregister the component for tooltips & then later reregister
     * it.
     *
     * The order of listeners must be:
     * - The D&D listeners, a TableDragRecognitionWrapper.
     * - Pre-existing listeners, checked for mouse event consumption.
     * - Tooltip listeners, not checked for mouse event consumption.
     * - Any listeners added later.
     * 
     */
    public static void install(LimeJTable table) {
        // First unregister tooltips -- we don't want to proxy tooltip events.
        ToolTipManager.sharedInstance().unregisterComponent(table);
        
        // Get a copy of the old MouseListener & MouseMotionListeners, so we
        // can later proxy them through a MouseEventConsumptionChecker.
        MouseListener[] oldMouseListeners =
            (MouseListener[])table.getListeners(MouseListener.class);
        MouseMotionListener[] oldMouseMotionListeners =
            (MouseMotionListener[])table.getListeners(MouseMotionListener.class);
            
        // Remove them, so the DragGestureRecognizer listeners can be installed
        // as the first listeners.
        for(int i = 0; i < oldMouseListeners.length; i++)
            table.removeMouseListener(oldMouseListeners[i]);
        for(int i = 0; i < oldMouseMotionListeners.length; i++)
            table.removeMouseMotionListener(oldMouseMotionListeners[i]);
            
        // Construct the drag recognizer.
        // We want to use a platform-dependent GestureRecognizer,
        // so that the fireDragGestureRecognized event
        // can set the correct actions based on user input,
        // but we also want to use our listeners
        // for input, so that can consume the event appropriately
        // and only make dragging possible if something was selected.
        MouseDragGestureRecognizer recognizer;
        try {
            recognizer = (MouseDragGestureRecognizer)
                DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                    table,
                    DnDConstants.ACTION_COPY | DnDConstants.ACTION_LINK, 
                    DragListener.instance());
        } catch(Throwable failed) {
            // If it can't load DnD, then restore & exit.
            for(int i = 0; i < oldMouseListeners.length; i++)
                table.addMouseListener(oldMouseListeners[i]);
            for(int i = 0; i < oldMouseMotionListeners.length; i++)
                table.addMouseMotionListener(oldMouseMotionListeners[i]);
            ToolTipManager.sharedInstance().registerComponent(table);
            return;
        }
            
        // Wrap the recognizer around another recognizer that will also
        // check for selection.
        table.removeMouseListener(recognizer);
        table.removeMouseMotionListener(recognizer);
        TableDragRecognitionWrapper inputListener =
            new TableDragRecognitionWrapper(table, recognizer);
        table.addMouseListener(inputListener);
        table.addMouseMotionListener(inputListener);
        // in order for the recognition to work all the time, it also needs to
        // be added as a drag source listener.
        // however, the method only exists on java 1.4+ ... so on java 1.3
        // and below we need to add it when the actual drag starts.
        if(CommonUtils.isJava14OrLater())
            DragSource.getDefaultDragSource().addDragSourceListener(inputListener);
        else
            table.putClientProperty("limewire.dragRecognizer", inputListener);
        
        // Re-add the mouse & mouse motion listeners, proxied by the
        // event-consumption checker.
        for(int i = 0; i < oldMouseListeners.length; i++)
            table.addMouseListener(MouseEventConsumptionChecker.proxy(oldMouseListeners[i]));
        for(int i = 0; i < oldMouseMotionListeners.length; i++)
            table.addMouseMotionListener(MouseEventConsumptionChecker.proxy(oldMouseMotionListeners[i]));
        table.setMouseEventsProxied(true);
            
        // Then re-register tooltips.
        ToolTipManager.sharedInstance().registerComponent(table);
    }
    
    /**
     * Creates an image for the given transferable.
     */
    public static Image createDragImage(Transferable t) {
        if(!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
            return null;
            
        List l = null;
        try {
            l = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
        } catch(UnsupportedFlavorException ufe) {
            return null;
        } catch(IOException ioe) {
            return null;
        }
        
        // if no data, there's nothing to draw.
        if(l.size() == 0)
            return null;

        int height = IMAGE_ROW_HEIGHT * l.size();
        BufferedImage buffer = new BufferedImage(
                    IMAGE_WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = buffer.getGraphics();
        JLabel label = new JLabel();
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setOpaque(false);
        int y = 0;
        for(Iterator i = l.iterator(); i.hasNext(); ) {
            File f = (File)i.next();
            Icon icon = IconManager.instance().getIconForFile(f);
            label.setIcon(icon);
            label.setText(f.getName());
            PANE.paintComponent(g, label, null, 0, y, IMAGE_WIDTH, height - y);
            y += IMAGE_ROW_HEIGHT;
        }

        g.dispose();
        return buffer;
    }
}