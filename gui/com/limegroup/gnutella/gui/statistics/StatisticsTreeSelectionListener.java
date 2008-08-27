package com.limegroup.gnutella.gui.statistics;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * This class handles the selection of nodes in the statistics tree 
 * constroller.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
final class StatisticsTreeSelectionListener implements TreeSelectionListener {
	
	/**
	 * Handle to the <code>JTree</code> instance that utilizes this listener.
	 */
	private JTree _tree;

	/**
	 * Sets the <code>JTree</code> reference that utilizes this listener.
	 *
	 * @param tree the <code>JTree</code> instance that utilizes this listener
	 */
	StatisticsTreeSelectionListener(final JTree tree) {
		_tree = tree;
	}

	/**
	 * Implements the <code>TreeSelectionListener</code> interface.
	 * Takes any action necessary for responding to the selection of a 
	 * node in the tree.
	 *
	 * @param e the <code>TreeSelectionEvent</code> object containing
	 *          information about the selection
	 */
	public void valueChanged(TreeSelectionEvent e) {	
		Object obj = _tree.getLastSelectedPathComponent();
		if(obj instanceof StatisticsTreeNode) {
			StatisticsTreeNode node = (StatisticsTreeNode)obj;
			StatisticsPane pane = node.getStatsPane();

			// only leaf nodes have corresponding panes to display
			if(pane.display())
				StatisticsMediator.instance().handleSelection(node.getTitleKey());
		}
	}
}
