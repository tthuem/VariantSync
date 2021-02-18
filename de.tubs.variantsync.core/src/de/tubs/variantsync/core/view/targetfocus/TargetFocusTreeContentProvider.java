package de.tubs.variantsync.core.view.targetfocus;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.tubs.variantsync.core.utilities.Tree;
import de.tubs.variantsync.core.utilities.TreeNode;

/**
 *
 * ContentProvider for {@link FeatureTree}
 *
 * @author Christopher Sontag
 */
public class TargetFocusTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Tree) {
			return ((Tree) inputElement).getRoot().getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeNode) {
			return ((TreeNode) parentElement).getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode) element).hasChildren();
		}
		return false;
	}

}
