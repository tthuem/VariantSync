package de.tubs.variantsync.core.patch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.serial.SerialArray;

import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.data.CodeLine;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public class ADelta<T> implements IDelta<T> {

	private IResource resource;
	private T original;
	private T revised;
	private IDelta.DELTATYPE type;
	private boolean isSynchronized = false;
	private long timestamp;
	private HashMap<String, Object> properties = new HashMap<>();
	private IPatch<?> parent = null;
	private String feature = "";
	
	public ADelta(IResource res) {
		this.resource = res;
		this.timestamp = res.getLocalTimeStamp()!=IResource.NULL_STAMP?res.getLocalTimeStamp():System.currentTimeMillis();
	}
	
	public ADelta(IResource res, long timestamp) {
		this.resource = res;
		this.timestamp = timestamp;
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
	public Object getProperty(String key) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		return null;
	}

	@Override
	public void addProperty(String key, Serializable obj) {
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
	
}
