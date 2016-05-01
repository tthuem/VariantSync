package de.ovgu.variantsync.applicationlayer.context;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.JavaClass;

/**
 * Defines functions to manage contexts.
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 02.09.2015
 */
public interface IContextOperations {

	void activateContext(String featureExpression);

	String getActiveFeatureContext();

	String getActiveProject();

	void recordCodeChange(List<String> tmpUnifiedDiff, String projectName,
			String pathToProject, String packageName, String className,
			List<String> wholeClass);

	void setContextColor(String featureExpression, CodeHighlighting color);

	void stopRecording();

	void addContext(Context c);

	void addCode(String projectName, String packageName, String className,
			List<String> code, List<String> wholeClass);

	void addCode(String projectName, String packageName, String className,
			List<String> code, Context c, List<String> wholeClass);

	Map<String, List<JavaClass>> findJavaClass(String projectName,
			String className);

	CodeHighlighting findColor(String featureExpression);

	Context getContext(String featureExpression);

	void deleteAllContexts();

	Collection<String> getProjects(String fe);

	Collection<String> getClasses(String fe, String projectName);

	List<String> getSyncTargets(String fe, String projectName, String className);

	Collection<CodeChange> getChanges(String fe, String projectName,
			String className);

	List<CodeLine> getTargetCode(String fe, String projectName, String className);

	CodeHighlighting getContextColor(String featureExpression);

	List<CodeLine> getTargetCodeWholeClass(String fe, String projectName,
			String className);

	void setBaseVersion(IFile file);

	List<CodeLine> getLinesOfActualFile(String filename);

	File getFile(String selectedFeatureExpression, String projectNameTarget,
			String classNameTarget);

	void removeChange(String selectedFeatureExpression, String selectedProject,
			String selectedClass, int selectedChange);

	IResource getResource(String selectedFeatureExpression,
			String selectedProject, String selectedClass);

	void refresh(boolean isAutomaticSync, String fe, String projectName,
			String filename, List<CodeLine> codeWC, List<CodeLine> syncCode);

	void removeTagging(String path);
}
