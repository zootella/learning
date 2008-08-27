package com.limegroup.gnutella.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.IOException;

import com.limegroup.gnutella.version.Version;
import com.limegroup.gnutella.version.VersionFormatException;
import com.limegroup.gnutella.util.Launcher;

class Java14Notice extends JDialog {
    
    private static final String REQUIRED = "1.4.1";
    private static final String CURRENT = System.getProperty("java.version");
    private static final String URL = "http://www.limewire.com/whyupgradejava";
    
    
    public static void showIfNecessary() {
        try {
            Version rq = new Version(REQUIRED);
            Version cr = new Version(CURRENT);
            if(cr.compareTo(rq) < 0)
                new Java14Notice().show();
        } catch(VersionFormatException ignored) {}
    }
    
    private Java14Notice() {
        construct();
    }
    
    private void construct() {
        setTitle("Upgrade Java");
        setSize(new Dimension(100, 300));
        setModal(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        MultiLineLabel label = new MultiLineLabel(
            "LimeWire requires Java " + REQUIRED + " or higher in order to run. " +
            "You are currently running an out-of-date version of Java.  " +
            "Please visit " + URL + " in order to upgrade your version of Java.\n\n" +
            "Current Java Version : " + CURRENT + "\n" +
            "Required Java Version: " + REQUIRED + "\n\n", 400);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.add(label);

        JPanel buttonPanel = new JPanel();
        JButton now = new JButton("Upgrade Java");
        now.setToolTipText("Visit " + URL);
        now.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    try {
                    loadWindowsLibrary();
			        Launcher.openURL(URL);
                } catch(UnsatisfiedLinkError ule) {
                    openURLFailed();
                } catch(IOException iox) {
                    openURLFailed();
                }
				System.exit(1);
			}
		});

        JButton later = new JButton("Upgrade Later");
        later.setToolTipText("Visit " + URL + " Later");
        later.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    System.exit(1);
			}
		});
		
        buttonPanel.add(now);
        buttonPanel.add(later);

        mainPanel.add(labelPanel);
        mainPanel.add(buttonPanel);

        getContentPane().add(mainPanel);
		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = getSize();
		setLocation((screenSize.width - dialogSize.width)/2,
						   (screenSize.height - dialogSize.height)/2);
    }
    
    private void loadWindowsLibrary() throws UnsatisfiedLinkError {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.indexOf("windows") != -1)
            System.loadLibrary("LimeWire20");
    }
    
    private void openURLFailed() {
        JOptionPane.showMessageDialog(this, 
          "To update, please direct your web-browser to " + URL + ".",
		  "Unable to open browser",
		  JOptionPane.ERROR_MESSAGE);
    }
}