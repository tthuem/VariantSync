package de.tubs.variantsync.core.monitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.MappingManager;
import de.tubs.variantsync.core.managers.data.CodeMapping;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IMarkerHandler;
import de.tubs.variantsync.core.utilities.IVariantSyncMarker;
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
				List<IVariantSyncMarker> variantSyncMarkers = markerHandler.getMarkersForDelta(delta.getResource(),
						delta);

				// Get current context
				ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager()
						.getActiveConfigurationProject();
				MappingManager mappingManager = configurationProject.getMappingManager();
				if (configurationProject != null) {
					// Get file with current mappings
					SourceFile sourceFile = mappingManager.getMapping(delta.getResource());
					if (sourceFile == null) {
						sourceFile = new SourceFile(delta.getResource());
					}

					// Update all other annotations
					markerHandler.updateMarkerForDelta(sourceFile, delta, variantSyncMarkers);

					// Add new code mapping
					for (IVariantSyncMarker variantSyncMarker : variantSyncMarkers) {
						variantSyncMarker.setContext(delta.getContext());
						sourceFile.addMapping(new CodeMapping(delta.getRevisedAsString(), variantSyncMarker));
					}
					mappingManager.addCodeMapping(delta.getResource(), sourceFile);
				}
			} catch (NoSuchExtensionException e) {
				LogOperations.logError("Could not map the delta to a context", e);
			}
		}
	}

	/**
	 * Adds manually created mappings
	 * 
	 * @param file
	 * @param context
	 * @param offset
	 * @param length
	 * @param content
	 */
	public static void addCodeMappings(IFile file, String feature, int offset, int length, String content) {
		try {
			IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
			List<IVariantSyncMarker> variantSyncMarkers = deltaFactory.getMarkerHandler().getMarkers(file, offset,
					length);

			ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager()
					.getActiveConfigurationProject();
			MappingManager mappingManager = configurationProject.getMappingManager();
			if (configurationProject != null) {
				SourceFile sourceFile = mappingManager.getMapping(file);
				if (sourceFile == null) {
					sourceFile = new SourceFile(file);
				}
				for (IVariantSyncMarker variantSyncMarker : variantSyncMarkers) {
					variantSyncMarker.setContext(feature);
					sourceFile.addMapping(new CodeMapping(content, variantSyncMarker));
				}
				mappingManager.addCodeMapping(file, sourceFile);
			}
		} catch (NoSuchExtensionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the mapping for a given source and marker
	 * 
	 * @param sourceFile
	 * @param variantSyncMarker
	 * @return
	 */
	public static CodeMapping getCodeMapping(SourceFile sourceFile, IVariantSyncMarker variantSyncMarker) {
		for (CodeMapping mapping : sourceFile.getMappings()) {
			if (!mapping.getMarkerInformation().equals(variantSyncMarker)) {
				return mapping;
			}
		}
		return null;
	}

	/**
	 * Returns true, if a marker information exists at the given line in the given
	 * file
	 * 
	 * @param sourceFile
	 * @param line
	 * @return
	 */
	public static boolean contains(SourceFile sourceFile, int line) {
		for (CodeMapping mapping : sourceFile.getMappings()) {
			IVariantSyncMarker variantSyncMarker = mapping.getMarkerInformation();
			if (variantSyncMarker.isLine() && variantSyncMarker.getOffset() == line) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a given marker information in the given file and returns true, if a
	 * mapping was removed
	 * 
	 * @param sourceFile
	 * @param variantSyncMarker
	 * @return
	 */
	public static boolean remove(SourceFile sourceFile, IVariantSyncMarker variantSyncMarker) {
		List<CodeMapping> mappings = new ArrayList<>();
		List<CodeMapping> oldMappings = sourceFile.getMappings();
		for (CodeMapping mapping : oldMappings) {
			if (!mapping.getMarkerInformation().equals(variantSyncMarker)) {
				mappings.add(mapping);
			}
		}
		sourceFile.setMapping(mappings);
		return oldMappings.size() != mappings.size();
	}

}
