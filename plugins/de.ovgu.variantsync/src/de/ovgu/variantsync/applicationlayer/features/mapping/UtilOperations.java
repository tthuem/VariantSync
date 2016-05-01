package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaClass;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaElement;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaPackage;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaProject;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

public class UtilOperations {

	private IPersistanceOperations persistanceOperations = ModuleFactory
			.getPersistanceOperations();
	private static boolean ignoreAddCounter = false;

	private static UtilOperations instance;

	private UtilOperations() {
	}

	public static UtilOperations getInstance() {
		if (instance == null) {
			instance = new UtilOperations();
		}
		return instance;
	}

	public List<JavaElement> addChild(JavaElement element,
			List<JavaElement> elements) {
		if (elements == null) {
			elements = new CopyOnWriteArrayList<JavaElement>();
		}
		for (JavaElement e : elements) {
			if (e != null && e.getName() != null
					&& e.getName().equals(element.getName())) {
				elements.remove(e);
				break;
			}
		}
		if (!elements.isEmpty() && elements.get(0) == null) {
			elements.remove(0);
		}
		elements.add(element);
		return elements;
	}

	public List<JavaElement> removeChild(String name, List<JavaElement> elements) {
		if (elements != null) {
			int i = 0;
			for (JavaElement p : elements) {
				if (p.getName().equals(name)) {
					break;
				}
				i++;
			}
			if (i < elements.size()) {
				elements.remove(i);
			}
		}
		return elements;
	}

	public List<CodeLine> addCode(CodeFragment codeFragment, List<CodeLine> code) {
		if (code == null || code.isEmpty()) {
			code = new ArrayList<CodeLine>();
			List<String> codeLines = codeFragment.getCode();
			int i = codeFragment.getStartLine();
			for (String line : codeLines) {
				code.add(new CodeLine(line, i, true, true));
				i++;
			}
		} else {
			int newStart = codeFragment.getStartLine();
			int newEnd = codeFragment.getEndLine();
			List<String> newCodeLines = codeFragment.getCode();
			List<CodeLine> beginning = new LinkedList<CodeLine>();
			List<CodeLine> end = new LinkedList<CodeLine>();
			int oldStart = code.get(0).getLine();
			int oldEnd = code.get(code.size() - 1).getLine();
			if (newStart > oldEnd) {
				end = addCodeToList(newStart, newCodeLines, end);
			} else if (newEnd < oldStart) {
				ignoreAddCounter = true;
				beginning = addCodeToList(newStart, newCodeLines, beginning);
			} else if (newEnd <= oldEnd) {
				int tmpNumber = newStart;
				for (String newCodeLine : newCodeLines) {
					int lineNumber = tmpNumber;
					if (lineNumber < oldStart) {
						beginning.add(new CodeLine(newCodeLine, lineNumber,
								true, true));
					} else {
						insertCode(code, tmpNumber, newCodeLine);
					}
					tmpNumber++;
				}
			} else if (newStart >= oldStart) {
				int tmpNumber = newStart;
				for (String newCodeLine : newCodeLines) {
					if (tmpNumber > oldEnd) {
						end.add(new CodeLine(newCodeLine, tmpNumber, true, true));
					} else {
						insertCode(code, tmpNumber, newCodeLine);
					}
					tmpNumber++;
				}
			} else {
				System.err.println("!!!!!!!!!! ERROR !!!!!!!!!!!");
			}

			// add new code lines to start of previous lines
			for (CodeLine cl : beginning) {
				code.add(0, cl);
			}

			// add new code lines to end of previous lines
			code.addAll(end);
		}

		// refresh line numbers
		int offset = codeFragment.getEndLine() - codeFragment.getStartLine();
		if (offset == 0) {
			offset++;
		}
		int i = 0;
		for (CodeLine cl : code) {
			if (cl.getLine() > codeFragment.getEndLine()) {
				cl.setLine(cl.getLine() + offset);
			} else if ((i - 1) >= 0 && cl.getLine() == code.get(i - 1).getLine()) {
				cl.setLine(cl.getLine() + offset);
			}
			i++;
		}

		List<CodeLine> c = new ArrayList<CodeLine>();
		for (CodeLine cl : code) {
			c.add(cl.clone());
		}
		return c;
	}

