package de.tubs.variantsync.core.utilities;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.tubs.variantsync.core.VariantSyncPlugin;

/**
 * Provides logging functionality.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @author Christopher Sontag (c.sontag@tu-bs.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class LogOperations {

	private LogOperations() {}

	/**
	 * Logs specified information.
	 * 
	 * @param message human-readable message, localized to the current locale
	 */
	public static void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}

	/**
	 * Logs specified information.
	 * 
	 * @param message human-readable message, localized to the current locale
	 */
	public static void logDebug(String message) {
		if (VariantSyncPlugin.getDefault().isDebugging()) {
			log(IStatus.INFO, IStatus.OK, message, null);
		}
	}

	/**
	 * Logs specified error.
	 * 
	 * @param message human-readable message, localized to the current locale
	 * @param exception low-level exception, or <code>null</code> if not applicable
	 */
	public static void logError(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.OK, message, exception);
	}

	/**
	 * Logs specified information.
	 * 
	 * @param severity the severity; one of the following: <code>IStatus.OK</code>, <code>IStatus.ERROR</code>, <code>IStatus.INFO</code>, or
	 *        <code>IStatus.WARNING</code>
	 * @param pluginId unique identifier of the relevant plug-in
	 * @param code plug-in-specific status code, or <code>OK</code>
	 * @param message human-readable message, localized to the current locale
	 * @param exception low-level exception, or <code>null</code> if not applicable
	 */
	public static void log(int severity, int code, String message, Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}

	/**
	 * Creates status object representing specified information.
	 * 
	 * @param severity the severity; one of the following: <code>IStatus.OK</code>, <code>IStatus.ERROR</code>, <code>IStatus.INFO</code>, or
	 *        <code>IStatus.WARNING</code>
	 * @param pluginId unique identifier of the relevant plug-in
	 * @param code plug-in-specific status code, or <code>OK</code>
	 * @param message human-readable message, localized to the current locale
	 * @param exception low-level exception, or <code>null</code> if not applicable.
	 * @return the status object (not <code>null</code>)
	 */
	public static IStatus createStatus(int severity, int code, String message, Throwable exception) {
		return new Status(severity, VariantSyncPlugin.PLUGIN_ID, code, message, exception);
	}

	/**
	 * Logs given status.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		VariantSyncPlugin.getDefault().getLog().log(status);
	}
}
