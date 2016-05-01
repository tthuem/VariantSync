package de.ovgu.variantsync.applicationlayer.deltacalculation;

import java.util.List;

import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.applicationlayer.datamodel.exception.PatchException;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import difflib.Patch;

/**
 * Defines functions to computes difference (delta) of a resource.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.05.2015
 */
public interface IDeltaOperations {

	/**
	 * Computes string describing changes between two files or one file in
	 * different versions and sends this string to registered listeners.
	 * 
	 * @param filePatch
	 *            patch of two files or one file in different versions
	 * @return changes of two files described in filePatch object
	 */
	String getChanges(ResourceChangesFilePatch filePatch);

	/**
	 * Computes unified difference of a changed file.
	 * 
	 * @param changedFile
	 *            changed file
	 * @return changes as string
	 */
	String getUnifieddiff(ResourceChangesFilePatch changedFile);

	/**
	 * Computes difference (delta) between actual and ancient version of changed
	 * file.
	 * 
	 * @param res
	 *            changed resource
	 */
	void createPatch(IResource res);

	/**
	 * Creates patch object from changed file.
	 * 
	 * @param changedFile
	 *            changed file
	 * @return patch object
	 */
	Patch getPatch(ResourceChangesFilePatch changedFile);

	/**
	 * Patches original text with given patch.
	 * 
	 * @param content
	 *            original text
	 * @param patch
	 *            given patch
	 * @return revised text
	 * @throws PatchException
	 *             patch could not be applied
	 */
	List<String> computePatch(List<String> content, Patch patch)
			throws PatchException;

	/**
	 * Computes the difference between original and revised list of string
	 * elements.
	 * 
	 * @param originalElements
	 *            original text as list of string elements
	 * @param revisedElements
	 *            revised text as list of string elements
	 * @return PatchWrapper-object describing difference between original and
	 *         revised sequences.
	 */
	Patch computeDifference(List<String> content1, List<String> content2);

	/**
	 * Unpatches revised text for a given patch.
	 * 
	 * @param content
	 *            revised text
	 * @param patch
	 *            given patch
	 * @return original text
	 */
	List<String> unpatchText(List<String> content, Patch patch);

	List<String> createUnifiedDifference(String filename, String filename2,
			List<String> oldCode, Patch patch, int i);
}
