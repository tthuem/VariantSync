package de.tubs.variantsync.core.view.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.CodeMapping;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.utilities.IVariantSyncMarker;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.MarkerUtils;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

/**
 * PartAdapter for the editor. Creates and updates the markers of the current file of the editor
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 17.09.2015
 */
public class PartAdapter implements IPartListener, IEventListener {

	private IFile currentFile = null;

	public PartAdapter() {
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				currentFile = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
				updateEditorMarkers();
			}
		}

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				currentFile = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
				updateEditorMarkers();
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPart arg0) {
		// not required
	}

	@Override
	public void partDeactivated(IWorkbenchPart arg0) {
		// not required
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			if (((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
				currentFile = ((IFileEditorInput) ((EditorPart) part).getEditorInput()).getFile();
				updateEditorMarkers();
			}
		}
	}

	private void updateEditorMarkers() {
		if (currentFile != null) {
			final MarkerUpdateJob job = new MarkerUpdateJob();
			job.schedule();
		}
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_CHANGED:
		case CONFIGURATIONPROJECT_SET:
		case FEATURECONTEXT_ADDED:
		case FEATURECONTEXT_CHANGED:
		case FEATURECONTEXT_REMOVED:
		case PATCH_ADDED:
		case PATCH_CHANGED:
			updateEditorMarkers();
			break;
		default:
			break;
		}
	}

	private class MarkerUpdateJob extends UIJob {

		public MarkerUpdateJob() {
			super("Update markers");
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			try {
				MarkerUtils.cleanResource(currentFile);
			} catch (final CoreException e) {
				LogOperations.logError("Cannot clear all markers from: " + currentFile.getFullPath(), e);
			}

			final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
			if (configurationProject != null) {
				final List<IVariantSyncMarker> markers = new ArrayList<>();
				final SourceFile sourceFile = configurationProject.getMappingManager().getMapping(currentFile);
				if (sourceFile != null) {
					for (final CodeMapping codeMapping : sourceFile.getMappings()) {
						markers.add(codeMapping.getMarkerInformation());
					}
				}
				if (!markers.isEmpty()) {
					MarkerUtils.setMarker(currentFile, markers);
				}
			}
			return Status.OK_STATUS;
		}

	}

}
