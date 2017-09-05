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
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException.Type;
import de.tubs.variantsync.core.markers.MarkerHandler;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.persistence.Persistence;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.VariantSyncEvent.EventType;
import guidsl.pattern;

public class Context {

	public static final String DEFAULT_CONTEXT_NAME = "Default";

	private String actualContext = DEFAULT_CONTEXT_NAME;

	private IFeatureProject configurationProject = null;

	private List<IProject> projectList = new ArrayList<>();

	private List<FeatureExpression> featureExpressions = new ArrayList<>();

	private boolean isActive;

	private IPatch<?> actualPatch = null;

	private List<IPatch<?>> patches = new ArrayList<IPatch<?>>();

	private List<IEventListener> listeners = new ArrayList<>();

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
		return configurationProject != null ? configurationProject.getProject().exists() ? configurationProject : null
				: null;
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
				if (!featureExpressions.contains(fe))
					featureExpressions.add(fe);
			}
		} else
			throw new ProjectNotFoundException(Type.CONFIGURATION);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean status) {
		this.isActive = status;
		if (isActive) {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_START, null, getActualContext()));
		} else {
			fireEvent(new VariantSyncEvent(this, EventType.CONTEXT_RECORDING_STOP, getActualContext(), null));
		}
	}

	public void reset() {
		actualContext = DEFAULT_CONTEXT_NAME;
		configurationProject = null;
		projectList.clear();
		featureExpressions.clear();
		isActive = false;
	}

	public boolean isDefaultContextSelected() {
		return getActualContext().equals(DEFAULT_CONTEXT_NAME);
	}

	public IPatch<?> getActualContextPatch() {
		return this.actualPatch;
	}

	public void setActualContextPatch(IPatch<?> patch) {
		this.actualPatch = patch;
	}

	public void addListener(IEventListener listener) {
		this.listeners.add(listener);

	}

	public void fireEvent(VariantSyncEvent event) {
		System.out.println(event);
		for (IEventListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	public void removeListener(IEventListener listener) {
		this.listeners.remove(listener);
	}

	public List<IPatch<?>> getPatches() {
		return patches;
	}

	public List<CodeLine> getMapping(IFile file) {
		if (codeMappings.containsKey(file.getProject()))
		for (SourceFile sf : codeMappings.get(file.getProject())) {
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

}
