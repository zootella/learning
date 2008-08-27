/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.limegroup.gnutella.gui.search;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.browser.ExternalControl;
import com.limegroup.gnutella.browser.MagnetOptions;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.gui.MultiLineLabel;
import com.limegroup.gnutella.gui.download.DownloaderUtils;
import com.limegroup.gnutella.util.ProcessingQueue;
import com.limegroup.gnutella.util.StringUtils;


/**
 *
 * This singleton class listens to window activated events and parses the clipboard to see
 * if a magnet uri is present.  If it is it asks the user whether to download the file.
 */
public class MagnetClipboardListener extends WindowAdapter {
	
	private static final Log LOG = LogFactory.getLog(MagnetClipboardListener.class);
	
	private static final MagnetClipboardListener instance = new MagnetClipboardListener();
	
	//the system clipboard
	private final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	//dummy clipboard content
	private final StringSelection empty =new StringSelection("");
		
	private volatile String copiedText;
	
	/**
	 * a thread which parses the clipboard and launches magnet downloads.
	 */
	private final ProcessingQueue clipboardParser = new ProcessingQueue("clipboard parser");
	
	private Runnable parser = new Runnable() {
	    public void run() {
              parseAndLaunch();
	    }
	};
	
	/**
	 * @return true if no errors occurred.  False if we should not try to 
	 * parse the clipboard anymore.
	 */
	private void parseAndLaunch() {
	    Transferable data = null;
    	try{
    	//check if there's anything in the clipboard
    		data = CLIPBOARD.getContents(this);
    	}catch(IllegalStateException isx) {
    		//we can't use the clipboard, give up.
    		return;
    	}
    	
    	//is there anything in the clipboard?
    	if (data==null) 
    		return;
    	
    	//then, check if the data in the clipboard is text
    	if (!data.isDataFlavorSupported(DataFlavor.stringFlavor)) 
    		return;
    		
    	
    	//next, extract the content into a string
    	String contents=null;
    	
    	try{
    		contents = (String)data.getTransferData(DataFlavor.stringFlavor);
    	} catch (IOException iox) {
    		LOG.info("problem occured while trying to parse clipboard, do nothing",iox);
    		return;
    	} catch (UnsupportedFlavorException ufx) {
    		LOG.error("UnsupportedFlavor??",ufx);
    		return;
    	} 
    	
    	//could not extract the clipboard as text.
    	if (contents == null)
    		return;
		
		String copied = copiedText;
		if (copied != null && copied.equals(contents)) {
			// it is the magnet we just created
			return;
		}
    	
    	//check if the magnet is valid
    	final MagnetOptions[] opts = ExternalControl.parseMagnets(contents);
    	if (opts.length == 0)
    		return; //not a valid magnet link
    	
    	//at this point we know we have a valid magnet link in the clipboard.
    	LOG.info("clipboard contains "+ contents);
    	
    	//purge the clipboard at this point
    	purgeClipboard();
    	
    	//get a nicer looking address from the magnet
    	//turns out magnets are very liberal.. so display the whole thing
    	final String address = contents;
		final MagnetOptions[] downloadCandidates = extractDownloadableMagnets(opts);
    	
    	// and fire off the download
    	Runnable r = new Runnable() {
    		public void run() {
				if (downloadCandidates.length > 0 
						&& showStartDownloadsDialog(downloadCandidates)) {

					for (int i = 0; i < downloadCandidates.length; i++) {
						DownloaderUtils.createDownloader(downloadCandidates[i]);
					}
				}
				boolean oneSearchStarted = false;
				for (int i = 0; i < opts.length; i++) {
					if (!opts[i].isDownloadable() 
						&& opts[i].isKeywordTopicOnly() && !oneSearchStarted) {
						String query = StringUtils.createQueryString
							(opts[i].getKeywordTopic());
						SearchInformation info = 
							SearchInformation.createKeywordSearch
							(query, null, MediaType.getAnyTypeMediaType());
						if (SearchMediator.validateInfo(info) 
							== SearchMediator.QUERY_VALID) {
							oneSearchStarted = true;
							SearchMediator.triggerSearch(info);
						}
					}
				}
				GUIMediator.instance().setWindow(GUIMediator.SEARCH_INDEX);
    		}
    	};
   	    SwingUtilities.invokeLater(r);
	}
	
	private MagnetClipboardListener() {
        super();
	}
	
	public static MagnetClipboardListener getInstance() {
		return instance;
	}
	
	/**
	 * Sets the text that is going to be copied to the clipboard from withing 
	 * LimeWire, so that the listener can discern between our own copied magnet 
	 * links and the ones pasted from the outside.
	 * @param text
	 */
	public void setCopiedText(String text) {
		copiedText = text;
	}
	
	/**
	 * ask the clipboard parser to see if there is a magnet.
	 */
	public void windowActivated(WindowEvent e) {
	    clipboardParser.add(parser);
	}
	
	/**
	 * clears the clipboard from the current string  
	 */
	private void purgeClipboard(){
		try {
			CLIPBOARD.setContents(empty, empty);
		}catch(IllegalStateException isx) {
			//do nothing
		}
	}
	
	/**
	 * Extracts magnets that are not keyword topic only magnets
	 * @param magnets
	 * @return
	 */
	private MagnetOptions[] extractDownloadableMagnets(MagnetOptions[] magnets) {
		ArrayList dls = new ArrayList(magnets.length);
		for (int i = 0; i < magnets.length; i++) {
			MagnetOptions magnet = magnets[i];
			if (!magnet.isKeywordTopicOnly()) {
				dls.add(magnets[i]);
			}
		}
		// all magnets are downloadable, return original array
		if (dls.size() == magnets.length) {
			return magnets;
		}
		else {
			return (MagnetOptions[])dls.toArray(new MagnetOptions[0]);
		}
	}

	private boolean showStartDownloadsDialog(MagnetOptions[] opts) {
		
		JList magnetList = new JList(opts);
		magnetList.setCellRenderer(new MagnetOptionsListCellRenderer());
		magnetList.setVisibleRowCount(Math.min(6, opts.length));
		magnetList.setFixedCellWidth(400);
				
		Object[] content = new Object[] {
			new MultiLineLabel(GUIMediator.getStringResource
							   ("DOWNLOAD_MAGNET_DIALOG_MESSAGE"), 400),
			new JScrollPane(magnetList)
		};
	
		int response = JOptionPane.showConfirmDialog
            (MessageService.getParentComponent(), content, 
			 GUIMediator.getStringResource("MESSAGE_CAPTION"),
			 JOptionPane.YES_NO_OPTION);
		
		return response == JOptionPane.YES_OPTION;
	}
	
	private class MagnetOptionsListCellRenderer extends DefaultListCellRenderer {
		
		public Component getListCellRendererComponent(JList list, Object value, 
													  int index, 
													  boolean isSelected, 
													  boolean cellHasFocus) 
		{
			MagnetOptions magnet = (MagnetOptions)value;
			String fileName = magnet.getDisplayName();
			if (fileName == null) {
				fileName = GUIMediator.getStringResource("NO_FILENAME_LABEL");
			}
			super.getListCellRendererComponent(list, fileName, index, false, 
											   false);
			setToolTipText("<html><table width=\"400\"><tr><td>" 
						   + magnet.toString() 
						   + "</td></tr></table></html>");
			return this;
		}
	}
}
