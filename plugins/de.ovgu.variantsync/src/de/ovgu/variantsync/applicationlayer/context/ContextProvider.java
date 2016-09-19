package de.ovgu.variantsync.applicationlayer.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.AbstractModel;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Class;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Context;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Element;
import de.ovgu.variantsync.applicationlayer.datamodel.context.Variant;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FolderOperationException;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.view.context.ConstraintTextValidator;
import de.ovgu.variantsync.ui.view.context.ConstraintTextValidator.ValidationResult;
import de.ovgu.variantsync.ui.view.context.FeatureContextSelection;
import de.ovgu.variantsync.ui.view.context.MarkerHandler;
import de.ovgu.variantsync.utilities.Util;
import difflib.Delta;

/**
 * Receives controller invocation as part of MVC implementation and encapsulates
 * functionality of its package.
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 02.09.2015
 */
public class ContextProvider extends AbstractModel implements ContextOperations {

	private ContextHandler contextHandler;
	private Persistable persistanceOperations = ModuleFactory.getPersistanceOperations();
	private boolean ignoreCodeChange;
	private static final ConstraintTextValidator VALIDATOR = new ConstraintTextValidator();
	private boolean ignoreAfterMerge;
	private static ContextProvider instance;

	private ContextProvider() {
		contextHandler = ContextHandler.getInstance();
	}

	public static ContextProvider getInstance() {
		if (instance == null) {
			instance = new ContextProvider();
		}
		return instance;
	}

	@Override
	public void activateContext(String featureExpression) {
		contextHandler.activateContext(featureExpression);
		ignoreAfterMerge = false;
	}

	@Override
	public void activateContext(String selectedFeatureExpression, boolean ignoreChange) {
		contextHandler.activateContext(selectedFeatureExpression);
		ignoreAfterMerge = ignoreChange;
	}

	@Override
	public String getActiveFeatureContext() {
		return FeatureContextSelection.activeContext;
	}

	@Override
	public String getActiveProject() {
		return FeatureContextSelection.activeProject;
	}

	@Override
	public void recordCodeChange(Collection<String> changedCode, String projectName, String pathToProject,
			String packageName, String className, Collection<String> newVersion, Collection<String> baseVersion,
			long modificationTime) {
		if (ignoreAfterMerge) {
			recordCodeChange(projectName, pathToProject, changedCode, className, packageName, newVersion, baseVersion,
					true, modificationTime);
			return;
		}
		if (!ignoreCodeChange) {
			System.out.println("\n=== Changed Code ===");
			System.out.println(changedCode.toString());
			contextHandler.recordCodeChange(projectName, pathToProject, changedCode, className, packageName, newVersion,
					baseVersion, modificationTime);
		}
		ignoreCodeChange = false;
	}

	@Override
	public void recordCodeChange(String projectName, String pathToProject, Collection<String> changedCode,
			String className, String packageName, Collection<String> wholeClass, Collection<String> baseVersion,
			boolean ignoreChange, long modificationTime) {
		contextHandler.recordCodeChange(projectName, pathToProject, changedCode, className, packageName, wholeClass,
				baseVersion, ignoreChange, modificationTime);
		ignoreChange = false;
	}

	@Override
	public void recordFileAdded(String projectName, String pathToProject, String packageName, String className,
			Collection<String> wholeClass, long modificationTime) {
		contextHandler.recordFileAdded(projectName, pathToProject, className, packageName, wholeClass,
				modificationTime);
	}

	@Override
	public void recordFileRemoved(String projectName, String pathToProject, String packageName, String className,
			Collection<String> wholeClass, long modificationTime) {
		contextHandler.recordFileRemoved(projectName, pathToProject, className, packageName, wholeClass,
				modificationTime);
	}

	@Override
	public void setContextColor(String featureExpression, CodeHighlighting color) {
		contextHandler.setContextColor(featureExpression, color);
	}

	@Override
	public void stopRecording() {
		contextHandler.stopRecording();
	}

	@Override
	public void addContext(Context c) {
		ContextHandler.getInstance().addContext(c);
	}

