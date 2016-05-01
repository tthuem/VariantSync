package de.ovgu.variantsync.applicationlayer.merging;

import javax.swing.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class CompareItem2 implements IStreamContentAccessor, ITypedElement,
		IModificationDate, IEditableContent {

	private String contents, name;

	public CompareItem2(String name, String contents) {
		this.name = name;
		this.contents = contents;
	}

	@Override
	public long getModificationDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return ITypedElement.TEXT_TYPE;
	}

	@Override
	public InputStream getContents() throws CoreException {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(contents.getBytes());
	}

	@Override
	public org.eclipse.swt.graphics.Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void setContent(byte[] newContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ITypedElement replace(ITypedElement dest, ITypedElement src) {
		// TODO Auto-generated method stub
		return null;
	}
}