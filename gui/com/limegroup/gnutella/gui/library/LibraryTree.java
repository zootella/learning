package com.limegroup.gnutella.gui.library;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.limegroup.gnutella.FileDesc;
import com.limegroup.gnutella.FileManagerEvent;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.RouterService;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.actions.ConfigureOptionsAction;
import com.limegroup.gnutella.gui.actions.ShareFileSpeciallyAction;
import com.limegroup.gnutella.gui.actions.ShareNewFolderAction;
import com.limegroup.gnutella.gui.playlist.PlaylistMediator;
import com.limegroup.gnutella.gui.search.NamedMediaType;
import com.limegroup.gnutella.gui.tables.DefaultMouseListener;
import com.limegroup.gnutella.gui.tables.DragManager;
import com.limegroup.gnutella.gui.tables.FileTransfer;
import com.limegroup.gnutella.gui.tables.MouseObserver;
import com.limegroup.gnutella.gui.themes.ThemeFileHandler;
import com.limegroup.gnutella.settings.FileSetting;
import com.limegroup.gnutella.settings.QuestionsHandler;
import com.limegroup.gnutella.settings.SharingSettings;
import com.limegroup.gnutella.util.StringUtils;

/**
 * This class forms a wrapper around the tree that controls navigation between
 * shared folders. It constructs the tree and supplies access to it. It also
 * controls tree directory selection, deletion, etc.
 */
