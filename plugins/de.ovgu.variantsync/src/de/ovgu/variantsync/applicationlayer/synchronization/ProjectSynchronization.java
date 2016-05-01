package de.ovgu.variantsync.applicationlayer.synchronization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FolderOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.XMLException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItem;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItemStorage;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorSet;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ChangeTypes;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.IChangedFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFile;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;
import difflib.Patch;

/**
 * Performs synchronization of two or more projects.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 17.05.2015
 */
class ProjectSynchronization extends Synchronization {

	private String status;
	private String path;
	private String patchFileName;
	private IProject patchProject;
	private List<IProject> result;
	private static final String ERROR_REFRESH_INFOFILE = "Info file .variantsyncInfo could not be refreshed in workspace.";

	/**
	 * Synchronizes projects with patch.
	 * 
	 * @param result
	 *            target projects for synchronization
	 * @param patch
	 *            patch to synchronize
	 */
	public void synchronize(Object[] result, ResourceChangesFilePatch patch) {
		status = patch.getStatus();
		path = patch.getPath();
		patchFileName = patch.getPatchFileName();
		patchProject = patch.getProject();
		this.result = new ArrayList<IProject>();
		for (Object o : result) {
			this.result.add((IProject) o);
		}
		if (status.equals(ChangeTypes.ADDFILE)) {
			addFile();
		} else if (status.equals(ChangeTypes.REMOVEFILE)) {
			removeFile();
		} else if (status.equals(ChangeTypes.ADDFOLDER)) {
			addFolder();
		} else if (status.equals(ChangeTypes.REMOVEFOLDER)) {
			removeFolder();
		} else if (status.equals(ChangeTypes.CHANGE)) {
			changeFile(patch);
		}
	}

	/**
	 * Adds a file to compatible target projects.
	 */
	@Override
	protected void addFile() {
		IPath relativePath = new Path(path);
		for (Object object : result) {
			IProject project = (IProject) object;
			if (patchProject.getFile(relativePath).exists()) {
				InputStream source;
				try {
					source = patchProject.getFile(relativePath).getContents();
					IPath iPath = project.getFile(relativePath).getParent()
							.getProjectRelativePath();
					createDir(iPath, project);
					IFile addFile = project.getFile(relativePath);
					MonitorSet.getInstance().addSynchroItem(addFile);
					actualizeAdminFolder(project, patchFileName);
					addFile.create(source, IResource.FORCE, null);
				} catch (CoreException e) {
					LogOperations
							.logError("File content could not be read.", e);
				}
			}
		}
	}

	/**
	 * Adds a folder to compatible target projects.
	 */
	@Override
	protected void addFolder() {
		IPath relativePath = new Path(path);
		for (Object object : result) {
			IProject project = (IProject) object;
			IFolder folder = project.getFolder(relativePath);
			if (!folder.exists()) {
				actualizeAdminFolder(project, patchFileName);
				try {
					persistanceOperations.mkdirs(folder);
				} catch (FolderOperationException e) {
					LogOperations.logError("Folder could not be created.", e);
				}
			}
		}
	}

	/**
	 * Removes a folder from target projects.
	 */
	@Override
	protected void removeFolder() {
		IPath relativePath = new Path(path);
		for (Object object : result) {
			IProject project = (IProject) object;
			if (project.getFolder(relativePath).exists()) {
				try {
					IFolder removeFolder = project.getFolder(relativePath);
					actualizeAdminFolder(project, patchFileName);
					persistanceOperations.deldirs(removeFolder);
				} catch (FolderOperationException e) {
					LogOperations.logError("Folder could not be removed.", e);
				}
			}
		}
	}

	/**
	 * Removes a file from target projects.
	 */
	@Override
	protected void removeFile() {
		IPath relativePath = new Path(path);
		for (Object object : result) {
			IProject project = (IProject) object;
			if (project.getFile(relativePath).exists()) {
				try {
					IFile removeFile = project.getFile(relativePath);
					MonitorSet.getInstance().addSynchroItem(removeFile);
					actualizeAdminFolder(project, patchFileName);
					removeFile.delete(true, null);
				} catch (CoreException e) {
					LogOperations.logError("File could not be deleted.", e);
				}
			}
		}
	}

