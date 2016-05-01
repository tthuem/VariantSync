package de.ovgu.variantsync.applicationlayer.datamodel.resources;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.VariantSyncConstants;

/**
 * Represents a file which can be synchronized.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
public class ResourceChangesFile implements IChangedFile {

	private String name;
	private List<IProject> projectList = new ArrayList<IProject>();
	private File file;
	private List<IChangedFile> children;
	private String status;

	public ResourceChangesFile(String name) {
		this.name = name;
	}

	@Override
	public List<IChangedFile> getChildren() {
		return this.children;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public IChangedFile getParent() {
		return null;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE);
	}

	@Override
	public boolean hasChildren() {
		if (children != null) {
			return !children.isEmpty();
		} else {
			return false;
		}
	}

	@Override
	public void addChildren(IChangedFile child) {
		if (children == null) {
			children = new ArrayList<IChangedFile>();
		}
		child.setParent(this);
		children.add(child);
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
		String[] timetxt = this.name.split("_");
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
			path = path.substring(path
					.indexOf(VariantSyncConstants.ADMIN_FOLDER)
					+ VariantSyncConstants.ADMIN_FOLDER.length());
			String[] pathtile = path.split("_");
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

	/**
	 * Adds a project this file belongs to.
	 * 
	 * @param project 
	 */
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

	@Override
	public void setParent(IChangedFile parent) {
		// not necessary
	}

}
