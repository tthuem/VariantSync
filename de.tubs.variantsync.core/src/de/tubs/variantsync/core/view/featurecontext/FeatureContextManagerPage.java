package de.tubs.variantsync.core.view.featurecontext;

import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.color.ColorPalette;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.FeatureContext;

/**
 * Page for {@link FeatureContextManager}.
 * 
 * @author Christopher Sontag
 */
public class FeatureContextManagerPage extends WizardPage {

	private final List<FeatureContext> contexts;

	private Table tabContexts;

	protected FeatureContextManagerPage(List<FeatureContext> contexts) {
		super("FeatureContextManager");
		this.contexts = contexts;
		setTitle("Feature Contexts");
		setDescription("Create, Edit or Delete Feature Contexts");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		// Existing Feature Expressions
		tabContexts = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tabContexts.setHeaderVisible(false);
		tabContexts.setLinesVisible(true);
		tabContexts.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {

				Table table = (Table) event.widget;
				int columnCount = table.getColumnCount();
				if (columnCount == 0)
					return;
				Rectangle area = table.getClientArea();
				int totalAreaWdith = area.width;
				int lineWidth = table.getGridLineWidth();
				int totalGridLineWidth = (columnCount - 1) * lineWidth;
				int totalColumnWidth = 0;
				for (TableColumn column : table.getColumns()) {
					totalColumnWidth = totalColumnWidth + column.getWidth();
				}
				int diff = totalAreaWdith - (totalColumnWidth + totalGridLineWidth);

				TableColumn lastCol = table.getColumns()[columnCount - 1];

				// check diff is valid or not. setting negetive width doesnt make sense.
				lastCol.setWidth(diff + lastCol.getWidth());

			}
		});

		TableColumn tcContexts = new TableColumn(tabContexts, SWT.NONE);
		tcContexts.setText("Name");
		tcContexts.setWidth(600);
		tcContexts.setResizable(true);

		tabContexts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Buttons
		final Composite buttonComposite = new Composite(composite, SWT.NULL);
		final GridLayout buttonLayout = new GridLayout(1, true);
		buttonLayout.marginWidth = 0;
		buttonLayout.marginHeight = 0;
		buttonComposite.setLayout(buttonLayout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

		Button button = new Button(buttonComposite, SWT.NULL);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				createFeatureContext();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		button = new Button(buttonComposite, SWT.NULL);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_CLEAR));
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editFeatureContext();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		button = new Button(buttonComposite, SWT.NULL);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteFeatureContext();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		updateFeatureContextList();
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.getControl(), "VariantSync.FeatureContextManager");
	}

	private void editFeatureContext() {
		final TableItem[] selection = tabContexts.getSelection();
		if (selection.length == 1) {
			WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(),
					new FeatureContextWizard((FeatureContext) selection[0].getData()));
			dialog.create();
			if (dialog.open() == Window.OK) {
				this.updateFeatureContextList();
			}
		}
	}

	private void deleteFeatureContext() {
		final TableItem[] selection = tabContexts.getSelection();
		if (selection.length > 0) {
			for (TableItem ti : selection) {
				contexts.remove((FeatureContext) ti.getData());
			}
			this.updateFeatureContextList();
		}
	}

	private void updateFeatureContextList() {
		TableItem tableItem = null;
		tabContexts.removeAll();
		if (!contexts.isEmpty()) {
			for (FeatureContext fe : contexts) {
				tableItem = new TableItem(tabContexts, NONE);
				tableItem.setText(fe.name);
				tableItem.setBackground(ColorPalette.toSwtColor(fe.highlighter));
				tableItem.setData(fe);
			}
		}
	}

	private void createFeatureContext() {
		WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureContextWizard(null));
		dialog.create();
		if (dialog.open() == Window.OK) {
			this.updateFeatureContextList();
		}
	}

}
