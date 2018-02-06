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

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.featurecontexts.manage";

	private final List<FeatureContext> expressions;

	public FeatureContextManager() {
		super();
		setWindowTitle("Feature Contexts Manager");
		this.expressions = VariantSyncPlugin.getActiveFeatureContextManager().getContexts();
	}

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
