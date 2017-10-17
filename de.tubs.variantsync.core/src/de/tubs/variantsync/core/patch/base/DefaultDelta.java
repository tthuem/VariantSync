package de.tubs.variantsync.core.patch.base;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.tubs.variantsync.core.patch.ADelta;
import difflib.Chunk;

@SuppressWarnings("rawtypes")
public class DefaultDelta extends ADelta<Chunk> {

	public DefaultDelta(IFile res, String factoryId) {
		super(res, factoryId);
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

}
