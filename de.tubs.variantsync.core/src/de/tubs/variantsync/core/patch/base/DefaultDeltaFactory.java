package de.tubs.variantsync.core.patch.base;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;

import de.tubs.variantsync.core.exceptions.DiffException;
import de.tubs.variantsync.core.patch.HistoryStore;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;
import de.tubs.variantsync.core.patch.interfaces.IDeltaFactory;
import de.tubs.variantsync.core.patch.interfaces.IMarkerHandler;
import de.tubs.variantsync.core.utilities.FileHelper;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * DefaultDeltaFactory
 * 
 * @author Christopher Sontag
 * @since 18.08.2017
 */
@SuppressWarnings("rawtypes")
public class DefaultDeltaFactory implements IDeltaFactory<Chunk<String>> {

	private static IDeltaFactory<?> instance = new DefaultDeltaFactory();
	private HistoryStore historyStore = new HistoryStore();

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
	public List<IDelta<Chunk<String>>> createDeltas(IFile res, long timestamp, DELTATYPE kind) throws DiffException {
		IFileState state = FileHelper.getLatestHistory((IFile) res);
		return createDeltas(res, state, timestamp, kind);
	}

	@Override
	public List<IDelta<Chunk<String>>> createDeltas(IFile res, IFileState oldState, long timestamp, DELTATYPE kind) throws DiffException {
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
		Patch<String> patch;
		try {
			patch = DiffUtils.diff(new ArrayList<String>(historyFilelines), new ArrayList<String>(currentFilelines), 2);
		} catch (com.github.difflib.algorithm.DiffException e) {
			throw new DiffException(e.getMessage());
		}

		// If a patch was created wrap it in Delta
		if (!patch.getDeltas().isEmpty() && patch.getDeltas().size() > 0) {

			List<IDelta<Chunk<String>>> deltas = new ArrayList<>();
			for (Delta<String> originalDelta : patch.getDeltas()) {

				IDelta<Chunk<String>> delta = new DefaultDelta(res, getId());
				delta.setType(kind);
				delta.setOriginal(originalDelta.getOriginal());
				delta.setRevised(originalDelta.getRevised());

				deltas.add(delta);
			}

			// Normal files are 1-based
			historyFilelines.remove(0);

			// Save original source for merging
			historyStore.addHistory(res, historyFilelines, timestamp);

			return deltas;
		}
		return null;
	}

	@Override
	public IDelta<Chunk<String>> createDeltas(IFile res, IDelta<?> originalDelta) {
		if (res == null) return null;
		if (originalDelta instanceof DefaultDelta) {
			DefaultDelta defaultDelta = (DefaultDelta) originalDelta;
			IDelta<Chunk<String>> delta = new DefaultDelta(res, getId());
			delta.setType(defaultDelta.getType());
			delta.setOriginal(defaultDelta.getOriginal());
			delta.setRevised(defaultDelta.getRevised());
			delta.setContext(defaultDelta.getContext());

			return delta;
		}
		return null;
	}

	@Override
	public List<IDelta<Chunk<String>>> createDeltas(IFile original, IFile revised) throws DiffException {
		// Check for null arguments
		if (original == null || revised == null) return null;

		// Get the current mappings
		List<String> currentFilelines = FileHelper.getFileLines(original);

		// Get the history mappings
		List<String> historyFilelines = FileHelper.getFileLines(revised);

		// DiffUtils are 0-based, Eclipse instead 1-based
		currentFilelines.add(0, "");
		historyFilelines.add(0, "");

		// Calculate patch
		Patch<String> patch;
		try {
			patch = DiffUtils.diff(new ArrayList<String>(historyFilelines), new ArrayList<String>(currentFilelines), 2);
		} catch (com.github.difflib.algorithm.DiffException e) {
			throw new DiffException(e.getMessage());
		}

		// If a patch was created wrap it in Delta
		if (!patch.getDeltas().isEmpty() && patch.getDeltas().size() > 0) {

			List<IDelta<Chunk<String>>> deltas = new ArrayList<>();
			for (Delta<String> originalDelta : patch.getDeltas()) {

				IDelta<Chunk<String>> delta = new DefaultDelta(original, getId());
				delta.setType(DELTATYPE.CHANGED);
				delta.setOriginal(originalDelta.getOriginal());
				delta.setRevised(originalDelta.getRevised());

				deltas.add(delta);
			}

			// Normal files are 1-based
			historyFilelines.remove(0);

			// Save original source for merging
			historyStore.addHistory(original, historyFilelines, System.currentTimeMillis());

			return deltas;
		}
		return null;
	}

