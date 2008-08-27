package com.limegroup.gnutella.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.SwingConstants;

import com.limegroup.gnutella.util.CommonUtils;

/**
 * Contains the <tt>JDialog</tt> instance that shows "about" information
 * for the application.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class AboutWindow {
	/**
	 * Constant handle to the <tt>JDialog</tt> that contains about
	 * information.
	 */
	private final JDialog DIALOG;

	/**
	 * Constant for the scolling pane of credits.
	 */
	private final ScrollingTextPane SCROLLING_PANE;

	/**
	 * Check box to specify whether to scroll or not.
	 */
	private final JCheckBox SCROLL_CHECK_BOX = 
		new JCheckBox(GUIMediator.getStringResource(
            "ABOUT_SCROLL_CHECK_BOX_LABEL"));

	/**
	 * Constructs the elements of the about window.
	 */
	AboutWindow() {
	    DIALOG = new JDialog(GUIMediator.getAppFrame());
	    
        if (!(CommonUtils.isMacOSX() && CommonUtils.isJava14OrLater()))
            DIALOG.setModal(true);

		DIALOG.setSize(new Dimension(450, 400));            
		DIALOG.setResizable(false);
		DIALOG.setTitle(GUIMediator.getStringResource("ABOUT_TITLE"));
		DIALOG.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		DIALOG.addWindowListener(new WindowAdapter() {
		    public void windowClosed(WindowEvent we) {
		        SCROLLING_PANE.stopScroll();
		    }
		    public void windowClosing(WindowEvent we) {
		        SCROLLING_PANE.stopScroll();
		    }
		});		

        //  set up scrolling pane
        SCROLLING_PANE = createScrollingPane();
        SCROLLING_PANE.addHyperlinkListener(GUIUtils.getHyperlinkListener());

        //  set up limewire version label
        JLabel client = new JLabel(GUIMediator.getStringResource("ABOUT_LABEL_START") +
                " " + CommonUtils.getLimeWireVersion());
        client.setHorizontalAlignment(SwingConstants.CENTER);
        
        //  set up java version label
        JLabel java = new JLabel("Java " + CommonUtils.getJavaVersion());
        java.setHorizontalAlignment(SwingConstants.CENTER);
        
        //  set up limewire.com label
        JLabel url = new URLLabel("http://www.limewire.com");
        url.setHorizontalAlignment(SwingConstants.CENTER);

        //  set up scroll check box
		SCROLL_CHECK_BOX.setSelected(true);
		SCROLL_CHECK_BOX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (SCROLL_CHECK_BOX.isSelected())
					SCROLLING_PANE.startScroll();
				else
					SCROLLING_PANE.stopScroll();
			}
		});

        //  set up close button
        JButton button = new JButton(GUIMediator.getStringResource("GENERAL_CLOSE_BUTTON_LABEL"));
        DIALOG.getRootPane().setDefaultButton(button);
        button.setToolTipText(GUIMediator.getStringResource("ABOUT_BUTTON_TIP"));
        button.addActionListener(GUIUtils.getDisposeAction());

        //  layout window
		JComponent pane = (JComponent)DIALOG.getContentPane();
		GUIUtils.addHideAction(pane);
		
		pane.setLayout(new GridBagLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(GUIConstants.SEPARATOR,
                GUIConstants.SEPARATOR, GUIConstants.SEPARATOR, GUIConstants.SEPARATOR));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1;
		gbc.insets = new Insets(0,0,0,0);
        gbc.gridwidth = 2;
		gbc.gridy = 0;
        
		LogoPanel logo = new LogoPanel();
		logo.setSearching(true);
		pane.add(logo, gbc);

        gbc.gridy = 1;
        pane.add(Box.createVerticalStrut(GUIConstants.SEPARATOR), gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 2;
        pane.add(client, gbc);

        gbc.gridy = 3;
		pane.add(java, gbc);
        
        gbc.gridy = 4;
		pane.add(url, gbc);
		
        gbc.gridy = 5;
        pane.add(Box.createVerticalStrut(GUIConstants.SEPARATOR), gbc);

		gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 6;
		pane.add(SCROLLING_PANE, gbc);

        gbc.gridy = 7;
		gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        pane.add(Box.createVerticalStrut(GUIConstants.SEPARATOR), gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy = 8;
		pane.add(SCROLL_CHECK_BOX, gbc);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		pane.add(button, gbc);
		
	}

	private ScrollingTextPane createScrollingPane() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");

        Color color = new JLabel().getForeground();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String hex = toHex(r) + toHex(g) + toHex(b);
        sb.append("<body text='#" + hex + "'>");

        //  introduction
        sb.append(GUIMediator.getStringResource("ABOUT_INTRODUCTION"));
        sb.append("<br><br>");
        
        //  developers
        sb.append(GUIMediator.getStringResource("ABOUT_DEV_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Greg Bildson</li>\n" + 
                "  <li>Sam Berlin</li>\n" + 
                "  <li>Zlatin Balevsky</li>\n" + 
                "  <li>Justin Schmidt</li>\n" + 
                "  <li>Dave Nicponski</li>\n" + 
                "  <li>Karl Magdsick</li>\n" + 
                "  <li>Tim Olsen</li>\n" + 
                "  <li>Felix Berger</li>\n" + 
                "</ul>");
        
        //  business developers
        sb.append(GUIMediator.getStringResource("ABOUT_BIZDEV_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Meghan Formel</li>\n" + 
                "  <li>Kathryn Catillaz</li>\n" + 
                "  <li>Rachel Sterne</li>\n" + 
                "</ul>");
        
        //  web developers
        sb.append(GUIMediator.getStringResource("ABOUT_WEBDEV_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Angel Leon</li>\n" + 
                "  <li>Aubrey Arago</li>\n" + 
                "  <li>Justin Schmidt</li>\n" + 
                "</ul>");
        
        //  support staff
        sb.append(GUIMediator.getStringResource("ABOUT_SUPPORT_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Zenzele Bell</li>\n" + 
                "  <li>Christine Nicponski</li>\n" + 
                "  <li>Kirk Kahn</li>\n" + 
                "</ul>");
        
        //  previous developers
        sb.append(GUIMediator.getStringResource("ABOUT_PREVIOUS_DEV_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Susheel Daswani</li>\n" +
                "  <li>Adam Fisk</li>\n" +
                "  <li>Tarun Kapoor</li>\n" +
                "  <li>Yusuke Naito</li>\n" +
                "  <li>Christopher Rohrs</li>\n" +
                "  <li>Anurag Singla</li>\n" +
                "  <li>Robert Soule</li>\n" +
                "  <li>Sumeet Thadani</li>\n" +
                "  <li>Ron Vogl</li>\n" +
                "</ul>");

        //  open source contributors
        sb.append(GUIMediator.getStringResource("ABOUT_CONTRIBUTORS_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Richie Bielak</li>\n" +
                "  <li>Jerry Charumilind</li>\n" +
                "  <li>Marvin Chase</li>\n" +
                "  <li>Robert Collins</li>\n" +
                "  <li>Kenneth Corbin</li>\n" +
                "  <li>David Graff</li>\n" +
                "  <li>Andy Hedges</li>\n" +
                "  <li>Michael Hirsch</li>\n" +
                "  <li>Roger Kapsi</li>\n" +
                "  <li>Jens-Uwe Mager</li>\n" +
                "  <li>Gordon Mohr</li>\n" +
                "  <li>Chance Moore</li>\n" +
                "  <li>Rick T. Piazza</li>\n" +
                "  <li>Eugene Romanenko</li>\n" +
                "  <li>Gregorio Roper</li>\n" +
                "  <li>William Rucklidge</li>\n" +
                "  <li>Eric Seidel</li>\n" +
                "  <li>Philippe Verdy</li>\n" +
                "  <li>Stephan Weber</li>\n" +
                "  <li>Jason Winzenried</li>\n" +
                "</ul>");
         
        //  internationalization contributors
        sb.append(GUIMediator.getStringResource("ABOUT_I18N_BLURB"));
        sb.append("<br><br>");
        
        //  community VIPs
        sb.append(GUIMediator.getStringResource("ABOUT_COMMUNITY_BLURB"));
        sb.append("<ul>\n" + 
                "  <li>Vincent Falco -- Free Peers, Inc.</li>\n" + 
                "  <li>Gordon Mohr -- Bitzi, Inc.</li>\n" + 
                "  <li>John Marshall -- Gnucleus</li>\n" +
                "  <li>Jason Thomas -- Swapper</li>\n" +
                "  <li>Brander Lien -- ToadNode</li>\n" +
                "  <li>Angelo Sotira -- www.gnutella.com</li>\n" +
                "  <li>Marc Molinaro -- www.gnutelliums.com</li>\n" +
                "  <li>Simon Bellwood -- www.gnutella.co.uk</li>\n" +
                "  <li>Serguei Osokine</li>\n" +
                "  <li>Justin Chapweske</li>\n" +
                "  <li>Mike Green</li>\n" +
                "  <li>Raphael Manfredi</li>\n" +
                "  <li>Tor Klingberg</li>\n" +
                "  <li>Mickael Prinkey</li>\n" +
                "  <li>Sean Ediger</li>\n" +
                "  <li>Kath Whittle</li>\n" +
                "</ul>");
        
        //  conclusion
        sb.append(GUIMediator.getStringResource("ABOUT_CONCLUSION"));
        sb.append("</body></html>");
        
        return new ScrollingTextPane(sb.toString());
    }

    /**
     * Returns the int as a hex string.
     */
    private String toHex(int i) {
        String hex = Integer.toHexString(i).toUpperCase();
        if(hex.length() == 1)
            return "0" + hex;
        else
            return hex;
    }
    
    /**
	 * Displays the "About" dialog window to the user.
	 */
	void showDialog() {
		if (GUIMediator.isAppVisible())
			DIALOG.setLocationRelativeTo(GUIMediator.getAppFrame());
		else
			DIALOG.setLocation(GUIMediator.getScreenCenterPoint(DIALOG));

		if (SCROLL_CHECK_BOX.isSelected()) {
			ActionListener startTimerListener = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
				    //need to check isSelected() again,
				    //it might have changed in the past 10 seconds.
				    if (SCROLL_CHECK_BOX.isSelected()) {
				        //activate scroll timer
					    SCROLLING_PANE.startScroll();
					}
				}
			};
			
			Timer startTimer = new Timer(10000, startTimerListener);
			startTimer.setRepeats(false);			
			startTimer.start();
		}
		DIALOG.setVisible(true);
	}
}
