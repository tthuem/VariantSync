package de.ovgu.variantsync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesFolder implements IResourceChangesViewItem {

	private String name;
	private File file;
	private long timestamp;
	// private int state = UNMODIFICATION;
	private IResourceChangesViewItem parent;
	private ArrayList<IResourceChangesViewItem> children;
	private String status;

	public ResourceChangesFolder(String name, IProject project) {
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
				.getImage(ISharedImages.IMG_OBJ_FOLDER);
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

	@Override
	public void setParent(IResourceChangesViewItem parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResourceChangesFolder) {
			return this.name.equals(((ResourceChangesFolder) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {

		return this.name.hashCode();
	}

	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkFile(File file) {
		this.file = file;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

}
