package de.tubs.variantsync.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException.Type;
import de.tubs.variantsync.core.markers.MarkerHandler;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.persistence.Persistence;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.VariantSyncEvent.EventType;
import guidsl.pattern;

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

	public List<IProject> getProjects() {
		return projectList;
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

	public List<FeatureExpression> getFeatureExpressions() {
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
	}

	public boolean isDefaultContextSelected() {
		return getActualContext().equals(DEFAULT_CONTEXT_NAME);
	}

	public IPatch<?> getActualContextPatch() {
		if (actualPatch == null) return null;
		if (!actualPatch.getFeature().equals(getActualContext())) {
			patches.add(actualPatch);
			actualPatch = null;
			return null;
		}
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

	public List<CodeLine> getMapping(IFile file) {
		if (codeMappings.containsKey(file.getProject())) for (SourceFile sf : codeMappings.get(file.getProject())) {
			if (sf.getResource().getFullPath().equals(file.getFullPath())) {
				return sf.getCodeLines();
			}
		}
		return null;
	}

	public FeatureExpression getFeatureExpression(String name) {
		for (FeatureExpression fe : getFeatureExpressions()) {
			if (fe.name.equals(name)) {
				return fe;
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
		for (SourceFile sf : codeMappings.get(file.getProject())) {
			if (sf.getResource().getFullPath().equals(file.getFullPath())) {
				sf = sourceFile;
			}
		}
	}
	
	public void closeActualPatch() {
		if (actualPatch != null) {
			actualPatch.setEndTime(System.currentTimeMillis());
			if (!actualPatch.isEmpty()) patches.add(actualPatch);
			fireEvent(new VariantSyncEvent(this, EventType.PATCH_CLOSED, actualPatch, null));
			actualPatch = null;
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
		case FEATUREEXPRESSION_ADDED:
			break;
		case PATCH_ADDED:
			break;
		case PATCH_CHANGED:
			break;
		case PATCH_CLOSED:
			break;
		default:
			break;
		}
	}
}
