package de.ovgu.variantsync.resource;

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

import de.ovgu.variantsync.VSyncSupportProjectNature;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.views.ResourceChangesView;

/**
 * 
 * @author Lei Luo
 * 
 */
public class DeltaProcessor implements IResourceDeltaVisitor {

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		int flag = delta.getFlags();
		IProject project = res.getProject();
		if (project != null) {
			if (project.isOpen()) {
				if (!project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
					return false;
				}
			} else {
				update();
				return false;
			}
		}
		if (res.getName().equals(VariantSyncPlugin.AdminFolder)) {
			return false;
		}
		IResourceDelta openDelta = delta.findMember(new Path(".project"));
		if (openDelta != null
				&& (openDelta.getKind() == IResourceDelta.ADDED || openDelta.getKind() == IResourceDelta.CHANGED)) {
			if (project != null && project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
				update();
			}
			return false;
		}
		if (res.isDerived()) {
			return false;
		}
		String name = res.getName();
		if (name.startsWith(".")) {
			return false;
		}
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			VariantSyncPlugin.getDefault().logMessage(
					"Resource " + res.getFullPath() + " was added " + getFlagTxt(flag));
			if ((flag & IResourceDelta.MARKERS) == 0
					|| (flag & IResourceDelta.MOVED_FROM) != 0) {
				AdminFileManager.add(res);
				update();
			}
			break;
		case IResourceDelta.REMOVED:
			VariantSyncPlugin.getDefault().logMessage(
					"Resource " + res.getFullPath() + " was removed "
							+ getFlagTxt(delta.getFlags()));
			AdminFileManager.remove(res);
			update();
			break;
		case IResourceDelta.CHANGED:
			if (res.getType() == IResource.FILE
					&& (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
				VariantSyncPlugin.getDefault().logMessage(
						"Resource " + res.getFullPath() + " has changed "
								+ getFlagTxt(delta.getFlags()));
				IFile file = (IFile) res;
				IFileState[] states = null;
				try {
					states = file.getHistory(null);
					if (states.length > 0) {
						long t = states[0].getModificationTime();
						Date d = new Date(t);
						VariantSyncPlugin.getDefault().logMessage(
								DateFormat.getDateTimeInstance(DateFormat.SHORT,
										DateFormat.SHORT).format(d)
										+ "");
						AdminFileManager.createPatch(res);
						update();
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		return true;
	}

	// Nur f¨¹r Testen
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

	private void update() {
		VariantSyncPlugin.getDefault().updateSynchroInfo();
		Display display = VariantSyncPlugin.getStandardDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();
					if (window == null)
						return;
					IWorkbenchPage page = window.getActivePage();
					if (page == null)
						return;
					if (page.findView(ResourceChangesView.ID) != null) {
						((ResourceChangesView) page.findView(ResourceChangesView.ID))
								.refreshTree();
					}
				}
			});
		}
	}
}
