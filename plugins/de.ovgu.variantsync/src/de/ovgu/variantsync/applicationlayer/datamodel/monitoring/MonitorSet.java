package de.ovgu.variantsync.applicationlayer.datamodel.monitoring;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;

/**
 * Stores resources in a set.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 18.05.2015
 */
public class MonitorSet {

	private static final MonitorSet INSTANCE = new MonitorSet();
	private Set<IResource> monitorItems;

	/**
	 * Initializes set of IResource-objects.
	 */
	private MonitorSet() {
		monitorItems = new HashSet<IResource>();
	}

	/**
	 * Realizes singleton pattern.
	 * 
	 * @return instance
	 */
	public static MonitorSet getInstance() {
		return INSTANCE;
	}

	/**
	 * Adds a resource object to this set.
	 * 
	 * @param res
	 *            resource to add
	 */
	public synchronized void addSynchroItem(IResource res) {
		this.monitorItems.add(res);
	}

	/**
	 * Removes a resource from this set.
	 * 
	 * @param res
	 *            resource to remove
	 * @return true if resource is successful removed; false otherwise
	 */
	public synchronized boolean removeSynchroItem(IResource res) {
		return this.monitorItems.remove(res);
	}
}