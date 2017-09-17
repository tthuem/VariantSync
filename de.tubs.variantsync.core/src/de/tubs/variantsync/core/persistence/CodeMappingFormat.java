package de.tubs.variantsync.core.persistence;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.AXMLFormat;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.CodeLine;
import de.tubs.variantsync.core.data.SourceFile;

public class CodeMappingFormat extends AXMLFormat<List<SourceFile>> {

	private static final String ID = "CodeMapping";
	private static final String MAPPINGS = "Mappings";
	private static final String SOURCEFILE = "SourceFile";
	private static final String CODELINE = "CodeLine";
	private static final Pattern CONTENT_REGEX = Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<"+MAPPINGS+"[\\s>]");
	
	public static final String FILENAME = ".mapping.xml";
	
	public IProject project;

	public CodeMappingFormat(IProject project) {
		this.project = project;
	}

	@Override
	public IPersistentFormat<List<SourceFile>> getInstance() {
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
			SourceFile sf = new SourceFile();
			sf.setResource(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(eSF.getAttribute("path"))));
			for (final Element eCL : getElements(eSF.getChildNodes())) {
				CodeLine cl = new CodeLine();
				cl.setCode(eCL.getAttribute("code"));
				cl.setFeatureExpression(VariantSyncPlugin.getDefault().getContext(project).getFeatureExpression(eCL.getAttribute("feature")));
				cl.setLine(Integer.parseInt(eCL.getAttribute("line")));
			}
			object.add(sf);
		}
		
	}

	@Override
	protected void writeDocument(Document doc) {
		final Element root = doc.createElement(MAPPINGS);
		
		for (SourceFile sf : object) {
			Element file = doc.createElement(SOURCEFILE);
			file.setAttribute("path", String.valueOf(sf.getResource().getFullPath()));
			
			for (CodeLine cl : sf.getCodeLines()) {
				Element line = doc.createElement(CODELINE);
				line.setAttribute("feature", cl.getFeatureExpression().name);
				line.setAttribute("line", String.valueOf(cl.getLine()));
				line.setAttribute("code", cl.getCode());
			}
			
			root.appendChild(file);
		}

		doc.appendChild(root);
	}

	@Override
	public boolean supportsContent(CharSequence content) {
		return supportsRead() && CONTENT_REGEX.matcher(content).find();
	}

}
