package de.ovgu.variantsync.applicationlayer.merging;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import difflib.Delta;

/**
 * Defines functions to merge deltas.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.05.2015
 */
public interface IMergeOperations {

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
	 * @param fList2
	 *            development history two
	 * @param fList3
	 *            development history three
	 * @return merged development branch
	 */
	List<String> performThreeWayMerge(List<String> fList1, List<String> fList2,
			List<String> fList3);

	List<CodeLine> doAutoSync(List<CodeLine> left, List<CodeLine> base,
			List<CodeLine> right);
}
