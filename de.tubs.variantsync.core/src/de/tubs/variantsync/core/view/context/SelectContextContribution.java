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
import de.tubs.variantsync.core.managers.FeatureContextManager;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.utilities.LogOperations;
import de.tubs.variantsync.core.utilities.event.IEventListener;
import de.tubs.variantsync.core.utilities.event.VariantSyncEvent;

/**
 * Contributes the combobox in the menu bar for selecting the current feature context
 *
 * @author Christopher Sontag
 */
public class SelectContextContribution extends WorkbenchWindowControlContribution implements SelectionListener, IEventListener {

	private CCombo selContext;

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

		final Composite composite = new Composite(parent, SWT.FILL);
		composite.setLayout(new FillLayout());

		selContext = new CCombo(composite, SWT.FLAT | SWT.BORDER | SWT.FILL);
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		if (configurationProject != null) {
			selContext.setText(configurationProject.getFeatureContextManager().getActual());
		} else {
			selContext.setText(FeatureContextManager.DEFAULT_CONTEXT_NAME);
		}
		selContext.addSelectionListener(this);
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

			@Override
			public void run() {
				final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
				if (configurationProject != null) {
					try {
						final FeatureContextManager featureContextManager = configurationProject.getFeatureContextManager();
						final int curSel = selContext.getSelectionIndex();
						selContext.setItems(featureContextManager.getContextsAsStrings().toArray(new String[] {}));
						if (curSel != -1) {
							selContext.select(curSel);
						} else {
							selContext.setText(featureContextManager.getActual());
						}
					} catch (final Exception e) {
						LogOperations.logError("Cannot update selection combo box in toolbar", e);
					}
				}
			}
		});
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		final String context = selContext.getItem(selContext.getSelectionIndex());

		VariantSyncPlugin.getActiveFeatureContextManager().setActual(context);
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
		case FEATURECONTEXT_ADDED:
		case FEATURECONTEXT_CHANGED:
		case FEATURECONTEXT_REMOVED:
			updateCCombo();
			break;
		default:
			break;
		}
	}

}
