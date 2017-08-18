package de.tubs.variantsync.core.data.interfaces;

import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.IEventManager;

public interface IContext extends IEventManager {
	
	public String getActualContext();

	public void setActualContext(String actualContext);
	
	public void setDefaultContext();
	
	public boolean isDefaultContextSelected();
	
	public IFeatureProject getConfigurationProject();
	
	public void setConfigurationProject(IFeatureProject configurationProject);

	public List<IProject> getProjects();

	public void setProjects(List<IProject> projects);
	
	public void addProject(IProject project);

	public List<FeatureExpression> getFeatureExpressions();

	public void setFeatureExpressions(List<FeatureExpression> featureExpressions);

	public void addFeatureExpression(String featureExpression);
	
	public void addFeatureExpression(String featureExpression, FeatureColor color);

	public void importFeaturesFromModel() throws ProjectNotFoundException;

	public boolean isActive();
	
	public void setActive(boolean status);
	
	public void reset();
	
	public IPatch<?> getActualContextPatch();
	
	public void setActualContextPatch(IPatch<?> patch);
}
