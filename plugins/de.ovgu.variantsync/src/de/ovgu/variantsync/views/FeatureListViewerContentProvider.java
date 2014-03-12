package de.ovgu.variantsync.views;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.variantsync.model.ResourceChangesFilePatch;

/**
 * @author Lei Luo
 * 
 */
public class FeatureListViewerContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<String> result = new ArrayList<String>();
		ResourceChangesFilePatch filePatch = ResourceChangesView.getDefault()
				.getSelectedFilePatch();
		IProject project = filePatch.getProject();
		ProjectSelectionDialog dlg = ProjectSelectionDialog.getDefault();
		Set<Feature> features = dlg.getFeaturesFor(project);
		Feature rootFeature = null;
		for (Feature f : features) {
			if (f.isRoot()) {
				rootFeature = f;
				break;
			}
		}
		features.remove(rootFeature);
		for (Feature f : features) {
			result.add(f.getName());
		}
		return result.toArray();
	}
}
