package de.ovgu.variantsync.applicationlayer.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.io.Persistable;

public class UpdateAlgorithm {

	private Persistable persistenceOp = ModuleFactory.getPersistanceOperations();

	public void updateCode(String projectName, String packageName, String className, Collection<String> changedCode,
			String featureExpression) {
		List<Diff> diffs = ContextUtils.analyzeDiff(new ArrayList<String>(changedCode));
		Collection<Context> contexts = ContextHandler.getInstance().getAllContexts();
		Iterator<Context> itC = contexts.iterator();
		while (itC.hasNext()) {
			Context c = itC.next();
			if (c.getFeatureExpression().equals(featureExpression)) {
				continue;
			}
			updateClasses(className, diffs, c, c.getJavaProject(projectName));
		}
	}

	private void updateClasses(String className, List<Diff> diffs, Context c, Variant var) {
		List<Class> classes = new ArrayList<Class>();
		if (var != null) {
			iterateElements(var.getChildren(), classes, className);
			for (Class jc : classes) {
				update(jc, diffs);
			}
			saveContext(c);
		}
	}

	// TODO test with different cases!
	private void update(Class jc, List<Diff> diffs) {
		List<CodeLine> cls = jc.getCodeLines();

		// actualize (increase or decrease diff line number (see Context
		// Algorithm))
		int diffCounter = 0;
		for (Diff d : diffs) {

			updateClasses(diffs, cls, diffCounter, d.getDiffIndices());
			diffCounter++;
		}
	}

	private void updateClasses(List<Diff> diffs, List<CodeLine> cls, int diffCounter, DiffIndices di) {

		// case: line numbers must be decreased
		if (di.getNumberOfOldCodeLines() > 0) {
			List<CodeLine> removeLines = new ArrayList<CodeLine>();
			for (int i = 0; i < cls.size(); i++) {
				CodeLine cl = cls.get(i);
				if ((cl.getLine() >= di.getStartIndixOldCode())
						&& (cl.getLine() <= di.getStartIndixOldCode() + (di.getNumberOfOldCodeLines() - 1))) {
					removeLines.add(cl);
				}
			}
			for (CodeLine cl : removeLines) {
				cls.remove(cl);
			}
			for (CodeLine cl : cls) {
				if (cl.getLine() > di.getStartIndixOldCode()) {
					cl.setLine(cl.getLine() - di.getNumberOfOldCodeLines());
				}
			}
		}

		// case: line numbers must be increased
		for (CodeLine cl : cls) {
			if (cl.getLine() >= di.getStartIndixNewCode()) {
				cl.setLine(cl.getLine() + di.getNumberOfNewCodeLines());
			}
		}
		List<CodeLine> addLines = new ArrayList<CodeLine>();
		for (int i = 0; i < cls.size(); i++) {
			CodeLine cl = cls.get(i);
			CodeLine follower = null;
			if ((i + 1) < cls.size()) {
				follower = cls.get(i + 1);
				if (cl.getLine() == follower.getLine()) {
					addLines.add(cl);
				}
			}
		}
		for (CodeLine cl : addLines) {
			cls.remove(cl);
		}
		if (di.getNumberOfOldCodeLines() > 0) {
			ContextUtils.decreaseCodeLines(diffs.subList(diffCounter + 1, diffs.size()), di.getNumberOfOldCodeLines());
		}
		if (di.getNumberOfNewCodeLines() > 0) {
			ContextUtils.increaseCodeLines(diffs.subList(diffCounter + 1, diffs.size()), di.getNumberOfNewCodeLines());
		}
	}

	private void iterateElements(List<Element> elements, List<Class> classes, String className) {
		if (elements != null && !elements.isEmpty()) {
			for (Element e : elements) {
				addClass(classes, className, e);
			}
		}
	}

	private void addClass(List<Class> classes, String className, Element e) {
		if (e instanceof Class && e.getName().equals(className)) {
			classes.add((Class) e);
		} else {
			iterateElements(e.getChildren(), classes, className);
		}
	}

	private void saveContext(Context c) {
		String storageLocation = VariantSyncPlugin.getWorkspaceLocation() + VariantSyncConstants.CONTEXT_PATH;
		String filename = "/" + c.getFeatureExpression() + ".xml";

		// creates target folder if it does not already exist
		File folder = new File(storageLocation);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		storageLocation += filename;
		persistenceOp.saveContext(c, storageLocation);
	}
}
