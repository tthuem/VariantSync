package de.tubs.variantsync.core.view.context;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import de.tubs.variantsync.core.VariantSyncPlugin;

/**
 * Contributes the start and stop button to the menu bar
 * 
 * @author Christopher Sontag
 */
public class ActiveContextHandler extends AbstractHandler implements IElementUpdater {

	boolean active = false;
	private static UIElement updateElement;

	@Override
	public boolean isEnabled() {
		return VariantSyncPlugin.getActiveFeatureContextManager().getContexts() != null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean oldValue = active;
		VariantSyncPlugin.getActiveMappingManager().setActive(!oldValue);
		if (!oldValue) updateElement.setIcon(VariantSyncPlugin.getDefault().getImageDescriptor("icons/nav_stop.gif"));
		else updateElement.setIcon(VariantSyncPlugin.getDefault().getImageDescriptor("icons/nav_go.gif"));
		active = !oldValue;
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		updateElement = element;
	}

}
