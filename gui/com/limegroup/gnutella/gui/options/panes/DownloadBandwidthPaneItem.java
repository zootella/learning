package com.limegroup.gnutella.gui.options.panes;

import java.awt.Font;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.limegroup.gnutella.downloader.HTTPDownloader;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.ConnectionSettings;
import com.limegroup.gnutella.settings.DownloadSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change the percentage of bandwidth that is devoted to downloads.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class DownloadBandwidthPaneItem extends AbstractPaneItem {

    /**
     * Constant for the key of the locale-specific <code>String</code> for
     * the label on the component that allows to user to change the setting
     * for this <tt>PaneItem</tt>.
     */
    private final String LABEL_LABEL =
        "OPTIONS_DOWNLOAD_BANDWIDTH_SLIDER_LABEL_LABEL";

    /**
     * Constant for the key of the locale-specific <code>String</code> for
     * the label on the component that allows to user to change the setting
     * for this <tt>PaneItem</tt>.
     */
    private final String SLIDER_MAX_LABEL =
        "OPTIONS_DOWNLOAD_BANDWIDTH_SLIDER_MAX_LABEL";

    /**
     * Constant handle to the <tt>JSlider</tt> that allows the user to
     * specify download bandwidth.
     */
    private final JSlider DOWNLOAD_SLIDER = new JSlider(5, 100);

    /**
     * Constant label for the current estimated bandwidth devoted to
     * downloads.
     */
    private final JLabel SLIDER_LABEL = new JLabel();

    /**
     * The stored value to allow rolling back changes.
     */
    private int _downloadThrottle;

    /**
     * The constructor constructs all of the elements of this
     * <tt>AbstractPaneItem</tt>.
     *
     * @param key the key for this <tt>AbstractPaneItem</tt> that the
     *            superclass uses to generate locale-specific keys
     */
    public DownloadBandwidthPaneItem(final String key) {
        super(key);
        DOWNLOAD_SLIDER.setMajorTickSpacing(10);
        DOWNLOAD_SLIDER.setPaintTicks(true);

        Hashtable labelTable = new Hashtable();
        //JLabel label1 = new JLabel("0%");
        JLabel label2 = new JLabel("1%");
        JLabel label3 = new JLabel("100%");
        Font font = new Font("Helvetica", Font.BOLD, 10);
        //label1.setFont(font);
        label2.setFont(font);
        label3.setFont(font);
        //labelTable.put(new Integer(0), label1);
        labelTable.put(new Integer(1), label2);
        labelTable.put(new Integer(100), label3);
        DOWNLOAD_SLIDER.setLabelTable(labelTable);
        DOWNLOAD_SLIDER.setPaintLabels(true);
        DOWNLOAD_SLIDER.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                handleThrottleLabel();
            }
        });

        LabeledComponent comp =
            new LabeledComponent(LABEL_LABEL, SLIDER_LABEL,
                                 LabeledComponent.LEFT_GLUE);
        add(DOWNLOAD_SLIDER);
        add(getVerticalSeparator());
        add(comp.getComponent());
    }

    /**
     * Changes the label for the download throttling slider based on the
     * slider's current value.
     */
    private void handleThrottleLabel() {
        float value = (float)DOWNLOAD_SLIDER.getValue();
        String labelText = "";
        if(value == 100)
            labelText = GUIMediator.getStringResource(SLIDER_MAX_LABEL);//"Unlimited";
        else {
            Float f = new Float
            ((((float)DOWNLOAD_SLIDER.getValue()/100.0))*
             (float)ConnectionSettings.CONNECTION_SPEED.getValue()/8.f);
            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(2);
            labelText = String.valueOf(formatter.format(f)) + " KB/s";
        }
        SLIDER_LABEL.setText(labelText);
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Sets the options for the fields in this <tt>PaneItem</tt> when the
     * window is shown.
     */
    public void initOptions() {
        _downloadThrottle = DownloadSettings.DOWNLOAD_SPEED.getValue();
        DOWNLOAD_SLIDER.setValue(_downloadThrottle);
        handleThrottleLabel();
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
        final int downloadThrottle = DOWNLOAD_SLIDER.getValue();
        if(downloadThrottle != _downloadThrottle) {
            DownloadSettings.DOWNLOAD_SPEED.setValue(downloadThrottle);
            _downloadThrottle = downloadThrottle;
            HTTPDownloader.applyRate();
        }
        return false;
    }
    
    public boolean isDirty() {
        return DownloadSettings.DOWNLOAD_SPEED.getValue() != DOWNLOAD_SLIDER.getValue();
    }
}
