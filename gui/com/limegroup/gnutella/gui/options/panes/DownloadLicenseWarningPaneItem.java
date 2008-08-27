package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.settings.QuestionsHandler;

/**
 * This class defines the pane in the options window that allows
 * the user to receive or not receive a warning about downloading
 * a file without a license.
 */
public class DownloadLicenseWarningPaneItem extends AbstractPaneItem {

    /**
     * Constant for the key of the locale-specific <code>String</code> for the 
     * download pane check box label in the options window.
     */
    private final String CHECK_BOX_LABEL = 
        "OPTIONS_DOWNLOAD_LICENSE_WARNING_CHECK_BOX_LABEL";
    
    /**
     * Constant for the check box that specifies whether or not downloads 
     * should be automatically cleared.
     */
    private final JCheckBox CHECK_BOX = new JCheckBox();

    /**
     * The stored value to allow rolling back changes.
     */
    private int _skipWarning;

    /**
     * The constructor constructs all of the elements of this 
     * <tt>AbstractPaneItem</tt>.
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public DownloadLicenseWarningPaneItem(final String key) {
        super(key);
        
        // add a labeled component with a glue forcing the component
        // to the right
        LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
                                                     CHECK_BOX,
                                                     LabeledComponent.LEFT_GLUE);
        add(comp.getComponent());
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Sets the options for the fields in this <tt>PaneItem</tt> when the 
     * window is shown.
     */
    public void initOptions() {
        _skipWarning = QuestionsHandler.SKIP_FIRST_DOWNLOAD_WARNING.getValue();
        CHECK_BOX.setSelected(_skipWarning != MessageService.YES_OPTION);
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Applies the options currently set in this window, displaying an
     * error message to the user if a setting could not be applied.
     *
     * @throws IOException if the options could not be applied for some reason
     */
    public boolean applyOptions() throws IOException {
        final boolean skip = !CHECK_BOX.isSelected();
        if (skip) {
            if (_skipWarning != MessageService.YES_OPTION)
                QuestionsHandler.SKIP_FIRST_DOWNLOAD_WARNING.setValue(MessageService.YES_OPTION);
        } else
            if (_skipWarning == MessageService.YES_OPTION)
                QuestionsHandler.SKIP_FIRST_DOWNLOAD_WARNING.setValue(0);
        return false;
    }
    
    public boolean isDirty() {
        final boolean skip = !CHECK_BOX.isSelected();
        if (skip)
            return _skipWarning != MessageService.YES_OPTION;
        else
            return _skipWarning == MessageService.YES_OPTION;
    }  
}
