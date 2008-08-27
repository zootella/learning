package com.limegroup.gnutella.gui.xml.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.limegroup.gnutella.FileEventListener;

public abstract class AbstractMetaEditorPanel extends JPanel {

	public abstract boolean checkInput();
	
	public abstract List getInput();
	
	public abstract FileEventListener getFileEventListener();
}
