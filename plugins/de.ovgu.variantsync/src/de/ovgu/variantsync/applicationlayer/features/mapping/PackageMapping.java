package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaPackage;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

public class PackageMapping extends Mapping {

	@Override
	public JavaElement createElement(String pathToProject, String elementName,
			String pathToElement) {
		return addPackage(pathToProject, elementName, pathToElement);
	}

	@Override
	protected JavaElement computeElement(JavaElement element, String name,
			String path) {
		return element;
	}

	@Override
	protected void computeElement(JavaElement element, MappingElement mapping,
			String elementName, String elementPath) {
		if (containsElement(element.getChildren(), elementName, "", "")) {
			element.removeChild(elementName);
		}
		element.addChild(addPackage(mapping.getPathToProject(), elementName,
				elementPath));
	}

	@Override
	protected JavaElement createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping) {
		JavaElement project = new JavaProject(UtilOperations.getInstance()
				.parseProjectName(pathToProject), pathToProject);
		project.addChild(addPackage(pathToProject, elementName, elementPath));
		return project;
	}

	@Override
	protected boolean removeElement(JavaElement element,
			List<JavaElement> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep,
			List<String> wholeClass) {
		String nameOfPackage = element.getName();
		String pathToPackage = UtilOperations.getInstance().removeSrcInPath(
				element.getPath());
		if (nameOfPackage.equals(elementName)
				&& pathToPackage.equals(UtilOperations.getInstance()
						.removeSrcInPath(elementPath))) {
			elements.remove(element);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean checkElement(JavaElement element, String elementName,
			String pathToElement) {
		if (element.getName().equals(elementName)) {
			return true;
		}
		return false;
	}

	private JavaPackage addPackage(String projectPath, String elementName,
			String elementPath) {
		elementPath = elementPath.substring(1);
		JavaPackage javaPackage = new JavaPackage(elementName, elementPath);
		javaPackage.setChildren(UtilOperations.getInstance().readClassFiles(
				projectPath, elementPath, ""));
		return javaPackage;
	}

}