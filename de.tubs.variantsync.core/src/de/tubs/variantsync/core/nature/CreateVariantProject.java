package de.tubs.variantsync.core.nature;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.utilities.VariantSyncProgressMonitor;

public class CreateVariantProject extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {

			final Object firstElement = ((IStructuredSelection) currentSelection).getFirstElement();

			// Get an IResource as an adapter from the current selection
			final IAdapterManager adapterManager = Platform.getAdapterManager();
			final IResource resourceAdapter = adapterManager.getAdapter(firstElement, IResource.class);

			if (resourceAdapter != null) {
				final IResource resource = resourceAdapter;

				String projectName = resource.getName();
				projectName = projectName.substring(0, projectName.lastIndexOf("."));

//				new NewProjectAction(VariantSyncPlugin.getActiveWorkbenchWindow()).run();
				createJavaProjectWithVariantNature(projectName);

				// Reinitalize All
				VariantSyncPlugin.getConfigurationProjectManager().reinitialize();
			}
		}
		return null;
	}

	private IProject createJavaProjectWithVariantNature(String projectName) {
		final VariantSyncProgressMonitor progressMonitor = new VariantSyncProgressMonitor("Create Project " + projectName);
		// create project
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		try {
			progressMonitor.setSubTaskName("Create and open project");
			project.create(progressMonitor);
			project.open(progressMonitor);

			// set natures
			final IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { Variant.NATURE_ID, JavaCore.NATURE_ID });

			// create java project
			progressMonitor.setSubTaskName("Setting needed natures");
			project.setDescription(description, progressMonitor);
			final IJavaProject javaProject = JavaCore.create(project);

			// set build path
			final IClasspathEntry[] buildPath = { JavaCore.newSourceEntry(project.getFullPath().append("src")), JavaRuntime.getDefaultJREContainerEntry() };

			progressMonitor.setSubTaskName("Setting build paths of project");
			javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), progressMonitor);

			// create src folder
			IFolder folder = project.getFolder("src");
			if (!folder.exists()) {
				progressMonitor.setSubTaskName("Create src folder");
				folder.create(true, true, progressMonitor);
			}

			// create bin folder
			folder = project.getFolder("bin");
			if (!folder.exists()) {
				progressMonitor.setSubTaskName("Create bin folder");
				folder.create(true, true, progressMonitor);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return project;
	}

}
