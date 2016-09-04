package de.ovgu.variantsync.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.applicationlayer.datamodel.exception.FolderOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorSet;

/**
 * Handle file and directory operations on file system including changes
 * monitoring.<br>
 * Supported operations:<br>
 * <ul>
 * <li>create directory</li>
 * <li>delete directory</li>
 * </ul>
 * Files and folders will be monitored.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 14.05.2015
 */
class FolderOperations {

	/**
	 * Creates specified folder.
	 * 
	 * @param folder
	 *            IFolder object
	 * @throws FolderOperationException
	 *             folder could not be created
	 */
	public void mkdirs(IFolder folder) throws FolderOperationException {
		IContainer container = folder.getParent();
		if (!container.exists()) {
			mkdirs((IFolder) container);
		}
		MonitorSet.getInstance().addSynchroItem(folder);
		try {
			folder.create(true, true, null);
		} catch (CoreException e) {
			throw new FolderOperationException("Folder could not be created.", e);
		}
	}

	/**
	 * Deletes specified folder.
	 * 
	 * @param folder
	 *            IFolder object
	 * @throws FolderOperationException
	 *             folder could not be deleted
	 */
	public void deldirs(IFolder folder) throws FolderOperationException {
		try {
			recordDelItem(folder);
			folder.delete(true, null);
		} catch (CoreException e) {
			throw new FolderOperationException("Folder could not be deleted.", e);
		}
	}

	public void deldir(IFolder folder, File f) throws FolderOperationException {
		try {
			if (f.exists()) {
				try {
					deleteFileOrFolder(f.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (f.exists()) {
				try {
					FileUtils.deleteDirectory(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			folder.delete(true, null);
		} catch (CoreException e) {
			throw new FolderOperationException("Folder could not be deleted.", e);
		}
	}

	private static void deleteFileOrFolder(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException e) {
				return handleException(e);
			}

			private FileVisitResult handleException(final IOException e) {
				e.printStackTrace(); // replace with more robust error handling
				return FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
				if (e != null)
					return handleException(e);
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	};

	/**
	 * Adds monitoring to folder and its containing sub-folders and files.
	 * 
	 * @param folder
	 *            IFolder object
	 * @throws FolderOperationException
	 */
	private void recordDelItem(IFolder folder) throws FolderOperationException {
		MonitorSet.getInstance().addSynchroItem(folder);
		IResource[] members;
		try {
			members = folder.members();
		} catch (CoreException e) {
			throw new FolderOperationException("Folder members could not be retrieved.", e);
		}
		for (IResource res : members) {
			if (res instanceof IFolder) {
				recordDelItem((IFolder) res);
			} else {
				MonitorSet.getInstance().addSynchroItem(res);
			}
		}
	}

}
