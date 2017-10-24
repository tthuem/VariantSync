package de.tubs.variantsync.core.syncronization;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

public class SynchronizationHandler {

	public static boolean handleSynchronization(IProject project, IDelta<?> delta) {
		IFile fileRight = project.getFile(delta.getResource().getProjectRelativePath());
		IFile fileLeft = delta.getProject().getFile(delta.getResource().getProjectRelativePath());

		try {
			IDeltaFactory factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
			if (factory.verifyDelta(fileRight, delta)) {
				IFile newFile = factory.applyDelta(fileRight, delta);
				if (!newFile.getContents().toString().equals(fileRight.getContents().toString())) {
					delta.addSynchronizedProject(project);
					return true;
				}
			} else {
				// Editor
				org.eclipse.compare.CompareConfiguration compconf = new org.eclipse.compare.CompareConfiguration();
				compconf.setLeftLabel(delta.getProject().getName());
				compconf.setRightLabel(project.getName());
				compconf.setAncestorLabel("Ancestor");
				compconf.setLeftEditable(false);
				compconf.setRightEditable(true);

				CompareEditorInput rci = new ResourceCompareInput(compconf, factory.reverseDelta(fileLeft, delta), fileLeft, fileRight);
				rci.setDirty(true);

				CompareUI.openCompareDialog(rci);
				delta.addSynchronizedProject(project);
				return true;
			}
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("DeltaFactory not found", e);
		} catch (CoreException e) {
			LogOperations.logError("File could not be read", e);
		}

		return false;
	}

}
