package com.limegroup.gnutella.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** 
 * This class generates a MagnetMix button to link out to website.
 */
public final class MagnetButton extends JPanel {

    private ImageIcon header;
    private JButton   bheader;
    private ActionListener magnetButtonListener;
    private MouseAdapter   magnetMouseListener;

    public MagnetButton() {
        header   = GUIMediator.getThemeImage("mm_header");
        bheader  = new JButton(header);
        bheader.setOpaque(false);

        magnetButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIMediator.openURL("http://www.MagnetMix.com"); 
            }
        };
        bheader.addActionListener(magnetButtonListener);

        magnetMouseListener = new MouseAdapter() {
            //simulate active cursor, we could choose another cursor though
            public void mouseEntered(MouseEvent e) { 
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            //go back to normal
            public void mouseExited(MouseEvent e) {
                e.getComponent().setCursor(Cursor.getDefaultCursor()); 
            }           
        };
        bheader.addMouseListener( magnetMouseListener );

        zeroInsets(this);
        adjustSizes(bheader, 176, 23);
        add(bheader);
    } 

    private void adjustSizes(JComponent jc, int width, int height) {
        zeroInsets(jc);
        setSizes((JButton)jc, width, height);
    }

    private static void setSizes(JButton b, int width, int height) {
        Dimension d = new Dimension(width,height);
        b.setMaximumSize(d);
        b.setMinimumSize(d);
        b.setPreferredSize(d);
    }

    private static void zeroInsets(JComponent jc) {
        Insets    insets   = jc.getInsets();
        insets.left   = 0;
        insets.right  = 0;
        insets.top    = 0;
        insets.bottom = 0;
    }
}
