
package com.limegroup.gnutella.gui;

import javax.swing.AbstractListModel;

import com.limegroup.gnutella.util.Buffer;

/**
 *  Use the Buffer class to efficiently deal with adding to a 
 *  fixed sized ListModel.
 *
 * @author Greg Bildson
 */
public class BufferListModel extends AbstractListModel
{

    private Buffer              buffer;

	/**
	 *  Create list model with size capacity
	 */
    public BufferListModel(int size) 
    {
        buffer = new Buffer(size);
    }

	/**
	 *  Implement the default value getter for ListModel
	 */
    public Object getElementAt(int idx)
    {
        return buffer.get(idx);
    }

	/**
	 *  Implement the default size return for ListModel
	 */
    public int getSize()
    {
		if ( buffer == null )
			return 0;
        return buffer.getSize();
    }

	/**
	 *  Change the size of the fixed list while maintaining the content
	 */
    public void changeSize(int size)
    {
		if ( size == 0 )
		{
            int oldSize = 0;
			if ( buffer != null )
			    oldSize = buffer.getSize();
			buffer = null;
            fireContentsChanged(this, 0, oldSize);
			return;
		}

        Buffer nbuffer = new Buffer(size);
        for ( int i = 0; buffer != null && 
		      i < Math.min(buffer.getSize(), size); i++ )
        {
            nbuffer.addFirst(buffer.get(i));
        }
        buffer = nbuffer;
        //fireContentsChanged(this, 0, Math.max(buffer.getSize(),size));
    }

	/**
	 *  Clear the list
	 */
    public void removeAllElements()
    {
		if ( buffer == null )
			return;

        buffer.clear();
        fireContentsChanged(this, 0, buffer.getCapacity());
    }

	/**
	 *  Add to the top of the fixed-size list
	 */
    public void addFirst(Object val)
    {
		if ( buffer == null )
			return;

        buffer.addFirst(val);
        fireContentsChanged(this, 0, buffer.getSize());
    }
}


