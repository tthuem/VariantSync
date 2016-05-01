package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangeLogMap {

	private HashMap<Long, ListWrapper> changeLog;

	public ChangeLogMap() {
		changeLog = new HashMap<Long, ListWrapper>();
	}

	public void put(long key, String source, String target) {
		ListWrapper syncTargets = changeLog.get(key);
		if (syncTargets == null) {
			syncTargets = new ListWrapper(new ArrayList<String>());
		}
		syncTargets.add(source + "-" + target);
		syncTargets.add(target + "-" + source);
		changeLog.put(key, syncTargets);
	}

	public boolean contains(long key, String source, String target) {
		ListWrapper syncTargets = changeLog.get(key);
		if (syncTargets == null)
			return false;
		List<String> list = syncTargets.list;
		for (String s : list) {
			String[] tmp = s.split("-");
			String mapSource = tmp[0];
			String mapTarget = tmp[1];
			if (mapSource.equals(source) && mapTarget.equals(target)) {
				return true;
			}
		}
		return false;
	}

	public void removeChange(long key) {
		changeLog.remove(key);
	}

	public HashMap<Long, ListWrapper> getChangeLog() {
		return changeLog;
	}

	/**
	 * @param changeLog
	 *            the changeLog to set
	 */
	public void setChangeLog(HashMap<Long, ListWrapper> changeLog) {
		this.changeLog = changeLog;
	}

}
