package de.tubs.variantsync.core.syncronization;

import java.util.Arrays;
import java.util.List;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.exceptions.DiffException;
import de.tubs.variantsync.core.monitor.CodeMappingHandler;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.HistoryStore;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 *
 * This class handles the synchronization between variants
 *
 * @author Christopher Sontag
 */
public class SynchronizationHandler {

	/**
	 * Synchronizes the given delta in the given project. Returns true if the delta is successfully applied.
	 *
	 * @param project
	 * @param delta
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean handleSynchronization(IProject project, IDelta<?> delta) {
		VariantSyncPlugin.removeResourceChangeListener();
		final IFile fileRight = project.getFile(delta.getResource().getProjectRelativePath());
		final IFile fileLeft = delta.getProject().getFile(delta.getResource().getProjectRelativePath());

		try {
			final IDeltaFactory factory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());

			// If delta is synchronisable
			if (factory.verifyDelta(fileRight, delta)) {
				final IFile newFile = factory.applyDelta(fileRight, delta);
				if (!newFile.getContents().toString().equals(fileRight.getContents().toString())) {
					delta.addSynchronizedProject(project);

					final IDelta<?> newDelta = factory.createDeltas(newFile, delta);
					//TODO: AST REFACTORING
					CodeMappingHandler.addCodeMappingsForDeltas(Arrays.asList(newDelta));

					VariantSyncPlugin.addResourceChangeListener();
					return true;
				}
				// if manual merge is needed
			} else {
				// Editor
				final org.eclipse.compare.CompareConfiguration compconf = new org.eclipse.compare.CompareConfiguration();
				compconf.setLeftLabel(delta.getProject().getName());
				compconf.setRightLabel(project.getName());
				compconf.setAncestorLabel("Ancestor");
				compconf.setLeftEditable(false);
				compconf.setRightEditable(true);

				final HistoryStore historyStore = new HistoryStore();
				final IFile fileBase = historyStore.getState(fileLeft, delta.getTimestamp());

				final String originalTmpName = String.valueOf(System.currentTimeMillis());
//				fileRight.copy(fileRight.getProject().getFolder(".tmp").getFile(originalTmpName + ".txt").getFullPath(), true, null);
				if (!fileRight.getProject().getFolder(".tmp").exists()) {
					fileRight.getProject().getFolder(".tmp").create(true, false, null);
				}
				final IFile fileOriginal = fileRight.getProject().getFolder(".tmp").getFile(originalTmpName + ".txt");
				fileOriginal.create(fileRight.getContents(), true, null);
				final CompareEditorInput rci = new ResourceCompareInput(compconf, fileBase, fileLeft, fileRight);
				rci.setDirty(true);

				CompareUI.openCompareDialog(rci);
				delta.addSynchronizedProject(project);
				VariantSyncPlugin.addResourceChangeListener();

				try {
					final List<IDelta<?>> deltas = factory.createDeltas(fileRight, fileOriginal);
					for (final IDelta<?> deltaFile : deltas) {
						deltaFile.setContext(delta.getContext());
					}
					//TODO: AST REFACTORING
					CodeMappingHandler.addCodeMappingsForDeltas(deltas);
				} catch (final DiffException e) {
					return false;
				}

				return rci.okPressed();
			}
		} catch (final NoSuchExtensionException e) {
			LogOperations.logError("DeltaFactory not found", e);
		} catch (final CoreException e) {
			LogOperations.logError("File could not be read", e);
		}
		VariantSyncPlugin.addResourceChangeListener();
		return false;
	}

}
