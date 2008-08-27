package com.limegroup.gnutella.gui.statistics.panes;

import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.statistics.Statistic;

/**
 * This class encapsulates data for a given set of graph x and y axis.
 *
 * @see GraphBorder
 */
public final class GraphAxisData {

	/**
	 * Bytes per kilobyte for conversion convenience.
	 */
	private static final int BYTES_PER_KILOBYTE = 1024;

	/**
	 * Constant for the number of values to display along the x axis
	 * between tick mark labels..
	 */	
	private final int X_INCREMENT;

	/**
	 * The number of pixels to use per value along the x axis.
	 */
	private final int X_PIXEL_VALUE;

	/**
	 * Constant for the number of pixels per y value to display when
	 * we're an Ultrapeer.
	 */
	private final double ULTRAPEER_Y_PIXELS;

	/**
	 * Constant for the number of pixels per y value to display when
	 * we're a leaf.
	 */
	private final double LEAF_Y_PIXELS;

	/**
	 * Constant for the number of values to display along the y axis
	 * between tick mark labels when we're an Ultrapeer.
	 */
	private final int ULTRAPEER_Y_INCREMENT;

	/**
	 * Constant for the number of values to display along the y axis
	 * between tick mark labels when we're a leaf.
	 */	
	private final int LEAF_Y_INCREMENT;

	/**
	 * Constant for the total number of values to display along
	 * the y axis when an Ultrapeer.
	 */
	private final int ULTRAPEER_TOTAL_Y_VALUES;

	/**
	 * Constant for the total number of values to display along
	 * the y axis when a leaf.
	 */
	private final int LEAF_TOTAL_Y_VALUES;

	/**
	 * The number to divide y values by before display (for example, to 
	 * convert to kilobytes).
	 */
	private final int Y_SCALE;

	/**
	 * The label for the x axis.
	 */
	private final String X_AXIS_LABEL;

	/**
	 * The label for the y axis.
	 */
	private final String Y_AXIS_LABEL;

	/**
	 * The default number of pixels to use for each x value.
	 */
	private static int DEFAULT_X_PIXELS = 2;

	/**
	 * The default number of values to include between tick marks.	 
	 */
	private static int DEFAULT_X_INCREMENT = 20;
	

	////// PIXELS PER VALUE ALONG THE Y AXIS //////

	/**
	 * Constant for the default number of pixels per y value to display 
	 * when we're an Ultrapeer.
	 */
	private static final double DEFAULT_ULTRAPEER_Y_PIXELS = 0.35;

	/**
	 * Constant for the default number of pixels per y value to display 
	 * when we're a leaf.
	 */
	private static final double DEFAULT_LEAF_Y_PIXELS = 3.0;

	/**
	 * Constant for the default number of pixels per y value to display 
	 * when we're an Ultrapeer and displaying a graph with kilobytes.
	 */
	private static final double DEFAULT_ULTRAPEER_Y_KILOBYTE_PIXELS = 2.00;

	/**
	 * Constant for the default number of pixels per y value to display 
	 * when we're a leaf and displaying a graph with bytes.
	 */
	private static final double DEFAULT_LEAF_Y_KILOBYTE_PIXELS = 3;


	////// VALUES BETWEEN TICK MARKS ON THE Y AXIS //////

	/**
	 * Constant for the default number of values to display along the y 
	 * axis between tick mark labels when we're an Ultrapeer.
	 */
	private static final int DEFAULT_ULTRAPEER_Y_INCREMENT = 100;

	/**
	 * Constant for the default number of values to display along the y 
	 * axis between tick mark labels when we're a leaf.
	 */	
	private static final int DEFAULT_LEAF_Y_INCREMENT = 10;

	/**
	 * Constant for the default number of values to display along the y 
	 * axis between tick mark labels when we're an Ultrapeer and displaying
	 * a graph with kilobytees.
	 */
	private static final int DEFAULT_ULTRAPEER_Y_KILOBYTE_INCREMENT = 10;

	/**
	 * Constant for the default number of values to display along the y 
	 * axis between tick mark labels when we're a leaf and displaying
	 * a graph with bytes.
	 */	
	private static final int DEFAULT_LEAF_Y_KILOBYTE_INCREMENT = 5;


	////// TOTAL Y AXIS VALUES //////

	/**
	 * Constant for the default total number of values to display along
	 * the y axis when an Ultrapeer.
	 */
	private static final int DEFAULT_ULTRAPEER_TOTAL_Y_VALUES = 500;

	/**
	 * Constant for the default total number of values to display along
	 * the y axis when a leaf.
	 */
	private static final int DEFAULT_LEAF_TOTAL_Y_VALUES = 60;

	/**
	 * Constant for the default total number of values to display along
	 * the y axis when an Ultrapeer and displaying a graph with kilobytes.
	 */
	private static final int DEFAULT_ULTRAPEER_TOTAL_Y_KILOBYTE_VALUES = 60;

