package com.limegroup.gnutella.gui.mp3;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JProgressBar;
import javax.swing.UIManager;

/**
 * This class handles rendering a JProgressBar (duh.) for playing songs.
 */
public final class SongProgressBar extends JProgressBar {
	
	
	public SongProgressBar() {		
        super();
				Font newFont; // ** jay declare it out of the if statement
				if (super.getFont() == null)  // ** jay
					newFont = new Font("moo", Font.PLAIN, 9); // no using old font's name if it is null
				else 
					newFont = new Font(super.getFont().getName(),Font.PLAIN, 9); // ** jay   
		super.setFont(newFont);
		super.setString("");
	// BTW, if I understood some comments in the skinLF CVS correctly, these changes may soon be unnecessary
	}
	/**
	 * Returns the value of the SongProgressBar String
	 *   overridden here because it doesn't change
	 *
	 * @return int
     */
	public int getOrientation() {
		return JProgressBar.HORIZONTAL;
	}
	/**
	 * Returns the value of the SongProgressBar String
	 *   overridden here because we don't need to support all the overhead JProgressBar does
	 *
	 * @return 
	 * @see    #setString
	 */
	public String getString(){
	    return super.progressString;
	}
	/**
	 * Returns true if the progress bar has a border or false if it does not.
	 *  overridden because we always paint our border
	 *
	 * @return whether the progress bar paints its border
	 */
	public boolean isBorderPainted() {
		return true;
	}
	/**
	 * Returns true, SongProgressBar Return whether the receiving component should use a buffer to paint 
	 *  overridden because we always want buffering
	 *
	 * @return true
	 */
	public boolean isDoubleBuffered() {
		return true;
	}
	/**
	 * Returns true, SongProgressBar always paints its contents (sometimes empty "") 
	 *  overridden because we always paint our string
	 *
	 * @return whether the progress bar paints its string
	 */
	public boolean isStringPainted() {
		return true;
	}
	/**
	 * Returns immediately, SongProgressBar never has children
	 *  overridden because we want to return quickly for performance
	 *
	 * @see #paint
	 * @see java.awt.Container#paint
	 */
	protected void paintChildren(Graphics g) {
		
		//Assert.that(super.getComponentCount() == 0, " SongProgressBar acquired children!");
		return;
	}
	
	/**
	 * Sets the value of the SongProgressBar String
	 *   overridden here because we don't need to support all the overhead JProgressBar does
	 *   no need to firePropertyChange events and we always repaint when setting strings
	 *
	 * @param  s       the value of the percent string
	 */
	public void setString(String s){
		
		super.progressString = s;
		repaint();
	}
	/**
	 * Notification from the UIFactory that the L&F has changed. 
	 * overridden to hardcode our UI choice, installed at instantiation
	 *
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
	  setUI((javax.swing.plaf.ProgressBarUI)UIManager.getUI(this));
          // You had this line in here, but commented out.  why?
	}
}
