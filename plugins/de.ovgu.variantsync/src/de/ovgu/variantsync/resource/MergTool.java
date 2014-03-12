package de.ovgu.variantsync.resource;

import java.util.HashSet;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

/**
 * 
 * @author Lei Luo
 * 
 */
public class MergTool {

	@SuppressWarnings("unchecked")
	public static List<String> merg(List<String> fList1, List<String> fList2,
			List<String> fList3) {
		List<String> result = null;
		Patch pf12 = DiffUtils.diff(fList1, fList2);
		Patch pf13 = DiffUtils.diff(fList1, fList3);
		List<Delta> deltas12 = pf12.getDeltas();
		List<Delta> deltas13 = pf13.getDeltas();
		if (!konfliktErkennen(deltas12, deltas13)) {

			Patch patchTemp = new Patch();
			HashSet<Delta> tempDeltas = new HashSet<Delta>();
			tempDeltas.addAll(deltas12);
			tempDeltas.addAll(deltas13);
			for (Delta d : tempDeltas) {
				patchTemp.addDelta(d);
			}
			try {
				result = (List<String>) DiffUtils.patch(fList1, patchTemp);
			} catch (PatchFailedException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return fList3;
		}
	}

	public static boolean konfliktErkennen(List<Delta> deltas12, List<Delta> deltas13) {
		if (deltas13.containsAll(deltas12)) {
			return true;
		} else {
			HashSet<Delta> tempDeltas = new HashSet<Delta>();
			tempDeltas.addAll(deltas12);
			tempDeltas.addAll(deltas13);
			Patch patchTemp = new Patch();
			for (Delta d : tempDeltas) {
				patchTemp.addDelta(d);
			}
			List<Delta> deltas = patchTemp.getDeltas();
			for (int i = 0; i < deltas.size(); i++) {
				if (i + 1 < deltas.size()) {
					int nextStartPosition = deltas.get(i + 1).getOriginal().getPosition();
					int curEndPosition = deltas.get(i).getOriginal().last();
					if (nextStartPosition - curEndPosition > 1) {
						continue;
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}
}