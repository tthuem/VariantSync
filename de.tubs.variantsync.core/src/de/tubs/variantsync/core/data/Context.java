package de.tubs.variantsync.core.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.tubs.variantsync.core.data.interfaces.IContext;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException.Type;
import de.tubs.variantsync.core.markers.MarkerHandler;

public class Context implements IContext {

	public static final String DEFAULT_CONTEXT_NAME = "Default";

	private String actualContext = DEFAULT_CONTEXT_NAME;

	private IFeatureProject configurationProject = null;

	private List<IProject> projectList = new ArrayList<>();

	private List<FeatureExpression> featureExpressions = new ArrayList<>();

	private boolean isActive;

	public String getActualContext() {
		return actualContext;
	}

	public void setActualContext(String actualContext) {
		this.actualContext = actualContext;
	}

	public void setDefaultContext() {
		this.actualContext = DEFAULT_CONTEXT_NAME;
	}

	public IFeatureProject getConfigurationProject() {
		return configurationProject != null ? configurationProject.getProject().exists() ? configurationProject : null : null;
	}

	public void setConfigurationProject(IFeatureProject configurationProject) {
		this.configurationProject = configurationProject;
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
		this.featureExpressions.add(new FeatureExpression(featureExpression, FeatureColor.Yellow));
	}

	public void addFeatureExpression(String featureExpression, FeatureColor color) {
		this.featureExpressions.add(new FeatureExpression(featureExpression, color));
	}

	public Iterable<IFeature> getFeatures() {
		return configurationProject.getFeatureModel().getFeatures();
	}

	@Override
	public void importFeaturesFromModel() throws ProjectNotFoundException {
		if (configurationProject != null) {
			for (IFeature feature : getFeatures()) {
				featureExpressions.add(new FeatureExpression(feature.getName()));
			}
		} else
			throw new ProjectNotFoundException(Type.CONFIGURATION);
	}

	@Override
	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public void setActive(boolean status) {
		this.isActive = status;
	}

	@Override
	public void reset() {
		actualContext = DEFAULT_CONTEXT_NAME;
		configurationProject = null;
		projectList.clear();
		featureExpressions.clear();
		isActive = false;
	}

	@Override
	public boolean isDefaultContextActive() {
		return getActualContext().equals(DEFAULT_CONTEXT_NAME);
	}

}
