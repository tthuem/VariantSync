package de.ovgu.variantsync.presentationlayer.controller.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.presentationlayer.view.eclipseadjustment.VSyncSupportProjectDecorator;

/**
 * Provides nature support of projects.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 21.05.2015
 */
public abstract class SyncSupportAction implements IObjectActionDelegate {

	protected ISelection selection;

	protected abstract void doNatureAction(IProject project);

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// not required
	}

	@Override
	public void run(IAction action) {
		IStructuredSelection iSSelection = null;
		if (selection instanceof IStructuredSelection) {
			iSSelection = (IStructuredSelection) selection;
			Object obj = iSSelection.getFirstElement();
			if (obj instanceof IResource) {
				IResource res = (IResource) obj;
				IProject project = res.getProject();
				doNatureAction(project);
				IDecoratorManager decoratorManager = VariantSyncPlugin
						.getDefault().getWorkbench().getDecoratorManager();
				decoratorManager
						.update(VSyncSupportProjectDecorator.DECORATOR_ID);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
