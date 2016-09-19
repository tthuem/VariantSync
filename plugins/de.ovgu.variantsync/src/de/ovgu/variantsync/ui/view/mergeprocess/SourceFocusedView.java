package de.ovgu.variantsync.ui.view.mergeprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeChange;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.10.2015
 */
public class SourceFocusedView extends SynchronizationView {

	private List projects;
	private List classes;
	private String featureExpressions[];
	private Collection<String> syncCode;
	private String projectNameTarget;
	private String classNameTarget;
	private CCombo combo;
	private Label lblMergeConflict;
	private Label lblChangedCode;
	private String autoSelection;
	private String manualSelection;
	private GridData gd_list_2;
	private String selectedFeatureExpression;

	private int selectionIndex;

	public SourceFocusedView() {
	}

	public void dispose() {
	}

	public void setFocus() {
		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions()
				.toArray(new String[] {});
		combo.setItems(featureExpressions);
		if (selectedFeatureExpression != null) {
			contextOperations.activateContext(selectedFeatureExpression, true);
			cc.setFeatureView(true);
		}
	}

	@Override
	public void createPartControl(final Composite arg0) {
		isManSynced = false;
		featureExpressions = fc.getFeatureExpressions().getFeatureExpressions()
				.toArray(new String[] {});
		arg0.setLayout(new GridLayout(5, false));

		Label lblSelectFeatureExpression = new Label(arg0, SWT.NONE);
		lblSelectFeatureExpression.setText("Select Feature Expression");
		combo = new CCombo(arg0, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
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
				projects.setItems(cc.getProjects(combo.getText()).toArray(
						new String[] {}));
				contextOperations.activateContext(selectedFeatureExpression,
						true);
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
				classes.setItems(cc.getClasses(selectedFeatureExpression,
						selectedProject).toArray(new String[] {}));
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
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				5);
		gd_list_1.heightHint = 259;
		gd_list_1.widthHint = 83;
		classes.setLayoutData(gd_list_1);
		classes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				processClassSelection(classes.getSelection()[0]);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		changes = new List(arg0, SWT.H_SCROLL | SWT.BORDER);
		GridData gd_changes = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				5);
		gd_changes.heightHint = 256;
		gd_changes.widthHint = 165;
		changes.setLayoutData(gd_changes);
		changes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				processChangeSelection(selectedFeatureExpression);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		newCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				5);
		gd_text_1.widthHint = 157;
		newCode.setLayoutData(gd_text_1);

		autoSyncTargets = new List(arg0, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		GridData gd_syncTargets = new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1);
		gd_syncTargets.heightHint = 89;
		gd_syncTargets.widthHint = 143;
		autoSyncTargets.setLayoutData(gd_syncTargets);
		autoSyncTargets.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				selectionIndex = autoSyncTargets.getSelectionIndex();
				autoSelection = autoSyncTargets.getSelection()[0];
				String[] tmp = autoSelection.split(":");
				projectNameTarget = tmp[0].trim();
				classNameTarget = tmp[1].trim();

				syncCode = sc.doAutoSync(left, base, cc.getTargetFile(
						selectedFeatureExpression, projectNameTarget,
						classNameTarget));
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
		btnSynchronize.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnSynchronize.setText("Auto Sync");
		btnSynchronize.setEnabled(false);
		btnSynchronize.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					processAutoSync(selectedFeatureExpression,
							projectNameTarget, classNameTarget, syncCode,
							autoSelection, selectionIndex);
					break;
				}
				}
			}
		});

		lblMergeConflict = new Label(arg0, SWT.NONE);
		lblMergeConflict.setText("conflict - manual sync necessary");

		manualSyncTargets = new List(arg0, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		gd_list_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_list_2.heightHint = 89;
		gd_list_2.widthHint = 191;
		manualSyncTargets.setLayoutData(gd_list_2);
		manualSyncTargets.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				selectionIndex = manualSyncTargets.getSelectionIndex();
				int i = 0;
				for (String target : manualSyncTargetsAsList) {
					if (i == selectionIndex) {
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
		btnManualSync.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 1, 1));
		btnManualSync.setText("Manual Sync");
		btnManualSync.setEnabled(false);
		btnManualSync.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					processManualSync(selectedFeatureExpression,
							projectNameTarget, classNameTarget,
							manualSelection, selectionIndex);
					break;
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
		btnSyncPreview.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnSyncPreview.setText("sync preview");
		btnSyncPreview.setVisible(false);
		new Label(arg0, SWT.NONE);
		new Label(arg0, SWT.NONE);
	}

	private void startBatchSync(String[] variantBatchSelection) {
		for (String variant : variantBatchSelection) {
			String[] classes = cc
					.getClasses(selectedFeatureExpression, variant).toArray(
							new String[] {});
			for (String clazz : classes) {
				Collection<CodeChange> collChanges = cc.getChanges(
						selectedFeatureExpression, variant, clazz);
				processBatchSynchronization(selectedFeatureExpression, variant,
						clazz, collChanges);
			}
		}
	}

	protected void setChanges() {
		collChanges = cc.getChanges(selectedFeatureExpression, selectedProject,
				selectedClass);
		java.util.List<String> timestamps = new ArrayList<String>();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"hh:mm:ss 'at' dd.MM.yyyy");
		for (CodeChange ch : collChanges) {
			timestamps.add(formatter.format(new Date(ch.getTimestamp())));
		}
		changes.setItems(timestamps.toArray(new String[] {}));
	}
}