	@Override
	public void addCode(String projectName, String packageName, String className, Collection<String> code,
			Collection<String> wholeClass, long modificationTime) {
		ContextAlgorithm ca = new ContextAlgorithm(ContextHandler.getInstance().getActiveContext());
		ca.addCode(projectName, packageName, className, code, wholeClass, false, modificationTime);
	}

	@Override
	public void addCode(String projectName, String packageName, String className, Collection<String> code, Context c,
			Collection<String> wholeClass, long modificationTime) {
		ContextAlgorithm ca = new ContextAlgorithm(c);
		ca.addCode(projectName, packageName, className, code, wholeClass, false, modificationTime);
	}

	@Override
	public Map<String, List<Class>> findJavaClass(String projectName, String className) {
		Map<String, List<Class>> result = new HashMap<String, List<Class>>();
		Collection<Context> contexts = ContextHandler.getInstance().getAllContexts();
		for (Context c : contexts) {
			List<Class> classes = new ArrayList<Class>();
			Variant jp = c.getJavaProject(projectName);
			if (jp != null && jp.getName().equals(projectName)) {
				Util.getClassesByClassName(jp.getChildren(), classes, className);
			}
			result.put(c.getFeatureExpression(), classes);
		}
		return result;
	}

	@Override
	public CodeHighlighting findColor(String featureExpression) {
		Collection<Context> contexts = ContextHandler.getInstance().getAllContexts();
		for (Context c : contexts) {
			if (c.getFeatureExpression().equals(featureExpression)) {
				return c.getColor();
			}
		}
		return null;
	}

	@Override
	public Context getContext(String featureExpression) {
		return ContextHandler.getInstance().getContext(featureExpression);
	}

	@Override
	public void deleteAllContexts() {
		contextHandler.clean();
	}

	@SuppressWarnings("serial")
	@Override
	public Collection<String> getProjects(String fe) {
		Context c = ContextHandler.getInstance().getContext(fe);
		if (c != null && c.getJavaProjects() != null)
			return c.getJavaProjects().keySet();
		else
			return new ArrayList<String>() {
			};
	}

	@Override
	public Collection<String> getClasses(String fe, String projectName) {
		Context c = ContextHandler.getInstance().getContext(fe);
		Variant jp = c.getJavaProjects().get(projectName);
		List<Class> classes = ContextUtils.getClasses(jp);
		List<String> classNames = new ArrayList<String>();
		for (Element e : classes) {
			if (checkSyncTarget(projectName, fe)) {
				classNames.add(e.getName());
			}
		}
		return classNames;
	}

	@Override
	public Collection<String> getClassesForVariant(String fe, String projectName) {
		Context c = ContextHandler.getInstance().getContext(fe);
		Map<String, Variant> projects = c.getJavaProjects();
		Set<Entry<String, Variant>> entries = projects.entrySet();
		Iterator<Entry<String, Variant>> it = entries.iterator();
		List<String> classNames = new ArrayList<String>();
		while (it.hasNext()) {
			Entry<String, Variant> e = it.next();
			if (e.getKey().equals(projectName)) {
				continue;
			}
			Variant jp = e.getValue();
			if (jp == null || jp.getChildren() == null) {
				continue;
			}
			List<Class> classes = ContextUtils.getClasses(jp);
			for (Class element : classes) {
				if (!element.getChanges().isEmpty()) {
					if (checkSyncTarget(projectName, fe)) {
						classNames.add(e.getKey() + ": " + element.getName());
					}
				}
			}
		}
		return classNames;
	}

	@Override
	public Collection<CodeChange> getChanges(String fe, String projectName, String className) {
		Context c = ContextHandler.getInstance().getContext(fe);
		Variant jp = c.getJavaProjects().get(projectName);
		List<Element> classes = new ArrayList<Element>();
		ContextUtils.iterateElements(jp.getChildren(), classes);
		for (Element e : classes) {
			if (e.getName().equals(className)) {
				return ((Class) e).getClonedChanges();
			}
		}
		return null;
	}

