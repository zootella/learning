package com.limegroup.gnutella.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.metal.MetalLabelUI;

/**
 * This class uses a timer to continually switch the hyperlink and label.
 * The link is displayed as a standard <tt>JLabel</tt> with a blue foreground 
 * and an overridden paint method to provide underlining.
 */
final class StatusLinkHandler {

	/**
	 * Constant array of <tt>LabelURLPair</tt> instances.
	 */
	private final LabelURLPair[] LABEL_URLS = {
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_01"), 
						 "http://www.limewire.com/index.jsp/pro&21"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_02"), 
						 "http://www.limewire.com/index.jsp/pro&22"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_03"), 
						 "http://www.limewire.com/index.jsp/pro&23"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_04"), 
						 "http://www.limewire.com/index.jsp/pro&24"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_05"), 
						 "http://www.limewire.com/index.jsp/pro&25"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_06"), 
						 "http://www.limewire.com/index.jsp/pro&26"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_07"), 
						 "http://www.limewire.com/index.jsp/pro&27"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_08"), 
						 "http://www.limewire.com/index.jsp/pro&28"),
		new LabelURLPair(GUIMediator.getStringResource("SUPPORT_LIMEWIRE_09"), 
						 "http://www.limewire.com/index.jsp/pro&29"),
	};

	/**
	 * The index of the currently displayed <tt>LabelURLPair</tt> in the array.
	 */
	private int _curLabelIndex = 0;

	/**
	 * Constant for the <tt>JLabel</tt> instance that displays the link.
	 */
	private final JLabel LABEL = new JLabel("", SwingConstants.CENTER);


	/**
	 * The constructor creates the labels, the <tt>Timer</tt> instance,
	 * and the listeners.
	 */
	StatusLinkHandler() {
        LABEL.setUI(new LinkLabelUI());
		FontMetrics fm = LABEL.getFontMetrics(LABEL.getFont());
  		int width = fm.stringWidth("123456789/123456789/1");
  		//check if any of the current labels are larger than default and adjust
  		//it's quite possible the future labels will vary in length
  		//the layout manager will prevent us from going to large, preferred isn't fixed
  		if (LABEL_URLS != null)
	  		for (int i = LABEL_URLS.length; --i >= 0; ) 
		  		width = Math.max(width,fm.stringWidth(LABEL_URLS[i].getLabel()));
	
  		Dimension dim = new Dimension(width, fm.getHeight());
		LABEL.setForeground(Color.blue); //link color, could grab system attribute as well
  		LABEL.setPreferredSize(dim);
  		LABEL.setMaximumSize(dim);

  		LABEL.setText(getNextLabelURLPair().getLabel());		
  		LABEL.addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				StatusLinkHandler.this.handleLinkClick();
			}

			//simulate active cursor, we could choose another cursor though
			public void mouseEntered(MouseEvent e) { 
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			//go back to normal
			public void mouseExited(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getDefaultCursor()); 
			}			
		});
  		
  		//only build and start timer if there are labels to cycle through
  		if (LABEL_URLS.length > 1) {
			new Timer(30 * 1000, new LabelTimerListener()).start();
  		}
	}

	/**
	 * Private class for handling a change in the link/labal pair.
	 */
	private class LabelTimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            StatusLinkHandler.this.handleLinkChange();
		}
	}


	/**
	 * This class wraps a label for the link and the url to link to.
	 */
	private static final class LabelURLPair {
	 	 
		/**
		 * Constant for the label string for this pair.
		 */
		private final String LABEL_STRING;

		/**
		 * Constant for the url for this pair.
		 */
		private final String URL;

		/**
		 * Creates a new <tt>LabelURLPair</tt> instance with the 
		 * specified label and url.
		 *
		 * @param label the label for the link
		 * @param url to url to link to
		 */
		private LabelURLPair(final String label, final String url) {
			LABEL_STRING = label;
			URL = url;
		}

		/**
		 * Returns the label's text
		 *  previous implementation returned the text in html format
		 *  to simulate a hyperlink look; the new label provides the
		 *  framework to simulate this work at a substantial reduction
		 *  in memory and processor cost; the jvm treats any html as a
		 *  possible full blown document and sets up the structures to
		 *  process hyper text formatting for just these couple of words
		 *  every 30 seconds (no reuse either). Also, the old html link
		 *  wasn't truly clickable, we use a mouse listener to provide
		 *  mouse clicking action support; <font color=blue>LABEL_STRING</font>
		 *  would have provided the same effect (or affect :)
		 *	previous: return "<html><a href=\"\">"+LABEL_STRING+"</a></html>";
		 *
		 * @return the label
		 */
		private String getLabel() {
			return LABEL_STRING;
		}

		/**
		 * Returns the url to link to.
		 *
		 * @return the url to link to
		 */
		private String getURL() {
			return URL;
		}
	}

	/**
	 * Returns the <tt>Component</tt> that contains all of the hyperlink.
	 *
	 * @return the <tt>Component</tt> the hyperlink
	 */	
	Component getComponent() {
		return LABEL;
	}
	
	/**
	 * Returns the next <tt>LabelURLPair</tt> in the list.
	 *
	 * @return the next <tt>LabelURLPair</tt> in the list
	 */
	private LabelURLPair getNextLabelURLPair() {
		if(_curLabelIndex == LABEL_URLS.length - 1) {
			_curLabelIndex = -1; //reset index
		}
		_curLabelIndex++;
		return LABEL_URLS[_curLabelIndex];
	}
	
	/**
	 * Handle a change in the current <tt>LabelURLPair</tt> pair.
	 */
	private void handleLinkChange() {
        String label = getNextLabelURLPair().getLabel();
		LABEL.setText(label);
        FontMetrics fm = LABEL.getFontMetrics(LABEL.getFont());
        int width = fm.stringWidth(label);
        int height = fm.getHeight();
        Dimension preferred = new Dimension(width, height);
        LABEL.setPreferredSize(preferred);
		GUIMediator.instance().getStatusLine().refresh();
	}
	
	/**
	 * Handles a click on the current link by opening the appropriate web 
	 * page.
	 */
	private void handleLinkClick() {
		GUIMediator.openURL(LABEL_URLS[_curLabelIndex].getURL());
	}

    /**
     * This class is a specialized UI class for drawing the link label
     * with an underline.
     */
    private class LinkLabelUI extends MetalLabelUI {
        /**
         * Paint clippedText at textX, textY with the labels foreground color.
         * 
         * @see #paint
         * @see #paintDisabledText
         */
        protected void paintEnabledText(JLabel l, Graphics g, String s, 
                                        int textX, int textY) {
            super.paintEnabledText(l, g, s, textX, textY);
			if (LABEL.getText() == null)  return;
			
			FontMetrics fm = g.getFontMetrics();
			g.fillRect(textX, fm.getAscent()+2, 
                       fm.stringWidth(LABEL.getText()) - 
					   LABEL.getInsets().right, 1); //X,Y,WIDTH,HEIGHT
            
        }        
    }
}
