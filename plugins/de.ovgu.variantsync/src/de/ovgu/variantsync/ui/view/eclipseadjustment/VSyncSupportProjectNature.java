package de.ovgu.variantsync.ui.view.eclipseadjustment;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.ui.view.resourcechanges.ResourceChangesView;
import de.ovgu.variantsync.utilities.LogOperations;

/**
 * 
 * @author Lei Luo
 * 
 */
public class VSyncSupportProjectNature implements IProjectNature {

	public static final String NATURE_ID = VariantSyncConstants.PLUGIN_ID + ".vsyncSupportProjectNature";
	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	@Override
	public void configure() throws CoreException {
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		refreshProject();
		VariantSyncPlugin.getDefault().logMessage("Project \"" + project.getName() + "\" is supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	@Override
	public void deconfigure() throws CoreException {
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		refreshProject();
		VariantSyncPlugin.getDefault().logMessage("Project \"" + project.getName() + "\" is not supported.");
		IFolder folder = project.getFolder(VariantSyncConstants.ADMIN_FOLDER);
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
		if (!project.isOpen()) {
			return;
		}

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			LogOperations.logError("Project description could not be retrieved.", e);
			return;
		}

		// Determine if the project already has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index != -1) {
			return;
		}

		// Add the nature
		newIds.add(NATURE_ID);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description.
		try {
			project.setDescription(description, new NullProgressMonitor());
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			refreshProject();
		} catch (CoreException e) {
			LogOperations.logError("Project description could not be set.", e);
		}
	}

	public static boolean hasNature(IProject project) {
		try {
			return project.isOpen() && project.hasNature(NATURE_ID);
		} catch (CoreException e) {
			LogOperations.logError("Project nature support could not be checked.", e);
			return false;
		}
	}

	public static void removeNature(IProject project) {

		// Cannot modify closed projects.
		if (!project.isOpen()) {
			return;
		}

		// Get the description.
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			LogOperations.logError("Project description could not be retrieved.", e);
			return;
		}

		// Determine if the project has the nature.
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));
		int index = newIds.indexOf(NATURE_ID);
		if (index == -1) {
			return;
		}

		// Remove the nature
		newIds.remove(index);
		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description.
		try {
			project.setDescription(description, null);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			refreshProject();
		} catch (CoreException e) {
			LogOperations.logError("Project description could not be set.", e);
		}
	}

	private static void refreshProject() {
		try {
			Robot r = null;
			r = new Robot();
			int keyCode = KeyEvent.VK_F5;
			r.keyPress(keyCode);
			r.keyRelease(keyCode);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// update the view
	private static void update() {
		Display display = VariantSyncPlugin.getStandardDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (window == null) {
						return;
					}
					IWorkbenchPage page = window.getActivePage();
					if (page == null) {
						return;
					}
					if (page.findView(ResourceChangesView.ID) != null) {
						((ResourceChangesView) page.findView(ResourceChangesView.ID)).refreshTree();
					}
				}
			});
		}
	}
}
