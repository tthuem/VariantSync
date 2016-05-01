package de.ovgu.variantsync.persistencelayer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FolderOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.XMLException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItemStorage;

/**
 * Provides functions to handle folders and different kinds of files on file
 * system.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 17.05.2015
 */
public class PersistanceOperationProvider implements IPersistanceOperations {

	private FileOperations fileOperations;
	private FolderOperations folderOperations;
	private XMLOperations xmlOperations;
	private AdminFolderManager adminFolderManager;

	public PersistanceOperationProvider() {
		fileOperations = new FileOperations();
		folderOperations = new FolderOperations();
		xmlOperations = new XMLOperations();
		adminFolderManager = new AdminFolderManager();
	}

	@Override
	public void mkdirs(IFolder folder) throws FolderOperationException {
		folderOperations.mkdirs(folder);
	}

	@Override
	public void deldirs(IFolder folder) throws FolderOperationException {
		folderOperations.deldirs(folder);
	}

	@Override
	public void addLinesToFile(List<String> lines, File file)
			throws FileOperationException {
		fileOperations.addLinesToFile(lines, file);
	}

	@Override
	public List<String> readFile(InputStream inputStream)
			throws FileOperationException {
		return fileOperations.readFile(inputStream);
	}

	@Override
	public IFile createIFile(IFile file) throws FileOperationException {
		return fileOperations.createIFile(file);
	}

	@Override
	public MonitorItemStorage readSynchroXMLFile(InputStream inputStream) {
		return xmlOperations.readSynchroXMLFile(inputStream);
	}

	@Override
	public void writeXMLFile(File file, MonitorItemStorage info)
			throws XMLException {
		xmlOperations.writeXMLFile(file, info);
	}

	@Override
	public void addAdminResource(IResource res) throws FileOperationException {
		adminFolderManager.add(res);
	}

	@Override
	public void removeAdminFile(IResource res) throws FileOperationException {
		adminFolderManager.remove(res);
	}

	@Override
	public List<String> readFile(InputStream in, String charset)
			throws FileOperationException {
		return fileOperations.readFile(in, charset);
	}

	@Override
	public Context loadContext(String path) {
		return JaxbOperations.loadContext(path);
	}

	@Override
	public void saveContext(Context context, String path) {
		JaxbOperations.writeContext(context, path);
	}

	@Override
	public FeatureExpressions loadFeatureExpressions(String path) {
		return JaxbOperations.loadFeatureExpression(path);
	}

	@Override
	public void saveFeatureExpressions(FeatureExpressions fe) {
		String path = VariantSyncPlugin.getDefault().getWorkspaceLocation()
				+ VariantSyncConstants.FEATURE_EXPRESSION_PATH;

		// creates target folder if it does not already exist
		File folder = new File(path.substring(0, path.lastIndexOf("/")));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (fe != null && fe.getFeatureExpressions() != null) {
			JaxbOperations.writeFeatureExpression(fe, path);
		}
	}

	@Override
	public void writeFile(List<CodeLine> syncCode, File file) {
		List<String> lines = new ArrayList<String>();
		for (CodeLine line : syncCode) {
			lines.add(line.getCode());
		}
		try {
			fileOperations.writeFile(lines, file);
		} catch (FileOperationException e) {
			e.printStackTrace();
		}
	}

}