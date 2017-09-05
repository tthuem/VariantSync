package de.tubs.variantsync.core.patch;

import java.util.ArrayList;
import java.util.List;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.patch.interfaces.IPatch;

public class APatch<T> implements IPatch<T> {

	private List<IDelta<T>> deltas = new ArrayList<>();
	private long startTime,endTime;
	private String context;
	
	@Override
	public void addDelta(IDelta<T> delta) {
		deltas.add(delta);
	}

	@Override
	public void addAll(List<IDelta<T>> deltas) {
		deltas.addAll(deltas);
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
		return context;
	}

	@Override
	public void setContext(String context) {
		this.context = context;
	}
}
