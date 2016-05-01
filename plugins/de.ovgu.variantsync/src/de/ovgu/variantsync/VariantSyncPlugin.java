package de.ovgu.variantsync;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.Util;
import de.ovgu.variantsync.applicationlayer.context.ContextProvider;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.datamodel.monitoring.MonitorItemStorage;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperationProvider;
import de.ovgu.variantsync.applicationlayer.features.FeatureProvider;
import de.ovgu.variantsync.applicationlayer.features.IFeatureOperations;
import de.ovgu.variantsync.applicationlayer.monitoring.ChangeListener;
import de.ovgu.variantsync.applicationlayer.monitoring.MonitorNotifier;
import de.ovgu.variantsync.applicationlayer.synchronization.SynchronizationProvider;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;
import de.ovgu.variantsync.presentationlayer.controller.ControllerTypes;
import de.ovgu.variantsync.presentationlayer.view.AbstractView;
import de.ovgu.variantsync.presentationlayer.view.console.ChangeOutPutConsole;
import de.ovgu.variantsync.presentationlayer.view.context.MarkerHandler;
import de.ovgu.variantsync.presentationlayer.view.context.PartAdapter;
import de.ovgu.variantsync.presentationlayer.view.eclipseadjustment.VSyncSupportProjectNature;
import de.ovgu.variantsync.utilitylayer.UtilityModel;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Entry point to start plugin variantsync. Variantsync supports developers to
 * synchronize similar software projects which are developed in different
 * variants. This version is based on first version of variantsync which was
 * developed by Lei Luo in 2012.
 * 
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 2.0
 * @since 14.05.2015
 */
public class VariantSyncPlugin extends AbstractUIPlugin {

	// shared instance
	private static VariantSyncPlugin plugin;
	private ChangeOutPutConsole console;
	private List<IProject> projectList = new ArrayList<IProject>();
	private Map<IProject, MonitorItemStorage> synchroInfoMap = new HashMap<IProject, MonitorItemStorage>();
	private ChangeListener resourceModificationListener;
	private IPersistanceOperations persistenceOp = ModuleFactory
			.getPersistanceOperations();
	private IContextOperations contextOp = ModuleFactory.getContextOperations();
	private IFeatureOperations featureOp = ModuleFactory.getFeatureOperations();
	private ControllerHandler controller = ControllerHandler.getInstance();

