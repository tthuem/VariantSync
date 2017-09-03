package de.tubs.variantsync.core.view.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.utilities.IEventListener;
import de.tubs.variantsync.core.utilities.VariantSyncEvent;

public class DynamicContextDropDownItems extends CompoundContributionItem
		implements IWorkbenchContribution, IEventListener {

	private IServiceLocator mServiceLocator;

	@Override
	protected IContributionItem[] getContributionItems() {
		if (VariantSyncPlugin.getDefault() != null) {
			Context context = VariantSyncPlugin.getContext();
			if (context != null) {
				context.addListener(this);
				if (context.getFeatureExpressions() != null) {
					List<FeatureExpression> expressions = context.getFeatureExpressions();

					List<IContributionItem> items = new ArrayList<>();
					for (FeatureExpression expression : expressions) {

						// Root element of the feature model will not be added
						if (!context.getConfigurationProject().getFeatureModel().getStructure().getRoot().getFeature()
								.getName().equals(expression.name)) {

							// Create a CommandContributionItem with a message which contains the feature
							// expression
							Map<String, String> params = new HashMap<String, String>();
							params.put(SelectContextHandler.PARM_MSG, expression.name);

							final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
									mServiceLocator, SelectContextHandler.ID, SelectContextHandler.ID,
									CommandContributionItem.STYLE_RADIO);
							contributionParameter.visibleEnabled = true;
							contributionParameter.parameters = params;
							contributionParameter.label = expression.name;
							contributionParameter.icon = VariantSyncPlugin
									.imageDescriptorFromPlugin(VariantSyncPlugin.PLUGIN_ID, "icons/public_co.gif");

							// Composed expressions will have another indicator as pure features
							if (expression.isComposed())
								contributionParameter.icon = VariantSyncPlugin.imageDescriptorFromPlugin(
										VariantSyncPlugin.PLUGIN_ID, "icons/protected_co.gif");

							items.add(new CommandContributionItem(contributionParameter));
						}
					}
					return items.toArray(new IContributionItem[items.size()]);
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(final IServiceLocator serviceLocator) {
		mServiceLocator = serviceLocator;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public void propertyChange(VariantSyncEvent event) {
		switch (event.getEventType()) {
		case CONFIGURATIONPROJECT_SET:
		case FEATUREEXPRESSION_ADDED:
			update();
			break;
		default:
			break;
		
		}
	}

}
