package de.tubs.variantsync.core.patch.interfaces;

import org.eclipse.core.resources.IFile;

/**
 * Defines functions to apply difference (delta) of a resource.
 *
 * @author Christopher Sontag
 * @version 1.0
 * @since 15.08.2017
 */
public interface IPatcher {

	/**
	 * Patches a resource with a given patch.
	 * 
	 * @param res - resource
	 * @param patch - patch
	 * @return patched temp resource
	 */
	IFile patch(IFile res, IPatch<?> patch);
	
	/**
	 * Unpatches revised text for a given patch.
	 * 
	 * @param content
	 *            revised text
	 * @param patch
	 *            given patch
	 * @return original text
	 */
	IFile reversePatch(IFile res, IPatch<?> patch);
}
