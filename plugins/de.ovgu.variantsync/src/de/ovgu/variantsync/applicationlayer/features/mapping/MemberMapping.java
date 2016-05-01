package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.presentationlayer.controller.data.MappingElement;

/**
 * Not yet implemented.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 08.06.2015
 */
public class MemberMapping extends Mapping {

	@Override
	public JavaElement createElement(String pathToProject, String elementName,
			String pathToElement) {
		return null;
	}

	@Override
	protected JavaElement computeElement(JavaElement element, String name,
			String path) {
		return null;
	}

	@Override
	protected void computeElement(JavaElement element, MappingElement mapping,
			String elementName, String elementPath) {
	}

	@Override
	protected JavaElement createProject(String pathToProject,
			String elementName, String elementPath, MappingElement mapping) {
		return null;
	}

	@Override
	protected boolean removeElement(JavaElement element,
			List<JavaElement> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep,
			List<String> wholeClass) {
		return false;
	}

	@Override
	protected boolean checkElement(JavaElement element, String elementName,
			String pathToElement) {
		return false;
	}
	/*
	 * private JavaProject addMethod(String projectPath, String elementName,
	 * String elementPath) { String projectName =
	 * UtilOperations.parseProjectName(projectPath); JavaProject javaProject =
	 * new JavaProject(projectName, projectPath); elementPath =
	 * elementPath.substring(1); String pathToFirstPackage =
	 * elementPath.substring(0, elementPath.indexOf("/src/") + 4); String
	 * packagePath = elementPath.substring(UtilOperations
	 * .getIndexOfFirstPathSeparator(elementPath)); packagePath =
	 * packagePath.substring(5); packagePath = packagePath.substring(0,
	 * packagePath.lastIndexOf(elementPath.substring(elementPath
	 * .lastIndexOf("/") + 1))); String[] packages = packagePath.split("/");
	 * JavaPackage javaPackage = null; if (packages.length > 0) { javaPackage =
	 * addMethodPackage(packages, 0, pathToFirstPackage, projectPath,
	 * elementName, elementPath); } javaProject.addChild(javaPackage); return
	 * javaProject; }
	 * 
	 * private JavaPackage addMethodPackage(String[] packages, int i, String
	 * path, String projectPath, String methodName, String classPath) { if
	 * (packages.length > i) { String packageName = packages[i]; String
	 * packagePath = path + "/" + packageName; JavaPackage javaPackage = new
	 * JavaPackage(packageName, packagePath);
	 * javaPackage.addChild(addClassWithMethodToPackage(projectPath,
	 * packagePath, methodName, classPath)); return javaPackage; } return null;
	 * }
	 * 
	 * private JavaClass addClassWithMethodToPackage(String projectPath, String
	 * packagePath, String methodName, String classPath) { JavaClass javaClass =
	 * null; String tmpPackagePath = ""; tmpPackagePath =
	 * packagePath.substring(packagePath.indexOf("/")); String path =
	 * projectPath + tmpPackagePath; List<String> results = new
	 * ArrayList<String>(); File[] files = new File(path).listFiles(); for (File
	 * file : files) { if (file.isFile()) { results.add(file.getAbsolutePath());
	 * } } for (String filePath : results) { try { List<String> listString =
	 * persistanceOperations .readFile(new FileInputStream(filePath));
	 * StringBuilder content = new StringBuilder(); for (String s : listString)
	 * { content.append(s + "\n"); } String name =
	 * filePath.substring(UtilOperations .getIndexOfLastPathSeparator(filePath)
	 * + 1); String className = classPath.substring(classPath .lastIndexOf("/")
	 * + 1); if (name.equals(className)) { javaClass = new JavaClass(name,
	 * packagePath + name, getMethodCode(methodName, listString));
	 * javaClass.addChild(new JavaMethod(methodName, packagePath + name,
	 * getMethodCode(methodName, listString))); break; } } catch
	 * (FileNotFoundException | FileOperationException e) {
	 * LogOperations.logError("Class could not be read.", e); } } return
	 * javaClass; }
	 * 
	 * private String getMethodCode(String methodName, List<String> code) {
	 * String methodCode = ""; int i = 0; List<String> methodLines = new
	 * ArrayList<String>(); for (String line : code) { if (line.contains(" " +
	 * methodName + "(")) { int bracketCounter = 1; for (int j = i; j <
	 * code.size(); j++) { methodLines.add(code.get(j)); if (bracketCounter ==
	 * 0) { StringBuilder content = new StringBuilder(); for (String s :
	 * methodLines) { content.append(s + "\n"); } return content.toString(); }
	 * if (code.get(j + 1).contains("{")) { bracketCounter++; } else if
	 * (code.get(j + 1).contains("}")) { bracketCounter--;
	 * 
	 * } } } i++; } return methodCode; }
	 */
}
