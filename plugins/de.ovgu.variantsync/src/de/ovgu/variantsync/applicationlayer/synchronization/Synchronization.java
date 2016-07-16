package de.ovgu.variantsync.applicationlayer.synchronization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.IChangedFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperations;
import de.ovgu.variantsync.applicationlayer.merging.Merging;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.utilities.LogOperations;

/**
 * Provides functions to synchronize projects.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 17.05.2015
 */
abstract class Synchronization {

	protected DeltaOperations deltaOperations = ModuleFactory
			.getDeltaOperations();
	protected Merging mergeOperations = ModuleFactory
			.getMergeOperations();
	protected Persistable persistanceOperations = ModuleFactory
			.getPersistanceOperations();

	protected abstract void addFile();

	protected abstract void addFolder();

	protected abstract void removeFolder();

	protected abstract void removeFile();

	protected abstract void changeFile(ResourceChangesFilePatch patch);

	/**
	 * Identifies changed file (patch) on file-system and reads file content.
	 * 
	 * @param project
	 *            project which contains changed file
	 * @param patch
	 *            changed file
	 * @return list with file content
	 */
	public List<String> getFileContent(IProject project,
			ResourceChangesFilePatch patch) {
		IFile file = project.getFile(patch.getPath());
		List<String> fileLines = new LinkedList<String>();
		if (file.exists() && file.getType() == IResource.FILE) {
			if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
				try {
					file.refreshLocal(IResource.DEPTH_ZERO, null);
				} catch (CoreException e) {
					LogOperations.logError(
							"File could not be refreshed in workspace.", e);
				}
			}
			try {
				fileLines = persistanceOperations.readFile(file.getContents());
			} catch (FileOperationException | CoreException e) {
				LogOperations.logError("File could not be accessed.", e);
			}
		}
		return fileLines;
	}

	/**
	 * Returns all patches of changed file which exists in the project. Patches
	 * are increasing sorted by creation time.
	 * 
	 * @param changedFile
	 *            changed file
	 * @param project
	 *            project containing changed file
	 * @return list of patches
	 */
	public List<ResourceChangesFilePatch> getPatchsFromProject(
			ResourceChangesFile changedFile, IProject project) {
		List<ResourceChangesFilePatch> patches = new ArrayList<ResourceChangesFilePatch>();
		List<IChangedFile> files = changedFile.getChildren();
		for (IChangedFile temp : files) {
			if ((temp instanceof ResourceChangesFilePatch)
					&& ((ResourceChangesFilePatch) temp).getProject().equals(
							project)) {
				patches.add((ResourceChangesFilePatch) temp);
			}
		}
		Collections.sort(patches, ResourceChangesFilePatch.TIMECOMPARATOR);
		return patches;
	}

	/**
	 * Returns all monitored projects containing given changed file.
	 * 
	 * @param patch
	 *            changed file
	 * @return list with project names
	 */
	public List<String> getSynchronizedProjects(ResourceChangesFilePatch patch) {
		return VariantSyncPlugin.getDefault()
				.getSynchroInfoFrom(patch.getProject())
				.getMonitoredProjects(patch.getName());
	}
}
