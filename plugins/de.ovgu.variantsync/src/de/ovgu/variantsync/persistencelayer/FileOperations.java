package de.ovgu.variantsync.persistencelayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Provides operations to handle files on file system.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
class FileOperations {

	private static final String ERROR_CODE_FILENOTREAD = "File could not be read.";

	/**
	 * Creates file with specific content.
	 * 
	 * @param lines
	 *            lines to add
	 * @param file
	 *            target file
	 * @throws IOException
	 *             file could not be created
	 */
	public void addLinesToFile(List<String> lines, File file)
			throws FileOperationException {
		if (file.exists()) {
			file.delete();
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		PrintWriter out = null;
		try {
			file.createNewFile();
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (int i = 0; i < lines.size(); i++) {
				out.println(lines.get(i));
			}
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTREAD, e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * Reads TXT-file and adds each line to list of string elements.
	 * 
	 * @param inputStream
	 *            input file
	 * @return list of file content
	 * @throws IOException
	 *             file could not be read
	 */
	public List<String> readFile(InputStream inputStream)
			throws FileOperationException {
		List<String> fileLines = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTREAD, e);
		}
		return fileLines;
	}

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
	public List<String> readFile(InputStream in, String charset)
			throws FileOperationException {
		List<String> fileContent = new LinkedList<String>();
		String line = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTREAD, e);
		} finally {
			try {
				reader.close();
			} catch (NullPointerException | IOException e) {
				LogOperations
						.logError("BufferedReader could not be closed.", e);
			}
		}
		return fileContent;
	}

	/**
	 * Creates an IFile-object.
	 * 
	 * @param file
	 *            file to create
	 * @return created IFile object
	 * @throws FileOperationException
	 *             file could not be created
	 */
	public IFile createIFile(IFile file) throws FileOperationException {
		String content = "";
		InputStream source = new ByteArrayInputStream(content.getBytes());
		try {
			file.create(source, IResource.FORCE, null);
		} catch (CoreException e) {
			new FileOperationException("File could not be created.", e);
		}
		return file;
	}

	public void writeFile(List<String> lines, File file)
			throws FileOperationException {
		if (file.exists()) {
			file.delete();
		}
		PrintWriter out = null;
		try {
			file.createNewFile();
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (int i = 0; i < lines.size(); i++) {
				out.println(lines.get(i));
			}
		} catch (IOException e) {
			throw new FileOperationException(ERROR_CODE_FILENOTREAD, e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
}
