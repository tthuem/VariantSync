package de.tubs.variantsync.core.patch.interfaces;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;

import com.github.difflib.algorithm.DiffException;

import de.ovgu.featureide.fm.core.IExtension;
import de.tubs.variantsync.core.patch.interfaces.IDelta.DELTATYPE;

/**
 * Factory to create patches from files
 *
 * @author Christopher Sontag
 * @version 1.0
 * @since 15.08.2017
 * @param <T> file element, e.g. line, ast element, ...
 */
public interface IDeltaFactory<T> extends IExtension {

	public static String extensionPointID = "DeltaFactory";

	public static String extensionID = "deltaFactory";

	String getName();

	/**
	 * Creates a empty delta object
	 * 
	 * @param res
	 * @return
	 */
	IDelta<T> createDelta(IFile file);

	/**
	 * Creates a delta object for a resource
	 * 
	 * @param res - resource
	 * @param timestamp - timestamp
	 * @param kind - type of change
	 * @return patch object
	 */
	List<IDelta<T>> createDeltas(IFile file, long timestamp, DELTATYPE kind) throws DiffException;

	/**
	 * Creates patch object from a changed resource.
	 * 
	 * @param res - resource
	 * @param oldState - last history state
	 * @param kind - type of change
	 * @return patch object
	 */
	List<IDelta<T>> createDeltas(IFile file, IFileState oldState, long timestamp, DELTATYPE kind) throws DiffException;

	/**
	 * Patches a resource with a given patch.
	 * 
	 * @param res - resource
	 * @param patch - patch
	 * @return patched temp resource
	 */
	IFile applyDelta(IFile file, IDelta<T> patch);

	/**
	 * Unpatches a revised resource for a given patch.
	 * 
	 * @param res - the resource
	 * @param patch - patch
	 * @return original resource
	 */
	IFile reverseDelta(IFile file, IDelta<T> patch);

	/**
	 * Verifies that the given patch can be applied to the given resource
	 * 
	 * @param res - the resource
	 * @param delta - the patch to verify
	 * @return true if the patch can be applied
	 */
	boolean verifyDelta(IFile file, IDelta<T> delta);

	/**
	 * Checks whether the file is supported
	 * 
	 * @param file
	 * @return true if the file is supported
	 */
	boolean isSupported(IFile file);

	IMarkerHandler getMarkerHandler();
//
//	public boolean checkConflict(List<IDelta> leftDeltas, List<IDelta> rightDeltas);
//
//	public boolean checkConflict(List<String> ancestorLines, List<String> leftLines, List<String> rightLines);
//
//	public List<String> performThreeWayMerge(IFile ancestor, IFile left, IFile right);
//
//	public List<IDelta> getConflictingDeltas(List<String> ancestorLines, List<String> leftLines, List<String> rightLines);

}
