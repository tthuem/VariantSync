package de.tubs.variantsync.core.view.resourcechanges;

import java.util.List;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;

public class View extends ViewPart implements IEventListener {

	public static final String ID = "de.tubs.variantsync.core.view.resourcechanges";
	private TreeViewer viewer;

	public View() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		//setupTreeViewer(viewer.getTree());

		viewer.setContentProvider(new ResourceChangesTreeContentProvider());
		viewer.setLabelProvider(new ResourceChangesTreeLabelProvider());

		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		List<IPatch<?>> patches = context.getPatches();
 
		IPatch<?> actualPatch = context.getActualContextPatch();
		if (actualPatch != null) patches.add(actualPatch);

		if (patches != null && !patches.isEmpty()) viewer.setInput(patches);

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

		TreeColumn col = new TreeColumn(tree, SWT.None);
		col.setText("Resource");
		col.setResizable(true);
		new TreeViewerColumn(viewer, col);

		col = new TreeColumn(tree, SWT.None);
		col.setText("Project");
		col.setResizable(true);
		new TreeViewerColumn(viewer, col);

		col = new TreeColumn(tree, SWT.None);
		col.setText("Possible Target");
		col.setResizable(true);
		new TreeViewerColumn(viewer, col);

		col = new TreeColumn(tree, SWT.None);
		col.setText("Targets");
		col.setResizable(true);
		new TreeViewerColumn(viewer, col);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case PATCH_ADDED:
		case PATCH_CHANGED:
		case PATCH_CLOSED:
			Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
			List<IPatch<?>> patches = context.getPatches();
			IPatch<?> actualPatch = context.getActualContextPatch();
			if (actualPatch != null) patches.add(actualPatch);

			if (patches != null && !patches.isEmpty()) viewer.setInput(patches);
			break;
		default:
			break;
		}
	}

}

