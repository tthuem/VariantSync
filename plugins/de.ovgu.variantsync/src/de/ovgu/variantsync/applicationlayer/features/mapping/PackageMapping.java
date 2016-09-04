package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.Collection;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Package;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.ui.controller.data.MappingElement;

public class PackageMapping extends Mapping {

	@Override
	public Element createElement(String pathToProject, String elementName,
			String pathToElement) {
		return addPackage(pathToProject, elementName, pathToElement);
	}

	@Override
	protected Element computeElement(Element element, String name,
			String path) {
		return element;
	}

	@Override
	protected void computeElement(Element element, MappingElement mapping,
			String elementName, String elementPath) {
		if (containsElement(element.getChildren(), elementName, "", "")) {
			element.removeChild(elementName);
		}
		element.addChild(addPackage(mapping.getPathToProject(), elementName,
				elementPath));
	}

	@Override
	protected Element createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping) {
		Element project = new Variant(UtilOperations.getInstance()
				.parseProjectName(pathToProject), pathToProject);
		project.addChild(addPackage(pathToProject, elementName, elementPath));
		return project;
	}

	@Override
	protected boolean removeElement(Element element,
			List<Element> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep,
			Collection<String> wholeClass, long modificationTime) {
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
	protected boolean checkElement(Element element, String elementName,
			String pathToElement) {
		if (element.getName().equals(elementName)) {
			return true;
		}
		return false;
	}

	private Package addPackage(String projectPath, String elementName,
			String elementPath) {
		elementPath = elementPath.substring(1);
		Package javaPackage = new Package(elementName, elementPath);
		javaPackage.setChildren(UtilOperations.getInstance().readClassFiles(
				projectPath, elementPath, ""));
		return javaPackage;
	}

}