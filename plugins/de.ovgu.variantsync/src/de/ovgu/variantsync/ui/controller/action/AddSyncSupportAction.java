package de.ovgu.variantsync.ui.controller.action;

import org.eclipse.core.resources.IProject;

import de.ovgu.variantsync.ui.view.eclipseadjustment.VSyncSupportProjectNature;

/**
 * Adds nature support to specific project.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 21.05.2015
 */
public class AddSyncSupportAction extends SyncSupportAction {

	@Override
	protected void doNatureAction(IProject project) {
		VSyncSupportProjectNature.addNature(project);
	}

}
