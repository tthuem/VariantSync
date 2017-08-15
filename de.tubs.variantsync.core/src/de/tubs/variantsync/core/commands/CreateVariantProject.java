package de.tubs.variantsync.core.commands;

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

import de.tubs.variantsync.core.nature.Variant;

public class CreateVariantProject extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {

			Object firstElement = ((IStructuredSelection) currentSelection).getFirstElement();
			
			// Get an IResource as an adapter from the current selection
			IAdapterManager adapterManager = Platform.getAdapterManager();
			IResource resourceAdapter = adapterManager.getAdapter(firstElement, IResource.class);

			if (resourceAdapter != null) {
				IResource resource = resourceAdapter;
				
				String projectName = resource.getName();
				projectName = projectName.substring(0, projectName.lastIndexOf("."));

				createJavaProjectWithVariantNature(projectName);
			}
		}
		return null;
	}

	private void createJavaProjectWithVariantNature(String projectName) {
		// create project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		try {
		project.create(null);
		project.open(null);
		 
		//set natures
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] {Variant.NATURE_ID, JavaCore.NATURE_ID });
		 
		//create java project
		project.setDescription(description, null);
		IJavaProject javaProject = JavaCore.create(project);
		 
		//set build path
		IClasspathEntry[] buildPath = {
				JavaCore.newSourceEntry(project.getFullPath().append("src")),JavaRuntime.getDefaultJREContainerEntry() };
		 
		javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), null);
		 
		//create src folder
		IFolder folder = project.getFolder("src");
		folder.create(true, true, null);
		
		//create bin folder
		folder = project.getFolder("bin");
		folder.create(true, true, null);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
