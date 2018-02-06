package de.tubs.variantsync.core.patch.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import com.github.difflib.patch.Chunk;

import de.tubs.variantsync.core.managers.data.CodeMapping;
import de.tubs.variantsync.core.managers.data.SourceFile;
import de.tubs.variantsync.core.patch.AMarkerHandler;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.utilities.AMarkerInformation;
import de.tubs.variantsync.core.utilities.IVariantSyncMarker;
import de.tubs.variantsync.core.utilities.MarkerUtils;

public class DefaultMarkerHandler extends AMarkerHandler<Chunk<String>> {

	@SuppressWarnings("rawtypes")
	@Override
	public List<IVariantSyncMarker> getMarkersForDeltas(IFile file, List<IDelta<Chunk<String>>> deltas) {
		List<IVariantSyncMarker> variantSyncMarkers = new ArrayList<>();
		for (IDelta<Chunk<String>> delta : deltas) {
			Chunk revised = delta.getRevised();
			IVariantSyncMarker variantSyncMarker = new AMarkerInformation(revised.getPosition() - 1, revised.getLines().size() - 1, true);
			variantSyncMarker.setContext(delta.getContext());
			variantSyncMarkers.add(variantSyncMarker);
		}
		return variantSyncMarkers;
	}

	@Override
	public List<IVariantSyncMarker> getMarkers(IFile file, int offset, int length) {
		return Arrays.asList(new AMarkerInformation(offset, length, false));
	}

	@Override
	public boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<Chunk<String>> delta, List<IVariantSyncMarker> variantSyncMarkers) {
		for (CodeMapping codeMapping : sourceFile.getMappings()) {
			IVariantSyncMarker cmMarkerInformation = codeMapping.getMarkerInformation();
			IMarker marker = MarkerUtils.getMarker(delta.getResource(), cmMarkerInformation.getMarkerId());

			int offset = marker.getAttribute(IMarker.CHAR_START, -1);
			int length = marker.getAttribute(IMarker.CHAR_END, -1);
			if (offset != -1) {
				cmMarkerInformation.setOffset(offset);
				cmMarkerInformation.setLength(length - offset);
				cmMarkerInformation.setLine(false);
				return true;
			}
		}
		return false;
	}

}
