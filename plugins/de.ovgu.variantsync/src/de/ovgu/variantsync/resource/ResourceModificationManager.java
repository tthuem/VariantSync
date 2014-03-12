package de.ovgu.variantsync.resource;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncPlugin;
/**
 * 
 * @author Lei Luo
 *
 */
public class ResourceModificationManager implements IResourceChangeListener,
		ISaveParticipant {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new DeltaProcessor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void registerSaveParticipant() throws CoreException {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ISavedState ss = ws.addSaveParticipant(VariantSyncPlugin.getDefault(), this);
		if (ss != null) {
			ss.processResourceChangeEvents(this);
		}
		ws.removeSaveParticipant(VariantSyncPlugin.getDefault());
	}

	@Override
	public void doneSaving(ISaveContext context) {

	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {

	}

	@Override
	public void rollback(ISaveContext context) {

	}

	@Override
	public void saving(ISaveContext context) throws CoreException {

	}

}