	// only searches for one class with the given className in target
	// projects. If multiple classes with the same name exist, they will
	// actually be ignored.
	@Override
	public Map<String, Collection<CodeChange>> getChangesForVariant(String fe, String projectName, String className) {
		Context c = ContextHandler.getInstance().getContext(fe);
		Variant jp = c.getJavaProjects().get(projectName);
		Map<String, Variant> projects = c.getJavaProjects();
		Set<Entry<String, Variant>> entries = projects.entrySet();
		Iterator<Entry<String, Variant>> it = entries.iterator();
		Map<String, Collection<CodeChange>> changes = new HashMap<String, Collection<CodeChange>>();
		while (it.hasNext()) {
			Entry<String, Variant> entry = it.next();
			if (jp == null || !entry.getKey().equals(jp.getName())) {
				List<Element> classes = new ArrayList<Element>();
				Variant providingVariant = entry.getValue();
				if (providingVariant != null && providingVariant.getChildren() != null) {
					ContextUtils.iterateElements(providingVariant.getChildren(), classes);
					for (Element e : classes) {
						if (e.getName().equals(className)) {
							changes.put(entry.getKey(), ((Class) e).getClonedChanges());
						}
					}
				}
			}
		}
		return changes;
	}

	@Override
	public boolean isAlreadySynchronized(String fe, long key, String source, String target) {
		Context c = getContext(fe);
		if (target.contains(":"))
			target = target.split(":")[0].trim();
		return c.isSynchronized(key, source, target);
	}

	@Override
	public void addSynchronizedChange(String fe, long key, String source, String target) {
		Context c = getContext(fe);
		if (target.contains(":"))
			target = target.split(":")[0].trim();
		c.addSynchronizedChange(key, source, target);
	}

	@Override
	public Collection<Delta> getConflictingDeltas(Collection<String> ancestor, Collection<String> left,
			Collection<String> right) {
		return ModuleFactory.getMergeOperations().getConflictingDeltas(ancestor, left, right);
	}

	@Override
	public Collection<String> getAutoSyncTargets(String fe, String projectName, String className,
			Collection<String> ancestor, Collection<String> left) {
		List<String> possbileSyncTargets = getSyncTargets(fe, projectName, className);
		List<String> conflictFreeSyncTargets = new ArrayList<String>();
		for (String target : possbileSyncTargets) {
			String[] targetInfo = target.split(":");
			String targetProject = targetInfo[0].trim();
			String targetClass = targetInfo[1].trim();
			List<String> right = getCodeLines(targetProject, targetClass);
			if (right.isEmpty()) {
				conflictFreeSyncTargets.add(target);
				continue;
			}
			if (!ModuleFactory.getMergeOperations().checkConflict(ancestor, left, right)) {
				conflictFreeSyncTargets.add(target);
			}
		}
		return conflictFreeSyncTargets;
	}

	@Override
	public List<String> getAutoSyncTargetsForVariant(String fe, String targetVariant, String className,
			List<CodeLine> ancestor, List<CodeLine> left) {
		List<String> conflictFreeSyncTargets = new ArrayList<String>();
		List<String> right = getCodeLines(targetVariant, className);
		if (right.isEmpty()) {
			conflictFreeSyncTargets.add(targetVariant + ": " + className);
		}
		if (!ModuleFactory.getMergeOperations().checkConflict(Util.parseCodeLinesToString(ancestor),
				Util.parseCodeLinesToString(left), right)) {
			conflictFreeSyncTargets.add(targetVariant + ": " + className);
		}
		return conflictFreeSyncTargets;
	}

	@Override
	public Collection<String> getConflictSyncTargets(String fe, String projectName, String className,
			Collection<String> ancestor, Collection<String> left) {
		List<String> possbileSyncTargets = getSyncTargets(fe, projectName, className);
		List<String> conflictedSyncTargets = new ArrayList<String>();
		for (String target : possbileSyncTargets) {
			String[] targetInfo = target.split(":");
			String targetProject = targetInfo[0].trim();
			String targetClass = targetInfo[1].trim();
			List<String> right = getCodeLines(targetProject, targetClass);
			if (ModuleFactory.getMergeOperations().checkConflict(ancestor, left, right)) {
				conflictedSyncTargets.add(target);
			}
		}
		return conflictedSyncTargets;
	}

