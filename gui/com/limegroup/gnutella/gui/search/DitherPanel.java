package com.limegroup.gnutella.gui.search;

import javax.swing.JPanel;

/** Simple extension of JPanel that makes a FlowLayout.LEADING JPanel that
 *  has a background image which is painted.
 */
public class DitherPanel extends JPanel {

    private final Ditherer DITHERER;
    private boolean isDithering = true;
    
    /**
     * Creates a FlowLayout.LEADING layout.
     *
     * @param ditherer the <tt>Ditherer</tt> that paints the dithered 
	 *  background
     */
    public DitherPanel(Ditherer ditherer) { 
        super();
        DITHERER = ditherer;
    }    

    /** Does the actual placement of the background image.
     */
    public void paintComponent(java.awt.Graphics g) {
        if(isDithering && 
           isOpaque() &&
           !DITHERER.getTop().equals(DITHERER.getBottom()))
            DITHERER.draw(g, getSize().height, getSize().width);
        else
            super.paintComponent(g);
    }
    
    public void setDithering(boolean dither) {
        isDithering = dither;
    }
    
}



