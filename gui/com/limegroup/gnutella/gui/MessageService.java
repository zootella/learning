package com.limegroup.gnutella.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.limegroup.gnutella.settings.BooleanSetting;
import com.limegroup.gnutella.settings.IntSetting;

/**
 * This class handles displaying messages to the user.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class MessageService {

	/**
	 * Constant for when the user selects the yes button in a message 
	 * giving the user a yes and a no option.
	 */
	public static final int YES_OPTION = 101;

	/**
	 * Constant for when the user selects the no button in a message giving 
	 * the user a yes and a no option.
	 */
	public static final int NO_OPTION  = 102;
	
	/**
	 * Constant for when the user selects cancel.
	 */
	public static final int CANCEL_OPTION = 103;
	
	/**
	 * Constant for when the 'Always use this answer' checkbox wants to 
	 * remember the answer.
	 */
	public static final int REMEMBER_ANSWER = 1;
	
	/**
	 * Constant for when the 'Always use this answer' checkbox does not
	 * want to remember the answer.
	 */
	public static final int FORGET_ANSWER = 0;

	/**
	 * <tt>MessageService</tt> instance, following singleton.
	 */
	private static final MessageService INSTANCE = new MessageService();

	/**
	 * Instance accessor for the <tt>MessageService</tt>.
	 */
	public static MessageService instance() {
		return INSTANCE;
	}

	/**
	 * Initializes all of the necessary messaging components.
	 */
	MessageService() {
		GUIMediator.setSplashScreenString(
	        GUIMediator.getStringResource("SPLASH_STATUS_MESSAGE_SERVICE"));
	}


	/**
	 * Display a standardly formatted error message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 */
	final void showError(String message) {
    	JOptionPane.showMessageDialog(getParentComponent(), 
                    new MultiLineLabel(message),
				  GUIMediator.getStringResource("MESSAGE_ERROR_CAPTION"),
				  JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a standardly formatted error message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 * @param ignore the Boolean setting to store/retrieve whether or not to
	 *  ignore this message in the future.	 
	 */
	final void showError(String message, BooleanSetting ignore) {
	    if ( !ignore.getValue() ) {
		    JOptionPane.showMessageDialog(getParentComponent(), 
                      doNotDisplayAgainLabel(message, ignore),
					  GUIMediator.getStringResource("MESSAGE_ERROR_CAPTION"),
					  JOptionPane.ERROR_MESSAGE);
        }
	}
	
	/**
	 * Display a standardly formatted warning message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 * @param ignore the Boolean setting to store/retrieve whether or not to
	 *  ignore this message in the future.	 
	 */
	final void showWarning(String message, BooleanSetting ignore) {
	    if ( !ignore.getValue() ) {
		    JOptionPane.showMessageDialog(getParentComponent(), 
                      doNotDisplayAgainLabel(message, ignore),
					  GUIMediator.getStringResource("MESSAGE_WARNING_CAPTION"),
					  JOptionPane.WARNING_MESSAGE);
        }
	}

	/**
	 * Display a standardly formatted warning message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 */
	final void showWarning(String message) {
		JOptionPane.showMessageDialog(getParentComponent(), 
									  new MultiLineLabel(message),
									  GUIMediator.getStringResource
									  ("MESSAGE_WARNING_CAPTION"),
									  JOptionPane.WARNING_MESSAGE);
	}
	
	
	/**
	 * Displays a standardly formatted information message with
	 * the specified Component.
	 *
	 * @param toDisplay the object to display in the message
	 */
	public final void showMessage(Component toDisplay) {
		JOptionPane.showMessageDialog(getParentComponent(), 
				  toDisplay,
				  GUIMediator.getStringResource("MESSAGE_CAPTION"),
				  JOptionPane.INFORMATION_MESSAGE);	
    }

	/**
	 * Display a standardly formatted information message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 */	
	final void showMessage(String message) {
		JOptionPane.showMessageDialog(getParentComponent(), 
				  new MultiLineLabel(message),
				  GUIMediator.getStringResource("MESSAGE_CAPTION"),
				  JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Display a standardly formatted information message with
	 * the specified String.  Store whether or not to display message
	 * again in the BooleanSetting ignore.
	 *
	 * @param message the message to display to the user
	 * @param ignore the Boolean setting to store/retrieve whether or not to
	 *  ignore this message in the future.
	 */	
	final void showMessage(String message, BooleanSetting ignore) {
	    if ( !ignore.getValue() ) {
    		JOptionPane.showMessageDialog(getParentComponent(),
                      doNotDisplayAgainLabel(message, ignore),
					  GUIMediator.getStringResource("MESSAGE_CAPTION"),
					  JOptionPane.INFORMATION_MESSAGE);
        }
	}	

	/**
	 * Display a standardly formatted confirmation message with
	 * the specified String. 
	 *
	 * @param message the message to display to the user
	 */	
	final void showConfirmMessage(String message) {
		JOptionPane.showConfirmDialog(getParentComponent(), 
					  new MultiLineLabel(message),
					  GUIMediator.getStringResource("MESSAGE_CAPTION"),
					  JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Display a standardly formatted confirmation message with
	 * the specified String.  Store whether or not to display
	 * the message again in the BooleanSetting ignore.
	 *
	 * @param message the message to display to the user
	 * @param ignore the Boolean setting to store/retrieve whether or not to
	 *  ignore this message in the future.	 
	 */	
	final void showConfirmMessage(String message, BooleanSetting ignore) {
	    if ( !ignore.getValue() ) {
    		JOptionPane.showConfirmDialog(getParentComponent(), 
						  doNotDisplayAgainLabel(message, ignore),
						  GUIMediator.getStringResource("MESSAGE_CAPTION"),
						  JOptionPane.INFORMATION_MESSAGE);
        }
	}	

	/**
	 * Displays a message to the user and returns 
	 * MessageService.YES_OPTION if the user selects yes and
	 * MessageService.NO_OPTION if the user selects no.
	 *
	 * @param message the message to display to the user
	 */ 
	final int showYesNoMessage(String message) {
		return showYesNoMessage(message,GUIMediator.getStringResource("MESSAGE_CAPTION"));
	}

    /**
     * Displays a message to the user and returns 
     * MessageService.YES_OPTION if the user selects yes and
     * MessageService.NO_OPTION if the user selects no.
     *
     * @param message the message to display to the user
     * @title the title on the dialog
     */ 
    final int showYesNoMessage(String message, String title) {
        int option;
        try {
            option =
                JOptionPane.showConfirmDialog(getParentComponent(), 
                              new MultiLineLabel(message), 
                              title,
                              JOptionPane.YES_NO_OPTION);
        } catch(InternalError ie) {
            // happens occasionally, assume no.
            option = JOptionPane.NO_OPTION;
        }
            
        if(option == JOptionPane.YES_OPTION) return MessageService.YES_OPTION;
        return MessageService.NO_OPTION;
    }
	/**
	 * Displays a message to the user and returns 
	 * MessageService.YES_OPTION if the user selects yes and
	 * MessageService.NO_OPTION if the user selects no.  Stores
	 * the default response in IntSetting default.
	 *
	 * @param message the message to display to the user
	 * @param defValue the IntSetting to store/retrieve the the default
	 *  value for this question.
	 */ 
	final int showYesNoMessage(String message, IntSetting defValue) {
	    // if default has a valid value, use it.
	    if(defValue.getValue() == YES_OPTION || defValue.getValue() == NO_OPTION)
	        return defValue.getValue();
	        
        // We only get here if the default didn't have a valid value.	        
		int option;
		try {
		    option =
		        JOptionPane.showConfirmDialog(getParentComponent(),
                    alwaysUseThisAnswerLabel(message, defValue),
                    GUIMediator.getStringResource("MESSAGE_CAPTION"),
                    JOptionPane.YES_NO_OPTION);
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // happens occasionally on windows, assume no.
            option = JOptionPane.NO_OPTION;
        } catch(InternalError ie) {
            // happens occasionally, assume no.
            option = JOptionPane.NO_OPTION;
        }
                
        int ret;                
		if(option == JOptionPane.YES_OPTION)
		    ret = MessageService.YES_OPTION;
        else
            ret = MessageService.NO_OPTION;
            
        // If we wanted to remember the answer, remember it.            
        if ( defValue.getValue() == REMEMBER_ANSWER )
            defValue.setValue(ret);
        else
            defValue.setValue(FORGET_ANSWER);
            
        return ret;
	}
	
	/**
	 * Displays a message to the user and returns 
	 * MessageService.YES_OPTION if the user selects yes and
	 * MessageService.NO_OPTION if the user selects no.
	 * MessageService.CANCEL_OPTION if the user selects cancel.
	 *
	 * @param message the message to display to the user
	 */ 
	final int showYesNoCancelMessage(String message) {
		int option;
		try {
		    option =
		        JOptionPane.showConfirmDialog(getParentComponent(), 
							  new MultiLineLabel(message), 
							  GUIMediator.getStringResource("MESSAGE_CAPTION"),
							  JOptionPane.YES_NO_CANCEL_OPTION );
        } catch(InternalError ie) {
            // happens occasionally, assume no.
            option = JOptionPane.NO_OPTION;
        }
            
		if(option == JOptionPane.YES_OPTION) return MessageService.YES_OPTION;
		else if(option == JOptionPane.NO_OPTION) return MessageService.NO_OPTION;
		return MessageService.CANCEL_OPTION;
	}
	
	/**
	 * Displays a message to the user and returns 
	 * MessageService.YES_OPTION if the user selects yes and
	 * MessageService.NO_OPTION if the user selects no.
	 * MessageService.CANCEL_OPTION if the user selects cancel.  Stores
	 * the default response in IntSetting default.
	 *
	 * @param message the message to display to the user
	 * @param defValue the IntSetting to store/retrieve the the default
	 *  value for this question.
	 */ 
	final int showYesNoCancelMessage(String message, IntSetting defValue) {
	    // if default has a valid value, use it.
	    if (defValue.getValue() == YES_OPTION || defValue.getValue() == NO_OPTION ||
	       defValue.getValue() == CANCEL_OPTION)
	        return defValue.getValue();
	        
        // We only get here if the default didn't have a valid value.	        
		int option;
		try {
		    option =
		        JOptionPane.showConfirmDialog(getParentComponent(),
                    alwaysUseThisAnswerLabel(message, defValue),
                    GUIMediator.getStringResource("MESSAGE_CAPTION"),
                    JOptionPane.YES_NO_CANCEL_OPTION );
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            // happens occasionally on windows, assume cancel.
            option = JOptionPane.CANCEL_OPTION;
        } catch(InternalError ie) {
            // happens occasionally, assume cancel.
            option = JOptionPane.CANCEL_OPTION;
        }
                
        int ret;                
		if (option == JOptionPane.YES_OPTION)
		    ret = MessageService.YES_OPTION;
        else if (option == JOptionPane.NO_OPTION)
            ret = MessageService.NO_OPTION;
        else
            ret = MessageService.CANCEL_OPTION;
            
        // If we wanted to remember the answer, remember it.            
        if (defValue.getValue() == REMEMBER_ANSWER && ret != MessageService.CANCEL_OPTION)
            defValue.setValue(ret);
        else
            defValue.setValue(FORGET_ANSWER);
            
        return ret;
	}	

	/**
	 * Convenience method for determining which window should be the parent
	 * of message windows.
	 *
	 * @return the <tt>Component</tt> that should act as the parent of message
	 *  windows
	 */
	public static Component getParentComponent() {
		if(GUIMediator.isOptionsVisible()) 
			return GUIMediator.getMainOptionsComponent();
		return GUIMediator.getAppFrame();
	}
    
    private final JComponent doNotDisplayAgainLabel(
      final String message, final BooleanSetting setting) {
        JPanel thePanel = new JPanel( new BorderLayout(0, 15) ); 
        JCheckBox option = new JCheckBox(
            GUIMediator.getStringResource("OPTIONS_DO_NOT_ASK_AGAIN")
        );
        MultiLineLabel lbl = new MultiLineLabel(message);
        thePanel.add( lbl, BorderLayout.NORTH );
        thePanel.add( option, BorderLayout.WEST );
        option.addItemListener( new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setting.setValue( e.getStateChange() == ItemEvent.SELECTED );
            }
        });
        return thePanel;
    }
    
    private final JComponent alwaysUseThisAnswerLabel(
      final String message, final IntSetting setting) {
        JPanel thePanel = new JPanel( new BorderLayout(0, 15) ); 
        JCheckBox option = new JCheckBox(
            GUIMediator.getStringResource("OPTIONS_ALWAYS_USE_ANSWER")
        );
        MultiLineLabel lbl = new MultiLineLabel(message);
        thePanel.add( lbl, BorderLayout.NORTH );
        thePanel.add( option, BorderLayout.WEST );
        option.addItemListener( new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if ( e.getStateChange() == ItemEvent.SELECTED )
                    setting.setValue( REMEMBER_ANSWER );
                else
                    setting.setValue( FORGET_ANSWER );
            }
        });
        return thePanel;
    }    
}
