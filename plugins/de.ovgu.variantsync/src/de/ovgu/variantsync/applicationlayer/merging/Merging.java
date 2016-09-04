package de.ovgu.variantsync.applicationlayer.merging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import difflib.Delta;

/**
 * Defines functions to merge deltas.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.05.2015
 */
public interface Merging {

	/**
	 * Checks conflicts between two deltas.
	 * 
	 * @param deltas12
	 *            original delta
	 * @param deltas13
	 *            changed delta
	 * @return true if conflict is detected; otherwise false
	 */
	boolean checkConflict(List<Delta> deltas12, List<Delta> deltas13);

	/**
	 * Performs three way merge. Joins three development histories together.
	 * 
	 * @param fOrigin
	 *            origin development history
	 * @param left
	 *            development history two
	 * @param fList3
	 *            development history three
	 * @return merged development branch
	 */
	Collection<String> performThreeWayMerge(Collection<String> base, Collection<String> left,
			Collection<String> fList3);

	boolean checkConflict(Collection<String> fAncestor, Collection<String> fLeft, Collection<String> fRight);

	Collection<Delta> getConflictingDeltas(Collection<String> ancestor, Collection<String> left,
			Collection<String> right);
}
