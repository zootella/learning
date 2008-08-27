package com.limegroup.gnutella.gui.search;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;
import javax.swing.BorderFactory;

import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.themes.ThemeObserver;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.util.CommonUtils;

/**
 * A JPanel designed to be used as an overlay in the default
 * search window.
 */
class OverlayAd extends JPanel implements ThemeObserver {
    
    /**
     * The URL that Pro goes to.
     */
    private static final String PRO_URL =
        "http://www.limewire.com/clientpro?";
    
    /**
     * The icon to close the overlay.
     */
    private final Icon CLOSER;
    
    /**
     * The background image.
     */
    private Icon _image;
    
    /**
     * Whether or not this is still the 'Getting Started'.
     */
    private boolean _searchDone;
   
    /**
     * Constructs a new OverlayAd, starting with the 'Getting Started'
     * image/text.
     */
    OverlayAd() {
        super();
        setLayout(new OverlayLayout(this));
        CLOSER = GUIMediator.getThemeImage("kill_on");
        _image = GUIMediator.getThemeImage("intro");
        
        Dimension preferredSize =
            new Dimension(_image.getIconWidth(), _image.getIconHeight());
        setMaximumSize(preferredSize);
        setPreferredSize(preferredSize);
        
        try {
            add(createTextPanel(false));
            add(createImagePanel());
            GUIUtils.setOpaque(false, this);
        } catch(NullPointerException npe) {
            // internal error w/ swing
            setVisible(false);
            _searchDone = true;
        }
    }
    
    /**
     * Resets everything to be opaque.
     */
    public void updateTheme() {
        GUIUtils.setOpaque(false, this);
    }
    
    /**
     * Changes the overlay after a search is done.
     */
    void searchPerformed() {
        if(!_searchDone) {
            _searchDone = true;
            if(CommonUtils.isPro())
                OverlayAd.this.setVisible(false);
            else {
                _image = GUIMediator.getThemeImage("gopro");
                Dimension preferredSize =
                    new Dimension(_image.getIconWidth(),
                                  _image.getIconHeight());
                setMaximumSize(preferredSize);
                setPreferredSize(preferredSize);                
                
                removeAll();
                add(createTextPanel(true));
                add(createImagePanel());
                GUIUtils.setOpaque(false, this);
            }
        }
    }
    
    /**
     * Creates the background image panel.
     */
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(_image), BorderLayout.CENTER);
        panel.setMaximumSize(getMaximumSize());
        panel.setPreferredSize(getPreferredSize());
        return panel;
    }
    
    /**
     * Creates the text panel, with either 'go pro' text or 'getting started'
     * text.
     * @param goPro whether or not the text is for going pro or getting started
     */
    private JPanel createTextPanel(boolean goPro) {
        JPanel panel = new JPanel(new BorderLayout());
        
        if(CommonUtils.isPro() || goPro)
            panel.add(createNorthPanel(), BorderLayout.NORTH);
        panel.add(Box.createHorizontalStrut(18), BorderLayout.WEST);
        JPanel center = goPro ? 
                    createGoProCenter() : createGettingStartedCenter();
        panel.add(center, BorderLayout.CENTER);
        panel.add(Box.createHorizontalStrut(18), BorderLayout.EAST);

        panel.setMaximumSize(getMaximumSize());
        panel.setPreferredSize(getPreferredSize());
        return panel;
    }
    
    /**
     * Creates the north panel, with the closer icon.
     */
    private JPanel createNorthPanel() {
        JPanel box = new BoxPanel(BoxPanel.X_AXIS);
        box.add(Box.createHorizontalGlue());
        if(CommonUtils.isPro()) {
            JLabel closer = new JLabel(CLOSER);
            closer.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    OverlayAd.this.setVisible(false);
                }
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });
            box.add(closer);
        } else
            box.add(Box.createVerticalStrut(CLOSER.getIconHeight()));
        return box;
    }
    
    /**
     * Creates the getting started center panel.
     */
    private JPanel createGettingStartedCenter() {
        JLabel title = new JLabel(
            GUIMediator.getStringResource("GETTING_STARTED_TITLE"));
        title.setFont(new Font("Dialog", Font.PLAIN, 24));
        title.setForeground(new Color(0x5A, 0x76, 0x94));
        
        JTextArea text = new JTextArea(
            GUIMediator.getStringResource("GETTING_STARTED_CONTENTS"));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setForeground(new Color(0x00, 0x00, 0x00));
        text.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        JScrollPane pane = new JScrollPane(text);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel box = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(40, 0, 5, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        box.add(title, c);
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        box.add(pane, c);
        return box;
    }
    
    /**
     * Creates the Go Pro panel.
     */
    private JPanel createGoProCenter() {
        JLabel title = new JLabel(
            GUIMediator.getStringResource("GOING_PRO_TITLE"));
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setForeground(new Color(0x43, 0x43, 0x43));
        
        JTextArea text = new JTextArea(
            GUIMediator.getStringResource("GOING_PRO_DESCRIPTION"));
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setForeground(new Color(0x43, 0x43, 0x43));
        text.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        JPanel textPanel = new BoxPanel(BoxPanel.X_AXIS);
        textPanel.add(Box.createHorizontalStrut(185));
        textPanel.add(text);
        
        final JPanel box = new BoxPanel(BoxPanel.Y_AXIS);
        box.add(GUIUtils.center(title));
        box.add(Box.createVerticalStrut(80));
        box.add(textPanel);
        
        MouseListener launcher = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if(!e.isConsumed()) {
                    e.consume();
                    GUIMediator.openURL(PRO_URL + 
                            ApplicationSettings.LANGUAGE.getValue());
                }
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {
                e.getComponent().setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        };
        box.addMouseListener(launcher);
        text.addMouseListener(launcher);
        title.addMouseListener(launcher);
        textPanel.addMouseListener(launcher);
        
        return box;
    }        
}
     
