package de.ovgu.variantsync.presentationlayer.view.mergeprocess;

import java.util.Collection;

import org.eclipse.compare.CompareViewerPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.ovgu.variantsync.applicationlayer.ModuleFactory;
import de.ovgu.variantsync.applicationlayer.context.IContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@st.ovgu.de)
 * @version 1.0
 * @since 20.09.2015
 */
public class ViewerPane {

	public ViewerPane() {
		Shell shell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL
				| SWT.SHEET);
		shell.setText("Color Chooser");
		CompareViewerPane pane = new CompareViewerPane(shell, SWT.BORDER
				| SWT.FLAT);
		pane.open(null);
		pane.setVisible(true);
		shell.setLayout(new GridLayout(2, false));
		final Label colorLabel = new Label(shell, SWT.NONE);
		colorLabel.setText("Test");
		shell.pack();
		shell.open();
	}
}
