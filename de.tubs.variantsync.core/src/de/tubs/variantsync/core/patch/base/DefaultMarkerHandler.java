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
import de.tubs.variantsync.core.utilities.MarkerUtils;
import de.variantsync.core.marker.AMarkerInformation;
import de.variantsync.core.marker.IVariantSyncMarker;

//TODO: AST REFACTORING
public class DefaultMarkerHandler extends AMarkerHandler<Chunk<String>> {

	@SuppressWarnings("rawtypes")
	@Override
	public List<IVariantSyncMarker> getMarkersForDeltas(IFile file, List<IDelta<Chunk<String>>> deltas) {
		final List<IVariantSyncMarker> variantSyncMarkers = new ArrayList<>();
		for (final IDelta<Chunk<String>> delta : deltas) {
			final Chunk revised = delta.getRevised();
			// For the display of markers, the utilities.MarkerUtils.setMarker-method uses Editor/Document-information provided by IDocument.
			// IDocument is 0-based (so the first line is line 0 in IDocument), which means that every line number has to be reduced by 1
			final IVariantSyncMarker variantSyncMarker = new AMarkerInformation(revised.getPosition() - 1, revised.getLines().size() - 1, true);
			variantSyncMarker.setContext(delta.getContext());
			variantSyncMarkers.add(variantSyncMarker);
		}
		return variantSyncMarkers;
	}

	@Override
	public List<IVariantSyncMarker> getMarkers(IFile file, int offset, int length) {
		return Arrays.asList(new AMarkerInformation(offset, length, false));
	}

	//TODO: AST REFACTORING
	@Override
	public boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<Chunk<String>> delta, List<IVariantSyncMarker> variantSyncMarkers) {
		for (final CodeMapping codeMapping : sourceFile.getMappings()) {
			final IVariantSyncMarker cmMarkerInformation = codeMapping.getMarkerInformation();
			final IMarker marker = MarkerUtils.getMarker(delta.getResource(), cmMarkerInformation.getMarkerId());

			final int offset = marker.getAttribute(IMarker.CHAR_START, -1);
			final int length = marker.getAttribute(IMarker.CHAR_END, -1);
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
