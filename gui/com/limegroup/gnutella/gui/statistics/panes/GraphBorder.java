package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

import com.limegroup.gnutella.gui.statistics.StatisticsMediator;

/**
 * Draws the border of a graph based on the supplied criteria.
 */
final class GraphBorder extends AbstractBorder {

	/**
	 * The offset between the left most pixel of this component and
	 * where the axis are drawn.
	 */
	private static final int OFFSET = 30;

	/**
	 * The number of tick marks on the x axis.
	 */
	private final int NUM_X_TICKS;

	/**
	 * The <tt>GraphAxisData</tt> instance containing all data for
	 * axis scale and display.
	 */
	private final GraphAxisData DATA;


	/**
	 * Creates a new <tt>GraphBorder</tt> instance with the specified number
	 * of values between x and y hash marks.
	 *
	 * @param xIncrement the number of values between x-axis hash marks
	 * @param yIncrement the number of values between y-axis hash marks
	 */
	GraphBorder(GraphAxisData data) {
		DATA = data;
  		NUM_X_TICKS = DATA.getTotalXValues()/DATA.getXIncrement();
	}

	/**
	 * Paints the x and y axis of the graph.
	 */
	public void paintBorder(Component comp, Graphics g, int x, 
							int y, int width, int height) {
		
		// the thickness of the axis lines
		int thickness = 2;
	   
		int yVal = 0;
		int xVal = 0;
		int yBase = y+height-OFFSET;

        JComponent component = 
            StatisticsMediator.getStatDisplayComponent();
        int xAxisPixelLength = component.getWidth()-OFFSET;
        
        int yAxisPixelLength = component.getHeight()-OFFSET-60;

        for(int i=0; i<thickness; i++)  {
			yVal = y+height-i-OFFSET;

			// draw x-axis
			g.drawLine(x+OFFSET, yVal, xAxisPixelLength, yVal);

			// draw y-axis
			xVal = x+i+OFFSET;
            int yTop = yBase-yAxisPixelLength;
			g.drawLine(xVal, yTop, xVal, yBase);
        }

		Font oldFont = g.getFont();
		Font tempFont = new Font(oldFont.getName(), 
								 oldFont.getStyle(),
								 oldFont.getSize()-1);
		g.setFont(tempFont);
		drawHashedLineX(x+OFFSET, yVal, g);
		drawHashedLineY(xVal, y, yBase, g);
		g.setFont(oldFont);
	}

	/**
	 * Accessor for the offset of the x and y axis.
	 *
	 * @return the offset, in pixels of the x and y axis
	 */
	static int getOffset() {
		return OFFSET;
	}

	/**
	 * Draws the hashes along the x axis.
	 *
	 * @param x0 the starting x position
	 * @param y0 the starting y position
	 * @param g the <tt>Graphics</tt> instance for doing the drawing
	 */
	private void drawHashedLineX(int x0, int y0, Graphics g) {
		int startY = y0-4;
		int endY = y0+4;
		double curX = x0;
        int xInc = DATA.getXIncrement();
        JComponent component = 
            StatisticsMediator.getStatDisplayComponent();
        double drawingWidth = (double)(component.getWidth()-OFFSET-x0);
        double xVal = drawingWidth/(double)DATA.getTotalXValues();
		for(int i=0, num=DATA.getTotalXValues()/xInc+1; 
			i<num; i++) {			
			g.drawLine((int)curX, startY, (int)curX, endY);
			g.drawString(new Integer((NUM_X_TICKS-i)*xInc).toString(),
						 (int)curX, startY+20); 
            curX += xVal*(double)xInc;
		}
		g.drawString(DATA.getXAxisLabel()+" vs. "+DATA.getYAxisLabel(), (int)((drawingWidth/2)-20), y0+30); 
	} 

	/**
	 * Draws the hashes along the y axis.
	 *
	 * @param x0 the starting x position
	 * @param y0 the starting y position
	 * @param y1 the ending y position
	 * @param g the <tt>Graphics</tt> instance for doing the drawing
	 */
	private void drawHashedLineY(int x0, int y0, int y1, Graphics g) {
		int startX = x0-4;
		int endX = x0+4;
		double curY = y1;
        int yInc = DATA.getYIncrement();
        JComponent component = 
            StatisticsMediator.getStatDisplayComponent();
        double yVal = (double)(component.getHeight()-OFFSET-y0-60)/
            (double)DATA.getTotalYValues();
		double pixelJump = yVal*(double)yInc;
		for(int i=0, num=(DATA.getTotalYValues()/yInc)+1;
			i<num; i++, curY -= pixelJump) {
			g.drawLine(startX, (int)curY, endX, (int)curY);
			g.drawString(new Integer(i*yInc).toString(),
						 startX-20, (int)curY); 
		}
	}	   
}








