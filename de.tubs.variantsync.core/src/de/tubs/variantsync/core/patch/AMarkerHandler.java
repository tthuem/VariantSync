package de.tubs.variantsync.core.patch;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;

import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IMarkerHandler;
import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;

public abstract class AMarkerHandler<T> implements IMarkerHandler<T> {

	@Override
	public List<IMarkerInformation> getMarkersForDelta(IFile file, IDelta<T> delta) {
		return getMarkersForDeltas(file, Arrays.asList(delta));
	}

	@Override
	public abstract List<IMarkerInformation> getMarkersForDeltas(IFile file, List<IDelta<T>> deltas);

	@Override
	public abstract List<IMarkerInformation> getMarkers(IFile file, int offset, int length);

	@Override
	public abstract boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<T> delta, List<IMarkerInformation> markerInformations);

}
