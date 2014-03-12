package de.ovgu.variantsync.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.resource.AdminFileManager;
import de.ovgu.variantsync.resource.MergTool;
import de.ovgu.variantsync.resource.SynchroInfo;
import de.ovgu.variantsync.resource.SynchroInfoItem;
import de.ovgu.variantsync.resource.SynchroSet;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesFilePatch implements IResourceChangesViewItem {

	private String patchFileName;
	private String fileName;
	private String unidiff;
	private IProject project;
	private File file;
	private boolean synchro = false;
	private long timestamp;
	private IResourceChangesViewItem parent;
	private String status;
	public static Comparator<IResourceChangesViewItem> timeComparator = new Comparator<IResourceChangesViewItem>() {

		@Override
		public int compare(IResourceChangesViewItem o1, IResourceChangesViewItem o2) {
			if (o1 instanceof ResourceChangesFilePatch
					&& o2 instanceof ResourceChangesFilePatch) {
				return (int) (((ResourceChangesFilePatch) o1).getTimeStamp() - ((ResourceChangesFilePatch) o2)
						.getTimeStamp());
			}
			if (o1 instanceof ResourceChangesFile
					&& o2 instanceof ResourceChangesFilePatch) {
				return 1;
			}
			if (o1 instanceof ResourceChangesFolder
					&& o2 instanceof ResourceChangesFilePatch) {
				return 1;
			}
			return 0;
		}
	};

	public ResourceChangesFilePatch(String name, IProject project) {
		this.patchFileName = name;
		this.project = project;
		String infoTxt[] = this.patchFileName.split("_");
		if (infoTxt.length > 2) {
			String time = infoTxt[infoTxt.length - 1];
			this.timestamp = Long.parseLong(time);
		}
		this.synchro = infoTxt[infoTxt.length - 2].equals("1");
		int index = name.indexOf(infoTxt[infoTxt.length - 3]);
		this.fileName = name.substring(0, index - 1);
	}

	public boolean isSynchronisiert() {
		return this.synchro;
	}

	@Override
	public ArrayList<IResourceChangesViewItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return this.patchFileName;
	}

	@Override
	public IResourceChangesViewItem getParent() {
		return this.parent;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
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
		return false;
	}

	@Override
	public void addChildren(IResourceChangesViewItem child) {
	}

	@Override
	public void setParent(IResourceChangesViewItem parent) {
		this.parent = parent;

	}

	@Override
	public String getTime() {
		String timetxt[] = this.patchFileName.split("_");
		if (timetxt.length > 2) {
			String time = timetxt[timetxt.length - 1];
			Date date = new Date(Long.parseLong(time));
			return DateFormat.getDateTimeInstance().format(date);
		}
		return "";
	}

	public long getTimeStamp() {
		return this.timestamp;
	}

	@Override
	public void linkFile(File file) {
		this.file = file;
	}

	/*
	 * bekomm der Pfad(relativ) von notiert File(File und ordner)
	 */
	@Override
	public String getPath() {
		if (file != null) {
			if (this.status.equals(AdminFileManager.ADDFILE)
					|| this.status.equals(AdminFileManager.REMOVEFILE)
					|| this.status.equals(AdminFileManager.CHANGE)) {
				String path = file.getPath();
				path = path.substring(path.indexOf(VariantSyncPlugin.AdminFolder)
						+ VariantSyncPlugin.AdminFolder.length());
				String pathtile[] = path.split("_");
				String infoText = "_" + pathtile[pathtile.length - 3] + "_"
						+ pathtile[pathtile.length - 2] + "_"
						+ pathtile[pathtile.length - 1];
				path = path.substring(0,path.indexOf(infoText));
				return path;
			} else {
				String path = file.getParentFile().getPath();
				path = path.substring(path.indexOf(VariantSyncPlugin.AdminFolder)
						+ VariantSyncPlugin.AdminFolder.length());
				return path;
			}
		}
		return null;
	}

	public IProject getProject() {
		return this.project;
	}

	/*
	 * get possible Targets
	 */
	public ArrayList<IProject> getProjectList() {
		ArrayList<IProject> projectList = VariantSyncPlugin.getDefault()
				.getSupportProjectList();
		ArrayList<IProject> conflictProjectList = new ArrayList<IProject>(0);
		if (this.status.equals(AdminFileManager.ADDFILE)) {
			if (this.project.getFile(this.getPath()).exists()) {
				for (IProject project : projectList) {
					if (this.project.equals(project)) {
						conflictProjectList.add(project);
						continue;
					}
					IFile file = project.getFile(this.getPath());
					if (file.exists()) {
						conflictProjectList.add(project);
					}
				}
			} else {
				conflictProjectList.addAll(projectList);
			}
		}
		if (this.status.equals(AdminFileManager.REMOVEFILE)) {
			for (IProject project : projectList) {
				if (this.project.equals(project)) {
					conflictProjectList.add(project);
					continue;
				}
				IFile file = project.getFile(this.getPath());
				if (!file.exists()) {
					conflictProjectList.add(project);
				}
			}
		}
		if (this.status.equals(AdminFileManager.ADDFOLDER)) {
			if (this.project.getFolder(this.getPath()).exists()) {
				for (IProject project : projectList) {
					if (this.project.equals(project)) {
						conflictProjectList.add(project);
						continue;
					}
					IFolder folder = project.getFolder(this.getPath());
					if (folder.exists()) {
						conflictProjectList.add(project);
					}
				}
			} else {
				conflictProjectList.addAll(projectList);
			}
		}
		if (this.status.equals(AdminFileManager.REMOVEFOLDER)) {
			for (IProject project : projectList) {
				if (this.project.equals(project)) {
					conflictProjectList.add(project);
					continue;
				}
				IFolder folder = project.getFolder(this.getPath());
				if (!folder.exists()) {
					conflictProjectList.add(project);
				}
			}
		}
		if (this.status.equals(AdminFileManager.CHANGE)) {
			ArrayList<ResourceChangesFilePatch> patchs = ((ResourceChangesFile) this
					.getParent()).getPatchsFromProject(project);

			Comparator<IResourceChangesViewItem> comparator = Collections
					.reverseOrder(timeComparator);
			Collections.sort(patchs, comparator);

			List<String> fileLines = this.getCurrentFileFrom(this.getProject());
			if (fileLines.size() != 0) {
				boolean fileRemove = false;
				for (ResourceChangesFilePatch p : patchs) {
					if (p.status.equals(AdminFileManager.REMOVEFILE)
							|| p.status.equals(AdminFileManager.ADDFILE)) {
						conflictProjectList.addAll(projectList);
						fileRemove = true;
						break;
					}
					if (this.timestamp == p.timestamp) {
						break;
					}
					List<?> temp = DiffUtils.unpatch(fileLines, p.getPatch());
					fileLines.clear();
					for (Object o : temp) {
						fileLines.add((String) o);
					}
				}
				if (!fileRemove) {
					for (IProject checkProject : projectList) {
						if (this.getProject().equals(checkProject)) {
							conflictProjectList.add(checkProject);
							continue;
						}
						IFile file = checkProject.getFile(this.getPath());
						if (!file.exists()
								|| (file.exists() && file.getType() != IResource.FILE)) {
							conflictProjectList.add(checkProject);
							continue;
						}
						Patch pf12 = this.getPatch();
						List<String> checkFileList = this
								.getCurrentFileFrom(checkProject);
						Patch pf13 = DiffUtils.diff(
								DiffUtils.unpatch(fileLines, this.getPatch()),
								checkFileList);
						List<Delta> deltas12 = pf12.getDeltas();
						List<Delta> deltas13 = pf13.getDeltas();
						if (MergTool.konfliktErkennen(deltas12, deltas13)) {
							conflictProjectList.add(checkProject);
						}
					}
				}
			} else {
				conflictProjectList.addAll(projectList);
			}
		}
		projectList.removeAll(conflictProjectList);
		return projectList;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	public List<String> getCurrentFileFrom(IProject project) {
		IFile file = project.getFile(this.getPath());
		List<String> fileLines = new LinkedList<String>();
		if (file.exists() && file.getType() == IResource.FILE) {
			try {
				if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
					file.refreshLocal(IResource.DEPTH_ZERO, null);
				}
				InputStream fileinput = file.getContents();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						fileinput));
				String line = "";
				while ((line = reader.readLine()) != null) {
					fileLines.add(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileLines;
	}

	public Patch getPatch() {
		Patch patch = null;
		String parentFolder = this.file.getParentFile().getPath();
		IPath base = new Path(this.project.getLocation().toOSString());
		IPath temp = new Path(parentFolder).append(this.patchFileName);
		IPath relativePath = temp.makeRelativeTo(base);
		if (this.status.equals(AdminFileManager.CHANGE)) {
			if (!this.project.getFile(relativePath).exists()) {
				try {
					this.project.getFile(relativePath).refreshLocal(IResource.DEPTH_ZERO,
							null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			if (this.project.getFile(relativePath).exists()) {
				try {
					InputStream patchinput = this.project.getFile(relativePath)
							.getContents();
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							patchinput));
					List<String> lines = new LinkedList<String>();
					String line = "";
					unidiff = "";
					while ((line = reader.readLine()) != null) {
						lines.add(line);
						unidiff = unidiff + line + "\n";
					}
					patch = DiffUtils.parseUnifiedDiff(lines);
				} catch (Exception e) {
					e.printStackTrace();
					unidiff = "";
				}
			}
		}
		return patch;
	}

	public String getUnidiff() {
		if (this.unidiff == null) {
			getPatch();
		}
		return this.unidiff;
	}

	@SuppressWarnings("unchecked")
	public void synchronize(Object[] result) {
		if (this.status.equals(AdminFileManager.ADDFILE)) {
			IPath relativePath = new Path(this.getPath());
			for (Object object : result) {
				IProject project = (IProject) object;
				if (this.getProject().getFile(relativePath).exists()) {
					InputStream source;
					try {
						source = this.getProject().getFile(relativePath).getContents();
						IPath path = project.getFile(relativePath).getParent()
								.getProjectRelativePath();
						if (!path.isEmpty()) {
							IFolder folder = project.getFolder(path);
							if (!folder.exists()) {
								AdminFileManager.mkdirs(folder);
							}
						}
						IFile addFile = project.getFile(relativePath);
						SynchroSet.getInstance().addSynchroItem(addFile);
						this.synchroTargetAdd(project);
						addFile.create(source, IResource.FORCE, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		if (this.status.equals(AdminFileManager.REMOVEFILE)) {
			IPath relativePath = new Path(this.getPath());
			for (Object object : result) {
				IProject project = (IProject) object;
				if (project.getFile(relativePath).exists()) {
					try {
						IFile removeFile = project.getFile(relativePath);
						SynchroSet.getInstance().addSynchroItem(removeFile);
						this.synchroTargetAdd(project);
						removeFile.delete(true, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		if (this.status.equals(AdminFileManager.ADDFOLDER)) {
			IPath relativePath = new Path(this.getPath());
			for (Object object : result) {
				IProject project = (IProject) object;
				IFolder folder = project.getFolder(relativePath);
				if (!folder.exists()) {
					try {
						this.synchroTargetAdd(project);
						AdminFileManager.mkdirs(folder);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		if (this.status.equals(AdminFileManager.REMOVEFOLDER)) {
			IPath relativePath = new Path(this.getPath());
			for (Object object : result) {
				IProject project = (IProject) object;
				if (project.getFolder(relativePath).exists()) {
					try {
						IFolder removeFolder = project.getFolder(relativePath);
						this.synchroTargetAdd(project);
						AdminFileManager.deldirs(removeFolder);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		if (this.status.equals(AdminFileManager.CHANGE)) {
			List<String> fList1 = new LinkedList<String>();
			List<String> fList2 = new LinkedList<String>();
			List<String> fList3 = new LinkedList<String>();
			ArrayList<ResourceChangesFilePatch> patchs = ((ResourceChangesFile) this
					.getParent()).getPatchsFromProject(project);
			Comparator<IResourceChangesViewItem> comparator = Collections
					.reverseOrder(timeComparator);
			Collections.sort(patchs, comparator);
			List<String> fileLines = this.getCurrentFileFrom(this.getProject());
			if (fileLines.size() != 0) {
				boolean fileRemove = false;
				for (ResourceChangesFilePatch p : patchs) {
					if (p.status.equals(AdminFileManager.REMOVEFILE)) {
						fileRemove = true;
						break;
					}
					if (this.timestamp == p.timestamp) {
						break;
					}
					List<?> temp = DiffUtils.unpatch(fileLines, p.getPatch());
					fileLines.clear();
					for (Object o : temp) {
						fileLines.add((String) o);
					}
				}
				if (!fileRemove) {
					fList2.addAll(fileLines);
					fList1.addAll(((LinkedList<String>) DiffUtils.unpatch(fileLines,
							this.getPatch())));

					IPath relativePath = new Path(this.getPath());
					for (Object object : result) {
						IProject project = (IProject) object;
						if (this.getProject().getFile(relativePath).exists()
								&& project.getFile(relativePath).exists()) {
							fList3 = this.getCurrentFileFrom(project);
							try {
								List<String> mergedList = MergTool.merg(fList1, fList2,
										fList3);
								String mergedString = "";
								for (String txt : mergedList) {
									mergedString = mergedString + txt + "\n";
								}
								if (mergedString != null) {
									InputStream source = new ByteArrayInputStream(
											mergedString.getBytes());
									IFile changeFile = project.getFile(relativePath);
									SynchroSet.getInstance().addSynchroItem(changeFile);
									this.synchroTargetAdd(project);
									changeFile.setContents(source, true, true, null);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private void synchroTargetAdd(IProject project) {
		IFolder infoFolder = this.project.getFolder(VariantSyncPlugin.AdminFolder);
		if (!infoFolder.exists()) {
			try {
				infoFolder.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		IFile infoFile = this.project.getFolder(VariantSyncPlugin.AdminFolder).getFile(
				VariantSyncPlugin.Adminfile);
		SynchroInfo info = null;
		if (!infoFile.exists()) {
			info = new SynchroInfo();
			try {
				String inhalt = "";
				InputStream source = new ByteArrayInputStream(inhalt.getBytes());
				infoFile.create(source, IResource.FORCE, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				XMLDecoder decoder;
				decoder = new XMLDecoder(infoFile.getContents());
				info = (SynchroInfo) decoder.readObject();
				decoder.close();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		info.addSynchroItem(new SynchroInfoItem(this.patchFileName, project.getName()));
		try {
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(infoFile
					.getLocation().toFile()));
			encoder.writeObject(info);
			encoder.close();
			infoFile.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getSynchoProjects() {
		return VariantSyncPlugin.getDefault().getSynchroInfoFrom(this.project)
				.getSynchroProjectsFrom(this);
	}
}
