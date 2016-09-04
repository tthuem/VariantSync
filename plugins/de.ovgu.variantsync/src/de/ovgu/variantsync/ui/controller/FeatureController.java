package de.ovgu.variantsync.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.features.FeatureOperations;
import de.ovgu.variantsync.ui.controller.data.JavaElements;
import de.ovgu.variantsync.ui.controller.data.MappingElement;

/**
 * Manages communication of project informations between view and model.
 * Transforms user interactions in gui elements in model compatible actions.
 * Implements required methods to communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class FeatureController extends AbstractController {

	private FeatureOperations featureOperations = ModuleFactory
			.getFeatureOperations();

	public void checkFeatureSupport(Object[] elements, Object[] selectedFeatures) {
		List<IProject> projects = new ArrayList<IProject>();
		for (Object o : selectedFeatures) {
			projects.add((IProject) o);
		}
		setModelProperty(ControllerProperties.FEATURECHECK.getProperty(),
				projects, selectedFeatures);
	}

	public void getFeatures(List<IProject> projects) {
		setModelProperty(ControllerProperties.FEATUREEXTRACTION.getProperty(),
				projects);
	}

	public Map<IProject, Boolean> checkFeatureSupport(List<IProject> projects,
			Object[] selectedFeatures) {
		return featureOperations
				.checkFeatureSupport(projects, selectedFeatures);
	}

	public Map<IProject, Set<IFeature>> getFeaturesDirectly(
			List<IProject> projects) {
		return featureOperations.getFeatures(projects);
	}

	public FeatureModel getFeatureModel() {
		return featureOperations.getFeatureModel();
	}

	public void addFeatureMapping(String feature, String elementName,
			JavaElements elementType, IPath elementPath, IProject project) {
		setModelProperty(
				ControllerProperties.PACKAGE_EXPLORER_ELEMENT_PROPERTY
						.getProperty(),
				new MappingElement(feature, elementName, elementType,
						elementPath.toString(), project.getLocation().toFile()
								.getAbsolutePath()));
	}

	public void addFeatureMapping(String feature, String elementName,
			JavaElements elementType, IPath elementPath, String code,
			int startLineOfSelection, int endLineOfSelection, int offset, String projectPath) {
		MappingElement mapping = new MappingElement(feature, elementName,
				elementType, elementPath.toString(), code,
				startLineOfSelection, endLineOfSelection, offset);
		mapping.setPathToProject(projectPath);
		setModelProperty(ControllerProperties.CODE_EDITOR_ELEMENT_PROPERTY
				.getProperty(), mapping, new Boolean(true));
	}

	public void removeMapping(MappingElement mapping) {
		setModelProperty(
				ControllerProperties.REMOVE_MAPPING_PROPERTY.getProperty(),
				mapping);
	}

	public void addFeatureExpression(String fe) {
		setModelProperty(
				ControllerProperties.CONSTRAINT_PROPERTY.getProperty(),
				fe);
	}

	public void deleteFeatureExpression(String expr) {
		setModelProperty(
				ControllerProperties.DELETE_EXPRESSION_PROPERTY.getProperty(),
				expr);
	}

	public FeatureExpressions getFeatureExpressions() {
		return featureOperations.getFeatureExpressions();
	}

	public List<String> getVariants() {
		List<String> variants = new ArrayList<String>();
		List<IProject> projects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for(IProject p: projects) {
			variants.add(p.getName());
		}
		return variants;
	}
}