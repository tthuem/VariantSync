package de.ovgu.variantsync.ui.view.mergeprocess.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class StringInput implements ITypedElement,
		IEncodedStreamContentAccessor {

	private String fContents;

	private String fType;

	private String fName;

	public StringInput(final String type, final String name, final String contents) {
		this.fType = type;
		this.fName = name;
		this.fContents = contents;
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return this.fName;
	}

	public String getType() {
		return this.fType;
	}

	public String getCharset() throws CoreException {
		return "UTF-16";
	}

	public InputStream getContents() throws CoreException {
		try {
			return new ByteArrayInputStream(this.fContents.getBytes("UTF-16"));
		} catch (final UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
