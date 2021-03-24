package de.tubs.variantsync.core.view.sourcefocus;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
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

/**
 *
 * Source-focused view
 *
 * @author Christopher Sontag
 */
public class View extends ViewPart implements SelectionListener, ISelectionChangedListener, IEventListener {

	private static class ComboBoxSelectionDispatcher implements Runnable {

		private final Combo combobox;
		public int selectionIndex = -1;

		public ComboBoxSelectionDispatcher(Combo combobox) {
			this.combobox = combobox;
		}

		@Override
		public void run() {
			selectionIndex = combobox.getSelectionIndex();
		}
	}

	public static final String ID = String.format("%s.views.sourcefocus", VariantSyncPlugin.PLUGIN_ID);

	private Combo cbFeature;
	private TreeViewer tvChanges;
	private SourceViewer lbChange;
	private Button btnSync;
	private org.eclipse.swt.widgets.List targetsList;
	private final TargetsCalculator targetsCalculator = new TargetsCalculator();
	private String feature = "";
	private final List<IDelta<?>> lastSelections = new ArrayList<>();

	public View() {
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		final Composite selectFeature = new Composite(parent, SWT.NONE);
		selectFeature.setLayout(new GridLayout(4, false));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = false;
		selectFeature.setLayoutData(gridData);

		final Label lblFeatureExpression = new Label(selectFeature, SWT.NONE);
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
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		if (configurationProject != null) {
			cbFeature.setItems(configurationProject.getFeatureContextManager().getContextsAsStrings().toArray(new String[] {}));
		}
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

		final CompositeRuler ruler = new CompositeRuler();
		final LineNumberRulerColumn lineNumber = new LineNumberRulerColumn();
		ruler.addDecorator(0, lineNumber);

		lbChange = new SourceViewer(parent, ruler, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FLAT);
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = 300;
		lbChange.getControl().setLayoutData(gridData);
		lbChange.setEditable(false);

		final Composite targets = new Composite(parent, SWT.NONE);
		targets.setLayout(new GridLayout(1, false));
		gridData = new GridData();
		gridData.verticalAlignment = SWT.FILL;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		targets.setLayoutData(gridData);

		final Label lblTargets = new Label(targets, SWT.NONE);
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

		final TableLayout layout = new TableLayout();
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

			for (final String project : targetsList.getSelection()) {
				final IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
				for (final IDelta<?> delta : lastSelections) {
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

					if ((patches != null) && !patches.isEmpty() && !tvChanges.getControl().isDisposed()) {
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

		final ITreeSelection selection = tvChanges.getStructuredSelection();
		if (selection.size() == 1) {
			Object o = selection.getFirstElement();
			if (o instanceof TreeNode) {
				o = ((TreeNode) o).getData();
			}
			if (o instanceof IDelta) {
				final IDelta<?> delta = ((IDelta<?>) o);
				lastSelections.add(delta);
				lbChange.setDocument(new Document(delta.getRepresentation()));
			} else {
				lbChange.setDocument(new Document(""));
			}
		} else {
			IPath res = null;
			String ret = "";
			for (Object o : selection.toList()) {
				if (o instanceof TreeNode) {
					o = ((TreeNode) o).getData();
				}
				if (o instanceof IDelta) {
					final IDelta<?> delta = ((IDelta<?>) o);
					if (res == null) {
						res = delta.getResource().getProjectRelativePath();
					}
					if (!res.equals(delta.getResource().getProjectRelativePath())) {
						lbChange.setDocument(new Document("No multiple resources supported"));
						return;
					}
					lastSelections.add(delta);
					if (ret.isEmpty()) {
						ret += delta.getRepresentation();
					} else {
						ret += String.format("%n%n%s", delta.getRepresentation());
					}
				}
			}
			lbChange.setDocument(new Document(ret));
		}
		updateTargets();
	}

	private void updateTargets() {
		final List<IProject> targets = targetsCalculator.getTargetsForFeatureContext(lastSelections);
		if ((targets != null) && !targetsList.isDisposed()) {
			targetsList.setItems(getProjectNames(targets).toArray(new String[] {}));
			if (!targets.isEmpty()) {
				btnSync.setEnabled(true);
			}
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
			updateTargets();
			break;
		case FEATURECONTEXT_ADDED:
		case FEATURECONTEXT_CHANGED:
		case FEATURECONTEXT_REMOVED:
			final ComboBoxSelectionDispatcher dispatcher = new ComboBoxSelectionDispatcher(cbFeature);
			Display.getDefault().syncExec(dispatcher);
			cbFeature.setItems(VariantSyncPlugin.getActiveFeatureContextManager().getContextsAsStrings().toArray(new String[] {}));
			cbFeature.select(dispatcher.selectionIndex);
		default:
			break;
		}
	}

	private List<String> getProjectNames(List<IProject> projects) {
		final List<String> projectNames = new ArrayList<>();
		for (final IProject project : projects) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

}
