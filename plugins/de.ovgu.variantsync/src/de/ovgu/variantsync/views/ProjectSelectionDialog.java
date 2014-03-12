package de.ovgu.variantsync.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.CheckStateChangedEvent;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.ConfigurationReader;
import de.ovgu.featureide.fm.core.io.FeatureModelReaderIFileWrapper;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelReader;
import de.ovgu.variantsync.model.ResourceChangesFilePatch;
import de.ovgu.variantsync.resource.AdminFileManager;

import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class ProjectSelectionDialog extends SelectionDialog {
	private Object inputElement;
	private ILabelProvider projectLabelProvider;
	private ILabelProvider featureLabelProvider;
	private IStructuredContentProvider projectContentProvider;
	private IStructuredContentProvider featureContentProvider;
	private CheckboxTableViewer projectsListViewer, featuresListViewer;
	private static ProjectSelectionDialog projectSelectionDiaglog;

	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;
	private final static int SIZING_SELECTION_WIDGET_WIDTH = 50;

	private static final String FEATUREINFO_PROJECT_NAME = "variantsyncFeatureInfo";

	private static final String CONFIG_FILE_EXTENSION = "config";
	private static final String CONFIGS_PATH = "configs";

	private static final String FEATURE_PROJECT_NATURE = "de.ovgu.featureide.core.featureProjectNature";

	private static final String MODEL_FILE = "model.xml";
	private Text changesText;

	public ProjectSelectionDialog(Shell parentShell, Object input,
			IStructuredContentProvider contentProvider, ILabelProvider labelProvider,
			String message) {
		super(parentShell);
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
		buttonComposite.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));

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
		Iterator itemsToCheck = getInitialElementSelections().iterator();

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

		featuresListViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		featuresListViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object[] selectedFeatures = getSelectedFeatures();
				projectsListViewer.setAllChecked(true);
				Object[] elements = projectsListViewer.getCheckedElements();
				for (Object element : elements) {
					projectsListViewer.setChecked(element,
							isProjectContain(((IProject) element), selectedFeatures));
				}
				projectsListViewer.refresh();
			}
		});
		GridData data1 = new GridData(GridData.FILL_BOTH);
		data1.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data1.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		featuresListViewer.getTable().setLayoutData(data1);

		changesText = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gd_text.widthHint = 500;
		gd_text.heightHint = 79;
		changesText.setLayoutData(gd_text);
		changesText.setText(getChangesDetail());

		Label labelProjects = new Label(composite, SWT.NONE);
		labelProjects.setText("Matching Projects :");

		projectsListViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
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

	protected void okPressed() {

		// Get the input children.
		Object[] children = projectContentProvider.getElements(inputElement);

		// Build a list of selected children.
		if (children != null) {
			ArrayList list = new ArrayList();
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

	public Object[] getSelectedFeatures() {
		ArrayList<Object> result = new ArrayList<Object>();
		Object[] obj = featureContentProvider.getElements(this);
		for (Object o : obj) {
			if (featuresListViewer.getChecked(o)) {
				result.add(o);
			}
		}
		return result.toArray();
	}

	public Set<Feature> getFeaturesFor(IProject project) {
		IProject featureInfoProject = null;
		IFile modelFile = null;
		IFile configFile = null;
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				if (p.isOpen() && p.getName().equals(FEATUREINFO_PROJECT_NAME)
						&& p.hasNature(FEATURE_PROJECT_NATURE)) {
					featureInfoProject = p;
					break;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (featureInfoProject != null) {
			modelFile = featureInfoProject.getFile(MODEL_FILE);
			configFile = featureInfoProject.getFolder(CONFIGS_PATH).getFile(
					project.getName() + "." + CONFIG_FILE_EXTENSION);
		}
		if (featureInfoProject != null && modelFile.exists() && configFile.exists()) {
			Configuration config = null;
			try {
				config = readConfig(configFile, modelFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedModelException e) {
				e.printStackTrace();
			}
			return config.getSelectedFeatures();
		} else {
			return new HashSet<Feature>();
		}
	}

	private Configuration readConfig(IFile configFile, IFile modelFile)
			throws FileNotFoundException, UnsupportedModelException {
		FeatureModel fm = new FeatureModel();
		fm.setRoot(new Feature(fm));
		new FeatureModelReaderIFileWrapper(new XmlFeatureModelReader(fm))
				.readFromFile(modelFile);
		Configuration config = new Configuration(fm);
		ConfigurationReader reader = new ConfigurationReader(config);
		try {
			reader.readFromFile(configFile);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

	private boolean isProjectContain(IProject project, Object[] features) {
		Set<Feature> projectFeatures = getFeaturesFor(project);
		int flag = 0;
		for (Object feature : features) {
			for (Feature f : projectFeatures) {
				if (f.getName().equals(feature)) {
					flag++;
					break;
				}
			}
		}
		if (flag == features.length) {
			return true;
		} else {
			return false;
		}
	}

	public static ProjectSelectionDialog getDefault() {
		return projectSelectionDiaglog;
	}

	private String getChangesDetail() {
		String ChangesDetail = "";
		ResourceChangesFilePatch filePatch = ResourceChangesView.getDefault()
				.getSelectedFilePatch();
		ChangesDetail = ChangesDetail + "Project:  " + filePatch.getProject().getName()
				+ "\n";
		ChangesDetail = ChangesDetail + "Path:  " + filePatch.getPath() + "\n";
		String status = filePatch.getStatus();
		ChangesDetail = ChangesDetail + "Operation:  " + status + "\n";
		if (status.equals(AdminFileManager.CHANGE)) {
			ChangesDetail = ChangesDetail
					+ "=====================Unified Diff=====================" + "\n";
			ChangesDetail = ChangesDetail + filePatch.getUnidiff();
		}
		return ChangesDetail;
	}
}
