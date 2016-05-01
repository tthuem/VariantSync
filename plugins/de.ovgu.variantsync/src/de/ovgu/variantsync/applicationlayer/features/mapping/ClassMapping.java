package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaClass;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaPackage;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

public class ClassMapping extends Mapping {

	private IMappingOperations packageMapping;

	public ClassMapping() {
		packageMapping = new PackageMapping();
	}

	@Override
	public JavaElement createElement(String pathToProject, String elementName,
			String pathToElement) {
		return createProjectWithClass(pathToProject, elementName, pathToElement);
	}

	@Override
	protected JavaElement computeElement(JavaElement element, String name,
			String path) {
		List<JavaElement> javaClasses = element.getChildren();
		if (javaClasses != null && !javaClasses.isEmpty()) {
			for (JavaElement javaClass : javaClasses) {
				if (javaClass.getName().equals(name)) {
					return javaClass;
				}
			}
		}
		return null;
	}

	@Override
	protected void computeElement(JavaElement element, MappingElement mapping,
			String elementName, String elementPath) {
		if (containsElement(element.getChildren(), elementName, elementPath, "")) {
			removeClass(element.getChildren(), elementName, elementPath);
		}
		JavaElement packageOfClass = packageMapping.getElement(
				element.getChildren(), elementName, elementPath);
		if (packageOfClass == null) {
			String packageName = elementPath.substring(0,
					elementPath.lastIndexOf("/"));
			packageName = packageName.substring(1);
			packageName = packageName.substring(UtilOperations.getInstance()
					.getIndexOfFirstPathSeparator(packageName));
			packageName = packageName.substring(5);
			packageName = packageName.replace("/", ".");
			packageOfClass = new JavaPackage(packageName,
					elementPath.substring(0, elementPath.lastIndexOf("/")));
			element.addChild(packageOfClass);
		}
		packageOfClass.addChild(addClassToPackage(mapping.getPathToProject(),
				elementPath.substring(0, elementPath.lastIndexOf("/")),
				elementName));
	}

	@Override
	protected JavaElement createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping) {
		return createProjectWithClass(pathToProject, elementName, elementPath);
	}

	@Override
	protected boolean removeElement(JavaElement element,
			List<JavaElement> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep, List<String> wholeClass) {
		List<JavaElement> classes = element.getChildren();
		for (JavaElement javaClass : classes) {
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
	protected boolean checkElement(JavaElement element, String elementName,
			String pathToElement) {
		String packageName = UtilOperations.getInstance().parsePackageName(
				pathToElement);
		if (element.getName().equals(packageName)) {
			List<JavaElement> javaClasses = element.getChildren();
			if (javaClasses != null && !javaClasses.isEmpty()) {
				for (JavaElement javaClass : javaClasses) {
					if (javaClass.getName().equals(elementName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void removeClass(List<JavaElement> javaPackages, String className,
			String classPath) {
		String packageName = classPath.substring(0, classPath.lastIndexOf("/"));
		packageName = packageName.substring(1);
		packageName = packageName.substring(UtilOperations.getInstance()
				.getIndexOfFirstPathSeparator(packageName));
		packageName = packageName.substring(5);
		packageName = packageName.replace("/", ".");
		if (javaPackages != null && !javaPackages.isEmpty()) {
			for (JavaElement pa : javaPackages) {
				if (pa.getName().equals(packageName)) {
					List<JavaElement> javaClasses = pa.getChildren();
					if (javaClasses != null && !javaClasses.isEmpty()) {
						for (JavaElement javaClass : javaClasses) {
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

	private JavaProject createProjectWithClass(String projectPath,
			String elementName, String elementPath) {
		String projectName = UtilOperations.getInstance().parseProjectName(
				projectPath);
		JavaProject javaProject = new JavaProject(projectName, projectPath);
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
		JavaPackage javaPackage = null;
		javaPackage = addPackage(packageName, elementPath, projectPath,
				elementName);
		javaProject.addChild(javaPackage);
		return javaProject;
	}

	private JavaPackage addPackage(String elementName, String elementPath,
			String projectPath, String className) {
		JavaPackage javaPackage = new JavaPackage(elementName, elementPath);
		javaPackage.addChild(addClassToPackage(projectPath, elementPath + "/",
				className));
		return javaPackage;
	}

	private JavaElement addClassToPackage(String projectPath,
			String packagePath, String className) {
		return UtilOperations.getInstance()
				.readClassFiles(projectPath, packagePath, className).get(0);
	}

	public JavaProject addClassWithCode(String projectPath, String elementName,
			String elementPath, List<String> code, int startLine, int endLine,
			int offset) {
		String projectName = UtilOperations.getInstance().parseProjectName(
				projectPath);
		JavaProject javaProject = new JavaProject(projectName, projectPath);
		String packagePath = elementPath.substring(projectName.length() + 1);
		packagePath = packagePath.substring(0,
				packagePath.lastIndexOf(elementName) - 1);
		String packageName = packagePath.replace("/", ".");
		elementPath = elementPath.substring(0,
				elementPath.lastIndexOf(elementName) - 1);
		JavaPackage javaPackage = new JavaPackage(packageName, elementPath);
		javaPackage.addChild(new JavaClass(elementName, elementPath + "/"
				+ elementName, new CodeFragment(code, startLine, endLine,
				offset)));
		javaProject.addChild(javaPackage);
		return javaProject;
	}
}