package de.tubs.variantsync.core.patch.base;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.github.difflib.patch.Chunk;

import de.tubs.variantsync.core.patch.ADelta;

@SuppressWarnings("rawtypes")
public class DefaultDelta extends ADelta<Chunk> {

	public DefaultDelta(IFile res, String factoryId) {
		super(res, factoryId);
	}

	public DefaultDelta(IFile res, long timestamp, String factoryId) {
		super(res, timestamp, factoryId);
	}

	@Override
	public String getOriginalAsString() {
		String ret = String.valueOf(original.getPosition());
		for (String line : (List<String>) original.getLines()) {
			ret = ret + ":;:" + line;
		}
		return ret;
	}

	@Override
	public void setOriginalFromString(String original) {
		List<String> elements = Arrays.asList(original.split(":;:"));
		int pos = Integer.valueOf(elements.get(0));
		elements = elements.subList(1, elements.size());
		this.original = new Chunk<String>(pos, elements);
	}

	@Override
	public String getRevisedAsString() {
		String ret = String.valueOf(revised.getPosition());
		for (String line : (List<String>) revised.getLines()) {
			ret = ret + ":;:" + line;
		}
		return ret;
	}

	@Override
	public void setRevisedFromString(String revised) {
		List<String> elements = Arrays.asList(revised.split(":;:"));
		int pos = Integer.valueOf(elements.get(0));
		elements = elements.subList(1, elements.size());
		this.revised = new Chunk<String>(pos, elements);
	}

	@Override
	public String getRepresentation() {
		return "--- (" + this.original.getPosition() + ") " + this.original.getLines() + "\n" + "+++ (" + this.revised.getPosition() + ") "
			+ this.revised.getLines();
	}

	@Override
	protected Object clone() {
		DefaultDelta defaultDelta = new DefaultDelta(this.resource, this.timestamp, this.factoryId);
		defaultDelta.setFeature(this.feature);
		defaultDelta.setOriginal(this.original);
		defaultDelta.setPatch(this.parent);
		defaultDelta.setProject(this.project);
		defaultDelta.setRevised(this.revised);
		defaultDelta.setSynchronizedProjects(this.syncronizedProjects);
		defaultDelta.setType(this.type);
		return defaultDelta;
	}

}
