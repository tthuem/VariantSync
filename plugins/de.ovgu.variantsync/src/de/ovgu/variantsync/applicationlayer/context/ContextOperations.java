package de.ovgu.variantsync.applicationlayer.context;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;

/**
 * Defines functions to manage contexts.
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
public interface ContextOperations {

	void activateContext(String featureExpression);

	String getActiveFeatureContext();

	String getActiveProject();

	void recordCodeChange(List<String> tmpUnifiedDiff, String projectName, String pathToProject, String packageName,
			String className, List<String> wholeClass, long modificationTime);

	void recordFileAdded(String projectName, String pathToProject, String packageName, String className,
			List<String> wholeClass, long modificationTime);

	void recordFileRemoved(String projectName, String pathToProject, String packageName, String className,
			List<String> wholeClass, long modificationTime);

	void setContextColor(String featureExpression, CodeHighlighting color);

	void stopRecording();

	void addContext(Context c);

	void addCode(String projectName, String packageName, String className, List<String> code, List<String> wholeClass, long modificationTime);

	void addCode(String projectName, String packageName, String className, List<String> code, Context c,
			List<String> wholeClass, long modificationTime);

	Map<String, List<Class>> findJavaClass(String projectName, String className);

	CodeHighlighting findColor(String featureExpression);

	Context getContext(String featureExpression);

	void deleteAllContexts();

	Collection<String> getProjects(String fe);

	Collection<String> getClasses(String fe, String projectName);

	List<String> getAutoSyncTargets(String fe, String projectName, String className, List<String> ancestor,
			List<String> left);

	List<String> getConflictSyncTargets(String fe, String projectName, String className, List<String> ancestor,
			List<String> left);

	Collection<CodeChange> getChanges(String fe, String projectName, String className);

	List<CodeLine> getTargetCode(String fe, String projectName, String className);

	CodeHighlighting getContextColor(String featureExpression);

	List<String> getLinesOfFile(String fe, String projectName, String className);

	void setBaseVersion(IFile file);

	List<String> getLinesOfActualFile(String filename);

	File getFile(String selectedFeatureExpression, String projectNameTarget, String classNameTarget);

	IResource getResource(String selectedFeatureExpression, String selectedProject, String selectedClass);

	void refresh(boolean isAutomaticSync, String fe, String projectName, String filename, List<String> codeWC,
			List<String> syncCode);

	void removeTagging(String path);

	void recordCodeChange(String projectName, String pathToProject, List<String> changedCode, String className,
			String packageName, List<String> wholeClass, boolean ignoreChange, long modificationTime);

	void activateContext(String selectedFeatureExpression, boolean ignoreChange);

	boolean isAlreadySynchronized(String fe, long key, String source, String target);

	void addSynchronizedChange(String fe, long key, String source, String target);

	void removeChange(String selectedFeatureExpression, String selectedProject, String selectedClass,
			int selectedChange, long timestamp);

	List<String> getSyncTargets(String fe, String projectName, String className);

	List<String> getFeatures(String variant);

	Map<String, Collection<CodeChange>> getChangesForVariant(String fe, String projectName, String className);

	Collection<String> getClassesForVariant(String fe, String projectName);

	List<String> getAutoSyncTargetsForVariant(String fe, String targetVariant, String className,
			List<CodeLine> ancestor, List<CodeLine> left);

	List<String> getConflictedSyncForVariant(String fe, String targetVariant, String className, List<CodeLine> ancestor,
			List<CodeLine> left);

}
