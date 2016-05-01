package de.ovgu.variantsync.presentationlayer.controller;

import de.ovgu.variantsync.applicationlayer.datamodel.resources.ResourceChangesFilePatch;

/**
 * Manages delta operations and data exchanges between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class DeltaController extends AbstractController {

	public void getChanges(ResourceChangesFilePatch patch) {
		setModelProperty(
				ControllerProperties.UNIFIEDDIFF_PROPERTY.getProperty(), patch);
	}

}