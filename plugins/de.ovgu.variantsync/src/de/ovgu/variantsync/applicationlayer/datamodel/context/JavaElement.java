package de.ovgu.variantsync.applicationlayer.datamodel.context;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import de.ovgu.variantsync.applicationlayer.features.mapping.UtilOperations;
import de.ovgu.variantsync.presentationlayer.controller.data.JavaElements;

@XmlTransient
@XmlSeeAlso({ JavaProject.class, JavaPackage.class, JavaClass.class,
		JavaMethod.class })
public abstract class JavaElement {

	private String name;
	private String path;
	private JavaElements type;
	private List<JavaElement> children;

	public abstract boolean setCodeLines(List<CodeLine> removeCode);

	public JavaElement() {
	}

	public JavaElement(String name, String path, JavaElements type) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
	}

	protected abstract JavaElement getConcreteType(String name, String path);

	@XmlTransient
	public List<CodeLine> getClonedCodeLines() {
		List<CodeLine> code = new ArrayList<CodeLine>();
		if (getChildren() != null) {
			for (JavaElement child : getChildren()) {
				code.addAll(child.getClonedCodeLines());
			}
		}
		return code;
	}

	public void addChild(JavaElement child) {
		children = UtilOperations.getInstance().addChild(child, children);
	}

	public void removeChild(String childName) {
		children = UtilOperations.getInstance()
				.removeChild(childName, children);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the type
	 */
	public JavaElements getType() {
		return type;
	}

	/**
	 * @return the children
	 */
	@XmlElement(name = "member")
	public List<JavaElement> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<JavaElement> children) {
		this.children = children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public JavaElement clone() throws CloneNotSupportedException {
		List<JavaElement> children = getChildren();
		List<JavaElement> clonedChildren = new ArrayList<JavaElement>();
		if (children != null && !children.isEmpty() && children.get(0) != null) {
			for (JavaElement element : children) {
				clonedChildren.add(element.clone());
			}
		}
		JavaElement copy = getConcreteType(getName(), getPath());
		if (clonedChildren != null) {
			copy.setChildren(clonedChildren);
		}
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return " [name=" + name + ", path=" + path + ", type=" + type
				+ ", children=" + children + "]";
	}

}
