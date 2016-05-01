package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.List;

import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

public class JavaMethod extends JavaElement {

	public JavaMethod() {
		super();
	}

	public JavaMethod(String name, String path) {
		super(name, path, JavaElements.METHOD);
	}

	public JavaMethod(String name, String path, String code) {
		super(name, path, JavaElements.METHOD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaMethod [toString()=" + super.toString() + "]";
	}

	@Override
	protected JavaElement getConcreteType(String name, String path) {
		return new JavaMethod(name, path);
	}

	@Override
	public boolean setCodeLines(List<CodeLine> removeCode) {

		// only classes contain code lines
		return false;
	}
}