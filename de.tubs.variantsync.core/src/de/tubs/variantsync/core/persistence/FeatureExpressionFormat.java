package de.tubs.variantsync.core.persistence;

import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.AXMLFormat;
import de.tubs.variantsync.core.data.FeatureExpression;

public class FeatureExpressionFormat extends AXMLFormat<List<FeatureExpression>> {

	private static final String ID = "FeatureExpressions";
	private static final String FEATURE_EXPRESSIONS = "featureExpressions";
	private static final String FEATURE_EXPRESSION = "featureExpression";
	private static final Pattern CONTENT_REGEX = Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<"+FEATURE_EXPRESSIONS+"[\\s>]");
	
	public static final String FILENAME = ".featureExpressions.xml";

	@Override
	public IPersistentFormat<List<FeatureExpression>> getInstance() {
		return new FeatureExpressionFormat();
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
			FeatureExpression fe = new FeatureExpression(e.getAttribute("name"), FeatureColor.getColor(e.getAttribute("highlighter")));
			object.add(fe);
		}
		
	}

	@Override
	protected void writeDocument(Document doc) {
		final Element root = doc.createElement(FEATURE_EXPRESSIONS);
		
		Element e;
		for (FeatureExpression fe : object) {
			e = doc.createElement(FEATURE_EXPRESSION);
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

}
