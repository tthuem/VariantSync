package de.tubs.variantsync.core.utilities;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class Utils {

	public static List<String> getFileLines(IResource res) {
		List<String> currentFilelines = null;
		IFile currentFile = (IFile) res;

		try {
			currentFilelines = FileHandler.readFile(currentFile.getContents(), currentFile.getCharset());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return currentFilelines;
	}

}
