package de.tubs.variantsync.core.view.context;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
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
import de.tubs.variantsync.core.managers.FeatureContextManager;
import de.tubs.variantsync.core.managers.data.FeatureContext;
import de.tubs.variantsync.core.monitor.CodeMappingHandler;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

/**
 * Manual mapping action available in the editor
 * 
 * @author Christopher Sontag
 */
public class DynamicContextPopupItems extends ContributionItem implements IEventListener {

	public static Image base = VariantSyncPlugin.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID, "icons/public_co.gif").createImage();
	public static Image composed = VariantSyncPlugin.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID, "icons/protected_co.gif").createImage();

	@Override
	public void fill(final Menu menu, int index) {
		List<FeatureContext> features = VariantSyncPlugin.getActiveFeatureContextManager().getContexts();
		for (final FeatureContext fe : features) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(fe.name);
			menuItem.setImage(fe.isComposed() ? composed : base);

			if (!fe.name.contains(FeatureContextManager.DEFAULT_CONTEXT_NAME)) {
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

	private void handleSelection(FeatureContext fe) {
		IEditorPart editorPart = VariantSyncPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart instanceof AbstractTextEditor) {
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
						offset = ((ITextSelection) iSelection).getOffset();
						length = ((ITextSelection) iSelection).getLength();

						IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);

						if (length > 0) {
							// Add mapping to file
							CodeMappingHandler.addCodeMappings(file, fe.name, offset, length, selectedText);
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
		case FEATURECONTEXT_ADDED:
			update();
			break;
		default:
			break;

		}
	}

}
