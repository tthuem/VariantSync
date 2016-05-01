package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Package;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

public class ClassMapping extends Mapping {

	private IMappingOperations packageMapping;

	public ClassMapping() {
		packageMapping = new PackageMapping();
	}

	@Override
	public Element createElement(String pathToProject, String elementName,
			String pathToElement) {
		return createProjectWithClass(pathToProject, elementName, pathToElement);
	}

	@Override
	protected Element computeElement(Element element, String name,
			String path) {
		List<Element> javaClasses = element.getChildren();
		if (javaClasses != null && !javaClasses.isEmpty()) {
			for (Element javaClass : javaClasses) {
				if (javaClass.getName().equals(name)) {
					return javaClass;
				}
			}
		}
		return null;
	}

	@Override
	protected void computeElement(Element element, MappingElement mapping,
			String elementName, String elementPath) {
		if (containsElement(element.getChildren(), elementName, elementPath, "")) {
			removeClass(element.getChildren(), elementName, elementPath);
		}
		Element packageOfClass = packageMapping.getElement(
				element.getChildren(), elementName, elementPath);
		if (packageOfClass == null) {
			String packageName = elementPath.substring(0,
					elementPath.lastIndexOf("/"));
			packageName = packageName.substring(1);
			packageName = packageName.substring(UtilOperations.getInstance()
					.getIndexOfFirstPathSeparator(packageName));
			packageName = packageName.substring(5);
			packageName = packageName.replace("/", ".");
			packageOfClass = new Package(packageName,
					elementPath.substring(0, elementPath.lastIndexOf("/")));
			element.addChild(packageOfClass);
		}
		packageOfClass.addChild(addClassToPackage(mapping.getPathToProject(),
				elementPath.substring(0, elementPath.lastIndexOf("/")),
				elementName));
	}

	@Override
	protected Element createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping) {
		return createProjectWithClass(pathToProject, elementName, elementPath);
	}

	@Override
	protected boolean removeElement(Element element,
			List<Element> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep, List<String> wholeClass) {
		List<Element> classes = element.getChildren();
		for (Element javaClass : classes) {
			String nameOfClass = javaClass.getName();
			String pathOfClass = UtilOperations.getInstance().removeSrcInPath(
					javaClass.getPath());
			if (nameOfClass.equals(elementName)
					&& pathOfClass.equals(UtilOperations.getInstance()
							.removeSrcInPath(elementPath))) {
				classes.remove(javaClass);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	protected boolean checkElement(Element element, String elementName,
			String pathToElement) {
		String packageName = UtilOperations.getInstance().parsePackageName(
				pathToElement);
		if (element.getName().equals(packageName)) {
			List<Element> javaClasses = element.getChildren();
			if (javaClasses != null && !javaClasses.isEmpty()) {
				for (Element javaClass : javaClasses) {
					if (javaClass.getName().equals(elementName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void removeClass(List<Element> javaPackages, String className,
			String classPath) {
		String packageName = classPath.substring(0, classPath.lastIndexOf("/"));
		packageName = packageName.substring(1);
		packageName = packageName.substring(UtilOperations.getInstance()
				.getIndexOfFirstPathSeparator(packageName));
		packageName = packageName.substring(5);
		packageName = packageName.replace("/", ".");
		if (javaPackages != null && !javaPackages.isEmpty()) {
			for (Element pa : javaPackages) {
				if (pa.getName().equals(packageName)) {
					List<Element> javaClasses = pa.getChildren();
					if (javaClasses != null && !javaClasses.isEmpty()) {
						for (Element javaClass : javaClasses) {
							if (javaClass.getName().equals(className)) {
								UtilOperations.getInstance().removeChild(
										className, pa.getChildren());
							}
						}
					}
				}
			}
		}
	}

	private Variant createProjectWithClass(String projectPath,
			String elementName, String elementPath) {
		String projectName = UtilOperations.getInstance().parseProjectName(
				projectPath);
		Variant javaProject = new Variant(projectName, projectPath);
		elementPath = elementPath.substring(1);
		String packagePath = elementPath.substring(UtilOperations.getInstance()
				.getIndexOfFirstPathSeparator(elementPath));
		packagePath = packagePath.substring(5);
		if (packagePath.length() > elementName.length()) {
			packagePath = packagePath.substring(0,
					packagePath.lastIndexOf(elementName) - 1);
		}
		String packageName = packagePath.replace("/", ".");
		elementPath = elementPath.substring(0,
				elementPath.lastIndexOf(elementName) - 1);
		Package javaPackage = null;
		javaPackage = addPackage(packageName, elementPath, projectPath,
				elementName);
		javaProject.addChild(javaPackage);
		return javaProject;
	}

	private Package addPackage(String elementName, String elementPath,
			String projectPath, String className) {
		Package javaPackage = new Package(elementName, elementPath);
		javaPackage.addChild(addClassToPackage(projectPath, elementPath + "/",
				className));
		return javaPackage;
	}

	private Element addClassToPackage(String projectPath,
			String packagePath, String className) {
		return UtilOperations.getInstance()
				.readClassFiles(projectPath, packagePath, className).get(0);
	}

	public Variant addClassWithCode(String projectPath, String elementName,
			String elementPath, List<String> code, int startLine, int endLine,
			int offset) {
		String projectName = UtilOperations.getInstance().parseProjectName(
				projectPath);
		Variant javaProject = new Variant(projectName, projectPath);
		String packagePath = elementPath.substring(projectName.length() + 1);
		packagePath = packagePath.substring(0,
				packagePath.lastIndexOf(elementName) - 1);
		String packageName = packagePath.replace("/", ".");
		elementPath = elementPath.substring(0,
				elementPath.lastIndexOf(elementName) - 1);
		Package javaPackage = new Package(packageName, elementPath);
		javaPackage.addChild(new Class(elementName, elementPath + "/"
				+ elementName, new CodeFragment(code, startLine, endLine,
				offset)));
		javaProject.addChild(javaPackage);
		return javaProject;
	}
}