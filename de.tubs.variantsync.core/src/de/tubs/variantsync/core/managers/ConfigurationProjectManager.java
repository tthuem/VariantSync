package de.tubs.variantsync.core.managers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;
import de.ovgu.featureide.fm.core.io.EclipseFileSystem;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.MarkerUtils;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

public class ConfigurationProjectManager extends AManager implements IEventListener, de.ovgu.featureide.fm.core.base.event.IEventListener {

	private static HashMap<IFeatureProject, ConfigurationProject> INSTANCES = new HashMap<>();
	private static ConfigurationProject lastRequestedConfiguration = null;

	public void initalize() {
		ConfigurationProject lastConfiguration = null;
		for (IFeatureProject project : findConfigurationProjects()) {
			try {
				MarkerUtils.cleanProject(project.getProject());
			} catch (CoreException e) {
				LogOperations.logError("A marker could not be deleted", e);
			}
			if (project.getProjectName().equals(VariantSyncPlugin.getDefault().getPreferenceStore().getString("lastRequestedConfiguration"))) {
				lastConfiguration = getConfigurationProject(project);
			} else {
				getConfigurationProject(project);
			}
		}
		if (lastConfiguration != null && lastRequestedConfiguration != lastConfiguration) {
			lastRequestedConfiguration = lastConfiguration;
			fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_CHANGED, null, lastRequestedConfiguration));
		}
		for (ConfigurationProject configurationProject : INSTANCES.values()) {
			findVariants(configurationProject);
			configurationProject.load();
		}

		fireEvent(new VariantSyncEvent(this, EventType.INITALIZED, null, null));
	}

	public void reinitialize() {
		terminate();
		initalize();
	}

	public void terminate() {
		if (lastRequestedConfiguration != null && lastRequestedConfiguration.getFeatureProject() != null) {
			VariantSyncPlugin.getDefault().getPreferenceStore().setValue("lastRequestedConfiguration",
					lastRequestedConfiguration.getFeatureProject().getProjectName());
		}

		for (ConfigurationProject configurationProject : INSTANCES.values()) {
			if (configurationProject != null && configurationProject.getFeatureProject().getProject().exists()) {
				configurationProject.save();
			}
		}
	}

	public ConfigurationProject getConfigurationProject(IFeatureProject project) {
		if (project != null) {
			for (IFeatureProject featureProject : INSTANCES.keySet()) {
				if (featureProject.getProjectName().equals(project.getProjectName())) {
					ConfigurationProject configurationProject = INSTANCES.get(featureProject);
					if (lastRequestedConfiguration != configurationProject) {
						lastRequestedConfiguration = configurationProject;
						fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_CHANGED, null, lastRequestedConfiguration));
					}
					return configurationProject;
				}
			}
			ConfigurationProject configurationProject = new ConfigurationProject();
			configurationProject.setFeatureProject(project);
			INSTANCES.put(project, configurationProject);
			if (lastRequestedConfiguration != configurationProject) {
				lastRequestedConfiguration = configurationProject;
				fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_CHANGED, null, lastRequestedConfiguration));
			}
			return configurationProject;
		}
		return null;
	}

	/**
	 * Loads all contexts which are saved in a XML-file.
	 */
	private List<IFeatureProject> findConfigurationProjects() {
		//System.out.println("[ConfigurationProjectManager.findConfigurationProjects]");
		List<IFeatureProject> projects = new ArrayList<>();
		for (IFeatureProject project : CorePlugin.getFeatureProjects()) {
			//System.out.print("    project " + project.getProjectName());
			if (project.getComposerID().equals("de.tubs.variantsync.core.composer")) {
				LogOperations.logInfo("Found configuration project with name: " + project.getProjectName());
				projects.add(project);
				//System.out.println("    is variant composing");
			} else {
				//System.out.println("    is NOT variant composing");
			}
		}
		return projects;
	}

	private void findVariants(ConfigurationProject configurationProject) {
		for (Path path : configurationProject.getFeatureProject().getAllConfigurations()) {
			IFile file = (IFile) EclipseFileSystem.getResource(path);
			String projectName = file.getName().substring(0, file.getName().lastIndexOf("."));
			IProject project = VariantSyncPlugin.getWorkspace().getProject(projectName);
			if (project.exists()) {
				configurationProject.addVariant(project);
			} else {
				try {
					IMarker m = file.createMarker("de.tubs.variantsync.marker.error");
					m.setAttribute(IMarker.MESSAGE, "Project " + projectName + " is missing in the workspace");
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					m.setAttribute(IMarker.LINE_NUMBER, 0);
				} catch (CoreException e) {
					LogOperations.logError("Marker cannot be created!", e);
				}
			}
		}
		configurationProject.getMappingManager().load();
	}

	public IFeatureProject getFeatureProject(IProject project) {
		for (IFeatureProject featureProject : INSTANCES.keySet()) {
			if (project.getName().equals(featureProject.getProjectName())) return featureProject;
			if (featureProject.getProject().exists() && INSTANCES.get(featureProject).getVariants().contains(project)) return featureProject;
		}
		return null;
	}

	public ConfigurationProject getConfigurationProject(IProject project) {
		if (project != null) {
			IFeatureProject featureProject = getFeatureProject(project);
			if (featureProject != null) {
				return getConfigurationProject(featureProject);
			}
		}
		return null;
	}

	/**
	 * Returns the configuration project for the active editor
	 * 
	 * @return
	 */
	public ConfigurationProject getActiveConfigurationProject() {
		if (VariantSyncPlugin.getEditorInput() == null) return lastRequestedConfiguration;
		return getConfigurationProject(VariantSyncPlugin.getEditorInput().getProject());
	}

	@Override
	public void propertyChange(FeatureIDEEvent event) {
		switch (event.getEventType()) {
		case FEATURE_ADD:
			LogOperations.logInfo("Feature added: " + event);
		case MODEL_DATA_SAVED:
			LogOperations.logInfo("Model Event" + event);
			if (event.getSource() instanceof IFeatureModel) {
				IFeatureModel model = (IFeatureModel) event.getSource();
				List<String> featureExpressions = getActiveConfigurationProject().getFeatureContextManager().getContextsAsStrings();
				for (IFeature feature : model.getFeatures()) {
					if (!featureExpressions.contains(feature.getName())) {
						getActiveConfigurationProject().getFeatureContextManager().addContext(feature.getName());
					}
				}
			}
			// context.importFeaturesFromModel();
			break;
		default:
			break;

		}
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_SET:
			if (event.getSource() instanceof ConfigurationProject) {
				ConfigurationProject context = (ConfigurationProject) event.getSource();
				if (context.getFeatureProject() != null) {
					context.getFeatureProject().getFeatureModelManager().addListener(this);
				}
			}
			break;
		default:
			break;
		}
	}

}
