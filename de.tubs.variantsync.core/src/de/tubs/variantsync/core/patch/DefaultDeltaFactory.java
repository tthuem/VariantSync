package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;

import de.tubs.variantsync.core.exceptions.PatchException;
import de.tubs.variantsync.core.markers.MarkerInformation;
import de.tubs.variantsync.core.markers.interfaces.IMarkerInformation;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.utilities.FileHelper;
import de.tubs.variantsync.core.utilities.LogOperations;
import difflib.ChangeDelta;
import difflib.Chunk;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.InsertDelta;
import difflib.Patch;
import difflib.PatchFailedException;

/**
 * DefaultDeltaFactory
 * 
 * @author Christopher Sontag
 * @since 18.08.2017
 */
@SuppressWarnings("rawtypes")
public class DefaultDeltaFactory implements IDeltaFactory<Chunk> {

	private static IDeltaFactory<?> instance = new DefaultDeltaFactory();

	@Override
	public String getId() {
		return "de.tubs.variantsync.core.diff";
	}

	@Override
	public String getName() {
		return "Line-wise Diff";
	}

	public static IDeltaFactory<?> getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "DefaultDeltaFactory [getId()=" + getId() + ", getName()=" + getName() + "]";
	}

	@Override
	public List<IDelta<Chunk>> createDeltas(IFile res, long timestamp, DELTATYPE kind) throws PatchException {
		IFileState state = FileHelper.getLatestHistory((IFile) res);
		return createDeltas(res, state, timestamp, kind);
	}

	@Override
	public List<IDelta<Chunk>> createDeltas(IFile res, IFileState oldState, long timestamp, DELTATYPE kind) throws PatchException {
		// Check for null arguments
		if (res == null || oldState == null || kind == null) return null;

		// Get the current mappings
		List<String> currentFilelines = null;
		if (res.exists() && !kind.equals(DELTATYPE.REMOVED)) {
			currentFilelines = FileHelper.getFileLines((IFile) res);
		} else {
			currentFilelines = new ArrayList<>();
		}

		// Get the history mappings
		List<String> historyFilelines = FileHelper.getFileLines(oldState);

		// DiffUtils are 0-based, Eclipse instead 1-based
		currentFilelines.add(0, "");
		historyFilelines.add(0, "");

		// Calculate patch
		Patch<String> patch = DiffUtils.diff(new ArrayList<String>(historyFilelines), new ArrayList<String>(currentFilelines));

		// If a patch was created wrap it in ADelta
		if (!patch.getDeltas().isEmpty() && patch.getDeltas().size() > 0) {

			List<IDelta<Chunk>> deltas = new ArrayList<>();
			for (Delta<String> originalDelta : patch.getDeltas()) {

				IDelta<Chunk> delta = new DefaultDelta(res, getId());
				delta.setType(kind);
				delta.setOriginal(originalDelta.getOriginal());
				delta.setRevised(originalDelta.getRevised());

				deltas.add(delta);
			}
			return deltas;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFile applyDelta(IFile res, IDelta<Chunk> patch) {
		try {
			Patch<String> p = new Patch<String>();

			Chunk<String> chunkOriginal = patch.getOriginal();
			Chunk<String> chunkRevised = patch.getRevised();

			Delta<String> delta = null;
			switch (patch.getType()) {
			case ADDED:
				delta = new InsertDelta<>(chunkOriginal, chunkRevised);
				break;
			case CHANGED:
				delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
				break;
			case REMOVED:
				delta = new DeleteDelta<>(chunkOriginal, chunkRevised);
				break;
			default:
				return null;
			}
			p.addDelta(delta);

			List<String> linesOld = FileHelper.getFileLines(res);
			List<String> linesNew;
			if (linesOld != null) {
				linesNew = DiffUtils.patch(linesOld, p);
				FileHelper.setFileLines(res, linesNew);
			}
		} catch (PatchFailedException e) {
			LogOperations.logError("Error when patching file", e);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFile reverseDelta(IFile res, IDelta<Chunk> patch) {
		Patch<String> p = new Patch<String>();

		Chunk<String> chunkOriginal = patch.getOriginal();
		Chunk<String> chunkRevised = patch.getRevised();

		Delta<String> delta = null;
		switch (patch.getType()) {
		case ADDED:
			delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			break;
		case CHANGED:
			delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
			break;
		case REMOVED:
			delta = new DeleteDelta<>(chunkOriginal, chunkRevised);
			break;
		default:
			return null;
		}
		p.addDelta(delta);

		List<String> linesNew = FileHelper.getFileLines(res);
		List<String> linesOld;
		if (linesNew != null) {
			linesOld = DiffUtils.unpatch(linesNew, p);
			FileHelper.setFileLines(res, linesOld);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean verifyDelta(IFile res, IDelta<Chunk> patch) {
		Chunk<String> chunkOriginal = patch.getOriginal();
		Chunk<String> chunkRevised = patch.getRevised();

		Delta<String> delta = null;
		switch (patch.getType()) {
		case ADDED:
			delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			break;
		case CHANGED:
			delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
			break;
		case REMOVED:
			delta = new DeleteDelta<>(chunkOriginal, chunkRevised);
			break;
		default:
			return false;
		}

		try {
			delta.verify(FileHelper.getFileLines(res));
		} catch (PatchFailedException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSupported(IFile file) {
		return true;
	}

	@Override
	public IDelta<Chunk> createDelta(IFile res) {
		return new DefaultDelta(res, getId());
	}

	@Override
	public List<IMarkerInformation> getMarkerInformations(IFile file, IDelta<Chunk> delta) {
		List<IMarkerInformation> markerInformations = new ArrayList<>();
		Chunk revised = delta.getRevised();
		for (int i = 0; i <= revised.getLines().size() - 1; i++) {
			if (!((String) revised.getLines().get(i)).replaceAll(" ", "").replaceAll("\t", "").isEmpty()) {
				IMarkerInformation markerInformation = new MarkerInformation(revised.getPosition() + i);
				markerInformation.setFeatureExpression(delta.getFeature());
				System.out.println("Created marker information: " + markerInformation);
				markerInformations.add(markerInformation);
			}
		}
		return markerInformations;
	}

}
