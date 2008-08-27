package com.limegroup.gnutella.gui;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JProgressBar;

import com.limegroup.gnutella.gui.ProgTabUIFactory;

/**
 * A progress-bar tab for use with OSX on Java 1.4.
 */
public class AquaTab extends apple.laf.AquaTabbedPaneUI {
    
    private final JProgressBar PROGRESS = new JProgressBar();
    private final CellRendererPane PANE = new CellRendererPane();
    
    public AquaTab() {
        super();
        PROGRESS.setMinimum(0);
        PROGRESS.setMaximum(100);
        PROGRESS.setBorderPainted(true);
    }   
    
    /**
     * Extended to paint the progress bar.
     */
    protected void paintContents(Graphics g, 
                                 int tabPlacement,
                                 int selTab,
                                 Rectangle tabRect,
                                 Rectangle iconRect,
                                 Rectangle tabRect2,
                                 boolean isSelected) {
        // The size check is necessary because an extra tab
        // is created that lets the user choose which hidden
        // tabs should be displayed.  This tab has no component
        // and is not really part of the JTabbedPane, and thus
        // causes an IndexOutOfBoundsException when getting the
        // component.  The extra tab is only created when there
        // are too many tabs to be displayed in a single tab run.
        if( selTab < tabPane.getTabCount() ) {
            int x = tabRect.x + 4;
            int y = tabRect.y + 4;
            int w = tabRect.width - 6;
            int h = tabRect.height - 2;
            long currentTime = System.currentTimeMillis();
            ProgTabUIFactory.Progressor prog =
                (ProgTabUIFactory.Progressor)tabPane.getComponentAt(selTab);
            double percent = prog.calculatePercentage(currentTime);
            if(percent > 1)
                percent = 1.0;
            PROGRESS.setValue((int)(percent * 100));
            PANE.paintComponent(g, PROGRESS,  tabPane.getParent(), x, y, w, h);
        }
        super.paintContents(g, tabPlacement, selTab, tabRect, iconRect,
                            tabRect2, isSelected);
    }
}
