package com.limegroup.gnutella.gui.xml;

/**
 * This panel is the place-holder for the InputPanel in the GUI.
 * It takes a JPanel in the constructor. And adds that JPanel to itself.
 * It also has a method, to re-set the the JPanel. Iin which case it removes 
 * the older JPanel frm Itself and set the sets the new one as the JPanel
 *
 *@author Sumeet Thadani(6/6/01)
 */


public class OuterInputPanel extends OuterEditingPanel {
    
    //constructor
    public OuterInputPanel(InputPanel content){
        super(content);
    }

    public String gatherData(){
        if(content == null )//no schema selected.
            return null;
        return content.getInput();
    }
}

