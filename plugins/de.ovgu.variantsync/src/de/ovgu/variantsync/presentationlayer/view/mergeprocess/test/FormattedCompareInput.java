package de.ovgu.variantsync.presentationlayer.view.mergeprocess.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.ICompareInputChangeListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.TextEdit;

public class FormattedCompareInput implements ICompareInput{

	private ICompareInput fCompareInput;

	private boolean ignoreFormatting;

	public FormattedCompareInput(final ICompareInput compareInput) {
		this.fCompareInput = compareInput;
	}

	public void addCompareInputChangeListener(
			final ICompareInputChangeListener listener) {
		this.fCompareInput.addCompareInputChangeListener(listener);
	}

	public void copy(final boolean leftToRight) {
		this.fCompareInput.copy(leftToRight);
	}

	public ITypedElement getAncestor() {
		return this.fCompareInput.getAncestor();
	}

	public Image getImage() {
		return this.fCompareInput.getImage();
	}

	public int getKind() {
		return this.fCompareInput.getKind();
	}

	public ITypedElement getLeft() {
		final ITypedElement leftElement = this.fCompareInput.getLeft();
		if (!this.ignoreFormatting) {
			return leftElement;
		}

		return format(leftElement);
	}

	public String getName() {
		return this.fCompareInput.getName();
	}

	public ITypedElement getRight() {
		final ITypedElement rightElement = this.fCompareInput.getRight();
		if (!this.ignoreFormatting) {
			return rightElement;
		}
		
		return format(rightElement); 
	}

	public void removeCompareInputChangeListener(
			final ICompareInputChangeListener listener) {
		this.fCompareInput.removeCompareInputChangeListener(listener);
	}

	public void toggleFormattingIgnore() {
		if (this.ignoreFormatting) {
			this.ignoreFormatting = false;
		} else {
			this.ignoreFormatting = true;
		}

	}

	private ITypedElement format(final ITypedElement elementToFormat) {
		try {
			if (elementToFormat instanceof IStreamContentAccessor) {
				final IStreamContentAccessor resNode = (IStreamContentAccessor) elementToFormat;
				final InputStream contentIs = resNode.getContents();
				final String contentsString = fromInputStreamToString(contentIs);
				final Map options = JavaCore.getOptions();
				
				final CodeFormatter codeFormatter = ToolFactory
						.createCodeFormatter(options);
				final TextEdit tmpOutputFromFormatter = codeFormatter.format(
						CodeFormatter.K_COMPILATION_UNIT, contentsString, 0, contentsString
								.length(), 0, null);
				if (tmpOutputFromFormatter != null) {
					//to convert from TextEdit to String we must pass it to a Document
					final Document tempDoc = new Document(contentsString);
					tmpOutputFromFormatter.apply(tempDoc);
					final String formattedText = tempDoc.get();
					final StringInput toReturn = new StringInput(elementToFormat
							.getType(), elementToFormat.getName(), formattedText);
					return toReturn;
				} else {
					System.err.println("format error");
					return elementToFormat;
				}
	
			}
	
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return elementToFormat;
	}

	private String fromInputStreamToString(InputStream contentIs) {
		try {
			ByteArrayOutputStream toReturn = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int readBytes = 0;
			while((readBytes = contentIs.read(buffer)) >= 0) {
				toReturn.write(buffer,0,readBytes);
			}
			return new String(toReturn.toByteArray(),"UTF-8");
		} catch (IOException e) {
			return "";
		}
	}

	public boolean isFormattingIgnoreEnabled() {
		return ignoreFormatting;
	}

}
