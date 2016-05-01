package de.ovgu.variantsync.presentationlayer.view.context;

import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.ui.editors.featuremodel.GUIDefaults;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.presentationlayer.controller.ControllerHandler;
import de.ovgu.variantsync.presentationlayer.controller.FeatureController;
import de.ovgu.variantsync.presentationlayer.view.context.ConstraintDialog.StringTable;

public class FeatureManagementDialog {

	private static FeatureController featureController = ControllerHandler
			.getInstance().getFeatureController();
	private Shell shell;
	private Group featureGroup;
	private static final String FILTERTEXT = "type filter text";
	private HeaderPanel headerPanel;
	private Button okButton;
	private Button closeButton;
	private Button deleteButton;
	private Button colorButton;
	private StyledText searchFeatureText;
	private Table featureTable;
	private String defaultDetailsText = "Create new feature expressions or delete selected feature expressions.";
	private String defaultHeaderText = "Manage Feature-Expressions";

	public FeatureManagementDialog() {
		initShell();
		initHead();
		initFeatureGroup();
		initBottom();
		shell.open();
	}

	private void initShell() {
		shell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL
				| SWT.SHEET);
		shell.setText("Feature-Expression Management Dialog");
		shell.setImage(VariantSyncPlugin.getDefault()
				.getImageDescriptor("icons/featureExpression.png")
				.createImage());
		shell.setSize(500, 485);

		GridLayout shellLayout = new GridLayout();
		shellLayout.marginWidth = 0;
		shellLayout.marginHeight = 0;
		shell.setLayout(shellLayout);

		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	private void initHead() {
		headerPanel = new HeaderPanel(shell);
		headerPanel.setHeader(defaultHeaderText);
		headerPanel.setDetails(defaultDetailsText,
				HeaderPanel.HeaderDescriptionImage.NONE);
	}

