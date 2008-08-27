package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.Statistic;

/**
 * This class handles displaying statistics for averages.
 */
final class AveragesPainter extends JComponent 
	implements ComponentListener, StatisticDisplayer {


	/**
	 * <tt>List</tt> of all statistics displayed for this window.
	 */
	private final List LIST = new LinkedList();

	/**
	 * String for the name column label of the legend.
	 */
	private static final String NAME = 
		GUIMediator.getStringResource("STATS_LEGEND_NAME");

	/**
	 * String for the average value column label of the legend.
	 */
	private static final String AVERAGE = 
		GUIMediator.getStringResource("STATS_LEGEND_AVERAGE");

	/**
	 * Constant <tt>NumberFormat</tt> to display floating point numbers
	 */
	private static final NumberFormat NUMBER_FORMAT = 
		NumberFormat.getNumberInstance();



	/**
	 * Constructs a new <tt>AveragesPainter</tt> instance with default settings.
	 */
	AveragesPainter() {
		this.addComponentListener(this);
	}


	// implements StatisticDisplayer -- inherit doc comment
	public void registerStatistic(Statistic stat, String displayName) {}

	// implements StatisticDisplayer -- inherit doc comment
	public void registerDualStatistic(Statistic totalBytes, 
									  Statistic totalMessages, 
									  String displayName) {
		AverageStatHandler averageHandler = 
			new AverageStatHandler(totalBytes, totalMessages, 
								   displayName);
		LIST.add(averageHandler);
	}

	// implements StatisticDisplayer -- inherit doc comment
	public void refresh() {
		repaint();
	}

	// implements StatisticDisplayer -- inherit doc comment
	public JComponent getComponent() {
		return this;
	}

	/**
	 * Notifies all <tt>Statistic</tt> instances for this graph that
	 * data should be written out to a file.
	 *
	 * @param write specifies whether or not to write data to a file
	 */
	public void setWriteStatsToFile(boolean write) {}


	private static final int NAME_COLUMN_WIDTH    = 160;
	private static final int AVERAGE_COLUMN_WIDTH =  54;

	private static final int RECT_WIDTH = 
		NAME_COLUMN_WIDTH +
		AVERAGE_COLUMN_WIDTH;


	/**
	 * Paints the graph, including the legend.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Font  oldFont  = g.getFont();
		final Font tempFont  = new Font(oldFont.getName(), 
			oldFont.getStyle(), oldFont.getSize() - 1);
		g.setFont(tempFont);
		final FontMetrics m = g.getFontMetrics();

		final int rowHeight = m.getHeight() + 1;
		final int rowBase = m.getLeading() / 2 + m.getMaxAscent();

		final int yOrig = 0;
		final int xName    = getWidth()/2 - RECT_WIDTH/2;
		final int xAverage = xName    + NAME_COLUMN_WIDTH;
		final int xMax     = xAverage + AVERAGE_COLUMN_WIDTH;
		final int margin = 3;
		int y = yOrig;

		g.drawString(NAME,    xName    + margin, y + rowBase);
		g.drawString(AVERAGE, xAverage + margin, y + rowBase);
		y += rowHeight;
		g.drawLine(xName, y, xName +RECT_WIDTH, y);
		y++;

		// loop through all of the statistics, displaying their graphs
		// and their legends
		final Iterator iter = LIST.iterator();
		while (iter.hasNext()) {
			final AverageStatHandler handler = (AverageStatHandler)iter.next();
			final double totalBytes  = handler.getByteStat().getTotal();
			final double totalNumber = handler.getNumberStat().getTotal();			
			double averageDouble = 0;
			if(totalNumber != 0) {
				averageDouble = totalBytes/totalNumber;
			}

			NUMBER_FORMAT.setMaximumFractionDigits(3);
			final String average =
				NUMBER_FORMAT.format(averageDouble);

			final String name = handler.getDisplayName();
			g.drawString(name,    xName    + margin, y + rowBase);
			g.drawString(average, xAverage + margin, y + rowBase);
			y += rowHeight;
		}

		// draw the outside of the legend
		g.drawRect(xName,    yOrig, RECT_WIDTH, y - yOrig);
		g.drawLine(xAverage, yOrig, xAverage, y);
		g.drawLine(xMax,     yOrig, xMax,     y);
		g.setFont(oldFont);
	}

	// implements the ComponentListener interface
	public void componentResized(ComponentEvent e) {}

	// implements the ComponentListener interface
	public void componentShown(ComponentEvent e) {}	

	// implements the ComponentListener interface
	public void componentHidden(ComponentEvent e) {}

	// implements the ComponentListener interface
	public void componentMoved(ComponentEvent e) {}
}
