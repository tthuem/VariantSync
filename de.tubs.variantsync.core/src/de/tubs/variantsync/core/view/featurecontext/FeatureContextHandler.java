package de.tubs.variantsync.core.view.featurecontext;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;

import de.tubs.variantsync.core.VariantSyncPlugin;

/**
 * CommandHandler for the feature context manager
 * 
 * @author Christopher Sontag
 */
public class FeatureContextHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {}

	@Override
	public void dispose() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureContextManager());
		dialog.create();

		if (dialog.open() == Dialog.CANCEL) {
			return null;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return VariantSyncPlugin.getActiveFeatureContextManager().getContexts() != null;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {}

}
