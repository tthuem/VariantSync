package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

public class Package extends Element {

	public Package() {
		super();
	}

	public Package(String name, String path,
			CopyOnWriteArrayList<Element> classes) {
		super(name, path, JavaElements.PACKAGE);
		setChildren(classes);
	}

	public Package(String name, String path, Element javaClass) {
		super(name, path, JavaElements.PACKAGE);
		addChild(javaClass);
	}

	public Package(String name, String path) {
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
	protected Element getConcreteType(String name, String path) {
		return new Package(name, path);
	}

	@Override
	public boolean setCodeLines(List<CodeLine> removeCode) {

		// only classes contain code lines
		return false;
	}

}