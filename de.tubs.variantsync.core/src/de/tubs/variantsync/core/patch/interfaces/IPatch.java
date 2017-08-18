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
	void addAll(List<IDelta<T>> deltas);
	
	/**
	 * Returns the list of all deltas
	 * @return the deltas
	 */
	List<IDelta<T>> getDeltas();
	
	/**
	 * Returns a visualization for this patch
	 * @return a string visualization
	 */
	String toString();
	
}