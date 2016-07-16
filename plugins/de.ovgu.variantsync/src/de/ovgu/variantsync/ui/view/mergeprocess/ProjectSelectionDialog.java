package de.ovgu.variantsync.ui.view.mergeprocess;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.variantsync.VariantSyncPlugin;
import de.ovgu.variantsync.ui.controller.ControllerHandler;
import de.ovgu.variantsync.ui.controller.FeatureController;
import de.ovgu.variantsync.ui.view.AbstractView;

/**
 * 
 * @author Lei Luo
 * 
 */
@SuppressWarnings("restriction")
public class ProjectSelectionDialog extends SelectionDialog implements
		AbstractView {

	private Object inputElement;
	private ILabelProvider projectLabelProvider;
	private ILabelProvider featureLabelProvider;
	private CheckboxTableViewer featuresListViewer;
	private IStructuredContentProvider featureContentProvider;
	private CheckboxTableViewer projectsListViewer;
	private IStructuredContentProvider projectContentProvider;
	private static ProjectSelectionDialog projectSelectionDiaglog;

	private static final int SIZING_SELECTION_WIDGET_HEIGHT = 200;
	private static final int SIZING_SELECTION_WIDGET_WIDTH = 50;

	private Text changesText;
	private String unifiedDiff;
	private FeatureController featureController = ControllerHandler.getInstance()
			.getFeatureController();
	private Map<IProject, Set<Feature>> featureMap;

	public ProjectSelectionDialog(Shell parentShell, Object input,
			IStructuredContentProvider contentProvider,
			ILabelProvider labelProvider, String message, String unifiedDiff) {
		super(parentShell);
		this.unifiedDiff = unifiedDiff;
		featureController.getFeatures(VariantSyncPlugin.getDefault()
				.getSupportProjectList());
		setTitle(WorkbenchMessages.ListSelection_title);
		inputElement = input;
		projectSelectionDiaglog = this;
		this.projectContentProvider = contentProvider;
		this.featureContentProvider = new FeatureListViewerContentProvider();
		this.projectLabelProvider = labelProvider;
		this.featureLabelProvider = new LabelProvider();
		if (message != null) {
			setMessage(message);
		} else {
			setMessage(WorkbenchMessages.ListSelection_message);
		}
	}

	private void addSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.END, SWT.TOP, true,
				false));

		Button selectButton = createButton(buttonComposite,
				IDialogConstants.SELECT_ALL_ID,
				WorkbenchMessages.SelectionDialog_selectLabel, false);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				projectsListViewer.setAllChecked(true);
			}
		};
		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite,
				IDialogConstants.DESELECT_ALL_ID,
				WorkbenchMessages.SelectionDialog_deselectLabel, false);

		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				projectsListViewer.setAllChecked(false);
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	/**
	 * Visually checks the previously-specified elements in this dialog's list
	 * viewer.
	 */
	private void checkInitialSelections() {
		Iterator<?> itemsToCheck = getInitialElementSelections().iterator();

		while (itemsToCheck.hasNext()) {
			projectsListViewer.setChecked(itemsToCheck.next(), true);
		}
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(shell, IWorkbenchHelpContextIds.LIST_SELECTION_DIALOG);
	}

	protected Control createDialogArea(Composite parent) {
		// page group
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) composite.getLayout();
		gridLayout.verticalSpacing = 6;
		gridLayout.numColumns = 2;

		initializeDialogUnits(composite);

		createMessageArea(composite);
		new Label(composite, SWT.NONE);

		Label labelFeatures = new Label(composite, SWT.NONE);
		labelFeatures.setText("changed by features :");

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Details of the change:");

		featuresListViewer = CheckboxTableViewer.newCheckList(composite,
				SWT.BORDER);
		featuresListViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				projectsListViewer.setAllChecked(true);
				Object[] elements = projectsListViewer.getCheckedElements();
				List<IProject> projects = new ArrayList<IProject>();
				for (Object o : elements) {
					projects.add((IProject) o);
				}
				Map<IProject, Boolean> checkedProjects = featureController
						.checkFeatureSupport(projects, getSelectedFeatures());
				Set<IProject> p = checkedProjects.keySet();
				Iterator<IProject> itProjects = p.iterator();
				while (itProjects.hasNext()) {
					IProject project = itProjects.next();
					projectsListViewer.setChecked(project,
							checkedProjects.get(project));
				}
				projectsListViewer.refresh();
			}
		});
		GridData data1 = new GridData(GridData.FILL_BOTH);
		data1.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data1.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		featuresListViewer.getTable().setLayoutData(data1);

		changesText = new Text(composite, SWT.BORDER | SWT.READ_ONLY
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		GridData gdText = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gdText.widthHint = 500;
		gdText.heightHint = 79;
		changesText.setLayoutData(gdText);
		changesText.setText(unifiedDiff);

		Label labelProjects = new Label(composite, SWT.NONE);
		labelProjects.setText("Matching Projects :");

		projectsListViewer = CheckboxTableViewer.newCheckList(composite,
				SWT.BORDER);
		GridData data2 = new GridData(GridData.FILL_BOTH);
		data2.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data2.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		projectsListViewer.getTable().setLayoutData(data2);

		projectsListViewer.setLabelProvider(projectLabelProvider);
		projectsListViewer.setContentProvider(projectContentProvider);

		featuresListViewer.setLabelProvider(featureLabelProvider);
		featuresListViewer.setContentProvider(featureContentProvider);

		addSelectionButtons(composite);

		initializeViewer();

		// initialize page
		if (!getInitialElementSelections().isEmpty()) {
			checkInitialSelections();
		}
		projectsListViewer.setAllChecked(true);
		Dialog.applyDialogFont(composite);
		new Label(composite, SWT.NONE);

		return composite;
	}

	/**
	 * Initializes this dialog's viewer after it has been laid out.
	 */
	private void initializeViewer() {
		projectsListViewer.setInput(inputElement);
		featuresListViewer.setInput(this);
	}

	/**
	 * Collects selected target projects.
	 */
	protected void okPressed() {

		// Get the input children.
		Object[] children = projectContentProvider.getElements(inputElement);

		// Build a list of selected children.
		if (children != null) {
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < children.length; ++i) {
				Object element = children[i];
				if (projectsListViewer.getChecked(element)) {
					list.add(element);
				}
			}
			setResult(list);
		}
		super.okPressed();
	}

	/**
	 * Collects selected features.
	 * 
	 * @return feature array
	 */
	public Object[] getSelectedFeatures() {
		List<Object> result = new ArrayList<Object>();
		Object[] obj = featureContentProvider.getElements(this);
		for (Object o : obj) {
			if (featuresListViewer.getChecked(o)) {
				result.add(o);
			}
		}
		return result.toArray();
	}

	/**
	 * Get specified features of projects.
	 * 
	 * @param project
	 * @return set of features
	 */
	public Set<Feature> getFeatures(IProject project) {
		if (featureMap == null) {
			featureMap = featureController.getFeaturesDirectly(VariantSyncPlugin
					.getDefault().getSupportProjectList());
		}
		return featureMap.get(project);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	public static ProjectSelectionDialog getDefault() {
		return projectSelectionDiaglog;
	}
}