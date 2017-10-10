package de.tubs.variantsync.core.markers;

import java.util.UUID;

import de.tubs.variantsync.core.markers.interfaces.IMarkerInformation;

/**
 * Contains all marker relevant informations
 * 
 * @author Tristan Pfofe
 * @author Christopher Sontag
 * @since 17.06.2016
 */
public class MarkerInformation implements IMarkerInformation {

	private long markerId;
	private int offset;
	private int length;
	private String expression;
	private boolean isLine = false;

	/**
	 * Constructor
	 * 
	 * @param markerId - marker id
	 * @param offset - offset
	 * @param length - length
	 * @param line - if true, the offset and length are interpreted as mappings
	 */
	public MarkerInformation(long markerId, int offset, int length, boolean line) {
		this.markerId = markerId;
		this.offset = offset;
		this.length = length;
		this.isLine = line;
	}

	/**
	 * Constructor
	 * 
	 * @param offset - offset
	 * @param length - length
	 * @param line - if true, the offset and length are interpreted as mappings
	 */
	public MarkerInformation(int offset, int length, boolean line) {
		this.markerId = UUID.randomUUID().getMostSignificantBits();
		this.offset = offset;
		this.length = length;
		this.isLine = line;
	}

	/**
	 * Constructor
	 * 
	 * @param markerId - marker id
	 * @param line - line
	 */
	public MarkerInformation(long markerId, int line) {
		this.markerId = markerId;
		this.offset = line;
		this.length = 1;
		this.isLine = true;
	}

	/**
	 * Constructor
	 * 
	 * @param line - line
	 */
	public MarkerInformation(int line) {
		this.markerId = UUID.randomUUID().getMostSignificantBits();
		this.offset = line;
		this.length = 1;
		this.isLine = true;
	}

	/**
	 * Returns marker id.
	 * 
	 * @return markerId
	 */
	@Override
	public long getMarkerId() {
		return markerId;
	}

	/**
	 * Returns offset.
	 * 
	 * @return offset
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns length.
	 * 
	 * @return length
	 */
	@Override
	public int getLength() {
		return length;
	}

	/**
	 * Sets marker id.
	 * 
	 * @param markerId - id of marker
	 */
	@Override
	public void setMarkerId(long markerId) {
		this.markerId = markerId;
	}

	/**
	 * Returns feature expression.
	 * 
	 * @return feature expression
	 */
	@Override
	public String getFeatureExpression() {
		return expression;
	}

	/**
	 * Sets feature expression.
	 * 
	 * @param expression - feature expression
	 */
	@Override
	public void setFeatureExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Returns whether offset and length should be interpreted as mappings
	 * 
	 * @return true, if offset and length should be interpreted as mappings
	 */
	@Override
	public boolean isLine() {
		return isLine;
	}

	@Override
	public String toString() {
		return String.format("MarkerInformation [markerId=%s, offset=%s, length=%s, expression=%s, isLine=%s]", markerId, offset, length, expression, isLine);
	}

}
