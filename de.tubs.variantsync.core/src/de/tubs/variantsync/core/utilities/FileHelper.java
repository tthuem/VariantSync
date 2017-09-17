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

public class FileHelper {
	
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

	public static List<String> getFileLines(IFile res) {
		List<String> currentFilelines = new ArrayList<>();
		try {
			currentFilelines = readFile(res.getContents(), res.getCharset());
		} catch (IOException | NullPointerException | CoreException e) {
			LogOperations.logError("File could not be read.", e);
		}
		return currentFilelines;
	}
	
	public static List<String> getFileLines(IFileState state) {
		List<String> currentFilelines = new ArrayList<>();
		try {
			currentFilelines = readFile(state.getContents(), state.getCharset());
		} catch (IOException | NullPointerException | CoreException e) {
			LogOperations.logError("File states could not be read.", e);
		}
		return currentFilelines;
	}
	
	public static void setFileLines(IFile res, List<String> lines) {
		try {
			writeFile(res, lines);
		} catch (IOException | CoreException e) {
			LogOperations.logError("File could not be written.", e);
		}
	}

	/**
	 * Reads content from file using buffered reader. Adds each line in file to List<String>.
	 *
	 * @param in buffered Reader for file
	 * @param charset
	 * @return list with file content
	 * @throws IOException
	 */
	private static List<String> readFile(InputStream in, String charset) throws IOException {
		List<String> fileContent = new LinkedList<String>();
		String line = "";
		BufferedReader reader = null;
		if (charset == null) charset = (String) "UTF-8";
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				reader.close();
			} catch (NullPointerException | IOException e) {
				LogOperations.logError("BufferedReader could not be closed.", e);
			}
		}
		return fileContent;
	}
	
//	private static void writeFile(IFile res, InputStream in) throws CoreException {
//		WorkspaceJob job = new WorkspaceJob("Write to file") {
//			
//			@Override
//			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
//				res.setContents(in, true, true, monitor);
//				return Status.OK_STATUS;
//			}
//		};
//		job.schedule();
//		try {
//			job.join(1_000, new VariantSyncProgressMonitor("Write to file"));
//		} catch (InterruptedException e) {
//			LogOperations.logError("Job was interrupted", e);
//		}
//	}
	
	public static void writeFile(IFile res, List<String> lines) throws IOException, CoreException {
		File file = new File(res.getRawLocationURI());
		if (file.exists()) {
			file.delete();
		}
		PrintWriter out = null;
		try {
			file.createNewFile();
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (String line : lines) {
				out.println(line);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				throw e;
			}
		}
	}
	
}
