package de.ovgu.variantsync.ui.view.mergeprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.variantsync.VariantSyncConstants;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.ContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.datamodel.exception.FileOperationException;
import de.ovgu.variantsync.applicationlayer.merging.ResourceCompareInput;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.controller.ContextController;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.FeatureController;
import de.ovgu.variantsync.ui.controller.SynchronizationController;
import difflib.Delta;

public abstract class SynchronizationView extends ViewPart {

	protected ContextController cc = ControllerHandler.getInstance().getContextController();
	protected SynchronizationController sc = ControllerHandler.getInstance().getSynchronizationController();
	protected FeatureController fc = ControllerHandler.getInstance().getFeatureController();
	protected ContextOperations contextOperations = ModuleFactory.getContextOperations();
	protected Persistable persOp = ModuleFactory.getPersistanceOperations();

	public static boolean isManSynced;

	protected Collection<String> base;
	protected Collection<String> left;

	protected List projects;
	protected List classes;
	protected Delta leftDelta;
	protected Delta rightDelta;
	protected File fRightVersion;
	protected IResource right;
	protected String selectedProject;
	protected Table newCode;
	protected List autoSyncTargets;
	protected List manualSyncTargets;
	protected Collection<String> manualSyncTargetsAsList;
	protected Collection<CodeChange> collChanges;
	protected String selectedClass;
	protected long timestamp;
	protected List changes;
	protected int selectedChange;

	protected Button btnSynchronize;
	protected Button btnManualSync;

	abstract protected void setChanges();

