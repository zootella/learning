package com.limegroup.gnutella.gui;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.limegroup.gnutella.util.CommonUtils;

class ShutdownWindow extends JDialog {
    
    public ShutdownWindow() {
        super(GUIMediator.getAppFrame());
        setResizable(false);
        setTitle(GUIMediator.getStringResource("SHUTDOWN_TITLE"));
        setLocation(GUIMediator.getScreenCenterPoint(this));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JComponent pane = (JComponent)getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(GUIMediator.getStringResource("SHUTDOWN_WAIT"));
        pane.add(label, c);
        
        if(CommonUtils.isJava14OrLater()) {
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setStringPainted(false);
            c.insets = new Insets(3, 3, 3, 3);
            c.anchor = GridBagConstraints.CENTER;
            pane.add(bar, c);
        }
        
        ((JComponent)getContentPane()).setPreferredSize(new Dimension(250, 80));
        pack();
    }
}