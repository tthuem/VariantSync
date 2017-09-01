package de.tubs.variantsync.core;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.internal.FeatureProject;
import de.ovgu.featureide.fm.core.EclipseExtensionLoader;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.monitor.ResourceChangeHandler;
import de.tubs.variantsync.core.nature.Variant;
import de.tubs.variantsync.core.patch.PatchFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.persistence.Persistence;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Christopher Sontag (c.sontag@tu-bs.de)
 * @version 1.0
 * @since 1.0.0.0
 */
public class VariantSyncPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.tubs.variantsync.core"; //$NON-NLS-1$

	// private static final String FEATUREEXPRESSION_PATH =
	// "/.featureExpression/featureExpressions.xml";

	// The shared instance
	private static VariantSyncPlugin plugin;
	private static Context context = null;

	/**
	 * The constructor
	 */
	public VariantSyncPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext ctxt) throws Exception {
		super.start(ctxt);
		plugin = this;
		context = Persistence.loadContext(getConfigurationProject());
		init();
		
		initResourceChangeListener();
		PatchFactoryManager.setExtensionLoader(new EclipseExtensionLoader<>(PLUGIN_ID, IPatchFactory.extensionPointID, IPatchFactory.extensionID, IPatchFactory.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		Persistence.writeContext(getContext());
		Persistence.writeFeatureExpressions(getContext().getFeatureExpressions());
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static VariantSyncPlugin getDefault() {
		return plugin;
	}

	public static IWorkspaceRoot getWorkspace() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();
	}

	public static Context getContext() {
		if (context == null)
			throw new NullPointerException("Context is not initialized!");
		return context;
	}

	/**
	 * Loads all contexts which are saved in a XML-file.
	 */
	private IFeatureProject getConfigurationProject() {
		for (IProject project : getWorkspace().getProjects()) {
			try {
				if (project.hasNature("de.ovgu.featureide.core.featureProjectNature")) {
					IFeatureProject featureProject = new FeatureProject(project);
					if (featureProject.getComposerID().equals("de.tubs.variantsync.core.composer")) {
						LogOperations.logInfo("Found configuration project with name: " + project.getName());
						return featureProject;
					}
					break;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void loadVariants() {
		if (context != null && context.getConfigurationProject() != null) {
			for (IFile file : context.getConfigurationProject().getAllConfigurations()) {
				System.out.println("Variant:" + file.getName());
				String projectName = file.getName().substring(0, file.getName().lastIndexOf("."));
				IProject project = getWorkspace().getProject(projectName);
				if (project.exists()) {
					context.addProject(project);
				} else {
					try {
						IMarker m = file.createMarker("de.tubs.variantsync.marker.error");
						m.setAttribute(IMarker.MESSAGE, "Project " + projectName + " is missing in the workspace");
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						m.setAttribute(IMarker.LINE_NUMBER, 0);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void loadFeatureExpressions() {
		if (context.getConfigurationProject() != null) {
			List<FeatureExpression> expressions = Persistence.loadFeatureExpressions();
			if (expressions.isEmpty()) {
				try {
					context.importFeaturesFromModel();
					LogOperations.logInfo("Loaded feature expressions from feature model");
				} catch (ProjectNotFoundException e) {
					System.out.println(e.toString());
				}
			} else {
				context.setFeatureExpressions(expressions);
			}
			
			
			// TODO
			// context.setFeatureExpressions(Persistence.loadFeatureExpression(
			// configurationProject.getProject().getFile(FEATUREEXPRESSION_PATH).getFullPath().toOSString()));
		}
	}
	

	private void initResourceChangeListener() {
		ResourceChangeHandler listener = new ResourceChangeHandler();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		try {
			listener.registerSaveParticipant();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void init() {
		getConfigurationProject();
		loadVariants();
		loadFeatureExpressions();
	}
	
	public void reinit() {
		getContext().reset();
		init();
	}

	public static void addNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 1, natures.length);

			newNatures[0] = Variant.NATURE_ID;

			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			LogOperations.logError("", e);
		}
	}

}
