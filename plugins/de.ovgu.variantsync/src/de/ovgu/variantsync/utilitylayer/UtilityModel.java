package de.ovgu.variantsync.utilitylayer;

import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.presentationlayer.controller.ControllerProperties;

/**
 * Receives requests of the controller. Represents the connection between
 * utilityOperations and controller.
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class UtilityModel extends AbstractModel {

	private static UtilityModel instance;

	private UtilityModel() {
	}

	/**
	 * Realizes Singleton-Pattern.
	 *
	 * @return Reference
	 */
	public static UtilityModel getInstance() {

		if (instance == null) {
			instance = new UtilityModel();
		}
		return instance;
	}

	/**
	 * Reports and logs an exception. Exception and error message will sent to
	 * registered listeners (i.e.: GUI to show error message). Stacktrace will
	 * be logged.
	 *
	 * @param e
	 *            Exception
	 * @param msg
	 *            error message
	 */
	public void handleError(Exception e, String msg) {
		propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(), msg,
				e.getMessage());
	}

	/**
	 * Reports and logs an error message. Message will sent to registered
	 * listeners (i.e.: GUI to show error message).
	 *
	 * @param msg
	 *            error message
	 */
	public void handleError(String msg) {
		propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(), null, msg);
	}

	/**
	 * Reports and logs an exception. Exception-Message will sent to registered
	 * listeners (i.e.: GUI to show error message). Stacktrace will be logged.
	 *
	 * @param e
	 *            Exception
	 */
	public void handleError(Exception e) {
		propertyChangeSupport.firePropertyChange(ControllerProperties.EXCEPTION_PROPERTY.getProperty(), null,
				e.getMessage());
	}

}