// 2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class LibraryTree extends JTree implements MouseObserver {

    private static final Log LOG = LogFactory.getLog(LibraryTree.class);
	
	
	///////////////////////////////////////////////////////////////////////////
	//  Nodes
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constant for the root node of the tree.
	 */
	private final LibraryTreeNode ROOT_NODE = 
		new LibraryTreeNode(new RootNodeDirectoryHolder(""));
	private RootSharedFilesDirectoryHolder rsfdh = new RootSharedFilesDirectoryHolder();

	/** Constant for the tree model. */
	private final DefaultTreeModel TREE_MODEL = new DefaultTreeModel(ROOT_NODE);
		
	/** The saved files node. */
    private LibraryTreeNode savedFilesNode;
	private final SavedFilesDirectoryHolder sfdh = new SavedFilesDirectoryHolder(
			SharingSettings.DIRECTORY_FOR_SAVING_FILES, 
		    GUIMediator.getStringResource("LIBRARY_TREE_SAVED_DIRECTORY"));

	/** The shared files node. It's an empty meta node. */
	private LibraryTreeNode sharedFilesNode;
	
    /** The incomplete node. */
    private LibraryTreeNode incompleteFilesNode;
	private final IncompleteDirectoryHolder idh = new IncompleteDirectoryHolder();
	
	/** The individually shared files node. */
	private LibraryTreeNode speciallySharedFilesNode;
	private final SpeciallySharedFilesDirectoryHolder ssfdh = new SpeciallySharedFilesDirectoryHolder();

	
	///////////////////////////////////////////////////////////////////////////
	//  Singleton Pattern
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Singleton instance of this class.
	 */
	private static final LibraryTree INSTANCE = new LibraryTree();
    
	/**
	 * @return the <tt>LibraryTree</tt> instance
	 */
	public static LibraryTree instance() { return INSTANCE; }

	/**
	 * Constructs the tree and its primary listeners,visualization options,
	 * editors, etc.
	 */
	private LibraryTree() {	
		setModel(TREE_MODEL);
		setRootVisible(false);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setEditable(false);
		setInvokesStopCellEditing(true);
		setShowsRootHandles(true);	
		putClientProperty("JTree.lineStyle", "None");
		setCellRenderer(new LibraryTreeCellRenderer());
		ToolTipManager.sharedInstance().registerComponent(this);

		makePopupMenu();
		addMouseListener(new DefaultMouseListener(this));

		//  1. add shared node
		sharedFilesNode = new LibraryTreeNode(rsfdh);
		addNode(ROOT_NODE, sharedFilesNode);
		
		//  -> add specially shared sub-node
		speciallySharedFilesNode = new LibraryTreeNode(ssfdh);
		
		//  2. add saved node
		savedFilesNode = new LibraryTreeNode(sfdh);
		addNode(ROOT_NODE, savedFilesNode);
		
		//  -> add media types under saved node
		addPerMediaTypeDirectories();
		
		//  3. add incomplete node
		incompleteFilesNode = new LibraryTreeNode(idh);
		addNode(ROOT_NODE, incompleteFilesNode);
		
		updateTheme();
		
		DragManager.install(this);
	
		addTreeSelectionListener(new LibraryTreeSelectionListener());
	}

	/**
	 * Adds a child node to the parent making sure the event is propagated 
	 * to the tree.
	 * @param parent
	 * @param child
	 * @param expand whether or not to expand the parent node so that the child
	 * is visible
	 */
	private void addNode(LibraryTreeNode parent, LibraryTreeNode child, boolean expand) {
		//  if parent already has child, expand (if necessary) and return
		if (parent.getIndex(child) != -1) {
			if (expand)
				expandPath(new TreePath(parent.getPath()));
			return;
		}
		
		//  insert shared folders alphabetically (and before the individually shared folder)
		int children = parent.getChildCount();
		int insert = 0;
		
		// If it wasn't a 'SharedFilesDirectoryHolder', don't even try to sort, 'cause we don't care.
		if(!(child.getDirectoryHolder() instanceof SharedFilesDirectoryHolder)) {
		    insert = children;
		} else {
    		for(; insert < children; insert++) {
    		    File f = ((LibraryTreeNode)parent.getChildAt(insert)).getFile();
    		    if(f == null || StringUtils.compareFullPrimary(f.getName(), child.getFile().getName()) >= 0)
    		        break;
    		}
        }
        
		TREE_MODEL.insertNodeInto(child, parent, insert);
		
		if (expand || (parent == sharedFilesNode && !isExpanded(new TreePath(sharedFilesNode.getPath()))))
			expandPath(new TreePath(parent.getPath()));
	}
	
	private void addNode(LibraryTreeNode parent, LibraryTreeNode child) {
		addNode(parent, child, false);
	}
	
	/**
	 * Removes the child node from the parent node.  Does nothing if child not a child of parent.
	 */
	private void removeNode(LibraryTreeNode parent, LibraryTreeNode child) {
		if (parent == null || child == null)
			return;
		if (parent.getIndex(child) == -1)
			return;
		TREE_MODEL.removeNodeFromParent(child);
	}

	private void addPerMediaTypeDirectories() {
		for (Iterator i = NamedMediaType.getAllNamedMediaTypes().iterator(); i.hasNext(); ) {
			NamedMediaType nm = (NamedMediaType)i.next();
			if (nm.getMediaType().getMimeType().equals(MediaType.SCHEMA_ANY_TYPE))
				continue;
			
			FileSetting fs = SharingSettings.getFileSettingForMediaType(nm.getMediaType());
			DirectoryHolder dh =
			    new MediaTypeSavedFilesDirectoryHolder(fs, nm.getName(), nm.getMediaType());
			LibraryTreeNode node = new LibraryTreeNode(dh);
			addNode(savedFilesNode, node, true);
		}
	}
	
	// inherit doc comment
	public void updateTheme() {
		Color tableColor = ThemeFileHandler.TABLE_BACKGROUND_COLOR.getValue();
		setBackground(tableColor);
		setCellRenderer(new LibraryTreeCellRenderer());
	}
	
	/**
	 * Sets the initial selection to the Saved Files folder.
	 */
	public void setInitialSelection() {
		TreePath tp = new TreePath(savedFilesNode.getPath());
		setSelectionPath(tp);
	}

	/**
	 * Adds the visual representation of this folder to the library.
	 * 
	 * @param dir
	 *            the <tt>File</tt> instance denoting the abstract pathname of
	 *            the new shared directory to add to the library.
	 */
	private void addSharedDirectory(File dir) {
		SharedFilesDirectoryHolder dh = new SharedFilesDirectoryHolder(dir);
		
		LibraryTreeNode current = new LibraryTreeNode(dh);

		// See if this is the parent of any existing nodes.  If so, 
		// redirect that node to be here.
		int children = sharedFilesNode.getChildCount();
		for(int i = children - 1; i >= 0; i--) {
			LibraryTreeNode child = (LibraryTreeNode)sharedFilesNode.getChildAt(i);
			File f = child.getFile();
			if(f != null && dir.equals(f.getParentFile())) {
				TREE_MODEL.removeNodeFromParent(child);
				addNode(current, child);
			}
		}

        // Add this into the correct position.
		File parent = dir.getParentFile();
		LibraryTreeNode parentNode = null;
		if (parent != null) {
			 parentNode = getNodeForFolder(parent, sharedFilesNode);
		}			
		if (parentNode == null)
		    parentNode = sharedFilesNode;

		addNode(parentNode, current);
	}

	/**
	 * Handles events created by the FileManager. Adds or removes nodes from the
	 * tree as necessary.
	 */
	public void handleFileManagerEvent(final FileManagerEvent evt) {
	    switch(evt.getKind()) {
	    case FileManagerEvent.ADD:
		    // If this was an individually shared file, add that node.
		    if(ssfdh.accept(evt.getFileDescs()[0].getFile()))
		        addNode(sharedFilesNode, speciallySharedFilesNode, true);
            break;
        case FileManagerEvent.REMOVE:
			//  hide individually shared files node if no individually shared files exist
			if (ssfdh.isEmpty()) {
				//  change selection to saved files
				if (ssfdh == getSelectedDirectoryHolder())
					setSelectionPath(new TreePath(savedFilesNode.getPath()));
				removeNode(sharedFilesNode, speciallySharedFilesNode);				
			}
			break;
	    case FileManagerEvent.ADD_FOLDER:
	        File[] files = evt.getFiles();
	        addSharedDirectory(files[0]);
	        break;
	    case FileManagerEvent.REMOVE_FOLDER:
	        File removed = evt.getFiles()[0];
	        removeFolder(removed);
	        break;
		}
	}
	
	/**
	 * Removes the given folder from the list of shared folders.
	 *
	 * If there are any children of this node when it is removed, they
	 * are moved up to be children of the 'Shared Folder' node.
	 *
	 * This 100% relies on the fact that FileManager.removeFolder sends events
	 * from the children first.
	 */
	void removeFolder(File folder) {
	    LibraryTreeNode node = getNodeForFolder(folder, sharedFilesNode);
	    if(node == null)
	        return;
	        
        if(getSelectedNode() == node)
	        setSelectionPath(new TreePath(sharedFilesNode.getPath()));	            

        int childCount = node.getChildCount();
	    for(int i = childCount - 1; i >= 0; i--) {
	        // Move any leftover children to be children of sharedFiles.
            LibraryTreeNode child = (LibraryTreeNode)node.getChildAt(i);
            TREE_MODEL.removeNodeFromParent(child);
            addNode(sharedFilesNode, child);
        }
        
        // Remove this node.
        TREE_MODEL.removeNodeFromParent(node);
    }
    
    /**
     * Gets the LibraryTreeNode that represents this folder.
     */
    LibraryTreeNode getNodeForFolder(File folder, LibraryTreeNode parent) {
        int children = parent.getChildCount();
        for(int i = children - 1; i >= 0; i--) {
            LibraryTreeNode child = (LibraryTreeNode)parent.getChildAt(i);
            File childFile = child.getFile();
            if(childFile != null) {
                if(childFile.equals(folder))
                    return child;
                if(child.isAncestorOf(folder))
                    return getNodeForFolder(folder, child);
            }
        }
        return null;
    }
	
    /**
	 * Adds files to the playlist recursively.
	 */
    void addPlayListEntries() {
		if (incompleteDirectoryIsSelected() || !GUIMediator.isPlaylistVisible())
			return;

		final DirectoryHolder dh = getSelectedDirectoryHolder();
		if (dh == null)
			return;
		
		if (PlaylistMediator.instance() == null)
			return;
		
		GUIMediator.instance().schedule(new Runnable() {
	        public void run() {
				PlaylistMediator pm = GUIMediator.getPlayList();
                if(pm == null) {
					return;
                }
				ArrayList list = new ArrayList();
                File[] files = dh.getFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().endsWith("mp3") || 
							files[i].getName().endsWith("ogg")) {
						list.add(files[i]);
					}
				}
				if (!list.isEmpty()) {
					pm.addFilesToPlaylist((File[])list.toArray(new File[0]));
				}
            }
        });
    }
	
	/**
	 * Returns true if the given node is in the Shared Files subtree. 
	 */
	private boolean canBeUnshared(LibraryTreeNode node) {
		if (node == null)
			return false;
		if (node == speciallySharedFilesNode)
			return false;
		if (node == incompleteFilesNode)
			return false;
		if (node == sharedFilesNode)
			return false;
		if (node.getParent() == null)
			return false;
		
		if (node.getParent() == sharedFilesNode)
			return true;
		
		return canBeUnshared((LibraryTreeNode)node.getParent());
	}

	/**
	 * Returns false in the following cases:
	 * <ul>
	 * <li>The node represents the incomplete directory.
	 * <li>The directory behind the node is null.
	 * <li>The directory is already shared either explicitly or recursively 
	 * because its parent is shared.
	 * </ul>
	 * @param node
	 * @return
	 */
	private boolean canBeShared(LibraryTreeNode node) {
		if (node == null || node == incompleteFilesNode)
			return false;
		File dir = node.getDirectoryHolder().getDirectory();
		if (dir == null || RouterService.getFileManager().isCompletelySharedDirectory(dir))
			return false;
		return true;
	}
	
	public DirectoryHolder getSelectedDirectoryHolder() {
		TreePath path = getSelectionPath();
		if (path != null)
			return ((LibraryTreeNode)path.getLastPathComponent()).getDirectoryHolder();
		return null;
	}
	
	/**
	 * Returns a boolean indicating whether or not the current mouse drop event
	 * is dropping to the incomplete folder.
	 * 
	 * @param mousePoint
	 *            the <tt>Point</tt> instance representing the location of the
	 *            mouse release
	 * @return <tt>true</tt> if the mouse was released on the Incomplete
	 *         folder, <tt>false</tt> otherwise
	 */
	boolean droppingToIncompleteFolder(Point mousePoint) {
		TreePath path = getPathForLocation(mousePoint.x, mousePoint.y);
		LibraryTreeNode node = (LibraryTreeNode)path.getLastPathComponent();
		return node == incompleteFilesNode;
	}

	/**
	 * Returns the File object associated with the currently selected directory.
	 * 
	 * @return the currently selected directory in the library, or <tt>null</tt>
	 *         if no directory is selected
	 */ 
	File getSelectedDirectory() {
		LibraryTreeNode node = getSelectedNode();
		if (node == null)
			return null;
		return node.getDirectoryHolder().getDirectory();
	}
	
	LibraryTreeNode getSelectedNode() {
		return (LibraryTreeNode)getLastSelectedPathComponent();
	}

	/**
	 * Returns the top-level directories as an array of <tt>File</tt> objects
	 * for updating the shared directories in the <tt>SettingsManager</tt>.
	 * 
	 * @return the array of top-level directories as <tt>File</tt> objects
	 */
	File[] getSharedDirectories() {
		int length = sharedFilesNode.getChildCount();
		ArrayList newFiles = new ArrayList(length);
		// collect all but the child that holds the specially shared files
		for (int i = 0; i < length - 1; i++) {
			LibraryTreeNode node = (LibraryTreeNode)sharedFilesNode.getChildAt(i);
			if (node != speciallySharedFilesNode)
				newFiles.add(node.getDirectoryHolder().getDirectory());
		}
		return (File[])newFiles.toArray(new File[0]);
	}

	/**
	 * Removes all shared directories from the visual display 
	 * and changes the selection if any of them were selected.
	 */ 
	void clear() {
	    boolean selected = false;
		int count = sharedFilesNode.getChildCount();
		// count down, but do not remove node 0
		for (int i = count - 1; i >= 0; i--) {
		    TreeNode node = sharedFilesNode.getChildAt(i);
		    if(node == getSelectedNode())
		        selected = true;
            sharedFilesNode.remove(i);
		}
		
		TREE_MODEL.reload(sharedFilesNode);
		if (selected)
			setSelectionPath(new TreePath(sharedFilesNode));
	}

	/**
	 * Stops sharing the selected folder in the library if there is a folder
	 * selected, if the folder is not the save folder, or if the folder is not a
	 * subdirectory of a "root" shared folder.
	 */
	void unshareLibraryFolder() {
		LibraryTreeNode node = getSelectedNode(); 

		if (node == null)
			return;

		if (incompleteDirectoryIsSelected()) {
			showIncompleteFolderMessage("delete");
		} else if (!canBeUnshared(node)) {
			GUIMediator.showMessage("MESSAGE_CANNOT_UNSHARE_DIRECTORY");
		} else {
			String msgKey = "MESSAGE_CONFIRM_UNSHARE_DIRECTORY";				
			int response = GUIMediator.showYesNoMessage(msgKey, QuestionsHandler.UNSHARE_DIRECTORY);
			if (response != GUIMediator.YES_OPTION)
				return;

            final File file = node.getFile();				
		    GUIMediator.instance().schedule(new Runnable() {
		        public void run() {
		            RouterService.getFileManager().removeFolderIfShared(file);
                }
            });
		}
	}


	/**
	 * Returns whether or not the incomplete directory is selected in the tree.
	 * 
	 * @return <tt>true</tt> if the incomplete directory is selected,
	 *         <tt>false</tt> otherwise
	 */
	boolean incompleteDirectoryIsSelected() {
		return incompleteFilesNode == getSelectedNode();
	}
	
	/**
	 * Returns whether or not the saved directory is selected in the tree.
	 */
	boolean savedDirectoryIsSelected() {
		return isSavedDirectory(getSelectedNode());
	}
	
	boolean sharedFoldersNodeIsSelected() {
		return getSelectedNode() == sharedFilesNode;
	}

	/**
	 * Determines whether the LibraryTreeNode parameter is the holder for the
	 * saved folder.
	 * 
	 * @param holder
	 *            the <tt>LibraryTreeNode</tt> class to check for whether or
	 *            not it is the saved directory
	 * @return <tt>true</tt> if it does contain the saved directory,
	 *         <tt>false</tt> otherwise
	 */
	private boolean isSavedDirectory(LibraryTreeNode node) {
	    return node == savedFilesNode || (node != null && node.getParent() == savedFilesNode);
	}

	/**
	 * Shows a message indicating that a specific action cannot be performed on
	 * the incomplete directory (such as changing its name).
	 * 
	 * @param action
	 *            the error that occurred
	 */
	private void showIncompleteFolderMessage(String action) {
		String key1 = "MESSAGE_INCOMPLETE_DIRECTORY_START";
		String key2 = "MESSAGE_INCOMPLETE_DIRECTORY_END";
		GUIMediator.showError(key1, action, key2);
	}

	/**
	 * Selection listener that changes the files displayed in the table if the
	 * user chooses a new directory in the tree.
	 */
	private class LibraryTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {	
			LibraryTreeNode node = getSelectedNode();

			unshareAction.setEnabled(canBeUnshared(node));
			shareAction.setEnabled(canBeShared(node));
			addDirToPlaylistAction.setEnabled(isEnqueueable());
			
			if (node == null)
				return;
			
			if (node == sharedFilesNode)
				LibraryMediator.showSharedFiles();
			else
				LibraryMediator.updateTableFiles(node.getDirectoryHolder());
		}
	}

	/**
	 * Private class that extends a DefaultMutableTreeNode. Using this class
	 * ensures that the "UserObjects" associated with the tree nodes will always
	 * be File objects.
	 */
	private final class LibraryTreeNode extends DefaultMutableTreeNode
	                                    implements FileTransfer {
		private DirectoryHolder _holder;

		private LibraryTreeNode(DirectoryHolder holder) {
			super(holder);
		    _holder = holder;
		}
		
		public DirectoryHolder getDirectoryHolder() {
			return _holder;
		}

		public File getFile() {
			return _holder.getDirectory();
		}	  
		
		/**
		 * Determines if this Node can be an ancestor of given folder.
		 */
		public boolean isAncestorOf(File folder) {
		    File f = getFile();
		    return f != null && folder.getPath().startsWith(f.getPath());
		}
		
		/**
		 * Determines if this is the direct parent of a given folder.
		 */
		public boolean isParentOf(File folder) {
		    return folder.getParentFile().equals(getFile());
		}
		
		/**
		 * Returns a description of this node.
		 */
		public String toString() {
		    return getClass().getName() + ", file: " + getFile();
        }
	}

	/**
	 * Root node class the extends AbstractFileHolder
	 */
	private class RootNodeDirectoryHolder implements DirectoryHolder {
		
		private String name;
		
		public RootNodeDirectoryHolder(String s) { this.name = s; }
		
		public File getDirectory() { return null; }

		public String getDescription() { return ""; }

		public File[] getFiles() { return new File[0]; }

		public FileDesc[] getFileDescs() { return new FileDesc[0]; }

		public String getName() { return name; }

		public boolean accept(File pathname) { return false; }
		
		public int size() { return 0; }
		
		public Icon getIcon() { return null; }
		
		public boolean isEmpty() { return true; }
	}
	
	private class RootSharedFilesDirectoryHolder extends RootNodeDirectoryHolder {

		public RootSharedFilesDirectoryHolder() {
			super(GUIMediator.getStringResource("LIBRARY_TREE_SHARED_FILES_DIRECTORY"));
		}
		
		public boolean accept(File file) {
			return RouterService.getFileManager().isFileInCompletelySharedDirectory(file);
		}
		
		public Icon getIcon() {
			return GUIMediator.getThemeImage("shared_folder");
		}
	}
	
	private class UnshareAction extends AbstractAction {

		public UnshareAction() {
			putValue(Action.NAME,
					GUIMediator.getStringResource("LIBRARY_TREE_UNSHARE_FOLDER_LABEL"));
		}
		
		public void actionPerformed(ActionEvent e) {
			unshareLibraryFolder();
		}
	}
	
	private class AddDirectoryToPlaylistAction extends AbstractAction {

		public AddDirectoryToPlaylistAction() {
			putValue(Action.NAME,
					GUIMediator.getStringResource("LIBRARY_TREE_TO_PLAYLIST_FOLDER_LABEL"));
		}
		
		public void actionPerformed(ActionEvent e) {
			addPlayListEntries();			
		}
	}
	
	
	private class LibraryTreeCellRenderer extends DefaultTreeCellRenderer {
		
		public LibraryTreeCellRenderer() {
			setOpaque(false);
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean sel, boolean expanded, boolean leaf, int row, 
				boolean focused) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, focused);
			LibraryTreeNode node = (LibraryTreeNode)value;
			DirectoryHolder dh = node.getDirectoryHolder();
			setText(dh.getName());
			setToolTipText(dh.getDescription());
			Icon icon = dh.getIcon();
			if (icon != null) {
				setIcon(icon);
			}
			return this;
		}
	}

	private class ShareAction extends AbstractAction {

		public ShareAction() {
			putValue(Action.NAME, GUIMediator.getStringResource
					("LIBRARY_TREE_SHARE_FOLDER_LABEL"));
		}
		
		public void actionPerformed(ActionEvent e) {
			LibraryMediator.instance().addSharedLibraryFolder(getSelectedDirectory());
		}
	}
	
	/**
	 * Enable enqueue action when non-incomplete, non-shared, and has a playable file. 
	 */
	private boolean isEnqueueable() {
		LibraryTreeNode node = getSelectedNode();
		boolean enqueueable = false;
		if (node != null && node != incompleteFilesNode && node != sharedFilesNode) {
			File[] files = node.getDirectoryHolder().getFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (GUIMediator.isPlaylistVisible() && PlaylistMediator.isPlayableFile(files[i]))
						enqueueable = true;
				}
			}
		}
		return enqueueable;
	}

	/**
	 * Updates the LibraryTree based on whether the player is enabled. 
	 */
	public void setPlayerEnabled(boolean value) {
		addDirToPlaylistAction.setEnabled(isEnqueueable());
	}
	

	///////////////////////////////////////////////////////////////////////////
	//  Popups
	///////////////////////////////////////////////////////////////////////////
	
	/** Constant for the popup menu. */
	private final JPopupMenu DIRECTORY_POPUP = new JPopupMenu();
	private Action shareAction = new ShareAction();
	private Action unshareAction = new UnshareAction();
	private Action addDirToPlaylistAction = new AddDirectoryToPlaylistAction();

	/**
	 * Constructs the popup menu that appears in the tree on a right mouse
	 * click.
	 */
	private void makePopupMenu() {
		DIRECTORY_POPUP.add(new JMenuItem(shareAction));
		DIRECTORY_POPUP.add(new JMenuItem(unshareAction));
        DIRECTORY_POPUP.add(new JMenuItem(addDirToPlaylistAction));
		DIRECTORY_POPUP.addSeparator();
		DIRECTORY_POPUP.add(new JMenuItem(new ShareFileSpeciallyAction()));
		DIRECTORY_POPUP.add(new JMenuItem(new ShareNewFolderAction()));
		
		DIRECTORY_POPUP.addSeparator();
		
		DIRECTORY_POPUP.add(new JMenuItem(new ConfigureOptionsAction(
				"OPTIONS_SHARED_MAIN_TITLE",
				"LIBRARY_SHARED_FILES_CONFIGURE_MENU",
				"LIBRARY_SHARED_FILES_CONFIGURE_EXPLAIN")));
	}

	
	///////////////////////////////////////////////////////////////////////////
	//  MouseObserver implementation
	///////////////////////////////////////////////////////////////////////////

    /**
     * Handles when the mouse is double-clicked.
     */
    public void handleMouseDoubleClick(MouseEvent e) { }
    
    /**
     * Handles a right-mouse click.
     */
    public void handleRightMouseClick(MouseEvent e) { }
    
    /**
     * Handles a trigger to the popup menu.
     */
    public void handlePopupMenu(MouseEvent e) {
		int row = getRowForLocation(e.getX(), e.getY());
		if(row == -1)
			return;

		setSelectionRow(row);
		DIRECTORY_POPUP.show(this, e.getX(), e.getY());
    }

	/**
	 * Sets the tree selection to be the given directory, if it exists.
	 * 
	 * @return true if the directory exists in the tree and could be selected
	 */
	public boolean setSelectedDirectory(File dir) {
		if (dir == null || !dir.isDirectory())
			return false;
		LibraryTreeNode ltn = getNodeForFolder(dir, sharedFilesNode);
		if (ltn == null)
			return false;
		
		setSelectionPath(new TreePath(ltn.getPath()));
		return true;
	}
}


