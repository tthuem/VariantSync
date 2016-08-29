package de.ovgu.variantsync.applicationlayer.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.deltacalculation.DeltaOperations;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.view.context.MarkerHandler;
import de.ovgu.variantsync.utilities.Util;
import difflib.Patch;

/**
 *
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
class ContextHandler {

	private Map<String, Context> contextMap;
	private static ContextHandler instance;
	private Persistable persistenceOp = ModuleFactory.getPersistanceOperations();
	private Context activeContext;
	private Map<String, List<String>> mapBaseVersion;

	private ContextHandler() {
		contextMap = new HashMap<String, Context>();
		mapBaseVersion = new HashMap<String, List<String>>();
	}

	public static ContextHandler getInstance() {
		if (instance == null) {
			instance = new ContextHandler();
		}
		return instance;
	}

	public void activateContext(String featureExpression) {
		if (contextExists(featureExpression)) {
			continueRecording(featureExpression);
		} else {
			startRecording(featureExpression);
		}
	}

	private boolean contextExists(String featureExpression) {
		return getContext(featureExpression) != null;
	}

	public void continueRecording(String featureExpression) {
		activeContext = getContext(featureExpression);
	}

	public void stopRecording() {
		if (activeContext != null) {
			contextMap.put(activeContext.getFeatureExpression(), activeContext);
			persistenceOp.saveContext(activeContext, Util.parseStorageLocation(activeContext));
		}
		activateContext(VariantSyncConstants.DEFAULT_CONTEXT);
	}

	private void startRecording(String featureExpression) {
		if (activeContext != null) {
			contextMap.put(activeContext.getFeatureExpression(), activeContext);
			persistenceOp.saveContext(activeContext, Util.parseStorageLocation(activeContext));
		}
		if (contextMap.containsKey(featureExpression)) {
			activeContext = getContext(featureExpression);
		} else {
			activeContext = new Context(featureExpression);
		}
	}

	public void recordCodeChange(String projectName, String pathToProject, List<String> changedCode, String className,
			String packageName, List<String> wholeClass, long modificationTime) {
		if (!activeContext.containsProject(projectName)) {
			activeContext.initProject(projectName, pathToProject);
		}
		ContextAlgorithm ca = new ContextAlgorithm(activeContext);
		ca.addCode(projectName, packageName, className, changedCode, wholeClass, false, modificationTime);
		UpdateAlgorithm ua = new UpdateAlgorithm();
		ua.updateCode(projectName, packageName, className, changedCode, activeContext.getFeatureExpression());
		if (packageName.equals("defaultpackage"))
			packageName = "";
		MarkerHandler.getInstance().updateMarker(projectName, packageName, className, activeContext);
		contextMap.put(activeContext.getFeatureExpression(), activeContext);
		persistenceOp.saveContext(activeContext, Util.parseStorageLocation(activeContext));
	}

	public void recordCodeChange(String projectName, String pathToProject, List<String> changedCode, String className,
			String packageName, List<String> wholeClass, boolean ignoreChange, long modificationTime) {
		if (!activeContext.containsProject(projectName)) {
			activeContext.initProject(projectName, pathToProject);
		}
		ContextAlgorithm ca = new ContextAlgorithm(activeContext);
		ca.addCode(projectName, packageName, className, changedCode, wholeClass, ignoreChange, modificationTime);
		UpdateAlgorithm ua = new UpdateAlgorithm();
		ua.updateCode(projectName, packageName, className, changedCode, activeContext.getFeatureExpression());
		if (packageName.equals("defaultpackage"))
			packageName = "";
		MarkerHandler.getInstance().updateMarker(projectName, packageName, className, activeContext);
		contextMap.put(activeContext.getFeatureExpression(), activeContext);
		persistenceOp.saveContext(activeContext, Util.parseStorageLocation(activeContext));
	}

	public void recordFileAdded(String projectName, String pathToProject, String className, String packageName,
			List<String> wholeClass, long modificationTime) {
		ContextAlgorithm ca = new ContextAlgorithm(activeContext);
		ca.addClass(projectName, packageName, className, wholeClass, modificationTime);
	}

	public void recordFileRemoved(String projectName, String pathToProject, String className, String packageName,
			List<String> wholeClass, long modificationTime) {
		ContextAlgorithm ca = new ContextAlgorithm(activeContext);
		ca.removeClass(projectName, packageName, className, wholeClass, modificationTime);
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

	public void setLinesOfActualClass(String filename, List<String> linesOfFile) {
		this.mapBaseVersion.put(filename, linesOfFile);
	}

	/**
	 * @return the linesOfFile
	 */
	public List<String> getLinesOfActualClass(String filename) {
		return this.mapBaseVersion.get(filename);
	}

	public void refreshContext(String fe, String projectName, String packageName, String filename, List<String> oldCode,
			List<String> newCode, long modificationTime) {
		DeltaOperations deltaOperations = ModuleFactory.getDeltaOperations();
		Patch patch = deltaOperations.computeDifference(oldCode, newCode);
		List<String> tmpUnifiedDiff = deltaOperations.createUnifiedDifference(filename, filename, oldCode, patch, 0);
		Context c = getContext(fe);
		ContextAlgorithm ca = new ContextAlgorithm(c);
		ca.addCode(projectName, packageName, filename, tmpUnifiedDiff, oldCode, true, modificationTime);
		UpdateAlgorithm ua = new UpdateAlgorithm();
		ua.updateCode(projectName, packageName, filename, tmpUnifiedDiff, c.getFeatureExpression());
		if (packageName.equals("defaultpackage")) {
			packageName = "";
		}
		MarkerHandler.getInstance().updateMarker(projectName, packageName, filename, c);
	}

}