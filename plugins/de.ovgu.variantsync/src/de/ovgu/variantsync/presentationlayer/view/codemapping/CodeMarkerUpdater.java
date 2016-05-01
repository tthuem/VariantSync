package de.ovgu.variantsync.presentationlayer.view.codemapping;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

/*
public class CodeMarkerUpdater implements IMarkerUpdater {
	/*
	*Returns the attributes for which this updater is responsible.
	*If the result is null, the updater assumes responsibility for any attributes.
	*/
/*	@Override
	public String[] getAttribute() {
	      return null;
	}

	@Override
	public String getMarkerType() {
	      //returns the marker type that we are interested in updating
	      return CodeMarkerFactory.MARKER_GREEN;
	}

	@Override
	public boolean updateMarker(IMarker marker, IDocument doc, Position position) {
	      try {
	            int start = position.getOffset();
	            int end = position.getOffset() + position.getLength();
	            marker.setAttribute(IMarker.CHAR_START, start);
	            marker.setAttribute(IMarker.CHAR_END, end);
	            return true;
	      } catch (CoreException e) {
	            return false;
	      }
	}
}
*/