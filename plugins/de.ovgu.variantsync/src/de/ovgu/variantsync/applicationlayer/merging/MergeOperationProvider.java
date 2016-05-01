package de.ovgu.variantsync.applicationlayer.merging;

import java.util.ArrayList;
import java.util.List;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
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
	public List<String> performThreeWayMerge(List<String> fOrigin,
			List<String> fList1, List<String> fList2) {
		return mergeCalculation.performThreeWayMerge(fOrigin, fList1, fList2);
	}

	@Override
	public List<CodeLine> doAutoSync(List<CodeLine> left, List<CodeLine> base,
			List<CodeLine> right) {
		List<String> quell = new ArrayList<String>();
		for (CodeLine cl : left) {
			quell.add(cl.getCode());
		}
		List<String> baseCode = new ArrayList<String>();
		for (CodeLine cl : base) {
			baseCode.add(cl.getCode());
		}
		List<String> targetCode = new ArrayList<String>();
		for (CodeLine cl : right) {
			targetCode.add(cl.getCode());
		}		List<CodeLine> syncResult = new ArrayList<CodeLine>();
		List<String> result = JDimeWrapper.merge(quell, baseCode, targetCode);
		int i = 1;
		for(String s : result){
			syncResult.add(new CodeLine(s, i));
			i++;
		}
		return syncResult;
	}
}