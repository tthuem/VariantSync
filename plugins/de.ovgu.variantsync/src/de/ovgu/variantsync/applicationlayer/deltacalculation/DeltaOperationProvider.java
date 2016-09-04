package de.ovgu.variantsync.applicationlayer.deltacalculation;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.PatchException;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.ui.controller.ControllerProperties;
import difflib.Patch;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class DeltaOperationProvider extends AbstractModel implements
		DeltaOperations {

	private DeltaCalculation deltaCalculation;
	private ExternalDeltaCalculation externalDeltaCalculation;

	public DeltaOperationProvider() {
		super();
		deltaCalculation = new DeltaCalculation();
		externalDeltaCalculation = new ExternalDeltaCalculation();
	}

	@Override
	public String getChanges(ResourceChangesFilePatch filePatch) {
		String changes = deltaCalculation.getChanges(filePatch);
		propertyChangeSupport.firePropertyChange(
				ControllerProperties.UNIFIEDDIFF_PROPERTY.getProperty(), null,
				changes);
		return changes;
	}

	@Override
	public String getUnifieddiff(ResourceChangesFilePatch changedFile) {
		return deltaCalculation.getUnifieddiff(changedFile);
	}

	@Override
	public void createPatch(IResource res) {
		deltaCalculation.createPatch(res);
	}

	@Override
	public Patch getPatch(ResourceChangesFilePatch changedFile) {
		return deltaCalculation.getPatch(changedFile);
	}

	@Override
	public Collection<String> computePatch(Collection<String> content, Patch patch)
			throws PatchException {
		return externalDeltaCalculation.computePatch(content, patch);
	}

	@Override
	public Patch computeDifference(Collection<String> content1, Collection<String> content2) {
		return externalDeltaCalculation.computeDifference(content1, content2);
	}

	@Override
	public List<String> unpatchText(List<String> content, Patch patch) {
		return externalDeltaCalculation.unpatchText(content, patch);
	}

	@Override
	public List<String> createUnifiedDifference(String filename,
			String filename2, List<String> oldCode, Patch patch, int i) {
		return externalDeltaCalculation.createUnifiedDifference(filename,
				filename, oldCode, patch, 0);
	}
}