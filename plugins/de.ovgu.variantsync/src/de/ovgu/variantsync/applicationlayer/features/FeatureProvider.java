package de.ovgu.variantsync.applicationlayer.features;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.FeatureExpressions;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FeatureException;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.ui.controller.ControllerProperties;
import de.ovgu.variantsync.ui.controller.data.MappingElement;
import de.ovgu.variantsync.ui.view.codemapping.CodeMarkerFactory;
import de.ovgu.variantsync.ui.view.context.MarkerHandler;
import de.ovgu.variantsync.utilities.Util;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class FeatureProvider extends AbstractModel implements FeatureOperations {

	private FeatureHandler featureHandler;
	private FeatureMapping featureMapping;

	public FeatureProvider() {
		featureHandler = FeatureHandler.getInstance();
		featureMapping = FeatureMapping.getInstance();
	}

	@Override
	public Map<IProject, Boolean> checkFeatureSupport(List<IProject> projects, Object[] selectedFeatures) {
		Map<IProject, Boolean> resultMap = null;
		try {
			resultMap = featureHandler.checkFeatureSupport(projects, selectedFeatures);
		} catch (FeatureException e) {
			propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(),
					"Feature support could not be checked.", e);
		}
		propertyChangeSupport.firePropertyChange(ControllerProperties.FEATURECHECK.getProperty(), null, resultMap);
		return resultMap;
	}

	@Override
	public Map<IProject, Set<Feature>> getFeatures(List<IProject> projects) {
		Map<IProject, Set<Feature>> featureMap = new HashMap<IProject, Set<Feature>>();
		for (IProject project : projects) {
			Set<Feature> features = null;
			try {
				features = featureHandler.getConfiguredFeaturesOfProject(project);
				featureMap.put(project, features);

				// TODO: Kapseln
				Iterator<Feature> it = features.iterator();
				int i = 1;
				while (it.hasNext()) {
					Feature f = it.next();
					CodeMarkerFactory.setFeatureColor(f.getName(), String.valueOf(i));
					if (i == 3) {
						i = 0;
					}
					i++;
				}
			} catch (FeatureException e) {
				propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(),
						"Supported features for project " + project.getName() + " could not be computed.", e);
			}
		}

		propertyChangeSupport.firePropertyChange(ControllerProperties.FEATUREEXTRACTION.getProperty(), null,
				featureMap);
		return featureMap;
	}

	@Override
	public void addElement(MappingElement mapping, Variant project) {
		featureMapping.mapElement(mapping, project);
	}

	@Override
	public void addCodeFragment(MappingElement mapping, Variant project) {
		featureMapping.mapCodeFragment(mapping, project);
	}

	@Override
	public void addCodeFragment(MappingElement mapping, Boolean markerUpdate) {
		Context c = ModuleFactory.getContextOperations().getContext(mapping.getFeature());
		if (c == null) {
			// Variant javaProject = new
			// Variant(UtilOperations.getInstance().parseProjectName(mapping.getPathToProject()),
			// mapping.getPathToProject(), null);
			c = new Context(mapping.getFeature());
			c.initProject(UtilOperations.getInstance().parseProjectName(mapping.getPathToProject()),
					mapping.getPathToProject());
			c.setJavaProject(featureMapping.mapCodeFragment(mapping,
					c.getJavaProject(UtilOperations.getInstance().parseProjectName(mapping.getPathToProject()))));
		} else {
			String projectName = UtilOperations.getInstance().parseProjectName(mapping.getPathToProject());
			featureMapping.mapCodeFragment(mapping,
					c.getJavaProject(projectName));
		}
		ModuleFactory.getPersistanceOperations().saveContext(c, Util.parseStorageLocation(c));
		if (markerUpdate)
			MarkerHandler.getInstance().updateMarker(
					UtilOperations.getInstance().parseProjectName(mapping.getPathToProject()),
					UtilOperations.getInstance().parsePackageName(mapping.getPathToSelectedElement()),
					mapping.getPathToSelectedElement().substring(mapping.getPathToSelectedElement().lastIndexOf("/")),
					c);
	}

	@Override
	public void removeMapping(MappingElement mapping, Variant project, long modificationTime) {
		featureMapping.removeMapping(mapping, project, modificationTime);
	}

	@Override
	public FeatureExpressions getFeatureExpressions() {
		String storageLocation = VariantSyncPlugin.getWorkspaceLocation()
				+ VariantSyncConstants.FEATURE_EXPRESSION_PATH;
		File folder = new File(storageLocation.substring(0, storageLocation.lastIndexOf("/")));
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				FeatureExpressions featureExpressions = ModuleFactory.getPersistanceOperations()
						.loadFeatureExpressions(f.getPath());
				featureHandler.setFeatureExpressions(featureExpressions);
			}
		}

		try {
			return featureHandler.getFeatureExpressions();
		} catch (FeatureException e) {
			propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(),
					"Features of variantsyncFeatureInfo-Project could not be read.", e);
		}
		return new FeatureExpressions();
	}

	@Override
	public FeatureModel getFeatureModel() {
		try {
			return featureHandler.getFeatureModel();
		} catch (FeatureException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addFeatureExpression(String featureExpression) {
		featureHandler.addFeatureExpression(featureExpression);
		propertyChangeSupport.firePropertyChange(ControllerProperties.CONSTRAINT_PROPERTY.getProperty(), null, null);
	}

	@Override
	public void deleteFeatureExpression(String expr) {
		featureHandler.deleteFeatureExpression(expr);
	}

	@Override
	public void addFeatureExpression(Set<String> featureExpressions) {
		featureHandler.addFeatureExpressions(featureExpressions);
		propertyChangeSupport.firePropertyChange(ControllerProperties.CONSTRAINT_PROPERTY.getProperty(), null, null);
	}

	@Override
	public Set<Feature> getConfiguredFeaturesOfProject(IProject project) {
		try {
			return featureHandler.getConfiguredFeaturesOfProject(project);
		} catch (FeatureException e) {
			e.printStackTrace();
		}
		return null;
	}

}