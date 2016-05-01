package de.ovgu.variantsync.applicationlayer.deltacalculation;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorSet;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ChangeTypes;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;
import difflib.Patch;

/**
 * Computes difference (delta) of a resource. If resource is changed, changes
 * will be computed. The following synonyms are used to rephrase differences
 * between two file versions are:
 * <ul>
 * <li>delta</li>
 * <li>patch</li>
 * </ul>
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
class DeltaCalculation {

	private String unifiedDiff;
	private ExternalDeltaCalculation externalDeltaOperations;
	private IContextOperations contextOperations = ModuleFactory
			.getContextOperations();
	private IPersistanceOperations persistanceOperations = ModuleFactory
			.getPersistanceOperations();

	public DeltaCalculation() {
		externalDeltaOperations = new ExternalDeltaCalculation();
	}

	/**
	 * Computes string describing changes between two files or one file in
	 * different versions.
	 * 
	 * @param filePatch
	 *            patch of two files or one file in different versions
	 * @return changes of two files described in filePatch object
	 */
	public String getChanges(ResourceChangesFilePatch filePatch) {
		StringBuilder changes = new StringBuilder();
		changes.append("Project:  ");
		changes.append(filePatch.getProject().getName());
		changes.append("\n");
		changes.append("Path:  ");
		changes.append(filePatch.getPath());
		changes.append("\n");
		String status = filePatch.getStatus();
		changes.append("Operation:  ");
		changes.append(status);
		changes.append("\n");
		if (status.equals(ChangeTypes.CHANGE)) {
			changes.append("=====================Unified Diff=====================");
			changes.append("\n");
			String diff = getUnifieddiff(filePatch);
			changes.append(diff);
		}
		return changes.toString();
	}

	/**
	 * Computes difference (delta) between actual and ancient version of changed
	 * file and creates new file with change information in admin folder.
	 * 
	 * @param res
	 *            changed resource
	 */
	public void createPatch(IResource res) {
		IFile currentFile = (IFile) res;
		IFileState[] states = null;
		try {
			states = currentFile.getHistory(null);
		} catch (CoreException e) {
			LogOperations.logError("File states could not be retrieved.", e);
			return;
		}
		List<String> historyFilelines = null;
		List<String> currentFilelines = null;
		try {
			historyFilelines = persistanceOperations.readFile(
					states[0].getContents(), states[0].getCharset());
			currentFilelines = persistanceOperations.readFile(
					currentFile.getContents(), currentFile.getCharset());
		} catch (CoreException | FileOperationException e) {
			LogOperations.logError("File states could not be read.", e);
		}

		Patch patch = externalDeltaOperations.computeDifference(
				historyFilelines, currentFilelines);
		if (patch.getDeltas().isEmpty()) {
			return;
		}
		String filename = currentFile.getName();
		List<String> tmpUnifiedDiff = externalDeltaOperations
				.createUnifiedDifference(filename, filename, historyFilelines,
						patch, 0);

		String packageName = res.getLocation().toString();
		packageName = packageName.substring(packageName.indexOf("src") + 4,
				packageName.lastIndexOf("/"));
		packageName = packageName.replace("/", ".");
		contextOperations.recordCodeChange(tmpUnifiedDiff, res.getProject()
				.getName(), res.getProject().getLocation().toString(),
				packageName, ((IFile) res).getName(), currentFilelines);
		if (contextOperations.getActiveFeatureContext() != null
				&& !contextOperations.getActiveFeatureContext().equals(
						VariantSyncConstants.DEFAULT_CONTEXT))
			contextOperations.setBaseVersion((IFile) res);

		int pointer = 0;
		if (MonitorSet.getInstance().removeSynchroItem(res)) {
			pointer = 1;
		}
		String diffFileName = res.getName() + "_" + ChangeTypes.CHANGE + "_"
				+ pointer + "_" + System.currentTimeMillis();

		File parentFile = res.getProjectRelativePath().toFile().getParentFile();
		String subPath = null;
		if (parentFile == null) {
			subPath = "";
		} else {
			subPath = parentFile.getPath();
		}

		IPath diffFilePath = res.getProject().getLocation()
				.append(VariantSyncConstants.ADMIN_FOLDER).append(subPath);
		File diffFile = new File(diffFilePath.append(diffFileName).toOSString());

		try {
			persistanceOperations.addLinesToFile(tmpUnifiedDiff, diffFile);
		} catch (FileOperationException e) {
			LogOperations
					.logError(
							"Lines could not be added to diff file in admin folder.",
							e);
		}
	}

	/**
	 * Creates patch object from changed file.
	 * 
	 * @param changedFile
	 *            changed file
	 * @return patch object
	 */
	public synchronized Patch getPatch(ResourceChangesFilePatch changedFile) {
		Patch patch = null;
		String parentFolder = changedFile.getFile().getParentFile().getPath();
		IPath base = new Path(changedFile.getProject().getLocation()
				.toOSString());
		IPath temp = new Path(parentFolder).append(changedFile
				.getPatchFileName());
		IPath relativePath = temp.makeRelativeTo(base);
		if (changedFile.getStatus().equals(ChangeTypes.CHANGE)) {
			if (!changedFile.getProject().getFile(relativePath).exists()) {
				try {
					changedFile.getProject().getFile(relativePath)
							.refreshLocal(IResource.DEPTH_ZERO, null);
				} catch (CoreException e) {
					LogOperations.logError(
							"File could not be refreshed in workspace.", e);
				}
			}
			if (changedFile.getProject().getFile(relativePath).exists()) {
				try {
					List<String> lines = persistanceOperations
							.readFile(changedFile.getProject()
									.getFile(relativePath).getContents());
					unifiedDiff = parseListToString(lines);
					patch = externalDeltaOperations
							.createUnifiedDifference(lines);
				} catch (CoreException | FileOperationException e) {
					LogOperations.logError(
							"Diff file in admin folder could not be read.", e);
					unifiedDiff = "";
				}
			} else {
				try {
					changedFile.getProject().getFile(relativePath)
							.refreshLocal(IResource.DEPTH_ZERO, null);
				} catch (CoreException e) {
					LogOperations.logError(
							"File could not be refreshed in workspace.", e);
				}
			}
		}
		return patch;
	}

	/**
	 * Computes unified difference of a changed file.
	 * 
	 * @param changedFile
	 *            changed file
	 * @return changes as string
	 */
	public String getUnifieddiff(ResourceChangesFilePatch changedFile) {
		unifiedDiff = "";
		getPatch(changedFile);
		return unifiedDiff;
	}

	/**
	 * Creates a string containing each line of given list of strings as new
	 * line.
	 * 
	 * @param content
	 *            the list to parse
	 * @return String containing list content
	 */
	public String parseListToString(List<String> content) {
		StringBuilder uniDiff = new StringBuilder();
		for (String s : content) {
			uniDiff.append(s);
			uniDiff.append("\n");
		}
		return uniDiff.toString();
	}
}
