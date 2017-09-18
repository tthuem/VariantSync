package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.List;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public class APatch<T> implements IPatch<T> {

	private List<IDelta<T>> deltas = new ArrayList<>();
	private long startTime,endTime;
	private String feature;
	private String factoryID;
	
	public APatch(String factoryID) {
		this.factoryID = factoryID;
	}
	
	@Override
	public void addDelta(IDelta<T> delta) {
		delta.setPatch(this);
		delta.setFeature(feature);
		deltas.add(delta);
	}

	@Override
	public void addDeltas(List<IDelta<T>> deltas) {
		for (IDelta<T> delta : deltas) {
			delta.setPatch(this);
			delta.setFeature(feature);
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
	public String getFeature() {
		return feature;
	}

	@Override
	public void setFeature(String feature) {
		this.feature = feature;
	}

	@Override
	public String getFactoryID() {
		return this.factoryID;
	}
}
