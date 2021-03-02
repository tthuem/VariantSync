package de.tubs.variantsync.core.view.resourcechanges;

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.view.resourcechanges.ResourceChangesColumnLabelProvider.TYPE;

/**
 *
 * Resource changes view
 *
 * @author Christopher Sontag
 */
public class View extends ViewPart implements IEventListener {

	public static final String ID = String.format("%s.views.resourcechanges", VariantSyncPlugin.PLUGIN_ID);
	private TreeViewer tvResourceChanges;

	public View() {
		super();
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		tvResourceChanges = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		setupTreeViewer(tvResourceChanges.getTree());

		tvResourceChanges.setContentProvider(new ResourceChangesTreeContentProvider());

		updateTreeViewer();
	}

	protected void setupTreeViewer(final Tree tree) {
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		final TableLayout layout = new TableLayout();
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		TreeColumn col = new TreeColumn(tree, SWT.None, 0);
		col.setText("Resource");
		col.setResizable(true);
		TreeViewerColumn tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.DELTATYPE));
		layout.addColumnData(new ColumnWeightData(2));

		col = new TreeColumn(tree, SWT.None, 1);
		col.setText("Source");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.SOURCE));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 2);
		col.setText("Synchronized Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSSYNCHRONIZED));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 3);
		col.setText("Automatic Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSWITHOUTCONFLICT));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 4);
		col.setText("Manual Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSWITHCONFLICT));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 5);
		col.setText("Time");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TIMESTAMP));
		layout.addColumnData(new ColumnWeightData(1));
	}

	protected void updateTreeViewer() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
				if (configurationProject != null) {
					final PatchesManager patchesManager = configurationProject.getPatchesManager();
					final List<IPatch<?>> patches = patchesManager.getPatches();
					final IPatch<?> actualPatch = patchesManager.getActualContextPatch();
					if ((actualPatch != null) && !patches.contains(actualPatch)) {
						patches.add(actualPatch);
					}

					if ((patches != null) && !patches.isEmpty()) {
						tvResourceChanges.setInput(ResourcesTree.construct(patches));
					}
					tvResourceChanges.expandToLevel(3);
				}
			}
		});
	}

	@Override
	public void setFocus() {
		tvResourceChanges.getControl().setFocus();
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case PATCH_ADDED:
		case PATCH_CHANGED:
		case PATCH_CLOSED:
		case INITALIZED:
		case CONTEXT_CHANGED:
		case CONFIGURATIONPROJECT_CHANGED:
			updateTreeViewer();
			break;
		default:
			break;
		}
	}

}