	@Override
	public void start(BundleContext context) {
		try {
			super.start(context);
		} catch (Exception e) {
			LogOperations.logError("Plugin could not be started.", e);
		}
		plugin = this;
		console = new ChangeOutPutConsole();
		removeAllMarkers();
		initMVC();
		initContext();
		initFeatureExpressions();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				listenForActiveClass();
			}
		});
		try {
			initResourceMonitoring();
		} catch (CoreException e) {
			LogOperations.logError(
					"Resouce monitoring could not be initialized.", e);
		}
	}

	/**
	 * Listen whether the active file in the java editor changes.
	 */
	public void listenForActiveClass() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = ww.getActivePage();
		if (page == null)
			return;
		page.addPartListener(new PartAdapter());
	}

	@Override
	public void stop(BundleContext context) {

		persistenceOp.saveFeatureExpressions(featureOp.getFeatureExpressions());

		// stop default-context or any other active context
		try {
			contextOp.stopRecording();
			persistenceOp.saveContext(contextOp
					.getContext(VariantSyncConstants.DEFAULT_CONTEXT), Util
					.parseStorageLocation(contextOp
							.getContext(VariantSyncConstants.DEFAULT_CONTEXT)));
		} catch (NullPointerException e) {
			// recordings was already stopped and contexts were cleaned up
		}
		plugin = null;
		try {
			super.stop(context);
		} catch (Exception e) {
			LogOperations.logError("Plugin could not be started.", e);
		}
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.removeResourceChangeListener(resourceModificationListener);
	}

	public String getWorkspaceLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString();
	}

	/**
	 * Returns list of projects which has eclipse nature support and variantsync
	 * nature id.
	 * 
	 * @return list of projects
	 */
	public List<IProject> getSupportProjectList() {
		this.projectList.clear();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			try {
				if (project.isOpen()
						&& project
								.hasNature(VSyncSupportProjectNature.NATURE_ID)) {
					this.projectList.add(project);
				}
			} catch (CoreException e) {
				UtilityModel.getInstance().handleError(e,
						"nature support could not be checked");
			}
		}
		return new ArrayList<IProject>(projectList);
	}

	/**
	 * Updates synchroInfoMap. This map contains IProject-objects mapped to
	 * SynchroInfo objects.
	 */
	public void updateSynchroInfo() {
		this.synchroInfoMap.clear();
		List<IProject> projects = this.getSupportProjectList();
		for (IProject project : projects) {
			IFile infoFile = project.getFolder(
					VariantSyncConstants.ADMIN_FOLDER).getFile(
					VariantSyncConstants.ADMIN_FILE);
			MonitorItemStorage info = new MonitorItemStorage();
			if (infoFile.exists()) {
				try {
					info = persistenceOp.readSynchroXMLFile(infoFile
							.getContents());
				} catch (CoreException e) {
					UtilityModel.getInstance().handleError(e,
							"info file could not be read");
				}
			}
			this.synchroInfoMap.put(project, info);
		}
	}

	/**
	 * Initializes model view controller implementation to encapsulate gui
	 * elements from business logic.
	 */
	private void initMVC() {

		// register models as request handler for controller
		controller.addModel(new SynchronizationProvider(),
				ControllerTypes.SYNCHRONIZATION);
		controller
				.addModel(new DeltaOperationProvider(), ControllerTypes.DELTA);
		controller.addModel(new FeatureProvider(), ControllerTypes.FEATURE);
		controller.addModel(new ContextProvider(), ControllerTypes.CONTEXT);
		controller.addModel(MonitorNotifier.getInstance(),
				ControllerTypes.MONITOR);
	}

	/**
	 * Loads all contexts which are saved in a XML-file.
	 */
	private void initContext() {
		String storageLocation = VariantSyncPlugin.getDefault()
				.getWorkspaceLocation() + VariantSyncConstants.CONTEXT_PATH;
		File folder = new File(storageLocation);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				Context c = persistenceOp.loadContext(f.getPath());
				if (c != null)
					contextOp.addContext(c);
				else
					System.out.println(f.toString());
			}
		}
		contextOp.activateContext(VariantSyncConstants.DEFAULT_CONTEXT);
	}

	/**
	 * Loads all feature expressions which are saved in a XML-file.
	 */
	private void initFeatureExpressions() {
		String storageLocation = VariantSyncPlugin.getDefault()
				.getWorkspaceLocation()
				+ VariantSyncConstants.FEATURE_EXPRESSION_PATH;
		File folder = new File(storageLocation.substring(0,
				storageLocation.lastIndexOf("/")));
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				FeatureExpressions featureExpressions = persistenceOp
						.loadFeatureExpressions(f.getPath());
				Iterator<String> it = featureExpressions
						.getFeatureExpressions().iterator();
				while (it.hasNext()) {
					featureOp.addFeatureExpression(it.next());
				}
				it = featureOp.getFeatureModel().getFeatureNames().iterator();
				while (it.hasNext()) {
					featureOp.addFeatureExpression(it.next());
				}
			}
		}
	}

	/**
	 * Prepares resource monitoring in eclipse workspace. Resource monitoring
	 * means that projects will be watches. If a projects´s resource changes and
	 * change was saved, monitoring will notice changed resource (POST_CHANGE).
	 * A resource is a file or folder.
	 * 
	 * @throws CoreException
	 *             resources could not be monitored
	 */
	private void initResourceMonitoring() throws CoreException {
		resourceModificationListener = new ChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceModificationListener, IResourceChangeEvent.POST_CHANGE);
		resourceModificationListener.registerSaveParticipant();
	}

	/**
	 * Returns monitored items of specific project.
	 * 
	 * @param project
	 *            the project to get monitored items from
	 * @return MonitorItemStorage
	 */
	public MonitorItemStorage getSynchroInfoFrom(IProject project) {
		if (this.synchroInfoMap.get(project) == null) {
			this.updateSynchroInfo();
		}
		return this.synchroInfoMap.get(project);
	}

	/**
	 * Registers a view. If a model fires an event, all registered views receive
	 * this element.
	 * 
	 * @param view
	 *            view to register
	 */
	public void registerView(AbstractView view, ControllerTypes type) {
		controller.addView(view, type);
	}

	/**
	 * Removes a view from controller. If a model fires an event, this view will
	 * no longer receive this event.
	 * 
	 * @param view
	 *            view to remove
	 */
	public void removeView(AbstractView view, ControllerTypes type) {
		controller.removeView(view, type);
	}

	/**
	 * @return the display
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Logs a message on eclipse console.
	 * 
	 * @param msg
	 *            the message to log
	 */
	public void logMessage(String msg) {
		console.logMessage(msg);
	}

	/**
	 * @return the console
	 */
	public ChangeOutPutConsole getConsole() {
		return console;
	}

	/**
	 * @return the controller
	 */
	public ControllerHandler getController() {
		return controller;
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
	 * Always good to have this static method as when dealing with IResources
	 * having a interface to get the editor is very handy
	 * 
	 * @return
	 */
	public static ITextEditor getEditor() {
		return (ITextEditor) getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
	}

	public static Shell getShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	private void removeAllMarkers() {
		List<IProject> projects = getSupportProjectList();
		for (IProject p : projects) {
			MarkerHandler.getInstance().clearAllMarker(p);
		}
	}

	public ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(VariantSyncConstants.PLUGIN_ID, path);
	}
}
