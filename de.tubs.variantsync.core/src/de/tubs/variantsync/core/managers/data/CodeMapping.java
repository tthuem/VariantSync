package de.tubs.variantsync.core.managers.data;

import de.tubs.variantsync.core.utilities.IVariantSyncMarker;

public class CodeMapping {

	private String code;
	private IVariantSyncMarker variantSyncMarker;

	/**
	 * Constructor
	 * @param code
	 * @param variantSyncMarker
	 */
	public CodeMapping(String code, IVariantSyncMarker variantSyncMarker) {
		this.code = code;
		this.variantSyncMarker = variantSyncMarker;
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
	 * @return variantSyncMarker
	 */
	public IVariantSyncMarker getMarkerInformation() {
		return variantSyncMarker;
	}

	/**
	 * Sets marker information
	 * @param variantSyncMarker
	 */
	public void setMarkerInformation(IVariantSyncMarker variantSyncMarker) {
		this.variantSyncMarker = variantSyncMarker;
	}

}
