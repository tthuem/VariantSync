package de.tubs.variantsync.core.monitor;

import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * Manages the changes within projects
 *
 * @author Christopher Sontag
 * @version 1.0
 * @since 24.08.2017
 */
public class ProjectHistoryManager {

	private static final String FILE_NAME = ".variantHistory";

	public static HashMap<String, ProjectHistoryManager> INSTANCES = new HashMap<>();
	
	private IProject project;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	
	public ProjectHistoryManager(IProject project) {
		this.project = project;
		try {
			JAXBContext jaxbInstance = JAXBContext.newInstance(ProjectHistoryManager.class);
			marshaller = jaxbInstance.createMarshaller();
			unmarshaller = jaxbInstance.createUnmarshaller();
		} catch (JAXBException e) {
			LogOperations.logError("JAXBContext failed", e);
		}
	}

	public static ProjectHistoryManager getInstance(IProject project) {
		if (!INSTANCES.containsKey(project.getName()))
			INSTANCES.put(project.getName(), new ProjectHistoryManager(project));
		return INSTANCES.get(project.getName());
	}

	public void createIfNotExists() throws CoreException {
		IFile file = project.getFile(FILE_NAME);
		if (!file.exists())
			file.create(null, true, null);
	}
	
	public void read() {
		IFile file = project.getFile(FILE_NAME);
		
	}
}