	protected void tagManualSyncedCode(String fe, long timestamp, String project, String clazz, Collection<String> base,
			Collection<String> left, String manualSelection) {
		if (isManSynced) {

			contextOperations.activateContext(fe, true);
			contextOperations.setManualMergeResult(rightDelta, fRightVersion,
					new File(right.getLocationURI().getPath()));
			try {
				right.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			contextOperations.stopRecording();

			cc.addSynchronizedChange(fe, timestamp, selectedProject, manualSelection);

			if (newCode != null)
				newCode.removeAll();

			// refreshSyncTargets(fe, project, clazz, base, left);

			isManSynced = false;
		}
	}

	protected void refreshSyncTargets(String fe, String project, String clazz, Collection<String> base,
			Collection<String> left) {

		boolean isAutoSyncPossible = false;
		String[] autoItems = cc.getAutoSyncTargets(fe, project, clazz, base, left).toArray(new String[] {});
		java.util.List<String> checkedItems = new ArrayList<String>();
		for (String target : autoItems) {
			if (!contextOperations.isAlreadySynchronized(fe, timestamp, selectedProject, target)) {
				checkedItems.add(target);
			}
		}
		autoSyncTargets.setItems(checkedItems.toArray(new String[] {}));
		if (!checkedItems.isEmpty())
			isAutoSyncPossible = true;

		boolean isManualSyncPossible = false;
		manualSyncTargetsAsList = cc.getConflictedSyncTargets(fe, selectedProject, selectedClass, base, left);
		String[] manualItems = manualSyncTargetsAsList.toArray(new String[] {});
		checkedItems = new ArrayList<String>();
		for (String target : manualItems) {
			if (!contextOperations.isAlreadySynchronized(fe, timestamp, selectedProject, target)) {
				checkedItems.add(target);
			}
		}
		manualSyncTargets.setItems(checkedItems.toArray(new String[] {}));
		manualSyncTargetsAsList = checkedItems;
		if (!checkedItems.isEmpty())
			isManualSyncPossible = true;

		if (!isAutoSyncPossible && !isManualSyncPossible) {
			contextOperations.removeChange(fe, selectedProject, selectedClass, selectedChange, timestamp);
		}
	}

	protected void processClassSelection(String selClass) {
		btnSynchronize.setEnabled(false);
		btnManualSync.setEnabled(false);
		if (autoSyncTargets != null)
			autoSyncTargets.setItems(new String[] {});
		if (manualSyncTargets != null)
			manualSyncTargets.setItems(new String[] {});
		if (newCode != null)
			newCode.removeAll();
		selectedClass = selClass;
		setChanges();
	}

	protected CodeChange processChangeSelection(String fe) {
		if (newCode != null)
			newCode.removeAll();
		selectedChange = changes.getSelectionIndex();
		Iterator<CodeChange> it = collChanges.iterator();
		int i = 0;
		newCode.removeAll();
		CodeHighlighting ccolor = cc.getContextColor(fe);
		while (it.hasNext()) {
			CodeChange ch = it.next();
			if (i == selectedChange) {
				timestamp = ch.getTimestamp();
				newCode.removeAll();
				for (CodeLine cl : ch.getNewVersion()) {
					TableItem item = new TableItem(newCode, SWT.NONE);
					item.setText(cl.getLine() + ": " + cl.getCode());
					Color color = new Color(getSite().getShell().getDisplay(), ccolor.getRGB());
					item.setBackground(color);
				}

				left = cc.getNewCode(ch);
				base = cc.getBaseCode(ch);

				refreshSyncTargets(fe, selectedProject, selectedClass, base, left);
				return ch;
			}
			i++;
		}
		return null;
	}

	protected void processAutoSync(String fe, String projectNameTarget, String classNameTarget,
			Collection<String> syncCode, String autoSelection, int selectionIndex) {
		ModuleFactory.getContextOperations().activateContext(fe);
		contextOperations.activateContext(fe, true);
		solveChange(syncCode, fe, projectNameTarget, classNameTarget, true);
		contextOperations.stopRecording();
		btnSynchronize.setEnabled(false);

		cc.addSynchronizedChange(fe, timestamp, selectedProject, autoSelection);

		if (autoSyncTargets != null) {
			autoSyncTargets.remove(selectionIndex);
		}

		removeChange(fe, selectedProject, selectedClass, selectedChange, timestamp);

		setChanges();
	}

	private void removeChange(String fe, String project, String clazz, int change, long timestamp) {
		if ((autoSyncTargets == null && manualSyncTargets == null)
				|| ((autoSyncTargets.getItems() != null && manualSyncTargets.getItems() != null)
						&& (autoSyncTargets.getItems().length == 0 && manualSyncTargets.getItems().length == 0))) {
			contextOperations.removeChange(fe, project, clazz, change, timestamp);
			if (newCode != null) {
				newCode.removeAll();
			}
			if (!contextOperations.hasClassChanges(fe, project, clazz) && classes != null) {
				classes.remove(clazz);
			}
			if (!contextOperations.hasProjectChanges(fe, project) && projects != null) {
				projects.remove(project);
			}
		}
	}

	protected void processManualSync(String fe, String projectName, String className, String manualSelection,
			int selectionIndex) {
		btnManualSync.setEnabled(false);

		try {
			syncWithEclipse(base, left, selectedProject, selectedClass, projectName, className);
		} catch (FileOperationException | CoreException ex) {
			ex.printStackTrace();
		}
		tagManualSyncedCode(fe, timestamp, selectedProject, selectedClass, base, left, manualSelection);

		if (manualSyncTargets != null)
			manualSyncTargets.remove(selectionIndex);

		removeChange(fe, selectedProject, selectedClass, selectedChange, timestamp);

		setChanges();
	}

	protected void syncWithEclipse(Collection<String> base2, Collection<String> left2, String projectName,
			String className, String projectNameRight, String classNameRight)
			throws FileOperationException, CoreException {

		// Right Version
		java.util.List<IProject> supportedProjects = VariantSyncPlugin.getDefault().getSupportProjectList();
		right = null;
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectNameRight)) {
				try {
					right = cc.findFileRecursively(p, classNameRight);
					break;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		Collection<Delta> deltas = null;
		try {
			deltas = cc.getConflictingDeltas(base2, left2,
					persOp.readFile(new FileInputStream(new File(right.getLocationURI().getPath()))));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		leftDelta = null;
		rightDelta = null;
		int i = 0;
		for (Delta d : deltas) {
			if (i == 0) {
				leftDelta = d;
			} else if (i == 1) {
				rightDelta = d;
			}
			i++;
		}

		// Base Version
		File f = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
				+ VariantSyncConstants.MERGE_PATH + "/BaseVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile((Collection<String>) leftDelta.getOriginal().getLines(), f);

		f = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + VariantSyncConstants.MERGE_PATH
				+ "/LeftVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile((Collection<String>) leftDelta.getRevised().getLines(), f);

		fRightVersion = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
				+ VariantSyncConstants.MERGE_PATH + "/RightVersion.java");
		if (!fRightVersion.exists())
			try {
				fRightVersion.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile((Collection<String>) rightDelta.getRevised().getLines(),
				fRightVersion);

		IResource base = null;
		IProject p = null;
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.getName().equals("variantsyncFeatureInfo")) {
				p = project;
				base = cc.findFileRecursively(project, "BaseVersion");
			}
		}
		base = p.getFolder("merge").getFile("BaseVersion.java");

		final IResource left = p.getFolder("merge").getFile("LeftVersion.java");
		final IResource rightMerge = p.getFolder("merge").getFile("RightVersion.java");

		// Editor
		org.eclipse.compare.CompareConfiguration compconf = new org.eclipse.compare.CompareConfiguration();
		compconf.setLeftLabel(projectName + ": " + className + " - changed version (left)");
		compconf.setRightLabel(projectNameRight + ": " + classNameRight + "");
		compconf.setAncestorLabel(projectName + ": " + className + " - version without change (ancestor)");
		compconf.setLeftEditable(false);
		compconf.setRightEditable(true);

		CompareEditorInput rci = new ResourceCompareInput(compconf, base, left, rightMerge);
		rci.setDirty(true);

		CompareUI.openCompareDialog(rci);
	}

