package de.ovgu.variantsync.applicationlayer.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.Util;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.deltacalculation.IDeltaOperations;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.presentationlayer.view.context.MarkerHandler;
import difflib.Patch;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
class ContextHandler {

	private Map<String, Context> contextMap;
	private static ContextHandler instance;
	private IPersistanceOperations persistenceOp = ModuleFactory
			.getPersistanceOperations();
	private Context activeContext;
	private Map<String, List<CodeLine>> mapBaseVersion;

	private ContextHandler() {
		contextMap = new HashMap<String, Context>();
		mapBaseVersion = new HashMap<String, List<CodeLine>>();
	}

	public static ContextHandler getInstance() {
		if (instance == null) {
			instance = new ContextHandler();
		}
		return instance;
	}

	public void activateContext(String featureExpression) {
		if (existsContext(featureExpression)) {
			continueRecording(featureExpression);
		} else {
			startContext(featureExpression);
		}
	}

	private boolean existsContext(String featureExpression) {
		return getContext(featureExpression) != null;
	}

	public void continueRecording(String featureExpression) {
		activeContext = getContext(featureExpression);
	}

	public void stopRecording() {
		if (activeContext != null) {
			contextMap.put(activeContext.getFeatureExpression(), activeContext);
			persistenceOp.saveContext(activeContext,
					Util.parseStorageLocation(activeContext));
		}
		activateContext(VariantSyncConstants.DEFAULT_CONTEXT);
	}

	private void startContext(String featureExpression) {
		if (activeContext != null) {
			contextMap.put(activeContext.getFeatureExpression(), activeContext);
			persistenceOp.saveContext(activeContext,
					Util.parseStorageLocation(activeContext));
		}
		if (contextMap.containsKey(featureExpression)) {
			activeContext = getContext(featureExpression);
		} else {
			activeContext = new Context(featureExpression);
		}
	}

	public void recordCodeChange(String projectName, String pathToProject,
			List<String> changedCode, String className, String packageName,
			List<String> wholeClass) {
		if (!activeContext.containsProject(projectName)) {
			activeContext.initProject(projectName, pathToProject);
		}
		ContextAlgorithm ca = new ContextAlgorithm(activeContext);
		ca.addCode(projectName, packageName, className, changedCode, wholeClass, false);
		UpdateAlgorithm ua = new UpdateAlgorithm();
		ua.updateCode(projectName, packageName, className, changedCode,
				activeContext.getFeatureExpression());
		MarkerHandler.getInstance().updateMarker(projectName, packageName,
				className, activeContext);
	}

	public void addContext(Context c) {
		contextMap.put(c.getFeatureExpression(), c);
	}

	public Context getContext(String featureExpression) {
		if (featureExpression == null) {
			featureExpression = VariantSyncConstants.DEFAULT_CONTEXT;
		}
		return contextMap.get(featureExpression);
	}

	public Collection<Context> getAllContexts() {
		return contextMap.values();
	}

	public void setContextColor(String featureExpression, CodeHighlighting color) {
		Context c = contextMap.get(featureExpression);
		if (c != null) {
			c.setColor(color);
			// Map<String, JavaProject> jps = c.getJavaProjects();
			// Set<Entry<String, JavaProject>> entries = jps.entrySet();
			// Iterator<Entry<String, JavaProject>> it = entries.iterator();
			// while (it.hasNext()) {
			// Entry<String, JavaProject> e = it.next();
			// List<JavaClass> classes = ContextUtils.getClasses(e.getValue());
			// for (JavaClass jc : classes) {
			// List<CodeLine> codelines = jc.getCodeLines();
			// for (CodeLine cl : codelines) {
			// cl.setColor(c.getColor());
			// }
			// }
			// }
			persistenceOp.saveContext(c, Util.parseStorageLocation(c));
		}
	}

	/**
	 * @return the activeContext
	 */
	public Context getActiveContext() {
		return activeContext;
	}

	public void clean() {
		contextMap.clear();
		activeContext = null;
		activateContext(VariantSyncConstants.DEFAULT_CONTEXT);
	}

	public void setLinesOfActualClass(String filename,
			List<CodeLine> linesOfFile) {
		this.mapBaseVersion.put(filename, linesOfFile);
	}

	/**
	 * @return the linesOfFile
	 */
	public List<CodeLine> getLinesOfActualClass(String filename) {
		return this.mapBaseVersion.get(filename);
	}

	public void refreshContext(String fe, String projectName,
			String packageName, String filename, List<String> oldCode,
			List<String> newCode) {
		IDeltaOperations deltaOperations = ModuleFactory.getDeltaOperations();
		Patch patch = deltaOperations.computeDifference(oldCode, newCode);
		List<String> tmpUnifiedDiff = deltaOperations.createUnifiedDifference(
				filename, filename, oldCode, patch, 0);
		Context c = getContext(fe);
		ContextAlgorithm ca = new ContextAlgorithm(c);
		ca.addCode(projectName, packageName, filename, tmpUnifiedDiff, oldCode, true);
		UpdateAlgorithm ua = new UpdateAlgorithm();
		ua.updateCode(projectName, packageName, filename, tmpUnifiedDiff,
				c.getFeatureExpression());
		MarkerHandler.getInstance().updateMarker(projectName, packageName,
				filename, c);
	}
}