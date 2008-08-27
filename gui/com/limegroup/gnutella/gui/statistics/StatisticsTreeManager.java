package com.limegroup.gnutella.gui.statistics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import com.limegroup.gnutella.gui.trees.LimeTreeCellRenderer;

/**
 * Manages the <code>JTree</code> instance of the the statistics window.  This
 * class constructs the <tt>TreeModel</tt> and forwards many method calls
 * the the contained <tt>TreeModel</tt>.<p>
 *
 * In addition, this class controls the <tt>Component</tt> that contains
 * the <tt>JTree</tt> instance and provides access to that 
 * <tt>Component</tt>.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class StatisticsTreeManager {
	
	/**
	 * Handle to the main <tt>JScrollPane</tt> instance for the main window
	 * that contains the <tt>JTree</tt>.
	 */
	private JScrollPane _scrollPane;

	/**
	 * Constant handle to the tree model.
	 */
	private final StatisticsTreeModel TREE_MODEL = new StatisticsTreeModel();

	private final JTree TREE = new JTree();

	/**
	 * The constructor constructs the <tt>JTree</tt>, the <tt>TreeModel</tt>,
	 * and the <tt>JScrollPane</tt>.
	 */
	StatisticsTreeManager() {
        TREE.setCellRenderer(new LimeTreeCellRenderer());
		TREE.setEditable(false);
		TREE.setShowsRootHandles(true);	
		TREE.setRootVisible(false);
		// The default lineStyle is None, but should be Angled
		// when there are three or more hierarchical levels
		TREE.putClientProperty("JTree.lineStyle", "Angled");
		TREE.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);	
		TREE.addTreeSelectionListener(
			new StatisticsTreeSelectionListener(TREE));

		TREE.setModel(TREE_MODEL.getTreeModel());

		_scrollPane = new JScrollPane(TREE);
		_scrollPane.getViewport().setBackground(Color.white);
		_scrollPane.setPreferredSize(new Dimension(125, 2000));
		_scrollPane.setMinimumSize(new Dimension(125, 300));
    }

	/**
	 * Sets the size the advanced width to make everything easier to see.
	 */
 	void advancedSize() {
 		_scrollPane.setPreferredSize(new Dimension(200, 2000));
 		_scrollPane.setMinimumSize(new Dimension(200, 300));
 	}

	/**
	 * Sets the size the default width to make everything easier to see.
	 */
 	void defaultSize() {
 		_scrollPane.setPreferredSize(new Dimension(125, 2000));
 		_scrollPane.setMinimumSize(new Dimension(125, 300));
 	}

	/**
	 * Adds a new child node to one of the top-level parent nodes. 
	 * children. Niether key can denote the root.<p>
	 * 
	 * This should only be called during tree construction.
	 * 
	 * @param parentKey the unique identifying key of the node to add as  
	 *  well as the key for the locale-specific name for the node as it 
	 *  appears to the user
	 * @param pane the <tt>StatisticsPane</tt> containing display 
	 *  information for this tree node
	 */
	void addNode(final String parentKey, final StatisticsPane pane) {
		TREE_MODEL.addNode(parentKey, pane);
	}


	/**
	 * Returns the main <code>Component</code> for this class.
	 *
	 * @return a <code>Component</code> instance that is the main component
	 *         for this class
	 */
	Component getComponent() {
		return _scrollPane;
	}

	/**
	 * Removes all children from the node with the specified key.
	 *
	 * @param parentKey the key of the parent node whose children should be
	 * removed
	 */
	void removeAllChildren(final String parentKey) {
		TREE_MODEL.removeAllChildren(parentKey);
	}

	/**
	 * Accessor for the <tt>JTree</tt>.
	 *
	 * @return the <tt>JTree</tt> instance
	 */
	JTree getTree() {
		return TREE;
	}
}
