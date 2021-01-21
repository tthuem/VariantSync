package de.tubs.variantsync.core.patch;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import de.tubs.variantsync.core.utilities.FileHelper;

public class HistoryStore {

	public static final String historyFolder = ".history";

	public void addHistory(IFile file, List<String> content, long timestamp) {
		IProject project = file.getProject();
		IFile historyFile = project.getFolder(historyFolder)
				.getFolder(file.getProjectRelativePath().toOSString().replace(".", "_").replace("/", "_"))
				.getFile(timestamp + ".java");
		FileHelper.setFileLines(historyFile, content);
	}

	public Set<IFile> allFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clean(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	public boolean exists(IFile file, long timestamp) {
		// TODO Auto-generated method stub
		return false;
	}

	public IFile getState(IFile file, long timestamp) {
		IProject project = file.getProject();
		return project.getFolder(historyFolder)
				.getFolder(file.getProjectRelativePath().toOSString().replace(".", "_").replace("/", "_"))
				.getFile(timestamp + ".txt");
	}

	public Set<IFile> getStates(IFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(IFile file) {

	}

	public void remove(IFile file, long timestamp) {

	}
}
