package de.tubs.variantsync.core.view.sourcefocus;

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
public class ProjectTree {

	public static Tree construct(String feature, List<IPatch<?>> patches) {
		Tree tree = new Tree();
		TreeNode root = new TreeNode(feature);
		tree.setRoot(root);

		for (IPatch<?> patch : patches) {
			for (IDelta<?> delta : patch.getDeltas()) {
				if (delta.getFeature().equals(feature)) {
					TreeNode projectNode = tree.find(delta.getProject().getName());
					if (projectNode == null) {
						projectNode = new TreeNode(delta.getProject().getName());
						root.addChild(projectNode);
					}
					TreeNode fileNode = tree.find(projectNode, delta.getResource().getProjectRelativePath());
					if (fileNode == null) {
						fileNode = new TreeNode(delta.getResource().getProjectRelativePath());
						projectNode.addChild(fileNode);
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