	/**
	 * Changes a file in target projects. Merges new contents in target files.
	 * 
	 * @param patch
	 *            changed original file
	 */
	@Override
	protected void changeFile(ResourceChangesFilePatch patch) {
		List<String> fList1 = new LinkedList<String>();
		List<String> fList2 = new LinkedList<String>();
		List<String> fList3 = new LinkedList<String>();
		Patch patchObject = deltaOperations.getPatch(patch);
		List<ResourceChangesFilePatch> patchs = getPatchsFromProject(
				(ResourceChangesFile) patch.getParent(), patchProject);
		Comparator<IChangedFile> comparator = Collections
				.reverseOrder(ResourceChangesFilePatch.TIMECOMPARATOR);
		Collections.sort(patchs, comparator);
		List<String> fileLines = getFileContent(patchProject, patch);
		if (!fileLines.isEmpty()) {
			boolean fileRemove = false;
			for (ResourceChangesFilePatch p : patchs) {
				if (p.getStatus().equals(ChangeTypes.REMOVEFILE)) {
					fileRemove = true;
					break;
				}
				if (patch.getTimestamp() == p.getTimestamp()) {
					break;
				}
				List<String> temp = deltaOperations.unpatchText(fileLines,
						patchObject);
				fileLines.clear();
				for (Object o : temp) {
					fileLines.add((String) o);
				}
			}
			if (!fileRemove) {
				fList2.addAll(fileLines);
				fList1.addAll(deltaOperations.unpatchText(fileLines,
						patchObject));
				IPath relativePath = new Path(path);
				for (Object object : result) {
					IProject project = (IProject) object;
					if (project.getFile(relativePath).exists()
							&& project.getFile(relativePath).exists()) {
						fList3 = getFileContent(project, patch);
						try {
							List<String> mergedList = mergeOperations
									.performThreeWayMerge(fList1, fList2,
											fList3);
							String mergedString = "";
							for (String txt : mergedList) {
								mergedString = mergedString + txt + "\n";
							}
							if (mergedString != null) {
								InputStream source = new ByteArrayInputStream(
										mergedString.getBytes());
								IFile changeFile = project
										.getFile(relativePath);
								MonitorSet.getInstance().addSynchroItem(
										changeFile);
								actualizeAdminFolder(project,
										patch.getPatchFileName());
								changeFile
										.setContents(source, true, true, null);
							}
						} catch (CoreException e) {
							LogOperations.logError(
									"File could not be deleted.", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a file to admin folder .variantsync.
	 * 
	 * @param project
	 *            project of file
	 * @param patchFileName
	 *            file to add
	 */
	private void actualizeAdminFolder(IProject project, String patchFileName) {
		IFolder infoFolder = project
				.getFolder(VariantSyncConstants.ADMIN_FOLDER);
		if (!infoFolder.exists()) {
			try {
				infoFolder.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				LogOperations.logError(ERROR_REFRESH_INFOFILE, e);
			}
		}
		IFile infoFile = project.getFolder(VariantSyncConstants.ADMIN_FOLDER)
				.getFile(VariantSyncConstants.ADMIN_FILE);
		try {
			infoFile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			LogOperations.logError(ERROR_REFRESH_INFOFILE, e);
		}
		MonitorItemStorage info = null;
		if (!infoFile.exists()) {
			info = new MonitorItemStorage();
			try {
				infoFile = persistanceOperations.createIFile(infoFile);
			} catch (FileOperationException e) {
				LogOperations.logError(ERROR_REFRESH_INFOFILE, e);
			}
		} else {
			try {
				info = persistanceOperations.readSynchroXMLFile(infoFile
						.getContents());
			} catch (CoreException e) {
				LogOperations.logError(
						".variantsyncInfo file could not be read.", e);
			}
		}
		info.addMonitorItem(new MonitorItem(patchFileName, project.getName()));
		try {
			persistanceOperations.writeXMLFile(infoFile.getLocation().toFile(),
					info);
		} catch (XMLException e) {
			LogOperations
					.logError(
							".variantsyncInfo file could not be written in admin folder .variantsync.",
							e);
		}
		try {
			infoFile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (CoreException e) {
			LogOperations.logError(ERROR_REFRESH_INFOFILE, e);
		}
	}

	/**
	 * Checks if iPath refers to an existing folder. If the folder does not
	 * exist, it will be created.
	 * 
	 * @param iPath
	 *            path of folder
	 * @param project
	 *            project containing folder
	 */
	private void createDir(IPath iPath, IProject project) {
		if (!iPath.isEmpty()) {
			IFolder folder = project.getFolder(iPath);
			if (!folder.exists()) {
				try {
					persistanceOperations.mkdirs(folder);
				} catch (FolderOperationException e) {
					LogOperations.logError("Folder could not be created.", e);
				}
			}
		}
	}
}
