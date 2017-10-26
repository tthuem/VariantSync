package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

/**
 * This abstract class defines all fundamental methods of deltas.<br><b>Classes extending this interface should implement also {@link #clone()} and
 * {@link #equals(Object)}<b>
 * 
 * @author Christopher Sontag
 * @version 1.0
 * @since 05.09.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public abstract class ADelta<T> implements IDelta<T> {

	protected IFile resource;
	protected T original;
	protected T revised;
	protected IDelta.DELTATYPE type;
	protected List<IProject> syncronizedProjects = new ArrayList<>();
	protected long timestamp;
	protected HashMap<String, String> properties = new HashMap<>();
	protected IPatch<?> parent = null;
	protected String feature = "";
	protected IProject project = null;
	protected String factoryId = "";

	public ADelta(IFile res, String factoryId) {
		this(res, System.currentTimeMillis(), factoryId);
	}

	public ADelta(IFile res, long timestamp, String factoryId) {
		this.resource = res;
		if (res != null && res.getLocalTimeStamp() != IResource.NULL_STAMP) {
			timestamp = res.getLocalTimeStamp();
		}
		this.timestamp = timestamp;
		this.project = res != null ? res.getProject() : null;
		this.factoryId = factoryId;
	}

	/**
	 * Creates a new instance with the same field for field attributes
	 * 
	 * @param delta - old delta
	 */
	public ADelta(ADelta<T> delta) {
		this.resource = delta.resource;
		this.timestamp = delta.timestamp;
		this.factoryId = delta.factoryId;
		this.feature = delta.feature;
		this.original = delta.original;
		this.revised = delta.revised;
		this.parent = delta.parent;
		this.project = delta.project;
		this.syncronizedProjects = delta.syncronizedProjects;
		this.type = delta.type;
	}

	@Override
	public DELTATYPE getType() {
		return type;
	}

	@Override
	public void setType(DELTATYPE type) {
		this.type = type;
	}

	@Override
	public IFile getResource() {
		return this.resource;
	}

	@Override
	public boolean isSynchronizedProject(IProject project) {
		return syncronizedProjects.contains(project);
	}

	@Override
	public boolean isSynchronizedProject(String projectName) {
		for (IProject project : syncronizedProjects) {
			if (project.getName().equals(projectName)) return true;
		}
		return false;
	}

	@Override
	public void addSynchronizedProject(IProject project) {
		syncronizedProjects.add(project);
	}

	@Override
	public List<IProject> getSynchronizedProjects() {
		return syncronizedProjects;
	}

	@Override
	public void setSynchronizedProjects(List<IProject> projects) {
		this.syncronizedProjects = projects;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public T getOriginal() {
		return original;
	}

	@Override
	public void setOriginal(T original) {
		this.original = original;
	}

	@Override
	public T getRevised() {
		return revised;
	}

	@Override
	public void setRevised(T revised) {
		this.revised = revised;
	}

	@Override
	public String getProperty(String key) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		return null;
	}

	@Override
	public HashMap<String, String> getProperties() {
		return properties;
	}

	@Override
	public void addProperty(String key, String obj) {
		properties.put(key, obj);
	}

	@Override
	public String getFeature() {
		return feature;
	}

	@Override
	public void setFeature(String feature) {
		this.feature = feature;
	}

	@Override
	public IPatch<?> getPatch() {
		return parent;
	}

	@Override
	public void setPatch(IPatch<?> patch) {
		this.parent = patch;
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	@Override
	public String getFactoryId() {
		return factoryId;
	}

	@Override
	public abstract String getRepresentation();

	@Override
	public abstract String getOriginalAsString();

	@Override
	public abstract void setOriginalFromString(String original);

	@Override
	public abstract String getRevisedAsString();

	@Override
	public abstract void setRevisedFromString(String revised);

	@Override
	public String toString() {
		return String.format("ADelta [resource=%s, revised=%s, type=%s, timestamp=%s, feature=%s, factoryId=%s]", resource, revised, type, timestamp, feature,
				factoryId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ADelta<?> other = (ADelta<?>) obj;
		if (factoryId == null) {
			if (other.factoryId != null) return false;
		} else if (!factoryId.equals(other.factoryId)) return false;
		if (feature == null) {
			if (other.feature != null) return false;
		} else if (!feature.equals(other.feature)) return false;
		if (original == null) {
			if (other.original != null) return false;
		} else if (!original.equals(other.original)) return false;
		if (parent == null) {
			if (other.parent != null) return false;
		} //else if (!parent.equals(other.parent)) return false;
		if (project == null) {
			if (other.project != null) return false;
		} else if (!project.equals(other.project)) return false;
		if (properties == null) {
			if (other.properties != null) return false;
		} else if (!properties.equals(other.properties)) return false;
		if (resource == null) {
			if (other.resource != null) return false;
		} else if (!resource.equals(other.resource)) return false;
		if (revised == null) {
			if (other.revised != null) return false;
		} else if (!revised.equals(other.revised)) return false;
		if (syncronizedProjects == null) {
			if (other.syncronizedProjects != null) return false;
		} else if (!syncronizedProjects.equals(other.syncronizedProjects)) return false;
		if (timestamp != other.timestamp) return false;
		if (type != other.type) return false;
		return true;
	}

}
