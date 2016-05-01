package de.ovgu.variantsync.presentationlayer.controller;

/**
 * Lists properties for controller in model-view-controller pattern.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public enum ControllerProperties {

	EXCEPTION_PROPERTY("exception"), SYNCHRONIZATION_PROPERTY("synchronize"), PROJECTLIST_PROPERTY(
			"getProjectList"), PROJECTNAMES_PROPERTY("getProjectNames"), SYNCHRONIZEDPROJECTS_PROPERTY(
			"getSynchronizedProjects"), UNIFIEDDIFF_PROPERTY("getChanges"), REFRESHTREE_PROPERTY(
			"refreshTree"), FEATURECHECK("checkFeatureSupport"), FEATUREEXTRACTION(
			"getFeatures"), PACKAGE_EXPLORER_ELEMENT_PROPERTY("addElement"), CODE_EDITOR_ELEMENT_PROPERTY(
			"addCodeFragment"), REMOVE_MAPPING_PROPERTY("removeMapping"), CONSTRAINT_PROPERTY(
			"addFeatureExpression"),DELETE_EXPRESSION_PROPERTY(
					"deleteFeatureExpression");

	private String property;

	ControllerProperties(String s) {
		this.property = s;
	}

	public String getProperty() {
		return this.property;
	}
}