	/**
	 * Constant for the default total number of values to display along
	 * the y axis when a leaf and displaying a graph with kilobytes.
	 */
	private static final int DEFAULT_LEAF_TOTAL_Y_KILOBYTE_VALUES = 20;

	/**
	 * Constructs a new <tt>GraphAxisData</tt> instance with default 
	 * settings.
	 */
	GraphAxisData() {
		this(DEFAULT_ULTRAPEER_Y_PIXELS, 
			 DEFAULT_LEAF_Y_PIXELS, 
			 DEFAULT_ULTRAPEER_Y_INCREMENT, 
			 DEFAULT_LEAF_Y_INCREMENT, 
			 DEFAULT_ULTRAPEER_TOTAL_Y_VALUES, 
			 DEFAULT_LEAF_TOTAL_Y_VALUES);
	}

	/**
	 * Specialized constructor that uses the default x values with customized
	 * values for the y axis of the graph.
	 *
	 * @param ultrapeerYPixels the total number of pixels to allocate per 
	 *  value on the y axis when in Ultrapper mode
	 * @param leafYPixels the total number of pixels to allocate per 
	 *  value on the y axis when in leaf mode
	 * @param ultrapeerYIncrement the value increment between tick mark
	 *  labels as an Ultrapeer
	 * @param leafYIncrement the value increment between tick mark
	 *  labels as a leaf
	 * @param ultrapeerTotalYValues the total number of y values to display
	 *  when in Ultrapeer mode
	 * @param leafTotalYValues the total number of y values to display
	 *  when in leaf mode
	 */
	GraphAxisData(double ultrapeerYPixels,
				  double leafYPixels, 
				  int ultrapeerYIncrement,
				  int leafYIncrement, 
				  int ultrapeerTotalYValues,
				  int leafTotalYValues) {
		this(ultrapeerYPixels, leafYPixels, 
			 ultrapeerYIncrement, leafYIncrement,
			 ultrapeerTotalYValues, leafTotalYValues,
			 1,
			 GUIMediator.getStringResource("DEFAULT_X_AXIS_LABEL"),
			 GUIMediator.getStringResource("DEFAULT_Y_AXIS_LABEL"));
	}

	/**
	 * Specialized constructor that uses the default x values with customized
	 * values for the y axis of the graph.
	 *
	 * @param ultrapeerYPixels the total number of pixels to allocate per 
	 *  value on the y axis when in Ultrapper mode
	 * @param leafYPixels the total number of pixels to allocate per 
	 *  value on the y axis when in leaf mode
	 * @param ultrapeerYIncrement the value increment between tick mark
	 *  labels as an Ultrapeer
	 * @param leafYIncrement the value increment between tick mark
	 *  labels as a leaf
	 * @param ultrapeerTotalYValues the total number of y values to display
	 *  when in Ultrapeer mode
	 * @param leafTotalYValues the total number of y values to display
	 *  when in leaf mode
	 * @param xLabel the x-axis label
	 * @param yLabel the y-axis label
	 */
	GraphAxisData(double ultrapeerYPixels,
				  double leafYPixels, 
				  int ultrapeerYIncrement,
				  int leafYIncrement, 
				  int ultrapeerTotalYValues,
				  int leafTotalYValues,
				  int yScale,
				  String xLabel,
				  String yLabel) {
		this(DEFAULT_X_PIXELS, ultrapeerYPixels, leafYPixels, 
			 DEFAULT_X_INCREMENT, ultrapeerYIncrement, leafYIncrement,
			 ultrapeerTotalYValues, leafTotalYValues, yScale,
			 xLabel, yLabel);
	}

	/**
	 * Creates a <tt>GraphAxisData</tt> instance for a bandwidth graph
	 * of kilobytes/second for Gnutella message data.
	 *
	 * @return a new <tt>GraphAxisData</tt> instance for kilobyte/second
	 *  Gnutella message data
	 */
	static GraphAxisData createKilobyteGraphData() {
		return new GraphAxisData
			(DEFAULT_ULTRAPEER_Y_KILOBYTE_PIXELS,
			 DEFAULT_LEAF_Y_KILOBYTE_PIXELS,
			 DEFAULT_ULTRAPEER_Y_KILOBYTE_INCREMENT,
			 DEFAULT_LEAF_Y_KILOBYTE_INCREMENT,
			 DEFAULT_ULTRAPEER_TOTAL_Y_KILOBYTE_VALUES,
			 DEFAULT_LEAF_TOTAL_Y_KILOBYTE_VALUES,
			 BYTES_PER_KILOBYTE,
			 GUIMediator.getStringResource("DEFAULT_X_AXIS_LABEL"),
			 GUIMediator.getStringResource("Y_AXIS_BANDWIDTH_LABEL"));
	}

