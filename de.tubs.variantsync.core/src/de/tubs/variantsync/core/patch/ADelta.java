package de.tubs.variantsync.core.patch;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public abstract class ADelta<T> implements IDelta<T> {

	protected IResource resource;
	protected T original;
	protected T revised;
	protected IDelta.DELTATYPE type;
	protected boolean isSynchronized = false;
	protected long timestamp;
	protected HashMap<String, String> properties = new HashMap<>();
	protected IPatch<?> parent = null;
	protected String feature = "";
	private IProject project = null;
	private String factoryId = "";
	
	public ADelta(IResource res, String factoryId) {
		this(res, res.getLocalTimeStamp()!=IResource.NULL_STAMP?res.getLocalTimeStamp():System.currentTimeMillis(), factoryId);	
	}
	
	public ADelta(IResource res, long timestamp, String factoryId) {
		this.resource = res;
		this.timestamp = timestamp;
		this.project = res!=null?res.getProject():null;
		this.factoryId = factoryId;
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
	public IResource getResource() {
		return this.resource;
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
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
		return feature ;
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
		return project ;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	@Override
	public String getFactoryId() {
		return factoryId ;
	}

	@Override
	public abstract String getOriginalAsString();

	@Override
	public abstract void setOriginalFromString(String original);

	@Override
	public abstract String getRevisedAsString();

	@Override
	public abstract void setRevisedFromString(String revised);
	
}
