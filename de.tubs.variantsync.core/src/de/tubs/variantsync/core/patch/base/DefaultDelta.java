package de.tubs.variantsync.core.patch.base;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.github.difflib.patch.Chunk;

import de.tubs.variantsync.core.patch.ADelta;

public class DefaultDelta extends ADelta<Chunk<String>> {

	public DefaultDelta(IFile res, String factoryId) {
		super(res, factoryId);
	}

	public DefaultDelta(IFile res, long timestamp, String factoryId) {
		super(res, timestamp, factoryId);
	}

	@Override
	public String getOriginalAsString() {
		String ret = String.valueOf(original.getPosition());
		ret = ret + ":;:";

		for (String line : (List<String>) original.getLines()) {
			ret = ret + line + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));
		ret = ret + ":;:";

		for (String bLine : original.getBefore()) {
			ret = ret + bLine + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));
		ret = ret + ":;:";

		for (String aLine : original.getAfter()) {
			ret = ret + aLine + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));

		return ret;
	}

	@Override
	public void setOriginalFromString(String original) {
		List<String> elements = Arrays.asList(original.split(":;:"));
		int pos = Integer.valueOf(elements.get(0));
		List<String> lines = Arrays.asList(elements.get(1).split("#:#"));
		List<String> before = Arrays.asList(elements.get(2).split("#:#"));
		List<String> after = Arrays.asList(elements.get(3).split("#:#"));
		this.original = new Chunk<String>(pos, lines);
		this.original.setBefore(before);
		this.original.setAfter(after);
	}

	@Override
	public String getRevisedAsString() {
		String ret = String.valueOf(revised.getPosition());
		ret = ret + ":;:";

		for (String line : (List<String>) revised.getLines()) {
			ret = ret + line + "#:#";
		}
		if (!((List<String>) revised.getLines()).isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}
		ret = ret + ":;:";

		for (String bLine : revised.getBefore()) {
			ret = ret + bLine + "#:#";
		}
		if (!((List<String>) revised.getBefore()).isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}
		ret = ret + ":;:";

		for (String aLine : revised.getAfter()) {
			ret = ret + aLine + "#:#";
		}
		if (!((List<String>) revised.getAfter()).isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}

		return ret;
	}

	@Override
	public void setRevisedFromString(String revised) {
		List<String> elements = Arrays.asList(revised.split(":;:"));
		int pos = Integer.valueOf(elements.get(0));
		List<String> lines = Arrays.asList(elements.get(1).split("#:#"));
		List<String> before = Arrays.asList(elements.get(2).split("#:#"));
		List<String> after = Arrays.asList(elements.get(3).split("#:#"));
		this.original = new Chunk<String>(pos, lines);
		this.original.setBefore(before);
		this.original.setAfter(after);
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
