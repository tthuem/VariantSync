package de.tubs.variantsync.core.view.context;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;

public class DynamicContextPopupItems extends ContributionItem
		implements IEventListener {
	
	public static Image base = VariantSyncPlugin
			.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID, "icons/public_co.gif").createImage();
	public static Image composed = VariantSyncPlugin
			.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID, "icons/protected_co.gif").createImage();

	@Override
	public void fill(final Menu menu, int index) {
		
		List<FeatureExpression> features = VariantSyncPlugin.getContext().getFeatureExpressions();
		for (final FeatureExpression fe : features) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(fe.name);
			menuItem.setImage(fe.isComposed()?composed:base);
			
			if (!fe.name.contains(Context.DEFAULT_CONTEXT_NAME)) {
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						handleSelection(fe);
					}
				});
			} else {
				menuItem.setEnabled(false);
			}
		}
	}
	
	private void handleSelection(FeatureExpression fe) {
		IEditorPart editorPart = VariantSyncPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		if (editorPart instanceof AbstractTextEditor) {
			int startLine = 0;
			int endLine = 0;
			int offset = 0;
			int length = 0;
			String selectedText = null;
			IEditorSite iEditorSite = editorPart.getEditorSite();
			if (iEditorSite != null) {
				ISelectionProvider selectionProvider = iEditorSite.getSelectionProvider();
				if (selectionProvider != null) {
					ISelection iSelection = selectionProvider.getSelection();
					if (!iSelection.isEmpty()) {
						selectedText = ((ITextSelection) iSelection).getText();
						startLine = ((ITextSelection) iSelection).getStartLine() + 1;
						endLine = ((ITextSelection) iSelection).getEndLine() + 1;
						offset = ((ITextSelection) iSelection).getOffset();
						length = ((ITextSelection) iSelection).getLength();
						
						
						String title = iEditorSite.getPage().getActiveEditor().getTitle();
						IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
						IPath path = file.getRawLocation().makeAbsolute();
						String project = file.getProject().toString();

						if (length > 0) {
							//TODO: Implement Set Context
						System.out.println("Selected fe:"+fe+" for'"+selectedText+"' (startLine:"+startLine+",endLine:"+endLine+",offset:"+offset+",length:"+length+") in "+path.toOSString());
						}
					}
				}
			}

		}
	}
	
	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_SET:
		case FEATUREEXPRESSION_ADDED:
			update();
			break;
		default:
			break;
		
		}
	}

}
