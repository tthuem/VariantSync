package de.ovgu.variantsync.applicationlayer.synchronization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ChangeTypes;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.IChangedFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import difflib.Delta;
import difflib.Patch;

/**
 * Computes possible and compatible targets to synchronize files and folders.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 17.05.2015
 */
class TargetCalculation extends Synchronization {

	private String path;
	private IProject project;
	private List<IProject> conflictProjectList;
	private List<IProject> projectList;

	/**
	 * Computes possible targets for synchronization. Identifies projects which
	 * would cause a conflict by subtracting conflict project list from
	 * supported project list.
	 * 
	 * @param patch
	 *            changed file
	 * @return list with compatible projects
	 */
	public List<IProject> getProjectList(ResourceChangesFilePatch patch) {
		projectList = VariantSyncPlugin.getDefault().getSupportProjectList();
		project = patch.getProject();
		path = patch.getPath();
		conflictProjectList = new ArrayList<IProject>(0);
		String status = patch.getStatus();
		if (status.equals(ChangeTypes.ADDFILE)) {
			addFile();
		}
		if (status.equals(ChangeTypes.REMOVEFILE)) {
			removeFile();
		}
		if (status.equals(ChangeTypes.ADDFOLDER)) {
			addFolder();
		}
		if (status.equals(ChangeTypes.REMOVEFOLDER)) {
			removeFolder();
		}
		if (status.equals(ChangeTypes.CHANGE)) {
			changeFile(patch);
		}
		projectList.removeAll(conflictProjectList);
		return projectList;
	}

	/**
	 * Identifies projects which causes a conflict by adding new file.
	 */
	@Override
	protected void addFile() {
		if (project.getFile(path).exists()) {
			for (IProject p : projectList) {
				if (project.equals(p)) {
					conflictProjectList.add(p);
					continue;
				}
				IFile file = p.getFile(path);
				if (file.exists()) {
					conflictProjectList.add(p);
				}
			}
		} else {
			conflictProjectList.addAll(projectList);
		}
	}

	/**
	 * Identifies projects which causes a conflict by adding new folder.
	 * Conflict appeared if new folder already exists in synchronized project.
	 */
	@Override
	protected void addFolder() {
		if (project.getFolder(path).exists()) {
			for (IProject p : projectList) {
				if (project.equals(p)) {
					conflictProjectList.add(p);
					continue;
				}
				IFolder folder = p.getFolder(path);
				if (folder.exists()) {
					conflictProjectList.add(p);
				}
			}
		} else {
			conflictProjectList.addAll(projectList);
		}
	}

	/**
	 * Identifies projects which causes a conflict by removing existing folder.
	 */
	@Override
	protected void removeFolder() {
		for (IProject p : projectList) {
			if (project.equals(p)) {
				conflictProjectList.add(p);
				continue;
			}
			IFolder folder = p.getFolder(path);
			if (!folder.exists()) {
				conflictProjectList.add(p);
			}
		}
	}

	/**
	 * Identifies projects which causes a conflict by removing existing file.
	 */
	@Override
	protected void removeFile() {
		for (IProject p : projectList) {
			if (project.equals(p)) {
				conflictProjectList.add(p);
				continue;
			}
			IFile file = p.getFile(path);
			if (!file.exists()) {
				conflictProjectList.add(p);
			}
		}
	}

	/**
	 * Identifies projects which cause a conflict by changing an existing file.
	 * 
	 * @param resourcePatch
	 *            changed original file
	 */
	@Override
	protected void changeFile(ResourceChangesFilePatch resourcePatch) {
		List<ResourceChangesFilePatch> patchs = getPatchsFromProject(
				(ResourceChangesFile) resourcePatch.getParent(), project);

		Comparator<IChangedFile> comparator = Collections
				.reverseOrder(ResourceChangesFilePatch.TIMECOMPARATOR);
		Collections.sort(patchs, comparator);

		Patch patch = deltaOperations.getPatch(resourcePatch);

		List<String> fileLines = getFileContent(project, resourcePatch);
		if (!fileLines.isEmpty()) {
			boolean fileRemove = false;
			for (ResourceChangesFilePatch p : patchs) {
				if (p.getStatus().equals(ChangeTypes.REMOVEFILE)
						|| p.getStatus().equals(ChangeTypes.ADDFILE)) {
					conflictProjectList.addAll(projectList);
					fileRemove = true;
					break;
				}
				if (resourcePatch.getTimestamp() == p.getTimestamp()) {
					break;
				}
				List<String> temp = deltaOperations.unpatchText(fileLines,
						patch);

				// really neccessary?
				fileLines.clear();
				for (Object o : temp) {
					fileLines.add((String) o);
				}
			}
			if (!fileRemove) {
				for (IProject checkProject : projectList) {
					if (project.equals(checkProject)) {
						conflictProjectList.add(checkProject);
						continue;
					}
					IFile file = checkProject.getFile(path);
					if (!file.exists()
							|| (file.exists() && file.getType() != IResource.FILE)) {
						conflictProjectList.add(checkProject);
						continue;
					}
					Patch pf12 = patch;
					List<String> checkFileList = getFileContent(checkProject,
							resourcePatch);
					List<String> content1 = deltaOperations.unpatchText(
							fileLines, patch);
					Patch pf13 = deltaOperations.computeDifference(content1,
							checkFileList);
					List<Delta> deltas12 = pf12.getDeltas();
					List<Delta> deltas13 = pf13.getDeltas();
					if (mergeOperations.checkConflict(deltas12, deltas13)) {
						conflictProjectList.add(checkProject);
					}
				}
			}
		} else {
			conflictProjectList.addAll(projectList);
		}
	}
}
