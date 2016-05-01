package de.ovgu.variantsync.presentationlayer.controller.popup.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.presentationlayer.controller.ContextController;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;

public class RemoveMappingAction implements IObjectActionDelegate {

	private Shell shell;

	public RemoveMappingAction() {
		super();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	@Override
	public void run(IAction action) {
		try {
			IEditorPart editorPart = VariantSyncPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			System.out.println(resource.toString());
			String filename = resource.toString().substring(
					resource.toString().indexOf("/") + 1);
			ContextController cc = ControllerHandler.getInstance()
					.getContextController();
			cc.removeTagging(filename);
		} catch (Exception e) {
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}