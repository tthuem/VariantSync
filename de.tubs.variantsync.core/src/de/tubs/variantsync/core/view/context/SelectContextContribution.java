package de.tubs.variantsync.core.view.context;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

public class SelectContextContribution extends WorkbenchWindowControlContribution implements SelectionListener, IEventListener {

	private CCombo featureExpressionSelection;

	public SelectContextContribution() {
		VariantSyncPlugin.getDefault().addListener(this);
	}

	public SelectContextContribution(String id) {
		super(id);
		VariantSyncPlugin.getDefault().addListener(this);
	}

	@Override
	protected Control createControl(Composite parent) {
		// Should solve Eclipse Bug 471313 and can be only used with FillLayout
		parent.getParent().setRedraw(true);

		Composite composite = new Composite(parent, SWT.FILL);
		composite.setLayout(new FillLayout());

		featureExpressionSelection = new CCombo(composite, SWT.FLAT | SWT.BORDER | SWT.FILL);
		featureExpressionSelection.setText(Context.DEFAULT_CONTEXT_NAME);
		featureExpressionSelection.addSelectionListener(this);
		updateCCombo();

		return composite;
	}

	// Should solve Eclipse Bug 471313 and can be only used with FillLayout
	@Override
	public boolean isDynamic() {
		return true;
	}

	private void updateCCombo() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
				if (context != null) {
					try {
						int curSel = featureExpressionSelection.getSelectionIndex();
						featureExpressionSelection.setItems(context.getFeatureExpressionsAsStrings().toArray(new String[] {}));
						if (curSel != -1) {
							featureExpressionSelection.select(curSel);
						} else {
							featureExpressionSelection.setText(context.getActualContext());
						}
					} catch (Exception e) {
						LogOperations.logError("Cannot update selection combo box in toolbar", e);
					}
				}
			}
		});
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		String featureExpression = featureExpressionSelection.getItem(featureExpressionSelection.getSelectionIndex());

		VariantSyncPlugin.getDefault().getActiveEditorContext().setActualContext(featureExpression);
		System.out.println("Setting context to: " + featureExpression);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_CHANGED:
		case CONFIGURATIONPROJECT_SET:
			updateCCombo();
			break;
		case CONTEXT_CHANGED:
			break;
		case CONTEXT_RECORDING_START:
			break;
		case CONTEXT_RECORDING_STOP:
			break;
		case FEATUREEXPRESSION_ADDED:
		case FEATUREEXPRESSION_CHANGED:
		case FEATUREEXPRESSION_REMOVED:
			updateCCombo();
			break;
		case PATCH_ADDED:
			break;
		case PATCH_CHANGED:
			break;
		case PATCH_CLOSED:
			break;
		case VARIANT_ADDED:
			break;
		case VARIANT_REMOVED:
			break;
		default:
			break;
		}
	}

}
