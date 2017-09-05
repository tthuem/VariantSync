package de.tubs.variantsync.core.data;

import java.util.List;

import org.eclipse.core.resources.IResource;

public class SourceFile {

	private IResource resource;
	List<CodeLine> lines;

	public SourceFile() {
	}
	
	public SourceFile(IResource res) {
		this.resource = res;
	}
	
	public SourceFile(IResource res, List<CodeLine> lines) {
		this.resource = res;
		this.lines = lines;
	}

	public IResource getResource() {
		return resource;
	}

	public List<CodeLine> getCodeLines() {
		return lines;
	}

	public void setResource(IResource res) {
		this.resource = res;
	}

}
