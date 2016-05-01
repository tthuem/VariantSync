package de.ovgu.variantsync.applicationlayer.datamodel.diff;

public class DiffStep {

	private boolean addFlag;
	private String code;

	public DiffStep(boolean addFlag, String code) {
		this.addFlag = addFlag;
		this.code = code;
	}

	/**
	 * @return the addFlag
	 */
	public boolean isAddFlag() {
		return addFlag;
	}

	/**
	 * @param addFlag
	 *            the addFlag to set
	 */
	public void setAddFlag(boolean addFlag) {
		this.addFlag = addFlag;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DiffStep [addFlag=" + addFlag + ", code=" + code + "]";
	}

}
