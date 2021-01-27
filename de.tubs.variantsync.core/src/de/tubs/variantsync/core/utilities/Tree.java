package de.tubs.variantsync.core.utilities;

/**
 * Utility class for building treeviewer trees, see also {@link TreeNode}
 * 
 * @author Christopher Sontag
 */
public class Tree {

	private TreeNode root;

	public Tree() {
		super();
	}

	public TreeNode getRoot() {
		return this.root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public int getNumberOfNodes() {
		int numberOfNodes = 0;

		if (root != null) {
			numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; // 1 for the root!
		}

		return numberOfNodes;
	}

	private int auxiliaryGetNumberOfNodes(TreeNode node) {
		int numberOfNodes = node.getNumberOfChildren();

		for (TreeNode child : node.getChildren()) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}

	public boolean exists(Object dataToFind) {
		return (find(dataToFind) != null);
	}

	public TreeNode find(Object dataToFind) {
		TreeNode returnNode = null;

		if (root != null && dataToFind != null) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	public TreeNode find(TreeNode start, Object dataToFind) {
		TreeNode returnNode = null;

		if (start != null) {
			returnNode = auxiliaryFind(start, dataToFind);
		}

		return returnNode;
	}

	private TreeNode auxiliaryFind(TreeNode currentNode, Object dataToFind) {
		TreeNode returnNode = null;
		int i = 0;

		if (currentNode == null || dataToFind == null)
			return null;

		if (currentNode.getData() != null) {
			if (currentNode.getData().equals(dataToFind)) {
				returnNode = currentNode;
			}
		}

		if (currentNode.hasChildren()) {
			i = 0;
			while (returnNode == null && i < currentNode.getNumberOfChildren()) {
				returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
				i++;
			}
		}

		return returnNode;
	}

	public boolean isEmpty() {
		return (root == null);
	}

	@Override
	public String toString() {
		if (root != null) {
			int i = 0;
			TreeNode currentNode = root;
			String ret = root.toString() + "\n";
			while (currentNode == null && i < currentNode.getNumberOfChildren()) {
				currentNode = currentNode.getChildAt(i);
				ret = ret + currentNode.toString() + "\n";
				i++;
			}
			return ret;
		}
		return "";
	}
}
