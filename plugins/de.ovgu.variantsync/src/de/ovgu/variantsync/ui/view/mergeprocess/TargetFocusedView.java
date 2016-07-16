package de.ovgu.variantsync.ui.view.mergeprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IContainer;
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
import de.ovgu.variantsync.applicationlayer.merging.ResourceCompareInput;
import de.ovgu.variantsync.ui.controller.ContextController;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.FeatureController;
import de.ovgu.variantsync.ui.controller.SynchronizationController;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.10.2015
 */
public class TargetFocusedView extends ViewPart {

	private ContextController cc = ControllerHandler.getInstance()
			.getContextController();
	private SynchronizationController sc = ControllerHandler.getInstance()
			.getSynchronizationController();
	private FeatureController fc = ControllerHandler.getInstance()
			.getFeatureController();
	private ContextOperations contextOperations = ModuleFactory
			.getContextOperations();

	private List features;
	private List classes;
	private List changes;
	private String selectedVariant;
	private String selectedFeature;
	private String selectedClass;
	private String selectedChange;
	private Collection<CodeChange> collChanges;
	private String variants[];
	private java.util.List<CodeLine> baseCode;
	private java.util.List<CodeLine> syncCode;
	private Button btnSynchronize;
	private Button btnManualSync;
	private CCombo combo;
	private Label lblMergeConflict;
	private Label lblChangedCode;
	private Table newCode;
	private java.util.List<String> manualSyncTargetsAsList;
	private long timestamp;
	private java.util.List<CodeLine> newVersionWholeClass;
	private Map<String, Collection<CodeChange>> changeMap;
	private String selClass;
	private int selIn;

	public TargetFocusedView() {
	}

	public void dispose() {
	}

	public void setFocus() {
		variants = fc.getVariants().toArray(new String[] {});
		combo.setItems(variants);
	}

	@Override
	public void createPartControl(final Composite arg0) {
		variants = fc.getFeatureExpressions().getFeatureExpressions()
				.toArray(new String[] {});
		arg0.setLayout(new GridLayout(5, false));

		Label lblSelectFeatureExpression = new Label(arg0, SWT.NONE);
		lblSelectFeatureExpression.setText("Select Variant");
		combo = new CCombo(arg0, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_combo.widthHint = 189;
		combo.setLayoutData(gd_combo);
		combo.setItems(variants);
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				variants = fc.getVariants().toArray(new String[] {});
				combo.setItems(variants);
			}
		};
		combo.addListener(SWT.MouseDown, listener);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				btnSynchronize.setEnabled(false);
				btnManualSync.setEnabled(false);
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (collChanges != null)
					collChanges.clear();
				if (newCode != null)
					newCode.removeAll();
				selectedVariant = combo.getText();

				// TODO insert features that have changes!
				features.setItems(cc.getFeatures(combo.getText()).toArray(
						new String[] {}));
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
		lblProjects.setText("Features");

		Label lblClasses = new Label(arg0, SWT.NONE);
		lblClasses.setText("Classes");

		Label lblChanges = new Label(arg0, SWT.NONE);
		lblChanges.setText("Changes");

		lblChangedCode = new Label(arg0, SWT.NONE);
		lblChangedCode.setText("Changed Code");
		new Label(arg0, SWT.NONE);

