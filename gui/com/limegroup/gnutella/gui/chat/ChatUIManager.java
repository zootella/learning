package com.limegroup.gnutella.gui.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import com.limegroup.gnutella.chat.Chatter;

/**
 * Manages all chat session and provides an interface to access each
 * chat session.
 */
public final class ChatUIManager {

    /**
     * Constant for the single <tt>ChatManager</tt> instance for 
     * singleton.
     */
	private static final ChatUIManager INSTANCE = new ChatUIManager();
	
	/** 
	 * <tt>Map</tt> of <tt>Chatter</tt> instances.
	 */
    private Map _chats = Collections.synchronizedMap(new HashMap());

	/** 
	 * Private constructor to ensure that this class cannot be 
	 * constructed from any other class. 
	 */
	private ChatUIManager() {}
	
	/** 
	 * Returns the single instance of this class, following singleton.
	 * 
	 * @return the single <tt>ChatManager</tt> instance
	 */
	public static ChatUIManager instance() {
		return INSTANCE;
	}
	
	/**
	 * Accepts a new chat session with a new user.
	 *
	 * @chatter the new <tt>Chatter</tt> instance to chat with
	 */
	public void acceptChat(Chatter chatter) {
		if (frameAlreadyExists(chatter)) {
			raiseExistingFrame(chatter);
			return;
		}
		ChatFrame cframe = new ChatFrame(chatter);
		cframe.setVisible(true);
		_chats.put(chatter, cframe);
	}
	
	/**
	 * Checks if there is already an existing chat with the same port.
	 * 
	 */
	private boolean frameAlreadyExists(Chatter chatter) {
		Set existingChats = _chats.keySet();
		for (Iterator i = existingChats.iterator(); i.hasNext(); ) {
			Chatter c = (Chatter)i.next();
			if (c.getHost().compareTo(chatter.getHost()) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Raises an existing chat window.
	 * 
	 */
	private void raiseExistingFrame(Chatter chatter) {
		Set existingChats = _chats.keySet();
		for (Iterator i = existingChats.iterator(); i.hasNext(); ) {
			Chatter c = (Chatter)i.next();
			if (c.getHost().compareTo(chatter.getHost()) == 0) {
				ChatFrame frame = (ChatFrame)(_chats.get(c));
				frame.setState(JFrame.NORMAL);
				frame.toFront();
				return;
			}
		}
	}

    /**
     * Removes the specified chat session from the list of active 
     * sessions.
     *
     * @param the <tt>Chatter</tt> instance to remove
     */
	public void removeChat(Chatter chatter) {
		ChatFrame cframe = (ChatFrame)(_chats.remove(chatter));
		if (cframe != null) {
		    cframe.dispose();
			cframe.setVisible(false);
		}
			
	}
	
	/**
	 * Receives a message for the session associated with the specified
	 * <tt>Chatter</tt> instance.
	 *
	 * @param chatter the <tt>Chatter</tt> instance with which the new
	 *  message is associated
	 */
	public void receiveMessage(Chatter chatter) {
		ChatFrame cframe = (ChatFrame)_chats.get(chatter);
        
        if(cframe == null) {
            // The frame could be null if the user on this end already
            // removed it, for example.
            return;
        }
		cframe.addResponse( chatter.getMessage() );
	}

	/** 
	 * Lets the user know that a host is no longer available. 
	 *
	 * @param the <tt>Chatter</tt> instance for the host that is no longer
	 *  available
	 */
	public void chatUnavailable(Chatter chatter) {
		ChatFrame cframe = (ChatFrame)_chats.get(chatter);
        if(cframe == null) {
            return;
        }
		cframe.chatUnavailable();
	}

	/** 
	 * Display an error message in the chat gui for the specified chat
	 * session.
	 *
	 * @param chatter the <tt>Chatter</tt> instance associated with the error
	 * @param str the error message to display
	 */
	public void chatErrorMessage(Chatter chatter, String str) {
		ChatFrame cframe = (ChatFrame)_chats.get(chatter);
        if(cframe == null) {
            return;
        }
		cframe.displayErrorMessage(str);
    }

}
