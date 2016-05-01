package de.ovgu.variantsync.presentationlayer.controller;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.presentationlayer.view.context.MarkerHandler;

/**
 * Manages context operations and data exchanges between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class ContextController extends AbstractController {

	private IContextOperations contextOperations = ModuleFactory
			.getContextOperations();
	private boolean isPartActivated = false;

	public String getActiveFeatureContext() {
		return contextOperations.getActiveFeatureContext();
	}

	public String getActiveProject() {
		return contextOperations.getActiveProject();
	}

	public void activateContext(String featureExpression) {
		if (!isPartActivated) {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IWorkbenchPart part = page.getActivePart();
			if (part instanceof IEditorPart) {
				if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
					IFile file = ((IFileEditorInput) ((EditorPart) part)
							.getEditorInput()).getFile();
					System.out
							.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR ======");
					System.out.println(file.getLocation());
					System.out
							.println("===============================================");
					MarkerHandler.getInstance().refreshMarker(file);
					setBaseVersion(file);
				}
			}
		}
		contextOperations.activateContext(featureExpression);
	}

	public void stopContextRecording() {
		contextOperations.stopRecording();
	}

	public Collection<String> getProjects(String fe) {
		return contextOperations.getProjects(fe);
	}

	public Collection<CodeChange> getChanges(String fe, String projectName,
			String className) {
		return contextOperations.getChanges(fe, projectName, className);
	}

	public List<String> getSyncTargets(String fe, String projectName,
			String className) {
		return contextOperations.getSyncTargets(fe, projectName, className);
	}

	public Collection<String> getClasses(String fe, String projectName) {
		return contextOperations.getClasses(fe, projectName);
	}

	public List<CodeLine> getTargetCode(String fe, String projectName,
			String className) {
		return contextOperations.getTargetCode(fe, projectName, className);
	}

	public CodeHighlighting getContextColor(String featureExpression) {
		return contextOperations.getContextColor(featureExpression);
	}

	public java.util.List<CodeLine> getTargetCodeWholeClass(
			String selectedFeatureExpression, String projectName,
			String className) {
		return contextOperations.getTargetCodeWholeClass(
				selectedFeatureExpression, projectName, className);
	}

	public void setBaseVersion(IFile file) {
		isPartActivated = true;
		contextOperations.setBaseVersion(file);
	}

	public void refreshContext(boolean isAutomaticSync, String fe,
			String projectName, String filename,
			java.util.List<CodeLine> codeWC, java.util.List<CodeLine> syncCode) {
		contextOperations.refresh(isAutomaticSync, fe, projectName, filename,
				codeWC, syncCode);
	}

	public void removeTagging(String path) {
		contextOperations.removeTagging(path);
	}

}