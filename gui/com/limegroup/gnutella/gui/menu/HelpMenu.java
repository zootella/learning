
// Edited for the Learning branch

package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.TipOfTheDayMediator;
import com.limegroup.gnutella.util.CommonUtils;

// Added for snippet class
import com.limegroup.gnutella.snippet.Snippet;

/**
 * Handles all of the contents of the help menu in the menu bar.  This 
 * includes such items as the link to the "Using LimeWire" page of the
 * web site as well as links to the forum, faq, "tell a friend", etc.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class HelpMenu extends AbstractMenu {

	/**
	 * Creates a new <tt>HelpMenu</tt>, using the <tt>key</tt> 
	 * argument for setting the locale-specific title and 
	 * accessibility text.
	 *
	 * @param key the key for locale-specific string resources unique
	 *            to the menu
	 */
	HelpMenu(final String key) {
		super(key);
		addMenuItem("HELP_USING_LIMEWIRE",
		    new LinkListener("http://www.limewire.com/support.htm"));
		addMenuItem("HELP_TOTD", new TOTDListener());
		addMenuItem("HELP_FAQ",
		    new LinkListener("http://www.limewire.com/support/faq.htm"));
		addMenuItem("HELP_FORUM",
		    new LinkListener("http://www.limewire.com/forum.htm"));
		if(!CommonUtils.isMacOSX()) {
            addSeparator();
            addMenuItem("HELP_ABOUT", new AboutListener());
        }
		if(CommonUtils.isTestingVersion()) {
		    addSeparator();
		    addMenuItem("HELP_ERROR", new ErrorListener());
		}
	}
	
	/**
	 * Displays the TOTD window.
	 */
	private static class TOTDListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
            TipOfTheDayMediator.instance().displayTipWindow();
        }
    }

	/**
	 * Opens an error report, for testing.
	 */
	private static class ErrorListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	        throw new RuntimeException("Generated Error");
	    }
	}
	
	/**
	 * Listeners that opens a link.
	 */
	private static class LinkListener implements ActionListener {
	    private final String link;
	    private LinkListener(String link) {
	        this.link = link;
	    }
	    
		public void actionPerformed(ActionEvent e) {
			GUIMediator.openURL(link);
		}
	}
	
	/**
	 * Shows the about window with more information about the application.
	 */
	private static class AboutListener implements ActionListener {				
		/**
		 * Implements the <tt>ActionListener</tt> interface, showing the
		 * about window.
		 */
		public void actionPerformed(ActionEvent e) {
            
		    //GUIMediator.showAboutWindow();
            
            // Added for snippet class
            Snippet.snippet();
		}	   
	}
}
