package de.tubs.variantsync.core.managers.persistence;

import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.AXMLFormat;
import de.tubs.variantsync.core.managers.data.FeatureContext;

public class FeatureContextFormat extends AXMLFormat<List<FeatureContext>> {

	private static final String ID = "FeatureContexts";
	private static final String FEATURE_CONTEXTS = "contexts";
	private static final String FEATURE_CONTEXT = "context";
	private static final Pattern CONTENT_REGEX = Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<" + FEATURE_CONTEXTS + "[\\s>]");

	public static final String FILENAME = ".contexts.xml";

	@Override
	public IPersistentFormat<List<FeatureContext>> getInstance() {
		return new FeatureContextFormat();
	}

	@Override
	public boolean supportsRead() {
		return true;
	}

	@Override
	public boolean supportsWrite() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void readDocument(Document doc, List<Problem> warnings) throws UnsupportedModelException {
		object.clear();
		for (final Element e : getElements(doc.getDocumentElement().getChildNodes())) {
			FeatureContext fe = new FeatureContext(e.getAttribute("name"), FeatureColor.getColor(e.getAttribute("highlighter")));
			object.add(fe);
		}

	}

	@Override
	protected void writeDocument(Document doc) {
		final Element root = doc.createElement(FEATURE_CONTEXTS);

		Element e;
		for (FeatureContext fe : object) {
			e = doc.createElement(FEATURE_CONTEXT);
			e.setAttribute("name", fe.name);
			e.setAttribute("highlighter", fe.highlighter.getColorName());
			root.appendChild(e);
		}
		doc.appendChild(root);
	}

	@Override
	public boolean supportsContent(CharSequence content) {
		return supportsRead() && CONTENT_REGEX.matcher(content).find();
	}

	@Override
	public String getName() {
		return "FeatureContexts";
	}

}