	/**
	 * Creates a <tt>GraphAxisData</tt> instance for a bandwidth graph
	 * of kilobytes/second for overall bandwidth data.
	 *
	 * @return a new <tt>GraphAxisData</tt> instance for kilobyte/second
	 *  overall bandwidth data
	 */
	static GraphAxisData createBandwidthGraphData() {
		return new GraphAxisData
            (0.60, 0.60, 60, 60, 200, 200, 
			 BYTES_PER_KILOBYTE,
			 GUIMediator.getStringResource("DEFAULT_X_AXIS_LABEL"),
			 GUIMediator.getStringResource("Y_AXIS_BANDWIDTH_LABEL"));
	}


	/**
	 * Constructor allowing all x any y values to be customized.
	 *
	 * @param xPixelValue the number of pixels to allocate per value on 
	 *  the y axis 
	 * @param ultrapeerYPixels the number of pixels to allocate per 
	 *  value on the y axis when in Ultrapper mode
	 * @param leafYPixels the total number of pixels to allocate per 
	 *  value on the y axis when in leaf mode
	 * @param xIncrement the value increment between tick mark labels
	 *  on the x axis
	 * @param ultrapeerYIncrement the value increment between tick mark
	 *  labels on the y axis as an Ultrapeer
	 * @param leafYIncrement the value increment between tick mark
	 *  labels on the y axis as a leaf
	 * @param ultrapeerTotalYValues the total number of y values to display
	 *  when in Ultrapeer mode
	 * @param leafTotalYValues the total number of y values to display
	 *  when in leaf mode
	 * @param yScale the amount to scale values by before display
	 * @param xAxisLabel the label for the x axis
	 * @param yAxisLabel the label for the y axis
	 */
	private GraphAxisData(int xPixelValue, 
						  double ultrapeerYPixels,
						  double leafYPixels,
						  int xIncrement,
						  int ultrapeerYIncrement,
						  int leafYIncrement,
						  int ultrapeerTotalYValues,
						  int leafTotalYValues,
						  int yScale,
						  String xAxisLabel,
						  String yAxisLabel) {

		X_PIXEL_VALUE = xPixelValue;
		ULTRAPEER_Y_PIXELS = ultrapeerYPixels;
		LEAF_Y_PIXELS = leafYPixels;
		ULTRAPEER_Y_INCREMENT = ultrapeerYIncrement;
		LEAF_Y_INCREMENT = leafYIncrement;
		X_INCREMENT = xIncrement;
		ULTRAPEER_TOTAL_Y_VALUES = ultrapeerTotalYValues;
		LEAF_TOTAL_Y_VALUES = leafTotalYValues;
		Y_SCALE = yScale;
		X_AXIS_LABEL = xAxisLabel;
		Y_AXIS_LABEL = yAxisLabel;
	}

	/**
	 * Accessor for the number of pixels to use per x value.
	 *
	 * @return the number of pixels to use per x value
	 */
	public int getXPixelValue() {
        return X_PIXEL_VALUE;
	}

	/**
	 * Accessor for the number of pixels to use per y value.
	 *
	 * @return the number of pixels to use per y value
	 */
	public double getYPixelValue() {
		if(RouterService.isSupernode()) {
			return ULTRAPEER_Y_PIXELS;
        }
		return LEAF_Y_PIXELS;
	}

	/**
	 * Returns the scale for the y axis.  The stored value is divided by
	 * this number before it is displayed.
	 *
	 * @return the number to divide the raw data by before display
	 */
	public int getYScale() {
		return Y_SCALE;
	}

	/**
	 * Accessor for the number of values to include between
	 * tick marks on the x axis.
	 *
	 * @return the number of values to include between
	 *  tick marks on the x axis
	 */
	public int getXIncrement() {
		return X_INCREMENT;
	}

	/**
	 * Accessor for the number of values to include between
	 * tick marks on the y axis.
	 *
	 * @return the number of values to include between
	 *  tick marks on the y axis
	 */
	public int getYIncrement() {
		if(RouterService.isSupernode())
			return ULTRAPEER_Y_INCREMENT;
		return LEAF_Y_INCREMENT;
	}

	/**
	 * Accessor for the total number of x values to display on the x 
	 * axis.
	 *
	 * @return the total number of x values to display on the x 
	 *  axis
	 */	 
	public int getTotalXValues() {
		return Statistic.HISTORY_LENGTH;
	}

	/**
	 * Accessor for the total number of y values to display on the y 
	 * axis.
	 *
	 * @return the total number of y values to display on the y 
	 *  axis
	 */	 
	public int getTotalYValues() {
		if(RouterService.isSupernode())
			return ULTRAPEER_TOTAL_Y_VALUES;
		return LEAF_TOTAL_Y_VALUES;
	}

	/**
	 * Accessor the x axis label.
	 *
	 * @return the x axis label
	 */
	public String getXAxisLabel() {
		return X_AXIS_LABEL;
	}

	/**
	 * Accessor the y axis label.
	 *
	 * @return the y axis label
	 */
	public String getYAxisLabel() {
		return Y_AXIS_LABEL;
	}

	
}
