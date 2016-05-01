package de.ovgu.variantsync.applicationlayer.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffStep;

public class ContextUtils {

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

	public static IFile findFileRecursively(IContainer container, String name)
			throws CoreException {
		for (IResource r : container.members()) {
			try {
				r.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			if (r instanceof IContainer) {
				IFile file = findFileRecursively((IContainer) r, name);
				if (file != null) {
					return file;
				}
			} else if (r instanceof IFile && r.getName().equals(name)) {
				return (IFile) r;
			}
		}

		return null;
	}

	public static IResource findResource(String projectNameTarget,
			String classNameTarget) {
		List<IProject> supportedProjects = VariantSyncPlugin.getDefault()
				.getSupportProjectList();
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectNameTarget)) {
				IResource javaClass = null;
				try {
					javaClass = findFileRecursively(p, classNameTarget);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return javaClass;
			}
		}
		return null;
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

	public static List<Class> getClasses(Variant jp) {
		if (jp == null || jp.getChildren() == null
				|| jp.getChildren().isEmpty()) {
			return new ArrayList<Class>();
		}
		List<Element> javaElements = new ArrayList<Element>();
		ContextUtils.iterateElements(jp.getChildren(), javaElements);
		List<Class> classes = new ArrayList<Class>();
		for (Element e : javaElements) {
			classes.add((Class) e);
		}
		return classes;
	}

	public static void iterateElements(List<Element> elements,
			List<Element> classes) {
		for (Element e : elements) {
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
