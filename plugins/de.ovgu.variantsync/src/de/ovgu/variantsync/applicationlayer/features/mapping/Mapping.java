package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.Collection;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Package;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.ui.controller.data.MappingElement;
import de.ovgu.variantsync.utilities.LogOperations;

public abstract class Mapping implements IMappingOperations {

	protected abstract Element computeElement(Element element, String name, String path);

	protected abstract void computeElement(Element element, MappingElement mapping, String elementName,
			String elementPath);

	protected abstract Element createProject(String pathToProject, String elementName, String elementPath,
			MappingElement mapping);

	protected abstract boolean removeElement(Element element, List<Element> elements, String elementName,
			String elementPath, CodeFragment code, boolean isFirstStep, boolean isLastStep, Collection<String> wholeClass,
			long modificationTime);

	protected abstract boolean checkElement(Element element, String elementName, String pathToElement);

	@Override
	public Element getElement(List<Element> javaPackages, String className, String classPath) {
		String packageName = UtilOperations.getInstance().parsePackageName(classPath);
		Element element = null;
		if (javaPackages != null && !javaPackages.isEmpty() && javaPackages.get(0) != null) {
			for (Element pa : javaPackages) {
				if (pa.getName().equals(packageName)) {
					element = computeElement(pa, className, classPath);
				}
			}
		}
		return element;
	}

	@Override
	public Variant addMapping(Variant project, MappingElement mapping) {
		String elementName = mapping.getTitle();
		String elementPath = mapping.getPathToSelectedElement();
		String projectPath = mapping.getPathToProject();
		if (projectPath == null) {
			projectPath = elementPath.substring(0, elementPath.indexOf("/src/"));
		}
		if (project == null) {
			Element javaProject = createProject(projectPath, elementName, elementPath, mapping);
			try {
				return (Variant) javaProject.clone();
			} catch (CloneNotSupportedException e) {
				LogOperations.logError("Clone-method in class JavaProject not yet implemented.", e);
			}
		} else {
			computeElement(project, mapping, elementName, elementPath);
		}
		return project;
	}

	@Override
	public void removeMapping(String elementName, String pathToElement, CodeFragment code, Element element,
			boolean isFirstStep, boolean isLastStep, Collection<String> wholeClass, long modificationTime) {
		List<Element> packages = element.getChildren();
		if (packages == null && !(element instanceof Class)) {
			computeElementForRemove(element, elementName, pathToElement);
			packages = element.getChildren();
		}
		if (packages != null) {
			for (Element javaPackage : packages) {
				removeMapping(elementName, pathToElement, code, javaPackage, isFirstStep, isLastStep, wholeClass,
						modificationTime);
			}
		} else {
			removeElement(element, packages, elementName, pathToElement, code, isFirstStep, isLastStep, wholeClass,
					modificationTime);
		}
	}

	private void computeElementForRemove(Element element, String name, String path) {
		String relativeClassPath = UtilOperations.getInstance().getRelativeClassPath(path);
		if (new ClassMapping().containsElement(element.getChildren(), name, path, "")) {

		} else {
			Element packageOfClass = new PackageMapping().getElement(element.getChildren(), name, relativeClassPath);
			if (packageOfClass == null) {
				String packageName = UtilOperations.getInstance().parsePackageName(path);
				path = UtilOperations.getInstance().parsePackagePath(path);
				packageOfClass = new Package(packageName, path);
				element.addChild(packageOfClass);
			}
			Class jc = new Class(name, path + "/" + name);
			packageOfClass.addChild(jc);
		}
	}

	@Override
	public boolean containsElement(List<Element> elements, String elementName, String pathToElement,
			String contentOfElement) {
		if (elements != null && !elements.isEmpty() && elements.get(0) != null) {
			for (Element pa : elements) {
				if (checkElement(pa, elementName, pathToElement)) {
					return true;
				}
			}
		}
		return false;
	}

}
