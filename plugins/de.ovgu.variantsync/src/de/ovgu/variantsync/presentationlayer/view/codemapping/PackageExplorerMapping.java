package de.ovgu.variantsync.presentationlayer.view.codemapping;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

/**
 * Realizes java-element to feature mapping. Java-Elements can be chosen in
 * package explorer.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 04.06.2015
 */
public class PackageExplorerMapping extends DynamicMapMenu {

	private String elementName;
	private IPath elementPath;
	private JavaElements elementType;
	private IProject project;

	@SuppressWarnings("restriction")
	@Override
	protected IProject getProject() {
		ISelectionService service = getWorkbenchPage().getWorkbenchWindow()
				.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		Object selectedElement = structured.getFirstElement();
		IJavaProject javaProject = null;
		org.eclipse.jdt.internal.core.JavaElement element = null;
		if (selectedElement instanceof org.eclipse.jdt.internal.core.PackageFragment) {
			element = ((org.eclipse.jdt.internal.core.PackageFragment) structured
					.getFirstElement());
			elementType = JavaElements.PACKAGE;
		} else if (selectedElement instanceof org.eclipse.jdt.internal.core.CompilationUnit) {
			element = ((org.eclipse.jdt.internal.core.CompilationUnit) structured
					.getFirstElement());
			elementType = JavaElements.CLASS;
		} else if (selectedElement instanceof org.eclipse.jdt.internal.core.SourceType) {
			element = ((org.eclipse.jdt.internal.core.SourceType) structured
					.getFirstElement());
			elementType = JavaElements.CLASS;
		} else if (selectedElement instanceof org.eclipse.jdt.internal.core.SourceMethod) {
			element = ((org.eclipse.jdt.internal.core.SourceMethod) structured
					.getFirstElement());
			elementType = JavaElements.METHOD;
		}
		javaProject = element.getJavaProject();
		elementPath = element.getPath();
		elementName = element.getElementName();
		project = javaProject.getProject();
		return project;
	}

	@Override
	protected void handleSelection(String feature) {
		controller.addFeatureMapping(feature, elementName, elementType,
				elementPath, project);
	}

	/**
	 * Returns reference to active workbench page.
	 * 
	 * @return the active workbench page
	 */
	private IWorkbenchPage getWorkbenchPage() {
		IWorkbench iworkbench = PlatformUI.getWorkbench();
		IWorkbenchWindow iworkbenchwindow = iworkbench
				.getActiveWorkbenchWindow();
		return iworkbenchwindow.getActivePage();
	}
}