package de.ovgu.variantsync.applicationlayer.features.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeFragment;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Package;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.controller.data.MappingElement;

public class CodeMapping extends Mapping {

	private IMappingOperations classMapping;
	private IMappingOperations packageMapping;
	private Persistable persistanceOperations = ModuleFactory.getPersistanceOperations();

	public CodeMapping() {
		classMapping = new ClassMapping();
		packageMapping = new PackageMapping();
	}

	@Override
	public Element getElement(List<Element> javaPackages, String className, String classPath) {
		return null;
	}

	@Override
	public Element createElement(String pathToProject, String elementName, String pathToElement) {
		return null;
	}

	@Override
	public boolean containsElement(List<Element> elements, String elementName, String pathToElement,
			String contentOfElement) {
		return false;
	}

	private static Collection<CodeLine> changedLines = new ArrayList<CodeLine>();

	@Override
	protected void computeElement(Element element, MappingElement mapping, String name, String path) {
		List<String> code = mapping.getCode();
		boolean ignoreChange = mapping.isIgnore();
		String relativeClassPath = UtilOperations.getInstance().getRelativeClassPath(path);
		if (classMapping.containsElement(element.getChildren(), name, path, "")) {
			Element javaElement = classMapping.getElement(element.getChildren(), name, relativeClassPath);
			List<CodeLine> actualCode = javaElement.getClonedCodeLines();

			// actualCode is wrong...debug
			CodeFragment change = new CodeFragment(code, mapping.getStartLineOfSelection(),
					mapping.getEndLineOfSelection(), mapping.getOffset());
			List<CodeLine> newLines = UtilOperations.getInstance().addCode(change, actualCode);

			javaElement.setCodeLines(newLines);

			changedLines.addAll(getChange(change));

			if (!ignoreChange && mapping.isLastStep()) {
				((Class) javaElement).addChange(changedLines,
						mapping.getPathToProject().substring(mapping.getPathToProject().lastIndexOf("/") + 1), name,
						mapping.getModificationTime());
				changedLines.clear();
			}
		} else {
			Element packageOfClass = packageMapping.getElement(element.getChildren(), name, relativeClassPath);
			if (packageOfClass == null) {
				String packageName = UtilOperations.getInstance().parsePackageName(path);
				path = UtilOperations.getInstance().parsePackagePath(path);
				packageOfClass = new Package(packageName, path);
				element.addChild(packageOfClass);
			}
			Class jc = new Class(name, path + "/" + name, new CodeFragment(code, mapping.getStartLineOfSelection(),
					mapping.getEndLineOfSelection(), mapping.getOffset()));
			if (!ignoreChange && mapping.isLastStep()) {
				jc.addChange(jc.getClonedCodeLines(), mapping.getModificationTime());
			}
			packageOfClass.addChild(jc);
		}
	}

	private Collection<CodeLine> getChange(CodeFragment cf) {
		Collection<CodeLine> code = new ArrayList<CodeLine>();
		List<String> codeLines = cf.getCode();
		int i = cf.getStartLine();
		for (String line : codeLines) {
			code.add(new CodeLine(line, i, true));
			i++;
		}
		return code;
	}

	private List<String> getCodeLinesFromFile(String projectName, String className) {
		List<String> linesOfFile = null;
		List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectName)) {
				IResource javaClass = null;
				try {
					javaClass = findFileRecursively(p, className);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (javaClass != null) {
					IFile file = (IFile) javaClass;
					try {
						linesOfFile = persistanceOperations.readFile(file.getContents(), file.getCharset());
						break;
					} catch (FileOperationException | CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return linesOfFile;
	}

	private IFile findFileRecursively(IContainer container, String name) throws CoreException {
		for (IResource r : container.members()) {
			if (r instanceof IContainer) {
				IFile file = findFileRecursively((IContainer) r, name);
				if (file != null) {
					return file;
				}
			} else if (r instanceof IFile && r.getName().equals(name)) {
				return (IFile) r;
			}
		}

		return null;
	}

	@Override
	protected Element createProject(String pathToProject, String elementName, String elementPath,
			MappingElement mapping) {
		String relativeClassPath = UtilOperations.getInstance().getRelativeClassPath(elementPath);
		return ((ClassMapping) classMapping).addClassWithCode(
				UtilOperations.getInstance().parseProjectPath(elementPath), elementName, relativeClassPath,
				mapping.getCode(), mapping.getStartLineOfSelection(), mapping.getEndLineOfSelection(),
				mapping.getOffset());
	}

	@Override
	protected boolean removeElement(Element javaElement, List<Element> elements, String elementName, String elementPath,
			CodeFragment code, boolean isFirstStep, boolean isLastStep, List<String> wholeClass,
			long modificationTime) {
		String nameOfClass = javaElement.getName();
		String pathOfClass = UtilOperations.getInstance().removeToSrcInPath(javaElement.getPath());
		if (nameOfClass.equals(elementName)
				&& pathOfClass.equals(UtilOperations.getInstance().removeToSrcInPath(elementPath))) {

			List<CodeLine> tmpCode = new ArrayList<CodeLine>();
			List<CodeLine> actualCode = javaElement.getClonedCodeLines();
			for (CodeLine cl : actualCode) {
				tmpCode.add(cl.clone());
			}
			List<CodeLine> codeLines = new ArrayList<CodeLine>();
			if (tmpCode != null && !tmpCode.isEmpty())
				codeLines = UtilOperations.getInstance().removeCode(code.getStartLine(), code.getEndLine(), tmpCode);
			if (isLastStep) {
				String projectName = elementPath.substring(0, elementPath.indexOf("/src"));
				if (projectName.contains("/"))
					projectName = projectName.substring(projectName.lastIndexOf("/"));
				((Class) javaElement).addChange(codeLines, projectName, elementName, modificationTime);
			}

			return javaElement.setCodeLines(codeLines);
		}
		return false;
	}

	@Override
	protected boolean checkElement(Element element, String elementName, String pathToElement) {
		return false;
	}

	@Override
	protected Element computeElement(Element element, String name, String path) {
		return null;
	}

}
