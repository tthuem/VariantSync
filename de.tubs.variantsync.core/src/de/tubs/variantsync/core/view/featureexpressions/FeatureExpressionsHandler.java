package de.tubs.variantsync.core.view.featureexpressions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;

import de.tubs.variantsync.core.VariantSyncPlugin;

public class FeatureExpressionsHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog dialog = new WizardDialog(VariantSyncPlugin.getShell(), new FeatureExpressionManager());
		dialog.create();

		if (dialog.open() == Dialog.CANCEL) {
			return null;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return VariantSyncPlugin.getContext().getFeatureExpressions() != null;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

}
