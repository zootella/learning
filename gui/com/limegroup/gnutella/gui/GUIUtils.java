package com.limegroup.gnutella.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.limegroup.gnutella.SpeedConstants;
import com.limegroup.gnutella.gui.themes.ThemeSettings;
import com.limegroup.gnutella.util.CommonUtils;

//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
/**
 *  This class serves as a holder for any static gui convenience
 *  methods.
 */
public final class GUIUtils {
    
    /**
     * Make sure the constructor is never called.
     */
    private GUIUtils() {}
    
    /**
     * Localizable Number Format constant for the current default locale
     * set at init time.
     */
    private static final NumberFormat NUMBER_FORMAT0; // localized "#,##0"
    private static final NumberFormat NUMBER_FORMAT1; // localized "#,##0.0"
    
    /**
     * Localizable Date Format constant for the current default locale
     * set at init time.
     */
    private static final DateFormat DATETIME_FORMAT;
    
    /**
     * Localizable constants
     */
    public static final String GENERAL_UNIT_KILOBYTES;
    public static final String GENERAL_UNIT_MEGABYTES;
    public static final String GENERAL_UNIT_GIGABYTES;
    public static final String GENERAL_UNIT_TERABYTES;
    /* ambiguous name: means kilobytes/second, not kilobits/second! */
    public static final String GENERAL_UNIT_KBPSEC;
    
    public static final HyperlinkListener HYPER_LISTENER;
    
    /**
     * An action that disposes the parent window.
     * Constructed lazily.
     */
    public static Action ACTION_DISPOSE;

