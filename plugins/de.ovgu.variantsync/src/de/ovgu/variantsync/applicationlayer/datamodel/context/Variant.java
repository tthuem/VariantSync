package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.List;

import de.ovgu.variantsync.ui.controller.data.JavaElements;

public class Variant extends Element {

	public Variant() {
		super();
	}

	public Variant(String name, String path, String project, List<Element> projects) {
		super(name, path, JavaElements.PROJECT);
		setChildren(projects);
	}

	public Variant(String name, String path, Element javaPackage) {
		super(name, path, JavaElements.PROJECT);
		addChild(javaPackage);
	}

	public Variant(String name, String path) {
		super(name, path, JavaElements.PROJECT);
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return getName();
	}

	/**
	 * @param name
	 *            the project to set
	 */
	public void setProject(String name) {
		setName(name);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaProject " + super.toString();
	}

	@Override
	protected Element getConcreteType(String name, String path) {
		return new Variant(name, path);
	}

	@Override
	public boolean setCodeLines(List<CodeLine> removeCode) {

		// only classes contain code lines
		return false;
	}

	public boolean isEmpty() {
		return hasNoChildren() || hasEmptyChildren();
	}

	private boolean hasEmptyChildren() {
		if (getChildren() == null || getChildren().isEmpty()) {
			return true;
		}
		for (Element e : getChildren()) {
			if (e != null && !e.isEmpty()) {
				return false;
			}
		}
		return true;
	}
}