package de.tubs.variantsync.core.monitor;

import java.util.List;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.CodeMapping;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.markers.interfaces.IMarkerInformation;
import de.tubs.variantsync.core.patch.DeltaFactoryManager;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;

public class CodeMappingHandler {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addCodeMappingsForDeltas(List<IDelta> deltas) {
		for (IDelta delta : deltas) {
			try {
				IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
				List<IMarkerInformation> markerInformations = deltaFactory.getMarkerInformations(delta.getResource(), delta);

				Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
				if (context != null) {
					for (IMarkerInformation markerInformation : markerInformations) {
						markerInformation.setFeatureExpression(context.getFeatureExpression(delta.getFeature()));
						SourceFile sourceFile = context.getMapping(delta.getResource());
						if (sourceFile == null) {
							sourceFile = new SourceFile(delta.getResource());
						}
						sourceFile.addMapping(new CodeMapping(delta.getRevisedAsString(), markerInformation));
						context.addCodeMapping(delta.getResource(), sourceFile);
					}
				}
			} catch (NoSuchExtensionException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateCodeMappingsForDelta(SourceFile sourceFile, IDelta delta) {
		if (sourceFile.getFile().getFullPath().equals(delta.getResource().getFullPath())) {
			try {
				IDeltaFactory<?> deltaFactory = DeltaFactoryManager.getFactoryById(delta.getFactoryId());
				List<IMarkerInformation> markerInformations = deltaFactory.getMarkerInformations(delta.getResource(), delta);

				switch (delta.getType()) {
				case ADDED:
					updateCodeMappingsForAddedDelta(sourceFile, delta, markerInformations);
					break;
				case CHANGED:
					updateCodeMappingsForChangedDelta(sourceFile, delta, markerInformations);
					break;
				case REMOVED:
					updateCodeMappingsForRemovedDelta(sourceFile, delta, markerInformations);
					break;
				default:
					break;
				}
			} catch (NoSuchExtensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void updateCodeMappingsForAddedDelta(SourceFile sourceFile, IDelta delta, List<IMarkerInformation> markerInformations) {
		for (CodeMapping codeMapping : sourceFile.getMappings()) {
			IMarkerInformation cmMarkerInformation = codeMapping.getMarkerInformation();
			if (cmMarkerInformation.isLine()) {

			}
		}
	}

	private static void updateCodeMappingsForChangedDelta(SourceFile sourceFile, IDelta delta, List<IMarkerInformation> markerInformations) {
		// TODO Auto-generated method stub

	}

	private static void updateCodeMappingsForRemovedDelta(SourceFile sourceFile, IDelta delta, List<IMarkerInformation> markerInformations) {
		// TODO Auto-generated method stub

	}

}
