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
			String addInfoFileName = res.getName() + "_"
					+ ChangeTypes.ADDFOLDER + "_" + pointer + "_"
					+ res.getLocalTimeStamp();
			IPath subPath = res.getProjectRelativePath()
					.append(addInfoFileName);
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncConstants.ADMIN_FOLDER).append(subPath);
		} else {
			String addInfoFileName = res.getName() + "_" + ChangeTypes.ADDFILE
					+ "_" + pointer + "_" + res.getLocalTimeStamp();
			File parentFile = res.getProjectRelativePath().toFile()
					.getParentFile();
			String subPath = null;
			if (parentFile == null) {
				subPath = "";
			} else {
				subPath = parentFile.getPath();
			}
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncConstants.ADMIN_FOLDER).append(subPath)
					.append(addInfoFileName);
		}
		try {
			if (!addInfoFilePath.toFile().getParentFile().exists()) {
				addInfoFilePath.toFile().getParentFile().mkdirs();
			}
			addInfoFilePath.toFile().createNewFile();
		} catch (IOException e) {
			throw new FileOperationException(
					"File could not be created in admin folder.", e);
		}
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
			String addInfoFileName = res.getName() + "_"
					+ ChangeTypes.REMOVEFOLDER + "_" + zeiger + "_"
					+ System.currentTimeMillis();
			IPath subPath = res.getProjectRelativePath()
					.append(addInfoFileName);
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncConstants.ADMIN_FOLDER).append(subPath);
		} else {
			String addInfoFileName = res.getName() + "_"
					+ ChangeTypes.REMOVEFILE + "_" + zeiger + "_"
					+ System.currentTimeMillis();
			File parentFile = res.getProjectRelativePath().toFile()
					.getParentFile();
			String subPath = null;
			if (parentFile == null) {
				subPath = "";
			} else {
				subPath = parentFile.getPath();
			}
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncConstants.ADMIN_FOLDER).append(subPath)
					.append(addInfoFileName);
		}
		try {
			if (!addInfoFilePath.toFile().getParentFile().exists()) {
				addInfoFilePath.toFile().getParentFile().mkdirs();
			}
			addInfoFilePath.toFile().createNewFile();
		} catch (IOException e) {
			throw new FileOperationException(
					"File could not be created in admin folder.", e);
		}
	}
}
