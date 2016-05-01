package de.ovgu.variantsync.applicationlayer.context;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaClass;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffStep;

class ContextUtils {

	public static void decreaseCodeLines(List<Diff> diffs, int removeCounter) {
		for (Diff diff : diffs) {
			DiffIndices di = diff.getDiffIndices();
			diff.setDiffIndices(new DiffIndices(di.getStartIndixOldCode()
					- removeCounter, di.getNumberOfOldCodeLines(), di
					.getStartIndixNewCode(), di.getNumberOfNewCodeLines()));
		}
	}

	public static void increaseCodeLines(List<Diff> diffs, int addCounter) {
		for (Diff diff : diffs) {
			DiffIndices di = diff.getDiffIndices();
			diff.setDiffIndices(new DiffIndices(di.getStartIndixOldCode()
					+ addCounter, di.getNumberOfOldCodeLines(), di
					.getStartIndixNewCode(), di.getNumberOfNewCodeLines()));
		}
	}

	public static List<Diff> analyzeDiff(List<String> code) {
		List<Diff> diffs = new ArrayList<Diff>();
		DiffIndices indices = null;
		for (int i = 2; i < code.size(); i++) {
			String codeChange = code.get(i);
			if (codeChange.startsWith("@@")) {
				indices = parseLineChanges(codeChange);
				List<DiffStep> diffSteps = new ArrayList<DiffStep>();
				for (int j = i + 1; j < code.size(); j++) {
					DiffStep diffStep = null;
					codeChange = code.get(j);
					if (codeChange.startsWith("-")) {
						codeChange = codeChange.substring(1).trim();
						diffStep = new DiffStep(false, codeChange);
					} else if (codeChange.startsWith("+")) {
						codeChange = codeChange.substring(1).trim();
						diffStep = new DiffStep(true, codeChange);
					} else if (codeChange.startsWith("@@")) {
						break;
					}
					diffSteps.add(diffStep);
				}
				diffs.add(new Diff(indices, diffSteps));
			}
		}
		return diffs;
	}

	public static List<JavaClass> getClasses(JavaProject jp) {
		List<JavaElement> javaElements = new ArrayList<JavaElement>();
		ContextUtils.iterateElements(jp.getChildren(), javaElements);
		List<JavaClass> classes = new ArrayList<JavaClass>();
		for (JavaElement e : javaElements) {
			classes.add((JavaClass) e);
		}
		return classes;
	}

	public static void iterateElements(List<JavaElement> elements,
			List<JavaElement> classes) {
		for (JavaElement e : elements) {
			if (e.getChildren() != null) {
				iterateElements(e.getChildren(), classes);
			} else {
				classes.add(e);
			}
		}
	}

	private static DiffIndices parseLineChanges(String codeChange) {
		int startNew;
		int numberOfLinesNew;
		String startS = codeChange.substring(codeChange.lastIndexOf("+") + 1,
				codeChange.lastIndexOf(","));
		String endS = codeChange.substring(codeChange.lastIndexOf(",") + 1,
				codeChange.lastIndexOf("@@") - 1);
		startNew = Integer.valueOf(startS);
		numberOfLinesNew = Integer.valueOf(endS);
		int startOld;
		int numberOfLinesOld;
		startS = codeChange.substring(codeChange.indexOf("-") + 1,
				codeChange.indexOf(","));
		endS = codeChange.substring(codeChange.indexOf(",") + 1,
				codeChange.indexOf("+") - 1);
		startOld = Integer.valueOf(startS);
		numberOfLinesOld = Integer.valueOf(endS);
		return new DiffIndices(startOld, numberOfLinesOld, startNew,
				numberOfLinesNew);
	}
}
