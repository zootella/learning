package com.limegroup.gnutella.gui.statistics;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * This class acts as a proxy and as a "decorator" for an underlying instance 
 * of a <tt>MutableTreeNode</tt> implementation.<p>
 *
 * This class includes the most of the functionality of a 
 * <tt>DefaultMutableTreeNode</tt>, which it simply wraps, without the 
 * coupling that directly subclassing <tt>DefaultMutableTreeNode</tt>
 * would incur.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public class StatisticsTreeNode implements MutableTreeNode {
	
	/**
	 * Handle to the underlying <tt>MutableTreeNode</tt> implemenation
	 * that calls are forwarded to.
	 */
	private final DefaultMutableTreeNode TREE_NODE = 
		new DefaultMutableTreeNode();

	/**
	 * The key for uniquely identifying this node.
	 */
	private String _titleKey;

	/**
	 * The name of this node as it is displayed to the user.
	 */
	private String _displayName;

	/**
	 * <tt>StatisticsPane</tt> containing display data.
	 */
	private StatisticsPane _pane;

	/**
	 * This constructor sets the values for the name of the node to display 
	 * to the user as well as the constant key to use for uniquely 
	 * identifying this node.
	 *
	 * @param titleKey the key for the name of the node to display to the 
	 *                 user and the unique identifier key for this node
	 *
	 * @param displayName the name of the node as it is displayed to the
	 *                    user
	 */
  	StatisticsTreeNode(final StatisticsPane pane, final String displayName) {
  		_titleKey = pane.getName();
  		_displayName = displayName;
		_pane = pane;
  	}
	
	/**
	 * This constructor sets the values for the name of the node to display 
	 * to the user as well as the constant key to use for uniquely 
	 * identifying this node.
	 *
	 * @param pane the <tt>StatisticsPane</tt> instance containing the 
	 *  display information for this node
	 */
	StatisticsTreeNode(final StatisticsPane pane) {
		_titleKey = pane.getName();
		_displayName = GUIMediator.getStringResource(pane.getName());
		_pane = pane;
	}

    /**
     * Removes <tt>newChild</tt> from its parent and makes it a child of
     * this node by adding it to the end of this node's child array.
	 *
	 * <p>Serves as a proxy for the add method of the wrapped 
	 * <tt>DefaultMutableTreeNode</tt> instance.
     *
     * @param	newChild node to add as a child of this node
     * @exception IllegalArgumentException if <tt>newChild</tt> is null
     * @exception IllegalStateException	if this node does not allow 
	 *            children
     */
	public void add(StatisticsTreeNode newChild) {
		TREE_NODE.add(newChild);
	}

    /**
     * Returns the child <tt>TreeNode</tt> at index 
     * <tt>childIndex</tt>.
     */
    public TreeNode getChildAt(int childIndex) {
		return TREE_NODE.getChildAt(childIndex);
	}

    /**
     * Returns the number of children <tt>TreeNode</tt>s the receiver
     * contains.
     */
    public int getChildCount() {
		return TREE_NODE.getChildCount();
	}

    /**
     * Returns the parent <tt>TreeNode</tt> of the receiver.
     */
    public TreeNode getParent() {
		return TREE_NODE.getParent();
	}

    /**
     * Returns the index of <tt>node</tt> in the receivers children.
     * If the receiver does not contain <tt>node</tt>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node) {
		return TREE_NODE.getIndex(node);
	}

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
		return TREE_NODE.getAllowsChildren();
	}

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf() {
		return TREE_NODE.isLeaf();
	}

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children() {
		return TREE_NODE.children();
	}

    /**
     * Adds <tt>child</tt> to the receiver at <tt>index</tt>.
     * <tt>child</tt> will be messaged with <tt>setParent</tt>.
     */
    public void insert(MutableTreeNode child, int index) {
		TREE_NODE.insert(child, index);
	}

    /**
     * Removes the child at <tt>index</tt> from the receiver.
     */
    public void remove(int index) {
		TREE_NODE.remove(index);
	}

    /**
     * Removes <tt>node</tt> from the receiver. <tt>setParent</tt>
     * will be messaged on <tt>node</tt>.
     */
    public void remove(MutableTreeNode node) {
		TREE_NODE.remove(node);
	}

    /**
     * Resets the user object of the receiver to <tt>object</tt>.
     */
    public void setUserObject(Object object) {
		TREE_NODE.setUserObject(object);
	}

    /**
     * Removes the receiver from its parent.
     */
    public void removeFromParent() {
		TREE_NODE.removeFromParent();
	}

    /**
     * Sets the parent of the receiver to <tt>newParent</tt>.
     */
    public void setParent(MutableTreeNode newParent) {
		TREE_NODE.setParent(newParent);
	}

    /**
     * Returns true if the receiver is the root node.
     */
    public boolean isRoot() {
		return TREE_NODE.isRoot();
	}
	/**
	 * Defines the class' representation as a <tt>String</tt> object, used 
	 * in determining how it is displayed in the <tt>JTree</tt>.
	 *
	 * @return the <tt>String</tt> identifier for the display of this class
	 */
	public String toString() {
		return _displayName;
	}

	/**
	 * Returns the <tt>String</tt> denoting both the title of the node
	 * as well as the unique identifying <tt>String</tt> for the node.
	 */
	public String getTitleKey() {
		return _titleKey;
	}

	/**
	 * Accessor for the <tt>StatisticsPane</tt> that contains display data.
	 *
	 * @return the <tt>StatisticsPane</tt> that contains display data
	 */
	public StatisticsPane getStatsPane() {
		return _pane;
	}
}
