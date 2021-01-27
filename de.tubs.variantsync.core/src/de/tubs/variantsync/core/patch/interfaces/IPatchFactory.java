package de.tubs.variantsync.core.patch.interfaces;

import org.eclipse.core.resources.IFile;

public interface IPatchFactory {

	/**
	 * Create a new patch under the given context
	 * 
	 * @param context
	 * @return
	 */
	IPatch<IDelta<?>> createPatch(String context);

	/**
	 * NOTE: Use this only for calculations otherwise it could lead to misbehavior
	 * 
	 * @return {@link IPatch}
	 */
	IPatch<IDelta<?>> createPatch();

	/**
	 * Patches a resource with a given patch.
	 * 
	 * @param res   - resource
	 * @param patch - patch
	 * @return patched temp resource
	 */
	IFile applyDelta(IFile file, IPatch<?> patch);

}
