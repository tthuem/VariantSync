package de.tubs.variantsync.core.view.resourcechanges;

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

public class View extends ViewPart implements IEventListener {

	public static final String ID = "de.tubs.variantsync.core.view.resourcechanges";
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
		
		update();
//		synchroFilter = new SynchroFilter();
//		viewer.addFilter(synchroFilter);
//		makeActions();
//		hookDoubleClickAction();
//		contributeToActionBars();
	}

	protected void setupTreeViewer(final Tree tree) {
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TableLayout layout = new TableLayout();
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		TreeColumn col = new TreeColumn(tree, SWT.None, 0);
		col.setText("Resource");
		col.setResizable(true);
		TreeViewerColumn tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(0));
		layout.addColumnData(new ColumnWeightData(2));

		col = new TreeColumn(tree, SWT.None, 1);
		col.setText("Project");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(1));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 2);
		col.setText("Possible Target");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(2));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 3);
		col.setText("Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(3));
		layout.addColumnData(new ColumnWeightData(1));

		col = new TreeColumn(tree, SWT.None, 4);
		col.setText("Time");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvResourceChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(4));
		layout.addColumnData(new ColumnWeightData(1));
	}

	protected void update() {
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		if (context != null) {
			List<IPatch<?>> patches = context.getPatches();
			IPatch<?> actualPatch = context.getActualContextPatch();
			if (actualPatch != null && !patches.contains(actualPatch)) patches.add(actualPatch);

			if (patches != null && !patches.isEmpty()) tvResourceChanges.setInput(ResourcesTree.construct(patches));
			tvResourceChanges.expandToLevel(3);
		}
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
			update();
			break;
		default:
			break;
		}
	}

}
