package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.limegroup.gnutella.version.UpdateInformation;
import com.limegroup.gnutella.settings.UpdateSettings;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.BoxPanel;

/** Update options */
public class UpdatePaneItem extends AbstractPaneItem { 
    
    /** button for wanting betas */
    private JRadioButton beta;
    
    /** button for wanting service releases */
    private JRadioButton service;
    
    /** button for wanting major releases */
    private JRadioButton major;

    /** Creates the UpdatePaneItem */
    public UpdatePaneItem(final String key) {
        super(key);
        
        beta = new JRadioButton(GUIMediator.getStringResource("OPTIONS_UPDATE_BETA"));
        service = new JRadioButton(GUIMediator.getStringResource("OPTIONS_UPDATE_SERVICE"));
        major = new JRadioButton(GUIMediator.getStringResource("OPTIONS_UPDATE_MAJOR"));
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(beta);
        bg.add(service);
        bg.add(major);


        JPanel panel = new BoxPanel();
        panel.add(major);
        panel.add(service);
        panel.add(beta);
        
        JPanel outer = new BoxPanel(BoxPanel.X_AXIS);
        outer.add(panel);
        outer.add(Box.createHorizontalGlue());
        
        add(outer);
    }

    /**
     * Applies the options currently set in this <tt>PaneItem</tt>.
     *
     * @throws IOException if the options could not be fully applied
     */
    public boolean applyOptions() throws IOException {
        if(beta.isSelected())
            UpdateSettings.UPDATE_STYLE.setValue(UpdateInformation.STYLE_BETA);
        else if(service.isSelected())
            UpdateSettings.UPDATE_STYLE.setValue(UpdateInformation.STYLE_MINOR);
        else // if beta.isSelected())
            UpdateSettings.UPDATE_STYLE.setValue(UpdateInformation.STYLE_MAJOR);
        return false;
    }
    
    /**
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        int style = UpdateSettings.UPDATE_STYLE.getValue();
        if(style <= UpdateInformation.STYLE_BETA)
            beta.setSelected(true);
        else if(style == UpdateInformation.STYLE_MINOR)
            service.setSelected(true);
        else // if style >= UpdateInformation.STYLE_MAJOR
            major.setSelected(true);
    }
    
    public boolean isDirty() {
        int style = UpdateSettings.UPDATE_STYLE.getValue();
        if(style <= UpdateInformation.STYLE_BETA)
            return !beta.isSelected();
        else if(style == UpdateInformation.STYLE_MINOR)
            return !service.isSelected();
        else // if style >= UpdateInformation.STYLE_MAJOR
            return !major.isSelected();
    }
}
