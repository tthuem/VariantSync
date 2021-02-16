package de.tubs.variantsync.core.view.featurecontext;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.exceptions.ProjectNotFoundException;
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.FeatureContext;

/**
 * Wizard for creating or editing feature contexts
 *
 * @author Christopher Sontag
 */
public class FeatureContextWizard extends Wizard {

	public static final String ID = String.format("%s.views.featurecontexts.wizard", VariantSyncPlugin.PLUGIN_ID);

	private final Iterable<IFeature> features;
	private FeatureContext featureContext = null;

	private FeatureContextWizardPage page;

	public FeatureContextWizard(FeatureContext context) {
		super();
		setWindowTitle("Feature ConfigurationProject Wizard");

		{
			final ConfigurationProject activeConfigurationProject = VariantSyncPlugin.getActiveConfigurationProject();

			if (activeConfigurationProject == null) {
				throw new ProjectNotFoundException(ProjectNotFoundException.Type.CONFIGURATION);
			}

			final IFeatureProject featureProject = activeConfigurationProject.getFeatureProject();
			final IFeatureModel featuremodel = featureProject.getFeatureModel();
			features = featuremodel.getFeatures();
		}
		featureContext = context;
	}

	@Override
	public void addPages() {
		page = new FeatureContextWizardPage(features, featureContext);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		final List<FeatureContext> contexts = configurationProject.getFeatureContextManager().getContexts();
		if (featureContext != null) {
			contexts.remove(featureContext);
		}
		contexts.add(new FeatureContext(page.getFeatureContext(), page.getColor()));
		configurationProject.getFeatureContextManager().setContext(contexts);
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