		features = new List(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 11);
		gd_list.heightHint = 255;
		gd_list.widthHint = 119;
		features.setLayoutData(gd_list);
		features.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				btnSynchronize.setEnabled(false);
				btnManualSync.setEnabled(false);
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (newCode != null)
					newCode.removeAll();
				selectedFeature = features.getSelection()[0];
				classes.setItems(cc.getClassesForVariant(selectedFeature,
						selectedVariant).toArray(new String[] {}));
				contextOperations.activateContext(selectedFeature, true);
				cc.setProductView(true);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		final Menu menu = new Menu(features);
		features.setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				int[] selection = features.getSelectionIndices();
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}
				final java.util.List<String> variants = new ArrayList<String>();
				for (int selected : selection) {
					if (selected < 0 || selected >= features.getItemCount())
						return;
					variants.add(features.getItem(selected));
				}
				MenuItem newItem = new MenuItem(menu, SWT.NONE);
				newItem.setText("Start Batch Synchronization");
				newItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						// TODO
						startBatchSync(variants.toArray(new String[] {}));
					}
				});
			}
		});

		classes = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				11);
		gd_list_1.heightHint = 259;
		gd_list_1.widthHint = 83;
		classes.setLayoutData(gd_list_1);
		classes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				btnSynchronize.setEnabled(false);
				btnManualSync.setEnabled(false);
				if (newCode != null)
					newCode.removeAll();
				selectedClass = classes.getSelection()[0];
				setChanges();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		changes = new List(arg0, SWT.H_SCROLL | SWT.BORDER);
		GridData gd_changes = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				11);
		gd_changes.heightHint = 256;
		gd_changes.widthHint = 165;
		changes.setLayoutData(gd_changes);
		changes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (newCode != null)
					newCode.removeAll();
				selectedChange = changes.getSelection()[0];
				selIn = changes.getSelectionIndex();
				// btnRemoveChangeEntry.setEnabled(true);
				String time = changes.getSelection()[0].trim();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"HH:mm:ss 'at' dd.MM.yyyy");
				String dateInString = time;
				Date date = null;
				try {
					date = formatter.parse(dateInString);
					System.out.println(date);
					System.out.println(formatter.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Collection<CodeChange> collChanges = changeMap.get(selClass);
				Iterator<CodeChange> it = collChanges.iterator();
				newCode.removeAll();
				CodeHighlighting ccolor = cc.getContextColor(selectedFeature);
				while (it.hasNext()) {
					CodeChange ch = it.next();
					formatter = new SimpleDateFormat("HH:mm:ss 'at' dd.MM.yyyy");
					String s = formatter.format(new Date(ch.getTimestamp()));
					if (time.equals(s)) {
						timestamp = ch.getTimestamp();
						baseCode = ch.getBaseVersionWholeClass();
						syncCode = ch.getNewVersionWholeClass();
						java.util.List<CodeLine> mappedCode = ch
								.getBaseVersion();
						for (CodeLine clWC : baseCode) {
							for (CodeLine cl : mappedCode) {
								if (cl.getLine() == clWC.getLine()) {
									clWC.setMapped(true);
								}
							}
						}
						newVersionWholeClass = ch.getNewVersionWholeClass();
						mappedCode = ch.getNewVersion();
						for (CodeLine clWC : newVersionWholeClass) {
							for (CodeLine cl : mappedCode) {
								if (cl.getLine() == clWC.getLine()) {
									clWC.setMapped(true);
								}
							}
						}
						newCode.removeAll();
						java.util.List<CodeLine> changedCode = getDifference(
								baseCode, newVersionWholeClass);
						for (CodeLine cl : changedCode) {
							TableItem item = new TableItem(newCode, SWT.NONE);
							item.setText(cl.getLine() + ": " + cl.getCode());
							// if (cl.isMapped()) {
							Color color = new Color(getSite().getShell()
									.getDisplay(), ccolor.getRGB());
							item.setBackground(color);
							// }
							if (cl.isNew()) {
								item.setForeground(getSite().getShell()
										.getDisplay()
										.getSystemColor(SWT.COLOR_DARK_RED));
							}
						}
						refreshSyncTargets(selectedClass);
						break;
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		newCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1,
				11);
		gd_text_1.heightHint = 227;
		gd_text_1.widthHint = 149;
		newCode.setLayoutData(gd_text_1);
						new Label(arg0, SWT.NONE);
								new Label(arg0, SWT.NONE);
								
										Label lblSyncTargets = new Label(arg0, SWT.NONE);
										GridData gd_lblSyncTargets = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
										gd_lblSyncTargets.heightHint = 26;
										lblSyncTargets.setLayoutData(gd_lblSyncTargets);
										lblSyncTargets.setText("automatic sync possible");
								
										btnSynchronize = new Button(arg0, SWT.NONE);
										btnSynchronize.addSelectionListener(new SelectionAdapter() {
											@Override
											public void widgetSelected(SelectionEvent e) {
											}
										});
										btnSynchronize.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
												false, false, 1, 1));
										btnSynchronize.setText("Auto Sync");
										btnSynchronize.setEnabled(false);
										btnSynchronize.addListener(SWT.Selection, new Listener() {
											public void handleEvent(Event e) {
												switch (e.type) {
												case SWT.Selection: {
													ModuleFactory.getContextOperations().activateContext(selectedFeature);
													solveChange(syncCode, selectedFeature, selectedVariant,
															selectedClass.split(":")[1].trim(), true);
													// setChanges();
													btnSynchronize.setEnabled(false);
													if (newCode != null)
														newCode.removeAll();

													// Problem: only changes of merged feature
													// expression is mapped, changes of other feature
													// expressions are lost
													// cc.refreshContext(true, selectedFeatureExpression,
													// projectNameTarget, classNameTarget, codeWC,
													// syncCode);

													cc.addSynchronizedChange(selectedFeature, timestamp,
															selectedClass.split(":")[0].trim(), selectedVariant);
													setChanges();
													btnSynchronize.setEnabled(false);
													break;
												}
												}
											}
										});
						new Label(arg0, SWT.NONE);
						new Label(arg0, SWT.NONE);
						new Label(arg0, SWT.NONE);
										new Label(arg0, SWT.NONE);
								
										lblMergeConflict = new Label(arg0, SWT.NONE);
										GridData gd_lblMergeConflict = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
										gd_lblMergeConflict.heightHint = 24;
										lblMergeConflict.setLayoutData(gd_lblMergeConflict);
										lblMergeConflict.setText("conflict - manual sync");
						
								btnManualSync = new Button(arg0, SWT.NONE);
								btnManualSync.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
										false, 1, 1));
								btnManualSync.setText("Manual Sync");
								btnManualSync.setEnabled(false);
								btnManualSync.addListener(SWT.Selection, new Listener() {
									public void handleEvent(Event e) {
										switch (e.type) {
										case SWT.Selection:
											btnManualSync.setEnabled(false);
											try {
												syncWithEclipse(baseCode, getNewCode(),
														selectedClass.split(":")[0].trim(),
														selectedClass.split(":")[1].trim(),
														selectedVariant,
														selectedClass.split(":")[1].trim());
											} catch (FileOperationException | CoreException ex) {
												ex.printStackTrace();
											}
											cc.addSynchronizedChange(selectedFeature, timestamp,
													selectedClass.split(":")[0].trim(), selectedVariant);
											refreshSyncTargets(selectedClass);
											setChanges();
											btnManualSync.setEnabled(false);
											break;
										}
									}
								});
						new Label(arg0, SWT.NONE);
	}

	private java.util.List<CodeLine> getDifference(
			java.util.List<CodeLine> base, java.util.List<CodeLine> changed) {
		java.util.List<CodeLine> diff = new ArrayList<CodeLine>();
		int i = 0;
		for (CodeLine cl : changed) {
			if (i > base.size() - 1
					|| !base.get(i).getCode().trim()
							.equals(cl.getCode().trim())) {
				diff.add(cl.clone());
			}
			i++;
		}
		if (diff.size() > 1) {
			diff.remove(diff.size() - 1);
		}
		return diff;
	}

	private void startBatchSync(String[] featureBatchSelection) {
		for (String s : featureBatchSelection) {
			String[] classes = cc.getClassesForVariant(s, selectedVariant)
					.toArray(new String[] {});
			for (String c : classes) {
				Map<String, Collection<CodeChange>> collChanges = cc
						.getChangesForVariant(selectedFeature, s, c);
				Set<Entry<String, Collection<CodeChange>>> entry = collChanges
						.entrySet();
				Iterator<Entry<String, Collection<CodeChange>>> it = entry
						.iterator();
				// Iterator<CodeChange> it = collChanges.iterator();
				while (it.hasNext()) {
					Entry<String, Collection<CodeChange>> en = it.next();
					String project = en.getKey();
					Iterator<CodeChange> itC = en.getValue().iterator();
					while (itC.hasNext()) {
						CodeChange ch = itC.next();
						long timestamp = ch.getTimestamp();
						baseCode = ch.getBaseVersionWholeClass();
						java.util.List<CodeLine> mappedCode = ch
								.getBaseVersion();
						for (CodeLine clWC : baseCode) {
							for (CodeLine cl : mappedCode) {
								if (cl.getLine() == clWC.getLine()) {
									clWC.setMapped(true);
								}
							}
						}
						newVersionWholeClass = ch.getNewVersionWholeClass();
						mappedCode = ch.getNewVersion();
						for (CodeLine clWC : newVersionWholeClass) {
							for (CodeLine cl : mappedCode) {
								if (cl.getLine() == clWC.getLine()) {
									clWC.setMapped(true);
								}
							}
						}
						String[] autoItems = cc.getAutoSyncTargetsForVariant(s,
								selectedVariant, c, baseCode,
								newVersionWholeClass).toArray(new String[] {});
						java.util.List<String> checkedItems = new ArrayList<String>();
						for (String target : autoItems) {
							if (!contextOperations.isAlreadySynchronized(
									selectedFeature, timestamp,
									c.split(":")[0].trim(), target)) {
								checkedItems.add(target);
							}
						}
						for (String target : checkedItems) {
							String t[] = target.split(":");
							String targetProject = t[0].trim();
							String targetClass = t[1].trim();
							java.util.List<CodeLine> code = cc.getTargetCode(s,
									targetProject, targetClass);
							java.util.List<CodeLine> codeWC = cc
									.getTargetCodeWholeClass(s, targetProject,
											targetClass);
							for (CodeLine clWC : codeWC) {
								for (CodeLine cl : code) {
									if (cl.getLine() == clWC.getLine()) {
										clWC.setMapped(true);
									}
								}
							}
							ModuleFactory.getContextOperations().activateContext(selectedFeature);
							java.util.List<CodeLine> syncCode = sc.doAutoSync(
									ch.getNewVersionWholeClass(), baseCode,
									codeWC);
							solveChange(syncCode, s, targetProject,
									targetClass, false);
							cc.addSynchronizedChange(s, timestamp,
									c.split(":")[0].trim(), targetProject);
						}

						// manueller Anteil
						java.util.List<String> manualSyncTargetsAsList = cc
								.getConflictedSyncForVariant(s,
										selectedVariant, c, baseCode,
										newVersionWholeClass);
						String[] manualItems = manualSyncTargetsAsList
								.toArray(new String[] {});
						checkedItems = new ArrayList<String>();
						for (String target : manualItems) {
							if (!contextOperations.isAlreadySynchronized(s,
									timestamp, c.split(":")[0].trim(), target)) {
								checkedItems.add(target);
							}
						}
						for (String target : checkedItems) {
							target = c.split(":")[0].trim();
							String className = c.split(":")[1].trim();
							try {
								syncWithEclipse(baseCode, newVersionWholeClass,
										target, className, selectedVariant,
										className);
							} catch (FileOperationException | CoreException e1) {
								e1.printStackTrace();
							}
							cc.addSynchronizedChange(s, timestamp, target,
									selectedVariant);
							refreshSyncTargets(selectedVariant + ": "
									+ className);
							setChanges();
						}
					}
				}
			}
		}
	}

	private java.util.List<CodeLine> getNewCode() {
		Iterator<CodeChange> it = collChanges.iterator();
		java.util.List<CodeLine> newCode = null;
		while (it.hasNext()) {
			CodeChange cc = it.next();
			String time = selectedChange.trim();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"HH:mm:ss 'at' dd.MM.yyyy");
			String s = formatter.format(new Date(cc.getTimestamp()));
			if (time.equals(s)) {
				newCode = cc.getNewVersionWholeClass();
				break;
			}
		}
		return newCode;
	}

	private void syncWithEclipse(java.util.List<CodeLine> baseCode,
			java.util.List<CodeLine> leftCode, String projectName,
			String className, String projectNameRight, String classNameRight)
			throws FileOperationException, CoreException {

		// Base Version
		java.util.List<String> baseLines = new ArrayList<String>();
		for (CodeLine cl : baseCode) {
			baseLines.add(cl.getCode());
		}
		File f = new File(ResourcesPlugin.getWorkspace().getRoot()
				.getLocation().toString()
				+ VariantSyncConstants.MERGE_PATH + "/BaseVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile(baseLines, f);

		java.util.List<String> leftLines = new ArrayList<String>();
		for (CodeLine cl : leftCode) {
			leftLines.add(cl.getCode());
		}
		f = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ VariantSyncConstants.MERGE_PATH + "/LeftVersion.java");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		ModuleFactory.getPersistanceOperations().addLinesToFile(leftLines, f);

		// Right Version
		java.util.List<IProject> supportedProjects = VariantSyncPlugin
				.getDefault().getSupportProjectList();
		IResource right = null;
		for (IProject p : supportedProjects) {
			String name = p.getName();
			if (name.equals(projectNameRight)) {
				try {
					right = findFileRecursively(p, classNameRight);
					break;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		IResource base = null;
		IProject p = null;
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
				.getProjects()) {
			if (project.getName().equals("variantsyncFeatureInfo")) {
				p = project;
				base = findFileRecursively(project, "BaseVersion");
			}
		}
		base = p.getFolder("merge").getFile("BaseVersion.java");

		final IResource left = p.getFolder("merge").getFile("LeftVersion.java");

		// Editor
		org.eclipse.compare.CompareConfiguration compconf = new org.eclipse.compare.CompareConfiguration();
		compconf.setLeftLabel(projectName + ": " + className
				+ " - changed version");
		compconf.setRightLabel(projectNameRight + ": " + classNameRight);
		compconf.setAncestorLabel(projectName + ": " + className
				+ " - version without change");
		compconf.setLeftEditable(false);
		compconf.setRightEditable(true);

		CompareEditorInput rci = new ResourceCompareInput(compconf, base, left,
				right);

		CompareUI.openCompareDialog(rci);
	}

	private IFile findFileRecursively(IContainer container, String name)
			throws CoreException {
		for (IResource r : container.members()) {
			if (r instanceof IContainer) {
				IFile file = findFileRecursively((IContainer) r, name);
				if (file != null) {
					return file;
				}
			} else if (r instanceof IFile && r.getName().equals(name)) {
				return (IFile) r;
			}
		}

		return null;
	}

	public void solveChange(java.util.List<CodeLine> code,
			String selectedFeatureExpression, String projectName,
			String className, boolean refreshGUI) {
		IResource res = contextOperations.getResource(
				selectedFeatureExpression, projectName, className);
		// try {
		// res.refreshLocal(IResource.DEPTH_INFINITE, null);
		// } catch (CoreException e1) {
		// e1.printStackTrace();
		// }
		// File file = contextOperations.getFile(selectedFeatureExpression,
		// projectNameTarget, classNameTarget);
		// persistanceOperations.writeFile(code, file);
		IFile f = (IFile) res;
		try {
			f.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		java.util.List<String> source = new ArrayList<String>();
		if (code.isEmpty())
			code = baseCode;
		for (CodeLine line : code) {
			source.add(line.getCode() + "\n");
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
		// ModuleFactory.getDeltaOperations().createPatch(f);
		if (refreshGUI)
			refreshSyncTargets(selectedClass);
		// contextOperations.removeChange(selectedFeatureExpression,
		// selectedProject, selectedClass, selectedChange);
		// btnRemoveChangeEntry.setEnabled(false);
	}

	private void refreshSyncTargets(String selectedClass) {
		boolean isAutoSyncPossible = false;
		String[] autoItems = cc.getAutoSyncTargetsForVariant(selectedFeature,
				selectedVariant, selectedClass, baseCode, newVersionWholeClass)
				.toArray(new String[] {});
		java.util.List<String> checkedItems = new ArrayList<String>();
		for (String target : autoItems) {
			if (!contextOperations.isAlreadySynchronized(selectedFeature,
					timestamp, selectedClass.split(":")[0].trim(), target)) {
				checkedItems.add(target);
			}
		}
		if (!checkedItems.isEmpty()) {
			isAutoSyncPossible = true;
			btnSynchronize.setEnabled(true);
		}

		boolean isManualSyncPossible = false;
		manualSyncTargetsAsList = cc.getConflictedSyncForVariant(
				selectedFeature, selectedVariant, selectedClass, baseCode,
				newVersionWholeClass);
		String[] manualItems = manualSyncTargetsAsList.toArray(new String[] {});
		checkedItems = new ArrayList<String>();
		for (String target : manualItems) {
			if (!contextOperations.isAlreadySynchronized(selectedFeature,
					timestamp, selectedClass.split(":")[0].trim(), target)) {
				checkedItems.add(target);
			}
		}
		manualSyncTargetsAsList = checkedItems;
		if (!checkedItems.isEmpty()) {
			isManualSyncPossible = true;
			btnManualSync.setEnabled(true);
		}

		if (!isAutoSyncPossible && !isManualSyncPossible) {
			// TODO
			// contextOperations.removeChange(selectedFeature, selectedVariant,
			// selectedClass, selectedChange, timestamp);
		}
	}

	public void setChanges() {
		collChanges = new ArrayList<CodeChange>();
		java.util.List<String> timestamps = new ArrayList<String>();
		changeMap = cc.getChangesForVariant(selectedFeature, selectedVariant,
				selectedClass);
		Set<Entry<String, Collection<CodeChange>>> entries = changeMap
				.entrySet();
		Iterator<Entry<String, Collection<CodeChange>>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, Collection<CodeChange>> entry = it.next();
			Collection<CodeChange> c = entry.getValue();
			selClass = selectedClass.split(":")[0].trim();
			for (CodeChange ch : c) {
				if (entry.getKey().equals(selClass)) {
					collChanges.add(ch);
					SimpleDateFormat formatter = new SimpleDateFormat(
							"HH:mm:ss 'at' dd.MM.yyyy");
					timestamps
							.add(formatter.format(new Date(ch.getTimestamp())));
				}
			}
		}
		changes.setItems(timestamps.toArray(new String[] {}));
	}
}
