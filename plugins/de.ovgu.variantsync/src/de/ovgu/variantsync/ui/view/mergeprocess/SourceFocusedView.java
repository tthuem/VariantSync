package de.ovgu.variantsync.ui.view.mergeprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.applicationlayer.merging.ResourceCompareInput;
import de.ovgu.variantsync.io.Persistable;
import de.ovgu.variantsync.ui.controller.ContextController;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.FeatureController;
import de.ovgu.variantsync.ui.controller.SynchronizationController;
import difflib.Delta;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.10.2015
 */
public class SourceFocusedView extends ViewPart {

	private ContextController cc = ControllerHandler.getInstance().getContextController();
	private SynchronizationController sc = ControllerHandler.getInstance().getSynchronizationController();
	private FeatureController fc = ControllerHandler.getInstance().getFeatureController();
	private ContextOperations contextOperations = ModuleFactory.getContextOperations();
	private Persistable persOp = ModuleFactory.getPersistanceOperations();

	private List projects;
	private List classes;
	private List changes;
	private List autoSyncTargets;
	private String selectedFeatureExpression;
	private String selectedProject;
	private String selectedClass;
	private int selectedChange;
	private Collection<CodeChange> collChanges;
	private String featureExpressions[];
	private Collection<String> syncCode;
	private String projectNameTarget;
	private String classNameTarget;
	private Button btnSynchronize;
	private Button btnManualSync;
	private String leftClass;
	private String rightClass;
	private SourceFocusedView reference;
	private Button btnRemoveChangeEntry;
	private java.util.List<String> codeWC;
	private CCombo combo;
	private Label lblMergeConflict;
	private List manualSyncTargets;
	private Label lblChangedCode;
	private Table newCode;
	private Collection<String> manualSyncTargetsAsList;
	private String autoSelection;
	private String manualSelection;
	private long timestamp;
	private List list_batchVariants;
	private GridData gd_list_2;
	private CodeChange currentChange;

	private Collection<String> base;
	private Collection<String> left;

	public static boolean isManSynced;
	private Delta rightDelta;
	private File fRightVersion;
	private IResource right;

	public SourceFocusedView() {
	}

	public void dispose() {
	}

	public void setFocus() {
		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions().toArray(new String[] {});
		combo.setItems(featureExpressions);
		if (selectedFeatureExpression != null) {
			contextOperations.activateContext(selectedFeatureExpression, true);
			cc.setFeatureView(true);
		}
	}

