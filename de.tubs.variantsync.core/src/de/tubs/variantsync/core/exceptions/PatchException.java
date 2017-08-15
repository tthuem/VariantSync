package de.tubs.variantsync.core.exceptions;

import org.eclipse.core.resources.IResource;

public class PatchException extends Exception {
	private static final long serialVersionUID = 1L;
	
	IResource resource = null;
	
	public PatchException() {
		super("The patcher has encountered an error while calculating the differences between two versions of a file.");
	}
	
	public PatchException(String message) {
		super(message);
	}
	
	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return getMessage() + this.resource!=null?this.resource.getFullPath().toOSString():"";
	}
	
}
