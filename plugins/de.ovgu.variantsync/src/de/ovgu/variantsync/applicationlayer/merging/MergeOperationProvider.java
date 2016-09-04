package de.ovgu.variantsync.applicationlayer.merging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import difflib.Delta;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class MergeOperationProvider extends AbstractModel implements Merging {

	private MergeCalculation mergeCalculation;

	public MergeOperationProvider() {
		this.mergeCalculation = new MergeCalculation();
	}

	@Override
	public boolean checkConflict(List<Delta> d12, List<Delta> d13) {
		return mergeCalculation.checkConflict(d12, d13);
	}

	@Override
	public boolean checkConflict(Collection<String> fAncestor, Collection<String> fLeft, Collection<String> fRight) {
		return mergeCalculation.checkConflict(new ArrayList<String>(fAncestor), new ArrayList<String>(fLeft),
				new ArrayList<String>(fRight));
	}

	@Override
	public Collection<String> performThreeWayMerge(Collection<String> fOrigin, Collection<String> fList1,
			Collection<String> fList2) {
		return mergeCalculation.performThreeWayMerge(fOrigin, fList1, fList2);
	}

	@Override
	public Collection<Delta> getConflictingDeltas(Collection<String> ancestor, Collection<String> left,
			Collection<String> right) {
		return mergeCalculation.getConflictingDeltas(new ArrayList<String>(ancestor), new ArrayList<String>(left),
				new ArrayList<String>(right));
	}
}