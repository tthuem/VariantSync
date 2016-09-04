package de.ovgu.variantsync.applicationlayer.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffStep;
import de.ovgu.variantsync.applicationlayer.features.FeatureOperations;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.ui.controller.data.JavaElements;
import de.ovgu.variantsync.ui.controller.data.MappingElement;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 14.09.2015
 */
class ContextAlgorithm {

	private FeatureOperations featureOperations = ModuleFactory.getFeatureOperations();
	private Context context;

	public ContextAlgorithm(Context context) {
		this.context = context;
	}

	@SuppressWarnings("serial")
	public void addClass(String projectName, String packageName, String className, Collection<String> wholeClass,
			long modificationTime) {
		addCode(projectName, packageName, className, 0, 0, wholeClass, new ArrayList<String>() {
		}, true, true, false, modificationTime);
	}

	public void removeClass(String projectName, String packageName, String className, Collection<String> wholeClass,
			long modificationTime) {
		removeCode(projectName, packageName, className, 0, 0, new ArrayList<String>() {
		}, true, true, wholeClass, false, modificationTime);
	}

	public void addCode(String projectName, String packageName, String className, Collection<String> code,
			Collection<String> newVersion, boolean ignore, long modificationTime) {

		// Mapping auf FeatureExpressions umstellen/ von FeatureExpressions
		// extrahieren und immer das Project zur�ckgeben mit angepasstem Mapping
		// => Blackbox mit Input (MappingElement, Project) und Output (Project)
		// Zuordnung Project zu FeatureExpression und Datenhaltung erfolgt im
		// Context

		List<Diff> diffs = ContextUtils.analyzeDiff(new ArrayList<String>(code));
		refreshCodeBase(diffs, projectName, packageName, className, newVersion, ignore, modificationTime);

		// String file = "/src/" + packageName + "/" + className;
		// refreshMarker(diffs, file, context.getPathOfJavaProject());

		UtilOperations.getInstance().printProject(context.getJavaProject(projectName));
		System.out.println("===============================================");
	}

	private void refreshCodeBase(List<Diff> diffs, String projectName, String packageName, String className,
			Collection<String> newVersion, boolean ignore, long modificationTime) {
		int i = 0;
		for (Diff diff : diffs) {
			DiffIndices di = diff.getDiffIndices();
			List<DiffStep> diffSteps = diff.getDiffSteps();
			int startNew = di.getStartIndixNewCode();
			int startOld = di.getStartIndixOldCode();
			int removeCounter = 0;
			int addCounter = 0;
			boolean isFirstStep = true;
			boolean isLastStep = false;
			if (diffSteps.size() == 1)
				isLastStep = true;
			int j = 0;
			for (DiffStep ds : diffSteps) {
				List<String> list = new LinkedList<String>();
				if (ds == null) {
					continue;
				}
				list.add(ds.getCode());
				if (ds.isAddFlag()) {
					addCode(projectName, packageName, className, startNew, startNew, list, newVersion, isFirstStep,
							isLastStep, ignore, modificationTime);
					if (!UtilOperations.getInstance().ignoreAddCounter()) {
						addCounter++;
					}
					startNew++;
				} else {
					if (j < diffSteps.size() - 2 && diffSteps.get(j + 1).isAddFlag()) {
						isLastStep = true;
					}
					removeCode(projectName, packageName, className, startOld, startOld, list, isFirstStep, isLastStep,
							newVersion, ignore, modificationTime);
					if (j < diffSteps.size() - 2 && diffSteps.get(j + 1).isAddFlag()) {
						isLastStep = false;
					}
					removeCounter++;
					if (startNew > startOld) {
						startNew--;
					}
				}
				isFirstStep = false;
				if (j == diffSteps.size() - 2) {
					isLastStep = true;
				}
				j++;
			}
			if (removeCounter > 0) {
				ContextUtils.decreaseCodeLines(diffs.subList(i + 1, diffs.size()), removeCounter);
			}
			if (addCounter > 0) {
				ContextUtils.increaseCodeLines(diffs.subList(i + 1, diffs.size()), addCounter);
			}
			i++;
		}
	}

	private void addCode(String projectName, String packageName, String className, int start, int end,
			Collection<String> wholeClass, Collection<String> newVersion, boolean isFirstStep, boolean isLastStep,
			boolean ignore, long modificationTime) {
		setUpProject(projectName);
		MappingElement mapping = new MappingElement(context.getFeatureExpression(), className,
				JavaElements.CODE_FRAGMENT,
				context.getPathToProject(projectName) + "/src/" + packageName.replace(".", "/") + "/" + className,
				wholeClass, start, end, end - start, newVersion, isFirstStep, isLastStep, ignore, modificationTime);
		mapping.setPathToProject(context.getPathToProject(projectName));

		featureOperations.addCodeFragment(mapping, context.getJavaProject(projectName));
	}

	private void removeCode(String projectName, String packageName, String className, int start, int end,
			List<String> extractedCode, boolean isFirstStep, boolean isLastStep, Collection<String> newVersion,
			boolean ignore, long modificationTime) {
		setUpProject(projectName);
		MappingElement mapping = new MappingElement(context.getFeatureExpression(), className,
				JavaElements.CODE_FRAGMENT,
				context.getPathToProject(projectName) + "/src/" + packageName.replace(".", "/") + "/" + className,
				extractedCode, start, end, end - start, newVersion, isFirstStep, isLastStep, ignore, modificationTime);
		mapping.setPathToProject(context.getPathToProject(projectName));
		featureOperations.removeMapping(mapping, context.getJavaProject(projectName), modificationTime);
	}

	private void setUpProject(String projectName) {
		if (context.getPathToProject(projectName) == null) {
			List<IProject> projects = VariantSyncPlugin.getDefault().getSupportProjectList();
			for (IProject p : projects) {
				if (p.getName().equals(projectName)) {
					context.setPathToProject(projectName, p.getFullPath().toString());
				}
			}
		}
	}

}
