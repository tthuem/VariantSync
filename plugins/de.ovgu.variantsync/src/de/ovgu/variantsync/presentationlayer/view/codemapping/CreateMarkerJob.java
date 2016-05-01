package de.ovgu.variantsync.presentationlayer.view.codemapping;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class CreateMarkerJob extends Job {

	private int start;
	private int end;
	private String feature;
	private IResource res;
	private IMarker marker;
	private String markerId;

	public CreateMarkerJob(String name) {
		super(name);
	}

	public CreateMarkerJob(String name, IResource res, int start, int end,
			String feature, String markerId) {
		super(name);
		this.res = res;
		this.start = start;
		this.end = end;
		this.feature = feature;
		this.markerId = markerId;
	}

	@Override
	protected IStatus run(IProgressMonitor arg0) {
		try {
			marker = null;
			marker = res.createMarker(markerId);
			marker.setAttribute(IMarker.MESSAGE, "Feature: " + feature);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

}