package de.tubs.variantsync.core.markers.interfaces;

public interface IMarkerInformation {

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
	 * Returns length.
	 * 
	 * @return length
	 */
	int getLength();

	/**
	 * Returns feature expression.
	 * 
	 * @return feature expression
	 */
	String getFeatureExpression();

	/**
	 * Sets feature expression.
	 * 
	 * @param featureExpression - feature expression
	 */
	void setFeatureExpression(String featureExpression);

	/**
	 * Returns whether the complete line is annotated
	 * 
	 * @return true, if the complete line is annotated
	 */
	boolean isLine();

}
