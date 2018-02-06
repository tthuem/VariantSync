package de.tubs.variantsync.core.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building treeviewer trees, see also {@link Tree}
 * 
 * @author Christopher Sontag
 */
public class TreeNode {

	private Object data;
	private List<TreeNode> children;
	private TreeNode parent;

	public TreeNode() {
		super();
		children = new ArrayList<TreeNode>();
	}

	public TreeNode(Object data) {
		this();
		setData(data);
	}

	public TreeNode getParent() {
		return this.parent;
	}

	public List<TreeNode> getChildren() {
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return (getNumberOfChildren() > 0);
	}

	public void setChildren(List<TreeNode> children) {
		for (TreeNode child : children) {
			child.parent = this;
		}

		this.children = children;
	}

	public void addChild(TreeNode child) {
		child.parent = this;
		children.add(child);
	}

	public void addChildAt(int index, TreeNode child) throws IndexOutOfBoundsException {
		child.parent = this;
		children.add(index, child);
	}

	public void removeChildren() {
		this.children = new ArrayList<TreeNode>();
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public TreeNode getChildAt(int index) throws IndexOutOfBoundsException {
		return children.get(index);
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String toString() {
		return getData() != null ? getData().toString() : "";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TreeNode other = (TreeNode) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

}
