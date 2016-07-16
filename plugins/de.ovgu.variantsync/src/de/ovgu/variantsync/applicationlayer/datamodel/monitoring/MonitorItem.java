package de.ovgu.variantsync.applicationlayer.datamodel.monitoring;

/**
 * Represents an item in eclipse workspace which can be monitored. Contains
 * informations about monitored file and workspace the file belongs to.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class MonitorItem {

	private String patchName = "";
	private String patchedProject = "";

	public MonitorItem() {
	}

	public MonitorItem(String name, String project) {
		this.patchName = name;
		this.patchedProject = project;
	}

	/**
	 * @return the patchName
	 */
	public String getPatchName() {
		return patchName;
	}

	/**
	 * @param patchName
	 *            the patchName to set
	 */
	public void setPatchName(String patchName) {
		this.patchName = patchName;
	}

	/**
	 * @return the patchedProject
	 */
	public String getPatchedProject() {
		return patchedProject;
	}

	/**
	 * @param patchedProject
	 *            the patchedProject to set
	 */
	public void setPatchedProject(String patchedProject) {
		this.patchedProject = patchedProject;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MonitorItem) {
			return this.patchName.equals(((MonitorItem) obj).getPatchName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.patchName.hashCode();
	}

}
