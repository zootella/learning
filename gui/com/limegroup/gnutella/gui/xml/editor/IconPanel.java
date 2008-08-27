package com.limegroup.gnutella.gui.xml.editor;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JPanel;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.gui.IconManager;

public class IconPanel extends JPanel {
    
    private Icon icon;
    
    public IconPanel() {
    }
    
    public void initWithFileDesc(FileDesc fd) {
        IconManager iconManager = IconManager.instance();
        icon = iconManager.getIconForFile(fd.getFile());
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        if (icon != null) {
            int x = (int)(getSize().width - icon.getIconWidth())/2;
            int y = (int)(getSize().height - icon.getIconHeight())/2;
            
            icon.paintIcon(this, g, x, y);
        }
    }
}
