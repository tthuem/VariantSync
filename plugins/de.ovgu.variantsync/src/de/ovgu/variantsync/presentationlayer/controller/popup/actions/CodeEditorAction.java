package de.ovgu.variantsync.presentationlayer.controller.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import de.ovgu.variantsync.VariantSyncPlugin;

public class CodeEditorAction implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			// get editor
			IEditorPart editorPart = VariantSyncPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();

			if (editorPart instanceof AbstractTextEditor) {
				int offset = 0;
				int length = 0;
				String selectedText = null;
				IEditorSite iEditorSite = editorPart.getEditorSite();
				if (iEditorSite != null) {
					// get selection provider
					ISelectionProvider selectionProvider = iEditorSite
							.getSelectionProvider();
					if (selectionProvider != null) {
						ISelection iSelection = selectionProvider
								.getSelection();
						// offset
						offset = ((ITextSelection) iSelection).getOffset();
						if (!iSelection.isEmpty()) {
							selectedText = ((ITextSelection) iSelection)
									.getText();
							// length
							length = ((ITextSelection) iSelection).getLength();
							MessageDialog.openInformation(shell,
									"Do Something Menu", "Length: " + length
											+ "    Offset: " + offset + "\n"
											+ "Content: " + selectedText);
						}
					}
				}

			}
		} catch (Exception e) {
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// not required
	}

}