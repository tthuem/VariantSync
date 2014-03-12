/**
 * 
 */
package de.ovgu.variantsync.resource;

import java.util.ArrayList;
import java.util.LinkedList;

import de.ovgu.variantsync.model.ResourceChangesFilePatch;

/**
 * @author Lei Luo
 * 
 */
public class SynchroInfo {
	public LinkedList<SynchroInfoItem> synchroItems = new LinkedList<SynchroInfoItem>();

	public void addSynchroItem(SynchroInfoItem item) {
		if (this.synchroItems.contains(item)) {
			int index = this.synchroItems.indexOf(item);
			SynchroInfoItem listItem = this.synchroItems.get(index);
			String projects = listItem.getPatchedProject() + " "
					+ item.getPatchedProject();
			listItem.setPatchedProject(projects);
		} else {
			this.synchroItems.add(item);
		}
	}

	public LinkedList<SynchroInfoItem> getSynchroItems() {
		return synchroItems;
	}

	public void setSynchroItems(LinkedList<SynchroInfoItem> synchroItems) {
		this.synchroItems.clear();
		this.synchroItems.addAll(synchroItems);
	}

	public ArrayList<String> getSynchroProjectsFrom(ResourceChangesFilePatch patch) {
		ArrayList<String> projects = new ArrayList<String>();
		SynchroInfoItem item = new SynchroInfoItem(patch.getName(), "");
		if (this.synchroItems.contains(item)) {
			int index = this.synchroItems.indexOf(item);
			SynchroInfoItem listItem = this.synchroItems.get(index);
			String[] temp = listItem.getPatchedProject().split(" ");
			for (String name : temp) {
				projects.add(name);
			}
		}
		return projects;
	}

}
