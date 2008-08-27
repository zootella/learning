package com.limegroup.gnutella.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * Window that displays the splash screen.  This loads the splash screen
 * image, places it on the center of the screen, and allows dynamic
 * updating of the status text for loading the application.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class SplashWindow extends JWindow {

    /**
     * Constant handle to the glass pane that handles drawing text
     * on top of the splash screen.
     */
    private static final StatusComponent GLASS_PANE = 
        new StatusComponent(StatusComponent.RIGHT);

    /** 
     * Constant handle to the label that represents the splash image.
     */
    private static final JLabel splashLabel = new JLabel();

    /**
     * The sole instance of the SplashWindow
     */
    private static final SplashWindow INSTANCE = new SplashWindow();

    static {
        GLASS_PANE.add(Box.createVerticalGlue(), 0);
        GLASS_PANE.add(GUIMediator.getVerticalSeparator());
        GLASS_PANE.setBorder(
            BorderFactory.createEmptyBorder(0, 2, 0, 2)
        );
    }

    /**
     * Returns the single instance of the SplashWindow.
     */
    public static SplashWindow instance() {
	    return INSTANCE;
    }    

    /**
     * Creates a new SplashWindow, setting its location, size, etc.
     */
    private SplashWindow() {
        final ImageIcon splashIcon = ResourceManager.getThemeImage("splash");
        final Image image = splashIcon.getImage();
        final Dimension size = 
            new Dimension(image.getWidth(null) + 2, image.getHeight(null) + 2);
        this.setSize(size);

        final Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - size.width) / 2,
                    (screenSize.height - size.height) / 2);

        splashLabel.setIcon(splashIcon);
        this.getContentPane().add(splashLabel, BorderLayout.CENTER);

        this.setGlassPane(GLASS_PANE);
        this.pack();
    }
    
    /**
     * Sets the Splash Window to be visible.
     */
    public void begin() {
        toFront();
        this.setVisible(true);
        GLASS_PANE.setVisible(true);
    }

    /**
     * Sets the loading status text to display in the splash 
     * screen window.
     *
     * @param text the text to display
     */
    public static void setStatusText(final String text) {
        GUIMediator.safeInvokeLater(new Runnable() {
            public void run() {
                GLASS_PANE.setText(text);
            }
        });
    }

    /**
     * Refreshes the image on the SplashWindow based on the current theme.
     * This method is used primarily during theme change.
     */
    public static void refreshImage() {
    	final ImageIcon splashIcon = ResourceManager.getThemeImage("splash");
    	splashLabel.setIcon(splashIcon);
    	GLASS_PANE.setVisible(false);
    	INSTANCE.pack();
    	//  force redraw so that splash is drawn before rest of theme changes
    	splashLabel.paintImmediately(0, 0, splashLabel.getWidth(), splashLabel.getHeight());
    }
}

