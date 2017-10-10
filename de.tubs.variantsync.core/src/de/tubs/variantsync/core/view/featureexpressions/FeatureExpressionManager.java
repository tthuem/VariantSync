package de.tubs.variantsync.core.view.featureexpressions;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.FeatureExpression;

/**
 * Wizard for managing feature expressions
 * 
 * @author Christopher Sontag
 */
public class FeatureExpressionManager extends Wizard {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.featureexpressions.manage";

	private final List<FeatureExpression> expressions;

	public FeatureExpressionManager() {
		super();
		setWindowTitle("Feature Expression Manager");
		this.expressions = VariantSyncPlugin.getDefault().getActiveEditorContext().getFeatureExpressions();
	}

	public void addPages() {
		addPage(new FeatureExpressionManagerPage(expressions));
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
