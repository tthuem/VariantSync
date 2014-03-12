package de.ovgu.variantsync.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ovgu.variantsync.model.ResourceChangesFilePatch;

/**
 * @author Lei Luo
 * 
 */
public class SynchroFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ResourceChangesFilePatch) {
			// if (((ResourceChangesFilePatch) element).isSynchronisiert()) {
			// return false;
			// }
			if (((ResourceChangesFilePatch) element).getSynchoProjects().size() != 0
					|| ((ResourceChangesFilePatch) element).isSynchronisiert()) {
				return false;
			}
		}
		return true;
	}

}