    static {
        NUMBER_FORMAT0 = NumberFormat.getNumberInstance();
        NUMBER_FORMAT0.setMaximumFractionDigits(0);
        NUMBER_FORMAT0.setMinimumFractionDigits(0);
        NUMBER_FORMAT0.setGroupingUsed(true);
        
        NUMBER_FORMAT1 = NumberFormat.getNumberInstance();
        NUMBER_FORMAT1.setMaximumFractionDigits(1);
        NUMBER_FORMAT1.setMinimumFractionDigits(1);
        NUMBER_FORMAT1.setGroupingUsed(true);

        DATETIME_FORMAT = DateFormat.getDateTimeInstance();
        
        GENERAL_UNIT_KILOBYTES =
            GUIMediator.getStringResource("GENERAL_UNIT_KILOBYTES");
        GENERAL_UNIT_MEGABYTES =
            GUIMediator.getStringResource("GENERAL_UNIT_MEGABYTES");
        GENERAL_UNIT_GIGABYTES =
            GUIMediator.getStringResource("GENERAL_UNIT_GIGABYTES");
        GENERAL_UNIT_TERABYTES =
            GUIMediator.getStringResource("GENERAL_UNIT_TERABYTES");
        GENERAL_UNIT_KBPSEC =
            GUIMediator.getStringResource("GENERAL_UNIT_KBPSEC");

        HYPER_LISTENER = new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent he) {
				if(he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				    URL url = he.getURL();
				    if(url != null)
    				    GUIMediator.openURL(url.toExternalForm());
                }
			}
		};
    }
    
    /**
     * This static method converts the passed in number
     * into a localizable representation of an integer, with
     * digit grouping using locale dependant separators.
     *
     * @param value the number to convert to a numeric String.
     *
     * @return a localized String representing the integer value
     */
    public static String toLocalizedInteger(long value) {
        return NUMBER_FORMAT0.format(value);
    }
    
    /**
     * This static method converts the passed in number of bytes into a
     * kilobyte string grouping digits with locale-dependant thousand separator
     * and with "KB" locale-dependant unit at the end.
     *
     * @param bytes the number of bytes to convert to a kilobyte String.
     *
     * @return a String representing the number of kilobytes that the
     *         <code>bytes</code> argument evaluates to, with "KB" appended
     *         at the end.  If the input value is negative, the string
     *         returned will be "? KB".
     */
    public static String toKilobytes(long bytes) {
        if (bytes < 0)
            return "? " + GENERAL_UNIT_KILOBYTES;
        long kbytes = bytes / 1024;
         // round to nearest multiple, or round up if size below 1024
        if ((bytes & 512) != 0 || (bytes > 0 && bytes < 1024)) kbytes++;
        // result formating, according to the current locale
        return NUMBER_FORMAT0.format(kbytes) + GENERAL_UNIT_KILOBYTES;
    }
    
    /**
     * Converts the passed in number of bytes into a byte-size string.
     * Group digits with locale-dependant thousand separator if needed, but
     * with "KB", or "MB" or "GB" or "TB" locale-dependant unit at the end,
     * and a limited precision of 4 significant digits.
     *
     * @param bytes the number of bytes to convert to a size String.
     * @return a String representing the number of kilobytes that the
     *         <code>bytes</code> argument evaluates to, with
     *         "KB"/"MB"/"GB"/TB" appended at the end. If the input value is
     *         negative, the string returned will be "? KB".
     */
    public static String toUnitbytes(long bytes) {
        if (bytes < 0) {
            return "? " + GENERAL_UNIT_KILOBYTES;
        }
        long   unitValue; // the multiple associated with the unit
        String unitName;  // one of localizable units
        if (bytes < 0x6400000) {                // below 100MB, use KB
            unitValue = 0x400;
            unitName = GENERAL_UNIT_KILOBYTES;
        } else if (bytes < 0x1900000000L) {     // below 100GB, use MB
            unitValue = 0x100000;
            unitName = GENERAL_UNIT_MEGABYTES;
        } else if (bytes < 0x640000000000L) {   // below 100TB, use GB
            unitValue = 0x40000000;
            unitName = GENERAL_UNIT_GIGABYTES;
        } else {                                // at least 10TB, use TB
            unitValue = 0x10000000000L;
            unitName = GENERAL_UNIT_TERABYTES;
        }
        NumberFormat numberFormat; // one of localizable formats
        if ((double)bytes * 100 / unitValue < 99995)
            // return a minimum "100.0xB", and maximum "999.9xB"
            numberFormat = NUMBER_FORMAT1; // localized "#,##0.0"
        else
            // return a minimum "1,000xB"
            numberFormat = NUMBER_FORMAT0; // localized "#,##0"
        try {
            return numberFormat.format((double)bytes / unitValue) +
                " " + unitName;
        } catch(ArithmeticException ae) {
            return "0 " + unitName;
            // see: http://bugs.limewire.com:8080/bugs/servlet/Search?l=152&c=537&o=3&j=43&m=470_319
            // internal java error, just return 0.
        }
    }
    
    /**
     * Converts the passed in number into a short localized string.
     * Group digits with locale-dependant thousand separator if needed, but
     * first try to append a "k", "M", "G", or "T" locale-independant unit.
     * Allows negative numbers in a locale-dependant representation.
     *
     * @param value the number of bytes to convert to a size String.
     * @param allowFractional enables 1 fractional digit for values lower than
     *                        999.95; 1 fractional digit is always enabled
     *                        for values greater than or equal to 99999.5,
     *                        which have a unit appended.
     * @return a <code>String</code> representing the number that the
     *         <code>value</code> argument evaluates to, with a
     *         possible "k"/"M"/"G"/T" unit appended at the end.
     */
    public static String toUnitnumber(double value, boolean allowFractional) {
        double abs = (value < 0) ? -value : value;
        long   unitValue; // the multiple associated with the unit
        String unitName;  // one of localizable units
        if (abs < 100000) {                     // below 100k, don't use unit
            unitValue = 1;
            unitName = ""; // no unit
        } else if (abs < 100000000) {           // below 100M, use k
            unitValue = 1000;
            unitName = "k"; // international unit for thousands (kilo)
            allowFractional = true;
        } else if (abs < 100000000000L) {       // below 100G, use M
            unitValue = 1000000;
            unitName = "M"; // international unit for millions (mega)
            allowFractional = true;
        } else if (abs < 100000000000000L) {    // below 100T, use G
            unitValue = 1000000000;
            unitName = "G"; // international unit for billions (giga)
            allowFractional = true;
        } else {                                // at least 10TB, use T
            unitValue = 1000000000000L;
            unitName = "T"; // international unit for trillions (tera)
            allowFractional = true;
        }
        NumberFormat numberFormat; // one of localizable formats
        if (allowFractional && abs * 100 / unitValue < 99995) {
            // return a minimum "100.0x", and maximum "999.9x"
            numberFormat = NUMBER_FORMAT1; // localized "#,##0.0"
        } else {
            // return a minimum "1,000x"
            numberFormat = NUMBER_FORMAT0; // localized "#,##0"
        }
        return numberFormat.format(value / unitValue) + unitName;
    }
    
    /**
     * Returns a label with multiple lines that is sized according to
     * the string parameter.
     *
     * @param msg the string that will be contained in the label.
     *
     * @return a MultiLineLabel sized according to the passed
     *  in string.
     */
    public static MultiLineLabel getSizedLabel(String msg) {
        Dimension dim = new Dimension();
        MultiLineLabel label = new MultiLineLabel(msg);
        FontMetrics fm = label.getFontMetrics(label.getFont());
        int width = fm.stringWidth(msg);
        dim.setSize(Integer.MAX_VALUE, width / 9); //what's this magic?
        label.setPreferredSize(dim);
        return label;
    }
    
    /**
     * Converts the following bandwidth value, in kbytes/second, to
     * a human readable.
     */
    public static String speed2name(int rate) {
        if (rate <= SpeedConstants.MODEM_SPEED_INT)
            return GUIConstants.MODEM_SPEED;
        else if (rate <= SpeedConstants.CABLE_SPEED_INT)
            return GUIConstants.CABLE_SPEED;
        else if (rate <= SpeedConstants.T1_SPEED_INT)
            return GUIConstants.T1_SPEED;
        else if (rate < Integer.MAX_VALUE)
            return GUIConstants.T3_SPEED;
        else
            return GUIConstants.MULTICAST_SPEED;
    }
    
    /**
     * Converts an rate into a human readable and localized KB/s speed.
     */
    public static String rate2speed(double rate) {
        return NUMBER_FORMAT0.format(rate) + " " + GENERAL_UNIT_KBPSEC;
    }
    
    /**
     * Converts a value in seconds to:
     *     "d:hh:mm:ss" where d=days, hh=hours, mm=minutes, ss=seconds, or
     *     "h:mm:ss" where h=hours<24, mm=minutes, ss=seconds, or
     *     "m:ss" where m=minutes<60, ss=seconds
     */
    public static String seconds2time(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        int days = hours / 24;
        hours = hours - days * 24;
        // build the numbers into a string
        StringBuffer time = new StringBuffer();
        if (days != 0) {
            time.append(Integer.toString(days));
            time.append(":");
            if (hours < 10) time.append("0");
        }
        if (days != 0 || hours != 0) {
            time.append(Integer.toString(hours));
            time.append(":");
            if (minutes < 10) time.append("0");
        }
        time.append(Integer.toString(minutes));
        time.append(":");
        if (seconds < 10) time.append("0");
        time.append(Integer.toString(seconds));
        return time.toString();
    }
    
    /**
     * Converts number of milliseconds since way back when to
     * a local-formatted date String
     */
    public static String msec2DateTime(long milliseconds) {
        Date d = new Date(milliseconds);
        return DATETIME_FORMAT.format(d);
    }
    
    /**
     * Sets the child components of a component to all be either
     * opaque or not opaque.
     */
    public static void setOpaque(boolean op, JComponent c) {
        c.setOpaque(op);
        Component[] cs = c.getComponents();
        for(int i = 0; i < cs.length; i++) {
            if(cs[i] instanceof JComponent &&
               !(cs[i] instanceof JTextField) &&
               (ThemeSettings.isNativeOSXTheme() ||
                !(cs[i] instanceof JButton))
              ) {  
                ((JComponent)cs[i]).setOpaque(op);
                setOpaque(op, (JComponent)cs[i]);
            }
        }
    }
    
    /**
     * Centers the given component.
     */
    public static JPanel center(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.add(c);
        return p;
    }  
    
    /**
     * Left flushes the given component.
     */
    public static JPanel left(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.add(c);
        return p;
    }

    /**
     * Gets the width of a given label.
     */
    public static int width(JLabel c) {
        FontMetrics fm = c.getFontMetrics(c.getFont());
        return fm.stringWidth(c.getText()) + 3;
    }
    
    /**
     * Determines if a font can display up to a point in the string.
     *
     * Returns -1 if it can display the whole string.
     */
    public static boolean canDisplay(Font f, String s) {
        int upTo = f.canDisplayUpTo(s);
        if(upTo >= s.length() || upTo == -1)
            return true;
        else
            return false;
    }
    
    /**
     * Adds an action to hide a window / dialog.
     *
     * On OSX, this is done by typing 'Command-W'.
     * On all other platforms, this is done by hitting 'ESC'.
     */
    public static void addHideAction(JComponent jc) {
        InputMap map = jc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        map.put(getHideKeystroke(), "limewire.hideWindow");
        jc.getActionMap().put("limewire.hideWindow", getDisposeAction());
    }
    
    /**
     * Gets the keystroke for hiding a window according to the platform.
     */
    public static KeyStroke getHideKeystroke() {
        if(CommonUtils.isMacOSX())
            return KeyStroke.getKeyStroke(KeyEvent.VK_W,
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        else
            return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    }
    
    /**
     * Binds a key stroke to the given action for the component. The action is 
     * triggered when the key is pressed and the keyboard focus is withing the
     * specifiedd scope.
     *  
     * @param c component for which the keybinding is installed
     * @param key the key that triggers the action
     * @param a the action
     * @param focusScope one of {@link JComponent.WHEN_FOCUSED},
     * {@link JComponent.WHEN_IN_FOCUSED_WINDOW},
     * {@link JComponent.WHEN_ANCESTOR_OF_FOCUSED_WINDOW}
     */
    public static void bindKeyToAction(JComponent c, KeyStroke key, Action a,
    		int focusScope) {
    	InputMap inputMap = c.getInputMap(focusScope);
        ActionMap actionMap = c.getActionMap();
        if (inputMap != null && actionMap != null) {
        	inputMap.put(key, a);
        	actionMap.put(a, a);
        }
    }
    
    /**
     * Convenience wrapper for {@link #bindKeyToAction(JComponent, KeyStroke,
     * Action, int) bindKeyToAction(c, key, a, JComponentn.WHEN_FOCUSED)}.
     */
    public static void bindKeyToAction(JComponent c, KeyStroke key, Action a) {
    	bindKeyToAction(c, key, a, JComponent.WHEN_FOCUSED);
    }
    
    
    /**
     * Returns (possibly constructing) the ESC action.
     */
    public static Action getDisposeAction() {
        if(ACTION_DISPOSE == null) {
            ACTION_DISPOSE = new AbstractAction() {
                public void actionPerformed(ActionEvent ae) {
                    Window parent;
                    if(ae.getSource() instanceof Window)
                        parent = (Window)ae.getSource();
                    else
                        parent = SwingUtilities.getWindowAncestor((Component)ae.getSource());

                    if(parent != null)
                        parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
                }
            };
        }
        return ACTION_DISPOSE;
    }
    
    /**
     * Fixes the InputMap to have the correct KeyStrokes registered for
     * actions on various OS's.
     *
     * Currently, this fixes OSX to use the 'meta' key instead of hard-coding
     * it to use the 'control' key for actions such as 'select all', etc..
     */
    public static void fixInputMap(JComponent jc) {
        InputMap map =
            jc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if(CommonUtils.isMacOSX()) {
            replaceAction(map, 'A'); // select all
            replaceAction(map, 'C'); // copy
            replaceAction(map, 'V'); // paste
            replaceAction(map, 'X'); // cut
        }
    }
    
    /**
     * Moves the action for the specified character from the 'ctrl' mask
     * to the 'meta' mask.
     */
    private static void replaceAction(InputMap map, char c) {
        KeyStroke ctrl = KeyStroke.getKeyStroke("control pressed " + c);
        KeyStroke meta = KeyStroke.getKeyStroke("meta pressed " + c);
        if(ctrl == null || meta == null)
            return;
        Object action = map.get(ctrl);
        if(action != null) {
            map.remove(ctrl);
            map.put(meta, action);
        }
	}
	
	/**
	 * Returns the sole hyperlink listener.
	 */
	public static HyperlinkListener getHyperlinkListener() {
	    return HYPER_LISTENER;
    }
    
    /**
     * A MouseListener that changes the cursor & 
     * goes to a URL on a click.
     */
    public static MouseListener getURLInputListener(final String url) {
        return new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            public void mouseClicked(MouseEvent e) {
                GUIMediator.openURL(url);
            }
        };
    }
    
    
    /**
     * Determines if the Start On Startup option is availble.
     */
    public static boolean shouldShowStartOnStartupWindow() {
        return (CommonUtils.isMacOSX() &&
                CommonUtils.isJava14OrLater() &&
                CommonUtils.isCocoaFoundationAvailable()) ||
               (WindowsUtils.isLoginStatusAvailable());
    }
    
    /**
     * Converts all spaces in the string to non-breaking spaces.
     *
     * Adds 'preSpaces' number of non-breaking spaces prior to the string.
     */
    public static String convertToNonBreakingSpaces(int preSpaces, String s) {
        StringBuffer b = new StringBuffer(preSpaces + s.length());
        for(int i = 0; i < preSpaces; i++)
            b.append('\u00a0');
        b.append(s.replace(' ', '\u00a0'));
        return b.toString();
    }
}
