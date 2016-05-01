package de.ovgu.variantsync.presentationlayer.view.mergeprocess.test;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Required when creating a JavaMergeViewer from the plugin.xml file.
 */
public class JavaContentViewerCreator implements IViewerCreator {

	public Viewer createViewer(final Composite parent, final CompareConfiguration mp) {
		return new JavaFormattingMergeViewer(parent, SWT.NULL, mp);
	}
}