	protected void solveChange(Collection<String> syncCode2, String selectedFeatureExpression, String projectName,
			String className, boolean refreshGUI) {
		IResource res = contextOperations.getResource(selectedFeatureExpression, projectName, className);
		IFile f = (IFile) res;
		try {
			f.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		java.util.List<String> source = new ArrayList<String>();
		for (String line : syncCode2) {
			source.add(line + "\n");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (String line : source) {
			try {
				baos.write(line.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		byte[] bytes = baos.toByteArray();
		InputStream in = new ByteArrayInputStream(bytes);
		try {
			f.setContents(in, true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			f.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	protected void processBatchSynchronization(String fe, String variant, String clazz,
			Collection<CodeChange> collChanges) {
		Iterator<CodeChange> it = collChanges.iterator();
		while (it.hasNext()) {
			CodeChange ch = it.next();
			long timestamp = ch.getTimestamp();

			left = cc.getNewCode(ch);
			base = cc.getBaseCode(ch);

			String[] autoItems = cc.getAutoSyncTargets(fe, variant, clazz, base, left).toArray(new String[] {});
			java.util.List<String> checkedItems = new ArrayList<String>();
			for (String target : autoItems) {
				if (!contextOperations.isAlreadySynchronized(fe, timestamp, selectedProject, target)) {
					checkedItems.add(target);
				}
			}

			for (String target : checkedItems) {
				String t[] = target.split(":");
				String targetProject = t[0].trim();
				String targetClass = t[1].trim();
				java.util.List<String> codeWC = cc.getTargetFile(fe, targetProject, targetClass);

				ModuleFactory.getContextOperations().activateContext(fe);
				Collection<String> syncCode = sc.doAutoSync(cc.getNewCode(ch), cc.getBaseCode(ch), codeWC);
				solveChange(syncCode, fe, targetProject, targetClass, false);
				cc.addSynchronizedChange(fe, timestamp, selectedProject, target);
			}

			// manueller Anteil
			java.util.Collection<String> manualSyncTargetsAsList = cc.getConflictedSyncTargets(fe, variant, clazz,
					cc.getBaseCode(ch), cc.getNewCode(ch));
			String[] manualItems = manualSyncTargetsAsList.toArray(new String[] {});
			checkedItems = new ArrayList<String>();
			for (String target : manualItems) {
				if (!contextOperations.isAlreadySynchronized(fe, timestamp, selectedProject, target)) {
					checkedItems.add(target);
				}
			}
			for (String target : checkedItems) {
				String t[] = target.split(":");
				String targetProject = t[0].trim();
				String targetClass = t[1].trim();
				try {
					Collection<String> baseCode = cc.getBaseCode(ch);
					Collection<String> newCode = cc.getNewCode(ch);

					syncWithEclipse(cc.getBaseCode(ch), cc.getNewCode(ch), variant, clazz, targetProject, targetClass);
					tagManualSyncedCode(fe, timestamp, variant, clazz, baseCode, newCode, target);

					setChanges();
				} catch (FileOperationException | CoreException e1) {
					e1.printStackTrace();
				}
				cc.addSynchronizedChange(fe, timestamp, variant, targetProject);
			}
		}
	}

}
