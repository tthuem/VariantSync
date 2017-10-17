package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

import org.eclipse.core.resources.IFile;

public interface IMarkerHandler<T> {

	List<IMarkerInformation> getMarkersForDelta(IFile file, IDelta<T> delta);

	List<IMarkerInformation> getMarkersForDeltas(IFile file, List<IDelta<T>> deltas);

	IMarkerInformation updateMarkerForDelta(IMarkerInformation markerInformation, IDelta<T> delta);
}
