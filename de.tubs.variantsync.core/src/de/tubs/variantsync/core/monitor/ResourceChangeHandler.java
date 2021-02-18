package de.tubs.variantsync.core.monitor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import de.tubs.variantsync.core.VariantSyncPlugin;
import de.tubs.variantsync.core.utilities.LogOperations;

/**
 * Listener which notifies resource changes in workspace that have already happened.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag
 * @version 1.2
 * @since 17.05.2015
 */
public class ResourceChangeHandler implements IResourceChangeListener, ISaveParticipant {

	/**
	 * Notifies ResourceDeltaListener that some resource changes have already happened.
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new ResourceChangeVisitor());
			} catch (final CoreException e) {
				LogOperations.logError("ResourceChangeVisitor could not be set.", e);
			}
		}
	}

	/**
	 * Ensures that resource monitoring is active even if variantsync plugin is not active in eclipse.
	 *
	 * @throws CoreException resources could not be monitored
	 */
	public void registerSaveParticipant() throws CoreException {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();

		// Registers the given plug-in's workspace save participant, and returns
		// an object describing the workspace state at the time of the last save
		// in which the bundle participated.
		final ISavedState ss = ws.addSaveParticipant(VariantSyncPlugin.PLUGIN_ID, this);
		if (ss != null) {

			// used to receive notification of changes that might have happened
			// while VariantSyncPlugin was not active.
			ss.processResourceChangeEvents(this);
		}
		ws.removeSaveParticipant(VariantSyncPlugin.PLUGIN_ID);
	}

	@Override
	public void doneSaving(ISaveContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback(ISaveContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		// TODO Auto-generated method stub

	}

}
