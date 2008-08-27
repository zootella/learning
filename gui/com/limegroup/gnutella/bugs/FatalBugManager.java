package com.limegroup.gnutella.bugs;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Toolkit;

import com.limegroup.gnutella.gui.MultiLineLabel;


/**
 * A bare-bones bug manager, for fatal errors.
 */
public final class FatalBugManager {
    
    private FatalBugManager() {}
    
    /**
     * Handles a fatal bug.
     */
    public static void handleFatalBug(Throwable bug) {
        if( bug instanceof ThreadDeath ) // must rethrow.
	        throw (ThreadDeath)bug;
	        
        bug.printStackTrace();
        
        // Build the LocalClientInfo out of the info ...
        LocalClientInfo info = new LocalClientInfo(bug, Thread.currentThread(), null, true);
        
        reviewBug(info);
    }
    
    /**
     * Reviews the bug.
     */
    public static void reviewBug(final LocalClientInfo info) {
        final JDialog DIALOG = new JDialog();
        DIALOG.setTitle("Fatal Error");
		final Dimension DIALOG_DIMENSION = new Dimension(100, 300);
		DIALOG.setSize(DIALOG_DIMENSION);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        MultiLineLabel label = new MultiLineLabel(
            "LimeWire has encountered a fatal internal error and will now exit. " +
            "This is generally caused by a corrupted installation.  Please try " + 
            "downloading and installing LimeWire again.\n\n" + 
            "To aid with debugging, please click 'Send' to notify LimeWire about the problem. " +
            "If desired, you can click 'Review' to look at the information that will be sent. " + 
            "If the problem persists, please copy " + 
            "and paste the 'review' message and send it to: bugs@limewire.com.\n\n" +
            "Thank You.", 400);
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(Box.createHorizontalGlue());
		labelPanel.add(label);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToServlet(info);
				DIALOG.dispose();
				System.exit(1);
			}
		});

        JButton reviewButton = new JButton("Review");
        reviewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                JTextArea textArea = new JTextArea(info.toBugReport());
                textArea.setColumns(50);
                textArea.setEditable(false);
                textArea.selectAll();
                textArea.copy();
                textArea.setCaretPosition(0);                
                JScrollPane scroller = new JScrollPane(textArea);
                scroller.setBorder(BorderFactory.createEtchedBorder());
                scroller.setPreferredSize( new Dimension(500, 200) );
                showMessage(DIALOG, scroller);
			}
		});

		JButton discardButton = new JButton("Discard");
		discardButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        DIALOG.dispose();
		        System.exit(1);
		    }
		});
        buttonPanel.add(sendButton);
        buttonPanel.add(reviewButton);
        buttonPanel.add(discardButton);

        mainPanel.add(labelPanel);
        mainPanel.add(buttonPanel);

        DIALOG.getContentPane().add(mainPanel);
		DIALOG.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = DIALOG.getSize();
		DIALOG.setLocation((screenSize.width - dialogSize.width)/2,
						   (screenSize.height - dialogSize.height)/2);

        DIALOG.show();
    }
    
    /**
     * Sends a bug to the servlet & then exits.
     */
    private static void sendToServlet(LocalClientInfo info) {
        new ServletAccessor().getRemoteBugInfo(info);
    }
    
    /**
     * Shows a message.
     */
    private static void showMessage(Component parent, Component toDisplay) {
		JOptionPane.showMessageDialog(parent,
				  toDisplay,
				  "Fatal Error - Review",
				  JOptionPane.INFORMATION_MESSAGE);	
    }
}