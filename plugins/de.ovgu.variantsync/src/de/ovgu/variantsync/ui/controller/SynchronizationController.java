package de.ovgu.variantsync.ui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.applicationlayer.merging.Merging;

/**
 * Manages synchronization operations and data exchanges between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 * 
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class SynchronizationController extends AbstractController {

	private Merging mergeOperations = ModuleFactory.getMergeOperations();

	public void synchronize(Object[] result, ResourceChangesFilePatch patch) {
		setModelProperty(ControllerProperties.SYNCHRONIZATION_PROPERTY.getProperty(), result, patch);
	}

	public void getSynchronizedProjects(ResourceChangesFilePatch patch) {
		setModelProperty(ControllerProperties.SYNCHRONIZEDPROJECTS_PROPERTY.getProperty(), patch);
	}

	public Collection<String> doAutoSync(Collection<String> left, Collection<String> base, Collection<String> right) {
		/* List<String> mergeResult = */
		return mergeOperations.performThreeWayMerge(base, left, right);
		// return parseStringsToCodeLines(mergeResult);
	}

	public boolean checkSyncConflict(List<CodeLine> ancestor, List<CodeLine> left, List<CodeLine> right) {
		return mergeOperations.checkConflict(parseCodeLinesToString(ancestor), parseCodeLinesToString(left),
				parseCodeLinesToString(right));
	}

	private List<String> parseCodeLinesToString(List<CodeLine> codelines) {
		List<String> list = new ArrayList<String>();
		for (CodeLine cl : codelines) {
			list.add(cl.getCode());
		}
		return list;
	}

	private List<CodeLine> parseStringsToCodeLines(List<String> strings) {
		List<CodeLine> list = new ArrayList<CodeLine>();
		int i = 0;
		for (String s : strings) {
			list.add(new CodeLine(s, i));
			i++;
		}
		return list;
	}
}