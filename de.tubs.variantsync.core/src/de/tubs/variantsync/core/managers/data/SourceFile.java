package de.tubs.variantsync.core.managers.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class SourceFile {

	private IFile resource;
	List<CodeMapping> mappings = new ArrayList<>();

	public SourceFile(IFile res) {
		resource = res;
	}

	public SourceFile(IFile res, List<CodeMapping> mappings) {
		resource = res;
		this.mappings = mappings;
	}

	public IFile getFile() {
		return resource;
	}

	public void setFile(IFile res) {
		resource = res;
	}

	public List<CodeMapping> getMappings() {
		return mappings;
	}

	public void addMapping(CodeMapping mapping) {
		mappings.add(mapping);
	}

	public void setMapping(List<CodeMapping> mappings) {
		this.mappings = mappings;
	}

}
