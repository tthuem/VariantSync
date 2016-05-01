package de.ovgu.variantsync.presentationlayer.view.mergeprocess;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.applicationlayer.synchronization.SynchronizationProvider;

/**
 * @author Lei Luo
 * 
 */
public class SynchroFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ResourceChangesFilePatch
				&& (!new SynchronizationProvider().getSynchronizedProjects(
						(ResourceChangesFilePatch) element).isEmpty() || ((ResourceChangesFilePatch) element)
						.isSynchronized())) {
			return false;
		}
		return true;
	}

}
