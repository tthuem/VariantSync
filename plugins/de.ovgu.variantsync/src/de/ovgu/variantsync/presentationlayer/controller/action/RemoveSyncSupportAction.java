package de.ovgu.variantsync.presentationlayer.controller.action;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.presentationlayer.view.eclipseadjustment.VSyncSupportProjectNature;

/**
 * Removes nature support to specific project.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 21.05.2015
 */
public class RemoveSyncSupportAction extends SyncSupportAction {

	@Override
	protected void doNatureAction(IProject project) {
		VSyncSupportProjectNature.removeNature(project);
	}

}
