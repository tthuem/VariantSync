package de.tubs.variantsync.core.view.featureexpressions;

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
import de.tubs.variantsync.core.data.FeatureExpression;

/**
 * Page for {@link FeatureExpressionManager}.
 * 
 * @author Christopher Sontag
 */
public class FeatureExpressionManagerPage extends WizardPage {

	private final List<FeatureExpression> expressions;

	private Table featureExpressionTable;

	protected FeatureExpressionManagerPage(List<FeatureExpression> expressions) {
		super("FeatureExpressionManager");
		this.expressions = expressions;
		setTitle("Feature Expressions");
		setDescription("Create, Edit or Delete Feature Expressions");
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		// Existing Feature Expressions
		featureExpressionTable = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		featureExpressionTable.setHeaderVisible(false);
		featureExpressionTable.setLinesVisible(true);
		featureExpressionTable.addListener(SWT.Resize, new Listener() {

	          @Override
	          public void handleEvent(Event event) {


	            Table table = (Table)event.widget;
	            int columnCount = table.getColumnCount();
	            if(columnCount == 0)
	              return;
	            Rectangle area = table.getClientArea();
	            int totalAreaWdith = area.width;
	            int lineWidth = table.getGridLineWidth();
	            int totalGridLineWidth = (columnCount-1)*lineWidth; 
	            int totalColumnWidth = 0;
	            for(TableColumn column: table.getColumns())
	            {
	              totalColumnWidth = totalColumnWidth+column.getWidth();
	            }
	            int diff = totalAreaWdith-(totalColumnWidth+totalGridLineWidth);

	            TableColumn lastCol = table.getColumns()[columnCount-1];

	            //check diff is valid or not. setting negetive width doesnt make sense.
	            lastCol.setWidth(diff+lastCol.getWidth());

	          }
	        });
		
	    TableColumn featureExpressionTableColumn = new TableColumn(featureExpressionTable, SWT.NONE);
	    featureExpressionTableColumn.setText("Name");
	    featureExpressionTableColumn.setWidth(600);
	    featureExpressionTableColumn.setResizable(true);
	    
	    featureExpressionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
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
				createFeatureExpression();
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
				editFeatureExpression();
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
				deleteFeatureExpression();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		updateFeatureExpressionList();
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.getControl(), "VariantSync.FeatureExpressionManager");
	}

	private void editFeatureExpression() {
		final TableItem[] selection = featureExpressionTable.getSelection();
		if (selection.length == 1) {
			WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureExpressionWizard((FeatureExpression)selection[0].getData()));
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
				expressions.remove((FeatureExpression)ti.getData());
			}
			this.updateFeatureExpressionList();
		}
	}

	private void updateFeatureExpressionList() {
		TableItem tableItem = null;
		featureExpressionTable.removeAll();
		if (!expressions.isEmpty()) {
			for (FeatureExpression fe : expressions) {
				tableItem = new TableItem(featureExpressionTable, NONE);
				tableItem.setText(fe.name);
				tableItem.setBackground(ColorPalette.toSwtColor(fe.highlighter));
				tableItem.setData(fe);
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
