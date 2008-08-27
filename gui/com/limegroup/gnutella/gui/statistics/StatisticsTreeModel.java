package com.limegroup.gnutella.gui.statistics;

import java.io.IOException;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * This class creates the <tt>TreeModel</tt> used in the <tt>JTree</tt> of 
 * the statistics pane.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class StatisticsTreeModel {

	/**
	 * Handle to the <tt>DefaultTreeModel</tt> implementation.
	 */
	private DefaultTreeModel _treeModel;

	/**
	 * Constant handle to the root node of the tree.
	 */
	private final StatisticsTreeNode ROOT =
		new StatisticsTreeNode(
			new StatisticsPaneParent(StatisticsMediator.ROOT_NODE_KEY), "");

	/**
	 * The constructor constructs the <tt>MutableTreeNode</tt> instances
	 * as well as the <tt>TreeModel</tt>.
	 */
	StatisticsTreeModel() {
		_treeModel = new DefaultTreeModel(ROOT);
	}

	/**
	 * Adds a new <tt>StatisticsTreeNode</tt> to one of the root node's
	 * children.  This should only be called during tree construction.
	 * The first key cannot denote the root.
	 *
	 * @param parentKey the unique identifying key of the node to add as
	 *  well as the key for the locale-specific name for
	 *  the node as it appears to the user
	 *
	 * @param pane the <tt>StatisticsPane</tt> containing display information
	 *  for this node
	 */
	void addNode(final String parentKey, final StatisticsPane pane) {
		MutableTreeNode newNode = new StatisticsTreeNode(pane);
		MutableTreeNode parentNode;

		try {
			parentNode = getParentNode(ROOT, parentKey);
		} catch (IOException e) {
			e.printStackTrace();
			//the parent node could not be found, so return
			return;
		}
		if (parentNode == null) {
			// this should never happen
			return;
		}

		// insert the new node
		_treeModel.insertNodeInto(newNode,
			parentNode, parentNode.getChildCount());
		_treeModel.reload(parentNode);
	}

	/**
	 * Removes all children from the node with the specified key.
	 *
	 * @param parentKey the key of the parent node whose children should be
	 * removed
	 */
	void removeAllChildren(final String parentKey) {
		try {
			MutableTreeNode parentNode = getParentNode(ROOT, parentKey);
			for(int i=_treeModel.getChildCount(parentNode)-1; 
				i>=0; i--) {
				MutableTreeNode child = 
				    (MutableTreeNode)_treeModel.getChild(parentNode, i);
				_treeModel.removeNodeFromParent(child);
			}
			_treeModel.reload();
		} catch(IOException e) {
			e.printStackTrace();
			// this should never happen -- not much we can do if it does
		}
	}

	/**
	 * This method performs a recursive depth-first search for the
	 * parent node with the specified key.
	 *
	 * @param node the current node to search through
	 * @param parentKey the key that will match the key of the parent node
	 *                  we are searching for
	 * @return the <tt>MutableTreeNode</tt> instance corresponding to
	 *         the specified key, or <tt>null</tt> if it could not be found
	 * @throws IOException if a corresponding key does not exist
	 */
	private final MutableTreeNode getParentNode(MutableTreeNode node,
												final String parentKey) 
		throws IOException {
		if (parentKey == StatisticsMediator.ROOT_NODE_KEY) {
			return ROOT;
		}
		// note that we use the key to denote equality, as each node may
		// have the same visual name, but it will not have the same key
		for (int i = 0, length=node.getChildCount(); i<length; i++) {
			StatisticsTreeNode curNode =
				(StatisticsTreeNode)node.getChildAt(i);
			if (curNode.getTitleKey().equals((parentKey))) {
				return curNode;
			}
			
			MutableTreeNode toReturn = getParentNode(curNode, parentKey);
			if(toReturn != null) return toReturn;
			if(curNode.isRoot() && i==(length-1)) {
				// this means we have looped through all of the nodes
				// without finding the parent key, so throw an exception
				String msg = "Parent node not in statistics tree.";
				throw new IOException(msg);
			}
		}
		
		// this will never happen -- the exception should always be thrown
		return null;
	}

	/**
	 * Returns the wrapped <tt>TreeModel</tt> instance.
	 *
	 * @return the enclosed <tt>TreeModel</tt> instance
	 */
	final TreeModel getTreeModel() {
		return _treeModel;
	}

}
