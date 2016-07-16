package de.ovgu.variantsync.applicationlayer.datamodel.exception;

/**
 * Exception which can be thrown in persistancelayer reading or writing xml
 * files.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 19.05.2015
 */
@SuppressWarnings("serial")
public class XMLException extends Exception {

	/**
	 * Creates exception with individual error message and throwable object
	 * containing stacktrace.
	 * 
	 * @param arg0
	 *            individual error message
	 * @param arg1
	 *            throwable object containing stacktrace
	 */
	public XMLException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
