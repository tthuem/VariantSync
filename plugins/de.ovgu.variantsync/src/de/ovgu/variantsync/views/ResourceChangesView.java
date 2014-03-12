package de.ovgu.variantsync.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.*;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import de.ovgu.variantsync.model.IResourceChangesViewItem;
import de.ovgu.variantsync.model.ResourceChangesFile;
import de.ovgu.variantsync.model.ResourceChangesFilePatch;
import de.ovgu.variantsync.model.ResourceChangesFolder;

/**
 * 
 * @author Lei Luo
 * 
 */
public class ResourceChangesView extends ViewPart {

	public static final String ID = "de.ovgu.variantsync.views.ResourceChanges";

	private TreeViewer viewer;
	private Action synchroFilterAction;
	// private Action action2;
	private Action doubleClickAction;
	private ViewerFilter synchroFilter;
	private static ResourceChangesView resourceChangesView;
	private ResourceChangesFilePatch selectedFilePatch;

	/**
	 * The constructor.
	 */
	public ResourceChangesView() {
		resourceChangesView = this;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		// viewer.setSorter(new NameSorter());
		viewer.getTree().setLinesVisible(true);
		createColumns(viewer.getTree());
		viewer.setContentProvider(new ResourceChangesContentProvider());
		// viewer.setLabelProvider(new ResourceChangesLabelProvider());
		viewer.setInput(this);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "de.ovgu.variantsync.viewer");
		synchroFilter = new SynchroFilter();
		viewer.addFilter(synchroFilter);
		makeActions();
		// hookContextMenu();
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

			// tc.setImage(field.getColumnHeaderImage());
			// tc.setResizable(columnWidths[i].resizable);
			column.setResizable(true);
			// column.setMoveable(true);
			// tc.addSelectionListener(getHeaderListener());
			// tc.setData(field);
			TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, column);
			viewerColumn.setLabelProvider(new ResourceChangesColumnLabelProvider(i));
			// viewerColumn.setLabelProvider(new
			// MarkerViewLabelProvider(field));
		}

		// int[] order = restoreColumnOrder(memento);
		// if (order != null && order.length == fields.length) {
		// tree.setColumnOrder(order);
		// }
	}

	// private void hookContextMenu() {
	// MenuManager menuMgr = new MenuManager("#PopupMenu");
	// menuMgr.setRemoveAllWhenShown(true);
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager manager) {
	// ResourceChanges.this.fillContextMenu(manager);
	// }
	// });
	// Menu menu = menuMgr.createContextMenu(viewer.getControl());
	// viewer.getControl().setMenu(menu);
	// getSite().registerContextMenu(menuMgr, viewer);
	// }

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		// fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	// private void fillLocalPullDown(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(new Separator());
	// manager.add(action2);
	// }

	// private void fillContextMenu(IMenuManager manager) {
	// manager.add(action1);
	// manager.add(action2);
	// manager.add(new Separator());
	// // Other plug-ins can contribute there actions here
	// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	// }

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(synchroFilterAction);
		// manager.add(new Separator());
		// manager.add(action2);
	}

	private void makeActions() {
		synchroFilterAction = new Action() {
			public void run() {
				synchroFilterAction.setToolTipText("Hide with tool synchronized changes");
				updateFilter(synchroFilterAction);
				refreshTree();
			}
		};
		synchroFilterAction.setChecked(true);
		synchroFilterAction.setText("ChangesFilter");
		synchroFilterAction.setToolTipText("Show with tool synchronized changes");
		synchroFilterAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		// action2 = new Action() {
		// public void run() {
		// showMessage("Action 2 executed");
		// }
		// };
		// action2.setText("Action 2");
		// action2.setToolTipText("Action 2 tooltip");
		// action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
		// .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof ResourceChangesFilePatch) {
					ResourceChangesFilePatch filePatch = (ResourceChangesFilePatch) obj;
					selectedFilePatch = filePatch;
					ArrayList<IProject> supportProjectList = filePatch.getProjectList();
//					if (supportProjectList.size() > 0) {
						IWorkspaceRoot root = new ProjectRoot(supportProjectList);
						ProjectSelectionDialog dlg = new ProjectSelectionDialog(viewer
								.getControl().getShell(), root,
								new BaseWorkbenchContentProvider(),
								new WorkbenchLabelProvider(),
								"Select the project to be synchronized:");
						dlg.setTitle("Project Selection");
						dlg.open();
						Object[] result = dlg.getResult();
						if (result != null && result.length > 0) {
							filePatch.synchronize(result);
						}
//					} else {
////						showMessage("No project can be synchronized");
//					}
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

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Info", message);
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

	public ResourceChangesFilePatch getSelectedFilePatch() {
		return this.selectedFilePatch;
	}
}