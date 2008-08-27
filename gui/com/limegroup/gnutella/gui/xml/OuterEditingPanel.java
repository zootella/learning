package com.limegroup.gnutella.gui.xml;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Serves as a place holder for a Editing Panel
 * @author Sumeet Thadani
 */
public class OuterEditingPanel extends JPanel{
    private JScrollPane scrollPane;
    protected InputPanel content;
    
    public OuterEditingPanel(InputPanel content){
        this.content = content;
        setLayout(new BorderLayout());
        if(content!=null){
            scrollPane = new JScrollPane(content);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBorder(null);
            add(scrollPane,BorderLayout.CENTER);
        }
    }
    
    public void setContent(InputPanel newContent){
        if(content!=null)
            remove(scrollPane);
        content = newContent;
        if(content!=null){
            scrollPane = new JScrollPane(content);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBorder(null);
            add(scrollPane,BorderLayout.CENTER);
        }
    }
    
    protected InputPanel getContentPanel(){
        return content;
    }
}
