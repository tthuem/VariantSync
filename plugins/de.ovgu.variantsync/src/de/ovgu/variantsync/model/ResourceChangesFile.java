package de.ovgu.variantsync.model;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.VariantSyncPlugin;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesFile implements IResourceChangesViewItem {

	private String name;
	private ArrayList<IProject> projectList = new ArrayList<IProject>();
	private File file;
	private long timestamp;
	private IResourceChangesViewItem parent;
	private ArrayList<IResourceChangesViewItem> children;
	private String status;

	public ResourceChangesFile(String name) {
		this.name = name;
	}

	@Override
	public ArrayList<IResourceChangesViewItem> getChildren() {
		return this.children;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public IResourceChangesViewItem getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE);
	}

	@Override
	public Action[] getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChildren(IResourceChangesViewItem child) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasChildren() {
		if (children != null) {
			return this.children.size() > 0;
		} else {
			return false;
		}
	}

	@Override
	public void addChildren(IResourceChangesViewItem child) {
		if (children == null) {
			children = new ArrayList<IResourceChangesViewItem>();
		}
		child.setParent(this);
		children.add(child);
	}

	public void setParent(IResourceChangesViewItem parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResourceChangesFile) {
			return this.name.equals(((ResourceChangesFile) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String getTime() {
		String timetxt[] = this.name.split("_");
		if (timetxt.length > 3) {
			String time = timetxt[timetxt.length - 1];
			Date date = new Date(Long.parseLong(time));
			return DateFormat.getDateTimeInstance().format(date);
		}
		return "";
	}

	@Override
	public void linkFile(File file) {
		this.file = file;
	}

	@Override
	public String getPath() {
		if (file != null) {
			String path = file.getParentFile().getPath();
			path = path.substring(path.indexOf(VariantSyncPlugin.AdminFolder)
					+ VariantSyncPlugin.AdminFolder.length());
			String pathtile[] = path.split("_");
			if (pathtile.length > 3) {
				String infoText = "_" + pathtile[pathtile.length - 3] + "_"
						+ pathtile[pathtile.length - 2] + "_"
						+ pathtile[pathtile.length - 1];
				path = path.substring(0, path.indexOf(infoText));
			}
			return path;
		}
		return null;
	}

	public ArrayList<IProject> getProjectList() {
		ArrayList<IProject> result = new ArrayList<IProject>();
		result.addAll(this.projectList);
		return result;
	}

	public void addProject(IProject project) {
		if (!this.projectList.contains(project)) {
			this.projectList.add(project);
		}
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	public ArrayList<ResourceChangesFilePatch> getPatchsFromProject(IProject project) {
		ArrayList<ResourceChangesFilePatch> patchs = new ArrayList<ResourceChangesFilePatch>(
				0);
		for (IResourceChangesViewItem temp : this.getChildren()) {
			if (temp instanceof ResourceChangesFilePatch) {
				if (((ResourceChangesFilePatch) temp).getProject().equals(project)) {
					patchs.add((ResourceChangesFilePatch) temp);
				}
			}
		}
		Collections.sort(patchs, ResourceChangesFilePatch.timeComparator);
		return patchs;
	}

}
