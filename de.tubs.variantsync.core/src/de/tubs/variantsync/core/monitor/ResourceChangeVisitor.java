package de.tubs.variantsync.core.monitor;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import de.tubs.variantsync.core.nature.Variant;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * Visits given resource delta and reacts on added, removed or changed resource
 * deltas. "A resource delta represents changes in the state of a resource tree
 * between two discrete points in time" - @see
 * org.eclipse.core.resources.IResourceDelta. A resource tree represents a
 * project of workspace.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 15.05.2015
 */
class ResourceChangeVisitor implements IResourceDeltaVisitor {

	/**
	 * Called from ResourceChangeListener if a resource change have happened.
	 * Filters changed resource and handle change event.
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		IProject project = res.getProject();
		if (!filterResource(project, res)) {
			return false;
		}
//		if (!checkMembers(delta, project)) {
//			return false;
//		}
		analyseDeltaType(delta);
		return true;
	}

	/**
	 * Triggers action depending on kind of delta. Possible actions: add, remove or
	 * change file in admin folder.
	 * 
	 * @param type
	 *            kind of delta
	 */
	private void analyseDeltaType(IResourceDelta delta) {
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			handleAddedResource(delta);
			break;
		case IResourceDelta.REMOVED:
			handleRemovedResource(delta);
			break;
		case IResourceDelta.CHANGED:
			handleChangedResource(delta);
			break;
		default:
			break;
		}
	}

	/**
	 * Adds patch file to admin folder.
	 * 
	 * @param res
	 *            changed resource
	 * @param delta
	 *            resource delta
	 * @param flag
	 */
	private void handleAddedResource(IResourceDelta delta) {
//		if ((flag & IResourceDelta.MARKERS) == 0 || (flag & IResourceDelta.MOVED_FROM) != 0) {
//			ContextOperations contextOperations = ModuleFactory.getContextOperations();
//			contextOperations.recordFileAdded(res.getProject().getName(), res.getProject().getLocation().toString(),
//					Util.parsePackageNameFromResource(res), ((IFile) res).getName(), Util.getFileLines(res),
//					res.getModificationStamp());
//			if (contextOperations.getActiveFeatureContext() != null
//					&& !contextOperations.getActiveFeatureContext().equals(VariantSyncConstants.DEFAULT_CONTEXT))
//				contextOperations.setBaseVersion((IFile) res);
//			try {
//				persistanceOperations.addAdminResource(res);
//			} catch (FileOperationException e) {
//				LogOperations.logError("Change file could not be created in admin folder.", e);
//			}
			LogOperations.logInfo(String.format("Resource %s was added with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
			//update();
//		}
	}

	/**
	 * Removes patch file from admin folder.
	 * 
	 * @param res
	 *            changed resource
	 * @param delta
	 *            resource delta
	 */
	private void handleRemovedResource(IResourceDelta delta) {
		LogOperations.logInfo(String.format("Resource %s was removed with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
//		ContextOperations contextOperations = ModuleFactory.getContextOperations();
//		contextOperations.recordFileRemoved(res.getProject().getName(), res.getProject().getLocation().toString(),
//				Util.parsePackageNameFromResource(res), ((IFile) res).getName(), Util.getFileLines(res),
//				res.getModificationStamp());
//		if (contextOperations.getActiveFeatureContext() != null
//				&& !contextOperations.getActiveFeatureContext().equals(VariantSyncConstants.DEFAULT_CONTEXT))
//			contextOperations.setBaseVersion((IFile) res);
//		try {
//			persistanceOperations.removeAdminFile(res);
//		} catch (FileOperationException e) {
//			LogOperations.logError("Change file could not be removed from admin folder.", e);
//		}
//		update();
	}

	/**
	 * Creates patch for changed resource.
	 * 
	 * @param res
	 *            changed resource
	 * @param delta
	 *            resource delta
	 */
	private void handleChangedResource(IResourceDelta delta) {
		if (delta.getResource().getType() == IResource.FILE && (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
			IFile file = (IFile) delta.getResource();
			IFileState[] states = null;
			try {
				states = file.getHistory(null);
				if (states.length > 0) {
					long t = states[0].getModificationTime();
					Date d = new Date(t);
					LogOperations
							.logInfo(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(d) + "");
					//deltaOperations.createPatch(res);
					//update();
				}
			} catch (CoreException e) {
				LogOperations.logError("File states could not be retrieved.", e);
			}
			LogOperations.logInfo(String.format("Resource %s was changed with flag %s", delta.getResource().getFullPath(), getFlagText(delta.getFlags())));
			//update();
		}
	}

	/**
	 * Checks if resource does not fulfill any following criteria:<br>
	 * <ul>
	 * <li>project has nature support</li>
	 * <li>project is open</li>
	 * <li>resource still exists</li>
	 * <li>resource starts not with \".\"</li>
	 * </ul>
	 * 
	 * @param project
	 *            project resource belongs to
	 * @param res
	 *            resource to check
	 * @return true if any criteria was fulfilled
	 * @throws CoreException
	 */
	private boolean filterResource(IProject project, IResource res) throws CoreException {
		if (project != null && project.isOpen() && !project.hasNature(Variant.NATURE_ID)) {
			return false;
		}
		if (project != null && !project.isOpen()) {
			return false;
		}
		if (!res.exists()) {
			return false;
		}
		String name = res.getName();
		return !(res.isDerived() || name.startsWith("."));
	}

//	/**
//	 * Checks that delta does not result of adding or changing a file.
//	 * 
//	 * @param delta
//	 *            the resource delta to check
//	 * @param project
//	 *            the project of delta
//	 * @return true if delta does not result of adding or changing a file, otherwise
//	 *         false
//	 * @throws CoreException
//	 *             project nature could not be proofed
//	 */
//	private boolean checkMembers(IResourceDelta delta, IProject project) throws CoreException {
//		IResourceDelta openDelta = delta.findMember(new Path(".project"));
//		if (openDelta != null
//				&& (openDelta.getKind() == IResourceDelta.ADDED || openDelta.getKind() == IResourceDelta.CHANGED)) {
//			if (project != null && project.hasNature(Variant.NATURE_ID)) {
//				//update();
//			}
//			return false;
//		}
//		return true;
//	}

	// /**
	// * Updates registered listeners that synchronize informations have changed.
	// */
	// private void update() {
	// VariantSyncPlugin.getDefault().updateSynchroInfo();
	// MonitorNotifier.getInstance().notifyViews();
	// Display display = VariantSyncPlugin.getStandardDisplay();
	// if (!display.isDisposed()) {
	// display.asyncExec(new Runnable() {
	// public void run() {
	// IWorkbenchWindow window = PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow();
	// if (window == null) {
	// return;
	// }
	// IWorkbenchPage page = window.getActivePage();
	// if (page == null) {
	// return;
	// }
	// if (page.findView(ResourceChangesView.ID) != null) {
	// ((ResourceChangesView) page
	// .findView(ResourceChangesView.ID))
	// .refreshTree();
	// }
	// }
	// });
	// }
	// }

	/**
	 * Adds debug messages.
	 * 
	 * @param flag
	 * @return debug message
	 */
	private String getFlagText(int flag) {
		String flags = "F_";
		if ((flag & IResourceDelta.ADDED) != 0) {
			flags += "ADDED ";
		}
		if ((flag & IResourceDelta.ADDED_PHANTOM) != 0) {
			flags += "ADDED_PHANTOM ";
		}
		if ((flag & IResourceDelta.ALL_WITH_PHANTOMS) != 0) {
			flags += "ALL_WITH_PHANTOMS ";
		}
		if ((flag & IResourceDelta.CHANGED) != 0) {
			flags += "CHANGED ";
		}
		if ((flag & IResourceDelta.CONTENT) != 0) {
			flags += "CONTENT ";
		}
		if ((flag & IResourceDelta.COPIED_FROM) != 0) {
			flags += "COPIED_FROM ";
		}
		if ((flag & IResourceDelta.DERIVED_CHANGED) != 0) {
			flags += "DERIVED_CHANGED ";
		}
		if ((flag & IResourceDelta.DESCRIPTION) != 0) {
			flags += "DESCRIPTION ";
		}
		if ((flag & IResourceDelta.ENCODING) != 0) {
			flags += "ENCODING ";
		}
		if ((flag & IResourceDelta.LOCAL_CHANGED) != 0) {
			flags += "LOCAL_CHANGED ";
		}
		if ((flag & IResourceDelta.MARKERS) != 0) {
			flags += "MARKERS ";
		}
		if ((flag & IResourceDelta.MOVED_FROM) != 0) {
			flags += "MOVED_FROM ";
		}
		if ((flag & IResourceDelta.MOVED_TO) != 0) {
			flags += "MOVED_TO ";
		}
		if ((flag & IResourceDelta.NO_CHANGE) != 0) {
			flags += "NO_CHANGE ";
		}
		if ((flag & IResourceDelta.OPEN) != 0) {
			flags += "OPEN ";
		}
		if ((flag & IResourceDelta.REMOVED) != 0) {
			flags += "REMOVED ";
		}
		if ((flag & IResourceDelta.REMOVED_PHANTOM) != 0) {
			flags += "REMOVED_PHANTOM ";
		}
		if ((flag & IResourceDelta.REPLACED) != 0) {
			flags += "REPLACED ";
		}
		if ((flag & IResourceDelta.SYNC) != 0) {
			flags += "SYNC ";
		}
		if ((flag & IResourceDelta.TYPE) != 0) {
			flags += "TYPE ";
		}
		return flags;
	}
}