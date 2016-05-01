package de.ovgu.variantsync.presentationlayer.view.mergeprocess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.presentationlayer.view.resourcechanges.ResourceChangesView;

/**
 * @author Lei Luo
 * 
 */
public class FeatureListViewerContentProvider implements
		IStructuredContentProvider {

	@Override
	public void dispose() {
		// not necessary
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// not necessary
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<String> result = new ArrayList<String>();
		ResourceChangesFilePatch filePatch = ResourceChangesView.getDefault()
				.getSelectedFilePatch();
		IProject project = filePatch.getProject();
		ProjectSelectionDialog dlg = ProjectSelectionDialog.getDefault();
		Set<Feature> features = dlg.getFeatures(project);
		if (features == null) {
			features = new HashSet<Feature>();
		}
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
