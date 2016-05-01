package de.ovgu.variantsync.presentationlayer.controller;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.applicationlayer.merging.IMergeOperations;

/**
 * Manages synchronization operations and data exchanges between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 * 
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class SynchronizationController extends AbstractController {

	private IMergeOperations mergeOperations = ModuleFactory
			.getMergeOperations();

	public void synchronize(Object[] result, ResourceChangesFilePatch patch) {
		setModelProperty(
				ControllerProperties.SYNCHRONIZATION_PROPERTY.getProperty(),
				result, patch);
	}

	public void getSynchronizedProjects(ResourceChangesFilePatch patch) {
		setModelProperty(
				ControllerProperties.SYNCHRONIZEDPROJECTS_PROPERTY
						.getProperty(),
				patch);
	}

	public List<CodeLine> doAutoSync(List<CodeLine> left, List<CodeLine> base,
			List<CodeLine> right) {
		return mergeOperations.doAutoSync(left, base, right);
	}

}