package de.ovgu.variantsync;

import java.beans.XMLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ovgu.variantsync.console.ChangeOutPutConsole;
import de.ovgu.variantsync.resource.ResourceModificationManager;
import de.ovgu.variantsync.resource.SynchroInfo;

/**
 * 
 * @author Lei Luo
 * 
 */
public class VariantSyncPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.ovgu.variantsync";

	public static final String AdminFolder = ".variantsync";
	public static final String Adminfile = ".variantsyncInfo";

	// The shared instance
	private static VariantSyncPlugin plugin;

	private ChangeOutPutConsole console;

	private IProject changeViewProject = null;

	private ArrayList<IProject> projectList = new ArrayList<IProject>(0);

	private HashMap<IProject, SynchroInfo> synchroInfoMap = new HashMap<IProject, SynchroInfo>();

	private ResourceModificationManager resourceModificationManager;

	public VariantSyncPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		console = new ChangeOutPutConsole();
		resourceModificationManager = new ResourceModificationManager();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceModificationManager, IResourceChangeEvent.POST_CHANGE);
		resourceModificationManager.registerSaveParticipant();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.removeResourceChangeListener(resourceModificationManager);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static VariantSyncPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public void logMessage(String msg) {
		console.logMessage(msg);
	}

	public ChangeOutPutConsole getConsole() {
		return console;
	}

	public IProject getChangeViewsProject() {
		return this.changeViewProject;
	}

	public void setChangeViewsProject(IProject project) {
		this.changeViewProject = project;
	}

	public ArrayList<IProject> getSupportProjectList() {
		this.projectList.clear();
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
		ArrayList<IProject> returnList = new ArrayList<IProject>(projectList);
		return returnList;
	}

	public void updateSynchroInfo() {
		this.synchroInfoMap.clear();
		ArrayList<IProject> projects = this.getSupportProjectList();
		for (IProject project : projects) {
			IFile infoFile = project.getFolder(VariantSyncPlugin.AdminFolder).getFile(
					VariantSyncPlugin.Adminfile);
			SynchroInfo info = new SynchroInfo();
			if (infoFile.exists()) {
				try {
					XMLDecoder decoder;
					decoder = new XMLDecoder(infoFile.getContents());
					info = (SynchroInfo) decoder.readObject();
					decoder.close();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			this.synchroInfoMap.put(project, info);
		}
	}

	public SynchroInfo getSynchroInfoFrom(IProject project) {
		if (this.synchroInfoMap.get(project) == null) {
			this.updateSynchroInfo();
		}
		return this.synchroInfoMap.get(project);
	}
}
