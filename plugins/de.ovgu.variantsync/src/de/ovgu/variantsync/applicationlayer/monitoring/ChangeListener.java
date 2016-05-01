package de.ovgu.variantsync.applicationlayer.monitoring;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.utilitylayer.log.LogOperations;

/**
 * Listener which notifies resource changes in workspace that have already
 * happened.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.1
 * @since 17.05.2015
 */
public class ChangeListener implements IResourceChangeListener,
		ISaveParticipant {

	/**
	 * Notifies ResourceDeltaListener that some resource changes have already
	 * happened.
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new ChangeHandler());
			} catch (CoreException e) {
				LogOperations.logError("ChangeHandler could not be set.", e);
			}
		}
	}

	/**
	 * Ensures that resource monitoring is active even if variantsync plugin is
	 * not active in eclipse.
	 * 
	 * @throws CoreException
	 *             resources could not be monitored
	 */
	public void registerSaveParticipant() throws CoreException {
		IWorkspace ws = ResourcesPlugin.getWorkspace();

		// Registers the given plug-in's workspace save participant, and returns
		// an object describing the workspace state at the time of the last save
		// in which the bundle participated.
		ISavedState ss = ws.addSaveParticipant(VariantSyncConstants.PLUGIN_ID,
				this);
		if (ss != null) {

			// used to receive notification of changes that might have happened
			// while VariantSyncPlugin was not active.
			ss.processResourceChangeEvents(this);
		}
		ws.removeSaveParticipant(VariantSyncConstants.PLUGIN_ID);
	}

	@Override
	public void doneSaving(ISaveContext context) {
		// not necessary
	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
		// not necessary
	}

	@Override
	public void rollback(ISaveContext context) {
		// not necessary
	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		// not necessary
	}

}