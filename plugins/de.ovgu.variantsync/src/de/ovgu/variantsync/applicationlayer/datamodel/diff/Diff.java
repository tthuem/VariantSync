package de.ovgu.variantsync.applicationlayer.datamodel.diff;

import java.util.List;

public class Diff {

	private DiffIndices diffIndices;
	private List<DiffStep> diffSteps;

	public Diff(DiffIndices diffIndices, List<DiffStep> diffSteps) {
		this.diffIndices = diffIndices;
		this.diffSteps = diffSteps;
	}

	/**
	 * @return the diffIndices
	 */
	public DiffIndices getDiffIndices() {
		return diffIndices;
	}

	/**
	 * @param diffIndices
	 *            the diffIndices to set
	 */
	public void setDiffIndices(DiffIndices diffIndices) {
		this.diffIndices = diffIndices;
	}

	/**
	 * @return the diffSteps
	 */
	public List<DiffStep> getDiffSteps() {
		return diffSteps;
	}

	/**
	 * @param diffSteps
	 *            the diffSteps to set
	 */
	public void setDiffSteps(List<DiffStep> diffSteps) {
		this.diffSteps = diffSteps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Diff [diffIndices=" + diffIndices + ", diffSteps=" + diffSteps
				+ "]";
	}

}
