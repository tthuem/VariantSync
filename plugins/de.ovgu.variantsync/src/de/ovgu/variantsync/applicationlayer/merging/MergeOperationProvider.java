package de.ovgu.variantsync.applicationlayer.merging;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import difflib.Delta;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class MergeOperationProvider extends AbstractModel implements
		IMergeOperations {

	private MergeCalculation mergeCalculation;

	public MergeOperationProvider() {
		this.mergeCalculation = new MergeCalculation();
	}

	@Override
	public boolean checkConflict(List<Delta> d12, List<Delta> d13) {
		return mergeCalculation.checkConflict(d12, d13);
	}

	@Override
	public boolean checkConflict(ArrayList<String> fAncestor,
			ArrayList<String> fLeft, ArrayList<String> fRight) {
		return mergeCalculation.checkConflict(fAncestor, fLeft, fRight);
	}

	@Override
	public List<String> performThreeWayMerge(List<String> fOrigin,
			List<String> fList1, List<String> fList2) {
		return mergeCalculation.performThreeWayMerge(fOrigin, fList1, fList2);
	}

	@Override
	public boolean checkConflict(List<String> fOrigin, List<String> fList1,
			List<String> fList2) {
		return mergeCalculation.checkConflict(fOrigin, fList1, fList2);
	}
}