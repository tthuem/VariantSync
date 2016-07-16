package de.ovgu.variantsync.ui.view.codemapping;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.data.JavaElements;
import de.ovgu.variantsync.ui.controller.data.MappingElement;
import de.ovgu.variantsync.ui.view.context.MarkerHandler;

public class RemoveMarkers implements IEditorActionDelegate {

	public RemoveMarkers() {
		super();
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart editor) {
	}

	/*
	 * Method to find and remove the markers that are directly related to the
	 * specified ifile.
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@SuppressWarnings("restriction")
	@Override
	public void run(IAction action) {
		TextSelection textSelection = CodeMarkerFactory.getTextSelection();
		TreeSelection treeSelection = CodeMarkerFactory.getTreeSelection();
		MappingElement map = null;
		if (textSelection != null) {
			IFile file = (IFile) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor().getEditorInput().getAdapter(IFile.class);
			int startLine = textSelection.getStartLine();
			int endLine = textSelection.getEndLine();
			int offset = textSelection.getOffset();
			MarkerHandler.getInstance().removeMarker(file, startLine, endLine);
			map = new MappingElement("", file.getName(),
					JavaElements.CODE_FRAGMENT, file.getFullPath().toString(),
					textSelection.getText(), startLine, endLine, offset);
		} else if (treeSelection != null) {
			String elementName;
			IPath elementPath;
			JavaElements elementType = null;
			IProject project;
			Object selectedElement = treeSelection.getFirstElement();
			IJavaProject javaProject = null;
			org.eclipse.jdt.internal.core.JavaElement element = null;
			if (selectedElement instanceof org.eclipse.jdt.internal.core.PackageFragment) {
				element = ((org.eclipse.jdt.internal.core.PackageFragment) treeSelection
						.getFirstElement());
				elementType = JavaElements.PACKAGE;
				IJavaElement[] children = null;
				try {
					children = element.getChildren();
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}
				for (IJavaElement child : children) {
					deleteMarkersOfFile(child.getResource());
				}
			} else if (selectedElement instanceof org.eclipse.jdt.internal.core.CompilationUnit) {
				element = ((org.eclipse.jdt.internal.core.CompilationUnit) treeSelection
						.getFirstElement());
				elementType = JavaElements.CLASS;
				deleteMarkersOfFile(element.getResource());
			} else if (selectedElement instanceof org.eclipse.jdt.internal.core.SourceType) {
				element = ((org.eclipse.jdt.internal.core.SourceType) treeSelection
						.getFirstElement());
				elementType = JavaElements.CLASS;
				deleteMarkersOfFile(element.getResource());
			} else if (selectedElement instanceof org.eclipse.jdt.internal.core.SourceMethod) {
				element = ((org.eclipse.jdt.internal.core.SourceMethod) treeSelection
						.getFirstElement());
				elementType = JavaElements.METHOD;
				// not yet implemented
			}
			javaProject = element.getJavaProject();
			elementPath = element.getPath();
			elementName = element.getElementName();
			project = javaProject.getProject();
			map = new MappingElement("", elementName, elementType,
					elementPath.toString(), project.getLocation().toFile()
							.getAbsolutePath());
		}
		ControllerHandler.getInstance().getFeatureController()
				.removeMapping(map);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// not necessary
	}

	private void deleteMarkersOfFile(IResource file) {
		List<IMarker> markers = CodeMarkerFactory.findMarkers(file);
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
