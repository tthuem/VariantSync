package de.tubs.variantsync.core.data;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.io.manager.ConfigurationManager;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException.Type;
import de.tubs.variantsync.core.markers.MarkerHandler;
import de.tubs.variantsync.core.markers.MarkerUpdateJob;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;

/**
 * 
 * A class for managing all informations about the product line for one configuration project
 * 
 * @author Christopher Sontag
 * @since 1.1
 * @TODO When switching between projects from different configuration projects the context should switch and the record should stop
 */
public class Context implements IEventListener {

	public static final String DEFAULT_CONTEXT_NAME = "Default";

	private String actualContext = DEFAULT_CONTEXT_NAME;

	private IFeatureProject configurationProject = null;

	private List<IProject> projectList = new ArrayList<>();

	private List<FeatureExpression> featureExpressions = new ArrayList<>();

	private IPatch<?> actualPatch = null;

	private List<IPatch<?>> patches = new ArrayList<IPatch<?>>();

	private HashMap<IProject, List<SourceFile>> codeMappings = new HashMap<>();

	public String getActualContext() {
		return actualContext;
	}

	public void setActualContext(String actualContext) {
		String oldContext = this.actualContext;
		this.actualContext = actualContext;
		fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_CHANGED, oldContext, actualContext));
	}

	public void setDefaultContext() {
		String oldContext = this.actualContext;
		this.actualContext = DEFAULT_CONTEXT_NAME;
		fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_CHANGED, oldContext, actualContext));
	}

	public IFeatureProject getConfigurationProject() {
		return configurationProject != null ? configurationProject.getProject().exists() ? configurationProject : null : null;
	}

	public void setConfigurationProject(IFeatureProject configurationProject) {
		this.configurationProject = configurationProject;
		fireEvent(new VariantSyncEvent(this, EventType.CONFIGURATIONPROJECT_SET, null, configurationProject));
	}

	public List<String> getProjectNames() {
		List<String> projectNames = new ArrayList<>();
		for (IProject project : this.projectList) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

	public List<IProject> getProjects() {
		return projectList;
	}

	public IProject getProject(String name) {
		for (IProject project : this.projectList) {
			if (project.getName().equals(name)) return project;
		}
		return null;
	}

	public void setProjects(List<IProject> projects) {
		try {
			MarkerHandler.getInstance().cleanProjects(projects);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		this.projectList = projects;
	}

	public void addProject(IProject project) {
		try {
			MarkerHandler.getInstance().cleanProject(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		this.projectList.add(project);
	}

	public Configuration getConfigurationForProject(IProject project) {
		for (IFile config : configurationProject.getAllConfigurations()) {
			if (project != null) {
				if (config.getName().replace("." + config.getFileExtension(), "").equals(project.getName())) {
					Configuration c = new Configuration(getConfigurationProject().getFeatureModel());
					ConfigurationManager configurationManager = ConfigurationManager.getInstance(Paths.get(config.getLocationURI()), c);
					if (configurationManager != null) return configurationManager.getObject();
				}
			}
		}
		return null;
	}

	public List<FeatureExpression> getFeatureExpressions() {
		return featureExpressions;
	}

	public List<String> getFeatureExpressionsAsStrings() {
		List<String> featureExpressions = new ArrayList<>();
		for (FeatureExpression fe : this.featureExpressions) {
			featureExpressions.add(fe.name);
		}
		return featureExpressions;
	}

	public void setFeatureExpressions(List<FeatureExpression> featureExpressions) {
		this.featureExpressions = featureExpressions;
	}

	public void addFeatureExpression(String featureExpression) {
		FeatureExpression fe = new FeatureExpression(featureExpression, FeatureColor.Yellow);
		this.featureExpressions.add(fe);
		fireEvent(new VariantSyncEvent(this, EventType.FEATUREEXPRESSION_ADDED, null, fe));
	}

	public void addFeatureExpression(String featureExpression, FeatureColor color) {
		FeatureExpression fe = new FeatureExpression(featureExpression, color);
		this.featureExpressions.add(fe);
		fireEvent(new VariantSyncEvent(this, EventType.FEATUREEXPRESSION_ADDED, null, fe));
	}

	public Iterable<IFeature> getFeatures() {
		return configurationProject.getFeatureModel().getFeatures();
	}

	public void importFeaturesFromModel() throws ProjectNotFoundException {
		if (configurationProject != null) {
			for (IFeature feature : getFeatures()) {
				FeatureExpression fe = new FeatureExpression(feature.getName());
				if (!featureExpressions.contains(fe)) featureExpressions.add(fe);
			}
		} else throw new ProjectNotFoundException(Type.CONFIGURATION);
	}

	public void reset() {
		projectList.clear();
		featureExpressions.clear();
		codeMappings.clear();
		patches.clear();
	}

	public boolean isDefaultContextSelected() {
		return getActualContext().equals(DEFAULT_CONTEXT_NAME);
	}

	public IPatch<?> getActualContextPatch() {
		if (actualPatch == null) return null;
		return this.actualPatch;
	}

	public void setActualContextPatch(IPatch<?> patch) {
		this.actualPatch = patch;
	}

	public List<IPatch<?>> getPatches() {
		return patches;
	}

	public void addPatch(IPatch<?> patch) {
		patches.add(patch);
	}

	public void setPatches(List<IPatch<?>> patches) {
		this.patches = patches;
	}

	public FeatureExpression getFeatureExpression(String name) {
		for (FeatureExpression fe : getFeatureExpressions()) {
			if (fe.name.equals(name)) {
				return fe;
			}
		}
		return null;
	}

	public SourceFile getMapping(IFile file) {
		if (file != null && codeMappings.containsKey(file.getProject())) {
			for (SourceFile sourceFile : codeMappings.get(file.getProject())) {
				if (sourceFile.getFile().getFullPath().equals(file.getFullPath())) {
					return sourceFile;
				}
			}
		}
		return null;
	}

	public HashMap<IProject, List<SourceFile>> getCodeMappings() {
		return codeMappings;
	}

	public void setCodeMappings(HashMap<IProject, List<SourceFile>> codeMappings) {
		this.codeMappings = codeMappings;
	}

	public void addCodeMapping(IProject project, List<SourceFile> files) {
		this.codeMappings.put(project, files);
	}

	public void addCodeMapping(IFile file, SourceFile sourceFile) {
		List<SourceFile> sourceFiles = codeMappings.get(file.getProject());
		for (SourceFile sf : sourceFiles) {
			if (sf.getFile().getFullPath().equals(file.getFullPath())) {
				sf = sourceFile;
				return;
			}
		}
		sourceFiles.add(sourceFile);
	}

	public void closeActualPatch() {
		if (actualPatch != null) {
			actualPatch.setEndTime(System.currentTimeMillis());
			if (!actualPatch.isEmpty() && !patches.contains(actualPatch)) patches.add(actualPatch);
			actualPatch = null;
			fireEvent(new VariantSyncEvent(this, EventType.PATCH_CLOSED, null, null));
		}
	}

	/**
	 * Wrapper for {@link VariantSyncPlugin#fireEvent(VariantSyncEvent)}
	 * 
	 * @param variantSyncEvent
	 */
	private void fireEvent(VariantSyncEvent variantSyncEvent) {
		VariantSyncPlugin.getDefault().fireEvent(variantSyncEvent);
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_SET:
			break;
		case CONTEXT_CHANGED:
			closeActualPatch();
			break;
		case CONTEXT_RECORDING_START:
			break;
		case CONTEXT_RECORDING_STOP:
			closeActualPatch();
			break;
		case PATCH_ADDED:
			break;
		case PATCH_CHANGED:
			break;
		case PATCH_CLOSED:
			break;
		case CONFIGURATIONPROJECT_CHANGED:
			break;
		case FEATUREEXPRESSION_ADDED:
		case FEATUREEXPRESSION_CHANGED:
		case FEATUREEXPRESSION_REMOVED:
			MarkerUpdateJob markerUpdateJob = new MarkerUpdateJob(VariantSyncPlugin.getDefault().getEditorInput());
			markerUpdateJob.schedule();
			break;
		default:
			break;
		}
	}
}
