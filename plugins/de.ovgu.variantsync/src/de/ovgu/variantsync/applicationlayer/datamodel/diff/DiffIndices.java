package de.ovgu.variantsync.applicationlayer.datamodel.diff;

public class DiffIndices {

	private int startIndixOldCode;
	private int numberOfOldCodeLines;
	private int startIndixNewCode;
	private int numberOfNewCodeLines;

	public DiffIndices(int startIndixOldCode, int numberOfOldCodeLines,
			int startIndixNewCode, int numberOfNewCodeLines) {
		this.startIndixOldCode = startIndixOldCode;
		this.numberOfOldCodeLines = numberOfOldCodeLines;
		this.startIndixNewCode = startIndixNewCode;
		this.numberOfNewCodeLines = numberOfNewCodeLines;
	}

	/**
	 * @return the startIndixOldCode
	 */
	public int getStartIndixOldCode() {
		return startIndixOldCode;
	}

	/**
	 * @return the numberOfOldCodeLines
	 */
	public int getNumberOfOldCodeLines() {
		return numberOfOldCodeLines;
	}

	/**
	 * @return the startIndixNewCode
	 */
	public int getStartIndixNewCode() {
		return startIndixNewCode;
	}

	/**
	 * @return the numberOfNewCodeLines
	 */
	public int getNumberOfNewCodeLines() {
		return numberOfNewCodeLines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DiffIndices [startIndixOldCode=" + startIndixOldCode
				+ ", numberOfOldCodeLines=" + numberOfOldCodeLines
				+ ", startIndixNewCode=" + startIndixNewCode
				+ ", numberOfNewCodeLines=" + numberOfNewCodeLines + "]";
	}

}
