package de.ovgu.variantsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.views.ResourceChangesView;

/**
 * 
 * @author Lei Luo
 * 
 */
public class VSyncSupportProjectNature implements IProjectNature {

	public static final String NATURE_ID = VariantSyncPlugin.PLUGIN_ID
			+ ".vsyncSupportProjectNature";
	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		VariantSyncPlugin.getDefault().logMessage(
				"Project \"" + project.getName() + "\" is supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		VariantSyncPlugin.getDefault().logMessage(
				"Project \"" + project.getName() + "\" is not supported.");
		IFolder folder = project.getFolder(VariantSyncPlugin.AdminFolder);
		folder.delete(true, false, null);
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	@Override
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static void addNature(IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen())
			return;

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
			return;
		}

		// Determine if the project already has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index != -1)
			return;

		// Add the nature
		newIds.add(NATURE_ID);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description.
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
		}
	}

	public static boolean hasNature(IProject project) {
		try {
			return project.isOpen() && project.hasNature(NATURE_ID);
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
			return false;
		}
	}

	public static void removeNature(IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen())
			return;

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
			return;
		}

		// Determine if the project has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index == -1)
			return;

		// Remove the nature
		newIds.remove(index);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description.
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			VariantSyncLog.logError(e);
		}
	}

	// update the view
	private void update() {
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
