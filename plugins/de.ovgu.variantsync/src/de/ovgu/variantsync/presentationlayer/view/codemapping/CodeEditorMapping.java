package de.ovgu.variantsync.presentationlayer.view.codemapping;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

/**
 * Realizes java-code to feature mapping. Code can be selected in active code
 * editor.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 04.06.2015
 */
public class CodeEditorMapping extends DynamicMapMenu {

	@Override
	protected IProject getProject() {
		IWorkbench iworkbench = PlatformUI.getWorkbench();
		IWorkbenchWindow iworkbenchwindow = iworkbench
				.getActiveWorkbenchWindow();
		IWorkbenchPage iworkbenchpage = iworkbenchwindow.getActivePage();
		IEditorPart ieditorpart = iworkbenchpage.getActiveEditor();
		IEditorInput input = ieditorpart.getEditorInput();
		IFile file = ((IFileEditorInput) input).getFile();
		return file.getProject();
	}

	@Override
	protected void handleSelection(String feature) {
		IEditorPart editorPart = VariantSyncPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart instanceof AbstractTextEditor) {
			int startLine = 0;
			int endLine = 0;
			int offset = 0;
			int length = 0;
			String selectedText = null;
			IEditorSite iEditorSite = editorPart.getEditorSite();
			if (iEditorSite != null) {
				ISelectionProvider selectionProvider = iEditorSite
						.getSelectionProvider();
				if (selectionProvider != null) {
					ISelection iSelection = selectionProvider.getSelection();
					if (!iSelection.isEmpty()) {
						selectedText = ((ITextSelection) iSelection).getText();
						startLine = ((ITextSelection) iSelection)
								.getStartLine();
						endLine = ((ITextSelection) iSelection).getEndLine();
						offset = ((ITextSelection) iSelection).getOffset();
						length = ((ITextSelection) iSelection).getLength();
						String title = iEditorSite.getPage().getActiveEditor()
								.getTitle();
						IFile file = (IFile) editorPart.getEditorInput()
								.getAdapter(IFile.class);
						IPath path = file.getRawLocation().makeAbsolute();
						// MarkerInformation mi = new MarkerInformation(0,
						// startLine, endLine, offset, length);
						// IMarker marker = null;
						try {
							CodeMarkerFactory.createMarker(String.valueOf(0),
									file, offset, offset + length, feature,
									CodeHighlighting.YELLOW);
						} catch (CoreException e) {
							e.printStackTrace();
						}
						// System.out
						// .println("\nOOOOOOOOOOOOOOOO OFFSET OOOOOOOOOOOOOOOO = "
						// + offset + ", " + length);
						// MarkerHandler.getInstance().addMarker(mi, marker,
						// file,
						// feature);

						controller.addFeatureMapping(feature, title,
								JavaElements.CODE_FRAGMENT, path, selectedText,
								startLine, endLine, offset);
					}
				}
			}

		}
	}
}