package de.tubs.variantsync.core.markers;

import de.tubs.variantsync.core.data.FeatureExpression;

public class MarkerInformation {

	private long markerId;
	private int start;
	private int end;
	private int offset;
	private int length;
	private FeatureExpression expression;

	public MarkerInformation(long markerId, int start, int end, int offset,
			int length) {
		this.markerId = markerId;
		this.start = start;
		this.end = end;
		this.offset = offset;
	}

	/**
	 * @return the markerId
	 */
	public long getMarkerId() {
		return markerId;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param markerId
	 *            the markerId to set
	 */
	public void setMarkerId(long markerId) {
		this.markerId = markerId;
	}

	public FeatureExpression getFeatureExpression() {
		return expression;
	}

	public void setFeatureExpression(FeatureExpression expression) {
		this.expression = expression;
	}

}
