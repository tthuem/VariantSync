package de.tubs.variantsync.core.view.targetfocus;

import java.util.List;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.Tree;
import de.tubs.variantsync.core.utilities.TreeNode;

/**
 * Wraps the patches to a tree format
 * 
 * @author Christopher Sontag
 */
public class FeatureTree {

	public static Tree construct(String projectName, List<IPatch<?>> patches) {
		Tree tree = new Tree();
		TreeNode root = new TreeNode(projectName);
		tree.setRoot(root);

		for (IPatch<?> patch : patches) {
			for (IDelta<?> delta : patch.getDeltas()) {
				if (!delta.getProject().getName().equals(projectName) && !delta.isSynchronizedProject(projectName)) {
					TreeNode featureNode = tree.find(delta.getContext());
					if (featureNode == null) {
						featureNode = new TreeNode(delta.getContext());
						root.addChild(featureNode);
					}
					TreeNode fileNode = tree.find(featureNode, delta.getResource().getProjectRelativePath());
					if (fileNode == null) {
						fileNode = new TreeNode(delta.getResource().getProjectRelativePath());
						featureNode.addChild(fileNode);
					}
					TreeNode deltaNode = tree.find(fileNode, delta);
					if (deltaNode == null) {
						deltaNode = new TreeNode(delta);
						fileNode.addChild(deltaNode);
					}
				}
			}
		}

		return tree;
	}

}
