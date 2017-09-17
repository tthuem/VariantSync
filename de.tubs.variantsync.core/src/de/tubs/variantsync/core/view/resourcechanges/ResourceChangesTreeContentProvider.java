package de.tubs.variantsync.core.view.resourcechanges;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public class ResourceChangesTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			return ((List<IPatch<?>>) inputElement).toArray();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IPatch<?>) {
			return ((IPatch<?>) parentElement).getDeltas().toArray();
		} else if (parentElement instanceof IDelta<?>) {
			List<String> strings = new ArrayList<>();
			strings.add(((IDelta) parentElement).getResource().getFullPath().toString());
			strings.add(((IDelta) parentElement).getFeature());
			strings.add(((IDelta) parentElement).getOriginal().toString());
			strings.add(((IDelta) parentElement).getRevised().toString());
			return strings.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IPatch<?>) {
			return null;
		} else if (element instanceof Long) {
			return null;
		} else if (element instanceof String) {
			return null;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IPatch) {
			return !((IPatch) element).isEmpty();
		} else if (element instanceof IDelta) {
			return true;
		} else if (element instanceof String) {

		}
		return false;
	}

}
