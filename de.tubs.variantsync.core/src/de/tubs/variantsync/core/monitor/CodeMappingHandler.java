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
import de.tubs.variantsync.core.utilities.LogOperations;
import de.variantsync.core.marker.IVariantSyncMarker;

@SuppressWarnings({ "unchecked", "rawtypes" })
//TODO: AST REFACTORING
public class CodeMappingHandler {

	/**
	 * Creates mappings for given deltas
	 * ENTRY POINT FOR FEATURE RECODRING
	 * @param deltas
	 */
	public static void addCodeMappingsForDeltas(List<IDelta<?>> deltas) {
		//TODO: AST REFACTORING
		//This is the connection between The IDelta and the SourceFile which can be refactored to AST or
		//we also include the IDeltas in the refactoring but this could take more time.
/*
		for (final IDelta delta : deltas) {
			try {
				// Get factory and marker handler for delta
				final IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
				final IMarkerHandler markerHandler = deltaFactory.getMarkerHandler();
				final List<IVariantSyncMarker> variantSyncMarkers = markerHandler.getMarkersForDelta(delta.getResource(), delta);

				// Get current context
				final ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject();
				final MappingManager mappingManager = configurationProject.getMappingManager();
				if (configurationProject != null) {
					//TODO: replace SoruceFile By AST, but what's whith the diffs? Should we replace the deltas?


					// Get file with current mappings
					SourceFile sourceFile = mappingManager.getMapping(delta.getResource());
					if (sourceFile == null) {
						sourceFile = new SourceFile(delta.getResource());
					}

					// Update all other annotations
					markerHandler.updateMarkerForDelta(sourceFile, delta, variantSyncMarkers);

					// Add new code mapping
					for (final IVariantSyncMarker variantSyncMarker : variantSyncMarkers) {
						variantSyncMarker.setContext(delta.getContext());
						sourceFile.addMapping(new CodeMapping(delta.getRevisedAsString(), variantSyncMarker));
					}
					mappingManager.addCodeMapping(delta.getResource(), sourceFile);
				}
			} catch (final NoSuchExtensionException e) {
				LogOperations.logError("Could not map the delta to a context", e);
			}
		}
 */
	}

	/**
	 * Adds manually created mappings
	 *
	 * @param file
	 * @param offset
	 * @param length
	 * @param content
	 */
	public static void addCodeMappings(IFile file, String feature, int offset, int length, String content) {
		//TODO: AST REFACTORING (this method is only used by DynamicContextPopupItems in ...core.view.context)
/*
		try {
			final IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getInstance().getFactoryByFile(file);
			final List<IVariantSyncMarker> variantSyncMarkers = deltaFactory.getMarkerHandler().getMarkers(file, offset, length);

			final ConfigurationProject configurationProject = VariantSyncPlugin.getConfigurationProjectManager().getActiveConfigurationProject();
			final MappingManager mappingManager = configurationProject.getMappingManager();
			if (configurationProject != null) {
				SourceFile sourceFile = mappingManager.getMapping(file);
				if (sourceFile == null) {
					sourceFile = new SourceFile(file);
				}
				for (final IVariantSyncMarker variantSyncMarker : variantSyncMarkers) {
					variantSyncMarker.setContext(feature);
					sourceFile.addMapping(new CodeMapping(content, variantSyncMarker));
				}
				mappingManager.addCodeMapping(file, sourceFile);
			}
		} catch (final NoSuchExtensionException e) {
			e.printStackTrace();
		}

 */
	}


	/**
	 * Returns true, if a marker information exists at the given line in the given file
	 *
	 * @param sourceFile
	 * @param line
	 * @return
	 */
	public static boolean contains(SourceFile sourceFile, int line) {
		//TODO: AST REFACTORING
		for (final CodeMapping mapping : sourceFile.getMappings()) {
			final IVariantSyncMarker variantSyncMarker = mapping.getMarkerInformation();
			if (variantSyncMarker.isLine() && (variantSyncMarker.getOffset() == line)) {
				return true;
			}
		}
		return false;
	}

	//TODO: AST REFACTORING
	//-------------------------------unused methods, maybe delete?

	/**
	 * Returns the mapping for a given source and marker
	 *
	 * @param sourceFile
	 * @param variantSyncMarker
	 * @return
	 */
	public static CodeMapping getCodeMapping(SourceFile sourceFile, IVariantSyncMarker variantSyncMarker) {
		for (final CodeMapping mapping : sourceFile.getMappings()) {
			if (!mapping.getMarkerInformation().equals(variantSyncMarker)) {
				return mapping;
			}
		}
		return null;
	}

	/**
	 * Removes a given marker information in the given file and returns true, if a mapping was removed
	 *
	 * @param sourceFile
	 * @param variantSyncMarker
	 * @return
	 */
	public static boolean remove(SourceFile sourceFile, IVariantSyncMarker variantSyncMarker) {
		final List<CodeMapping> mappings = new ArrayList<>();
		final List<CodeMapping> oldMappings = sourceFile.getMappings();
		for (final CodeMapping mapping : oldMappings) {
			if (!mapping.getMarkerInformation().equals(variantSyncMarker)) {
				mappings.add(mapping);
			}
		}
		sourceFile.setMapping(mappings);
		return oldMappings.size() != mappings.size();
	}

}
