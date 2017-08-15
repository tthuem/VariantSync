package de.tubs.variantsync.core.patch.interfaces;

import org.eclipse.core.resources.IResource;

import de.tubs.variantsync.core.exceptions.PatchException;

/**
 * Defines functions to compute differences (deltas) of a resource.
 *
 * @author Christopher Sontag
 * @version 1.0
 * @since 15.08.2017
 */
public interface IPatchCalculator {

	/**
	 * Computes difference (delta) between actual and ancient version of changed
	 * file.
	 * 
	 * @param res
	 *            changed resource
	 */
	IPatch getPatch(IResource res) throws PatchException;

	/**
	 * Creates patch object from changed files.
	 * 
	 * @param changedFile
	 *            changed file
	 * @return patch object
	 */
	IPatch getPatch(IResource res1, IResource res2) throws PatchException;
	
}
