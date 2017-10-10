package de.tubs.variantsync.core.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class SourceFile {

	private IFile resource;
	List<CodeMapping> mappings = new ArrayList<>();
	
	public SourceFile(IFile res) {
		this.resource = res;
	}
	
	public SourceFile(IFile res, List<CodeMapping> mappings) {
		this.resource = res;
		this.mappings = mappings;
	}

	public IFile getFile() {
		return resource;
	}
	
	public void setFile(IFile res) {
		this.resource = res;
	}

	public List<CodeMapping> getMappings() {
		return mappings;
	}
	
	public void addMapping(CodeMapping mapping) {
		this.mappings.add(mapping);
	}
	
	public void setMapping(List<CodeMapping> mappings) {
		this.mappings = mappings;
	}
	
}
