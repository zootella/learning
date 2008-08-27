package com.limegroup.gnutella.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JProgressBar;

import com.limegroup.gnutella.gui.ProgTabUIFactory;

/**
 * A progress-bar tab for use with OSX on Java 1.3.
 */
public class MacTab extends com.apple.mrj.swing.MacTabbedPaneUI {
    
        
    private final JProgressBar PROGRESS = new JProgressBar();
    private final CellRendererPane PANE = new CellRendererPane();
    
    public MacTab() {
        super();
        PROGRESS.setMinimum(0);
        PROGRESS.setMaximum(100);
        PROGRESS.setBorderPainted(false);
        PROGRESS.setStringPainted(false);
        PROGRESS.setOpaque(true);
    }  
    
    /**
     * Extended to paint the progress bar.
     */
    protected void paintTabNormal(Graphics g,
                                  int tabPlacement,
                                  Rectangle[] rects,
                                  int tabIndex,
                                  Rectangle iconRect,
                                  Rectangle textRect,
                                  boolean unknown,
                                  boolean unknown2) {
            super.paintTabNormal(g, tabPlacement, rects, tabIndex,
                                 iconRect, textRect, unknown, unknown2);
            ProgTabUIFactory.Progressor prog =
                (ProgTabUIFactory.Progressor)tabPane.getComponentAt(tabIndex);
            double percent = prog.calculatePercentage(System.currentTimeMillis());
            Rectangle tabRect = rects[tabIndex];
            int x = tabRect.x + 4;
            int y = tabRect.y + 1;
            int w = tabRect.width - 8;
            int h = tabRect.height - textRect.height - 3;
            if(percent > 1)
                percent = 1;
            PROGRESS.setValue((int)(percent * 100));
            PROGRESS.setString(tabPane.getTitleAt(tabIndex));
            PANE.paintComponent(g, PROGRESS,  tabPane.getParent(), x, y, w, h);
        }

}