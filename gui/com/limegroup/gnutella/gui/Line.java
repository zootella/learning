package com.limegroup.gnutella.gui;

import java.awt.Dimension;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;

/**
 * A component that draws a line.
 */
public class Line extends JComponent {
    
    private Color color;
    
    public Line(Color color) {
        this.color = color;
        setPreferredSize(new Dimension(1, 1));
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(oldColor);
    }
}