	@Override
	public List<String> getConflictedSyncForVariant(String fe, String targetVariant, String className,
			List<CodeLine> ancestor, List<CodeLine> left) {
		List<String> conflictedSyncTargets = new ArrayList<String>();
		List<String> right = getCodeLines(targetVariant, className);
		if (ModuleFactory.getMergeOperations().checkConflict(Util.parseCodeLinesToString(ancestor),
				Util.parseCodeLinesToString(left), right)) {
			conflictedSyncTargets.add(targetVariant + ": " + className);
		}
		return conflictedSyncTargets;
	}

	private List<String> getCodeLines(String projectName, String className) {
		java.util.List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		IResource javaClass = null;
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectName)) {
				try {
					javaClass = ContextUtils.findFileRecursively(p, className);
					break;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			javaClass.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<String> linesOfCode = new ArrayList<String>();
		try {
			linesOfCode = persistanceOperations.readFile(((IFile) javaClass).getContents());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return linesOfCode;
	}

	public List<String> getSyncTargets(String fe, String projectName, String className) {
		List<String> syncTargets = new ArrayList<String>();
		Context c = ContextHandler.getInstance().getContext(fe);
		Map<String, Variant> mapJp = c.getJavaProjects();
		Set<Entry<String, Variant>> entries = mapJp.entrySet();
		Iterator<Entry<String, Variant>> it = entries.iterator();
		Set<String> usedProjects = new HashSet<String>();
		while (it.hasNext()) {
			Entry<String, Variant> e = it.next();
			boolean isValidSyncTarget = checkSyncTarget(e.getKey(), fe);
			if (!e.getKey().equals(projectName) && isValidSyncTarget) {
				List<Element> classes = new ArrayList<Element>();
				Variant jp = e.getValue();
				ContextUtils.iterateElements(jp.getChildren(), classes);
				for (Element element : classes) {
					if (element.getName().equals(className)) {
						syncTargets.add(jp.getName() + ": " + element.getName());
						usedProjects.add(e.getKey());
					}
				}
			}
		}
		List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for (IProject p : supportedProjects) {
			String name = p.getName();
			boolean isValidSyncTarget = checkSyncTarget(name, fe);
			if (!usedProjects.contains(name) && !name.equals(projectName) && isValidSyncTarget) {
				IResource javaClass = null;
				try {
					javaClass = ContextUtils.findFileRecursively(p, className);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (javaClass != null) {
					syncTargets.add(name + ": " + javaClass.getName());
					usedProjects.add(name);
				}
			}
		}
		return syncTargets;
	}

	private boolean checkSyncTarget(String projectName, String fe) {

		// check if feature expression is a feature that exists in target
		// variant
		Collection<String> featuresSyncTarget = Util.getConfiguredFeatures(projectName);
		if (featuresSyncTarget.contains(fe)) {
			return true;
		}

		// check syntax and semantic of feature expressions
		ValidationResult result = VALIDATOR.validateSync(ModuleFactory.getFeatureOperations().getFeatureModel(), fe);
		if (result != ValidationResult.OK) {
			return false;
		}

		// check if feature expression is valid regarding feature configuration
		// of variant
		// TODO if fe is feature expression and not a feature, then proof if
		// features of sync target conform this expression
		if (fe.contains("and") && !fe.contains("or")) {
			boolean isValidTarget = true;
			String[] parts = fe.split("and");
			for (String part : parts) {
				part = part.trim();
				if (!featuresSyncTarget.contains(part)) {
					isValidTarget = false;
				}
			}
			return isValidTarget;
		}
		if (fe.contains("or") && !fe.contains("and")) {
			boolean isValidTarget = false;
			String[] parts = fe.split("or");
			for (String part : parts) {
				part = part.trim();
				if (featuresSyncTarget.contains(part)) {
					isValidTarget = true;
				}
			}
			return isValidTarget;
		}
		// TODO implement more possibilities to evaluate feature expressions

		return false;
	}

	// TODO: use package-name to get full-qualified path to class
	@Override
	public List<CodeLine> getTargetCode(String fe, String projectName, String className) {
		List<CodeLine> targetCode = new ArrayList<CodeLine>();
		Context c = ContextHandler.getInstance().getContext(fe);
		Map<String, Variant> mapJp = c.getJavaProjects();
		Set<Entry<String, Variant>> entries = mapJp.entrySet();
		Iterator<Entry<String, Variant>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, Variant> e = it.next();
			if (e.getKey().equals(projectName)) {
				List<Element> classes = new ArrayList<Element>();
				Variant jp = e.getValue();
				ContextUtils.iterateElements(jp.getChildren(), classes);
				for (Element element : classes) {
					if (element.getName().equals(className)) {
						targetCode.addAll(element.getClonedCodeLines());
					}
				}
			}
		}
		return targetCode;
	}

	@Override
	public List<String> getLinesOfFile(String fe, String projectName, String fileName) {
		List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectName)) {
				IResource javaClass = null;
				try {
					javaClass = ContextUtils.findFileRecursively(p, fileName);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (javaClass != null) {
					IFile file = (IFile) javaClass;
					try {
						file.refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
					try {
						return persistanceOperations.readFile(file.getContents(), file.getCharset());
					} catch (FileOperationException | CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	@Override
	public CodeHighlighting getContextColor(String featureExpression) {
		return contextHandler.getContext(featureExpression).getColor();
	}

	@Override
	public void setBaseVersion(IFile file) {
		List<String> linesOfFile = null;
		try {
			file.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		try {
			linesOfFile = persistanceOperations.readFile(file.getContents(), file.getCharset());
		} catch (FileOperationException | CoreException e) {
			e.printStackTrace();
		}
		ContextHandler.getInstance().setLinesOfActualClass(file.getName(), linesOfFile);
	}

	@Override
	public List<String> getLinesOfActualFile(String filename) {
		return ContextHandler.getInstance().getLinesOfActualClass(filename);
	}

	@Override
	public File getFile(String selectedFeatureExpression, String projectNameTarget, String classNameTarget) {
		IResource res = ContextUtils.findResource(projectNameTarget, classNameTarget);
		return new File(res.getLocation().toString());

	}

	@Override
	public void removeChange(String selectedFeatureExpression, String selectedProject, String selectedClass,
			int selectedChange, long timestamp) {
		Context c = ContextHandler.getInstance().getContext(selectedFeatureExpression);
		if (selectedClass.contains(":")) {
			String[] tmp = selectedClass.split(":");
			selectedProject = tmp[0].trim();
			selectedClass = tmp[1].trim();
		}
		Variant jp = c.getJavaProjects().get(selectedProject);
		List<Element> classes = new ArrayList<Element>();
		ContextUtils.iterateElements(jp.getChildren(), classes);
		for (Element e : classes) {
			if (e.getName().equals(selectedClass)) {
				((Class) e).removeChange(selectedChange);
				break;
			}
		}
		try {
			List<IProject> projects = VariantSyncPlugin.getDefault().getSupportProjectList();
			for (IProject p : projects) {
				if (p.getName().equals(selectedProject)) {
					p.getFolder(VariantSyncConstants.CHANGES_PATH).refreshLocal(IResource.DEPTH_INFINITE, null);
					IFolder f = p.getFolder(VariantSyncConstants.CHANGES_PATH + String.valueOf(timestamp));
					persistanceOperations.deldir(f,
							new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
									+ VariantSyncConstants.CHANGES_PATH + String.valueOf(timestamp)));
				}
			}
		} catch (FolderOperationException | CoreException e1) {
			e1.printStackTrace();
		}
		persistanceOperations.saveContext(c, Util.parseStorageLocation(c));
	}

	@Override
	public IResource getResource(String selectedFeatureExpression, String selectedProject, String selectedClass) {
		List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(selectedProject)) {
				IResource javaClass = null;
				try {
					javaClass = ContextUtils.findFileRecursively(p, selectedClass);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (javaClass != null)
					return javaClass;
			}
		}
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (!name.equals(selectedProject)) {
				IResource javaClass = null;
				try {
					javaClass = ContextUtils.findFileRecursively(p, selectedClass);
					if (javaClass != null) {
						String projectName = javaClass.getLocation().toString();
						projectName = projectName.replace(name, selectedProject);
						ModuleFactory.getPersistanceOperations().writeFile(new ArrayList<CodeLine>() {
						}, new File(projectName));
						return getResource(selectedFeatureExpression, selectedProject, selectedClass);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public void removeTagging(String path) {
		String projectName = path.substring(0, path.indexOf("/"));
		String className = path.substring(path.lastIndexOf("/") + 1);
		Collection<Context> coll = ContextHandler.getInstance().getAllContexts();
		Iterator<Context> it = coll.iterator();
		while (it.hasNext()) {
			Context c = it.next();
			Variant jp = c.getJavaProjects().get(projectName);
			if (jp != null) {
				List<Element> classes = new ArrayList<Element>();
				ContextUtils.iterateElements(jp.getChildren(), classes);
				for (Element e : classes) {
					if (e.getName().equals(className)) {
						((Class) e).removeContent();
						IResource res = ContextUtils.findResource(projectName, className);
						MarkerHandler.getInstance().clearAllMarker(res);
						return;
					}
				}
			}
		}
	}

	@Override
	public List<String> getFeatures(String variant) {
		List<String> features = new ArrayList<String>();
		Collection<Context> contexts = contextHandler.getAllContexts();
		Iterator<Context> itC = contexts.iterator();
		while (itC.hasNext()) {
			Context c = itC.next();
			Map<String, Variant> projects = c.getJavaProjects();
			Set<Entry<String, Variant>> entries = projects.entrySet();
			Iterator<Entry<String, Variant>> it = entries.iterator();
			while (it.hasNext()) {
				Entry<String, Variant> e = it.next();
				if (e.getKey().equals(variant)) {
					continue;
				}
				Variant jp = e.getValue();
				if (jp == null || jp.getChildren() == null) {
					continue;
				}
				List<Class> classes = ContextUtils.getClasses(jp);
				for (Class element : classes) {
					if (!element.getChanges().isEmpty() && !features.contains(c.getFeatureExpression())) {
						features.add(c.getFeatureExpression());
						break;
					}
				}
			}
		}
		return features;
	}

	@Override
	public void refresh(boolean isAutomaticSync, String fe, String projectName, String filename,
			Collection<String> codeWC, Collection<String> syncCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setManualMergeResult(Delta rightDelta, File mergeResult, File file) {
		// TODO: replace new lines with existing ones in target
		// (right file) - chunk+1

		List<String> mergedLines = null;
		List<String> orgLines = null;
		try {
			mergedLines = new ArrayList<String>(persistanceOperations.readFile(new FileInputStream(mergeResult)));
			orgLines = new ArrayList<String>(persistanceOperations.readFile(new FileInputStream(file)));
		} catch (FileNotFoundException | FileOperationException e) {
			e.printStackTrace();
		}
		System.out.println("Insert Merge Result");
		System.out.println(orgLines.toString());
		int i = 0;
		Iterator<?> it = rightDelta.getRevised().getLines().iterator();
		while (it.hasNext()) {
			it.next();
			System.out.println("Removed: " + orgLines.get(rightDelta.getRevised().getPosition() + i));
			orgLines.remove(rightDelta.getRevised().getPosition() + i);
		}
		System.out.println(orgLines.toString());
		i = 0;
		for (String line : mergedLines) {
			orgLines.add(rightDelta.getRevised().getPosition() + i, line);
			i++;
		}
		System.out.println(orgLines.toString());

		persistanceOperations.writeFile(orgLines, file);
	}

	@Override
	public boolean hasProjectChanges(String fe, String project) {
		return !getClazzesWithChanges(fe, project).isEmpty();
	}

	@Override
	public boolean hasClassChanges(String fe, String project, String clazz) {
		return !getChanges(fe, project, clazz).isEmpty();
	}

	@Override
	public Collection<String> getProjectsWithChanges(String fe) {
		Collection<String> projects = getProjects(fe);
		Collection<String> projectsWithChanges = new ArrayList<String>();
		for (String project : projects) {
			if (!getClazzesWithChanges(fe, project).isEmpty()) {
				projectsWithChanges.add(project);
			}
		}
		return projectsWithChanges;
	}

	@Override
	public Collection<String> getClazzesWithChanges(String fe, String project) {
		Collection<String> clazzes = getClasses(fe, project);
		Collection<String> clazzesWithChanges = new ArrayList<String>();
		for (String clazz : clazzes) {
			if (hasClassChanges(fe, project, clazz)) {
				clazzesWithChanges.add(clazz);
			}
		}
		return clazzesWithChanges;
	}

}