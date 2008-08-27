package com.limegroup.gnutella.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.limegroup.gnutella.version.UpdateInformation;

/** 
 * Creates a new update panel for prompting the user for whether 
 * or not they would like to update. 
 */
final class UpdateDialog extends JDialog {
	
	/**
	 * Constructs the dialog.
	 */
	public UpdateDialog(UpdateInformation info) {
	    super(GUIMediator.getAppFrame());
	    setModal(true);
	    setResizable(false);
	    setTitle(GUIMediator.getStringResource("UPDATE_DIALOG_CAPTION"));
	    
	    JButton button = buildContentArea(info);
	    pack();
	    Dimension size = new Dimension(500, 300);
	    setSize(size);
	    ((JComponent)getContentPane()).setPreferredSize(size);
        getRootPane().setDefaultButton(button);
        button.requestFocus();
	    
        setLocation(GUIMediator.getScreenCenterPoint(this));
	}
	
	private JButton buildContentArea(UpdateInformation info) {
	    JComponent icon = new URLLabel(info.getUpdateURL(), getUpdateIcon());
		JComponent title = makeText(getUpdateTitle(info.getUpdateTitle()), false);
		JComponent text = makeText(info.getUpdateText(), true);
		JButton button1 = makeButton1(info);
		JButton button2 = makeButton2(info);
		
		JComponent jc = (JComponent)getContentPane();
		jc.setLayout(new GridBagLayout());
		jc.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints gc = new GridBagConstraints();
		
		JPanel p = new JPanel(new GridBagLayout());		
		gc.gridwidth = GridBagConstraints.RELATIVE;
		gc.insets = new Insets(0, 0, 0, 5);
		p.add(icon, gc);
		
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(0, 0, 0, 0);
		p.add(title, gc);

		jc.add(p, gc);
		
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = new Insets(5, 0, 5, 0);
		gc.weightx = 1;
		gc.weighty = 1;
		jc.add(text, gc);
		
		p = new JPanel(new GridBagLayout());
		gc.gridwidth = GridBagConstraints.RELATIVE;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 0, 3);
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.EAST;		
		p.add(button1, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 3, 0, 0);
		p.add(button2, gc);
		
        gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.CENTER;
		jc.add(p, gc);
		
		return button1;
	}
	
	private String getUpdateTitle(String title) {
	    if(title != null)
	        return "<b>" + title + "</b>";
	    else
	        return "<b>" + GUIMediator.getStringResource("UPDATE_TITLE") + "</b>";
	}
	
	private Icon getUpdateIcon() {
	    return GUIMediator.getThemeImage("searching");
	}
	
	private JComponent makeText(String text, boolean scroll) {
	    JEditorPane pane = new JEditorPane();
	    JLabel dummy = new JLabel();
        pane.setContentType("text/html");
        pane.setEditable(false);
        pane.setBackground(dummy.getBackground());
        pane.setFont(dummy.getFont());
        pane.addHyperlinkListener(GUIUtils.getHyperlinkListener());
        // set the color of the foreground appropriately.
        text = updateForeground(dummy.getForeground(), text);
        pane.setText(text);
        pane.setCaretPosition(0);
        if(!scroll)
            return pane;
        
        JScrollPane scroller = new JScrollPane(pane);
        scroller.setPreferredSize(new Dimension(400, 100));
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setBorder(null);
        return scroller;
    }
    
    private String updateForeground(Color color, String html) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String hex = toHex(r) + toHex(g) + toHex(b);
        return "<html><body text='#" + hex + "'>" + html + "</body></html>";
    }
        
        
    /**
     * Returns the int as a hex string.
     */
    private String toHex(int i) {
        String hex = Integer.toHexString(i).toUpperCase();
        if(hex.length() == 1)
            return "0" + hex;
        else
            return hex;
    }
	
	private JButton makeButton1(final UpdateInformation info) {
	    String text = info.getButton1Text();
	    if(text == null)
	        text = GUIMediator.getStringResource("UPDATE_NOW_LABEL");
	    
	    JButton b = new JButton(text);
	    b.setToolTipText(GUIMediator.getStringResource("UPDATE_TIP"));
	    b.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
                
                String updateCommand = info.getUpdateCommand();
                
                if (updateCommand != null) {
                    GUIMediator.flagUpdate(updateCommand);
        
                    int restartNow = 
                        GUIMediator.showYesNoTitledMessage("UPDATE_RESTART","UPDATE_RESTART_TITLE");
                    
                    if (restartNow == MessageService.YES_OPTION)
                        GUIMediator.shutdown();
                    
                } else 
                    GUIMediator.openURL(info.getUpdateURL());
                
                setVisible(false);
                dispose();
	        }
        });
        
        return b;
    }
    
	private JButton makeButton2(final UpdateInformation info) {
	    String text = info.getButton2Text();
	    if(text == null)
	        text = GUIMediator.getStringResource("UPDATE_LATER_LABEL");
	    
	    JButton b = new JButton(text);
	    b.setToolTipText(GUIMediator.getStringResource("UPDATE_TIP"));
	    b.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            setVisible(false);
	            dispose();
	        }
        });
        
        return b;
    }
}