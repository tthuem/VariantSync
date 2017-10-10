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
import de.tubs.variantsync.core.data.Context;
import de.tubs.variantsync.core.data.FeatureExpression;
import de.tubs.variantsync.core.markers.interfaces.IMarkerInformation;

public class MarkerHandler {

	private static MarkerHandler instance = null;
	private static List<String> annotationMarkers = new ArrayList<>();

	private MarkerHandler() {
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.red");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.orange");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.yellow");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.darkgreen");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.lightgreen");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.cyan");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.lightgrey");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.blue");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.margenta");
		annotationMarkers.add("de.tubs.variantsync.marker.highlighter.pink");
	}

	public static MarkerHandler getInstance() {
		if (instance == null) instance = new MarkerHandler();
		return instance;
	}

	/**
	 * Removes all markers for all projects in the list
	 * 
	 * @param projectList
	 * @throws CoreException
	 */
	public void cleanProjects(List<IProject> projectList) throws CoreException {
		for (IProject p : projectList)
			cleanProject(p);
	}

	/**
	 * Removes all markers for the project
	 * 
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
		if (res != null) {
			List<IMarker> markers = Arrays.asList(res.findMarkers(IMarker.MARKER, true, IResource.DEPTH_INFINITE));
			for (IMarker marker : markers) {
				try {
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns all markers for the given resource
	 * 
	 * @param res
	 * @return List<IMarker> - All markers of the resource with DEPTH_INFINITE
	 * @throws CoreException
	 */
	private List<IMarker> getAllMarkers(IResource res) throws CoreException {
		List<IMarker> returnList = new ArrayList<IMarker>();
		if (res != null) {
			for (String marker : annotationMarkers) {
				try {
					returnList.addAll(Arrays.asList(res.findMarkers(marker, true, IResource.DEPTH_INFINITE)));
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return returnList;
	}

	/**
	 * Returns the id of a code highlighting annotation
	 * 
	 * @param color
	 * @return String - The id of the annotation highlighter
	 */
	private static String getMarker(FeatureColor color) {
		if (color == null) {
			return annotationMarkers.get(FeatureColor.Yellow.getValue());
		}
		return annotationMarkers.get(color.getValue());
	}

	/**
	 * Adds a marker to the resource
	 * 
	 * @param res
	 * @param start - Starting line
	 * @param end - Ending line
	 * @param feature
	 * @param color
	 */
	public static void addMarker(IResource res, int offset, int length, FeatureExpression featureExpression) {
		try {
			IMarker marker = null;
			Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
			if (res.exists()) {
				marker = res.createMarker(getMarker(featureExpression.highlighter));
				marker.setAttribute(IMarker.MESSAGE, "Feature: " + featureExpression.name);
				marker.setAttribute(IMarker.CHAR_START, offset);
				marker.setAttribute(IMarker.CHAR_END, length);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void setMarker(IFile file, List<IMarkerInformation> markers) {
		Context context = VariantSyncPlugin.getDefault().getActiveEditorContext();
		for (IMarkerInformation mi : markers) {
			if (mi.isLine()) {
				try {
					IDocument document = null;
					try {
						document = (IDocument) VariantSyncPlugin.getEditor().getDocumentProvider().getDocument(VariantSyncPlugin.getEditor().getEditorInput());
					} catch (NullPointerException e) {
						return;
					}

					for (int i = mi.getOffset(); i <= (mi.getOffset() + mi.getLength() - 1); i++) {
						IRegion regionStart = document.getLineInformation(i - 1);
						int start = regionStart.getOffset();
						int end = regionStart.getOffset() + regionStart.getLength();
//				if (regionStart.getLength() == regionEnd.getLength()
//						&& regionStart.getOffset() == regionEnd.getOffset()) {
//					end = regionStart.getOffset() + regionEnd.getLength();
//				}
						addMarker(file, start, end, context.getFeatureExpression(mi.getFeatureExpression()));
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				addMarker(file, mi.getOffset(), mi.getLength(), context.getFeatureExpression(mi.getFeatureExpression()));
			}
		}
	}

}
