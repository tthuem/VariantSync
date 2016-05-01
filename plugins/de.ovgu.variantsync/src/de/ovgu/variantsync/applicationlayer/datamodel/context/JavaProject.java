package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.List;

import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

public class JavaProject extends JavaElement {

	public JavaProject() {
		super();
	}

	public JavaProject(String name, String path, String project,
			List<JavaElement> projects) {
		super(name, path, JavaElements.PROJECT);
		setChildren(projects);
	}

	public JavaProject(String name, String path, JavaElement javaPackage) {
		super(name, path, JavaElements.PROJECT);
		addChild(javaPackage);
	}

	public JavaProject(String name, String path) {
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
	protected JavaElement getConcreteType(String name, String path) {
		return new JavaProject(name, path);
	}

	@Override
	public boolean setCodeLines(List<CodeLine> removeCode) {

		// only classes contain code lines
		return false;
	}

}