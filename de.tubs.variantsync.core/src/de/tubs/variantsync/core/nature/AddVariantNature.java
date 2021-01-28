package de.tubs.variantsync.core.nature;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.tubs.variantsync.core.VariantSyncPlugin;

public class AddVariantNature extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {

			for (final Object firstElement : ((IStructuredSelection) currentSelection).toArray()) {

				// Get an IResource as an adapter from the current selection
				final IAdapterManager adapterManager = Platform.getAdapterManager();
				final IResource resourceAdapter = adapterManager.getAdapter(firstElement, IResource.class);

				if (resourceAdapter != null) {
					final IResource resource = resourceAdapter;
					final IProject project = resource.getProject();
					VariantSyncPlugin.addNature(project);
				}
			}
		}
		return null;
	}

}
