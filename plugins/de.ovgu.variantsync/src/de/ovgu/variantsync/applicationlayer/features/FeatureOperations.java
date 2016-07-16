package de.ovgu.variantsync.applicationlayer.features;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FeatureException;
import de.ovgu.variantsync.ui.controller.data.MappingElement;

/**
 * Defines functions to manage features.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.05.2015
 */
public interface FeatureOperations {

	/**
	 * Checks if projects contain all given features.
	 * 
	 * @param projects
	 *            the projects to check
	 * @param selectedFeatures
	 *            the features which all projects should contain
	 * @return mapping with specification which projects support all features
	 *         and which projects do not support all features
	 */
	Map<IProject, Boolean> checkFeatureSupport(List<IProject> projects,
			Object[] selectedFeatures);

	/**
	 * Get specified features of projects.
	 * 
	 * @param projects
	 *            the list of projects to check
	 * @return mapped projects with containing features
	 * @throws FeatureException
	 *             configuration object could not be created and features could
	 *             not be read
	 */
	Map<IProject, Set<Feature>> getFeatures(List<IProject> projects);

	FeatureExpressions getFeatureExpressions();

	void addElement(MappingElement mapping, Variant project);

	void addCodeFragment(MappingElement mapping, Variant project);

	void removeMapping(MappingElement mapping, Variant project);

	FeatureModel getFeatureModel();

	void addFeatureExpression(String featureExpression);

	void addFeatureExpression(Set<String> featureExpressions);

	void deleteFeatureExpression(String expr);

	Set<Feature> getConfiguredFeaturesOfProject(IProject project);

	void addCodeFragment(MappingElement mapping, Boolean markerUpdate);
}
