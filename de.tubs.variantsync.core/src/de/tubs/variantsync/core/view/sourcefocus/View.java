package de.tubs.variantsync.core.view.sourcefocus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.syncronization.SynchronizationHandler;
import de.tubs.variantsync.core.syncronization.TargetsCalculator;
import de.tubs.variantsync.core.utilities.TreeNode;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;
import de.tubs.variantsync.core.view.resourcechanges.ResourceChangesColumnLabelProvider;
import de.tubs.variantsync.core.view.resourcechanges.ResourceChangesColumnLabelProvider.TYPE;

public class View extends ViewPart implements SelectionListener, ISelectionChangedListener, IEventListener {

	private Combo cbFeature;
	private TreeViewer tvChanges;
	private Text lbChange;
	private Button btnSync;
	private org.eclipse.swt.widgets.List targetsList;
	private TargetsCalculator targetsCalculator = new TargetsCalculator();
	private String feature = "";
	private List<IDelta<?>> lastSelections = new ArrayList<>();
	private IFile lastResource = null;
	private String[] lastTargets;

	public View() {
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		Composite selectFeature = new Composite(parent, SWT.NONE);
		selectFeature.setLayout(new GridLayout(4, false));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = false;
		selectFeature.setLayoutData(gridData);

		Label lblFeatureExpression = new Label(selectFeature, SWT.NONE);
		lblFeatureExpression.setText("Feature Expression: ");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		lblFeatureExpression.setLayoutData(gridData);

		cbFeature = new Combo(selectFeature, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		cbFeature.setLayoutData(gridData);
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		if (context != null)
			cbFeature.setItems(context.getFeatureExpressionsAsStrings().toArray(new String[] {}));
		cbFeature.select(0);
		cbFeature.addSelectionListener(this);

		tvChanges = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 4;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tvChanges.setContentProvider(new SourceFocusTreeContentProvider());
		tvChanges.addSelectionChangedListener(this);
		tvChanges.getTree().setLayoutData(gridData);
		setupTreeViewer(tvChanges.getTree());

		lbChange = new Text(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = 300;
		lbChange.setLayoutData(gridData);
		lbChange.setEditable(false);

		Composite targets = new Composite(parent, SWT.NONE);
		targets.setLayout(new GridLayout(1, false));
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		targets.setLayoutData(gridData);

		Label lblTargets = new Label(targets, SWT.NONE);
		lblTargets.setText("Targets:");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		lblTargets.setLayoutData(gridData);

		targetsList = new org.eclipse.swt.widgets.List(targets, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		targetsList.setLayoutData(gridData);
		targetsList.addSelectionListener(this);

		btnSync = new Button(targets, SWT.NONE);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.BOTTOM;
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		btnSync.setLayoutData(gridData);
		btnSync.setText("Synchronize");
		btnSync.addSelectionListener(this);
		btnSync.setEnabled(false);
	}

	protected void setupTreeViewer(final Tree tree) {
		tree.setLinesVisible(true);
		tree.setHeaderVisible(false);

		TableLayout layout = new TableLayout();
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		TreeColumn col = new TreeColumn(tree, SWT.None, 0);
		col.setText("Resource");
		TreeViewerColumn tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.DELTATYPE));
		layout.addColumnData(new ColumnWeightData(1, 300, true));

		col = new TreeColumn(tree, SWT.None, 1);
		col.setText("Synchronized Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSSYNCHRONIZED));
		layout.addColumnData(new ColumnWeightData(1, 250, true));

		col = new TreeColumn(tree, SWT.None, 2);
		col.setText("Automatic Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSWITHOUTCONFLICT));
		layout.addColumnData(new ColumnWeightData(1, 250, true));

		col = new TreeColumn(tree, SWT.None, 3);
		col.setText("Manual Targets");
		col.setResizable(true);
		tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TARGETSWITHCONFLICT));
		layout.addColumnData(new ColumnWeightData(1, 250, true));

		col = new TreeColumn(tree, SWT.None, 4);
		col.setText("Time");
		tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TIMESTAMP));
		layout.addColumnData(new ColumnWeightData(1, 250, true));
	}

	@Override
	public void setFocus() {
		cbFeature.setFocus();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource().equals(cbFeature)) {
			
			clearAll();
			feature = cbFeature.getItem(cbFeature.getSelectionIndex());
			updateTreeViewer(feature);
			
		} else if (e.getSource().equals(btnSync)) {
			
			for (String project : targetsList.getSelection()) {
				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
				for (IDelta<?> delta : lastSelections) {
					if (!delta.getProject().equals(iProject)) {
						SynchronizationHandler.handleSynchronization(iProject, delta);
					}
				}
			}
			VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(View.this, EventType.PATCH_CHANGED));
			
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// CLEAR ALL
	}

	protected void updateTreeViewer(String feature) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
				if (context != null) {
					List<IPatch<?>> patches = context.getPatches();
					IPatch<?> actualPatch = context.getActualContextPatch();
					if (actualPatch != null && !patches.contains(actualPatch))
						patches.add(actualPatch);

					if (patches != null && !patches.isEmpty() && !tvChanges.getControl().isDisposed()) {
						tvChanges.setInput(ProjectTree.construct(feature, patches));
					}
					tvChanges.expandToLevel(3);
				}
			}
		});
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		clearAll();
		lastSelections.clear();

		ITreeSelection selection = tvChanges.getStructuredSelection();
		if (selection.size() == 1) {
			Object o = selection.getFirstElement();
			if (o instanceof TreeNode)
				o = ((TreeNode) o).getData();
			if (o instanceof IDelta) {
				IDelta<?> delta = ((IDelta<?>) o);
				lastSelections.add(delta);
				lastResource = delta.getResource();
				lbChange.setText(delta.getRepresentation());
				List<IProject> targets = targetsCalculator.getTargetsForFeatureExpression(delta.getFeature());
				if (targets != null) {
					targetsList.setItems(getProjectNames(targets).toArray(new String[] {}));
					if (!targets.isEmpty())
						btnSync.setEnabled(true);
				}
			} else {
				lbChange.setText("");
			}
		} else {
			IFile res = null;
			String ret = "";
			for (Object o : selection.toList()) {
				if (o instanceof TreeNode)
					o = ((TreeNode) o).getData();
				if (o instanceof IDelta) {
					IDelta<?> delta = ((IDelta<?>) o);
					if (res == null)
						res = delta.getResource();
					if (!res.equals(delta.getResource())) {
						lbChange.setText("No multiple resources supported");
						return;
					}
					lastSelections.add(delta);
					lastResource = res;
					ret += ret.isEmpty() ? delta.getRepresentation() : "\n\n" + delta.getRepresentation();
				}
			}
			lbChange.setText(ret);
		}
	}

	private void clearAll() {
		btnSync.setEnabled(false);
		targetsList.removeAll();
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case PATCH_ADDED:
		case PATCH_CHANGED:
		case PATCH_CLOSED:
		case CONTEXT_CHANGED:
		case CONFIGURATIONPROJECT_CHANGED:
		case INITALIZED:
			updateTreeViewer(feature);
			break;
		case FEATUREEXPRESSION_ADDED:
		case FEATUREEXPRESSION_CHANGED:
		case FEATUREEXPRESSION_REMOVED:
			int oldSelection = cbFeature.getSelectionIndex();
			cbFeature.setItems(VariantSyncPlugin.getDefault().getActiveEditorContext().getFeatureExpressionsAsStrings()
					.toArray(new String[] {}));
			cbFeature.select(oldSelection);
		default:
			break;
		}
	}

	private List<String> getProjectNames(List<IProject> projects) {
		List<String> projectNames = new ArrayList<>();
		for (IProject project : projects) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

}
