package de.tubs.variantsync.core.view.featurecontext;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.managers.data.FeatureContext;

/**
 * Wizard for managing feature contexts
 *
 * @author Christopher Sontag
 */
public class FeatureContextManager extends Wizard {

	public static final String ID = String.format("%s.views.featurecontexts.manage", VariantSyncPlugin.PLUGIN_ID);

	private final List<FeatureContext> expressions;

	public FeatureContextManager() {
		super();
		setWindowTitle("Feature Contexts Manager");
		expressions = VariantSyncPlugin.getActiveFeatureContextManager().getContexts();
	}

	@Override
	public void addPages() {
		addPage(new FeatureContextManagerPage(expressions));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean isHelpAvailable() {
		return false;
	}

}
