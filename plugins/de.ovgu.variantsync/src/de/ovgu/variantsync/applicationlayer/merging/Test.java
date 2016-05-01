package de.ovgu.variantsync.applicationlayer.merging;
/*
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.compare.structuremergeviewer.DiffElement;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.diffmerge.api.IComparison;
import org.eclipse.emf.diffmerge.api.IMergeSelector;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IDifference;
import org.eclipse.emf.diffmerge.api.scopes.IEditableModelScope;
import org.eclipse.emf.diffmerge.diffdata.EComparison;
import org.eclipse.emf.diffmerge.diffdata.impl.EComparisonImpl;
import org.eclipse.emf.diffmerge.impl.scopes.FragmentedModelScope;
import org.eclipse.emf.diffmerge.ui.util.DiffMergeDialog;
import org.eclipse.emf.diffmerge.ui.viewers.EMFDiffNode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.emf.compare.diff.merge.service.MergeService;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.common.util.URI;

public class Test {

	public static void main(String[] args) {
		IFile file = new File();
		byte[] bytes = "File contents".getBytes();
		InputStream source = new ByteArrayInputStream(bytes);
		file.create(source, IResource.NONE, null);

		// Instantiate the scopes to compare
		IEditableModelScope targetScope = new FragmentedModelScope(null, false); // For
																					// example
		IEditableModelScope referenceScope = new FragmentedModelScope(null,
				false); // For example
		
		IComparison comparison = new EComparisonImpl(targetScope, referenceScope);
		comparison.compute(aMatchPolicy, aDiffPolicy, aMergePolicy, aProgressMonitor);
		
		Collection<IDifference> differences = comparison.getRemainingDifferences();
		
		comparison.merge(new IMergeSelector() {
			  public Role getMergeDirection(IDifference difference) {
			    Role result = null;
			    ... // If difference is relevant, assign result
			    return result;
			  }
			}, true, aProgressMonitor);	
		
		// Turn comparison into an input for viewers
		final EMFDiffNode diffNode = new EMFDiffNode((EComparison)comparison, anEditingDomain);
		// Show the comparison in a dialog
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
		  public void run() {
		    DiffMergeDialog dialog = new DiffMergeDialog(
		        display.getActiveShell(), "Your Title", diffNode);
		    dialog.open();
		  }
		});
		
		// Loading models
		EObject model1 = ModelUtils.load(model1, resourceSet);
		EObject model2 = ModelUtils.load(model2, resourceSet);

		// Matching model elements
		MatchModel match = MatchService.doMatch(model1, model2, Collections.<String, Object> emptyMap());
		// Computing differences
		DiffModel diff = DiffService.doDiff(match, false);
		// Merges all differences from model1 to model2
		List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
		MergeService.merge(differences, true);
		
		// Loading models
		EObject model1 = ModelUtils.load(model1, resourceSet);
		EObject model2 = ModelUtils.load(model2, resourceSet);

		DiffModel diff = CompareUtils.compare(model1, model2, Collections.<String, Object> emptyMap());
		CompareUtils.merge(diff);
	}
}
*/