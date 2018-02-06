package de.tubs.variantsync.core.patch.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import com.github.difflib.patch.Chunk;

import de.tubs.variantsync.core.data.CodeMapping;
import de.tubs.variantsync.core.data.SourceFile;
import de.tubs.variantsync.core.patch.AMarkerHandler;
import de.tubs.variantsync.core.patch.AMarkerInformation;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;
import de.tubs.variantsync.core.utilities.MarkerUtils;

public class DefaultMarkerHandler extends AMarkerHandler<Chunk<String>> {

	@SuppressWarnings("rawtypes")
	@Override
	public List<IMarkerInformation> getMarkersForDeltas(IFile file, List<IDelta<Chunk<String>>> deltas) {
		List<IMarkerInformation> markerInformations = new ArrayList<>();
		for (IDelta<Chunk<String>> delta : deltas) {
			Chunk revised = delta.getRevised();
			IMarkerInformation markerInformation = new AMarkerInformation(revised.getPosition() - 1, revised.getLines().size() - 1, true);
			markerInformation.setFeatureExpression(delta.getFeature());
			markerInformations.add(markerInformation);
		}
		return markerInformations;
	}

	@Override
	public List<IMarkerInformation> getMarkers(IFile file, int offset, int length) {
		return Arrays.asList(new AMarkerInformation(offset, length, false));
	}

	@Override
	public boolean updateMarkerForDelta(SourceFile sourceFile, IDelta<Chunk<String>> delta, List<IMarkerInformation> markerInformations) {
		for (CodeMapping codeMapping : sourceFile.getMappings()) {
			IMarkerInformation cmMarkerInformation = codeMapping.getMarkerInformation();
			IMarker marker = MarkerUtils.getMarker(delta.getResource(), cmMarkerInformation.getMarkerId());

			int offset = marker.getAttribute(IMarker.CHAR_START, -1);
			int length = marker.getAttribute(IMarker.CHAR_END, -1);
			if (offset != -1) {
				System.out.println(cmMarkerInformation.getMarkerId() + ": " + offset + " , length: " + length);
				cmMarkerInformation.setOffset(offset);
				cmMarkerInformation.setLength(length - offset);
				cmMarkerInformation.setLine(false);
				return true;
			}
		}
		return false;
	}

}
