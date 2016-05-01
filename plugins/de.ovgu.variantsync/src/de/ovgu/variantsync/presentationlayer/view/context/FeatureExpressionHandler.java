package de.ovgu.variantsync.presentationlayer.view.context;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class FeatureExpressionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		// Turn comparison into an input for viewers
//		final EMFDiffNode diffNode = new EMFDiffNode((EComparison)comparison, anEditingDomain);
//		// Show the comparison in a dialog
//		final Display display = Display.getDefault();
//		display.syncExec(new Runnable() {
//		  public void run() {
//		    DiffMergeDialog dialog = new DiffMergeDialog(
//		        display.getActiveShell(), "Your Title", diffNode);
//		    dialog.open();
//		  }
//		});
		new FeatureManagementDialog();
		return null;
	}

}