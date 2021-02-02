package de.tubs.variantsync.core.view.featurecontext;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.featureide.fm.core.color.ColorPalette;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.FeatureContext;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

/**
 *
 * Feature context view
 *
 * @author Christopher Sontag
 */
public class View extends ViewPart implements IEventListener {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.featurecontexts";

	private List<FeatureContext> expressions;

	private Table featureExpressionTable;

	private final IPartListener editorListener = new IPartListener() {

		@Override
		public void partActivated(IWorkbenchPart part) {
			updateFeatureExpressionList();
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			updateFeatureExpressionList();
		}

		@Override
		public void partClosed(IWorkbenchPart part) {}

		@Override
		public void partDeactivated(IWorkbenchPart part) {}

		@Override
		public void partOpened(IWorkbenchPart part) {
			updateFeatureExpressionList();
		}

	};

	public View() {
		super();
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		// Existing Feature Expressions
		featureExpressionTable = new Table(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		featureExpressionTable.setHeaderVisible(false);
		featureExpressionTable.setLinesVisible(true);
		featureExpressionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		featureExpressionTable.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {

				final Table table = (Table) event.widget;
				final int columnCount = table.getColumnCount();
				if (columnCount == 0) {
					return;
				}
				final Rectangle area = table.getClientArea();
				final int totalAreaWdith = area.width;
				final int lineWidth = table.getGridLineWidth();
				final int totalGridLineWidth = (columnCount - 1) * lineWidth;
				int totalColumnWidth = 0;
				for (final TableColumn column : table.getColumns()) {
					totalColumnWidth = totalColumnWidth + column.getWidth();
				}
				final int diff = totalAreaWdith - (totalColumnWidth + totalGridLineWidth);

				final TableColumn lastCol = table.getColumns()[columnCount - 1];

				// check diff is valid or not. setting negetive width doesnt make sense.
				lastCol.setWidth(diff + lastCol.getWidth());

			}
		});

		final TableColumn featureExpressionTableColumn = new TableColumn(featureExpressionTable, SWT.NONE);
		featureExpressionTableColumn.setText("Name");
		featureExpressionTableColumn.setWidth(280);
		featureExpressionTableColumn.setResizable(true);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "VariantSync.FeatureExpressionManager");

		final IWorkbenchPage page = getSite().getPage();
		page.addPartListener(editorListener);

		fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		final IAction addExpression = new Action("", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				createFeatureExpression();
			}
		};
		addExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		toolBarManager.add(addExpression);

		final IAction editExpression = new Action("", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				editFeatureExpression();
			}
		};
		editExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		toolBarManager.add(editExpression);

		final IAction removeExpression = new Action("", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				deleteFeatureExpression();
			}
		};
		removeExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		toolBarManager.add(removeExpression);
	}

	@Override
	public void setFocus() {
		if (!featureExpressionTable.isDisposed()) {
			featureExpressionTable.setFocus();
		}
	}

	private void editFeatureExpression() {
		if (!featureExpressionTable.isDisposed()) {
			final TableItem[] selection = featureExpressionTable.getSelection();
			if (selection.length == 1) {
				final WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureContextWizard((FeatureContext) selection[0].getData()));
				dialog.create();
				if (dialog.open() == Window.OK) {
					updateFeatureExpressionList();
				}
			}
		}
	}

	private void deleteFeatureExpression() {
		if (!featureExpressionTable.isDisposed()) {
			final TableItem[] selection = featureExpressionTable.getSelection();
			if (selection.length > 0) {
				for (final TableItem ti : selection) {
					expressions.remove(ti.getData());
				}
				updateFeatureExpressionList();
			}
		}
	}

	private void updateFeatureExpressionList() {
		TableItem tableItem = null;
		if (!featureExpressionTable.isDisposed() && featureExpressionTable.isVisible()) {
			featureExpressionTable.removeAll();
			final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
			if (configurationProject != null) {
				expressions = configurationProject.getFeatureContextManager().getContexts();
				if (!expressions.isEmpty()) {
					for (final FeatureContext fe : expressions) {
						tableItem = new TableItem(featureExpressionTable, SWT.NONE);
						tableItem.setText(fe.name);
						tableItem.setBackground(ColorPalette.toSwtColor(fe.highlighter));
						tableItem.setData(fe);
					}
				}
			}
		}
	}

	private void createFeatureExpression() {
		final WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureContextWizard(null));
		dialog.create();
		if (dialog.open() == Window.OK) {
			updateFeatureExpressionList();
		}
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_SET:
			break;
		case CONTEXT_CHANGED:
			break;
		case CONTEXT_RECORDING_START:
			break;
		case CONTEXT_RECORDING_STOP:
			break;
		case FEATURECONTEXT_ADDED:
			updateFeatureExpressionList();
			break;
		case PATCH_ADDED:
			break;
		case PATCH_CHANGED:
			break;
		case PATCH_CLOSED:
			break;
		default:
			break;
		}
	}

}
