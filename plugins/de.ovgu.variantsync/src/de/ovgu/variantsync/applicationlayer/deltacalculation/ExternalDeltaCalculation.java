package de.ovgu.variantsync.applicationlayer.deltacalculation;

import java.util.List;

import de.ovgu.variantsync.applicationlayer.datamodel.exception.PatchException;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

/**
 * Provides functions to compute deltas (differences) between original and
 * revised files. Encapsulates functions of external difflib.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 15.05.2015
 */
class ExternalDeltaCalculation {

	/**
	 * Parse the given text in unified format and creates the list of deltas for
	 * it.
	 *
	 * @param diff
	 *            the text in unified format
	 * @return the patch with deltas.
	 */
	public Patch createUnifiedDifference(List<String> content) {
		return DiffUtils.parseUnifiedDiff(content);
	}

	/**
	 * Takes a patch and some other arguments, returning the unified diff format
	 * text representing the patch.
	 *
	 * @param originalFile
	 *            filename of the original (unrevised file)
	 * @param revisedFile
	 *            filename of the revised file
	 * @param linesOriginalFile
	 *            lines of the original file
	 * @param patch
	 *            patch
	 * @param lineNumbers
	 *            number of lines of context output around each difference in
	 *            file
	 * @return list of strings representing the unified diff of patch argument
	 */
	public List<String> createUnifiedDifference(String originalFile,
			String revisedFile, List<String> linesOriginalFile, Patch patch,
			int lineNumbers) {
		return DiffUtils.generateUnifiedDiff(originalFile, revisedFile,
				linesOriginalFile, patch, lineNumbers);
	}

	/**
	 * Unpatch the revised text for a given patch.
	 *
	 * @param revised
	 *            the revised text
	 * @param patch
	 *            the given patch
	 * @return the original text
	 */
	@SuppressWarnings("unchecked")
	public List<String> unpatchText(List<String> content, Patch patch) {
		System.out.println("UNPATCHTEXT:");
		System.out.println("Content: " + content.toString());
		System.out.println("Patch: " + patch.toString());
		return (List<String>) DiffUtils.unpatch(content, patch);
	}

	/**
	 * Computes difference between original and revised list of elements.
	 *
	 * @param original
	 *            original text. Must not be {@code null}
	 * @param revised
	 *            revised text. Must not be {@code null}
	 * @return patch describing the difference between the original and revised
	 *         sequences. Never {@code null}
	 */
	public Patch computeDifference(List<String> content1, List<String> content2) {
		return DiffUtils.diff(content1, content2);
	}

	/**
	 * Patch original text with given patch.
	 *
	 * @param original
	 *            original text
	 * @param patch
	 *            given patch
	 * @return revised text
	 * @throws PatchException
	 *             patch cannot be applied
	 */
	@SuppressWarnings("unchecked")
	public List<String> computePatch(List<String> content, Patch patch)
			throws PatchException {
		try {
			return (List<String>) DiffUtils.patch(content, patch);
		} catch (PatchFailedException e) {
			throw new PatchException("Patch could not be computed in diffLib.",
					e);
		}
	}

}