package de.ovgu.variantsync.applicationlayer.context;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.Diff;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffIndices;
import de.ovgu.variantsync.applicationlayer.datamodel.diff.DiffStep;
import de.ovgu.variantsync.applicationlayer.features.IFeatureOperations;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 14.09.2015
 */
class ContextAlgorithm {

	private IFeatureOperations featureOperations = ModuleFactory
			.getFeatureOperations();
	private Context context;

	public ContextAlgorithm(Context context) {
		this.context = context;
	}

	public void addCode(String projectName, String packageName,
			String className, List<String> code, List<String> wholeClass,
			boolean ignore) {

		// Mapping auf FeatureExpressions umstellen/ von FeatureExpressions
		// extrahieren und immer das Project zurückgeben mit angepasstem Mapping
		// => Blackbox mit Input (MappingElement, Project) und Output (Project)
		// Zuordnung Project zu FeatureExpression und Datenhaltung erfolgt im
		// Context

		List<Diff> diffs = ContextUtils.analyzeDiff(code);
		refreshCodeBase(diffs, projectName, packageName, className, wholeClass,
				ignore);

		// String file = "/src/" + packageName + "/" + className;
		// refreshMarker(diffs, file, context.getPathOfJavaProject());

		UtilOperations.getInstance().printProject(
				context.getJavaProject(projectName));
		System.out.println("===============================================");
	}

	private void refreshCodeBase(List<Diff> diffs, String projectName,
			String packageName, String className, List<String> wholeClass,
			boolean ignore) {
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
					addCode(projectName, packageName, className, startNew,
							startNew, list, wholeClass, isFirstStep,
							isLastStep, ignore);
					if (!UtilOperations.getInstance().ignoreAddCounter()) {
						addCounter++;
					}
					startNew++;
				} else {
					if (j < diffSteps.size() - 2
							&& diffSteps.get(j + 1).isAddFlag()) {
						isLastStep = true;
					}
					removeCode(projectName, packageName, className, startOld,
							startOld, list, isFirstStep, isLastStep,
							wholeClass, ignore);
 					if (j < diffSteps.size() - 2
							&& diffSteps.get(j + 1).isAddFlag()) {
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
				ContextUtils.decreaseCodeLines(
						diffs.subList(i + 1, diffs.size()), removeCounter);
			}
			if (addCounter > 0) {
				ContextUtils.increaseCodeLines(
						diffs.subList(i + 1, diffs.size()), addCounter);
			}
			i++;
		}
	}

	private void addCode(String projectName, String packageName,
			String className, int start, int end, List<String> extractedCode,
			List<String> wholeClass, boolean isFirstStep, boolean isLastStep, boolean ignore) {
		setUpProject(projectName);
		MappingElement mapping = new MappingElement(
				context.getFeatureExpression(), className,
				JavaElements.CODE_FRAGMENT,
				context.getPathToProject(projectName) + "/src/"
						+ packageName.replace(".", "/") + "/" + className,
				extractedCode, start, end, end - start, wholeClass,
				isFirstStep, isLastStep, ignore);
		mapping.setPathToProject(context.getPathToProject(projectName));

		featureOperations.addCodeFragment(mapping,
				context.getJavaProject(projectName));
	}

	private void removeCode(String projectName, String packageName,
			String className, int start, int end, List<String> extractedCode,
			boolean isFirstStep, boolean isLastStep, List<String> wholeClass, boolean ignore) {
		setUpProject(projectName);
		MappingElement mapping = new MappingElement(
				context.getFeatureExpression(), className,
				JavaElements.CODE_FRAGMENT,
				context.getPathToProject(projectName) + "/src/"
						+ packageName.replace(".", "/") + "/" + className,
				extractedCode, start, end, end - start, wholeClass,
				isFirstStep, isLastStep, ignore);
		mapping.setPathToProject(context.getPathToProject(projectName));
		featureOperations.removeMapping(mapping,
				context.getJavaProject(projectName));
	}

	private void setUpProject(String projectName) {
		if (context.getPathToProject(projectName) == null) {
			List<IProject> projects = VariantSyncPlugin.getDefault()
					.getSupportProjectList();
			for (IProject p : projects) {
				if (p.getName().equals(projectName)) {
					context.setPathToProject(projectName, p.getFullPath()
							.toString());
				}
			}
		}
	}

}
