package de.ovgu.variantsync.applicationlayer.monitoring;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.presentationlayer.controller.ControllerProperties;

public class MonitorNotifier extends AbstractModel {

	private static MonitorNotifier instance;

	private MonitorNotifier() {
	}

	public static MonitorNotifier getInstance() {
		if (instance == null) {
			instance = new MonitorNotifier();
		}
		return instance;
	}

	public void notifyViews() {
		propertyChangeSupport.firePropertyChange(
				ControllerProperties.REFRESHTREE_PROPERTY.getProperty(), null,
				null);
	}
}
