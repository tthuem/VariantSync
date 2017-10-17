package de.tubs.variantsync.core.syncronization;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.syncronization.compare.DeltaCompareViewer;
import de.tubs.variantsync.core.utilities.LogOperations;

public class SynchronizationHandler {

	public static boolean handleSynchronization(IProject project, IDelta<?> delta) {
		IFile file = project.getFile(delta.getResource().getProjectRelativePath());
		try {
			IDeltaFactory factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
			if (factory.verifyDelta(file, delta)) {
				IFile newFile = factory.applyDelta(file, delta);
				if (!newFile.getContents().toString().equals(file.getContents().toString())) {
					delta.addSynchronizedProject(project);
					return true;
				}
			} else {
				WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new DeltaCompareViewer(file, delta));
				dialog.create();
				if (dialog.open() == Window.OK) {
					delta.addSynchronizedProject(project);
					return true;
				}
			}
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("DeltaFactory not found", e);
		} catch (CoreException e) {
			LogOperations.logError("File could not be read", e);
		}
		return false;
	}

}
