package de.tubs.variantsync.core.data;

import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;

public class CodeMapping {

	private String code;
	private IMarkerInformation markerInformation;

	/**
	 * Constructor
	 * @param code
	 * @param markerInformation
	 */
	public CodeMapping(String code, IMarkerInformation markerInformation) {
		this.code = code;
		this.markerInformation = markerInformation;
	}

	/**
	 * Returns mapped code
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets mapped code
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Returns marker information
	 * @return markerInformation
	 */
	public IMarkerInformation getMarkerInformation() {
		return markerInformation;
	}

	/**
	 * Sets marker information
	 * @param markerInformation
	 */
	public void setMarkerInformation(IMarkerInformation markerInformation) {
		this.markerInformation = markerInformation;
	}

}