	@Override
	public IFile applyDelta(IFile res, IDelta<Chunk<String>> patch) {
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
				if (chunkOriginal.size() != chunkRevised.size()) {
					delta = new InsertDelta<>(chunkOriginal, chunkRevised);
				} else {
					delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
				}
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

			// DiffUtils are 0-based, Eclipse instead 1-based
			linesOld.add(0, "");

			if (linesOld != null) {
				linesNew = DiffUtils.patch(linesOld, p);

				if (linesNew.get(0).equals("")) {
					linesNew.remove(0);
				}

				FileHelper.setFileLines(res, linesNew);
			}
		} catch (PatchFailedException e) {
			LogOperations.logError("Error when patching file", e);
		}
		return res;
	}

	@Override
	public IFile reverseDelta(IFile res, IDelta<Chunk<String>> patch) {
		Patch<String> p = new Patch<String>();

		Chunk<String> chunkOriginal = patch.getOriginal();
		Chunk<String> chunkRevised = patch.getRevised();

		Delta<String> delta = null;
		switch (patch.getType()) {
		case ADDED:
			delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			break;
		case CHANGED:
			if (chunkOriginal.size() != chunkRevised.size()) {
				delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			} else {
				delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
			}
			break;
		case REMOVED:
			delta = new DeleteDelta<>(chunkOriginal, chunkRevised);
			break;
		default:
			return null;
		}
		p.addDelta(delta);

		List<String> linesNew = FileHelper.getFileLines(res);
		linesNew.add(0, "");

		List<String> linesOld;
		if (linesNew != null) {
			linesOld = DiffUtils.unpatch(linesNew, p);

			if (linesOld.get(0).equals("")) {
				linesOld.remove(0);
			}

			FileHelper.setFileLines(res, linesOld);
		}
		return res;
	}

	@Override
	public boolean verifyDelta(IFile res, IDelta<Chunk<String>> patch) {
		Chunk<String> chunkOriginal = patch.getOriginal();
		Chunk<String> chunkRevised = patch.getRevised();

		if (chunkOriginal == null || chunkRevised == null) return false;

		Delta<String> delta = null;
		switch (patch.getType()) {
		case ADDED:
			delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			break;
		case CHANGED:
			if (chunkOriginal.size() != chunkRevised.size()) {
				delta = new InsertDelta<>(chunkOriginal, chunkRevised);
			} else {
				delta = new ChangeDelta<>(chunkOriginal, chunkRevised);
			}
			break;
		case REMOVED:
			delta = new DeleteDelta<>(chunkOriginal, chunkRevised);
			break;
		default:
			return false;
		}

		List<String> lines = FileHelper.getFileLines(res);
		lines.add(0, "");
		try {
			delta.verify(lines);
		} catch (PatchFailedException e) {
			return false;
		} catch (IndexOutOfBoundsException e) {
			LogOperations.logDebug("An IndexOutOfBoundsException occured. The file used for comparision has fewer lines than what was changed by the delta.");
			return false;
		}
		return true;
	}

	@Override
	public boolean isSupported(IFile file) {
		return true;
	}

	@Override
	public IDelta<Chunk<String>> createDelta(IFile res) {
		return new DefaultDelta(res, getId());
	}

	@Override
	public IMarkerHandler getMarkerHandler() {
		return new DefaultMarkerHandler();
	}


	/*
	@Override
	public boolean initExtension() {
		return true;
	}**/

}
