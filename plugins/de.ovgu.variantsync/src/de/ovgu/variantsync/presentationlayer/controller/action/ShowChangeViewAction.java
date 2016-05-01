package de.ovgu.variantsync.presentationlayer.controller.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.presentationlayer.view.resourcechanges.ResourceChangesView;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * 
 * @author Lei Luo
 *
 */
public class ShowChangeViewAction implements IObjectActionDelegate {

	@Override
	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		try {
			if (page.findView(ResourceChangesView.ID) != null) {
				((ResourceChangesView) page.findView(ResourceChangesView.ID))
						.refreshTree();
			}
			page.showView(ResourceChangesView.ID);
		} catch (PartInitException e) {
			LogOperations
					.logError("ResourceChangesView could not be shown.", e);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// not required
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// not required
	}

}
