package de.tubs.variantsync.core.monitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.CodeMapping;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IMarkerHandler;
import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;
import de.tubs.variantsync.core.utilities.LogOperations;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CodeMappingHandler {

	/**
	 * Creates mappings for given deltas
	 * 
	 * @param deltas
	 */
	public static void addCodeMappingsForDeltas(List<IDelta<?>> deltas) {
		for (IDelta delta : deltas) {
			try {
				// Get factory and marker handler for delta
				IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
				IMarkerHandler markerHandler = deltaFactory.getMarkerHandler();
				List<IMarkerInformation> markerInformations = markerHandler.getMarkersForDelta(delta.getResource(), delta);

				// Get current context
				Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
				if (context != null) {
					// Get file with current mappings
					SourceFile sourceFile = context.getMapping(delta.getResource());
					if (sourceFile == null) {
						sourceFile = new SourceFile(delta.getResource());
					}

					// Update all other annotations
					markerHandler.updateMarkerForDelta(sourceFile, delta, markerInformations);

					// Add new code mapping
					for (IMarkerInformation markerInformation : markerInformations) {
						markerInformation.setFeatureExpression(delta.getFeature());
						sourceFile.addMapping(new CodeMapping(delta.getRevisedAsString(), markerInformation));
					}
					context.addCodeMapping(delta.getResource(), sourceFile);
				}
			} catch (NoSuchExtensionException e) {
				LogOperations.logError("Could not map the delta to a feature", e);
			}
		}
	}

	/**
	 * Adds manually created mappings
	 * 
	 * @param file
	 * @param feature
	 * @param offset
	 * @param length
	 * @param content
	 */
	public static void addCodeMappings(IFile file, String feature, int offset, int length, String content) {
		try {
			IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
			List<IMarkerInformation> markerInformations = deltaFactory.getMarkerHandler().getMarkers(file, offset, length);

			Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
			if (context != null) {
				SourceFile sourceFile = context.getMapping(file);
				if (sourceFile == null) {
					sourceFile = new SourceFile(file);
				}
				for (IMarkerInformation markerInformation : markerInformations) {
					markerInformation.setFeatureExpression(feature);
					sourceFile.addMapping(new CodeMapping(content, markerInformation));
				}
				context.addCodeMapping(file, sourceFile);
			}
		} catch (NoSuchExtensionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the mapping for a given source and marker
	 * 
	 * @param sourceFile
	 * @param markerInformation
	 * @return
	 */
	public static CodeMapping getCodeMapping(SourceFile sourceFile, IMarkerInformation markerInformation) {
		for (CodeMapping mapping : sourceFile.getMappings()) {
			if (!mapping.getMarkerInformation().equals(markerInformation)) {
				return mapping;
			}
		}
		return null;
	}

	/**
	 * Returns true, if a marker information exists at the given line in the given file
	 * 
	 * @param sourceFile
	 * @param line
	 * @return
	 */
	public static boolean contains(SourceFile sourceFile, int line) {
		for (CodeMapping mapping : sourceFile.getMappings()) {
			IMarkerInformation markerInformation = mapping.getMarkerInformation();
			if (markerInformation.isLine() && markerInformation.getOffset() == line) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a given marker information in the given file and returns true, if a mapping was removed
	 * 
	 * @param sourceFile
	 * @param markerInformation
	 * @return
	 */
	public static boolean remove(SourceFile sourceFile, IMarkerInformation markerInformation) {
		List<CodeMapping> mappings = new ArrayList<>();
		List<CodeMapping> oldMappings = sourceFile.getMappings();
		for (CodeMapping mapping : oldMappings) {
			if (!mapping.getMarkerInformation().equals(markerInformation)) {
				mappings.add(mapping);
			}
		}
		sourceFile.setMapping(mappings);
		return oldMappings.size() != mappings.size();
	}

}
