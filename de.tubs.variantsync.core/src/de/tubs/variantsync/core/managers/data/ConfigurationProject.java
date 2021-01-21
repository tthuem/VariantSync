package de.tubs.variantsync.core.managers.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.io.EclipseFileSystem;
import de.ovgu.featureide.fm.core.io.manager.ConfigurationManager;
import de.tubs.variantsync.core.managers.AManager;
import de.tubs.variantsync.core.managers.FeatureContextManager;
import de.tubs.variantsync.core.managers.ISaveableManager;
import de.tubs.variantsync.core.managers.MappingManager;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

/**
 * A class for managing all informations about the product line for one
 * configuration project
 *
 * @author Christopher Sontag
 * @since 1.1
 */
public class ConfigurationProject extends AManager implements ISaveableManager {

	private IFeatureProject configurationProject = null;

	private FeatureContextManager featureContextManager = new FeatureContextManager(this);
	private MappingManager mappingManager = new MappingManager(this);
	private PatchesManager patchesManager = new PatchesManager(this);

	private List<IProject> projects = new ArrayList<>();

	public IFeatureProject getFeatureProject() {
		return configurationProject != null ? configurationProject.getProject().exists() ? configurationProject : null
				: null;
	}

	public void setFeatureProject(IFeatureProject configurationProject) {
		this.configurationProject = configurationProject;
		fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_SET, null, configurationProject));
	}

	public List<String> getVariantNames() {
		List<String> projectNames = new ArrayList<>();
		for (IProject project : this.projects) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

	public List<IProject> getVariants() {
		return projects;
	}

	public IProject getVariant(String name) {
		for (IProject project : this.projects) {
			if (project.getName().equals(name))
				return project;
		}
		return null;
	}

	public void setVariants(List<IProject> projects) {
		this.projects = projects;
	}

	public void addVariant(IProject project) {
		this.projects.add(project);
	}

	public Configuration getConfigurationForVariant(IProject project) {
		if (project != null) {
			for (Path confPath : configurationProject.getAllConfigurations()) {
				IFile configPath = (IFile) EclipseFileSystem.getResource(confPath);
				String configFileName = configPath.getName();
				String configName = configFileName.substring(0, configFileName.lastIndexOf('.'));
				System.out.println("[ConfigurationProject.getConfigurationForVariant] Check name equality Project("
						+ project.getName() + ") with Config(" + configName + ")");
				if (configName.equals(project.getName())) {
					ConfigurationManager configurationManager = ConfigurationManager
							.getInstance(Paths.get(configPath.getRawLocationURI()));
					if (configurationManager != null) {
						return configurationManager.getObject();
					}
				}
			}
		}
		return null;
	}

	public Iterable<IFeature> getFeatures() {
		return configurationProject.getFeatureModel().getFeatures();
	}

	@Override
	public void reset() {
		projects.clear();
	}

	public FeatureContextManager getFeatureContextManager() {
		return featureContextManager;
	}

	public MappingManager getMappingManager() {
		return mappingManager;
	}

	public PatchesManager getPatchesManager() {
		return patchesManager;
	}

	@Override
	public void save() {
		featureContextManager.save();
		mappingManager.save();
		patchesManager.save();
	}

	@Override
	public void load() {
		featureContextManager.load();
		mappingManager.load();
		patchesManager.load();
	}

}
