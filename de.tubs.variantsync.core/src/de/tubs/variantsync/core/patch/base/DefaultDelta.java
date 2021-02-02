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

		for (final String line : original.getLines()) {
			ret = ret + line + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));
		ret = ret + ":;:";

		for (final String bLine : original.getBefore()) {
			ret = ret + bLine + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));
		ret = ret + ":;:";

		for (final String aLine : original.getAfter()) {
			ret = ret + aLine + "#:#";
		}
		ret = ret.substring(0, ret.lastIndexOf("#:#"));

		return ret;
	}

	@Override
	public void setOriginalFromString(String original) {
		final List<String> elements = Arrays.asList(original.split(":;:"));
		final int pos = Integer.valueOf(elements.get(0));
		final List<String> lines = Arrays.asList(elements.get(1).split("#:#"));
		final List<String> before = Arrays.asList(elements.get(2).split("#:#"));
		final List<String> after = Arrays.asList(elements.get(3).split("#:#"));
		this.original = new Chunk<String>(pos, lines);
		this.original.setBefore(before);
		this.original.setAfter(after);
	}

	@Override
	public String getRevisedAsString() {
		String ret = String.valueOf(revised.getPosition());
		ret = ret + ":;:";

		for (final String line : revised.getLines()) {
			ret = ret + line + "#:#";
		}
		if (!revised.getLines().isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}
		ret = ret + ":;:";

		for (final String bLine : revised.getBefore()) {
			ret = ret + bLine + "#:#";
		}
		if (!revised.getBefore().isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}
		ret = ret + ":;:";

		for (final String aLine : revised.getAfter()) {
			ret = ret + aLine + "#:#";
		}
		if (!revised.getAfter().isEmpty()) {
			ret = ret.substring(0, ret.lastIndexOf("#:#"));
		}

		return ret;
	}

	@Override
	public void setRevisedFromString(String revised) {
		final List<String> elements = Arrays.asList(revised.split(":;:"));
		final int pos = Integer.valueOf(elements.get(0));
		final List<String> lines = Arrays.asList(elements.get(1).split("#:#"));
		final List<String> before = Arrays.asList(elements.get(2).split("#:#"));
		final List<String> after = Arrays.asList(elements.get(3).split("#:#"));
		this.revised = new Chunk<String>(pos, lines);
		this.revised.setBefore(before);
		this.revised.setAfter(after);
	}

	@Override
	public String getRepresentation() {
		return "--- (" + original.getPosition() + ") " + original.getLines() + "\n" + "+++ (" + revised.getPosition() + ") " + revised.getLines();
	}

	@Override
	protected Object clone() {
		final DefaultDelta defaultDelta = new DefaultDelta(resource, timestamp, factoryId);
		defaultDelta.setContext(context);
		defaultDelta.setOriginal(original);
		defaultDelta.setPatch(parent);
		defaultDelta.setProject(project);
		defaultDelta.setRevised(revised);
		defaultDelta.setSynchronizedProjects(syncronizedProjects);
		defaultDelta.setType(type);
		return defaultDelta;
	}

}
