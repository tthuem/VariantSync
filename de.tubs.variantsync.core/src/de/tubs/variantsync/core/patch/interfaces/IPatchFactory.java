package de.tubs.variantsync.core.patch.interfaces;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;

import de.ovgu.featureide.fm.core.IExtension;
import de.tubs.variantsync.core.exceptions.PatchException;

/**
 * Factory to create patches from files
 *
 * @author Christopher Sontag
 * @version 1.0
 * @since 15.08.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public interface IPatchFactory<T> extends IExtension {

	public static String extensionPointID = "PatchFactory";

	public static String extensionID = "patchFactory";
	
	String getName();
	
	IPatch<T> createPatch(String context);
	
	/**
	 * Creates a empty patch object for a resource
	 * 
	 * @param res - resource
	 */
	IDelta<T> createDelta(IResource res, long timestamp) throws PatchException;

	/**
	 * Creates patch object from a changed resource.
	 * 
	 * @param res - resource
	 * @param oldState - last history state
	 * @return patch object
	 */
	IDelta<T> createDelta(IResource res, IFileState oldState, long timestamp) throws PatchException;
	
	/**
	 * Patches a resource with a given patch.
	 * 
	 * @param res - resource
	 * @param patch - patch
	 * @return patched temp resource
	 */
	IFile applyDelta(IFile res, IDelta<T> patch);
	
	/**
	 * Unpatches a revised resource for a given patch.
	 * 
	 * @param res - the resource
	 * @param patch - patch
	 * @return original resource
	 */
	IFile reverseDelta(IFile res, IDelta<T> patch);
	
	/**
	 * Verifies that the given patch can be applied to the given resource
	 * 
	 * @param res - the resource
	 * @param patch - the patch to verify
	 * @return true if the patch can be applied
	 */
	boolean verifyDelta(IFile res, IDelta<T> patch);
	
}
