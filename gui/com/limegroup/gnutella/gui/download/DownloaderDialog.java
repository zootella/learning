package com.limegroup.gnutella.gui.download;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.util.CommonUtils;
import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.MessageService;
import com.limegroup.gnutella.gui.util.CoreExceptionHandler;

/**
 * Handles {@link com.limegroup.gnutella.SaveLocationException SaveLocationExceptions}
 * showing the user the exact cause of the exception and giving them a choice
 * to choose a different download location or overwrite the existing file.
 * <p>
 * The dialog stays visible as long as 
 * {@link com.limegroup.gnutella.gui.download.DownloaderFactory#createDownloader(boolean)}
 * throws exceptions and it has not been cancelled.
 */
public class DownloaderDialog extends JDialog {

	private DownloaderFactory factory;
	private Downloader downloader;
	private JLabel titleLabel = new JLabel();
	private JLabel descLabel = new JLabel();
	private JLabel noteLabel = new JLabel();
	private ButtonRow buttons;
	
	/**
	 * Creates a new dialog for a factory and an already thrown exception.
	 * @param factory
	 * @param sle
	 */
	private DownloaderDialog(DownloaderFactory factory, SaveLocationException sle) {
		this.factory = factory;
		// dialog
		setModal(true);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		GUIUtils.addHideAction((JComponent)getContentPane());
		
		// top panel
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(topPanel, BorderLayout.CENTER);
		
		// main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		topPanel.add(mainPanel, BorderLayout.CENTER);

		Color bg = UIManager.getColor("TextField.background");
		Color fg = UIManager.getColor("TextField.foreground");
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(bg);
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		JLabel icon = new JLabel(GUIMediator.getThemeImage("warning"));
		icon.setBackground(bg);
		icon.setBorder(new EmptyBorder(2, 5, 2, 5));
		titlePanel.add(icon, BorderLayout.WEST);
		titleLabel.setBackground(bg);
		titleLabel.setForeground(fg);
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		
		// labels
		Box labelBox = Box.createVerticalBox();
		mainPanel.add(labelBox, BorderLayout.CENTER);
		labelBox.add(descLabel);
		labelBox.add(Box.createVerticalStrut(10));
		labelBox.add(noteLabel);
		labelBox.add(Box.createVerticalStrut(15));
		labelBox.add(Box.createVerticalGlue());
		mainPanel.add(labelBox, BorderLayout.CENTER);
		
		buttons = new ButtonRow(new Action[] { new OverWriteAction(),
				new SaveAsAction(), new CancelAction() },
				ButtonRow.X_AXIS, ButtonRow.LEFT_GLUE);
		topPanel.add(buttons, BorderLayout.SOUTH);
		
		setContentFromException(sle);
		pack();
	}
	
	
	private void setLabel(JLabel label, String text) {
		StringBuffer buffer = new StringBuffer("<html><table><tr><td width=\"400\">");
		buffer.append(text);
		buffer.append("</td></tr></html>");
		label.setText(buffer.toString());
	}
	
	private void setContentFromException(SaveLocationException sle) {
		
		// special case, close this dialog if visible and show warning dialog
		if (sle.getErrorCode() == SaveLocationException.FILE_ALREADY_DOWNLOADING) {
			dispose();
			DownloaderUtils.showIsAlreadyDownloadingWarning(factory);
			return;
		}
		
		String error = CoreExceptionHandler.getSaveLocationErrorString(sle);
		
		if (sle.getErrorCode() == SaveLocationException.FILE_ALREADY_EXISTS) {
			setTitleLabel(MessageFormat.format(GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_WARNING_LABEL"),
					new Object[] { CoreExceptionHandler.getShortSaveLocationErrorString(sle) }));
			setDescLabel(error);
		}
		else {
			setTitleLabel(MessageFormat.format(GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_ERROR_LABEL"),
					new Object[] { CoreExceptionHandler.getShortSaveLocationErrorString(sle) }));
			setDescLabel(error);
		}
		
		FileDesc desc = DownloaderUtils.getFromLibrary(factory.getURN());
		if (desc != null) {
			setNoteLabel(MessageFormat.format(GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_NOTE_LABEL"),
					new Object[] { desc.getFile() }));
		}
		
		buttons.getButtonAtIndex(0).setVisible
			(sle.getErrorCode() == SaveLocationException.FILE_ALREADY_EXISTS);
	}
	
	private void setTitleLabel(String text) {
		setTitle(text);
		titleLabel.setText(text);
	}
	
	private void setDescLabel(String text) {
		setLabel(descLabel, text);
	}
	
	private void setNoteLabel(String text) {
		setLabel(noteLabel, text);
	}
	
	public static Downloader handle(DownloaderFactory factory,
			SaveLocationException sle) {
		
		if (sle.getErrorCode() == SaveLocationException.FILE_ALREADY_DOWNLOADING) {
			DownloaderUtils.showIsAlreadyDownloadingWarning(factory);
			return null;
		}
		
		DownloaderDialog dlg = new DownloaderDialog(factory, sle);
		dlg.setLocationRelativeTo(MessageService.getParentComponent());
		dlg.setVisible(true);
		return dlg.getDownloader();
	}
	
	/**
	 * Returns the successfully created downloader or <code>null</code> if
	 * the dialog was cancelled.
	 * @return
	 */
	public Downloader getDownloader() {
		return downloader;
	}
	
	private class OverWriteAction extends AbstractAction {
		
		public OverWriteAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_OVERWRITE_ACTION_SHORT_NAME"));
			putValue(Action.SHORT_DESCRIPTION, GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_OVERWRITE_ACTION_SHORT_DESCRIPTION"));
		}

		public void actionPerformed(ActionEvent e) {
			try {
				downloader = factory.createDownloader(true);
				dispose();
			}
			catch (SaveLocationException sle) {
				setContentFromException(sle);
			}
		}
	}
	
	private class SaveAsAction extends AbstractAction {
		
		public SaveAsAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_SAVE_AS_ACTION_NAME"));
			putValue(Action.SHORT_DESCRIPTION, GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_SAVE_AS_ACTION_SHORT_DESCRIPTION"));
		}

		public void actionPerformed(ActionEvent e) {
			File file = DownloaderUtils.showFileChooser(factory, 
					DownloaderDialog.this);
			if (file != null) {
				try {
					factory.setSaveFile(file);
					// OSX's FileDialog box already prompts the user that they're
					// going to be overwriting a file, so we don't need to do that
					// particular check again.
					downloader = factory.createDownloader(CommonUtils.isAnyMac());
					dispose();
				}
				catch (SaveLocationException sle) {
					setContentFromException(sle);
				}
			}
		}
	}
	
	private class CancelAction extends AbstractAction {
		
		public CancelAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("GENERAL_CANCEL_BUTTON_LABEL"));
			putValue(Action.SHORT_DESCRIPTION, GUIMediator.getStringResource
					("DOWNLOADER_DIALOG_CANCEL_ACTION_SHORT_DESCRIPTION"));
		}

		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
