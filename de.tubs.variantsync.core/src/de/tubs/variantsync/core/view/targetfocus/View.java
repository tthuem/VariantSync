package de.tubs.variantsync.core.view.targetfocus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.PatchesManager;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;
import de.tubs.variantsync.core.syncronization.SynchronizationHandler;
import de.tubs.variantsync.core.utilities.TreeNode;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent.EventType;
import de.tubs.variantsync.core.view.resourcechanges.ResourceChangesColumnLabelProvider;
import de.tubs.variantsync.core.view.resourcechanges.ResourceChangesColumnLabelProvider.TYPE;

/**
 *
 * Target-focused view
 *
 * @author Christopher Sontag
 */
public class View extends ViewPart implements SelectionListener, ISelectionChangedListener, IEventListener {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.targetfocus";

	private Combo cbVariant;
	private TreeViewer tvChanges;
	private SourceViewer lbChange;
	private Button btnSync;
	private String project = "";
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
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		if (configurationProject != null) {
			cbVariant.setItems(configurationProject.getVariantNames().toArray(new String[] {}));
		}
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

		final TableLayout layout = new TableLayout();
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

			final IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			for (final IDelta<?> delta : lastSelections) {
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

					final Configuration config = configurationProject.getConfigurationForVariant(configurationProject.getVariant(project));
					if (config != null) {
						final Set<String> selectedFeatures = config.getSelectedFeatureNames();
						final List<IPatch<?>> checkedPatches = new ArrayList<>();
						for (final IPatch<?> patch : patches) {
							if (selectedFeatures.contains(patch.getContext())) {
								checkedPatches.add(patch);
							}
						}

						if ((patches != null) && !patches.isEmpty()) {
							tvChanges.setInput(FeatureTree.construct(project, checkedPatches));
						}
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

		final ITreeSelection selection = tvChanges.getStructuredSelection();
		// Only one element selected
		if (selection.size() == 1) {
			Object o = selection.getFirstElement();
			if (o instanceof TreeNode) {
				o = ((TreeNode) o).getData();
			}
			if (o instanceof IDelta) {
				final IDelta<?> delta = ((IDelta<?>) o);
				lastSelections.add(delta);
				lbChange.setDocument(new Document(delta.getRepresentation()));
				btnSync.setEnabled(true);
			} else {
				lbChange.setDocument(new Document(""));
			}
			// Multiple elements selected
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
					ret += ret.isEmpty() ? delta.getRepresentation() : "\n\n" + delta.getRepresentation();
					btnSync.setEnabled(true);
				}
			}
			lbChange.setDocument(new Document(ret));
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
			final int oldSelection = cbVariant.getSelectionIndex();
			cbVariant.setItems(VariantSyncPlugin.getActiveFeatureContextManager().getContextsAsStrings().toArray(new String[] {}));
			cbVariant.select(oldSelection);
		default:
			break;
		}
	}
}
