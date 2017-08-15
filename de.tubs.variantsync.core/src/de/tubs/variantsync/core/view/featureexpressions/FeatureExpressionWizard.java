package de.tubs.variantsync.core.view.featureexpressions;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.FeatureExpression;

/**
 * Wizard for managing feature expressions
 * 
 * @author Christopher Sontag
 */
public class FeatureExpressionWizard extends Wizard {

	public static final String ID = VariantSyncPlugin.PLUGIN_ID + ".views.featureexpressions.wizard";

	private final Iterable<IFeature> features;
	private FeatureExpression featureExpression = null;

	private FeatureExpressionWizardPage page;

	public FeatureExpressionWizard(FeatureExpression featureExpression) {
		super();
		setWindowTitle("Feature Expression Wizard");
		this.features = VariantSyncPlugin.getContext().getConfigurationProject().getFeatureModel().getFeatures();
		this.featureExpression = featureExpression;
		
	}

	public void addPages() {
		page = new FeatureExpressionWizardPage(features, featureExpression);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		VariantSyncPlugin.getDefault();
		List<FeatureExpression> expressions = VariantSyncPlugin.getContext().getFeatureExpressions();
		if (featureExpression != null) {
			expressions.remove(featureExpression);
		}
		expressions.add(new FeatureExpression(page.getFeatureExpression(), page.getColor()));
		VariantSyncPlugin.getContext().setFeatureExpressions(expressions);
		return true;
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean isHelpAvailable() {
		return false;
	}
	
}
