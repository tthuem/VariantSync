package de.tubs.variantsync.core.utilities;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A progress monitor to keep track of probably time intensive jobs
 *
 * @author Christopher Sontag
 * @since 1.1
 */
public class VariantSyncProgressMonitor implements IProgressMonitor {

	private static final String PREFIX = "VariantSync ";

	private boolean isCanceled = false;
	private String taskName = "";
	private String subTaskName = "";
	public double worked = 0;

	public VariantSyncProgressMonitor(String taskName) {
		super();
		this.taskName = taskName;
	}

	public VariantSyncProgressMonitor(String taskName, String subTaskName) {
		super();
		this.taskName = taskName;
		this.subTaskName = subTaskName;
	}

	@Override
	public void beginTask(String name, int totalWork) {}

	@Override
	public void done() {}

	@Override
	public void internalWorked(double work) {
		worked = work;
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public void setCanceled(boolean value) {
		isCanceled = value;
	}

	@Override
	public void setTaskName(String name) {
		taskName = PREFIX + name;
	}

	@Override
	public void subTask(String name) {
		subTaskName = name;
	}

	@Override
	public void worked(int work) {
		worked = work;
	}

	public String getSubTaskName() {
		return subTaskName;
	}

	public void setSubTaskName(String subTaskName) {
		this.subTaskName = subTaskName;
	}

	public double getWorked() {
		return worked;
	}

	public void setWorked(double worked) {
		this.worked = worked;
	}

	public String getTaskName() {
		return taskName;
	}

}
