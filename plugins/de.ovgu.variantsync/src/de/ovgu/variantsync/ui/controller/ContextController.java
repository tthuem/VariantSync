package de.ovgu.variantsync.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.ContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.view.context.MarkerHandler;
import difflib.Delta;

/**
 * Manages context operations and data exchanges between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class ContextController extends AbstractController {

	private ContextOperations contextOperations = ModuleFactory.getContextOperations();
	private Persistable persistanceOperations = ModuleFactory.getPersistanceOperations();

	private boolean isPartActivated = false;
	private boolean featureView = false;
	private boolean productView = false;

	public IFile findFileRecursively(IContainer container, String name) throws CoreException {
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

	public java.util.List<String> getBaseCode(CodeChange ch) {
		try {
			return persistanceOperations.readFile(
					new FileInputStream(new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
							+ VariantSyncConstants.CHANGES_PATH
							+ String.valueOf(ch.getTimestamp() + VariantSyncConstants.BASE_VERION))));
		} catch (FileOperationException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public java.util.List<String> getNewCode(CodeChange ch) {
		try {
			return persistanceOperations.readFile(
					new FileInputStream(new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
							+ VariantSyncConstants.CHANGES_PATH
							+ String.valueOf(ch.getTimestamp() + VariantSyncConstants.NEW_VERION))));
		} catch (FileOperationException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// private java.util.List<String> getCodeFromFile(IFile file, long
	// timestamp)
	// throws CoreException, FileOperationException {
	// IFileState[] states = file.getHistory(null);
	// for (IFileState state : states) {
	// if (state.getModificationTime() == timestamp) {
	// return persistanceOperations.readFile(state.getContents(),
	// state.getCharset());
	// }
	// }
	// return null;
	// }
	//
	// private java.util.List<String> getAncientCodeFromFile(IFile file, long
	// timestamp)
	// throws CoreException, FileOperationException {
	// IFileState[] states = file.getHistory(null);
	// int i = 0;
	// for (IFileState state : states) {
	// if (state.getModificationTime() == timestamp) {
	// return persistanceOperations.readFile(states[i + 1].getContents(),
	// states[i + 1].getCharset());
	// }
	// i++;
	// }
	// return null;
	// }

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
					IFile file = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
					System.out.println("\n====== LOCATION OF ACTIVE FILE IN EDITOR ======");
					System.out.println(file.getLocation());
					System.out.println("===============================================");
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

	public Collection<CodeChange> getChanges(String fe, String projectName, String className) {
		return contextOperations.getChanges(fe, projectName, className);
	}

	public Collection<Delta> getConflictingDeltas(Collection<String> ancestor, Collection<String> left,
			Collection<String> right) {
		return contextOperations.getConflictingDeltas(ancestor, left, right);
	}

	public List<String> getSyncTargets(String fe, String projectName, String className) {
		return contextOperations.getSyncTargets(fe, projectName, className);
	}

	public Collection<String> getAutoSyncTargets(String fe, String projectName, String className,
			Collection<String> base, Collection<String> left) {
		return contextOperations.getAutoSyncTargets(fe, projectName, className, base, left);
	}

	public List<String> getAutoSyncTargetsForVariant(String fe, String projectName, String className,
			List<CodeLine> ancestor, List<CodeLine> left) {
		className = className.split(":")[1].trim();
		return contextOperations.getAutoSyncTargetsForVariant(fe, projectName, className, ancestor, left);
	}

	public Collection<String> getConflictedSyncTargets(String fe, String projectName, String className,
			Collection<String> base, Collection<String> left) {
		return contextOperations.getConflictSyncTargets(fe, projectName, className, base, left);
	}

	public List<String> getConflictedSyncForVariant(String fe, String projectName, String className,
			List<CodeLine> ancestor, List<CodeLine> left) {
		className = className.split(":")[1].trim();
		return contextOperations.getConflictedSyncForVariant(fe, projectName, className, ancestor, left);
	}

	public Collection<String> getClasses(String fe, String projectName) {
		return contextOperations.getClasses(fe, projectName);
	}

	public List<CodeLine> getTargetCode(String fe, String projectName, String className) {
		return contextOperations.getTargetCode(fe, projectName, className);
	}

	public CodeHighlighting getContextColor(String featureExpression) {
		return contextOperations.getContextColor(featureExpression);
	}

	public java.util.List<String> getTargetFile(String selectedFeatureExpression, String projectName,
			String className) {
		return contextOperations.getLinesOfFile(selectedFeatureExpression, projectName, className);
	}

	public void setBaseVersion(IFile file) {
		isPartActivated = true;
		contextOperations.setBaseVersion(file);
	}

	public void refreshContext(boolean isAutomaticSync, String fe, String projectName, String filename,
			java.util.List<String> codeWC, Collection<String> syncCode) {
		contextOperations.refresh(isAutomaticSync, fe, projectName, filename, codeWC, syncCode);
	}

	public void removeTagging(String path) {
		contextOperations.removeTagging(path);
	}

	public void setFeatureView(boolean b) {
		this.featureView = b;
	}

	public boolean isFeatureView() {
		return this.featureView;
	}

	public void setProductView(boolean b) {
		this.productView = b;
	}

	public boolean isProductView() {
		return productView;
	}

	public boolean isAlreadySynchronized(String fe, long key, String source, String target) {
		return contextOperations.isAlreadySynchronized(fe, key, source, target);
	}

	public void addSynchronizedChange(String fe, long key, String source, String target) {
		contextOperations.addSynchronizedChange(fe, key, source, target);
	}

	public List<String> getFeatures(String variant) {
		return contextOperations.getFeatures(variant);
	}

	public Map<String, Collection<CodeChange>> getChangesForVariant(String selectedFeature, String selectedVariant,
			String selectedClass) {
		String className = selectedClass.split(":")[1].trim();
		return contextOperations.getChangesForVariant(selectedFeature, selectedVariant, className);
	}

	public Collection<String> getClassesForVariant(String fe, String projectName) {
		return contextOperations.getClassesForVariant(fe, projectName);
	}

}