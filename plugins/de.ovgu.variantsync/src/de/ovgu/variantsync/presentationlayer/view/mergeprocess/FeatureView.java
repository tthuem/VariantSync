package de.ovgu.variantsync.presentationlayer.view.mergeprocess;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;
import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.persistencelayer.IPersistanceOperations;
import de.ovgu.variantsync.presentationlayer.controller.ContextController;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;
import de.ovgu.variantsync.presentationlayer.controller.FeatureController;
import de.ovgu.variantsync.presentationlayer.controller.SynchronizationController;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.10.2015
 */
public class FeatureView extends ViewPart {

	private ContextController cc = ControllerHandler.getInstance()
			.getContextController();
	private SynchronizationController sc = ControllerHandler.getInstance()
			.getSynchronizationController();
	private FeatureController fc = ControllerHandler.getInstance()
			.getFeatureController();
	private IPersistanceOperations persistanceOperations = ModuleFactory
			.getPersistanceOperations();
	private IContextOperations contextOperations = ModuleFactory
			.getContextOperations();

	private List projects;
	private List classes;
	private List changes;
	private List syncTargets;
	private String selectedFeatureExpression;
	private String selectedProject;
	private String selectedClass;
	private int selectedChange;
	private Collection<CodeChange> collChanges;
	private String featureExpressions[];
	private Table oldCode;
	private Table newCode;
	private Table codeOfTarget;
	private Table syncPreview;
	private java.util.List<CodeLine> baseCode;
	private java.util.List<CodeLine> syncCode;
	private String projectNameTarget;
	private String classNameTarget;
	private Button btnSynchronize;
	private Button btnManualSync;
	private java.util.List<CodeLine> left;
	private String leftClass;
	private java.util.List<CodeLine> right;
	private String rightClass;
	private FeatureView reference;
	private Button btnRemoveChangeEntry;
	private java.util.List<CodeLine> codeWC;
	private CCombo combo;

	public FeatureView() {
	}

	public void dispose() {
	}

	public void setFocus() {
		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions()
				.toArray(new String[] {});
		combo.setItems(featureExpressions);
	}

