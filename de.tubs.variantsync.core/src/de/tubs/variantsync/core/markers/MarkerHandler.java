package de.tubs.variantsync.core.markers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

public class MarkerHandler {
	
	private static MarkerHandler instance = null;
	private static List<String> annotationMarkers = new ArrayList<>();
	//private static final String defaultAnnoationMarker = "de.ovgu.featureide.ui.editors.annotations.lightgrey1";
	//private static final String markerId = "de.tubs.variantsync.marker";
	
	private MarkerHandler() {
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.lightgrey1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.red1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.orange1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.yellow1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.darkgreen1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.lightgreen1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.cyan1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.blue1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.margenta1");
		annotationMarkers.add("de.ovgu.featureide.ui.editors.annotations.pink1");
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
	private static String getMarker(CodeHighlighting color) {
		if (color == null) {
			return annotationMarkers.get(CodeHighlighting.YELLOW.ordinal());
		}
		return annotationMarkers.get(color.ordinal());
	}
	
	/**
	 * Adds a annotation to the current file in the editor
	 * @param marker
	 * @param editor
	 * @param annotation
	 * @param offset
	 * @param length
	 */
	public static void addAnnotation(IMarker marker, ITextEditor editor, String annotation, int offset, int length) {
		// The DocumentProvider enables to get the document currently loaded in
		// the editor
		IDocumentProvider idp = editor.getDocumentProvider();

		// This is the document we want to connect to. This is taken from the
		// current editor input.
		IDocument document = idp.getDocument(editor.getEditorInput());

		// The IannotationModel enables to add/remove/change annotation to a
		// Document loaded in an Editor
		IAnnotationModel iamf = idp.getAnnotationModel(editor.getEditorInput());

		// Note: The annotation type id specify that you want to create one of
		// your annotations
		String an = "de.tubs.variantsync.annoations";
		SimpleMarkerAnnotation ma = new SimpleMarkerAnnotation(an, marker);

		// Finally add the new annotation to the model
		iamf.connect(document);
		iamf.addAnnotation(ma, new Position(offset, length));
		iamf.disconnect(document);
	}
	
	/**
	 * Adds a marker to the resource
	 * @param res
	 * @param start - Starting line
	 * @param end - Ending line
	 * @param feature
	 * @param color
	 */
	public static void addMarker(IResource res, int start, int end,
			String feature, CodeHighlighting color) {
		try {
			IMarker marker = null;
			if (res.exists()) {
				marker = res.createMarker(getMarker(color));
				marker.setAttribute(IMarker.MESSAGE, "Feature: " + feature);
				marker.setAttribute(IMarker.CHAR_START, start);
				marker.setAttribute(IMarker.CHAR_END, end);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
