package de.tubs.variantsync.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

import de.ovgu.featureide.fm.core.EclipseExtensionLoader;
import de.tubs.variantsync.core.managers.ConfigurationProjectManager;
import de.tubs.variantsync.core.managers.FeatureContextManager;
import de.tubs.variantsync.core.managers.MappingManager;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.monitor.ResourceChangeHandler;
import de.tubs.variantsync.core.nature.VariantNature;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.VariantSyncProgressMonitor;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.view.editor.PartAdapter;

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

	// The shared instance
	private static VariantSyncPlugin plugin;
	private static ResourceChangeHandler listener = new ResourceChangeHandler();
	private static ConfigurationProjectManager configurationProjectManager = new ConfigurationProjectManager();

	private final List<IEventListener> listeners = new ArrayList<>();

	/**
	 * The constructor
	 */
	public VariantSyncPlugin() {}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework. BundleContext)
	 */
	@Override
	public void start(BundleContext ctxt) throws Exception {
		super.start(ctxt);
		plugin = this;

		DeltaFactoryManager
				.setExtensionLoader(new EclipseExtensionLoader<>(PLUGIN_ID, IDeltaFactory.extensionPointID, IDeltaFactory.extensionID, IDeltaFactory.class));

		configurationProjectManager.initalize();

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				listenForActiveClass();
			}
		});
		addResourceChangeListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework. BundleContext)
	 */
	@Override
	public void stop(BundleContext ctxt) throws Exception {
		LogOperations.logRefactor("[stop]");
		configurationProjectManager.terminate();
		plugin = null;
		super.stop(ctxt);
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

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();
	}

	public static ConfigurationProjectManager getConfigurationProjectManager() {
		return configurationProjectManager;
	}

	public static ConfigurationProject getActiveConfigurationProject() {
		return configurationProjectManager.getActiveConfigurationProject();
	}

	public static FeatureContextManager getActiveFeatureContextManager() {
		return configurationProjectManager.getActiveConfigurationProject().getFeatureContextManager();
	}


	public static PatchesManager getActivePatchesManager() {
		return configurationProjectManager.getActiveConfigurationProject().getPatchesManager();
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Always good to have this static method as when dealing with IResources having a interface to get the editor is very handy
	 *
	 * @return
	 */
	public static ITextEditor getEditor() {
		if (getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof ITextEditor) {
			return (ITextEditor) getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} else {
			return null;
		}
	}

	public static IFile getEditorInput() {
		if (VariantSyncPlugin.getActiveWorkbenchWindow() == null) {
			return null;
		}
		final IEditorPart editorPart = VariantSyncPlugin.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if ((editorPart == null) || !(editorPart instanceof IFileEditorInput)) {
			return null;
		}
		return ((IFileEditorInput) editorPart.getEditorInput()).getFile();
	}

	public static void addResourceChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		try {
			listener.registerSaveParticipant();
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	public static void removeResourceChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
	}

	/**
	 * Listen whether the active file in the java editor changes.
	 */
	public void listenForActiveClass() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();

		final PartAdapter adapter = new PartAdapter();
		ww.getPartService().addPartListener(adapter);
	}

	public static void addNature(IProject project) {
		final VariantSyncProgressMonitor progressMonitor = new VariantSyncProgressMonitor(String.format("Adding VariantSync nature to %s", project.getName()));
		try {
			final IProjectDescription description = project.getDescription();
			final String[] natures = description.getNatureIds();

			// the natures array is copied to a newNatures array, in which the variant NATURE_ID is added as first element
			final String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 1, natures.length);

			newNatures[0] = VariantNature.NATURE_ID;

			description.setNatureIds(newNatures);
			progressMonitor.setSubTaskName("Add nature");
			project.setDescription(description, progressMonitor);
			progressMonitor.setSubTaskName("Refresh resources");
			project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
		} catch (final CoreException e) {
			LogOperations.logError("", e);
		}
	}

	public void addListener(IEventListener listener) {
		listeners.add(listener);

	}

	public void fireEvent(VariantSyncEvent event) {
		System.out.println(event);
		for (final IEventListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	public void removeListener(IEventListener listener) {
		listeners.remove(listener);
	}
}
