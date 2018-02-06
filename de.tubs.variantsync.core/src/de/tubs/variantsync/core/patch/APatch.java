package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.List;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public class APatch<T> implements IPatch<T> {

	private List<IDelta<T>> deltas = new ArrayList<>();
	private long startTime, endTime;
	private String feature;

//	public APatch() {}

	@Override
	public void addDelta(IDelta<T> delta) {
		if (delta.getPatch() == null) delta.setPatch(this);
		if (delta.getContext() == "") delta.setContext(feature);
		deltas.add(delta);
	}

	@Override
	public void addDeltas(List<IDelta<T>> deltas) {
		for (IDelta<T> delta : deltas) {
			if (delta.getPatch() == null) delta.setPatch(this);
			if (delta.getContext() == "") delta.setContext(feature);
		}
		this.deltas.addAll(deltas);
	}

	@Override
	public List<IDelta<T>> getDeltas() {
		return deltas;
	}

	@Override
	public boolean removeDelta(IDelta<T> delta) {
		if (deltas.contains(delta)) {
			deltas.remove(delta);
			return true;
		}
		return false;
	}

	@Override
	public int size() {
		return deltas.size();
	}

	@Override
	public boolean isEmpty() {
		return deltas.isEmpty();
	}

	@Override
	public String toString() {
		return "APatch [deltas=" + deltas + "]";
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(long timestamp) {
		this.startTime = timestamp;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(long timestamp) {
		this.endTime = timestamp;
	}

	@Override
	public String getContext() {
		return feature;
	}

	@Override
	public void setContext(String feature) {
		this.feature = feature;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		APatch<?> other = (APatch<?>) obj;
		if (deltas == null) {
			if (other.deltas != null) return false;
		} else if (!deltas.equals(other.deltas)) return false;
		if (endTime != other.endTime) return false;
		if (feature == null) {
			if (other.feature != null) return false;
		} else if (!feature.equals(other.feature)) return false;
		if (startTime != other.startTime) return false;
		return true;
	}

}
