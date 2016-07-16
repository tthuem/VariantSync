/**
 * 
 */
package de.ovgu.variantsync.applicationlayer.datamodel.monitoring;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores items which will be monitored by eclipse functionality.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class MonitorItemStorage {

	private List<MonitorItem> monitorItems = new LinkedList<MonitorItem>();

	/**
	 * Adds a monitorItem.
	 * 
	 * @param item
	 *            item to monitor
	 */
	public void addMonitorItem(MonitorItem item) {
		if (this.monitorItems.contains(item)) {
			int index = this.monitorItems.indexOf(item);
			MonitorItem listItem = this.monitorItems.get(index);
			String projects = listItem.getPatchedProject() + " "
					+ item.getPatchedProject();
			listItem.setPatchedProject(projects);
		} else {
			this.monitorItems.add(item);
		}
	}

	/**
	 * Returns all monitored projects containing given changed file.
	 * 
	 * @param patchName
	 *            changed file name
	 * @return list with project names
	 */
	public List<String> getMonitoredProjects(String patchName) {
		List<String> projects = new ArrayList<String>();
		MonitorItem item = new MonitorItem(patchName, "");
		if (this.monitorItems.contains(item)) {
			int index = this.monitorItems.indexOf(item);
			MonitorItem listItem = this.monitorItems.get(index);
			String[] temp = listItem.getPatchedProject().split(" ");
			for (String name : temp) {
				projects.add(name);
			}
		}
		return projects;
	}

	/**
	 * Getter for list of monitor items.
	 * 
	 * @return list of monitor items
	 */
	public List<MonitorItem> getMonitorItems() {
		return monitorItems;
	}

	/**
	 * Sets new list of monitored items.
	 * 
	 * @param monitorItems
	 *            items to monitor
	 */
	public void setMonitorItems(List<MonitorItem> monitorItems) {
		this.monitorItems.clear();
		this.monitorItems.addAll(monitorItems);
	}
}