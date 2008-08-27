package com.limegroup.gnutella.gui.search;

import java.awt.Color;


/**
 * Optimized class to draw vertical fades from one color to another.
 */
public final class Ditherer {
    private final int _redT;
    private final int _greenT;
    private final int _blueT;

    private final int _redB;
    private final int _greenB;
    private final int _blueB;

    private final int STEPS;
    
    private final Color top;
    private final Color bottom;

    /** 
     * Constructs a new ditherer that will fade from top to bottom vertically.
     */
    Ditherer(int steps, Color top, Color bottom) { 
        STEPS = steps;
        _redT   = top.getRed();
        _greenT = top.getGreen();
        _blueT  = top.getBlue();

        _redB   = bottom.getRed();
        _greenB = bottom.getGreen();
        _blueB  = bottom.getBlue();
        
        this.top = top;
        this.bottom = bottom;
	}
	
	Color getTop() { return top; }
	Color getBottom() { return bottom; }
	

    /** 
     * Draws the requested fade to g, with the given width and height.
     */
    public void draw(final java.awt.Graphics g, final int height, final int width) {
        final float redStep=(float)(_redB-_redT)/(float)STEPS;    
        final float greenStep=(float)(_greenB-_greenT)/(float)STEPS;
        final float blueStep=(float)(_blueB-_blueT)/(float)STEPS;
        float red=(float)_redT;
        float green=(float)_greenT;
        float blue=(float)_blueT;

        final int yStep=height/STEPS;   //rounds down
        int y=0;

        //Draw a rectangle for each step
        for (int i=0; i<STEPS; i++) {
            Color c=new Color(round(red), round(green), round(blue));
            g.setColor(c);
            g.fillRect(0, y, width, yStep);

            y+=yStep;
            red+=redStep;
            green+=greenStep;
            blue+=blueStep;
        }
        
        //Ensure bottom is filled.
        Color c=new Color(round(red), round(green), round(blue));
        g.setColor(c);
        g.fillRect(0, y, width, height-y);
    }

    private static int round(float color) {
        int ret=Math.round(color);
        if (ret<0)
            return 0;
        if (ret>255)
            return 255;
        else
            return ret;
    }
}
