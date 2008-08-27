
package com.limegroup.gnutella.gui.notify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import com.limegroup.gnutella.gui.GUIMediator;


public class LinuxNotifyUser implements NotifyUser {
	
	private final SystemTray _tray;
	private TrayIcon _icon;
	private final JPopupMenu _menu;
	
	public LinuxNotifyUser() {
		_tray = SystemTray.getDefaultSystemTray();
		_menu = buildPopupMenu();
		
		buildTrayIcon(GUIMediator.getStringResource("TRAY_TOOLTIP"),"limeicon");
	}

	private void buildTrayIcon(String desc, String imageFileName) {
        //String tip = "LimeWire: Running the Gnutella Network";
        _icon = new TrayIcon(GUIMediator.getThemeImage(imageFileName), desc, _menu);
        
    	// left click restores.  This happens on the awt thread.
        _icon.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		GUIMediator.restoreView();
        	}
        });
        
        _icon.setIconAutoSize(true);
	}
	
	private JPopupMenu buildPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		// restore
		JMenuItem item = new JMenuItem(GUIMediator.getStringResource("TRAY_RESTORE_LABEL"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.restoreView();
			}
		});
		menu.add(item);
		
		menu.addSeparator();
		
		// about box
		item = new JMenuItem(GUIMediator.getStringResource("TRAY_ABOUT_LABEL"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.showAboutWindow();
			}
		});
		menu.add(item);
		
		menu.addSeparator();
		
		//exit after transfers
		item = new JMenuItem(GUIMediator.getStringResource("TRAY_EXIT_LATER_LABEL"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.shutdownAfterTransfers();
			}
		});
		menu.add(item);
		
		// exit
		item = new JMenuItem(GUIMediator.getStringResource("TRAY_EXIT_LABEL"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIMediator.shutdown();
			}
		});
		menu.add(item);
		
		return menu;
	}
	
	public void addNotify() {
        _tray.addTrayIcon(_icon);
	}

	public void removeNotify() {
		_tray.removeTrayIcon(_icon);
	}

	public void updateNotify(String imageFileName, String desc) {
		removeNotify();
		buildTrayIcon(desc,imageFileName);
		_tray.addTrayIcon(_icon);
	}

	public void updateDesc(String desc) {
		removeNotify();
		_icon.setCaption(desc);
		addNotify();
	}

	public void updateImage(String imageFileName) {
		removeNotify();
		ImageIcon i = new ImageIcon(LinuxNotifyUser.class.getResource(imageFileName));
		_icon.setIcon(i);
		addNotify();
	}

	public void hideNotify() {
		removeNotify();
	}
}
