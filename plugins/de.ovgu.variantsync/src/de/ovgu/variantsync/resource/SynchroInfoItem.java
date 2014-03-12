package de.ovgu.variantsync.resource;

/**
 * @author Lei Luo
 * 
 */
public class SynchroInfoItem {
	private String patchName = "";
	private String patchedProject = "";

	public SynchroInfoItem() {
	}

	public SynchroInfoItem(String name, String project) {
		this.patchName = name;
		this.patchedProject = project;
	}

	public String getPatchName() {
		return patchName;
	}

	public void setPatchName(String patchName) {
		this.patchName = patchName;
	}

	public String getPatchedProject() {
		return patchedProject;
	}

	public void setPatchedProject(String patchedProject) {
		this.patchedProject = patchedProject;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SynchroInfoItem) {
			return this.patchName.equals(((SynchroInfoItem) obj).getPatchName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.patchName.hashCode();
	}

}
