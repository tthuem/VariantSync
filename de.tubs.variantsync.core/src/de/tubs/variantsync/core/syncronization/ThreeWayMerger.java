package de.tubs.variantsync.core.syncronization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import com.github.difflib.patch.Delta;

import de.tubs.variantsync.core.patch.base.DefaultPatchFactory;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.patch.interfaces.IPatchFactory;

/**
 * Performes a three-way like merge operation
 * 
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.1
 * @since 15.05.2015
 */
@Deprecated
public class ThreeWayMerger {

	private IPatchFactory factory = new DefaultPatchFactory();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean checkConflict(List<IDelta<?>> leftDeltas, List<IDelta<?>> rightDeltas) {
		if (rightDeltas.containsAll(leftDeltas)) {
			return true;
		}

		IPatch patchTemp = factory.createPatch();
		patchTemp.addDeltas(leftDeltas);
		patchTemp.addDeltas(rightDeltas);

		List<Delta> deltas = patchTemp.getDeltas();
		for (int i = 1; i < deltas.size() - 1; i++) {
			Delta actualDelta = deltas.get(i);
			int nextStartPosition = actualDelta.getOriginal().getPosition();
			Delta followingDelta = deltas.get(i - 1);
			int curEndPosition = followingDelta.getOriginal().last();
			if (nextStartPosition - curEndPosition <= 1) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<Delta> getConflictingDeltas(List<IDelta<?>> leftDeltas, List<IDelta<?>> rightDeltas) {
		if (rightDeltas.containsAll(leftDeltas)) {
			return Collections.emptyList();
		}

		IPatch patchTemp = factory.createPatch();
		patchTemp.addDeltas(leftDeltas);
		patchTemp.addDeltas(rightDeltas);

		List<Delta> deltas = patchTemp.getDeltas();
		List<Delta> result = new ArrayList<Delta>();
		for (int i = 1; i < deltas.size() - 1; i++) {
			Delta actualDelta = deltas.get(i);
			int nextStartPosition = actualDelta.getOriginal().getPosition();
			Delta followingDelta = deltas.get(i - 1);
			int curEndPosition = followingDelta.getOriginal().last();
			if (nextStartPosition - curEndPosition <= 1) {
				result.add(actualDelta);
				result.add(followingDelta);
				return result;
			}

		}
		return Collections.emptyList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IFile performThreeWayMerge(IFile original, List<IDelta<?>> leftDeltas, List<IDelta<?>> rightDeltas) {
		IFile result = null;
		if (!checkConflict(leftDeltas, rightDeltas)) {

			IPatch patchTemp = factory.createPatch();
			patchTemp.addDeltas(leftDeltas);
			patchTemp.addDeltas(rightDeltas);

			result = factory.applyDelta(original, patchTemp);
		}
		return result;
	}

}
