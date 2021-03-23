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
import de.tubs.variantsync.core.managers.data.ConfigurationProject;
import de.tubs.variantsync.core.managers.data.FeatureContext;
import de.variantsync.core.marker.IVariantSyncMarker;

/**
 * Utilities for creating specialized VariantSync eclipse resource markers
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
		for (final IProject p : projectList) {
			cleanProject(p);
		}
	}

	/**
	 * Removes all markers for the project
	 *
	 * @param project
	 * @throws CoreException
	 */
	public static void cleanProject(IProject project) throws CoreException {
		final List<IMarker> markers = getMarkers(project);
		for (final IMarker marker : markers) {
			try {
				marker.delete();
			} catch (final CoreException e) {
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
		if ((res != null) && res.exists()) {
			final List<IMarker> markers = Arrays.asList(res.findMarkers(IMarker.MARKER, true, IResource.DEPTH_INFINITE));
			for (final IMarker marker : markers) {
				try {
					marker.delete();
				} catch (final CoreException e) {
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
	public static List<IMarker> getMarkers(IResource res) {
		final List<IMarker> returnList = new ArrayList<IMarker>();
		if (res != null) {
			for (final String marker : annotationMarkers) {
				try {
					returnList.addAll(Arrays.asList(res.findMarkers(marker, true, IResource.DEPTH_INFINITE)));
				} catch (final CoreException e) {
					LogOperations.logError("File does not exists or can not be accessed because the project is closed", e);
				}
			}
		}
		return returnList;
	}

	/**
	 * Wrapper for res.getMarker(id)
	 *
	 * @param res
	 * @param id
	 * @return marker with id
	 */
	public static IMarker getMarker(IResource res, long id) {
		return res.getMarker(id);
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
	 * @param context
	 * @param color
	 */
	private static long addMarker(IResource res, int posStart, int posEnd, FeatureContext featureContext) {
		try {
			IMarker marker = null;
			if (res.exists()) {
				marker = res.createMarker(getMarker(featureContext.highlighter));
				marker.setAttribute(IMarker.MESSAGE, String.format("Feature: %s", featureContext.name));
				marker.setAttribute(IMarker.CHAR_START, posStart);
				marker.setAttribute(IMarker.CHAR_END, posEnd);
				return marker.getId();
			}
		} catch (final CoreException e) {
			LogOperations.logError("Marker can not be created", e);
		}
		return -1;
	}

	/**
	 * Adds the given markers to the given file
	 *
	 * @param file
	 * @param markers
	 */
	public static void setMarker(IFile file, List<IVariantSyncMarker> markers) {
		final ConfigurationProject configurationProject = VariantSyncPlugin.getActiveConfigurationProject();
		for (final IVariantSyncMarker mi : markers) {
			long markerId = -1;
			if (mi.isLine()) {
				try {
					IDocument document = null;
					try {
						document = VariantSyncPlugin.getEditor().getDocumentProvider().getDocument(VariantSyncPlugin.getEditor().getEditorInput());
					} catch (final NullPointerException e) {
						LogOperations.logError("Marker line is not available in the document", e);
					}

					final IRegion regionStart = document.getLineInformation(mi.getOffset());
					final IRegion regionEnd = document.getLineInformation(mi.getOffset() + mi.getLength());
					final int start = regionStart.getOffset();
					final int end = regionStart.getOffset() + regionEnd.getLength();

					markerId = addMarker(file, start, end, configurationProject.getFeatureContextManager().getContext(mi.getContext()));
				} catch (final BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				markerId = addMarker(file, mi.getOffset(), mi.getOffset() + mi.getLength(),
						configurationProject.getFeatureContextManager().getContext(mi.getContext()));
			}
			if (markerId != -1) {
				mi.setMarkerId(markerId);
			}
		}
	}

}