	@Override
	public void createPartControl(final Composite arg0) {
		isManSynced = false;
		reference = this;
		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions().toArray(new String[] {});
		arg0.setLayout(new GridLayout(5, false));

		Label lblSelectFeatureExpression = new Label(arg0, SWT.NONE);
		lblSelectFeatureExpression.setText("Select Feature Expression");
		combo = new CCombo(arg0, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 189;
		combo.setLayoutData(gd_combo);
		combo.setItems(featureExpressions);
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				featureExpressions = fc.getFeatureExpressions().getFeatureExpressions().toArray(new String[] {});
				combo.setItems(featureExpressions);
			}
		};
		combo.addListener(SWT.MouseDown, listener);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (autoSyncTargets != null)
					autoSyncTargets.setItems(new String[] {});
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (collChanges != null)
					collChanges.clear();
				if (newCode != null)
					newCode.removeAll();
				selectedFeatureExpression = combo.getText();
				projects.setItems(cc.getProjects(combo.getText()).toArray(new String[] {}));
				// list_batchVariants.setItems(cc.getProjects(combo.getText()).toArray(new String[] {}));
				contextOperations.activateContext(selectedFeatureExpression, true);
				cc.setFeatureView(true);
			}
		});
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		Label lblProjects = new Label(arg0, SWT.NONE);
		lblProjects.setText("Projects");

		Label lblClasses = new Label(arg0, SWT.NONE);
		lblClasses.setText("Classes");

		Label lblChanges = new Label(arg0, SWT.NONE);
		lblChanges.setText("Changes");

		lblChangedCode = new Label(arg0, SWT.NONE);
		lblChangedCode.setText("Changed Code");

		Label lblSyncTargets = new Label(arg0, SWT.NONE);
		lblSyncTargets.setText("automatic sync possible");

		projects = new List(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 5);
		gd_list.heightHint = 255;
		gd_list.widthHint = 119;
		projects.setLayoutData(gd_list);
		projects.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (autoSyncTargets != null)
					autoSyncTargets.setItems(new String[] {});
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (newCode != null)
					newCode.removeAll();
				selectedProject = projects.getSelection()[0];
				classes.setItems(cc.getClasses(selectedFeatureExpression, selectedProject).toArray(new String[] {}));
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		final Menu menu = new Menu(projects);
		projects.setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				int[] selection = projects.getSelectionIndices();
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}
				final java.util.List<String> variants = new ArrayList<String>();
				for (int selected : selection) {
					if (selected < 0 || selected >= projects.getItemCount())
						return;
					variants.add(projects.getItem(selected));
				}
				MenuItem newItem = new MenuItem(menu, SWT.NONE);
				newItem.setText("Start Batch Synchronization");
				newItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						startBatchSync(variants.toArray(new String[] {}));
					}
				});
			}
		});

		classes = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 5);
		gd_list_1.heightHint = 259;
		gd_list_1.widthHint = 83;
		classes.setLayoutData(gd_list_1);
		classes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (autoSyncTargets != null)
					autoSyncTargets.setItems(new String[] {});
				if (manualSyncTargets != null)
					manualSyncTargets.setItems(new String[] {});
				if (newCode != null)
					newCode.removeAll();
				selectedClass = classes.getSelection()[0];
				leftClass = selectedProject + " - " + selectedClass;
				setChanges();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		changes = new List(arg0, SWT.H_SCROLL | SWT.BORDER);
		GridData gd_changes = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 5);
		gd_changes.heightHint = 256;
		gd_changes.widthHint = 165;
		changes.setLayoutData(gd_changes);
		changes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (newCode != null)
					newCode.removeAll();
				selectedChange = changes.getSelectionIndex();
				btnRemoveChangeEntry.setEnabled(true);
				Iterator<CodeChange> it = collChanges.iterator();
				int i = 0;
				newCode.removeAll();
				CodeHighlighting ccolor = cc.getContextColor(selectedFeatureExpression);
				while (it.hasNext()) {
					CodeChange ch = it.next();
					if (i == selectedChange) {
						currentChange = ch;
						timestamp = ch.getTimestamp();
						newCode.removeAll();
						for (CodeLine cl : ch.getNewVersion()) {
							TableItem item = new TableItem(newCode, SWT.NONE);
							item.setText(cl.getLine() + ": " + cl.getCode());
							Color color = new Color(getSite().getShell().getDisplay(), ccolor.getRGB());
							item.setBackground(color);
						}

						left = cc.getNewCode(currentChange);
						base = cc.getBaseCode(currentChange);

						refreshSyncTargets();
						break;
					}
					i++;
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		newCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 5);
		gd_text_1.widthHint = 157;
		newCode.setLayoutData(gd_text_1);

		autoSyncTargets = new List(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_syncTargets = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_syncTargets.heightHint = 89;
		gd_syncTargets.widthHint = 143;
		autoSyncTargets.setLayoutData(gd_syncTargets);
		autoSyncTargets.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				autoSelection = autoSyncTargets.getSelection()[0];
				String[] tmp = autoSelection.split(":");
				projectNameTarget = tmp[0].trim();
				classNameTarget = tmp[1].trim();
				rightClass = projectNameTarget + " - " + classNameTarget;

				syncCode = sc.doAutoSync(left, base,
						cc.getTargetFile(selectedFeatureExpression, projectNameTarget, classNameTarget));
				if (syncCode != null && !syncCode.isEmpty()) {
					btnSynchronize.setEnabled(true);
				} else {
					btnManualSync.setEnabled(true);
				}

			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		btnSynchronize = new Button(arg0, SWT.NONE);
		btnSynchronize.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSynchronize.setText("Auto Sync");
		btnSynchronize.setEnabled(false);
		btnSynchronize.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					ModuleFactory.getContextOperations().activateContext(selectedFeatureExpression);
					contextOperations.activateContext(selectedFeatureExpression, true);
					solveChange(syncCode, selectedFeatureExpression, projectNameTarget, classNameTarget, true);
					contextOperations.stopRecording();
					btnSynchronize.setEnabled(false);

					cc.addSynchronizedChange(selectedFeatureExpression, timestamp, selectedProject, autoSelection);
					setChanges();

					if (newCode != null)
						newCode.removeAll();
					if (autoSyncTargets != null)
						autoSyncTargets.removeAll();
					if (manualSyncTargets != null)
						manualSyncTargets.removeAll();

					break;
				}
				}
			}
		});

		lblMergeConflict = new Label(arg0, SWT.NONE);
		lblMergeConflict.setText("conflict - manual sync necessary");

		manualSyncTargets = new List(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gd_list_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_list_2.heightHint = 89;
		gd_list_2.widthHint = 191;
		manualSyncTargets.setLayoutData(gd_list_2);
		manualSyncTargets.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				int selectedTargetIndex = manualSyncTargets.getSelectionIndex();
				int i = 0;
				for (String target : manualSyncTargetsAsList) {
					if (i == selectedTargetIndex) {
						btnManualSync.setEnabled(true);
						String[] targetInfo = target.split(":");
						projectNameTarget = targetInfo[0].trim();
						classNameTarget = targetInfo[1].trim();
						manualSelection = target;

						break;
					}
					i++;
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		btnManualSync = new Button(arg0, SWT.NONE);
		btnManualSync.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnManualSync.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnManualSync.setText("Manual Sync");
		btnManualSync.setEnabled(false);
		btnManualSync.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					btnManualSync.setEnabled(false);

					try {
						syncWithEclipse(base, left, selectedProject, selectedClass, projectNameTarget, classNameTarget);
					} catch (FileOperationException | CoreException ex) {
						ex.printStackTrace();
					}
					if (isManSynced) {

						contextOperations.activateContext(selectedFeatureExpression, true);
						contextOperations.setManualMergeResult(rightDelta, fRightVersion,
								new File(right.getLocationURI().getPath()));
						try {
							right.refreshLocal(IResource.DEPTH_INFINITE, null);
						} catch (CoreException e1) {
							e1.printStackTrace();
						}
						contextOperations.stopRecording();

						cc.addSynchronizedChange(selectedFeatureExpression, timestamp, selectedProject,
								manualSelection);
						refreshSyncTargets();
						setChanges();

						if (newCode != null)
							newCode.removeAll();
						if (autoSyncTargets != null)
							autoSyncTargets.removeAll();
						if (manualSyncTargets != null)
							manualSyncTargets.removeAll();

						isManSynced = false;
					}
					break;
				}
			}
		});

		btnRemoveChangeEntry = new Button(arg0, SWT.NONE);
		btnRemoveChangeEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemoveChangeEntry.setText("Remove Change Entry");
		btnRemoveChangeEntry.setVisible(false);
		btnRemoveChangeEntry.setEnabled(false);
		btnRemoveChangeEntry.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					contextOperations.removeChange(selectedFeatureExpression, selectedProject, selectedClass,
							selectedChange, timestamp);
					btnRemoveChangeEntry.setEnabled(false);
					setChanges();
					if (newCode != null)
						newCode.removeAll();
				}
				}
			}
		});
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		Button btnSyncPreview = new Button(arg0, SWT.CHECK);
		btnSyncPreview.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSyncPreview.setText("sync preview");
		btnSyncPreview.setVisible(false);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
	}

	// TODO
	private void startBatchSync(String[] variantBatchSelection) {
		for (String s : variantBatchSelection) {
			String[] classes = cc.getClasses(selectedFeatureExpression, s).toArray(new String[] {});
			for (String c : classes) {
				Collection<CodeChange> collChanges = cc.getChanges(selectedFeatureExpression, s, c);
				Iterator<CodeChange> it = collChanges.iterator();
				while (it.hasNext()) {
					CodeChange ch = it.next();
					long timestamp = ch.getTimestamp();
					String[] autoItems = cc.getAutoSyncTargets(selectedFeatureExpression, s, c,
							cc.getBaseCode(currentChange), cc.getNewCode(currentChange)).toArray(new String[] {});
					java.util.List<String> checkedItems = new ArrayList<String>();
					for (String target : autoItems) {
						if (!contextOperations.isAlreadySynchronized(selectedFeatureExpression, timestamp,
								selectedProject, target)) {
							checkedItems.add(target);
						}
					}
					for (String target : checkedItems) {
						String t[] = target.split(":");
						String targetProject = t[0].trim();
						String targetClass = t[1].trim();
						java.util.List<String> codeWC = cc.getTargetFile(selectedFeatureExpression, targetProject,
								targetClass);

						ModuleFactory.getContextOperations().activateContext(selectedFeatureExpression);
						Collection<String> syncCode = sc.doAutoSync(cc.getNewCode(ch), cc.getBaseCode(ch), codeWC);
						solveChange(syncCode, selectedFeatureExpression, targetProject, targetClass, false);
						cc.addSynchronizedChange(selectedFeatureExpression, timestamp, selectedProject, target);
					}

					// manueller Anteil
					java.util.Collection<String> manualSyncTargetsAsList = cc.getConflictedSyncTargets(
							selectedFeatureExpression, s, c, cc.getBaseCode(currentChange),
							cc.getNewCode(currentChange));
					String[] manualItems = manualSyncTargetsAsList.toArray(new String[] {});
					checkedItems = new ArrayList<String>();
					for (String target : manualItems) {
						if (!contextOperations.isAlreadySynchronized(selectedFeatureExpression, timestamp,
								selectedProject, target)) {
							checkedItems.add(target);
						}
					}
					for (String target : checkedItems) {
						String t[] = target.split(":");
						String targetProject = t[0].trim();
						String targetClass = t[1].trim();
						try {
							syncWithEclipse(cc.getBaseCode(currentChange), cc.getNewCode(currentChange), s, c,
									targetProject, targetClass);
						} catch (FileOperationException | CoreException e1) {
							e1.printStackTrace();
						}
						cc.addSynchronizedChange(selectedFeatureExpression, timestamp, selectedProject, targetProject);
					}
				}
			}
		}
	}

	private void syncWithEclipse(Collection<String> base2, Collection<String> left2, String projectName,
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
		Delta d1 = null;
		rightDelta = null;
		int i = 0;
		for (Delta d : deltas) {
			if (i == 0) {
				d1 = d;
			} else if (i == 1) {
				rightDelta = d;
			}
			i++;
		}

		System.out.println(d1.getOriginal().getPosition() + ": " + d1.getOriginal().getLines());
		System.out.println(d1.getRevised().getPosition() + ": " + d1.getRevised().getLines());
		System.out.println(rightDelta.getOriginal().getPosition() + ": " + rightDelta.getOriginal().getLines());
		System.out.println(rightDelta.getRevised().getPosition() + ": " + rightDelta.getRevised().getLines());

		// Base Version
		File f = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()
				+ VariantSyncConstants.MERGE_PATH + "/BaseVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile((Collection<String>) d1.getOriginal().getLines(), f);

		f = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + VariantSyncConstants.MERGE_PATH
				+ "/LeftVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile((Collection<String>) d1.getRevised().getLines(), f);

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
		compconf.setLeftLabel(projectName + ": " + className + " - changed version");
		compconf.setRightLabel(projectNameRight + ": " + classNameRight);
		compconf.setAncestorLabel(projectName + ": " + className + " - version without change");
		compconf.setLeftEditable(false);
		compconf.setRightEditable(true);

		CompareEditorInput rci = new ResourceCompareInput(compconf, base, left, rightMerge);
		try {
			System.out.println(persOp.readFile(new FileInputStream(base.getLocationURI().getPath())));
			System.out.println(persOp.readFile(new FileInputStream(left.getLocationURI().getPath())));

			// TODO: test if order is correct to solve a conflict
			System.out.println(persOp.readFile(new FileInputStream(rightMerge.getLocationURI().getPath())).toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		CompareUI.openCompareDialog(rci);
	}

	public void solveChange(Collection<String> syncCode2, String selectedFeatureExpression, String projectName,
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
		contextOperations.removeChange(selectedFeatureExpression, selectedProject, selectedClass, selectedChange,
				timestamp);

		if (refreshGUI)
			refreshSyncTargets();
	}

	private void refreshSyncTargets() {

		boolean isAutoSyncPossible = false;
		String[] autoItems = cc
				.getAutoSyncTargets(selectedFeatureExpression, selectedProject, selectedClass, base, left)
				.toArray(new String[] {});
		java.util.List<String> checkedItems = new ArrayList<String>();
		for (String target : autoItems) {
			if (!contextOperations.isAlreadySynchronized(selectedFeatureExpression, timestamp, selectedProject,
					target)) {
				checkedItems.add(target);
			}
		}
		autoSyncTargets.setItems(checkedItems.toArray(new String[] {}));
		if (!checkedItems.isEmpty())
			isAutoSyncPossible = true;

		boolean isManualSyncPossible = false;
		manualSyncTargetsAsList = cc.getConflictedSyncTargets(selectedFeatureExpression, selectedProject, selectedClass,
				base, left);
		String[] manualItems = manualSyncTargetsAsList.toArray(new String[] {});
		checkedItems = new ArrayList<String>();
		for (String target : manualItems) {
			if (!contextOperations.isAlreadySynchronized(selectedFeatureExpression, timestamp, selectedProject,
					target)) {
				checkedItems.add(target);
			}
		}
		manualSyncTargets.setItems(checkedItems.toArray(new String[] {}));
		manualSyncTargetsAsList = checkedItems;
		if (!checkedItems.isEmpty())
			isManualSyncPossible = true;

		if (!isAutoSyncPossible && !isManualSyncPossible) {
			contextOperations.removeChange(selectedFeatureExpression, selectedProject, selectedClass, selectedChange,
					timestamp);
		}
	}

	public void setChanges() {
		collChanges = cc.getChanges(selectedFeatureExpression, selectedProject, selectedClass);
		java.util.List<String> timestamps = new ArrayList<String>();
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss 'at' dd.MM.yyyy");
		for (CodeChange ch : collChanges) {
			timestamps.add(formatter.format(new Date(ch.getTimestamp())));
		}
		changes.setItems(timestamps.toArray(new String[] {}));
	}

	public void checkManualMerge(java.util.List<String> mergeResult) {
		boolean manualMergeIsNeeded = false;
		for (String cl : mergeResult) {
			if (cl.contains("<<<<<<<")) {
				manualMergeIsNeeded = true;
				break;
			}
		}
		if (manualMergeIsNeeded) {
			UtilOperations.getInstance().printLines(mergeResult);
			ManualMerge m = new ManualMerge(reference, mergeResult, leftClass, mergeResult, rightClass, syncCode);
			m.open();
		} else {
			btnManualSync.setEnabled(false);
			if (newCode != null)
				newCode.removeAll();
			cc.refreshContext(false, selectedFeatureExpression, projectNameTarget, classNameTarget, codeWC, syncCode);
			solveChange(mergeResult, selectedFeatureExpression, projectNameTarget, classNameTarget, true);
			setChanges();
		}
	}
}
