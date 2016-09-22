package de.ovgu.variantsync.ui.view.mergeprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class TargetFocusedView extends SynchronizationView {

	private List features;
	private String selectedVariant;
	private String selectedFeature;
	private String[] variants;
	private CCombo combo;
	private Label lblMergeConflict;
	private Label lblChangedCode;
	private Map<String, Collection<CodeChange>> changeMap;
	private String selClass;

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
		variants = fc.getFeatureExpressions().getFeatureExpressions().toArray(new String[] {});
		arg0.setLayout(new GridLayout(5, false));

		Label lblSelectFeatureExpression = new Label(arg0, SWT.NONE);
		lblSelectFeatureExpression.setText("Select Variant");
		combo = new CCombo(arg0, SWT.BORDER);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
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

				java.util.List<String> f = cc.getFeatures(combo.getText());
				f.remove("Default_Context");
				features.setItems(f.toArray(new String[] {}));
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
				classes.setItems(cc.getClassesForVariant(selectedFeature, selectedVariant).toArray(new String[] {}));
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
						startBatchSync(variants.toArray(new String[] {}));
					}
				});
			}
		});

		classes = new List(arg0, SWT.BORDER | SWT.H_SCROLL);
		GridData gd_list_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 11);
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
		GridData gd_changes = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 11);
		gd_changes.heightHint = 256;
		gd_changes.widthHint = 165;
		changes.setLayoutData(gd_changes);
		changes.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				processChangeSelection(selectedFeature);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		newCode = new Table(arg0, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 11);
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
		btnSynchronize.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSynchronize.setText("Auto Sync");
		btnSynchronize.setEnabled(false);
		btnSynchronize.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection: {
					processAutoSync(selectedFeature, selectedVariant, selectedClass.split(":")[1].trim(),
							sc.doAutoSync(left, base,
									cc.getTargetFile(selectedFeature, selectedVariant,
											selectedClass.split(":")[1].trim())),
							selectedClass.split(":")[0].trim(), 0);
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
		btnManualSync.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnManualSync.setText("Manual Sync");
		btnManualSync.setEnabled(false);
		btnManualSync.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					selectedProject = selectedVariant;
					processManualSync(selectedFeature, selectedClass.split(":")[0].trim(),
							selectedClass.split(":")[1].trim(), selectedClass.split(":")[0].trim(), 0);
					break;
				}
			}
		});
		new Label(arg0, SWT.NONE);
	}

	private void startBatchSync(String[] featureBatchSelection) {
		for (String feature : featureBatchSelection) {
			String[] classes = cc.getClassesForVariant(feature, selectedVariant).toArray(new String[] {});
			for (String clazz : classes) {
				Map<String, Collection<CodeChange>> collChanges = cc.getChangesForVariant(selectedFeature, feature,
						clazz);
				Set<Entry<String, Collection<CodeChange>>> entry = collChanges.entrySet();
				Iterator<Entry<String, Collection<CodeChange>>> it = entry.iterator();
				while (it.hasNext()) {
					Entry<String, Collection<CodeChange>> en = it.next();
					processBatchSynchronization(feature, selectedVariant, clazz, en.getValue());
				}
			}
		}
	}

	protected void setChanges() {
		collChanges = new ArrayList<CodeChange>();
		java.util.List<String> timestamps = new ArrayList<String>();
		changeMap = cc.getChangesForVariant(selectedFeature, selectedVariant, selectedClass);
		Set<Entry<String, Collection<CodeChange>>> entries = changeMap.entrySet();
		Iterator<Entry<String, Collection<CodeChange>>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, Collection<CodeChange>> entry = it.next();
			Collection<CodeChange> c = entry.getValue();
			selClass = selectedClass.split(":")[0].trim();
			for (CodeChange ch : c) {
				if (entry.getKey().equals(selClass)) {
					collChanges.add(ch);
					SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss 'at' dd.MM.yyyy");
					timestamps.add(formatter.format(new Date(ch.getTimestamp())));
				}
			}
		}
		changes.setItems(timestamps.toArray(new String[] {}));
	}

	@Override
	protected void refreshSyncTargets(String fe, String project, String clazz, Collection<String> base,
			Collection<String> left) {
		btnSynchronize.setEnabled(true);
		// btnManualSync.setEnabled(true);
	}
}
