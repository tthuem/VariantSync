package de.ovgu.variantsync.applicationlayer.synchronization;

import java.util.List;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;

/**
 * Defines functions to synchronize projects.
 * 
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.05.2015
 */
public interface ISynchronizationOperations {

	/**
	 * Computes possible targets for synchronization and sends them to all
	 * registered listeners. Identifies projects which would cause a conflict by
	 * subtracting conflict project list from supported project list.
	 * 
	 * @param patch
	 *            changed file
	 * @return list with compatible projects
	 */
	List<IProject> getProjectList(ResourceChangesFilePatch patch);

	/**
	 * Returns all monitored projects containing given changed file and sends
	 * them to all registered listeners.
	 * 
	 * @param patch
	 *            changed file
	 * @return list with project names
	 */
	List<String> getSynchronizedProjects(ResourceChangesFilePatch patch);

	/**
	 * Synchronizes projects with patch.
	 * 
	 * @param result
	 *            target projects for synchronization
	 * @param patch
	 *            patch to synchronize
	 */
	void synchronize(Object[] result, ResourceChangesFilePatch patch);

	/**
	 * Computes possible targets for synchronization and sends project names to
	 * all registered listeners. Identifies projects which would cause a
	 * conflict by subtracting conflict project list from supported project
	 * list.
	 * 
	 * @param patch
	 *            changed file
	 * @return list with names of compatible projects
	 */
	List<String> getProjectNames(ResourceChangesFilePatch patch);

	/**
	 * Returns project of IProject-type retrieved on given project name if the
	 * specified project exists.
	 * 
	 * @param projectName
	 *            project to search
	 * @return IProject-instance
	 */
	IProject getProjectByName(String projectName);
}
