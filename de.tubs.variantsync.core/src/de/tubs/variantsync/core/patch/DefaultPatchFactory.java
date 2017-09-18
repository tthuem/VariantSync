package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;

import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.tubs.variantsync.core.exceptions.PatchException;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;
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
 * DefaultPatchFactory
 * 
 * @author Christopher Sontag
 * @since 18.08.2017
 * @TODO Replace with line-wise diff
 */
@SuppressWarnings("rawtypes")
public class DefaultPatchFactory implements IPatchFactory<Chunk> {

	private static IPatchFactory<?> instance = new DefaultPatchFactory();

	@Override
	public String getId() {
		return "de.tubs.variantsync.core.diff";
	}

	@Override
	public String getName() {
		return "Line-wise Diff";
	}

	public static IPatchFactory<?> getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "DefaultPatchFactory [getId()="
			+ getId()
			+ ", getName()="
			+ getName()
			+ "]";
	}

	@Override
	public IPatch<IDelta<Chunk>> createPatch(String feature) {
		IPatch<IDelta<Chunk>> patch = new APatch<IDelta<Chunk>>(getId());
		patch.setFeature(feature);
		patch.setStartTime(System.currentTimeMillis());
		return patch;
	}

	@Override
	public List<IDelta<Chunk>> createDeltas(IResource res, long timestamp, DELTATYPE kind) throws PatchException {
		IFileState state = FileHelper.getLatestHistory((IFile) res);
		return createDeltas(res, state, timestamp, kind);
	}

	@Override
	public List<IDelta<Chunk>> createDeltas(IResource res, IFileState oldState, long timestamp, DELTATYPE kind) throws PatchException {
		// Check for null arguments
		if (res == null
			|| oldState == null
			|| kind == null) return null;

		// Get the current lines
		List<String> currentFilelines = null;
		if (res.exists()
			&& !kind.equals(DELTATYPE.REMOVED)) {
			currentFilelines = FileHelper.getFileLines((IFile) res);
		} else {
			currentFilelines = new ArrayList<>();
		}

		// Get the history lines
		List<String> historyFilelines = FileHelper.getFileLines(oldState);

		// Calculate patch
		Patch<String> patch = DiffUtils.diff(new ArrayList<String>(historyFilelines), new ArrayList<String>(currentFilelines));

		// If a patch was created wrap it in ADelta
		if (!patch.getDeltas().isEmpty()
			&& patch.getDeltas().size() > 0) {

			List<IDelta<Chunk>> deltas = new ArrayList<>();
			for (Delta<String> originalDelta : patch.getDeltas()) {

				IDelta<Chunk> delta = new DefaultDelta(res);
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
	public IDelta<Chunk> createDelta(IResource res) {
		return new DefaultDelta(res);
	}

}
