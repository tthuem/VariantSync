package de.tubs.variantsync.core.view.targetfocus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.featureide.fm.core.configuration.Configuration;
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

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".view.targetfocus";

	private Combo cbVariant;
	private TreeViewer tvChanges;
	private Text lbChange;
	private Button btnSync;
	private TargetsCalculator targetsCalculator = new TargetsCalculator();
	private String project = "";
	private List<IDelta<?>> lastSelections = new ArrayList<>();
	private IFile lastResource = null;

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
		lblFeatureExpression.setText("Variant: ");
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		lblFeatureExpression.setLayoutData(gridData);

		cbVariant = new Combo(selectFeature, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		cbVariant.setLayoutData(gridData);
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		if (context != null) cbVariant.setItems(context.getProjectNames().toArray(new String[] {}));
		cbVariant.addSelectionListener(this);

		tvChanges = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 4;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tvChanges.setContentProvider(new TargetFocusTreeContentProvider());
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
		targets.setLayout(new GridLayout(2, false));
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		targets.setLayoutData(gridData);

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
		btnSync.setEnabled(false);
		btnSync.addSelectionListener(this);

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
		col.setText("Time");
		tvCol = new TreeViewerColumn(tvChanges, col);
		tvCol.setLabelProvider(new ResourceChangesColumnLabelProvider(TYPE.TIMESTAMP));
		layout.addColumnData(new ColumnWeightData(1, 250, true));
	}

	@Override
	public void setFocus() {
		cbVariant.setFocus();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource().equals(cbVariant)) {

			project = cbVariant.getItem(cbVariant.getSelectionIndex());
			updateTreeViewer(project);

		} else if (e.getSource().equals(btnSync)) {

			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			for (IDelta<?> delta : lastSelections) {
				SynchronizationHandler.handleSynchronization(iProject, delta);
			}
			VariantSyncPlugin.getDefault().fireEvent(new VariantSyncEvent(View.this, EventType.PATCH_CHANGED));

		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// CLEAR ALL
	}

	protected void updateTreeViewer(String project) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
				if (context != null) {
					List<IPatch<?>> patches = context.getPatches();
					IPatch<?> actualPatch = context.getActualContextPatch();
					if (actualPatch != null && !patches.contains(actualPatch)) patches.add(actualPatch);

					Configuration config = context.getConfigurationForProject(context.getProject(project));
					if (config != null) {
						Set<String> selectedFeatures = config.getSelectedFeatureNames();
						List<IPatch<?>> checkedPatches = new ArrayList<>();
						for (IPatch<?> patch : patches) {
							if (selectedFeatures.contains(patch.getFeature())) checkedPatches.add(patch);
						}

						if (patches != null && !patches.isEmpty()) tvChanges.setInput(FeatureTree.construct(project, checkedPatches));
						tvChanges.expandToLevel(3);
					}
				}
			}
		});
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		btnSync.setEnabled(false);
		lastSelections.clear();

		ITreeSelection selection = tvChanges.getStructuredSelection();
		// Only one element selected
		if (selection.size() == 1) {
			Object o = selection.getFirstElement();
			if (o instanceof TreeNode) o = ((TreeNode) o).getData();
			if (o instanceof IDelta) {
				IDelta<?> delta = ((IDelta<?>) o);
				lastSelections.add(delta);
				lastResource = delta.getResource();
				lbChange.setText(delta.getRepresentation());
				btnSync.setEnabled(true);
			} else {
				lbChange.setText("");
			}
			// Multiple elements selected
		} else {
			IFile res = null;
			String ret = "";
			for (Object o : selection.toList()) {
				if (o instanceof TreeNode) o = ((TreeNode) o).getData();
				if (o instanceof IDelta) {
					IDelta<?> delta = ((IDelta<?>) o);
					if (res == null) res = delta.getResource();
					if (!res.equals(delta.getResource())) {
						lbChange.setText("No multiple resources supported");
						return;
					}
					lastSelections.add(delta);
					lastResource = res;
					ret += ret.isEmpty() ? delta.getRepresentation() : "\n\n" + delta.getRepresentation();
					btnSync.setEnabled(true);
				}
			}
			lbChange.setText(ret);
		}
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case PATCH_ADDED:
		case PATCH_CHANGED:
		case PATCH_CLOSED:
		case CONTEXT_CHANGED:
		case INITALIZED:
			updateTreeViewer(project);
			break;
		case VARIANT_ADDED:
		case VARIANT_REMOVED:
			int oldSelection = cbVariant.getSelectionIndex();
			cbVariant.setItems(VariantSyncPlugin.getDefault().getActiveEditorContext().getFeatureExpressionsAsStrings().toArray(new String[] {}));
			cbVariant.select(oldSelection);
		default:
			break;
		}
	}
}
