package de.tubs.variantsync.core.view.context;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import de.tubs.variantsync.core.VariantSyncPlugin;

public class ActiveContextHandler extends AbstractHandler implements IElementUpdater {

	private boolean active = false;
	private static UIElement updateElement;

	@Override
	public boolean isEnabled() {
		return VariantSyncPlugin.getDefault().getActiveEditorContext().getFeatureExpressions() != null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		VariantSyncPlugin.getDefault().setActive(!oldValue);
		if (!oldValue) updateElement.setIcon(VariantSyncPlugin.getDefault().getImageDescriptor("icons/nav_stop.gif"));
		else updateElement.setIcon(VariantSyncPlugin.getDefault().getImageDescriptor("icons/nav_go.gif"));
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		updateElement = element;
	}

}
