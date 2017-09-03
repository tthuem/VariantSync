package de.tubs.variantsync.core.view.resourcechanges;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class ResourceChangesTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {

		} else if (parentElement instanceof Long) {

		} else if (parentElement instanceof String) {

		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IProject) {
			return null;
		} else if (element instanceof Long) {
			
		} else if (element instanceof String) {

		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IProject) {

		} else if (element instanceof Long) {

		} else if (element instanceof String) {

		}
		return false;
	}

}
