package de.ovgu.variantsync.ui.view.context;

import java.util.Collection;

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
import de.ovgu.variantsync.applicationlayer.context.ContextOperations;
import de.ovgu.variantsync.applicationlayer.datamodel.context.CodeHighlighting;

/**
 * 
 *
 * @author Tristan Pfofe (tristan.pfofe@ckc.de)
 * @version 1.0
 * @since 20.09.2015
 */
public class CustomColorDialog {

	private Color color;
	private ContextOperations contextOp = ModuleFactory.getContextOperations();
	private static final CodeHighlighting[] BUTTONS = {
			CodeHighlighting.YELLOW, CodeHighlighting.GREEN_BRIGHT,
			CodeHighlighting.ORANGE, CodeHighlighting.RED,
			CodeHighlighting.GREEN, CodeHighlighting.PURPLE,
			CodeHighlighting.PINK, CodeHighlighting.BLUE_BRIGHT,
			CodeHighlighting.BLUE, CodeHighlighting.DEFAULTCONTEXT };

	public CustomColorDialog(Collection<String> featureExpressions) {
		Shell shell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL
				| SWT.SHEET);
		shell.setText("Color Chooser");
		createContents(shell, featureExpressions);
		shell.pack();
		shell.open();
		if (color != null) {
			color.dispose();
		}
	}

	/**
	 * Creates the window contents
	 * 
	 * @param shell
	 *            the parent shell
	 */
	private void createContents(final Shell shell,
			Collection<String> featureExpressions) {
		shell.setLayout(new GridLayout(2, false));

		for (final String fe : featureExpressions) {

			CodeHighlighting ch = contextOp.findColor(fe);
			if (ch == null) {
				ch = CodeHighlighting.YELLOW;
			}

			// final variable to use it in selection adapter
			final CodeHighlighting highlightColor = ch;
			color = new Color(shell.getDisplay(), highlightColor.getRGB());

			// Use a label full of spaces to show the color
			final Label colorLabel = new Label(shell, SWT.NONE);
			colorLabel.setText(fe);
			colorLabel.setBackground(color);

			final Combo buttons = new Combo(shell, SWT.DROP_DOWN
					| SWT.READ_ONLY);
			for (int i = 0, n = BUTTONS.length; i < n; i++) {
				buttons.add(BUTTONS[i].getColorName());
			}
			buttons.select(getComboItemId(BUTTONS, fe));
			buttons.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (buttons.getText().equals(
							CodeHighlighting.YELLOW.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.YELLOW);
					} else if (buttons.getText().equals(
							CodeHighlighting.GREEN_BRIGHT.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe,
								CodeHighlighting.GREEN_BRIGHT);
					} else if (buttons.getText().equals(
							CodeHighlighting.ORANGE.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.ORANGE);
					} else if (buttons.getText().equals(
							CodeHighlighting.GREEN.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.GREEN);
					} else if (buttons.getText().equals(
							CodeHighlighting.RED.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.RED);
					} else if (buttons.getText().equals(
							CodeHighlighting.PINK.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.PINK);
					} else if (buttons.getText().equals(
							CodeHighlighting.BLUE_BRIGHT.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe,
								CodeHighlighting.BLUE_BRIGHT);
					} else if (buttons.getText().equals(
							CodeHighlighting.BLUE.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.BLUE);
					} else if (buttons.getText().equals(
							CodeHighlighting.PURPLE.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe, CodeHighlighting.PURPLE);
					} else if (buttons.getText().equals(
							CodeHighlighting.DEFAULTCONTEXT.getColorName())) {
						colorLabel.setBackground(new Color(shell.getDisplay(),
								getColor(buttons.getText())));
						contextOp.setContextColor(fe,
								CodeHighlighting.DEFAULTCONTEXT);
					}
				}
			});
		}
		Button closeButton = new Button(shell, SWT.NONE);
		closeButton.setText("Close");
		closeButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						MarkerHandler.getInstance().refreshMarker(null);
						shell.dispose();
					}
				});
	}

	private int getComboItemId(CodeHighlighting[] items,
			String featureExpression) {
		CodeHighlighting ch = contextOp.findColor(featureExpression);
		if (ch == null) {
			ch = CodeHighlighting.YELLOW;
		}
		int i = 0;
		for (CodeHighlighting item : items) {
			if (item.getColorName().equals(ch.getColorName())) {
				return i;
			}
			i++;
		}
		return 0;
	}

	private RGB getColor(String color) {
		if (color.equals(CodeHighlighting.YELLOW.getColorName())) {
			return CodeHighlighting.YELLOW.getRGB();
		} else if (color.equals(CodeHighlighting.GREEN_BRIGHT.getColorName())) {
			return CodeHighlighting.GREEN_BRIGHT.getRGB();
		} else if (color.equals(CodeHighlighting.ORANGE.getColorName())) {
			return CodeHighlighting.ORANGE.getRGB();
		} else if (color.equals(CodeHighlighting.GREEN.getColorName())) {
			return CodeHighlighting.GREEN.getRGB();
		} else if (color.equals(CodeHighlighting.RED.getColorName())) {
			return CodeHighlighting.RED.getRGB();
		} else if (color.equals(CodeHighlighting.PINK.getColorName())) {
			return CodeHighlighting.PINK.getRGB();
		} else if (color.equals(CodeHighlighting.BLUE_BRIGHT.getColorName())) {
			return CodeHighlighting.BLUE_BRIGHT.getRGB();
		} else if (color.equals(CodeHighlighting.BLUE.getColorName())) {
			return CodeHighlighting.BLUE.getRGB();
		} else if (color.equals(CodeHighlighting.PURPLE.getColorName())) {
			return CodeHighlighting.PURPLE.getRGB();
		} else if (color.equals(CodeHighlighting.DEFAULTCONTEXT.getColorName())) {
			return CodeHighlighting.DEFAULTCONTEXT.getRGB();
		} else {
			return new RGB(0, 0, 0);
		}
	}
}
