package de.ovgu.variantsync.applicationlayer.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.presentationlayer.controller.ControllerProperties;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class SynchronizationProvider extends AbstractModel implements
		ISynchronizationOperations {

	private ProjectSynchronization projectSynchronization;
	private TargetCalculation targetCalculation;

	public SynchronizationProvider() {
		super();
		this.projectSynchronization = new ProjectSynchronization();
		this.targetCalculation = new TargetCalculation();
	}

	@Override
	public List<IProject> getProjectList(ResourceChangesFilePatch patch) {
		List<IProject> projects = targetCalculation.getProjectList(patch);
		propertyChangeSupport.firePropertyChange(
				ControllerProperties.PROJECTLIST_PROPERTY.getProperty(), null,
				projects);
		return projects;
	}

	@Override
	public List<String> getProjectNames(ResourceChangesFilePatch patch) {
		List<IProject> projects = targetCalculation.getProjectList(patch);
		List<String> projectsNames = new ArrayList<String>();
		for (IProject project : projects) {
			projectsNames.add(project.getName());
		}
		propertyChangeSupport.firePropertyChange(
				ControllerProperties.PROJECTNAMES_PROPERTY.getProperty(), null,
				projectsNames);
		return projectsNames;
	}

	@Override
	public List<String> getSynchronizedProjects(ResourceChangesFilePatch patch) {
		List<String> projectNames = targetCalculation
				.getSynchronizedProjects(patch);
		propertyChangeSupport.firePropertyChange(
				ControllerProperties.SYNCHRONIZEDPROJECTS_PROPERTY
						.getProperty(), null, projectNames);
		return projectNames;
	}

	@Override
	public void synchronize(Object[] result, ResourceChangesFilePatch patch) {
		projectSynchronization.synchronize(result, patch);
	}

	@Override
	public IProject getProjectByName(String projectName) {
		List<IProject> projectList = VariantSyncPlugin.getDefault()
				.getSupportProjectList();
		for (IProject project : projectList) {
			if (project.getName().equals(projectName)) {
				return project;
			}
		}
		return null;
	}
}