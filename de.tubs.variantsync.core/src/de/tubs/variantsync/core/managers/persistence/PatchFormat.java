package de.tubs.variantsync.core.managers.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.io.APersistentFormat;
import de.ovgu.featureide.fm.core.io.IPersistentFormat;
import de.ovgu.featureide.fm.core.io.Problem;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.AXMLFormat;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.base.DefaultPatchFactory;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
import de.tubs.variantsync.core.utilities.LogOperations;

public class PatchFormat extends AXMLFormat<List<IPatch<?>>> {

	private static final String ID = "Patches";
	private static final String PATCHES = "Patches";
	private static final String PATCH = "Patch";
	private static final String DELTA = "Delta";

	private static final Pattern CONTENT_REGEX = FormatHelper.createContentRegex(PATCHES);

	public static final String FILENAME = ".patches.xml";

	public IFeatureProject project;

	public PatchFormat(IFeatureProject project) {
		this.project = project;
	}

	@Override
	public APersistentFormat<List<IPatch<?>>> getInstance() {
		return new PatchFormat(null);
	}

	public IPersistentFormat<List<IPatch<?>>> getInstance(IFeatureProject project) {
		return new PatchFormat(project);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void readDocument(Document doc, List<Problem> warnings) throws UnsupportedModelException {
		object.clear();
		if (doc.getDocumentElement().getChildNodes().getLength() != 0) {
			final IPatchFactory patchFactory = new DefaultPatchFactory();
			for (final Element ePatch : getElements(doc.getDocumentElement().getChildNodes())) {

				final IPatch<?> patch = patchFactory.createPatch(ePatch.getAttribute("context"));
				patch.setStartTime(Long.valueOf(ePatch.getAttribute("start")));
				patch.setEndTime(Long.valueOf(ePatch.getAttribute("end")));

				for (final Element eDelta : getElements(ePatch.getChildNodes())) {
					IFile res;

					IDeltaFactory deltaFactory;
					try {
						deltaFactory = DeltaFactoryManager.getFactoryById(eDelta.getAttribute("factoryId"));
					} catch (final NoSuchExtensionException e) {
						LogOperations.logInfo("Could not find extension point for factoryId: " + eDelta.getAttribute("factoryId"));
						continue;
					}

					res = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(eDelta.getAttribute("res"));// .getFileForLocation(new
																													// Path(eDelta.getAttribute("res")));
					if (res == null) {
						LogOperations.logInfo("Could not find resource: " + eDelta.getAttribute("res"));
						res = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(eDelta.getAttribute("res")));
						System.out.println(res);
					}

					final IDelta delta = deltaFactory.createDelta(res);
					delta.setContext(eDelta.getAttribute("context"));
					delta.setTimestamp(Long.valueOf(eDelta.getAttribute("timestamp")));
					delta.setType(DELTATYPE.valueOf(eDelta.getAttribute("type")));

					for (final Element eSyncronizedProject : getElements(eDelta.getElementsByTagName("Project"))) {
						delta.addSynchronizedProject(ResourcesPlugin.getWorkspace().getRoot().getProject(eSyncronizedProject.getAttribute("name")));
					}
					for (final Element eProperty : getElements(eDelta.getElementsByTagName("Property"))) {
						delta.addProperty(eProperty.getAttribute("key"), eProperty.getAttribute("value"));
					}
					for (final Element eOriginal : getElements(eDelta.getElementsByTagName("Original"))) {
						delta.setOriginalFromString(eOriginal.getTextContent());
					}
					for (final Element eRevised : getElements(eDelta.getElementsByTagName("Revised"))) {
						delta.setRevisedFromString(eRevised.getTextContent());
					}
					patch.addDelta(delta);
				}
				object.add(patch);
			}
		}
	}

	@Override
	protected void writeDocument(Document doc) {
		final Element root = doc.createElement(PATCHES);

		for (final IPatch<?> patch : object) {
			final Element ePatch = doc.createElement(PATCH);
			ePatch.setAttribute("start", String.valueOf(patch.getStartTime()));
			ePatch.setAttribute("end", String.valueOf(patch.getEndTime()));
			ePatch.setAttribute("context", patch.getContext());

			if (patch.getDeltas().get(0) instanceof IDelta<?>) {
				for (final IDelta<?> delta : patch.getDeltas()) {
					final Element eDelta = doc.createElement(DELTA);
					eDelta.setAttribute("context", delta.getContext());
					eDelta.setAttribute("res", delta.getResource().getFullPath().toOSString());
					eDelta.setAttribute("timestamp", String.valueOf(delta.getTimestamp()));
					eDelta.setAttribute("type", String.valueOf(delta.getType()));
					eDelta.setAttribute("factoryId", delta.getFactoryId());

					final Element eSynchronizedProject = doc.createElement("SynchronizedProjects");
					final List<IProject> projects = delta.getSynchronizedProjects();
					for (final IProject project : projects) {
						final Element eProperty = doc.createElement("Project");
						eProperty.setAttribute("name", project.getName());
						eSynchronizedProject.appendChild(eProperty);
					}
					eDelta.appendChild(eSynchronizedProject);

					final Element eProperties = doc.createElement("Properties");
					final HashMap<String, String> properties = delta.getProperties();
					for (final String key : properties.keySet()) {
						final Element eProperty = doc.createElement("Property");
						eProperty.setAttribute("key", key);
						eProperty.setAttribute("value", properties.get(key));
						eProperties.appendChild(eProperty);
					}
					eDelta.appendChild(eProperties);

					final Element eOriginal = doc.createElement("Original");
					eOriginal.setTextContent(delta.getOriginalAsString());
					eDelta.appendChild(eOriginal);
					final Element eRevised = doc.createElement("Revised");
					eRevised.setTextContent(delta.getRevisedAsString());
					eDelta.appendChild(eRevised);

					ePatch.appendChild(eDelta);
				}
			}
			root.appendChild(ePatch);
		}
		doc.appendChild(root);
	}

	@Override
	public boolean supportsContent(CharSequence content) {
		return supportsRead() && CONTENT_REGEX.matcher(content).find();
	}

	@Override
	public String getName() {
		return "Patch";
	}

}
