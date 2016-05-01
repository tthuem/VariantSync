package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

public class JavaPackage extends JavaElement {

	public JavaPackage() {
		super();
	}

	public JavaPackage(String name, String path,
			CopyOnWriteArrayList<JavaElement> classes) {
		super(name, path, JavaElements.PACKAGE);
		setChildren(classes);
	}

	public JavaPackage(String name, String path, JavaElement javaClass) {
		super(name, path, JavaElements.PACKAGE);
		addChild(javaClass);
	}

	public JavaPackage(String name, String path) {
		super(name, path, JavaElements.PACKAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaPackage " + super.toString();
	}

	@Override
	protected JavaElement getConcreteType(String name, String path) {
		return new JavaPackage(name, path);
	}

	@Override
	public boolean setCodeLines(List<CodeLine> removeCode) {

		// only classes contain code lines
		return false;
	}

}