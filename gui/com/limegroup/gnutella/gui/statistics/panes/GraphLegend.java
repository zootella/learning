package com.limegroup.gnutella.gui.statistics.panes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.limegroup.gnutella.gui.BoxPanel;

/**
 * The legend displayed for a given graph.
 */
final class GraphLegend {
	
	private final JPanel PANEL = new BoxPanel();
	private final List LIST = new LinkedList();
	private int _curHeight = 20;

	GraphLegend() {
		Dimension size = new Dimension(150, _curHeight);
		PANEL.setPreferredSize(size);
		PANEL.setMaximumSize(size);
		PANEL.setBorder(new LineBorder(Color.black));
		PANEL.add(new LegendPainter());
	}

	public void add(StatHandler handler) {
		_curHeight += 20;
		Dimension size = new Dimension(150, _curHeight);
		PANEL.setPreferredSize(size);
		PANEL.setMaximumSize(size);
		PANEL.revalidate();
		LIST.add(handler);
	}

	JComponent getComponent() {
		return PANEL;
	}

	private class LegendPainter extends JComponent {

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int dim = 15;
			Iterator iter = LIST.iterator();
			for(int i=0, x=6, y=12; iter.hasNext(); i++, y+=20) {
				StatHandler handler = (StatHandler)iter.next();
				String name = handler.getDisplayName();
				Color oldColor = g.getColor();
				g.drawRect(120, y-1, 21, dim+1);
				g.setColor(handler.getColor());
				g.fillRect(121, y, 20, dim);
				g.setColor(oldColor);
				g.drawString(name, x, y+dim-1);
			}
		}
	}
}
