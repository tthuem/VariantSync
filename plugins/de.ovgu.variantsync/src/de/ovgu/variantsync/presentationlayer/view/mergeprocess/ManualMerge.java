package de.ovgu.variantsync.presentationlayer.view.mergeprocess;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeLine;

public class ManualMerge {

	protected Object result;
	protected Shell shell;
	private java.util.List<CodeLine> left;
	private String classLeft;
	private java.util.List<CodeLine> right;
	private String classRight;
	private String selectedCode;
	private boolean isLeftChosen;
	private boolean isRightChosen;
	private FeatureView featureView;
	private List<CodeLine> syncCode;

	/**
	 * Create the dialog.
	 * 
	 * @param reference
	 * @param syncCode
	 * 
	 * @param parent
	 * @param style
	 */
	public ManualMerge(FeatureView reference, java.util.List<CodeLine> left,
			String classLeft, java.util.List<CodeLine> right,
			String classRight, List<CodeLine> syncCode) {
		shell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL
				| SWT.SHEET);
		shell.setText("Manual Merge");
		this.classLeft = classLeft;
		this.classRight = classRight;
		this.left = left;
		this.right = right;
		this.featureView = reference;
		this.syncCode = syncCode;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell.setSize(675, 420);

		final Text leftCode = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP
				| SWT.H_SCROLL | SWT.V_SCROLL);
		leftCode.setBounds(27, 68, 282, 220);
		boolean take = false;
		boolean isConflict = false;
		for (CodeLine cl : right) {
			if (cl.getCode().contains("<<<<<<<")) {
				take = true;
				isConflict = true;
				continue;
			}
			if (cl.getCode().contains("=======")) {
				take = false;
				break;
			}
			if (take)
				leftCode.append(cl.getCode() + "\n");
		}

		final Text rightCode = new Text(shell, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		rightCode.setBounds(346, 68, 295, 220);
		take = false;
		for (CodeLine cl : right) {
			if (cl.getCode().contains("=======")) {
				take = true;
				continue;
			}
			if (cl.getCode().contains(">>>>>>>")) {
				take = false;
				break;
			}
			if (take)
				rightCode.append(cl.getCode() + "\n");
		}
		if (!isConflict) {
			for (CodeLine cl : left) {
				leftCode.append(cl.getCode() + "\n");
			}
			for (CodeLine cl : right) {
				rightCode.append(cl.getCode() + "\n");
			}
		}
		Label lblLeft = new Label(shell, SWT.NONE);
		lblLeft.setBounds(27, 47, 282, 15);
		lblLeft.setText("Left: " + classLeft);

		Label lblRight = new Label(shell, SWT.NONE);
		lblRight.setBounds(346, 47, 295, 15);
		lblRight.setText("Right: " + classRight);

		final Button btnCheckLeftVersion = new Button(shell, SWT.CHECK);
		btnCheckLeftVersion.setBounds(132, 292, 150, 25);
		btnCheckLeftVersion.setText("choose left version");
		btnCheckLeftVersion.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnCheckLeftVersion.getSelection()) {
					rightCode.setEnabled(false);
					isLeftChosen = true;
				} else {
					rightCode.setEnabled(true);
					isLeftChosen = false;
				}
			}
		});

		Button btnTakeLeft = new Button(shell, SWT.NONE);
		btnTakeLeft.setBounds(132, 343, 75, 25);
		btnTakeLeft.setText("Take Left");
		btnTakeLeft.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					selectedCode = leftCode.getText();
					rightCode.setText(selectedCode + "\n");
				}
			}
		});

		final Button btnCheckRightVersion = new Button(shell, SWT.CHECK);
		btnCheckRightVersion.setBounds(451, 292, 150, 25);
		btnCheckRightVersion.setText("choose right version");
		btnCheckRightVersion.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (btnCheckRightVersion.getSelection()) {
					leftCode.setEnabled(false);
					isRightChosen = true;
				} else {
					leftCode.setEnabled(true);
					isRightChosen = false;
				}
			}
		});

		Button btnTakeRight = new Button(shell, SWT.NONE);
		btnTakeRight.setBounds(451, 343, 75, 25);
		btnTakeRight.setText("Take Right");
		btnTakeRight.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					selectedCode = rightCode.getText();
					leftCode.setText(selectedCode + "\n");
				}
			}
		});

		Button btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.setBounds(293, 343, 75, 25);
		btnConfirm.setText("Confirm");
		btnConfirm.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					if ((isLeftChosen && isRightChosen)
							|| (!isLeftChosen && !isRightChosen)) {
						Display display = PlatformUI.getWorkbench()
								.getDisplay();
						MessageDialog.openWarning(display.getActiveShell(),
								"Warning",
								"Please choose one version to continue.");
					} else if (isLeftChosen) {
						String text = leftCode.getText();
						String[] code = text.split("\n");
						List<String> codeLines = new ArrayList<String>();
						for (String s : code) {
							if (s.equals("\r"))
								continue;
							if (s.endsWith("\r"))
								s = s.substring(0, s.lastIndexOf("\r"));
							codeLines.add(s);
						}
						List<CodeLine> mergeResult = insertMergedCode(right,
								codeLines);
						featureView.checkManualMerge(mergeResult);
						shell.dispose();
					} else if (isRightChosen) {
						String text = rightCode.getText();
						String[] code = text.split("\n");
						List<String> codeLines = new ArrayList<String>();
						for (String s : code) {
							if (s.equals("\r"))
								continue;
							if (s.endsWith("\r"))
								s = s.substring(0, s.lastIndexOf("\r"));
							codeLines.add(s);
						}
						List<CodeLine> mergeResult = insertMergedCode(right,
								codeLines);
						featureView.checkManualMerge(mergeResult);
						shell.dispose();
					}
				}
			}
		});
	}

	private List<CodeLine> insertMergedCode(List<CodeLine> wholeCode,
			List<String> mergedCode) {
		List<CodeLine> codeLines = new ArrayList<CodeLine>();
		boolean insert = false;
		boolean lock = false;
		int newLineNumber = 0;
		int lineNumber = wholeCode.get(0).getLine();
		for (CodeLine cl : wholeCode) {
			newLineNumber++;
			if (cl.getCode().contains("<<<<<<<")) {
				insert = true;
				continue;
			}
			if (!lock && insert) {
				for (String s : mergedCode) {
					codeLines.add(new CodeLine(s, lineNumber++));
				}
				insert = false;
				lock = true;
			} else if (!lock) {
				codeLines.add(new CodeLine(cl.getCode(), lineNumber++));
			}
			if (cl.getCode().contains(">>>>>>>")) {
				break;
			}
		}
		for (int i = newLineNumber; i < wholeCode.size(); i++) {
			CodeLine cl = wholeCode.get(i);
			codeLines.add(new CodeLine(cl.getCode(), lineNumber++));
		}
		return codeLines;
	}
}
