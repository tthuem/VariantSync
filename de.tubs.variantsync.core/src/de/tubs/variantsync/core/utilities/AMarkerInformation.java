package de.tubs.variantsync.core.utilities;

import java.util.UUID;

/**
 * Contains all marker relevant informations
 * 
 * @author Tristan Pfofe
 * @author Christopher Sontag
 * @since 17.06.2016
 */
public class AMarkerInformation implements IVariantSyncMarker {

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
	public AMarkerInformation(long markerId, int offset, int length, boolean line) {
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
	public AMarkerInformation(int offset, int length, boolean line) {
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
	public AMarkerInformation(long markerId, int line) {
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
	public AMarkerInformation(int line) {
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
	 * Sets offset.
	 * 
	 * @param offset
	 */
	@Override
	public void setOffset(int offset) {
		this.offset = offset;
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
	 * Sets length.
	 * 
	 * @param length
	 */
	@Override
	public void setLength(int length) {
		this.length = length;
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
	public String getContext() {
		return expression;
	}

	/**
	 * Sets feature expression.
	 * 
	 * @param expression - feature expression
	 */
	@Override
	public void setContext(String expression) {
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

	/**
	 * Sets whether the complete line is annotated
	 * 
	 * @param true, if the complete line is annotated
	 */
	@Override
	public void setLine(boolean isLine) {
		this.isLine = isLine;
	}

	@Override
	public String toString() {
		return String.format("AMarkerInformation [markerId=%s, offset=%s, length=%s, expression=%s, isLine=%s]", markerId, offset, length, expression, isLine);
	}

}
