package de.ovgu.variantsync.presentationlayer.controller;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.presentationlayer.view.AbstractView;

/**
 * Connections between view and model. Transforms user interactions in gui
 * elements in model compatible actions. Implements required methods to
 * communication with view and model.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class ControllerHandler {

	private static ControllerHandler instance;
	private FeatureController featureController;
	private ContextController contextController;
	private SynchronizationController synchronizationController;
	private DeltaController deltaController;
	private ProjectController projectController;
	private MonitorController monitorController;

	private ControllerHandler() {
		featureController = new FeatureController();
		contextController = new ContextController();
		synchronizationController = new SynchronizationController();
		deltaController = new DeltaController();
		projectController = new ProjectController();
		monitorController = new MonitorController();
	}

	public static ControllerHandler getInstance() {
		if (instance == null) {
			instance = new ControllerHandler();
		}
		return instance;
	}

	public void addView(AbstractView view, ControllerTypes type) {
		switch (type) {
		case DELTA: {
			deltaController.addView(view);
			break;
		}
		case SYNCHRONIZATION: {
			synchronizationController.addView(view);
			break;
		}
		case FEATURE: {
			featureController.addView(view);
			break;
		}
		case PROJECT: {
			projectController.addView(view);
			break;
		}
		case CONTEXT: {
			contextController.addView(view);
			break;
		}
		case MONITOR: {
			monitorController.addView(view);
			break;
		}
		default:
			break;
		}
	}

	public void removeView(AbstractView view, ControllerTypes type) {
		switch (type) {
		case DELTA: {
			deltaController.removeView(view);
			break;
		}
		case SYNCHRONIZATION: {
			synchronizationController.removeView(view);
			break;
		}
		case FEATURE: {
			featureController.removeView(view);
			break;
		}
		case PROJECT: {
			projectController.removeView(view);
			break;
		}
		case CONTEXT: {
			contextController.removeView(view);
			break;
		}
		case MONITOR: {
			monitorController.removeView(view);
			break;
		}
		default:
			break;
		}
	}

	public void addModel(AbstractModel model, ControllerTypes type) {
		switch (type) {
		case DELTA: {
			deltaController.addModel(model);
			break;
		}
		case SYNCHRONIZATION: {
			synchronizationController.addModel(model);
			break;
		}
		case FEATURE: {
			featureController.addModel(model);
			break;
		}
		case PROJECT: {
			projectController.addModel(model);
			break;
		}
		case CONTEXT: {
			contextController.addModel(model);
			break;
		}
		case MONITOR: {
			monitorController.addModel(model);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @return the featureController
	 */
	public FeatureController getFeatureController() {
		return featureController;
	}

	/**
	 * @return the contextController
	 */
	public ContextController getContextController() {
		return contextController;
	}

	/**
	 * @return the synchronizationController
	 */
	public SynchronizationController getSynchronizationController() {
		return synchronizationController;
	}

	/**
	 * @return the deltaController
	 */
	public DeltaController getDeltaController() {
		return deltaController;
	}

	/**
	 * @return the projectController
	 */
	public ProjectController getProjectController() {
		return projectController;
	}

	/**
	 * @return the monitorController
	 */
	public MonitorController getMonitorController() {
		return monitorController;
	}

}