package com.limegroup.gnutella.gui.options.panes;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.limegroup.gnutella.bugs.LocalClientInfo;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.settings.BugSettings;

/**
 * This class defines the panel in the options window that allows
 * the user to handle bugs.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class BugsPaneItem extends AbstractPaneItem {

    /**
     * The 'Always Send Immediately' string
     */
    private static final String ALWAYS_SEND = "ERROR_INTERNAL_ALWAYS_SEND";
        
    /**
     * The 'Always Ask For Review' string
     */
    private static final String ALWAYS_REVIEW = "ERROR_INTERNAL_ALWAYS_REVIEW";
        
    /**
     * The 'Always Discard Bugs' string
     */
    private static final String ALWAYS_DISCARD="ERROR_INTERNAL_ALWAYS_DISCARD";
    
    /**
     * The 'View Example Bug' string
     */
    private static final String VIEW_EXAMPLE = 
        GUIMediator.getStringResource("OPTIONS_BUGS_VIEW_EXAMPLE");

	/**
	 * Radiobutton for sending
	 */
	private final JRadioButton SEND_BOX = new JRadioButton();

	/**
	 * Radiobutton for reviewing
	 */
	private final JRadioButton REVIEW_BOX = new JRadioButton();
	
	/**
	 * Radiobutton for discarding
	 */
	private final JRadioButton DISCARD_BOX = new JRadioButton();
	
	/**
	 * Buttongroup for radiobuttons.
	 */
	private final ButtonGroup BGROUP = new ButtonGroup();

	/**
	 * The constructor constructs all of the elements of this 
	 * <tt>AbstractPaneItem</tt>.
	 *
	 * @param key the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public BugsPaneItem(final String key) {
		super(key);
		LabeledComponent comp1 = new LabeledComponent(ALWAYS_SEND,
													 SEND_BOX,
													 LabeledComponent.LEFT_GLUE);
		LabeledComponent comp2 = new LabeledComponent(ALWAYS_REVIEW,
													 REVIEW_BOX,
													 LabeledComponent.LEFT_GLUE);
		LabeledComponent comp3 = new LabeledComponent(ALWAYS_DISCARD,
													 DISCARD_BOX,
													 LabeledComponent.LEFT_GLUE);
        JButton example = new JButton(VIEW_EXAMPLE);
        example.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Exception e = new Exception("Example Bug");
                LocalClientInfo info =
                    new LocalClientInfo(e, Thread.currentThread(), "Example", false);
                JTextArea textArea = new JTextArea(info.toBugReport());
                textArea.setColumns(50);
                textArea.setEditable(false);
                JScrollPane scroller = new JScrollPane(textArea);
                scroller.setBorder(BorderFactory.createEtchedBorder());
                scroller.setPreferredSize( new Dimension(500, 200) );
                MessageService.instance().showMessage(scroller);
            }
        });
                
        BGROUP.add(SEND_BOX);
        BGROUP.add(REVIEW_BOX);
        BGROUP.add(DISCARD_BOX);
		add(example);
		add(comp1.getComponent());
		add(comp2.getComponent());
		add(comp3.getComponent());
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Sets the options for the fields in this <tt>PaneItem</tt> when the 
	 * window is shown.
	 */
	public void initOptions() {
        if( BugSettings.IGNORE_ALL_BUGS.getValue() )
            BGROUP.setSelected(DISCARD_BOX.getModel(), true);
        else if (BugSettings.USE_BUG_SERVLET.getValue() )
            BGROUP.setSelected(SEND_BOX.getModel(), true);
        else
            BGROUP.setSelected(REVIEW_BOX.getModel(), true);
	}

	/**
	 * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
	 *
	 * Applies the options currently set in this window, displaying an
	 * error message to the user if a setting could not be applied.
	 *
	 * @throws IOException if the options could not be applied for some reason
	 */
	public boolean applyOptions() throws IOException {
	    ButtonModel bm = BGROUP.getSelection();
	    if( bm.equals(DISCARD_BOX.getModel()) )
	        BugSettings.IGNORE_ALL_BUGS.setValue(true);
	    else if ( bm.equals(SEND_BOX.getModel()) ) {
	        BugSettings.IGNORE_ALL_BUGS.setValue(false);
	        BugSettings.USE_BUG_SERVLET.setValue(true);
	    } else {
	        BugSettings.IGNORE_ALL_BUGS.setValue(false);
	        BugSettings.USE_BUG_SERVLET.setValue(false);
	    }
        return false;
	}
	
    public boolean isDirty() {
        if(BGROUP.getSelection().equals(DISCARD_BOX.getModel()))
            return !BugSettings.IGNORE_ALL_BUGS.getValue();
        if(BGROUP.getSelection().equals(SEND_BOX.getModel()))
            return BugSettings.IGNORE_ALL_BUGS.getValue() ||
                   !BugSettings.USE_BUG_SERVLET.getValue();
        return BugSettings.IGNORE_ALL_BUGS.getValue() ||
               BugSettings.USE_BUG_SERVLET.getValue();
    }	
}
