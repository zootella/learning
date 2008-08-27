package com.limegroup.gnutella.gui;

import javax.swing.SwingUtilities;

import com.limegroup.gnutella.ErrorCallback;

/**
 * This class handles putting error callbacks onto the swing thread for
 * display.
 */
public final class ErrorHandler implements ErrorCallback {


	/**
	 * Displays the error to the user.
	 */
	public void error(Throwable problem) {
        // ThreadDeath must NOT be caught, or a thread will be left zombied	    
	    if(problem instanceof ThreadDeath)
	        throw (ThreadDeath)problem;
        else {
            Runnable doWorkRunnable = new Error(problem, null);
            SwingUtilities.invokeLater(doWorkRunnable);
        }
	}
	
	/**
	 * Displays the error to the user with a specific message.
	 */
	public void error(Throwable problem, String msg) {
        // ThreadDeath must NOT be caught, or a thread will be left zombied	    
	    if(problem instanceof ThreadDeath)
	        throw (ThreadDeath)problem;
        else {
            Runnable doWorkRunnable = new Error(problem, msg);
            SwingUtilities.invokeLater(doWorkRunnable);
        }
    }	    



	/**
	 * This class handles error callbacks.
	 */
    private static class Error implements Runnable {

        /**
         * Constant for the <tt>Throwable</tt> error.
         */
        private final Throwable PROBLEM;
        
        /**
         * Constant for the message to display.
         */
        private final String MESSAGE;

        /**
         * Constant for the <tt>Thread</tt> that the error was caught in.
         */
        private final Thread CURRENT_THREAD;


		private Error( Throwable problem, String msg ) {
			PROBLEM = problem;
			MESSAGE = msg;
            CURRENT_THREAD = Thread.currentThread();
		}
		
        public void run() {
            if( MESSAGE == null )
			    GUIMediator.showInternalError(PROBLEM, CURRENT_THREAD);
            else
                GUIMediator.showInternalError(PROBLEM, MESSAGE,CURRENT_THREAD);
		}
    }	
}
