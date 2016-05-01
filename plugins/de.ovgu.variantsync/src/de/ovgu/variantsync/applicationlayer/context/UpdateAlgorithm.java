package de.ovgu.variantsync.applicationlayer.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaClass;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;

public class UpdateAlgorithm {

	private IPersistanceOperations persistenceOp = ModuleFactory
			.getPersistanceOperations();

	public UpdateAlgorithm() {
	}

	public void updateCode(String projectName, String packageName,
			String className, List<String> code, String featureExpression) {
		List<Diff> diffs = ContextUtils.analyzeDiff(code);
		Collection<Context> contexts = ContextHandler.getInstance()
				.getAllContexts();
		Iterator<Context> itC = contexts.iterator();
		while (itC.hasNext()) {
			Context c = itC.next();
			if (c.getFeatureExpression().equals(featureExpression)) {
				continue;
			}
			JavaProject jp = c.getJavaProject(projectName);
			List<JavaClass> classes = new ArrayList<JavaClass>();
			if (jp != null) {
				iterate(jp.getChildren(), classes, className);
				for (JavaClass jc : classes) {
					update(jc, diffs);
				}
				saveContext(c);
			}
		}
	}

	// TODO test with different cases!
	private void update(JavaClass jc, List<Diff> diffs) {
		List<CodeLine> cls = jc.getCodeLines();

		// actualize (increase or decrease diff line number (see Context
		// Algorithm))
		int diffCounter = 0;
		for (Diff d : diffs) {

			// case: line numbers must be decreased
			DiffIndices di = d.getDiffIndices();
			if (di.getNumberOfOldCodeLines() > 0) {
				List<CodeLine> removeLines = new ArrayList<CodeLine>();
				for (int i = 0; i < cls.size(); i++) {
					CodeLine cl = cls.get(i);
					if ((cl.getLine() >= di.getStartIndixOldCode())
							&& (cl.getLine() <= di.getStartIndixOldCode()
									+ (di.getNumberOfOldCodeLines() - 1))) {
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
				ContextUtils.decreaseCodeLines(
						diffs.subList(diffCounter + 1, diffs.size()),
						di.getNumberOfOldCodeLines());
			}
			if (di.getNumberOfNewCodeLines() > 0) {
				ContextUtils.increaseCodeLines(
						diffs.subList(diffCounter + 1, diffs.size()),
						di.getNumberOfNewCodeLines());
			}
			diffCounter++;
		}
	}

	private void iterate(List<JavaElement> elements, List<JavaClass> classes,
			String className) {
		if (elements != null && !elements.isEmpty()) {
			for (JavaElement je : elements) {
				if (je instanceof JavaClass && je.getName().equals(className)) {
					classes.add((JavaClass) je);
				} else {
					iterate(je.getChildren(), classes, className);
				}
			}
		}
	}

	private void saveContext(Context c) {
		String storageLocation = "";
		if (VariantSyncPlugin.getDefault() != null) {
			storageLocation = VariantSyncPlugin.getDefault()
					.getWorkspaceLocation() + VariantSyncConstants.CONTEXT_PATH;
		} else {
			storageLocation = VariantSyncConstants.CONTEXT_PATH;
		}
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
