package de.tubs.variantsync.core.view.featurecontext;

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
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.color.ColorPalette;
import de.ovgu.featureide.fm.core.color.FeatureColor;
import de.ovgu.featureide.fm.ui.editors.SimpleSyntaxHighlightEditor;
import de.tubs.variantsync.core.managers.data.FeatureContext;

/**
 * Page for {@link FeatureContextManager}.
 *
 * @author Christopher Sontag
 */
public class FeatureContextWizardPage extends WizardPage {

	protected static final String FILTERTEXT = "Search...";
	private final Iterable<IFeature> features;
	private TableViewer tabFeatures;
	private StyledText txtSearch;
	private SimpleSyntaxHighlightEditor txtContext;
	private FeatureContext featureContext = null;
	private Combo cbColors;

	protected FeatureContextWizardPage(Iterable<IFeature> features, FeatureContext featureContext) {
		super("FeatureContextManager");
		this.features = features;
		this.featureContext = featureContext;
		setTitle("Feature ConfigurationProject");
		if (featureContext == null) {
			setDescription("Create Feature ConfigurationProject");
		} else {
			setDescription("Edit Feature ConfigurationProject");
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		// Features Group
		final Group grFeatures = new Group(composite, SWT.NONE);
		grFeatures.setText("Features");
		grFeatures.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		final GridLayout grFeaturesLayout = new GridLayout();
		grFeaturesLayout.numColumns = 1;
		grFeatures.setLayout(grFeaturesLayout);

		// Feature Search
		txtSearch = new StyledText(grFeatures, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtSearch.setText(FILTERTEXT);
		txtSearch.setMargins(3, 5, 3, 5);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));

		// Feature Table
		tabFeatures = new TableViewer(grFeatures, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		tabFeatures.getTable().setHeaderVisible(false);
		tabFeatures.getTable().setLinesVisible(true);
		tabFeatures.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabFeatures.setContentProvider(ArrayContentProvider.getInstance());
		tabFeatures.setInput(features);

		// Feature Table Column
		final TableViewerColumn tcFeatures = new TableViewerColumn(tabFeatures, SWT.NONE);
		tcFeatures.getColumn().setWidth(600);
		tcFeatures.getColumn().setText("Name");
		tcFeatures.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((IFeature) cell.getElement()).getName());
			}
		});

		// Feature Search Listener
		txtSearch.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!FILTERTEXT.equalsIgnoreCase(txtSearch.getText())) {
					final ViewerFilter searchFilter = new ViewerFilter() {

						@Override
						public boolean select(Viewer viewer, Object parentElement, Object element) {
							return ((IFeature) element).getName().toLowerCase(Locale.ENGLISH).contains(txtSearch.getText().toLowerCase(Locale.ENGLISH));
						}
					};
					tabFeatures.addFilter(searchFilter);
				}
			}
		});
		txtSearch.addListener(SWT.FocusOut, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (txtSearch.getText().isEmpty()) {
					txtSearch.setText(FILTERTEXT);
				}
			}
		});
		txtSearch.addListener(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (FILTERTEXT.equals(txtSearch.getText())) {
					txtSearch.setText("");
				}
			}
		});

		// Feature Table Listener
		tabFeatures.getTable().addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final TableItem[] selectedItem = tabFeatures.getTable().getSelection();
				if (selectedItem.length > 0) {
					String featureName = selectedItem[0].getText();
					// if the featureName is of the following structure:
					// arbitrary amount of chars until the first occurence of 1 or more consecutive spaces followed by an arbitrary amount of chars
					if (featureName.matches(".*?\\s+.*")) {
						featureName += String.format("\"%s\"", featureName);
					} else {
						for (final String op : Operator.NAMES) {
							if (featureName.equalsIgnoreCase(op)) {
								featureName += String.format("\"%s\"", featureName);
								break;
							}
						}
					}
					txtContext.copyIn(featureName);
				}
			}
		});

		// Operator Buttons
		final Group grButton = new Group(composite, SWT.NONE);
		grButton.setText(OPERATORS);
		grButton.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		final GridLayout grButtonsLayout = new GridLayout();
		grButtonsLayout.numColumns = 7;
		grButton.setLayout(grButtonsLayout);

		for (int i = 0; i < Operator.NAMES.length; i++) {
			final Button button = new Button(grButton, SWT.PUSH);
			button.setText(Operator.NAMES[i]);
			button.setLayoutData(new GridData(SWT.WRAP, SWT.WRAP, false, false));
			button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

				@Override
				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					txtContext.copyIn(button.getText().toLowerCase(Locale.ENGLISH));
				}
			});
		}

		// Input Text
		txtContext = new SimpleSyntaxHighlightEditor(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.BORDER, Operator.NAMES);

		txtContext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (featureContext != null) {
			txtContext.setText(featureContext.name);
		}
		txtContext.setMargins(10, 5, 3, 5);
		if (featureContext != null) {
			txtContext.setBackground(ColorPalette.toSwtColor(featureContext.highlighter));
		}

		txtContext.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// TODO validate();
			}
		});

		// Color Selector
		cbColors = new Combo(composite, SWT.READ_ONLY);
		cbColors.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));
		final List<String> colors = new ArrayList<>();
		for (final FeatureColor color : FeatureColor.values()) {
			colors.add(color.getColorName());
		}
		cbColors.setItems(colors.toArray(new String[] {}));
		if (featureContext != null) {
			cbColors.select(featureContext.highlighter.ordinal());
		}

		cbColors.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtContext.setBackground(ColorPalette.toSwtColor(getColor()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		setControl(composite);
	}

	public String getFeatureContext() {
		return txtContext.getText();
	}

	public FeatureColor getColor() {
		return FeatureColor.getColor(cbColors.getText());
	}

}
