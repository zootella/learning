
package com.limegroup.gnutella.gui.tables;

/**
 * Abstract dataline class that
 * implements DataLine functions
 * that may not be absolutely necessary
 * in all DataLine instances
 */
public abstract class AbstractDataLine implements DataLine {

    /**
     * The object that initialized the dataline.
     */
    protected Object initializer;

    /**
     * @implements DataLine interface
     */
    public void initialize(Object o) {
        initializer = o;
    }

    /**
     * @implements DataLine interface
     */
    public Object getInitializeObject() { return initializer; }

    /**
     * @implements DataLine interface
     */
    public void setInitializeObject(Object o) { initializer = o; }

    /**
     * A blank implementation of setValueAt, because it is not necessary.
     * @implements DataLine interface
     */
    public void setValueAt(Object o, int col) { ; }

    /**
     * A blank implementatino of cleanup, because it is not necessary.
     * @implements DataLine interface
     */
    public void cleanup() { ; }

    /**
     * A blank implementation of update, because it is not necessary.
     * @implements DataLine interface
     */
    public void update() { ; }

    /**
     * By default, DataLines will have no tooltip.
     */
    public String[] getToolTipArray(int col) { return null; }
}