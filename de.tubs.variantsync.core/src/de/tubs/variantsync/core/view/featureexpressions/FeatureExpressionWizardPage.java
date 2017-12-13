package de.tubs.variantsync.core.view.featureexpressions;

import static de.ovgu.featureide.fm.core.localization.StringTable.OPERATORS;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import de.ovgu.featureide.fm.core.Operator;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.ColorPalette;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.core.functional.Functional;
import de.ovgu.featureide.fm.ui.editors.SimpleSyntaxHighlightEditor;
import de.tubs.variantsync.core.data.FeatureExpression;

/**
 * Page for {@link FeatureExpressionManager}.
 * 
 * @author Christopher Sontag
 */
public class FeatureExpressionWizardPage extends WizardPage {

	protected static final String FILTERTEXT = "Search...";
	private final Iterable<IFeature> features;
	private TableViewer featureTable;
	private StyledText searchFeatureText;
	private SimpleSyntaxHighlightEditor expressionText;
	private FeatureExpression featureExpression = null;
	private Combo colorBox;

	protected FeatureExpressionWizardPage(Iterable<IFeature> features, FeatureExpression featureExpression) {
		super("FeatureExpressionManager");
		this.features = features;
		this.featureExpression = featureExpression;
		setTitle("Feature Context");
		if (featureExpression == null) {
			setDescription("Create Feature Context");
		} else {
			setDescription("Edit Feature Context");
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		// Features Group
		Group featureGroup = new Group(composite, SWT.NONE);
		featureGroup.setText("Features");
		featureGroup.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		GridLayout featureGroupLayout = new GridLayout();
		featureGroupLayout.numColumns = 1;
		featureGroup.setLayout(featureGroupLayout);

		// Feature Search
		searchFeatureText = new StyledText(featureGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		searchFeatureText.setText(FILTERTEXT);
		searchFeatureText.setMargins(3, 5, 3, 5);
		searchFeatureText.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));

		// Feature Table
		featureTable = new TableViewer(featureGroup, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		featureTable.getTable().setHeaderVisible(false);
		featureTable.getTable().setLinesVisible(true);
		featureTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		featureTable.setContentProvider(ArrayContentProvider.getInstance());
		featureTable.setInput(features);

		// Feature Table Column
		TableViewerColumn featureTableColumn = new TableViewerColumn(featureTable, SWT.NONE);
		featureTableColumn.getColumn().setWidth(600);
		featureTableColumn.getColumn().setText("Name");
		featureTableColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((IFeature) cell.getElement()).getName());
			}
		});

		// Feature Search Listener
		searchFeatureText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!FILTERTEXT.equalsIgnoreCase(searchFeatureText.getText())) {
					ViewerFilter searchFilter = new ViewerFilter() {

						@Override
						public boolean select(Viewer viewer, Object parentElement, Object element) {
							return ((IFeature) element).getName().toLowerCase(Locale.ENGLISH).contains(searchFeatureText.getText().toLowerCase(Locale.ENGLISH));
						}
					};
					featureTable.addFilter(searchFilter);
				}
			}
		});
		searchFeatureText.addListener(SWT.FocusOut, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (searchFeatureText.getText().isEmpty()) {
					searchFeatureText.setText(FILTERTEXT);
				}
			}
		});
		searchFeatureText.addListener(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (FILTERTEXT.equals(searchFeatureText.getText())) {
					searchFeatureText.setText("");
				}
			}
		});

		// Feature Table Listener
		featureTable.getTable().addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TableItem[] selectedItem = featureTable.getTable().getSelection();
				if (selectedItem.length > 0) {
					String featureName = selectedItem[0].getText();
					if (featureName.matches(".*?\\s+.*")) {
						featureName = "\"" + featureName + "\"";
					} else {
						for (String op : Operator.NAMES) {
							if (featureName.equalsIgnoreCase(op)) {
								featureName = "\"" + featureName + "\"";
								break;
							}
						}
					}
					expressionText.copyIn(featureName);
				}
			}
		});

		// Operator Buttons
		Group buttonGroup = new Group(composite, SWT.NONE);
		buttonGroup.setText(OPERATORS);
		buttonGroup.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		GridLayout buttonGroupLayout = new GridLayout();
		buttonGroupLayout.numColumns = 7;
		buttonGroup.setLayout(buttonGroupLayout);

		for (int i = 0; i < Operator.NAMES.length; i++) {
			Button button = new Button(buttonGroup, SWT.PUSH);
			button.setText(Operator.NAMES[i]);
			button.setLayoutData(new GridData(SWT.WRAP, SWT.WRAP, false, false));
			button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					expressionText.copyIn(button.getText().toLowerCase(Locale.ENGLISH));
				}
			});
		}

		// Input Text
		expressionText = new SimpleSyntaxHighlightEditor(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.BORDER, Operator.NAMES);

		expressionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (featureExpression != null) expressionText.setText(featureExpression.name);
		expressionText.setMargins(10, 5, 3, 5);
		expressionText.setPossibleWords(Functional.toSet(FeatureUtils.extractFeatureNames(features)));
		if (featureExpression != null) expressionText.setBackground(ColorPalette.toSwtColor(featureExpression.highlighter));

		expressionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// validate();
			}
		});

		// Color Selector
		colorBox = new Combo(composite, SWT.READ_ONLY);
		colorBox.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		List<String> colors = new ArrayList<>();
		for (FeatureColor color : FeatureColor.values()) {
			colors.add(color.getColorName());
		}
		colorBox.setItems(colors.toArray(new String[] {}));
		if (featureExpression != null) colorBox.select(featureExpression.highlighter.ordinal());

		colorBox.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				expressionText.setBackground(ColorPalette.toSwtColor(getColor()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		setControl(composite);
	}

	public String getFeatureExpression() {
		return expressionText.getText();
	}

	public FeatureColor getColor() {
		return FeatureColor.getColor(colorBox.getText());
	}

}
