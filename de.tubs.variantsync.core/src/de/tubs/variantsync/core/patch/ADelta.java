package de.tubs.variantsync.core.patch;

import java.util.List;

import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.data.CodeLine;
import de.tubs.variantsync.core.patch.interfaces.IDelta;

public class ADelta<T> implements IDelta<T> {

	private IResource resource;
	private List<CodeLine> original;
	private List<CodeLine> revised;
	private IDelta.TYPE type;
	private boolean isSynchronized = false;
	private long timestamp;
	private T delta;
	
	public ADelta(IResource res) {
		this.resource = res;
		this.timestamp = res.getLocalTimeStamp()!=IResource.NULL_STAMP?res.getLocalTimeStamp():System.currentTimeMillis();
	}

	@Override
	public TYPE getType() {
		return type;
	}

	@Override
	public void setType(TYPE type) {
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
	public List<CodeLine> getOriginal() {
		return original;
	}

	@Override
	public void setOriginal(List<CodeLine> original) {
		this.original = original;
	}

	@Override
	public void setOriginalFromLines(List<String> original) {
		//TODO: Implement
	}

	@Override
	public List<CodeLine> getRevised() {
		return revised;
	}



	@Override
	public void setRevised(List<CodeLine> revised) {
		this.revised = revised;
	}



	@Override
	public void setRevisedFromLines(List<String> revised) {
		//TODO: Implement
	}



	@Override
	public T getDelta() {
		return delta;
	}



	@Override
	public void setDelta(T delta) {
		this.delta = delta;
	}



	@Override
	public List<CodeLine> getCodeLines() {
		//TODO: Implement
		return null;
	}
	
}
