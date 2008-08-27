package com.limegroup.gnutella.gui;

import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * Displays a status update in various ways, depending on the
 * operating system & JDK.
 *
 * Below JDK 1.4:
 *   - Displays a status label (center, left, or right justified)
 * JDK 1.4 w/ OSX:
 *   - Displays an indeterminate JProgressBar with a JLabel
 *     left justified above it.
 * JDK 1.4 w/o OSX:
 *   - Displays an indeterminate JProgressBar with the status text
 *     inside the progressbar.
 */
public class StatusComponent extends JPanel {
    
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int CENTER = 2;
    
    /**
     * The position to display the status label.
     * Only used if running below JDK 1.4.
     */
    private final int POSITION;
    
    /**
     * The JProgressBar whose text is updated, if running
     * on Java 1.4+ (and not OSX).
     */
    private final JProgressBar BAR;
    
    /**
     * The JLabel being updated if this is running on
     * below Java 1.4 or on Java 1.4 w/ OSX.
     */
    private final JLabel LABEL;
        
    /**
     * Creates a new StatusComponent.
     */
    public StatusComponent(int orientation) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        POSITION = orientation;
        LABEL = new JLabel();

        if(CommonUtils.isJava14OrLater()) {
            BAR = new JProgressBar();
            BAR.setIndeterminate(true);
        } else {
            BAR = null;
        }
        construct();
        GUIUtils.setOpaque(false, this);
        if(BAR != null && !CommonUtils.isMacOSX())
            BAR.setOpaque(true);
        if(LABEL != null)
            LABEL.setForeground(ThemeFileHandler.WINDOW4_COLOR.getValue());
    }
    
    /**
     * Sets the preferred size of the progressbar.
     */
    public void setProgressPreferredSize(Dimension dim) {
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
        if(BAR != null) {
            BAR.setMinimumSize(dim);
            BAR.setMaximumSize(dim);
            BAR.setPreferredSize(dim);
        }
    }
    
    /**
     * Updates the status of this component.
     */
    public void setText(String text) {
        if(CommonUtils.isJava14OrLater() &&
           !CommonUtils.isMacOSX()) {
            BAR.setString(text);
        } else {
            FontMetrics fm = LABEL.getFontMetrics(LABEL.getFont());
            LABEL.setPreferredSize(new Dimension(fm.stringWidth(text),
                                                 fm.getHeight()));
            LABEL.setText(text);
        }
    }
    
    /**
     * Constructs the panel.
     */
    private void construct() {
        if(CommonUtils.isJava14OrLater()) {
            if(CommonUtils.isMacOSX()) {
                JPanel textPanel = new BoxPanel(BoxPanel.X_AXIS);
                textPanel.add(LABEL);
                textPanel.add(Box.createHorizontalGlue());
                add(textPanel);
            } else {
                BAR.setStringPainted(true);
            }
            add(BAR);
        } else {
            JPanel textPanel = new BoxPanel(BoxPanel.X_AXIS);
            if(POSITION == CENTER) {
                textPanel.add(Box.createHorizontalGlue());
                textPanel.add(LABEL);
                textPanel.add(Box.createHorizontalGlue());
            } else if(POSITION == LEFT) {
                textPanel.add(LABEL);
                textPanel.add(Box.createHorizontalGlue());
            } else if(POSITION == RIGHT) {
                textPanel.add(Box.createHorizontalGlue());
                textPanel.add(LABEL);
            }
            add(textPanel);
        }
    }
}                
        
