package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

public abstract class Mapping implements IMappingOperations {

	protected abstract JavaElement computeElement(JavaElement element,
			String name, String path);

	protected abstract void computeElement(JavaElement element,
			MappingElement mapping, String elementName, String elementPath);

	protected abstract JavaElement createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping);

	protected abstract boolean removeElement(JavaElement element,
			List<JavaElement> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep,
			List<String> wholeClass);

	protected abstract boolean checkElement(JavaElement element,
			String elementName, String pathToElement);

	@Override
	public JavaElement getElement(List<JavaElement> javaPackages,
			String className, String classPath) {
		String packageName = UtilOperations.getInstance().parsePackageName(
				classPath);
		JavaElement element = null;
		if (javaPackages != null && !javaPackages.isEmpty()) {
			for (JavaElement pa : javaPackages) {
				if (pa.getName().equals(packageName)) {
					element = computeElement(pa, className, classPath);
				}
			}
		}
		return element;
	}

	@Override
	public JavaProject addMapping(JavaProject project, MappingElement mapping) {
		String elementName = mapping.getTitle();
		String elementPath = mapping.getPathToSelectedElement();
		String projectPath = mapping.getPathToProject();
		if (projectPath == null) {
			projectPath = elementPath
					.substring(0, elementPath.indexOf("/src/"));
		}
		if (project == null) {
			JavaElement javaProject = createProject(projectPath, elementName,
					elementPath, mapping);
			try {
				return (JavaProject) javaProject.clone();
			} catch (CloneNotSupportedException e) {
				LogOperations
						.logError(
								"Clone-method in class JavaProject not yet implemented.",
								e);
			}
		} else {
			computeElement(project, mapping, elementName, elementPath);
		}
		return project;
	}

	@Override
	public void removeMapping(String elementName, String pathToElement,
			CodeFragment code, JavaElement element, boolean isFirstStep,
			boolean isLastStep, List<String> wholeClass) {
		List<JavaElement> packages = element.getChildren();
		if (packages != null) {
			for (JavaElement javaPackage : packages) {
				removeMapping(elementName, pathToElement, code, javaPackage,
						isFirstStep, isLastStep, wholeClass);
			}
		} else {
			removeElement(element, packages, elementName, pathToElement, code,
					isFirstStep, isLastStep, wholeClass);
		}
	}

	@Override
	public boolean containsElement(List<JavaElement> elements,
			String elementName, String pathToElement, String contentOfElement) {
		if (elements != null && !elements.isEmpty()) {
			for (JavaElement pa : elements) {
				if (checkElement(pa, elementName, pathToElement)) {
					return true;
				}
			}
		}
		return false;
	}

}