/*
 * import java.beans.PropertyChangeEvent; import java.util.ArrayList; import org.eclipse.core.resources.IProject; import
 * org.eclipse.core.resources.IWorkspaceRoot; import org.eclipse.jface.action.Action; import org.eclipse.jface.action.IToolBarManager; import
 * org.eclipse.jface.viewers.ColumnPixelData; import org.eclipse.jface.viewers.DoubleClickEvent; import org.eclipse.jface.viewers.IDoubleClickListener; import
 * org.eclipse.jface.viewers.ISelection; import org.eclipse.jface.viewers.IStructuredSelection; import org.eclipse.jface.viewers.TableLayout; import
 * org.eclipse.jface.viewers.TreeViewer; import org.eclipse.jface.viewers.TreeViewerColumn; import org.eclipse.jface.viewers.ViewerFilter; import
 * org.eclipse.swt.SWT; import org.eclipse.swt.layout.FillLayout; import org.eclipse.swt.widgets.Composite; import org.eclipse.swt.widgets.Tree; import
 * org.eclipse.swt.widgets.TreeColumn; import org.eclipse.ui.IActionBars; import org.eclipse.ui.ISharedImages; import org.eclipse.ui.PlatformUI; import
 * org.eclipse.ui.model.BaseWorkbenchContentProvider; import org.eclipse.ui.model.WorkbenchLabelProvider; import org.eclipse.ui.part.ViewPart; import
 * de.ovgu.variantsync.VariantSyncPlugin; import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch; import
 * de.ovgu.variantsync.ui.controller.ControllerHandler; import de.ovgu.variantsync.ui.controller.ControllerProperties; import
 * de.ovgu.variantsync.ui.controller.ControllerTypes; import de.ovgu.variantsync.ui.controller.DeltaController; import
 * de.ovgu.variantsync.ui.controller.ProjectController; import de.ovgu.variantsync.ui.controller.SynchronizationController; import
 * de.ovgu.variantsync.ui.view.AbstractView; import de.ovgu.variantsync.ui.view.mergeprocess.ProjectSelectionDialog; import
 * de.ovgu.variantsync.ui.view.mergeprocess.SynchroFilter; public class ResourceChangesView extends ViewPart implements AbstractView { public static final
 * String ID = "de.ovgu.variantsync.presentationlayer.view.resourcechanges.ResourceChanges"; private TreeViewer viewer; private Action synchroFilterAction;
 * private Action doubleClickAction; private ViewerFilter synchroFilter; private static ResourceChangesView resourceChangesView; private
 * ResourceChangesFilePatch selectedfilePatch; private ProjectController projectController = ControllerHandler .getInstance().getProjectController(); private
 * SynchronizationController synchronizationController = ControllerHandler .getInstance().getSynchronizationController(); private DeltaController
 * deltaController = ControllerHandler.getInstance() .getDeltaController(); private IWorkspaceRoot root; public ResourceChangesView() { resourceChangesView =
 * this; VariantSyncPlugin.getDefault().registerView(this, ControllerTypes.MONITOR); VariantSyncPlugin.getDefault().registerView(this, ControllerTypes.PROJECT);
 * VariantSyncPlugin.getDefault().registerView(this, ControllerTypes.SYNCHRONIZATION); } public void createPartControl(Composite parent) { } protected void
 * createColumns(final Tree tree) { TableLayout layout = new TableLayout(); tree.setLayout(layout); tree.setHeaderVisible(true); for (int i = 0; i < 4; i++) {
 * if (i == 1) { layout.addColumnData(new ColumnPixelData(100, true)); } else { layout.addColumnData(new ColumnPixelData(300, true)); } TreeColumn column = new
 * TreeColumn(tree, SWT.NONE, i); if (i == 0) { column.setText("Resource"); } if (i == 1) { column.setText("Project"); } if (i == 2) {
 * column.setText("Possible Targets"); } if (i == 3) { column.setText("Targets"); } column.setResizable(true); TreeViewerColumn viewerColumn = new
 * TreeViewerColumn(viewer, column); viewerColumn .setLabelProvider(new ResourceChangesColumnLabelProvider(i)); } } private void contributeToActionBars() {
 * IActionBars bars = getViewSite().getActionBars(); fillLocalToolBar(bars.getToolBarManager()); } private void fillLocalToolBar(IToolBarManager manager) {
 * manager.add(synchroFilterAction); } private void makeActions() { synchroFilterAction = new Action() { public void run() { synchroFilterAction
 * .setToolTipText("Hide with tool synchronized changes"); updateFilter(synchroFilterAction); refreshTree(); } }; synchroFilterAction.setChecked(true);
 * synchroFilterAction.setText("ChangesFilter"); synchroFilterAction .setToolTipText("Show with tool synchronized changes");
 * synchroFilterAction.setImageDescriptor(PlatformUI.getWorkbench() .getSharedImages() .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK)); doubleClickAction
 * = new Action() { public void run() { ISelection selection = viewer.getSelection(); Object obj = ((IStructuredSelection) selection) .getFirstElement(); if
 * (obj instanceof ResourceChangesFilePatch) { ResourceChangesFilePatch filePatch = (ResourceChangesFilePatch) obj; selectedfilePatch = filePatch;
 * projectController.getProjectList(filePatch); } } }; } protected void updateFilter(Action action) { if (action.isChecked()) { viewer.addFilter(synchroFilter);
 * } else { viewer.removeFilter(synchroFilter); } } private void hookDoubleClickAction() { viewer.addDoubleClickListener(new IDoubleClickListener() { public
 * void doubleClick(DoubleClickEvent event) { doubleClickAction.run(); } }); } public void setFocus() { viewer.getControl().setFocus(); } public static
 * ResourceChangesView getViewPart() { return resourceChangesView; } public void refreshTree() { this.viewer.refresh(); } public static ResourceChangesView
 * getDefault() { return resourceChangesView; }
 * @SuppressWarnings("unchecked")
 * @Override public void modelPropertyChange(PropertyChangeEvent evt) { if (evt.getPropertyName().equals(
 * ControllerProperties.PROJECTLIST_PROPERTY.getProperty())) { root = new ProjectRoot((ArrayList<IProject>) evt.getNewValue());
 * deltaController.getChanges(selectedfilePatch); } else if (evt.getPropertyName().equals( ControllerProperties.UNIFIEDDIFF_PROPERTY.getProperty())) {
 * ProjectSelectionDialog dlg = new ProjectSelectionDialog(viewer .getControl().getShell(), root, new BaseWorkbenchContentProvider(), new
 * WorkbenchLabelProvider(), "Select the project to be synchronized:", (String) evt.getNewValue()); dlg.setTitle("Project Selection"); dlg.open(); Object[]
 * result = dlg.getResult(); if (result != null && result.length > 0) { synchronizationController .synchronize(result, selectedfilePatch); } } else if
 * (evt.getPropertyName().equals( ControllerProperties.REFRESHTREE_PROPERTY.getProperty())) { refreshTree(); } } public ResourceChangesFilePatch
 * getSelectedFilePatch() { return selectedfilePatch; } }
 */
