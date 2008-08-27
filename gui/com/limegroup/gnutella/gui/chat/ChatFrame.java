package com.limegroup.gnutella.gui.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.limegroup.gnutella.chat.Chatter;
import com.limegroup.gnutella.gui.BoxPanel;
import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.GUIConstants;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.PaddedPanel;
import com.limegroup.gnutella.gui.themes.ThemeObserver;

/**
 * the gui front end for the chat class.  it is a subclass of 
 * JFrame, and displays both user's messages, as well as allowing
 * message input for one user.
 */

//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|

public class ChatFrame extends JFrame implements ThemeObserver {

	/**
	 * Constant for the locale-specific resource key for the label of the 
	 * chat block sender button.
	 */
	private final String BLOCK_BUTTON_LABEL = "CHAT_BLOCK_BUTTON_LABEL";

	/**
	 * Constant for the locale-specific resource key for the label of the 
	 * chat send button.
	 */
	private final String SEND_BUTTON_LABEL = "CHAT_SEND_BUTTON_LABEL";

	/**
	 * Constant for the locale-specific resource key for the toolTip of the 
	 * chat block sender button.
	 */
	private final String BLOCK_BUTTON_TIP = "CHAT_BLOCK_BUTTON_TIP";

	/**
	 * Constant for the locale-specific resource key for the toolTip of the 
	 * chat send button.
	 */
	private final String SEND_BUTTON_TIP = "CHAT_SEND_BUTTON_TIP";

	private final String WITH_LABEL = "CHAT_WITH_LABEL";

	private final String UNAVAILABLE_LABEL = "CHAT_HOST_UNAVAILABLE";

	private final String YOU_LABEL = "CHAT_YOU";

	private final int WINDOW_WIDTH  = 500;
	private final int WINDOW_HEIGHT = 300;
	
	/**
	 * Constants for preventing resizing the frame to an unusable size
	 */
	private final int WINDOW_MIN_WIDTH = 250;
	private final int WINDOW_MIN_HEIGHT = 200;
	
	private final int TEXT_FIELD_LIMIT = 500;
	
	private boolean _connected = true;
	
	JTextArea _area;    /* where the conversation is displayed */
	JTextField _field;  /* where the user enters the message to send */
	JTextField _connectField;
	Chatter _chat;      /* the interface to the backend */
	ButtonRow _buttons; /* block and send buttons */

