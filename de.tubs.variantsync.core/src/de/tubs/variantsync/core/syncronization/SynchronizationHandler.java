package de.tubs.variantsync.core.syncronization;

import java.util.Arrays;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.monitor.CodeMappingHandler;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.HistoryStore;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

public class SynchronizationHandler {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean handleSynchronization(IProject project, IDelta<?> delta) {
		VariantSyncPlugin.removeResourceChangeListener();
		IFile fileRight = project.getFile(delta.getResource().getProjectRelativePath());
		IFile fileLeft = delta.getProject().getFile(delta.getResource().getProjectRelativePath());

		try {
			IDeltaFactory factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
			if (factory.verifyDelta(fileRight, delta)) {
				IFile newFile = factory.applyDelta(fileRight, delta);
				if (!newFile.getContents().toString().equals(fileRight.getContents().toString())) {
					delta.addSynchronizedProject(project);

					IDelta<?> newDelta = factory.createDeltas(newFile, delta);
					CodeMappingHandler.addCodeMappingsForDeltas(Arrays.asList(newDelta));

					VariantSyncPlugin.addResourceChangeListener();
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

				HistoryStore historyStore = new HistoryStore();
				IFile fileBase = historyStore.getState(fileLeft, delta.getTimestamp());
				CompareEditorInput rci = new ResourceCompareInput(compconf, fileBase, fileLeft, fileRight);
				rci.setDirty(true);

				CompareUI.openCompareDialog(rci);
				delta.addSynchronizedProject(project);
				VariantSyncPlugin.addResourceChangeListener();
				return rci.okPressed();
			}
		} catch (NoSuchExtensionException e) {
			LogOperations.logError("DeltaFactory not found", e);
		} catch (CoreException e) {
			LogOperations.logError("File could not be read", e);
		}
		VariantSyncPlugin.addResourceChangeListener();
		return false;
	}

}
