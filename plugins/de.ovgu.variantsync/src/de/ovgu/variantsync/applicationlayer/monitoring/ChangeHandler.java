package de.ovgu.variantsync.applicationlayer.monitoring;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.deltacalculation.IDeltaOperations;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.presentationlayer.view.eclipseadjustment.VSyncSupportProjectNature;
import de.ovgu.variantsync.presentationlayer.view.resourcechanges.ResourceChangesView;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Visits given resource delta and reacts on added, removed or changed resource
 * deltas. "A resource delta represents changes in the state of a resource tree
 * between two discrete points in time" - @see
 * org.eclipse.core.resources.IResourceDelta. A resource tree represents a
 * project of workspace.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
class ChangeHandler implements IResourceDeltaVisitor {

	private IResource res;
	private int flag;
	private IResourceDelta delta;
	private IDeltaOperations deltaOperations = ModuleFactory
			.getDeltaOperations();
	private IPersistanceOperations persistanceOperations = ModuleFactory
			.getPersistanceOperations();
	private static final String RESOURCE = "Resource ";

	/**
	 * Called from ResourceChangeListener if a resource change have happened.
	 * Filters changed resource and handle change event.
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		this.delta = delta;
		res = delta.getResource();
		flag = delta.getFlags();
		IProject project = res.getProject();
		if (!filterResource(project, res)) {
			return false;
		}
		if (!checkMembers(delta, project)) {
			return false;
		}
		analyseDeltaType(delta.getKind());
		return true;
	}

	/**
	 * Triggers action depending on kind of delta. Possible actions: add, remove
	 * or change file in admin folder.
	 * 
	 * @param type
	 *            kind of delta
	 */
	private void analyseDeltaType(int type) {
		switch (type) {
		case IResourceDelta.ADDED:
			handleAddedResource(res, delta, flag);
			break;
		case IResourceDelta.REMOVED:
			handleRemovedResource(res, delta);
			break;
		case IResourceDelta.CHANGED:
			handleChangedResource(res, delta);
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
	private void handleAddedResource(IResource res, IResourceDelta delta,
			int flag) {
		if ((flag & IResourceDelta.MARKERS) == 0
				|| (flag & IResourceDelta.MOVED_FROM) != 0) {
			try {
				persistanceOperations.addAdminResource(res);
			} catch (FileOperationException e) {
				LogOperations.logError(
						"Change file could not be created in admin folder.", e);
			}
			VariantSyncPlugin.getDefault().logMessage(
					RESOURCE + res.getFullPath() + " was added "
							+ getFlagTxt(flag));
			update();
		}
	}

	/**
	 * Removes patch file from admin folder.
	 * 
	 * @param res
	 *            changed resource
	 * @param delta
	 *            resource delta
	 */
	private void handleRemovedResource(IResource res, IResourceDelta delta) {
		VariantSyncPlugin.getDefault().logMessage(
				RESOURCE + res.getFullPath() + " was removed "
						+ getFlagTxt(delta.getFlags()));
		try {
			persistanceOperations.removeAdminFile(res);
		} catch (FileOperationException e) {
			LogOperations.logError(
					"Change file could not be removed from admin folder.", e);
		}
		update();
	}

	/**
	 * Creates patch for changed resource.
	 * 
	 * @param res
	 *            changed resource
	 * @param delta
	 *            resource delta
	 */
	private void handleChangedResource(IResource res, IResourceDelta delta) {
		if (res.getType() == IResource.FILE
				&& (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
			IFile file = (IFile) res;
			IFileState[] states = null;
			try {
				states = file.getHistory(null);
				if (states.length > 0) {
					long t = states[0].getModificationTime();
					Date d = new Date(t);
					LogOperations.logInfo(DateFormat.getDateTimeInstance(
							DateFormat.SHORT, DateFormat.SHORT).format(d)
							+ "");
					deltaOperations.createPatch(res);
					update();
				}
			} catch (CoreException e) {
				LogOperations
						.logError("File states could not be retrieved.", e);
			}
			VariantSyncPlugin.getDefault().logMessage(
					RESOURCE + res.getFullPath() + " has changed "
							+ getFlagTxt(delta.getFlags()));
			update();
		}
	}

	/**
	 * Checks if resource does not fulfill any following criteria:<br>
	 * <ul>
	 * <li>resource´s project has nature support</li>
	 * <li>resource is not admin folder</li>
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
	private boolean filterResource(IProject project, IResource res)
			throws CoreException {
		if (project != null && project.isOpen()
				&& !project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
			return false;
		}
		if (project != null && !project.isOpen()) {
			update();
			return false;
		}
		String name = res.getName();
		return !(name.equals(VariantSyncConstants.ADMIN_FOLDER)
				|| res.isDerived() || name.startsWith("."));
	}

	/**
	 * Checks that delta does not result of adding or changing a file.
	 * 
	 * @param delta
	 *            the resource delta to check
	 * @param project
	 *            the project of delta
	 * @return true if delta does not result of adding or changing a file,
	 *         otherwise false
	 * @throws CoreException
	 *             project nature could not be proofed
	 */
	private boolean checkMembers(IResourceDelta delta, IProject project)
			throws CoreException {
		IResourceDelta openDelta = delta.findMember(new Path(".project"));
		if (openDelta != null
				&& (openDelta.getKind() == IResourceDelta.ADDED || openDelta
						.getKind() == IResourceDelta.CHANGED)) {
			if (project != null
					&& project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
				update();
			}
			return false;
		}
		return true;
	}

	/**
	 * Updates registered listeners that synchronize informations have changed.
	 */
	private void update() {
		VariantSyncPlugin.getDefault().updateSynchroInfo();
		MonitorNotifier.getInstance().notifyViews();
		Display display = VariantSyncPlugin.getStandardDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();
					if (window == null) {
						return;
					}
					IWorkbenchPage page = window.getActivePage();
					if (page == null) {
						return;
					}
					if (page.findView(ResourceChangesView.ID) != null) {
						((ResourceChangesView) page
								.findView(ResourceChangesView.ID))
								.refreshTree();
					}
				}
			});
		}
	}

	/**
	 * Adds debug messages.
	 * 
	 * @param flag
	 * @return debug message
	 */
	private String getFlagTxt(int flag) {
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
