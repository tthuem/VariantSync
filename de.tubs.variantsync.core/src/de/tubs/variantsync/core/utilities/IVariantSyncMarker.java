package de.tubs.variantsync.core.utilities;

public interface IVariantSyncMarker {

	/**
	 * Returns marker id.
	 *
	 * @return markerId
	 */
	long getMarkerId();

	/**
	 * Sets marker id.
	 *
	 * @param markerId - id of marker
	 */
	void setMarkerId(long markerId);

	/**
	 * Returns offset.
	 *
	 * @return offset
	 */
	int getOffset();

	/**
	 * Sets offset.
	 *
	 * @param offset
	 */
	void setOffset(int offset);

	/**
	 * Returns length.
	 *
	 * @return length
	 */
	int getLength();

	/**
	 * Sets length.
	 *
	 * @param length
	 */
	void setLength(int length);

	/**
	 * Returns feature context.
	 *
	 * @return feature context
	 */
	String getContext();

	/**
	 * Sets feature expression.
	 *
	 * @param context - feature context
	 */
	void setContext(String context);

	/**
	 * Returns whether the complete line is annotated
	 *
	 * @return true, if the complete line is annotated
	 */
	boolean isLine();

	/**
	 * Sets whether the complete line is annotated
	 *
	 * @param true, if the complete line is annotated
	 */
	void setLine(boolean isLine);
}
