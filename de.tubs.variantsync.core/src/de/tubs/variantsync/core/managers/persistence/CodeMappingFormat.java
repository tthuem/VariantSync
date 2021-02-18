package de.tubs.variantsync.core.managers.persistence;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ovgu.featureide.fm.core.io.APersistentFormat;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.AXMLFormat;
import de.tubs.variantsync.core.managers.data.CodeMapping;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.utilities.AMarkerInformation;
import de.tubs.variantsync.core.utilities.IVariantSyncMarker;

public class CodeMappingFormat extends AXMLFormat<List<SourceFile>> {

	private static final String ID = "CodeMapping";
	private static final String MAPPINGS = "Mappings";
	private static final String SOURCEFILE = "SourceFile";
	private static final String CODEMAPPINGS = "CodeMapping";
	private static final Pattern CONTENT_REGEX = Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<" + MAPPINGS + "[\\s>]");

	public static final String FILENAME = ".mapping.xml";

	public IProject project;

	public CodeMappingFormat(IProject project) {
		this.project = project;
	}

	@Override
	public APersistentFormat<List<SourceFile>> getInstance() {
		return new CodeMappingFormat(null);
	}

	public IPersistentFormat<List<SourceFile>> getInstance(IProject project) {
		return new CodeMappingFormat(project);
	}

	@Override
	public boolean supportsRead() {
		return true;
	}

	@Override
	public boolean supportsWrite() {
		return project != null;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	protected void readDocument(Document doc, List<Problem> warnings) throws UnsupportedModelException {
		object.clear();
		for (final Element eSF : getElements(doc.getDocumentElement().getChildNodes())) {
			final SourceFile sourceFile = new SourceFile(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(eSF.getAttribute("path"))));
			for (final Element eCM : getElements(eSF.getChildNodes())) {
				final IVariantSyncMarker variantSyncMarker = new AMarkerInformation(Integer.parseInt(eCM.getAttribute("offset")),
						Integer.parseInt(eCM.getAttribute("length")), Boolean.parseBoolean(eCM.getAttribute("isLine")));
				variantSyncMarker.setContext(eCM.getAttribute("context"));
				final CodeMapping codeMapping = new CodeMapping(eCM.getTextContent(), variantSyncMarker);
				sourceFile.addMapping(codeMapping);
			}
			object.add(sourceFile);
		}
	}

	@Override
	protected void writeDocument(Document doc) {
		final Element root = doc.createElement(MAPPINGS);

		for (final SourceFile sf : object) {
			final Element file = doc.createElement(SOURCEFILE);
			file.setAttribute("path", String.valueOf(sf.getFile().getFullPath()));

			for (final CodeMapping cm : sf.getMappings()) {
				final Element line = doc.createElement(CODEMAPPINGS);
				line.setAttribute("context", cm.getMarkerInformation().getContext());
				line.setAttribute("offset", String.valueOf(cm.getMarkerInformation().getOffset()));
				line.setAttribute("length", String.valueOf(cm.getMarkerInformation().getLength()));
				line.setAttribute("isLine", String.valueOf(cm.getMarkerInformation().isLine()));
				line.setTextContent(cm.getCode());
				file.appendChild(line);
			}

			root.appendChild(file);
		}

		doc.appendChild(root);
	}

	@Override
	public boolean supportsContent(CharSequence content) {
		return supportsRead() && CONTENT_REGEX.matcher(content).find();
	}

	@Override
	public String getName() {
		return "CodeMapping";
	}

}