	public boolean ignoreAddCounter() {
		boolean ignore = ignoreAddCounter;
		ignoreAddCounter = false;
		return ignore;
	}

	public List<CodeLine> removeCode(int start, int end, List<CodeLine> code) {

		// remove code lines
		for (int i = 0; i < code.size(); i++) {
			CodeLine selection = code.get(i);
			if (start <= selection.getLine() && end >= selection.getLine()) {
				code.remove(selection);
				i--;
			}
		}

		// refresh line numbers
		int offset = end - start;
		if (offset == 0) {
			offset++;
		}
		for (CodeLine cl : code) {
			if (cl.getLine() > end) {
				cl.setLine(cl.getLine() - offset);
			}
		}
		List<CodeLine> c = new ArrayList<CodeLine>();
		for (CodeLine cl : code) {
			c.add(cl.clone());
		}
		return c;
	}

	private List<CodeLine> addCodeToList(int newStart,
			List<String> newCodeLines, List<CodeLine> list) {
		int j = newStart;
		for (String newCodeLine : newCodeLines) {
			list.add(new CodeLine(newCodeLine, j, true, true));
			j++;
		}
		return list;
	}

	private void insertCode(List<CodeLine> code, int tmpNumber,
			String newCodeLine) {
		List<CodeLine> tmp = new LinkedList<CodeLine>();
		int listIndex = 0;
		for (CodeLine cl : code) {
			if (cl.getLine() >= tmpNumber) {
				break;
			}
			listIndex++;
		}
		for (int x = listIndex; x < code.size(); x++) {
			tmp.add(code.get(x).clone());
		}
		code.add(listIndex, new CodeLine(newCodeLine, tmpNumber, true, true));
		// for (int k = 0; k < tmp.size(); k++) {
		// try {
		// CodeLine cl = tmp.get(k);
		// cl.setLine(cl.getLine() + 1);
		// code.set(++listIndex, cl);
		// } catch (Exception e) {
		// code.add(tmp.get(k));
		// }
		// }
	}

	public void printCode(List<CodeLine> code) {
		for (CodeLine line : code) {
			System.out.println(line.toString());
		}
	}

	public void printProject(JavaElement element) {
		if (element instanceof JavaProject) {
			System.out
					.println("\n=================== Project ===================");
			System.out.println(element.getName());
		}
		if (element.getChildren() != null) {
			for (JavaElement child : element.getChildren()) {
				if (child instanceof JavaPackage) {
					System.out.println("\nPackage: " + child.getName());
					printProject(child);
				} else if (child instanceof JavaClass) {
					System.out.println("\nClass: " + child.getName());
					printCode(child.getClonedCodeLines());
				}
			}
		} else {
			if (element instanceof JavaClass) {
				System.out.println("\nClass: " + element.getName());
				printCode(element.getClonedCodeLines());
			}
		}
	}

	public String parseProjectName(String path) {
		String projectName = null;
		if (path.contains("/")) {
			projectName = path.substring(path.lastIndexOf("/") + 1);
		} else {
			projectName = path.substring(path.lastIndexOf("\\") + 1);
		}
		return projectName;
	}

	public int getIndexOfLastPathSeparator(String path) {
		int index = path.lastIndexOf("/");
		if (index == -1) {
			index = path.lastIndexOf("\\");
		}
		return index;
	}

	public int getIndexOfFirstPathSeparator(String path) {
		int index = path.indexOf("/");
		if (index == -1) {
			index = path.indexOf("\\");
		}
		return index;
	}

