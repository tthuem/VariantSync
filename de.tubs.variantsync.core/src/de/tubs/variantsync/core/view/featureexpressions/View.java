package de.tubs.variantsync.core.view.featureexpressions;

import java.util.List;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.featureide.fm.core.color.ColorPalette;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;

public class View extends ViewPart {

	private List<FeatureExpression> expressions;

	private Table featureExpressionTable;

	private IPartListener editorListener = new IPartListener() {

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
	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		// Existing Feature Expressions
		featureExpressionTable =
			new Table(composite, SWT.MULTI
				| SWT.H_SCROLL
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		featureExpressionTable.setHeaderVisible(false);
		featureExpressionTable.setLinesVisible(true);
		featureExpressionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		featureExpressionTable.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {

				Table table = (Table) event.widget;
				int columnCount = table.getColumnCount();
				if (columnCount == 0) return;
				Rectangle area = table.getClientArea();
				int totalAreaWdith = area.width;
				int lineWidth = table.getGridLineWidth();
				int totalGridLineWidth =
					(columnCount
						- 1)
						* lineWidth;
				int totalColumnWidth = 0;
				for (TableColumn column : table.getColumns()) {
					totalColumnWidth =
						totalColumnWidth
							+ column.getWidth();
				}
				int diff =
					totalAreaWdith
						- (totalColumnWidth
							+ totalGridLineWidth);

				TableColumn lastCol =
					table.getColumns()[columnCount
						- 1];

				// check diff is valid or not. setting negetive width doesnt make sense.
				lastCol.setWidth(diff
					+ lastCol.getWidth());

			}
		});

		TableColumn featureExpressionTableColumn = new TableColumn(featureExpressionTable, SWT.NONE);
		featureExpressionTableColumn.setText("Name");
		featureExpressionTableColumn.setWidth(280);
		featureExpressionTableColumn.setResizable(true);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "VariantSync.FeatureExpressionManager");

		IWorkbenchPage page = getSite().getPage();
		page.addPartListener(editorListener);

		fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		IAction addExpression = new Action("", Action.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				createFeatureExpression();
			}
		};
		addExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		toolBarManager.add(addExpression);

		IAction editExpression = new Action("", Action.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				editFeatureExpression();
			}
		};
		editExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		toolBarManager.add(editExpression);

		IAction removeExpression = new Action("", Action.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				deleteFeatureExpression();
			}
		};
		removeExpression.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		toolBarManager.add(removeExpression);
	}

	@Override
	public void setFocus() {}

	private void editFeatureExpression() {
		final TableItem[] selection = featureExpressionTable.getSelection();
		if (selection.length == 1) {
			WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureExpressionWizard((FeatureExpression) selection[0].getData()));
			dialog.create();
			if (dialog.open() == Window.OK) {
				this.updateFeatureExpressionList();
			}
		}
	}

	private void deleteFeatureExpression() {
		final TableItem[] selection = featureExpressionTable.getSelection();
		if (selection.length > 0) {
			for (TableItem ti : selection) {
				expressions.remove((FeatureExpression) ti.getData());
			}
			this.updateFeatureExpressionList();
		}
	}

	private void updateFeatureExpressionList() {
		TableItem tableItem = null;
		featureExpressionTable.removeAll();
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		if (context != null) {
			expressions = context.getFeatureExpressions();
			if (!expressions.isEmpty()) {
				for (FeatureExpression fe : expressions) {
					tableItem = new TableItem(featureExpressionTable, SWT.NONE);
					tableItem.setText(fe.name);
					tableItem.setBackground(ColorPalette.toSwtColor(fe.highlighter));
					tableItem.setData(fe);
				}
			}
		}
	}

	private void createFeatureExpression() {
		WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureExpressionWizard(null));
		dialog.create();
		if (dialog.open() == Window.OK) {
			this.updateFeatureExpressionList();
		}
	}

}