	@Override
	public void createPartControl(final Composite arg0) {
		arg0.setLayout(new GridLayout(9, false));

		Label lblSelectFeatureExpression = new Label(arg0, SWT.NONE);
		lblSelectFeatureExpression.setText("Select Feature Expression");
		new Label(arg0, SWT.NONE);

		reference = this;

		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions()
				.toArray(new String[] {});
		combo = new CCombo(arg0, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3,
				1);
		gd_combo.widthHint = 189;
		combo.setLayoutData(gd_combo);
		combo.setItems(featureExpressions);
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				featureExpressions = fc.getFeatureExpressions()
						.getFeatureExpressions().toArray(new String[] {});
				combo.setItems(featureExpressions);
			}
		};
		combo.addListener(SWT.MouseDown, listener);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (syncTargets != null)
					syncTargets.setItems(new String[] {});
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (codeOfTarget != null)
					codeOfTarget.removeAll();
				if (collChanges != null)
					collChanges.clear();
				if (oldCode != null)
					oldCode.removeAll();
				if (newCode != null)
					newCode.removeAll();
				selectedFeatureExpression = combo.getText();
				projects.setItems(cc.getProjects(combo.getText()).toArray(
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
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		Label lblProjects = new Label(arg0, SWT.NONE);
		lblProjects.setText("Projects");
		new Label(arg0, SWT.NONE);

		Label lblClasses = new Label(arg0, SWT.NONE);
		lblClasses.setText("Classes");
		new Label(arg0, SWT.NONE);

		Label lblChanges = new Label(arg0, SWT.NONE);
		lblChanges.setText("Changes");
		new Label(arg0, SWT.NONE);

		Label lblOldCode = new Label(arg0, SWT.NONE);
		lblOldCode.setText("Old Code");
		new Label(arg0, SWT.NONE);

		Label lblNewCode = new Label(arg0, SWT.NONE);
		lblNewCode.setText("New Code");

		projects = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_list.heightHint = 255;
		gd_list.widthHint = 119;
		projects.setLayoutData(gd_list);
		projects.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (syncTargets != null)
					syncTargets.setItems(new String[] {});
				if (changes != null)
					changes.setItems(new String[] {});
				if (classes != null)
					classes.setItems(new String[] {});
				if (codeOfTarget != null)
					codeOfTarget.removeAll();
				if (oldCode != null)
					oldCode.removeAll();
				if (newCode != null)
					newCode.removeAll();
				if (syncPreview != null)
					syncPreview.removeAll();
				selectedProject = projects.getSelection()[0];
				classes.setItems(cc.getClasses(selectedFeatureExpression,
						selectedProject).toArray(new String[] {}));
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		new Label(arg0, SWT.NONE);

		classes = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1);
		gd_list_1.heightHint = 259;
		gd_list_1.widthHint = 83;
		classes.setLayoutData(gd_list_1);
		classes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (syncTargets != null)
					syncTargets.setItems(new String[] {});
				if (codeOfTarget != null)
					codeOfTarget.removeAll();
				if (oldCode != null)
					oldCode.removeAll();
				if (newCode != null)
					newCode.removeAll();
				selectedClass = classes.getSelection()[0];
				leftClass = selectedProject + " - " + selectedClass;
				setChanges();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		new Label(arg0, SWT.NONE);

		changes = new List(arg0, SWT.H_SCROLL | SWT.BORDER);
		GridData gd_changes = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1);
		gd_changes.heightHint = 256;
		gd_changes.widthHint = 93;
		changes.setLayoutData(gd_changes);
		changes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (oldCode != null)
					oldCode.removeAll();
				if (newCode != null)
					newCode.removeAll();
				selectedChange = changes.getSelectionIndex();
				btnRemoveChangeEntry.setEnabled(true);
				Iterator<CodeChange> it = collChanges.iterator();
				int i = 0;
				oldCode.removeAll();
				newCode.removeAll();
				CodeHighlighting ccolor = cc
						.getContextColor(selectedFeatureExpression);
				while (it.hasNext()) {
					CodeChange ch = it.next();
					if (i == selectedChange) {
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
						oldCode.removeAll();
						for (CodeLine cl : baseCode) {
							TableItem item = new TableItem(oldCode, SWT.NONE);
							item.setText(cl.getLine() + ": " + cl.getCode());
							if (cl.isMapped()) {
								Color color = new Color(getSite().getShell()
										.getDisplay(), ccolor.getRGB());
								item.setBackground(color);
							}
						}
						java.util.List<CodeLine> code = ch
								.getNewVersionWholeClass();
						mappedCode = ch.getNewVersion();
						for (CodeLine clWC : code) {
							for (CodeLine cl : mappedCode) {
								if (cl.getLine() == clWC.getLine()) {
									clWC.setMapped(true);
								}
							}
						}
						newCode.removeAll();
						for (CodeLine cl : code) {
							TableItem item = new TableItem(newCode, SWT.NONE);
							item.setText(cl.getLine() + ": " + cl.getCode());
							if (cl.isMapped()) {
								Color color = new Color(getSite().getShell()
										.getDisplay(), ccolor.getRGB());
								item.setBackground(color);
							}
							if (cl.isNew()) {
								item.setForeground(getSite().getShell()
										.getDisplay()
										.getSystemColor(SWT.COLOR_DARK_RED));
							}
						}
						break;
					}
					i++;
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		new Label(arg0, SWT.NONE);

		oldCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 241;
		oldCode.setLayoutData(gd_text);
		new Label(arg0, SWT.NONE);

		newCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text_1.heightHint = 251;
		newCode.setLayoutData(gd_text_1);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		btnRemoveChangeEntry = new Button(arg0, SWT.NONE);
		btnRemoveChangeEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnRemoveChangeEntry.setText("Remove Change Entry");
		btnRemoveChangeEntry.setEnabled(false);
		btnRemoveChangeEntry.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					contextOperations.removeChange(selectedFeatureExpression,
							selectedProject, selectedClass, selectedChange);
					btnRemoveChangeEntry.setEnabled(false);
					setChanges();
					if (codeOfTarget != null)
						codeOfTarget.removeAll();
					if (oldCode != null)
						oldCode.removeAll();
					if (newCode != null)
						newCode.removeAll();
					if (syncPreview != null)
						syncPreview.removeAll();
				}
				}
			}
		});
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		Label lblSyncTargets = new Label(arg0, SWT.NONE);
		lblSyncTargets.setText("Sync Targets");
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);

		Label lblCodeOfTarget = new Label(arg0, SWT.NONE);
		lblCodeOfTarget.setText("Code of Target");
		new Label(arg0, SWT.NONE);

		Label lblSyncPreview = new Label(arg0, SWT.NONE);
		lblSyncPreview.setText("Sync Preview");

		syncTargets = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_syncTargets = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_syncTargets.heightHint = 79;
		gd_syncTargets.widthHint = 100;
		syncTargets.setLayoutData(gd_syncTargets);
		syncTargets.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				if (codeOfTarget != null)
					codeOfTarget.removeAll();
				Iterator<CodeChange> it = collChanges.iterator();
				java.util.List<CodeLine> newCode = null;
				int i = 0;
				while (it.hasNext()) {
					CodeChange cc = it.next();
					if (i == selectedChange) {
						newCode = cc.getNewVersionWholeClass();
					}
					i++;
				}
				String selection = syncTargets.getSelection()[0];
				String[] tmp = selection.split(":");
				projectNameTarget = tmp[0].trim();
				classNameTarget = tmp[1].trim();
				rightClass = projectNameTarget + " - " + classNameTarget;
				java.util.List<CodeLine> code = cc.getTargetCode(
						selectedFeatureExpression, projectNameTarget,
						classNameTarget);
				codeWC = cc.getTargetCodeWholeClass(selectedFeatureExpression,
						projectNameTarget, classNameTarget);
				for (CodeLine clWC : codeWC) {
					for (CodeLine cl : code) {
						if (cl.getLine() == clWC.getLine()) {
							clWC.setMapped(true);
						}
					}
				}
				CodeHighlighting ccolor = cc
						.getContextColor(selectedFeatureExpression);
				codeOfTarget.removeAll();
				for (CodeLine cl : codeWC) {
					TableItem item = new TableItem(codeOfTarget, SWT.NONE);
					item.setText(cl.getLine() + ": " + cl.getCode());
					if (cl.isMapped()) {
						Color color = new Color(getSite().getShell()
								.getDisplay(), ccolor.getRGB());
						item.setBackground(color);
					}
				}
				syncCode = sc.doAutoSync(newCode, baseCode, codeWC);
				left = new ArrayList<CodeLine>();
				right = new ArrayList<CodeLine>();
				if (syncCode != null && !syncCode.isEmpty()) {
					btnSynchronize.setEnabled(true);
					boolean addLeft = false;
					boolean addRight = false;
					syncPreview.removeAll();
					for (CodeLine cl : syncCode) {
						TableItem item = new TableItem(syncPreview, SWT.NONE);
						item.setText(cl.getLine() + ": " + cl.getCode());
						if (cl.getCode().contains("<<<<<<<")) {
							btnSynchronize.setEnabled(false);
							btnManualSync.setEnabled(true);
							addLeft = true;
						}
						if (cl.getCode().contains("=======")) {
							addLeft = false;
							addRight = true;
							continue;
						}
						if (addRight) {
							item.setBackground(new Color(getSite().getShell()
									.getDisplay(), CodeHighlighting.RED
									.getRGB()));
						}
						if (cl.getCode().contains(">>>>>>>")) {
							addRight = false;
						}
						if (addLeft) {
							item.setBackground(new Color(getSite().getShell()
									.getDisplay(), CodeHighlighting.RED
									.getRGB()));
						}
					}
					if (left.isEmpty() && right.isEmpty()) {
						left = codeWC;
						right = syncCode;
					}
				} else {
					btnManualSync.setEnabled(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		new Label(arg0, SWT.NONE);

		btnSynchronize = new Button(arg0, SWT.NONE);
		btnSynchronize.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnSynchronize.setText("Auto Sync");
		btnSynchronize.setEnabled(false);
		btnSynchronize.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					solveChange(syncCode);
					// setChanges();
					btnSynchronize.setEnabled(false);
					if (codeOfTarget != null)
						codeOfTarget.removeAll();
					if (oldCode != null)
						oldCode.removeAll();
					if (newCode != null)
						newCode.removeAll();
					if (syncPreview != null)
						syncPreview.removeAll();

					// Problem: only changes of merged feature
					// expression is mapped, changes of other feature
					// expressions are lost
					cc.refreshContext(true, selectedFeatureExpression,
							projectNameTarget, classNameTarget, codeWC,
							syncCode);
					break;
				}
				}
			}
		});
		new Label(arg0, SWT.NONE);

		btnManualSync = new Button(arg0, SWT.NONE);
		btnManualSync.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		btnManualSync.setText("Manual Sync");
		btnManualSync.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					ManualMerge m = new ManualMerge(reference, left, leftClass,
							right, rightClass, syncCode);
					m.open();
					break;
				}
			}
		});
		new Label(arg0, SWT.NONE);

		codeOfTarget = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		gd_text = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_text.heightHint = 247;
		codeOfTarget.setLayoutData(gd_text);
		new Label(arg0, SWT.NONE);

		syncPreview = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 111;
		syncPreview.setLayoutData(gd_text);
	}

	public void solveChange(java.util.List<CodeLine> code) {
		File file = contextOperations.getFile(selectedFeatureExpression,
				projectNameTarget, classNameTarget);
		persistanceOperations.writeFile(code, file);
		IResource res = contextOperations.getResource(
				selectedFeatureExpression, projectNameTarget, classNameTarget);
		try {
			res.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		// contextOperations.removeChange(selectedFeatureExpression,
		// selectedProject, selectedClass, selectedChange);
		// btnRemoveChangeEntry.setEnabled(false);
	}

	public void setChanges() {
		collChanges = cc.getChanges(selectedFeatureExpression, selectedProject,
				selectedClass);
		java.util.List<String> timestamps = new ArrayList<String>();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss 'at' dd.MM.yyyy");
		for (CodeChange ch : collChanges) {
			timestamps.add(formatter.format(new Date(ch.getTimestamp())));
		}
		changes.setItems(timestamps.toArray(new String[] {}));

		String[] items = cc.getSyncTargets(selectedFeatureExpression,
				selectedProject, selectedClass).toArray(new String[] {});
		syncTargets.setItems(items);
	}

	public void checkManualMerge(java.util.List<CodeLine> mergeResult) {
		boolean manualMergeIsNeeded = false;
		for (CodeLine cl : mergeResult) {
			if (cl.getCode().contains("<<<<<<<")) {
				manualMergeIsNeeded = true;
				break;
			}
		}
		if (manualMergeIsNeeded) {
			UtilOperations.getInstance().printCode(mergeResult);
			ManualMerge m = new ManualMerge(reference, mergeResult, leftClass,
					mergeResult, rightClass, syncCode);
			m.open();
		} else {
			btnManualSync.setEnabled(false);
			if (codeOfTarget != null)
				codeOfTarget.removeAll();
			if (oldCode != null)
				oldCode.removeAll();
			if (newCode != null)
				newCode.removeAll();
			if (syncPreview != null)
				syncPreview.removeAll();
			cc.refreshContext(false, selectedFeatureExpression,
					projectNameTarget, classNameTarget, codeWC, syncCode);
			solveChange(mergeResult);
			setChanges();
		}
	}
}
