package com.limegroup.gnutella.gui.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;

/**
 * This class handles rendering a <tt>JProgressBar</tt> for improved
 * performance in tables.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class ProgressBarRenderer extends JProgressBar
	implements TableCellRenderer, ThemeObserver {

	private Border _selectedBorder;
	private Border _unselectedBorder;
    private Map /* Color -> Border */ borders = new HashMap();
    
	/**
	 * Sets the font, border, and colors for the progress bar.
	 *
	 * @param table the <tt>JTable</tt> instance used to obtain the colors
	 * to use for rendering
	 */
	public ProgressBarRenderer() {
	    ThemeMediator.addThemeObserver(this);
		setStringPainted(true);

		Font font = getFont();
		Font newFont;

		if(font == null || font.getName() == null) {
			newFont = new Font("Dialog", Font.BOLD, 9);
		}
		else {
			newFont = new Font(font.getName(), Font.BOLD, 9);
		}

		setFont(newFont);
	}
	
    /**
     * Overrides <tt>JComponent.setForeground</tt> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c) {
        super.setBackground(c);
        _unselectedBorder = getCachedOrNewBorder(c);
        if( _unselectedBorder != null) setBorder(_unselectedBorder);
    }
    
    public void updateTheme() {
        _selectedBorder = null;
        _unselectedBorder = null;
        borders.clear();
    }

	/**
     * Gets a new or old border for this color.
     */
    public Border getCachedOrNewBorder(Color c) {
        if( c == null ) return null;
        if(borders == null) return null;
        
        Border b = (Border)borders.get(c);
        if( b == null ) {
            b = BorderFactory.createMatteBorder(2,5,2,5,c);
            borders.put(c, b);
        }
        return b;
    }

	public Component getTableCellRendererComponent
		(JTable table,Object value,boolean isSel,
		 boolean hasFocus,int row,int column) {
		int v = (value == null ? 0 : ((Integer)value).intValue());
		String s = Integer.toString(v) + "%";
		    
		setValue(v);
		setString(s);

        if ( _selectedBorder == null && _unselectedBorder == null ) {
    		Color sc = table.getSelectionBackground();
    		Color uc = ((LimeJTable)table).getBackgroundForRow(row);
    		_selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,sc);
    		_unselectedBorder = getCachedOrNewBorder(uc);
        }

		if(isSel) {
			setBorder(_selectedBorder);
			setBackground( table.getSelectionBackground() );
		} else
			setBorder(_unselectedBorder);
		return this;
	}
    
    /*
     * The following methods are overridden as a performance measure to 
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and 
     * drawbacks of overriding methods like these.
     */

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public boolean isOpaque() { 
	Color back = getBackground();
	Component p = getParent(); 
	if (p != null) { 
	    p = p.getParent(); 
	}
	JComponent jp = (JComponent)p;
	// p should now be the JTable. 
	boolean colorMatch = (back != null) && (p != null) && 
	    back.equals(p.getBackground()) && 
			jp.isOpaque();
	return !colorMatch && super.isOpaque(); 
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void validate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void revalidate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(long tm, int x, int y, int width, int height) {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(Rectangle r) { }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {	
	// Strings get interned...
	if (propertyName=="text") {
	    super.firePropertyChange(propertyName, oldValue, newValue);
	}
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { }		

}
