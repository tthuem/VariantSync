package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

import org.eclipse.core.resources.IFile;

import de.tubs.variantsync.core.managers.data.SourceFile;
import de.variantsync.core.marker.IVariantSyncMarker;

public interface IMarkerHandler<T> {

	List<IVariantSyncMarker> getMarkers(IFile file, int offset, int length);

	List<IVariantSyncMarker> getMarkersForDelta(IFile file, IDelta<T> delta);

	List<IVariantSyncMarker> getMarkersForDeltas(IFile file, List<IDelta<T>> deltas);

	boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<T> delta, List<IVariantSyncMarker> variantSyncMarkers);
}
