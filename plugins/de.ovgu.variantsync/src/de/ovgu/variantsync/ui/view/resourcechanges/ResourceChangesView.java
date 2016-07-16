package de.ovgu.variantsync.ui.view.resourcechanges;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.ControllerProperties;
import de.ovgu.variantsync.ui.controller.ControllerTypes;
import de.ovgu.variantsync.ui.controller.DeltaController;
import de.ovgu.variantsync.ui.controller.ProjectController;
import de.ovgu.variantsync.ui.controller.SynchronizationController;
import de.ovgu.variantsync.ui.view.AbstractView;
import de.ovgu.variantsync.ui.view.mergeprocess.ProjectSelectionDialog;
import de.ovgu.variantsync.ui.view.mergeprocess.SynchroFilter;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesView extends ViewPart implements AbstractView {

	public static final String ID = "de.ovgu.variantsync.presentationlayer.view.resourcechanges.ResourceChanges";

	private TreeViewer viewer;
	private Action synchroFilterAction;
	private Action doubleClickAction;
	private ViewerFilter synchroFilter;
	private static ResourceChangesView resourceChangesView;
	private ResourceChangesFilePatch selectedfilePatch;
	private ProjectController projectController = ControllerHandler
			.getInstance().getProjectController();
	private SynchronizationController synchronizationController = ControllerHandler
			.getInstance().getSynchronizationController();
	private DeltaController deltaController = ControllerHandler.getInstance()
			.getDeltaController();
	private IWorkspaceRoot root;

	public ResourceChangesView() {
		resourceChangesView = this;
		VariantSyncPlugin.getDefault().registerView(this,
				ControllerTypes.MONITOR);
		VariantSyncPlugin.getDefault().registerView(this,
				ControllerTypes.PROJECT);
		VariantSyncPlugin.getDefault().registerView(this,
				ControllerTypes.SYNCHRONIZATION);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		viewer.getTree().setLinesVisible(true);
		createColumns(viewer.getTree());
		viewer.setContentProvider(new ResourceChangesContentProvider());
		viewer.setInput(this);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "de.ovgu.variantsync.viewer");
		synchroFilter = new SynchroFilter();
		viewer.addFilter(synchroFilter);
		makeActions();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	protected void createColumns(final Tree tree) {
		TableLayout layout = new TableLayout();
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		for (int i = 0; i < 4; i++) {

			if (i == 1) {
				layout.addColumnData(new ColumnPixelData(100, true));
			} else {
				layout.addColumnData(new ColumnPixelData(300, true));
			}
			TreeColumn column = new TreeColumn(tree, SWT.NONE, i);
			if (i == 0) {
				column.setText("Resource");
			}
			if (i == 1) {
				column.setText("Project");
			}
			if (i == 2) {
				column.setText("Possible Targets");
			}
			if (i == 3) {
				column.setText("Targets");
			}
			column.setResizable(true);
			TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, column);
			viewerColumn
					.setLabelProvider(new ResourceChangesColumnLabelProvider(i));
		}
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(synchroFilterAction);
	}

	private void makeActions() {
		synchroFilterAction = new Action() {
			public void run() {
				synchroFilterAction
						.setToolTipText("Hide with tool synchronized changes");
				updateFilter(synchroFilterAction);
				refreshTree();
			}
		};
		synchroFilterAction.setChecked(true);
		synchroFilterAction.setText("ChangesFilter");
		synchroFilterAction
				.setToolTipText("Show with tool synchronized changes");
		synchroFilterAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof ResourceChangesFilePatch) {
					ResourceChangesFilePatch filePatch = (ResourceChangesFilePatch) obj;
					selectedfilePatch = filePatch;
					projectController.getProjectList(filePatch);
				}
			}
		};
	}

	protected void updateFilter(Action action) {
		if (action.isChecked()) {
			viewer.addFilter(synchroFilter);
		} else {
			viewer.removeFilter(synchroFilter);
		}
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public static ResourceChangesView getViewPart() {
		return resourceChangesView;
	}

	public void refreshTree() {
		this.viewer.refresh();
	}

	public static ResourceChangesView getDefault() {
		return resourceChangesView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				ControllerProperties.PROJECTLIST_PROPERTY.getProperty())) {
			root = new ProjectRoot((ArrayList<IProject>) evt.getNewValue());
			deltaController.getChanges(selectedfilePatch);
		} else if (evt.getPropertyName().equals(
				ControllerProperties.UNIFIEDDIFF_PROPERTY.getProperty())) {
			ProjectSelectionDialog dlg = new ProjectSelectionDialog(viewer
					.getControl().getShell(), root,
					new BaseWorkbenchContentProvider(),
					new WorkbenchLabelProvider(),
					"Select the project to be synchronized:",
					(String) evt.getNewValue());
			dlg.setTitle("Project Selection");
			dlg.open();
			Object[] result = dlg.getResult();
			if (result != null && result.length > 0) {
				synchronizationController
						.synchronize(result, selectedfilePatch);
			}
		} else if (evt.getPropertyName().equals(
				ControllerProperties.REFRESHTREE_PROPERTY.getProperty())) {
			refreshTree();
		}
	}

	/**
	 * @return the filePatch
	 */
	public ResourceChangesFilePatch getSelectedFilePatch() {
		return selectedfilePatch;
	}
}