	public String unifyStartOfPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	public String removeSrcInPath(String path) {
		if (path.contains("src/")) {
			String tmp = path;
			path = path.substring(path.indexOf("src/") + 4);
			tmp = tmp.substring(0, tmp.indexOf("src/"));
			path = tmp + path;
		}
		if (path.contains("java/")) {
			String tmp = path;
			path = path.substring(path.indexOf("java/") + 5);
			tmp = tmp.substring(0, tmp.indexOf("java/"));
			path = tmp + path;
		}
		return path;
	}

	public String parseProjectPath(String path) {
		if (path.contains("src/")) {
			return path.substring(0, path.indexOf("/src/"));
		}
		return path;
	}

	public String parseToRelativeElementPath(String projectName, String path) {
		final String SRC_FOLDER = "/src/";
		if (path.contains(SRC_FOLDER)) {
			return projectName + "/"
					+ path.substring(path.indexOf(SRC_FOLDER) + 5);
		} else {
			return path;
		}
	}

	public String parsePackageName(String path) {
		if (path.contains("/src/")) {
			String tmp = path.substring(0, path.lastIndexOf("/src/"));

			// case: is full path to resource on file system
			if (countChar(tmp, '/') > 1 || countChar(tmp, '\\') > 1) {
				String projectName = tmp
						.substring(getIndexOfLastPathSeparator(tmp) + 1);
				String relativePath = path.substring(path.lastIndexOf("/src/"));
				path = projectName + relativePath;
			}
		}
		String packageName = path.substring(0, path.lastIndexOf("/"));
		packageName = packageName.substring(1);
		packageName = packageName
				.substring(getIndexOfFirstPathSeparator(packageName));
		packageName = packageName.substring(5);
		packageName = packageName.replace("/", ".");
		if (packageName.contains("/src/")) {
			packageName = packageName.replace("/src", "");
		}
		if (packageName.endsWith(".java")) {
			packageName = packageName
					.substring(0, packageName.indexOf(".java"));
		}
		return packageName;
	}

	public String parsePackagePath(String path) {
		String project = path.substring(0, path.indexOf("/src/"));
		project = project.substring(getIndexOfLastPathSeparator(project) + 1);
		path = parsePackageName(path);
		path = path.replace(".", "/");
		path = project + "/src/" + path;
		if (path.endsWith(".java")) {
			path = path.substring(0, path.lastIndexOf("/"));
		}
		return path;
	}

	public String getRelativeClassPath(String path) {
		String projectPath = parseProjectPath(path);
		String projectName = parseProjectName(projectPath);
		if (path.contains("/src/")) {
			return "/" + projectName + path.substring(path.indexOf("/src/"));
		} else {
			return path;
		}
	}

	public List<JavaElement> readClassFiles(String projectPath,
			String packagePath, String className) {
		CopyOnWriteArrayList<JavaElement> classes = new CopyOnWriteArrayList<JavaElement>();
		String tmpPackagePath = packagePath.substring(1);
		tmpPackagePath = tmpPackagePath.substring(tmpPackagePath.indexOf("/"));
		String path = projectPath + tmpPackagePath;
		List<String> results = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getAbsolutePath());
			}
		}
		for (String filePath : results) {
			try {
				List<String> listString = persistanceOperations
						.readFile(new FileInputStream(filePath));
				String name = filePath
						.substring(getIndexOfLastPathSeparator(filePath) + 1);
				if (!className.isEmpty() && name.equals(className)) {
					classes.add(new JavaClass(name, packagePath + name,
							listString, listString.size()));
					break;
				} else if (className.isEmpty()) {
					classes.add(new JavaClass(name, packagePath + "/" + name,
							listString, listString.size()));
				}
			} catch (FileNotFoundException | FileOperationException e) {
				LogOperations.logError("Class could not be read.", e);
			}
		}
		return classes;
	}

	private int countChar(String s, char c) {
		int counter = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				counter++;
			}
		}
		return counter;
	}

	public String removeToSrcInPath(String path) {
		if (path.contains("src/")) {
			path = path.substring(path.indexOf("src/") + 4);
		}
		return path;
	}
}
