package de.tubs.variantsync.core.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * Class to retrieve file content when the workspace is busy. This class also
 * enables to retrieve history states.
 * 
 * @author Christopher Sontag
 */
public class FileHelper {

	/**
	 * Returns the latest history of an IFile resource
	 * 
	 * @param res
	 * @return
	 */
	public static IFileState getLatestHistory(IFile res) {
		IFileState[] states = null;
		try {
			states = res.getHistory(null);
		} catch (CoreException e) {
			LogOperations.logError("File states could not be retrieved.", e);
		}
		if (states.length > 0) {
			return states[0];
		}
		return null;
	}

	/**
	 * Returns all lines of an IFile resource as an list of strings
	 * 
	 * @param res
	 * @return
	 */
	public static List<String> getFileLines(IFile res) {
		List<String> currentFilelines = new ArrayList<>();
		try {
			currentFilelines = readFile(res.getContents(), res.getCharset());
		} catch (CoreException e) {
			LogOperations.logError("File could not be accessed.", e);
		}
		return currentFilelines;
	}

	/**
	 * Returns all lines of an IFile history state resource as an list of strings
	 * 
	 * @param state
	 * @return
	 */
	public static List<String> getFileLines(IFileState state) {
		List<String> currentFilelines = new ArrayList<>();
		try {
			currentFilelines = readFile(state.getContents(), state.getCharset());
		} catch (CoreException e) {
			LogOperations.logError("History can not be accessed.", e);
		}
		return currentFilelines;
	}

	/**
	 * Overwrites the IFile resource with the given lines
	 * 
	 * @param res
	 * @param lines
	 */
	public static void setFileLines(IFile res, List<String> lines) {
		writeFile(res, lines);
	}

	/**
	 * Reads content from file using buffered reader. Adds each line in file to
	 * List<String>.
	 *
	 * @param in      buffered Reader for file
	 * @param charset
	 * @return list with file content
	 */
	private static List<String> readFile(InputStream in, String charset) {
		List<String> fileContent = new LinkedList<String>();
		String line = "";
		BufferedReader reader = null;
		if (charset == null)
			charset = (String) "UTF-8";
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (IOException e) {
			LogOperations.logError("BufferedReader could not be opened.", e);
		} finally {
			try {
				reader.close();
			} catch (NullPointerException | IOException e) {
				LogOperations.logError("BufferedReader could not be closed.", e);
			}
		}
		return fileContent;
	}

	/**
	 * Writes content to a IFile resource using a buffered writer. Each line is one
	 * item in lines
	 * 
	 * @param res
	 * @param lines
	 */
	public static void writeFile(IFile res, List<String> lines) {
		File file = new File(res.getRawLocationURI());
		if (file.exists()) {
			file.delete();
		}
		File parentDir = file.getParentFile();
		if (!parentDir.exists())
			parentDir.mkdirs();
		PrintWriter out = null;
		try {
			file.createNewFile();
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (String line : lines) {
				out.println(line);
			}
		} catch (IOException e) {
			LogOperations.logError("File can not be created.", e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				LogOperations.logInfo("Refresh could not be made because the workspace is locked up.");
			}
		}
	}

}
