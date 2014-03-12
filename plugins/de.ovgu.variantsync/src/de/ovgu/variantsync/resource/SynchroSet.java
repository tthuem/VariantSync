package de.ovgu.variantsync.resource;

import java.util.HashSet;

import org.eclipse.core.resources.IResource;

/**
 * @author Lei Luo
 *
 */
public class SynchroSet {
	private final static SynchroSet INSTANCE = new SynchroSet();
	private HashSet<IResource> synchroItems;

	private SynchroSet() {
		synchroItems = new HashSet<IResource>();
	}

	public static SynchroSet getInstance() {
		return INSTANCE;
	}

	public synchronized void addSynchroItem(IResource res) {
		this.synchroItems.add(res);
	}

	public synchronized boolean removeSynchroItem(IResource res) {
		return this.synchroItems.remove(res);
	}
}
