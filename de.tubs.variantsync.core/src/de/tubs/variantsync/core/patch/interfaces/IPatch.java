package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

/**
 * Class containing a number of deltas for a given file
 * 
 * @author Christopher Sontag
 * @version 1.0
 * @since 18.08.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public interface IPatch<T> {

	/**
	 * Add the given delta to this patch
	 * @param delta - the given delta
	 */
	void addDelta(IDelta<T> delta);
	
	/**
	 * Add the given deltas to this patch
	 * @param deltas - the given deltas
	 */
	void addDeltas(List<IDelta<T>> deltas);
	
	/**
	 * Returns the list of all deltas
	 * @return the deltas
	 */
	List<IDelta<T>> getDeltas();
	
	/**
	 * Removes the given delta from this patch
	 * @param delta
	 * @return true when the element has exist and is now removed
	 */
	boolean removeDelta(IDelta<T> delta);
	
	/**
	 * Returns the number of elements
	 * @return the number of elements
	 */
	int size();
	
	/**
	 * Returns whether the list of deltas is empty
	 * @return true if the list is empty
	 */
	boolean isEmpty();
	
	/**
	 * Returns the timestamp from the start of this patch
	 * @return timestamp
	 */
	long getStartTime();
	
	/**
	 * 
	 * @param timestamp
	 */
	void setStartTime(long timestamp);
	
	/**
	 * 
	 * @return
	 */
	long getEndTime();
	
	/**
	 * 
	 * @param timestamp
	 */
	void setEndTime(long timestamp);
	
	/**
	 * 
	 * @return
	 */
	String getFeature();
	
	/**
	 * 
	 * @param context
	 */
	void setFeature(String feature);
	
}