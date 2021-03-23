package de.tubs.variantsync.core.syncronization.compare;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.tubs.variantsync.core.patch.interfaces.IDelta;
import de.tubs.variantsync.core.utilities.FileHelper;

/**
 * Creates a page for editing a file with showing a delta on the right
 *
 * @author Christopher Sontag
 */
@Deprecated
public class DeltaCompareViewerPage extends WizardPage {

	private final IFile file;
	private final IDelta<?> delta;
	private SourceViewer sourceViewer;

	protected DeltaCompareViewerPage(IFile file, IDelta<?> delta) {
		super("Delta Compare");
		setTitle("Synchronize Delta");
		setDescription("Merge the change from the right to the left.");

		this.file = file;
		this.delta = delta;
	}

	@Override
	public void createControl(Composite parent) {
		parent.setLayout(new FillLayout());

		final Composite composite = new Composite(parent, SWT.FILL);
		final GridLayout gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);

		final Label codeLabel = new Label(composite, SWT.NONE);
		codeLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		codeLabel.setText("Code:");

		final Label deltaLabel = new Label(composite, SWT.NONE);
		deltaLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deltaLabel.setText("Delta:");

		final CompositeRuler ruler = new CompositeRuler();
		final LineNumberRulerColumn lineNumber = new LineNumberRulerColumn();

		ruler.addDecorator(0, lineNumber);
		sourceViewer = new SourceViewer(composite, ruler, SWT.V_SCROLL | SWT.MULTI | SWT.FLAT);
		sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sourceViewer.setDocument(new Document(getStringFromList(FileHelper.getFileLines(file))));

		final Text deltaText = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.FLAT);
		deltaText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		deltaText.setText(delta.getRepresentation());

		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), "VariantSync.DeltaCompareViewer");
	}

	public List<String> getSourceCode() {
		return Arrays.asList(sourceViewer.getDocument().get().split(String.format("%n")));
	}

	private String getStringFromList(List<String> entries) {
		String str = "";
		for (final String el : entries) {
			str += String.format("%s%n", el);
		}
		return str;
	}

}
