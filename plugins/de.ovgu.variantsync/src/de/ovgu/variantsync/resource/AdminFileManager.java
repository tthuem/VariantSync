package de.ovgu.variantsync.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.ovgu.variantsync.VariantSyncPlugin;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 
 * @author Lei Luo
 * 
 */
public class AdminFileManager {
	public static String CHANGE = "CHANGE";
	public static String REMOVEFOLDER = "REMOVEFOLDER";
	public static String REMOVEFILE = "REMOVEFILE";
	public static String ADDFOLDER = "ADDFOLDER";
	public static String ADDFILE = "ADDFILE";

	public static void createPatch(IResource res) {
		IFile currentFile = (IFile) res;
		IFileState[] states = null;
		try {
			states = currentFile.getHistory(null);
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
		BufferedReader historyFileReader = null;
		BufferedReader currentFileReader = null;
		try {
			historyFileReader = new BufferedReader(new InputStreamReader(
					states[0].getContents(), states[0].getCharset()));
			currentFileReader = new BufferedReader(new InputStreamReader(
					currentFile.getContents(), currentFile.getCharset()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		List<String> historyFilelines = new LinkedList<String>();
		List<String> currentFilelines = new LinkedList<String>();
		String line = "";
		try {
			while ((line = historyFileReader.readLine()) != null) {
				historyFilelines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				historyFileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		line = "";
		try {
			while ((line = currentFileReader.readLine()) != null) {
				currentFilelines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				currentFileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Patch p = DiffUtils.diff(historyFilelines, currentFilelines);
		if (p.getDeltas().size() == 0) {
			return;
		}
		String filename = currentFile.getName();

		List<String> unifiedDiff = DiffUtils.generateUnifiedDiff(filename, filename,
				historyFilelines, p, 0);

		// Time is wrong
		// String diffFileName = res.getName() + "_" + AdminFileManager.CHANGE +
		// "_"
		// + states[0].getModificationTime();
		int zeiger = 0;
		if (SynchroSet.getInstance().removeSynchroItem(res)) {
			zeiger = 1;
		}
		String diffFileName = res.getName() + "_" + AdminFileManager.CHANGE + "_"
				+ zeiger + "_" + System.currentTimeMillis();

		File parentFile = res.getProjectRelativePath().toFile().getParentFile();
		String subPath = null;
		if (parentFile == null) {
			subPath = "";
		} else {
			subPath = parentFile.getPath();
		}
		
		IPath diffFilePath = res.getProject().getLocation()
				.append(VariantSyncPlugin.AdminFolder).append(subPath);
		File diffFile = new File(diffFilePath.append(diffFileName).toOSString());

		linesToFile(unifiedDiff, diffFile);
	}

	public static void linesToFile(List<String> lines, File file) {
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
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public static void add(IResource res) {
		IPath addInfoFilePath;
		int zeiger = 0;
		if (SynchroSet.getInstance().removeSynchroItem(res)) {
			zeiger = 1;
		}
		if (res instanceof IFolder) {
			String addInfoFileName = res.getName() + "_" + AdminFileManager.ADDFOLDER
					+ "_" + zeiger + "_" + res.getLocalTimeStamp();
			IPath subPath = res.getProjectRelativePath().append(addInfoFileName);
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncPlugin.AdminFolder).append(subPath);
		} else {
			String addInfoFileName = res.getName() + "_" + AdminFileManager.ADDFILE + "_"
					+ zeiger + "_" + res.getLocalTimeStamp();
			File parentFile = res.getProjectRelativePath().toFile().getParentFile();
			String subPath = null;
			if (parentFile == null) {
				subPath = "";
			} else {
				subPath = parentFile.getPath();
			}
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncPlugin.AdminFolder).append(subPath)
					.append(addInfoFileName);
		}
		try {
			if (!addInfoFilePath.toFile().getParentFile().exists()) {
				addInfoFilePath.toFile().getParentFile().mkdirs();
			}
			addInfoFilePath.toFile().createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void remove(IResource res) {
		IPath addInfoFilePath;
		int zeiger = 0;
		if (SynchroSet.getInstance().removeSynchroItem(res)) {
			zeiger = 1;
		}
		if (res instanceof IFolder) {
			String addInfoFileName = res.getName() + "_" + AdminFileManager.REMOVEFOLDER
					+ "_" + zeiger + "_" + System.currentTimeMillis();
			IPath subPath = res.getProjectRelativePath().append(addInfoFileName);
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncPlugin.AdminFolder).append(subPath);
		} else {
			String addInfoFileName = res.getName() + "_" + AdminFileManager.REMOVEFILE
					+ "_" + zeiger + "_" + System.currentTimeMillis();
			File parentFile = res.getProjectRelativePath().toFile().getParentFile();
			String subPath = null;
			if (parentFile == null) {
				subPath = "";
			} else {
				subPath = parentFile.getPath();
			}
			addInfoFilePath = res.getProject().getLocation()
					.append(VariantSyncPlugin.AdminFolder).append(subPath)
					.append(addInfoFileName);
		}
		try {
			if (!addInfoFilePath.toFile().getParentFile().exists()) {
				addInfoFilePath.toFile().getParentFile().mkdirs();
			}
			addInfoFilePath.toFile().createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the specified IFolder
	 * 
	 * @param folder
	 * @throws CoreException
	 */
	public static void mkdirs(IFolder folder) throws CoreException {
		IContainer container = folder.getParent();
		if (!container.exists()) {
			mkdirs((IFolder) container);
		}
		SynchroSet.getInstance().addSynchroItem(folder);
		folder.create(true, true, null);
	}

	public static void deldirs(IFolder folder) throws CoreException {
		recordDelItem(folder);
		folder.delete(true, null);
	}

	private static void recordDelItem(IFolder folder) throws CoreException {
		SynchroSet.getInstance().addSynchroItem(folder);
		IResource[] members = folder.members();
		for (IResource res : members) {
			if (res instanceof IFolder) {
				recordDelItem((IFolder) res);
			} else {
				SynchroSet.getInstance().addSynchroItem(res);
			}
		}
	}
}
