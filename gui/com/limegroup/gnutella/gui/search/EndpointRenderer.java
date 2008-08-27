package com.limegroup.gnutella.gui.search;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.limegroup.gnutella.gui.themes.ThemeMediator;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;

/** Draws EndpointHolder's appropriately colorized */
class EndpointRenderer extends DefaultTableCellRenderer 
                                                     implements ThemeObserver {

	private static Color _nonPrivateColor;

	private static Color _privateColor;


    public EndpointRenderer() {
        updateTheme();
        ThemeMediator.addThemeObserver(this);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, 
                                                   boolean isSel, 
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        EndpointHolder e = (EndpointHolder)value;
        
        Component ret = super.getTableCellRendererComponent(
            table, e, isSel, hasFocus, row, column);
        //Render private IP addresses in red, others in black.  The second call
        //is necessary to prevent everything from turning red, since one
        //renderer is shared among all cells.
        if (e.isPrivateAddress())
            ret.setForeground(_privateColor);
        else
            ret.setForeground(_nonPrivateColor);

        return ret;
    }

    public void updateTheme() {
        _nonPrivateColor = ThemeFileHandler.WINDOW8_COLOR.getValue();
        _privateColor = ThemeFileHandler.SEARCH_PRIVATE_IP_COLOR.getValue();
    }
}
