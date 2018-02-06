package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

import org.eclipse.core.resources.IFile;

import de.tubs.variantsync.core.data.SourceFile;

public interface IMarkerHandler<T> {

	List<IMarkerInformation> getMarkers(IFile file, int offset, int length);

	List<IMarkerInformation> getMarkersForDelta(IFile file, IDelta<T> delta);

	List<IMarkerInformation> getMarkersForDeltas(IFile file, List<IDelta<T>> deltas);

	boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<T> delta, List<IMarkerInformation> markerInformations);
}
