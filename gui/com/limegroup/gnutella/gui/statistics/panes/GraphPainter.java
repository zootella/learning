package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Color;
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
import com.limegroup.gnutella.statistics.NumericalStatistic;
import com.limegroup.gnutella.statistics.Statistic;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.util.IntBuffer;

/**
 * This class handles painting the graph, including updating the data 
 * for the given statistic.  It offloads the painting of the axis to
 * another class.
 *
 * @see GraphAxisData
 * @see GraphBorder
 */
final class GraphPainter extends JComponent 
	implements ComponentListener, StatisticDisplayer {

	/**
	 * Variable for the x coordinates for a graph.
	 */
	private final int[] X_COORDS =
		new int[Statistic.HISTORY_LENGTH];

	/**
	 * The x and y axis for this graph.
	 */
	private GraphBorder _border;

	/**
	 * <tt>List</tt> of all statistics displayed for this window.
	 */
	private final List LIST = new LinkedList();

	/**
	 * The current color index to use.
	 */
	private static int _curColor = 0;

	/**
	 * The colors used in the graph sequentially.  As each new data item
	 * is added, it is given a different color.
	 */
	private final Color[] GRAPH_COLORS = {
		// TODO: white is problematic on OS X -- put this in themes?
        // usually play white, but black on OSX - this really should be in
        // themes
		CommonUtils.isMacOSX() ? new Color(0,0,0) : new Color(255, 255, 255),	
		new Color(  0, 153,   0),	//medium green
		new Color(255, 102, 255),	//medium pink
		new Color(255, 255,   0),	//plain yellow
		new Color(255,  51,   0),	//light red
		new Color( 51, 153, 204),	//medium cyan
		new Color(255, 153,   0),	//medium orange
		new Color(153,   0,  51),	//dark red
		new Color( 51,   0, 153),	//dark blue
	};

	/**
	 * Default graph scale data for the x and y axis.
	 */
	private static GraphAxisData DEFAULT_AXIS_DATA = new GraphAxisData();

	/**
	 * The actual <tt>GraphAxisData</tt> for this graph instance.
	 */
	private final GraphAxisData AXIS_DATA;

	/**
	 * String for the name column label of the legend.
	 */
	private static final String NAME = 
		GUIMediator.getStringResource("STATS_LEGEND_NAME");

    /**
     * String for the current value column label of the legend.
     */
    private static final String CURRENT =
        GUIMediator.getStringResource("STATS_LEGEND_CURRENT");

	/**
	 * String for the average value column label of the legend.
	 */
	private static final String AVERAGE = 
		GUIMediator.getStringResource("STATS_LEGEND_AVERAGE");

	/**
	 * String for the max value column label of the legend.
	 */
	private static final String MAX = 
		GUIMediator.getStringResource("STATS_LEGEND_MAX");

	/**
	 * String for the total messages column label of the legend.
	 */
	private static final String TOTAL = 
		GUIMediator.getStringResource("STATS_LEGEND_TOTAL");

	/**
	 * String for the colorcolumn label of the legend.
	 */
	private static final String COLOR = 
		GUIMediator.getStringResource("STATS_LEGEND_COLOR");

	/**
	 * Constant <tt>NumberFormat</tt> to display floating point numbers
	 */
	private static final NumberFormat NUMBER_FORMAT = 
		NumberFormat.getNumberInstance();

	private double _yPixelFactor = 0;

	/**
	 * Constructs a new <tt>GraphPainter</tt> instance with default settings.
	 */
	GraphPainter() {
		this(DEFAULT_AXIS_DATA);
	}

	/**
     * Creates a new <tt>GraphPainter</tt> with data from the specified 
     * <tt>GraphAxisData</tt>.
     *
     * @param data the <tt>GraphAxisData</tt> instance with data for
     *  the graph
	 */
	GraphPainter(GraphAxisData data) {
		AXIS_DATA = data;
		this.addComponentListener(this);
	}

	// implements StatisticDisplayer -- inherit doc comment
	public void registerStatistic(Statistic stat, String displayName) {
		StatHandler handler = 
		    new StatHandler(stat, GRAPH_COLORS[_curColor], displayName);
		LIST.add(handler);
		if(_curColor != (GRAPH_COLORS.length - 1))
			_curColor++;		
		else
			_curColor = 0;
        if(!(stat instanceof NumericalStatistic) &&
           _border == null) {
            _border = new GraphBorder(AXIS_DATA);
            setBorder(_border);
        }
	}

	// implements StatisticDisplayer -- inherit doc comment
	public void registerDualStatistic(Statistic stat0, 
									  Statistic stat1, 
									  String displayName) {
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
	public void setWriteStatsToFile(boolean write) {
		synchronized(LIST) {
			Iterator iter = LIST.iterator();
			while(iter.hasNext()) {
				Statistic curStat = ((StatHandler)iter.next()).getStat();
				curStat.setWriteStatToFile(write);
			}
		}
	}


	private static final int NAME_COLUMN_WIDTH    = 160;
	private static final int CURRENT_COLUMN_WIDTH =  54;
	private static final int AVERAGE_COLUMN_WIDTH =  54;
	private static final int MAX_COLUMN_WIDTH     =  54;
	private static final int TOTAL_COLUMN_WIDTH   =  70;
	private static final int COLOR_COLUMN_WIDTH   =  44;

	private static final int RECT_WIDTH = 
		NAME_COLUMN_WIDTH +
		CURRENT_COLUMN_WIDTH +
		AVERAGE_COLUMN_WIDTH +
		MAX_COLUMN_WIDTH +
		TOTAL_COLUMN_WIDTH +
		COLOR_COLUMN_WIDTH;

	private static final int COLOR_BOX_WIDTH = 10;

	/**
	 * Paints the graph, including the legend.
	 */
	protected void paintComponent(Graphics g) {
        paintGraph(g);
    }

    private void paintGraph(Graphics g) {
		super.paintComponent(g);

		final Color oldColor = g.getColor();
		final Font  oldFont  = g.getFont();
		final Font tempFont  = new Font(oldFont.getName(), 
			Font.PLAIN, oldFont.getSize() - 2);
		g.setFont(tempFont);
		final FontMetrics m = g.getFontMetrics();

		final int rowHeight = m.getHeight() + 1;
		final int rowBase = m.getLeading() / 2 + m.getMaxAscent();

		final int yBoxColor = (rowHeight - COLOR_BOX_WIDTH) / 2;

		final int yOrig    = 0;
		final int xName    = getWidth() - RECT_WIDTH - 10;
		final int xCurrent = xName    + NAME_COLUMN_WIDTH;
		final int xAverage = xCurrent + CURRENT_COLUMN_WIDTH;
		final int xMax     = xAverage + AVERAGE_COLUMN_WIDTH;
		final int xTotal   = xMax     + MAX_COLUMN_WIDTH;
		final int xColor   = xTotal   + TOTAL_COLUMN_WIDTH;
		final int margin   = 3;
		final int xBoxColor = xColor
		                    +(COLOR_COLUMN_WIDTH - COLOR_BOX_WIDTH) / 2;

		int y = yOrig;

		g.drawString(NAME,    xName    + margin, y + rowBase);
		g.drawString(CURRENT, xCurrent + margin, y + rowBase);
		g.drawString(AVERAGE, xAverage + margin, y + rowBase);
		g.drawString(MAX,     xMax     + margin, y + rowBase);
		g.drawString(TOTAL,   xTotal   + margin, y + rowBase);
		g.drawString(COLOR,   xColor   + margin, y + rowBase);

		y += rowHeight;
		g.drawLine(xName, y, xName + RECT_WIDTH, y);
		y++;

		// loop through all of the statistics, displaying their graphs
		// and their legends
		final Iterator iter = LIST.iterator();
		while (iter.hasNext()) {
			final StatHandler handler = (StatHandler)iter.next();
			final Statistic stat = handler.getStat();
			final int[] Y_COORDS = handler.getData();

            // fill up the coords with the correct positions
            final IntBuffer buf = stat.getStatHistory();

			g.setColor(handler.getColor());
            // fill up the coords with the correct positions
            if(!(stat instanceof NumericalStatistic)) {
                synchronized(buf) {
                    for (int j = 0; j < buf.size(); j++) {
                        int yVal = (int)( buf.get(j)
                                          * _yPixelFactor
                                          / AXIS_DATA.getYScale());
                    
                        Y_COORDS[j] = getHeight() - GraphBorder.getOffset()
                            - yVal;
                    }
                }
                g.drawPolyline(X_COORDS, Y_COORDS, Y_COORDS.length);
            }
 			
            String current;
            synchronized(buf) {
                // Set the current value to be the last one in the buffer.
                current =
                    NUMBER_FORMAT.format((double)buf.get(Y_COORDS.length-1)
                                         / AXIS_DATA.getYScale());
            }
			// draw an individual legend entry
			g.fillRect(xBoxColor, y + yBoxColor,
			           COLOR_BOX_WIDTH, COLOR_BOX_WIDTH);
			g.setColor(oldColor);
			g.drawRect(xBoxColor - 1, y + yBoxColor - 1,
			           COLOR_BOX_WIDTH + 1, COLOR_BOX_WIDTH + 1);

			final String name = handler.getDisplayName();
			NUMBER_FORMAT.setMaximumFractionDigits(3);
			final String average =
				NUMBER_FORMAT.format(stat.getAverage());
			final String max =
				NUMBER_FORMAT.format(stat.getMax());
			NUMBER_FORMAT.setMaximumFractionDigits(1);
			final String total =
				NUMBER_FORMAT.format(stat.getTotal());
			g.drawString(name,    xName    + margin, y + rowBase);
            g.drawString(current, xCurrent + margin, y + rowBase);
			g.drawString(average, xAverage + margin, y + rowBase);
			g.drawString(max,     xMax     + margin, y + rowBase);
			g.drawString(total,   xTotal   + margin, y + rowBase);
			y += rowHeight;
		}

		// draw the outside of the legend
		g.drawRect(xName,    yOrig, RECT_WIDTH, y - yOrig);
		g.drawLine(xCurrent, yOrig, xCurrent, y);
		g.drawLine(xAverage, yOrig, xAverage, y);
		g.drawLine(xMax,     yOrig, xMax,     y);
		g.drawLine(xTotal,   yOrig, xTotal,   y);
		g.drawLine(xColor,   yOrig, xColor,   y);

		g.setFont(oldFont);
	}

	// implements the ComponentListener interface
	public void componentResized(ComponentEvent e) {
		double xPixelFactor =
			(double)(getWidth() - GraphBorder.getOffset() - 30) /
			(double)AXIS_DATA.getTotalXValues();
		double curCoord = (double)GraphBorder.getOffset();
		for(int i = 0;
			i < X_COORDS.length; 
			i++, 
			curCoord += xPixelFactor) {
			X_COORDS[i] = (int)curCoord;
		}

		_yPixelFactor =
			(double)(getHeight() - GraphBorder.getOffset() - 60) /
			(double)AXIS_DATA.getTotalYValues();
	}

	// implements the ComponentListener interface
	public void componentShown(ComponentEvent e) {}	

	// implements the ComponentListener interface
	public void componentHidden(ComponentEvent e) {}

	// implements the ComponentListener interface
	public void componentMoved(ComponentEvent e) {}
}
