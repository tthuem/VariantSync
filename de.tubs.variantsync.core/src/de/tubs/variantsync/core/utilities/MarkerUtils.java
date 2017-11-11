package de.tubs.variantsync.core.utilities;

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
import de.tubs.variantsync.core.patch.interfaces.IMarkerInformation;

/**
 * Utilities for markers
 * 
 * @author Christopher Sontag
 * @since 15.08.2017
 */
public class MarkerUtils {

	private static List<String> annotationMarkers = Arrays.asList("de.tubs.variantsync.marker.highlighter.red", "de.tubs.variantsync.marker.highlighter.orange",
			"de.tubs.variantsync.marker.highlighter.yellow", "de.tubs.variantsync.marker.highlighter.darkgreen",
			"de.tubs.variantsync.marker.highlighter.lightgreen", "de.tubs.variantsync.marker.highlighter.cyan",
			"de.tubs.variantsync.marker.highlighter.lightgrey", "de.tubs.variantsync.marker.highlighter.blue",
			"de.tubs.variantsync.marker.highlighter.margenta", "de.tubs.variantsync.marker.highlighter.pink");

	/**
	 * Removes all markers for all projects in the list
	 * 
	 * @param projectList
	 * @throws CoreException
	 */
	public static void cleanProjects(List<IProject> projectList) throws CoreException {
		for (IProject p : projectList)
			cleanProject(p);
	}

	/**
	 * Removes all markers for the project
	 * 
	 * @param project
	 * @throws CoreException
	 */
	public static void cleanProject(IProject project) throws CoreException {
		List<IMarker> markers = getMarkers(project);
		for (IMarker marker : markers) {
			try {
				marker.delete();
			} catch (CoreException e) {
				throw e;
			}
		}
	}

	/**
	 * Removes all markers from the given resource
	 * 
	 * @param res
	 * @throws CoreException
	 */
	public static void cleanResource(IResource res) throws CoreException {
		if (res != null && res.exists()) {
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
	private static List<IMarker> getMarkers(IResource res) throws CoreException {
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
	private static void addMarker(IResource res, int offset, int length, FeatureExpression featureExpression) {
		try {
			IMarker marker = null;
			if (res.exists()) {
				marker = res.createMarker(getMarker(featureExpression.highlighter));
				marker.setAttribute(IMarker.MESSAGE, "Feature: " + featureExpression.name);
				marker.setAttribute(IMarker.CHAR_START, offset);
				marker.setAttribute(IMarker.CHAR_END, offset + length);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the given markers to the given file
	 * 
	 * @param file
	 * @param markers
	 */
	public static void setMarker(IFile file, List<IMarkerInformation> markers) {
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

					IRegion regionStart = document.getLineInformation(mi.getOffset());
					IRegion regionEnd = document.getLineInformation(mi.getOffset() + mi.getLength());
					int start = regionStart.getOffset();
					int end = regionEnd.getLength();

					addMarker(file, start, end, context.getFeatureExpression(mi.getFeatureExpression()));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				addMarker(file, mi.getOffset(), mi.getLength(), context.getFeatureExpression(mi.getFeatureExpression()));
			}
		}
	}

}
