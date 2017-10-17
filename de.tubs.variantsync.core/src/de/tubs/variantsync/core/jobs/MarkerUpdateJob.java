package de.tubs.variantsync.core.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.CodeMapping;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.MarkerUtils;

public class MarkerUpdateJob extends UIJob {

	private IFile file;

	public MarkerUpdateJob(IFile file) {
		super("Update markers");
		this.file = file;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		try {
			MarkerUtils.cleanResource(file);
		} catch (CoreException e) {
			LogOperations.logError("Cannot clear all markers from: " + file.getFullPath(), e);
		}

		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		if (context != null) {
			List<IMarkerInformation> markers = new ArrayList<>();
			SourceFile sourceFile = context.getMapping(file);
			if (sourceFile != null) {
				for (CodeMapping codeMapping : sourceFile.getMappings()) {
					markers.add(codeMapping.getMarkerInformation());
				}
			}
			if (!markers.isEmpty()) MarkerUtils.setMarker(file, markers);
		}
		return Status.OK_STATUS;
	}

}
