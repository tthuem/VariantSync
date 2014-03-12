package de.ovgu.variantsync.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ovgu.variantsync.VSyncSupportProjectNature;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.model.*;
import de.ovgu.variantsync.resource.AdminFileManager;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesContentProvider implements ITreeContentProvider {

	IResourceChangesViewItem invisibleRoot;

	private ArrayList<IProject> projectList = new ArrayList<IProject>(0);

	private void initalize(ResourceChangesView view) {
		projectList.clear();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				if (project.isOpen()
						&& project.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
					this.projectList.add(project);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		IResourceChangesViewItem root;
		invisibleRoot = new ResourceChangesFile("");
		if (projectList.size() > 0) {
			root = new ResourceChangesFolder("Project root", null);
			invisibleRoot.addChildren(root);
			for (IProject project : projectList) {
				IPath adminPath = project.getLocation().append(
						VariantSyncPlugin.AdminFolder);
				File admin = new File(adminPath.toOSString());
				if (admin.exists())
					scanAdminFiles(admin, root, project);
			}
		} else {
			root = new ResourceChangesFolder("No Changes", null);
			invisibleRoot.addChildren(root);
		}
	}

	private void scanAdminFiles(File file, IResourceChangesViewItem root, IProject project) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				IResourceChangesViewItem subdir = new ResourceChangesFolder(f.getName(),
						project);
				if (root.hasChildren() && root.getChildren().contains(subdir)) {
					IResourceChangesViewItem tempDir = root.getChildren().get(
							root.getChildren().indexOf(subdir));
					scanAdminFiles(f, tempDir, project);
				} else if (!root.hasChildren() || !root.getChildren().contains(subdir)) {
					subdir.linkFile(f);
					root.addChildren(subdir);
					scanAdminFiles(f, subdir, project);
				}
			}
			if (f.isFile()) {
				String adminFileInfo[] = f.getName().split("_");
				if (adminFileInfo.length >= 4) {
					String event = adminFileInfo[adminFileInfo.length - 3];
					if (event.equals(AdminFileManager.ADDFOLDER)
							|| event.equals(AdminFileManager.REMOVEFOLDER)) {
						IResourceChangesViewItem subfile = new ResourceChangesFilePatch(
								f.getName(), project);
						subfile.setStatus(event);
						subfile.linkFile(f);
						root.addChildren(subfile);
					} else {
						String adminInfoTeil = "_"
								+ adminFileInfo[adminFileInfo.length - 3] + "_"
								+ adminFileInfo[adminFileInfo.length - 2] + "_"
								+ adminFileInfo[adminFileInfo.length - 1];
						ResourceChangesFile temp = new ResourceChangesFile(f.getName()
								.replaceAll(adminInfoTeil, ""));
						ResourceChangesFilePatch patch = new ResourceChangesFilePatch(
								f.getName(), project);
						patch.setStatus(adminFileInfo[adminFileInfo.length - 3]);
						patch.linkFile(f);
						if (root.hasChildren() && root.getChildren().contains(temp)) {
							int index = root.getChildren().indexOf(temp);
							IResourceChangesViewItem existFileKnoten = root.getChildren()
									.get(index);
							((ResourceChangesFile) existFileKnoten).addProject(project);
							existFileKnoten.addChildren(patch);
						} else {
							root.addChildren(temp);
							temp.addProject(project);
							temp.addChildren(patch);
						}
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ResourceChangesView) {
			ResourceChangesView view = (ResourceChangesView) parentElement;
			initalize(view);
			if (invisibleRoot.hasChildren()) {
				return invisibleRoot.getChildren().toArray();
			}
		}
		if (parentElement instanceof IResourceChangesViewItem) {
			List<IResourceChangesViewItem> elements = ((IResourceChangesViewItem) parentElement)
					.getChildren();
			if (elements != null) {
				Collections.sort(elements, ResourceChangesFilePatch.timeComparator);
				return elements.toArray();
			}
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IResourceChangesViewItem) {
			return ((IResourceChangesViewItem) element).hasChildren();
		}
		return false;
	}
}
