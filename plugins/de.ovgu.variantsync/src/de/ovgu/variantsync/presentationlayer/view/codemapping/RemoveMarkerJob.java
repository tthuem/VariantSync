package de.ovgu.variantsync.presentationlayer.view.codemapping;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class RemoveMarkerJob extends Job {

	private IResource res;

	public RemoveMarkerJob(String name) {
		super(name);
	}

	public RemoveMarkerJob(IResource res) {
		super(res.getName());
		this.res = res;
	}

	@Override
	protected IStatus run(IProgressMonitor arg0) {
		List<IMarker> markers = CodeMarkerFactory.findMarkers(res);
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

}