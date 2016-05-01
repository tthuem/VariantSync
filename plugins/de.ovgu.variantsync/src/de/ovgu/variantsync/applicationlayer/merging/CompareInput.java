package de.ovgu.variantsync.applicationlayer.merging;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.IProgressMonitor;

public class CompareInput extends CompareEditorInput {
	public CompareInput() {
		super(new CompareConfiguration());
	}

	protected Object prepareInput(IProgressMonitor pm) {
		CompareItem ancestor = new CompareItem("Common", "contents", 0);
		CompareItem left = new CompareItem("Left", "new contents", 1);
		CompareItem right = new CompareItem("Right", "old contents", 2);
		return new DiffNode(null, Differencer.CONFLICTING, ancestor, left,
				right);
	}
}