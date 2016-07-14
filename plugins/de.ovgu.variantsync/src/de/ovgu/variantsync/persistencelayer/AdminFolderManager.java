package de.ovgu.variantsync.persistencelayer;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorSet;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ChangeTypes;

/**
 * Manages ".variantsync" folder which is called admin folder. Implements
 * functions to add and remove files to or from admin folder. Admin folder
 * represents project structure and contains file with change infos instead of
 * project files (e.g. source files etc.). Contains parts of functionality which
 * was originally implemented by Lei Luo.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
class AdminFolderManager {

	private static final String ERROR_CODE_FILENOTCREATED = "File could not created in admin folder.";
	private static final String NAME_SEPARATOR = "_";

	/**
	 * Creates file in folder ".variantsync" which maps original project
	 * structure. Each file contains informations about changes.
	 *
	 * @param res
	 *            resource to add
	 * @throws FileOperationException
	 *             file could not be created in admin folder
	 */
	public void add(IResource res) throws FileOperationException {
		IPath addInfoFilePath;
		int pointer = 0;
		if (MonitorSet.getInstance().removeSynchroItem(res)) {
			pointer = 1;
		}
		if (res instanceof IFolder) {
			addInfoFilePath = addFolder(res, pointer);
		} else {
			addInfoFilePath = addFile(res, pointer);
		}
		try {
			createNewFile(addInfoFilePath);
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTCREATED, e);
		}
	}

	private void createNewFile(IPath addInfoFilePath) throws IOException {
		if (!addInfoFilePath.toFile().getParentFile().exists()) {
			addInfoFilePath.toFile().getParentFile().mkdirs();
		}
		addInfoFilePath.toFile().createNewFile();
	}

	private IPath addFile(IResource res, int pointer) {
		IPath addInfoFilePath;
		String addInfoFileName = res.getName() + NAME_SEPARATOR + ChangeTypes.ADDFILE + NAME_SEPARATOR + pointer
				+ NAME_SEPARATOR + res.getLocalTimeStamp();
		File parentFile = res.getProjectRelativePath().toFile().getParentFile();
		String subPath = null;
		if (parentFile == null) {
			subPath = "";
		} else {
			subPath = parentFile.getPath();
		}
		addInfoFilePath = res.getProject().getLocation().append(VariantSyncConstants.ADMIN_FOLDER).append(subPath)
				.append(addInfoFileName);
		return addInfoFilePath;
	}

	private IPath addFolder(IResource res, int pointer) {
		String addInfoFileName = res.getName() + NAME_SEPARATOR + ChangeTypes.ADDFOLDER + NAME_SEPARATOR + pointer
				+ NAME_SEPARATOR + res.getLocalTimeStamp();
		IPath subPath = res.getProjectRelativePath().append(addInfoFileName);
		IPath addInfoFilePath = res.getProject().getLocation().append(VariantSyncConstants.ADMIN_FOLDER)
				.append(subPath);
		return addInfoFilePath;
	}

	/**
	 * Removes file from folder ".variantsync".
	 *
	 * @param res
	 *            resource to remove
	 * @throws FileOperationException
	 *             file could not be created in admin folder
	 */
	public void remove(IResource res) throws FileOperationException {
		IPath addInfoFilePath;
		int zeiger = 0;
		if (MonitorSet.getInstance().removeSynchroItem(res)) {
			zeiger = 1;
		}
		if (res instanceof IFolder) {
			addInfoFilePath = removeFolder(res, zeiger);
		} else {
			addInfoFilePath = removeFile(res, zeiger);
		}
		try {
			createNewFile(addInfoFilePath);
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTCREATED, e);
		}
	}

	private IPath removeFile(IResource res, int zeiger) {
		IPath addInfoFilePath;
		String addInfoFileName = res.getName() + NAME_SEPARATOR + ChangeTypes.REMOVEFILE + NAME_SEPARATOR + zeiger
				+ NAME_SEPARATOR + System.currentTimeMillis();
		File parentFile = res.getProjectRelativePath().toFile().getParentFile();
		String subPath = null;
		if (parentFile == null) {
			subPath = "";
		} else {
			subPath = parentFile.getPath();
		}
		addInfoFilePath = res.getProject().getLocation().append(VariantSyncConstants.ADMIN_FOLDER).append(subPath)
				.append(addInfoFileName);
		return addInfoFilePath;
	}

	private IPath removeFolder(IResource res, int zeiger) {
		IPath addInfoFilePath;
		String addInfoFileName = res.getName() + NAME_SEPARATOR + ChangeTypes.REMOVEFOLDER + NAME_SEPARATOR + zeiger
				+ NAME_SEPARATOR + System.currentTimeMillis();
		IPath subPath = res.getProjectRelativePath().append(addInfoFileName);
		addInfoFilePath = res.getProject().getLocation().append(VariantSyncConstants.ADMIN_FOLDER).append(subPath);
		return addInfoFilePath;
	}
}