	private void initFeatureGroup() {

		featureGroup = new Group(shell, SWT.NONE);
		featureGroup.setText("Feature-Expressions");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		featureGroup.setLayoutData(gridData);
		GridLayout featureGroupLayout = new GridLayout();
		featureGroupLayout.numColumns = 1;
		featureGroup.setLayout(featureGroupLayout);

		searchFeatureText = new StyledText(featureGroup, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		searchFeatureText.setText(FILTERTEXT);
		searchFeatureText.setMargins(3, 5, 3, 5);
		searchFeatureText.setForeground(shell.getDisplay().getSystemColor(
				SWT.COLOR_GRAY));
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		searchFeatureText.setLayoutData(gridData);

		Composite tableComposite = new Composite(featureGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tableComposite.setLayoutData(gridData);

		final TableViewer featureTableViewer = new TableViewer(tableComposite,
				SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		featureTable = featureTableViewer.getTable();
		featureTableViewer.setContentProvider(new ArrayContentProvider());
		TableViewerColumn viewerNameColumn = new TableViewerColumn(
				featureTableViewer, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableColumnLayout.setColumnData(viewerNameColumn.getColumn(),
				new ColumnWeightData(100, 100, false));
		featureTableViewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object feature1, Object feature2) {

				return ((String) feature1)
						.compareToIgnoreCase(((String) feature2));
			}

		});

		viewerNameColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((String) cell.getElement()));
				cell.setImage(VariantSyncPlugin.getDefault()
						.getImageDescriptor("icons/featureExpression.png")
						.createImage());
			}
		});

		searchFeatureText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!FILTERTEXT.equalsIgnoreCase(searchFeatureText.getText())) {
					ViewerFilter searchFilter = new ViewerFilter() {

						@Override
						public boolean select(Viewer viewer,
								Object parentElement, Object element) {
							return ((String) element).toLowerCase(
									Locale.ENGLISH).contains(
									searchFeatureText.getText().toLowerCase(
											Locale.ENGLISH));
						}

					};
					featureTableViewer.addFilter(searchFilter);

				}
			}

		});

		searchFeatureText.addListener(SWT.FocusOut, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (searchFeatureText.getText().isEmpty()) {
					searchFeatureText.setText(FILTERTEXT);
					searchFeatureText.setForeground(shell.getDisplay()
							.getSystemColor(SWT.COLOR_GRAY));

				}

			}
		});
		searchFeatureText.addListener(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (FILTERTEXT.equals(searchFeatureText.getText())) {
					searchFeatureText.setText("");
				}
				searchFeatureText.setForeground(shell.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
			}

		});

		featureTableViewer.setInput(featureController.getFeatureExpressions()
				.getFeatureExpressionsAsSet());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		featureTable.setLayoutData(gridData);
	}

	private void initBottom() {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Composite lastComposite = new Composite(shell, SWT.NONE);
		lastComposite.setLayoutData(gridData);

		FormLayout lastCompositeLayout = new FormLayout();
		lastCompositeLayout.marginHeight = 5;
		lastCompositeLayout.marginTop = 85;
		lastCompositeLayout.marginWidth = 5;
		lastComposite.setLayout(lastCompositeLayout);
		ToolBar helpButtonBar = new ToolBar(lastComposite, SWT.FLAT);
		ToolItem helpButton = new ToolItem(helpButtonBar, SWT.NONE);
		helpButton.setImage(VariantSyncPlugin.getDefault()
				.getImageDescriptor("icons/questionMark.jpg").createImage());
		helpButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						Program.launch(StringTable.HREF_HELP_LINK);
					}
				});
		FormData formDataHelp = new FormData();
		formDataHelp.left = new FormAttachment(0, 5);
		helpButtonBar.setLayoutData(formDataHelp);

		closeButton = new Button(lastComposite, SWT.NONE);
		closeButton.setText("Close");
		FormData formDataCancel = new FormData();
		formDataCancel.width = 70;
		formDataCancel.right = new FormAttachment(100, -5);
		formDataCancel.bottom = new FormAttachment(100, -5);

		deleteButton = new Button(lastComposite, SWT.NONE);
		deleteButton.setText("Delete");
		FormData formDataDelete = new FormData();
		formDataDelete.width = 70;
		formDataDelete.right = new FormAttachment(closeButton, -5);
		formDataDelete.bottom = new FormAttachment(100, -5);

		okButton = new Button(lastComposite, SWT.NONE);
		okButton.setText("Create new Constraint");
		FormData formDataOk = new FormData();
		formDataOk.width = 150;
		formDataOk.right = new FormAttachment(deleteButton, -5);
		formDataOk.bottom = new FormAttachment(100, -5);
		okButton.setLayoutData(formDataOk);

		colorButton = new Button(lastComposite, SWT.NONE);
		colorButton.setText("Color");
		FormData formDataColor = new FormData();
		formDataColor.width = 70;
		formDataColor.right = new FormAttachment(okButton, -5);
		formDataColor.bottom = new FormAttachment(100, -5);

		closeButton.setLayoutData(formDataCancel);
		colorButton.setLayoutData(formDataColor);
		deleteButton.setLayoutData(formDataDelete);

		shell.setTabList(new Control[] { featureGroup, lastComposite });
		closeButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FeatureContextSelection.refreshMenuItems();
						shell.dispose();
					}
				});
		deleteButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						TableItem[] selectedItem = featureTable.getSelection();
						if (selectedItem.length > 0) {
							String selectedFeatureExpression = selectedItem[0]
									.getText();
							featureController
									.deleteFeatureExpression(selectedFeatureExpression);
							featureTable.remove(featureTable
									.getSelectionIndex());
						} else {
							Display display = PlatformUI.getWorkbench()
									.getDisplay();
							MessageDialog.openWarning(display.getActiveShell(),
									"Warning",
									"Please select the feature-expression that you want to delete.");
						}
					}
				});
		colorButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								new CustomColorDialog(featureController
										.getFeatureExpressions()
										.getFeatureExpressions());
							}
						});
					}
				});

		lastComposite.setTabList(new Control[] { colorButton, okButton,
				deleteButton, closeButton });

		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				new ConstraintDialog(null);
				shell.dispose();
			}

		});
	}

	public static class HeaderPanel {

		/**
		 * Image types for description inside header panel
		 * {@link ConstraintDialog.HeaderPanel#headerDescriptionImageLabel}
		 * 
		 * @author Marcus Pinnecke
		 */
		public enum HeaderDescriptionImage {
			ERROR, WARNING, NONE
		}

		private static final String STRING_HEADER_LABEL_DEFAULT = "Create new constraint";

		private static final String STRING_HEADER_DETAILS_DEFAULT = "You can create or edit constraints with this dialog.";

		/**
		 * The panels background color
		 */
		private final Color panelBackgroundColor;

		/**
		 * The actual image of the headers description label
		 * 
		 * {@link ConstraintDialog.HeaderPanel.HeaderDescriptionImage}
		 */
		private Label headerDescriptionImageLabel;

		/**
		 * Brief text what's the current mode for the dialog. This is more or
		 * less a visualization of "editing" or "creating" mode of this dialog.
		 */
		private Label headerLabel;

		/**
		 * Area which contains useful information about current progresses. It
		 * contains e.g. a list of dead features if any exists.
		 */
		private Label detailsLabel;

		/**
		 * The composite to be used for placing the GUI elements
		 */
		protected Composite headComposite;

		/**
		 * Constructs a new header panel to the shell. This panel contains a
		 * header text ({@link #setHeader(String)}), a details text (
		 * {@link #setDetails(String)}).
		 * 
		 * By default a short info about possibilities with this dialog is
		 * display as details and that a new constraint will be created now.
		 * This should be altered with the methods above depending on the
		 * current state.
		 * 
		 * @param shell
		 *            Shell to use
		 */
		public HeaderPanel(Shell shell) {
			headComposite = new Composite(shell, SWT.NONE);
			panelBackgroundColor = shell.getDisplay().getSystemColor(
					SWT.COLOR_WIDGET_BACKGROUND);

			headComposite.setBackground(panelBackgroundColor);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			headComposite.setLayoutData(gridData);

			GridLayout headLayout = new GridLayout();
			headLayout.numColumns = 2;
			headLayout.marginBottom = 7;
			headLayout.marginLeft = 10;
			headLayout.marginRight = 10;
			headLayout.marginTop = 7;
			headComposite.setLayout(headLayout);

			headerDescriptionImageLabel = new Label(headComposite, SWT.NONE
					| SWT.TOP);
			headerDescriptionImageLabel.setImage(null);

			headerLabel = new Label(headComposite, SWT.NONE);
			FontData fontData = headerLabel.getFont().getFontData()[0];
			Font fontActionLabel = new Font(shell.getDisplay(), new FontData(
					fontData.getName(), 12, SWT.BOLD));
			headerLabel.setFont(fontActionLabel);
			headerLabel.setText(STRING_HEADER_LABEL_DEFAULT);

			new Label(headComposite, SWT.NONE); // adds an invisible separator
												// to align details text field
												// correctly

			detailsLabel = new Label(headComposite, SWT.NONE);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.heightHint = 50;
			detailsLabel.setLayoutData(gridData);
			detailsLabel.setBackground(panelBackgroundColor);

			setDetails(STRING_HEADER_DETAILS_DEFAULT,
					HeaderDescriptionImage.NONE);
		}

		/**
		 * @return Gets the current details text
		 */
		public String getDetails() {
			return detailsLabel.getText();
		}

		/**
		 * @return Gets the current headers text
		 */
		public String getHeader() {
			return headerLabel.getText();
		}

		/**
		 * Set the details for this panel. This text should explain more in
		 * details what is going on or should provide useful hints or an error
		 * message. It can contain e.g. the list of dead features.
		 * 
		 * To set the header panels text, consider to use
		 * {@link #setHeader(String)}
		 * 
		 * @param text
		 *            Text to display
		 */
		public void setDetails(String text, HeaderDescriptionImage image) {
			detailsLabel.setText(text);
			setImage(image);
		}

		/**
		 * Sets the header text for this panel. This text should highlight the
		 * current dialogs state, e.g. editing an existing constraint. More
		 * information should be displayed in the details text are.
		 * 
		 * {@link ConstraintDialog.HeaderPanel#setDetails(String)}
		 * 
		 * @param text
		 *            Text to display
		 */
		public void setHeader(String text) {
			headerLabel.setText(text.trim());
		}

		/**
		 * Set current image for the details text.
		 * 
		 * {@link ConstraintDialog.HeaderPanel.HeaderDescriptionImage}
		 * {@link ConstraintDialog.HeaderPanel#headerDescriptionImageLabel}
		 * 
		 * @param image
		 *            The image to set
		 */
		private void setImage(HeaderDescriptionImage image) {
			switch (image) {
			case ERROR:
				headerDescriptionImageLabel.setImage(GUIDefaults.ERROR_IMAGE);
				break;
			case WARNING:
				headerDescriptionImageLabel.setImage(GUIDefaults.WARNING_IMAGE);
				break;
			default:
				headerDescriptionImageLabel.setImage(GUIDefaults.IMAGE_EMPTY);
				break;
			}
			headerDescriptionImageLabel.redraw();

		}
	}
}
