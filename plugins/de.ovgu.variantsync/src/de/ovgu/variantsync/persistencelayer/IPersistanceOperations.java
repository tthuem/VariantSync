package de.ovgu.variantsync.persistencelayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FolderOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.XMLException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItemStorage;

/**
 * Defines functions to handle folders and different kinds of files on file
 * system.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 17.05.2015
 */
public interface IPersistanceOperations {

	/**
	 * Creates specified folder.
	 * 
	 * @param folder
	 *            IFolder object
	 * @throws FolderOperationException
	 *             folder could not be created
	 */
	void mkdirs(IFolder folder) throws FolderOperationException;

	/**
	 * Deletes specified folder.
	 * 
	 * @param folder
	 *            IFolder object
	 * @throws FolderOperationException
	 *             folder could not be deleted
	 */
	void deldirs(IFolder folder) throws FolderOperationException;

	/**
	 * Creates file or folder in admin folder ".variantsync" which maps original
	 * project structure. Each file contains informations about changes.
	 * 
	 * @param res
	 *            resource to add
	 * @throws FileOperationException
	 *             file could not be created in admin folder
	 */
	void addAdminResource(IResource res) throws FileOperationException;

	/**
	 * Removes file or folder from admin folder ".variantsync".
	 * 
	 * @param res
	 *            resource to remove
	 * @throws FileOperationException
	 *             file could not be created in admin folder
	 */
	void removeAdminFile(IResource res) throws FileOperationException;

	/**
	 * Creates file with specific content.
	 * 
	 * @param lines
	 *            lines to add
	 * @param file
	 *            target file
	 * @throws FileOperationException
	 *             file could not be created
	 */
	void addLinesToFile(List<String> lines, File file)
			throws FileOperationException;

	/**
	 * Reads TXT-file and adds each line to list of string elements.
	 * 
	 * @param inputStream
	 *            input file
	 * @return list of file content
	 * @throws FileOperationException
	 *             file could not be read
	 */
	List<String> readFile(InputStream inputStream)
			throws FileOperationException;

	/**
	 * Creates an IFile-object.
	 * 
	 * @param file
	 *            file to create
	 * @return created IFile
	 * @throws FileOperationException
	 *             file could not be created
	 */
	IFile createIFile(IFile file) throws FileOperationException;

	/**
	 * Reads a xml file and creates a SynchroInfo object.
	 * 
	 * @param inputStream
	 * @return SynchroInfo object
	 */
	MonitorItemStorage readSynchroXMLFile(InputStream inputStream);

	/**
	 * Writes SynchroInfo object in xml file.
	 * 
	 * @param file
	 *            target file
	 * @param info
	 *            SynchroInfo object
	 * @throws FileNotFoundException
	 *             target does not exists
	 */
	void writeXMLFile(File file, MonitorItemStorage info) throws XMLException;

	/**
	 * Reads content from file using buffered reader. Adds each line in file to
	 * List<String>.
	 * 
	 * @param in
	 *            buffered Reader for file
	 * @param charset
	 * @return list with file content
	 * @throws FileOperationException
	 */
	List<String> readFile(InputStream in, String charset)
			throws FileOperationException;

	/**
	 * Reads context information from its storage.
	 * 
	 * @param path
	 *            path to storage
	 * @return context-object
	 */
	Context loadContext(String path);

	/**
	 * Saves a context to specified storage.
	 * 
	 * @param context
	 *            the context to save
	 * @param path
	 *            storage location
	 */
	void saveContext(Context context, String path);

	FeatureExpressions loadFeatureExpressions(String path);

	void saveFeatureExpressions(FeatureExpressions fe);

	void writeFile(java.util.List<CodeLine> syncCode, File file);
}