package de.ovgu.variantsync.presentationlayer.controller;

import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;

/**
 * Manages communication of project informations between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class ProjectController extends AbstractController {

	public void getProjectList(ResourceChangesFilePatch patch) {
		setModelProperty(
				ControllerProperties.PROJECTLIST_PROPERTY.getProperty(), patch);
	}

	public void getProjectNames(ResourceChangesFilePatch patch) {
		setModelProperty(
				ControllerProperties.PROJECTNAMES_PROPERTY.getProperty(), patch);
	}

}