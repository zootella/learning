package com.limegroup.gnutella.gui;

import javax.swing.SwingUtilities;

import com.limegroup.gnutella.MessageCallback;
import com.limegroup.gnutella.settings.BooleanSetting;

/**
 * Displays messages to the user using the standard LimeWire messaging service
 * classes.
 */
public class MessageHandler implements MessageCallback {

    /**
     * Creats a new <tt>MessageHandler</tt> for displaying messages to the user.
     */
    public MessageHandler() {}

    // Inherit doc comment.
    public void showError(final String messageKey) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showError(messageKey);
            }
        });
    }
    
    // Inherit doc comment.
    public void showError(final String messageKey,
                          final BooleanSetting ignore) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showError(messageKey, ignore);
            }
        });
    }    

    // Inherit doc comment.
    public void showError(final String messageKey, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showError(messageKey, message);
            }
        });
    }

    // Inherit doc comment.
    public void showError(final String messageKey,
                          final String message,
                          final BooleanSetting ignore) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showError(messageKey, message, ignore);
            }
        });
    }

    // Inherit doc comment.
    public void showMessage(final String messageKey) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showMessage(messageKey);
            }
        });
    }

    // Inherit doc comment.
    public void showMessage(final String messageKey,
                            final BooleanSetting ignore) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIMediator.showMessage(messageKey, ignore);
            }
        });
    }

}