	public ChatFrame(Chatter chat) {
		super();
		setTitle(GUIMediator.getStringResource(WITH_LABEL) + " " 
				 + chat.getHost());
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		_chat = chat;
    

		Frame parentFrame = GUIMediator.getAppFrame();

		int mwidth = parentFrame.getSize().width / 2;
		int mheight = parentFrame.getSize().height / 2;
		
		int fwidth = getSize().width / 2;
		int fheight = getSize().height / 2;

		int xlocation = mwidth - fwidth;
		int ylocation = mheight - fheight;

		int xstart = parentFrame.getLocation().x;
		int ystart = parentFrame.getLocation().y;

		setLocation(xstart+xlocation, ystart+ylocation);

		BlockListener blockListener = new BlockListener();
		ActionListener sendListener = new SendListener();

		String[] buttonLabels = {
			BLOCK_BUTTON_LABEL,
			SEND_BUTTON_LABEL
		};

		String[] buttonTips = {
			BLOCK_BUTTON_TIP,
			SEND_BUTTON_TIP
		};

		ActionListener[] buttonListeners = {
			blockListener, sendListener
		};

		_buttons = new ButtonRow(buttonLabels,
										  buttonTips,
										  buttonListeners,
										  ButtonRow.X_AXIS,
										  ButtonRow.NO_GLUE);
		_buttons.setButtonEnabled(1, false);

		TextPanel tp = new TextPanel();
		BoxPanel mainPanel = new BoxPanel(BoxLayout.Y_AXIS);
		PaddedPanel myPanel = new PaddedPanel();
		myPanel.setPreferredSize(new Dimension(1000,1000));		
		myPanel.add(tp);

		mainPanel.add(myPanel);
        mainPanel.add(Box.createVerticalStrut(GUIConstants.SEPARATOR));
		mainPanel.add(_buttons);
		mainPanel.add(Box.createVerticalStrut(GUIConstants.SEPARATOR));
		getContentPane().add(mainPanel);
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
				_chat.stop();
				ChatUIManager.instance().removeChat(_chat);
            }
        });
		
		// establish minimum size of the chat window
		addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
            	JFrame src = (JFrame)e.getSource();
                int width = src.getWidth();
                int height = src.getHeight();

                int newWidth = Math.max(width, WINDOW_MIN_WIDTH);
                int newHeight = Math.max(height, WINDOW_MIN_HEIGHT);

                if(newWidth != width || newHeight != height)
            		src.setSize(newWidth, newHeight);
            }
        });		

		updateTheme();
	}

	// inherit doc comment
	public void updateTheme() {
		ImageIcon plugIcon = GUIMediator.getThemeImage(GUIConstants.LIMEWIRE_ICON);
		setIconImage(plugIcon.getImage());		
	}

	/** displays an incoming message from a specific host */
	public void addResponse(String str) {
		String host = _chat.getHost();
		_area.setText(_area.getText() + host + ": " + str + "\n");
	}
	
	/** sets the interface to the back end */
	public void setChat(Chatter chat) {
		_chat = chat;
	}

	public void chatUnavailable() {
		_area.setForeground(Color.red);
		_area.setText(_area.getText() + 
					  GUIMediator.getStringResource(UNAVAILABLE_LABEL)
					  + "\n");
		_connected = false;
		_buttons.setButtonEnabled(1, false);
	}
	
	/** display an error message in red in the chat gui */
	public void displayErrorMessage(String str) {
		_area.setForeground(Color.red);
		_area.setText(str);
	}
	

	/*****************************************************************
	 *                     PRIVATE METHODS
	 *
	 *****************************************************************/

	/** displays your message on the screen, and sends it to
		your chat partner. */
	private void send() {
		if (!_connected)
			return;
		String str = _field.getText();
		if (str.length() == 0)
			return;
		_field.setText("");
		_buttons.setButtonEnabled(1, false);
		_area.setText(_area.getText() + 
					  GUIMediator.getStringResource(YOU_LABEL) +
					  ": "  + str + "\n");
		try {
			_chat.send(str);
		} catch (Exception eeee) {
		}
	} 

	/*****************************************************************
	 *                     PRIVATE CLASSES
	 *
	 *****************************************************************/

	/**
	 * limits the JTextField input to the specified number of characters, 
	 * including pastes
	 */
	private class JTextFieldLimit extends PlainDocument {
		private int limit;
		
		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}
		   
		public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
			if (str == null) return;
			if ((getLength() + str.length()) <= limit)
				super.insertString(offset, str, attr);
		}
	}


	private class TextPanel extends JPanel {
		public TextPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			BoxPanel fieldPanel = new BoxPanel(BoxLayout.X_AXIS);
			_area = new JTextArea();
			_area.setLineWrap(true);
			_field = new JTextField();
			_field.setDocument(new JTextFieldLimit(TEXT_FIELD_LIMIT));
			JScrollPane areaScrollPane = new JScrollPane(_area);

			_area.setEditable(false);
			
		    _field.addKeyListener(new JTextFieldKeyListener());

		    fieldPanel.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR));
			fieldPanel.add(_field);
			fieldPanel.add(Box.createHorizontalStrut(GUIConstants.SEPARATOR));
			fieldPanel.setPreferredSize(new Dimension(1000,20));
			fieldPanel.setMaximumSize(new Dimension(1000, 20));
			add(areaScrollPane);
			add(Box.createVerticalStrut(GUIConstants.SEPARATOR));
			add(fieldPanel);
		}
	}
	
	
	/*****************************************************************
	 *                        LISTENERS
	 *
	 *****************************************************************/
	
	/** end the chat session with the current host */
	private class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            _chat.stop();
    	    ChatUIManager.instance().removeChat(_chat);
		}
	}
		
	/** send a message to the specified host */
	private class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            send();
		}
	}
	
	/** connect to the specified host */
	private class BlockListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String host = _chat.getHost();
			_chat.blockHost(host);
			_chat.stop();
		}
	}

	/** enable state for the send button */
	private class JTextFieldKeyListener implements KeyListener {
		public void keyTyped(KeyEvent k) {
			if (k.getKeyChar() == KeyEvent.VK_ENTER)
				send();                              // send the message
		}
		public void keyPressed(KeyEvent k) {}
		public void keyReleased(KeyEvent k) {
			String text = _field.getText();
			if (text.length() == 0)
				_buttons.setButtonEnabled(1, false); // disable send button
			else if (_connected) {
				_buttons.setButtonEnabled(1, true);  // enable send button
            }
		}
	}
	
}



