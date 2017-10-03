package de.tubs.variantsync.core.markers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.tubs.variantsync.core.VariantSyncPlugin;

public class MarkerHandler {
	
	private static MarkerHandler instance = null;
	private static List<String> annotationMarkers = new ArrayList<>();
	//private static final String defaultAnnoationMarker = "de.tubs.variantsync.core.annotations.lightgrey1";
	//private static final String markerId = "de.tubs.variantsync.marker";
	
	private MarkerHandler() {
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.lightgrey");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.red");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.orange");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.yellow");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.darkgreen");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.lightgreen");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.cyan");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.blue");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.margenta");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.pink");
	}

	public static MarkerHandler getInstance() {
		if (instance == null)
			instance = new MarkerHandler();
		return instance;
	}

	/**
	 * Removes all markers for all projects in the list
	 * @param projectList
	 * @throws CoreException
	 */
	public void cleanProjects(List<IProject> projectList) throws CoreException {
		for (IProject p : projectList)
			cleanProject(p);
	}

	/**
	 * Removes all markers for the project
	 * @param project
	 * @throws CoreException
	 */
	public void cleanProject(IProject project) throws CoreException {
		List<IMarker> markers = getAllMarkers(project);
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clearResource(IResource res) throws CoreException {
		List<IMarker> markers = Arrays.asList(res.findMarkers(IMarker.MARKER, true, IResource.DEPTH_INFINITE));
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns all markers for the given resource
	 * @param res
	 * @return List<IMarker> - All markers of the resource with DEPTH_INFINITE
	 * @throws CoreException
	 */
	private List<IMarker> getAllMarkers(IResource res) throws CoreException {
		List<IMarker> returnList = new ArrayList<IMarker>();
		for (String marker : annotationMarkers) {
			try {
					returnList.addAll(Arrays.asList(res.findMarkers(marker, true, IResource.DEPTH_INFINITE)));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return returnList;
	}
	
	/**
	 * Returns the id of a code highlighting annotation
	 * @param color
	 * @return String - The id of the annotation highlighter
	 */
	private static String getMarker(FeatureColor color) {
		if (color == null) {
			return annotationMarkers.get(FeatureColor.Yellow.ordinal());
		}
		return annotationMarkers.get(color.ordinal());
	}
	
	/**
	 * Adds a marker to the resource
	 * @param res
	 * @param start - Starting line
	 * @param end - Ending line
	 * @param feature
	 * @param color
	 */
	public static void addMarker(IResource res, int line, int endOffset,
			String feature, FeatureColor color) {
		try {
			IMarker marker = null;
			if (res.exists()) {
				marker = res.createMarker(getMarker(color));
				marker.setAttribute(IMarker.MESSAGE, "Feature: " + feature);
				marker.setAttribute(IMarker.CHAR_START, 20);
				marker.setAttribute(IMarker.CHAR_END, 40);
			}
		
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void setMarker(IFile file, List<MarkerInformation> markers) {
		IDocument document = null;
		try {
			document = (IDocument) VariantSyncPlugin.getEditor().getDocumentProvider()
					.getDocument(VariantSyncPlugin.getEditor().getEditorInput());
		} catch (NullPointerException e) {
			return;
		}
		for (MarkerInformation mi : markers) {
			try {
				IRegion regionStart = document.getLineInformation(mi.getStart());
				IRegion regionEnd = document.getLineInformation(mi.getEnd());
				int start = regionStart.getOffset();
				int end = regionEnd.getOffset() + 2;
				if (regionStart.getLength() == regionEnd.getLength()
						&& regionStart.getOffset() == regionEnd.getOffset()) {
					end = regionStart.getOffset() + regionEnd.getLength();
				}
				addMarker(file, start, end, mi.getFeatureExpression().name, mi.getFeatureExpression().highlighter);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

}
